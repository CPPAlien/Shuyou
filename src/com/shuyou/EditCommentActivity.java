package com.shuyou;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.shuyou.lib.internal.ChangeToEnum;
import com.shuyou.lib.internal.ChangeToEnum.EnumCode;
import com.shuyou.net.UserInfo;
import com.shuyou.values.Values;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditCommentActivity extends Activity{
	
	private final String TAG = "EditCommentActivity";
	
	private EditText mEditText;
	private TextView mTextNum;
	private TextView mTextMaxNum;
	private String bookId;
	private String type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_uploadbook);
		
		mEditText = (EditText)findViewById(R.id.uploadbook_edittext);
		mEditText.setText(getIntent().getStringExtra("content"));
		bookId = getIntent().getStringExtra("bookId");
		type = getIntent().getStringExtra("type");
		
		mEditText.addTextChangedListener(mTextWatcher);
		mTextNum = (TextView)findViewById(R.id.uploadbook_textnum);
		mTextNum.setText(String.valueOf(Values.MAX_TEXT_COUNT));
		mTextMaxNum = (TextView)findViewById(R.id.uploadbook_textmaxnum);
		mTextMaxNum.setText("/" + Values.MAX_TEXT_COUNT);
		
		
		if(type.equals("BookInfoActivity")) {
			findViewById(R.id.uploadbook_back).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			findViewById(R.id.uploadbook_confirm).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i = new Intent(EditCommentActivity.this, BookshelfDetailActivity.class);
					i.putExtra("content", mEditText.getText().toString());
					setResult(RESULT_OK,i); //这理有2个参数(int resultCode, Intent intent)
					finish();
				}
			});
		}
		else if(type.equals("BookshelfDetailActivity")) {
			findViewById(R.id.uploadbook_back).setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			findViewById(R.id.uploadbook_confirm).setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					UpdateComment();
					findViewById(R.id.uploadbook_confirm).setEnabled(false);
				}
				
			});
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	private TextWatcher mTextWatcher = new TextWatcher() {  
		  
        private int editStart;  
  
        private int editEnd;  
  
        public void afterTextChanged(Editable s) {  
            editStart = mEditText.getSelectionStart();  
            editEnd = mEditText.getSelectionEnd();  
  
            // 先去掉监听器，否则会出现栈溢出  
            mEditText.removeTextChangedListener(mTextWatcher);  
  
            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度  
            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1  
            while (calculateLength(s.toString()) > Values.MAX_TEXT_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作  
                s.delete(editStart - 1, editEnd);  
                editStart--;  
                editEnd--;  
            }  
            // mEditText.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒  
            mEditText.setSelection(editStart);  
  
            // 恢复监听器  
            mEditText.addTextChangedListener(mTextWatcher);  
  
            setLeftCount();  
        }  
  
        /** 
         * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1 
         *  
         * @param c 
         * @return 
         */  
        private long calculateLength(CharSequence c) {  
            double len = 0;  
            for (int i = 0; i < c.length(); i++) {  
                int tmp = (int) c.charAt(i);  
                if (tmp > 0 && tmp < 127) {  
                    len += 0.5;  
                } else {  
                    len++;  
                }  
            }  
            return Math.round(len);  
        }  
      
        /** 
         * 刷新剩余输入字数,最大值新浪微博是140个字，人人网是200个字 
         */  
        private void setLeftCount() {  
        	mTextNum.setText(String.valueOf((Values.MAX_TEXT_COUNT - getInputCount())));  
        }  
      
        /** 
         * 获取用户输入的分享内容字数 
         *  
         * @return 
         */  
        private long getInputCount() {  
            return calculateLength(mEditText.getText().toString());  
        }  
        
        public void beforeTextChanged(CharSequence s, int start, int count,  
                int after) {  
  
        }  
  
        public void onTextChanged(CharSequence s, int start, int before,  
                int count) {  
  
        }
    };
    
    /**
     * 向服务器申请更新书籍备注
     */
    private void UpdateComment() {    	
    	new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				map.put("bookid", bookId);
				map.put("comment", mEditText.getText().toString());
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("post", Values.UPDATE_COMMENT_API, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.what = Values.SUCCESS_TO_UPDATE;
					message.obj = changeToEnum.getData().getBookDetail();
					handler.sendMessage(message);
					break ;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.FAIL_TO_UPDATE;
					handler.sendMessage(message);
					break;
				}
			}
    	}.start();
    }
    
    /**
	 * 静态handler,防止内存泄漏错误
	 * @author chris
	 *
	 */
	static class MyInnerHandler extends Handler {
        WeakReference<EditCommentActivity> mActivity;

        MyInnerHandler(EditCommentActivity mAct) {
        	mActivity = new WeakReference<EditCommentActivity>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
        	EditCommentActivity theAct = mActivity.get();
        	switch (msg.what) {
        	case Values.SUCCESS_TO_UPDATE:
        		Intent i = new Intent(theAct, BookshelfDetailActivity.class);
				i.putExtra("content", theAct.mEditText.getText().toString());
				theAct.setResult(RESULT_OK,i); //这理有2个参数(int resultCode, Intent intent)
        		break;
        	case Values.FAIL_TO_UPDATE:
        		Toast.makeText(theAct, "备注更新失败", Toast.LENGTH_SHORT).show();
        		break;
        	case Values.NETWORK_ERROR:
        		Toast.makeText(theAct, "网络异常", Toast.LENGTH_SHORT).show();
        		break;
			default:
				break;
			}
        	theAct.finish();
	    }
	 }
	 MyInnerHandler handler = new MyInnerHandler(this);
}
