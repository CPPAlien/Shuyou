package com.shuyou;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.shuyou.lib.AsyncImageLoader;
import com.shuyou.lib.AsyncImageLoader.ImageCallback;
import com.shuyou.lib.internal.ChangeToEnum;
import com.shuyou.lib.internal.ChangeToEnum.EnumCode;
import com.shuyou.net.Data;
import com.shuyou.net.User;
import com.shuyou.net.UserInfo;
import com.shuyou.values.Values;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends Activity{
	
	Dialog dialogUsrInfo = null;
	
	private final String TAG = "UserInfoActivity";
	
	private TextView textNickName = null;
	private TextView textSex = null;
	private TextView textPhoneNum = null;
	private TextView textQQ = null;
	private TextView textWechat = null;
	private ImageView imageLogo = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_userinfo);
		
		Init();
		
		new GetUserInfoThread(getIntent().getStringExtra("userid")).start();
	}
	
	private void Init(){
		findViewById(R.id.userinfo_usrlogo).setEnabled(false);
		textNickName = (TextView)findViewById(R.id.userinfo_con_nickname);
		textSex = (TextView)findViewById(R.id.userinfo_con_sex);
		textPhoneNum = (TextView)findViewById(R.id.userinfo_con_phonenum);
		textQQ = (TextView)findViewById(R.id.userinfo_con_qq);
		textWechat = (TextView)findViewById(R.id.userinfo_con_wechat);
		imageLogo = (ImageView)findViewById(R.id.userinfo_con_usrlogo);
		findViewById(R.id.userinfo_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		findViewById(R.id.userinfo_phonenum).setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + textPhoneNum.getText().toString()));
				startActivity(intent);
			}
	        	
	    });
	        
	    findViewById(R.id.userinfo_qq).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClipboardManager cmb = (ClipboardManager) getSystemService(Service.CLIPBOARD_SERVICE);
				cmb.setText(textQQ.getText().toString());
				Toast.makeText(UserInfoActivity.this, "已经复制QQ号到剪贴板", Toast.LENGTH_SHORT).show();
			} 	
	    });

	    findViewById(R.id.userinfo_wechat).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClipboardManager cmb = (ClipboardManager) getSystemService(Service.CLIPBOARD_SERVICE);
				cmb.setText(textWechat.getText().toString());
				Toast.makeText(UserInfoActivity.this, "已经复制微信号到剪贴板", Toast.LENGTH_SHORT).show();
			}	
	    });
		
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
	
	private void getSuccess(Data data) {
		User user = data.getUser();
		
		textNickName.setText(user.getNick_name());
		textSex.setText(user.getSex());
		
		if (user.getTel() == null || user.getTel().equals("")) {
			findViewById(R.id.userinfo_phonenum).setEnabled(false);
		} else {
			textPhoneNum.setText(user.getTel());
		}
		if (user.getQq() == null || user.getQq().equals("")) {
			findViewById(R.id.userinfo_qq).setEnabled(false);
		} else {
			textQQ.setText(user.getQq());
		}
		if (user.getMic_msg() == null || user.getQq().equals("")) {
			findViewById(R.id.userinfo_wechat).setEnabled(false);
		} else {
			textWechat.setText(user.getMic_msg());
		}
		/*加载头像*/
		AsyncImageLoader asyncImageLoader = new AsyncImageLoader(UserInfoActivity.this);
		String imageUrl = Values.PICSAVEPATH + user.getUserid() + Values.PICSAVEFORMAT;
		Drawable cachedImage = null;
        final String sex = user.getSex();
    	asyncImageLoader.shutdownSave();
    	cachedImage = asyncImageLoader.loadDrawable(imageUrl, new ImageCallback() {
            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
            	imageLogo.setImageDrawable(imageDrawable);
            }
        });
        if (cachedImage == null) {
			if (sex.equals(Values.MALE)) {
				imageLogo.setImageResource(R.drawable.shuyou_boy_logo);
			} else {
				imageLogo.setImageResource(R.drawable.shuyou_girl_logo);
			}
		}
	}
	
	class GetUserInfoThread extends Thread {
		private String userId;
		
		public GetUserInfoThread(String userId) {
			this.userId = userId;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map<String, String> map = new HashMap<String, String>();
			map.put("checkuserid", userId);
			map.put("sessionid", UserInfo.sessionid);
			map.put("userid", UserInfo.userid);
			ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
			EnumCode code = changeToEnum.getUrl("get", Values.GET_USERINFO_URL, map);
			Message message = new Message();
			switch(code) {
			case OPERATE_SUCCESS:
				message.what = Values.SUCCESS_GET_USERINFO;
				message.obj = changeToEnum.getData();
				handler.sendMessage(message);
				break ;
			case NETWORK_ERROR:
				message.what = Values.NETWORK_ERROR;
				handler.sendMessage(message);
				break;
			default:
				message.what = Values.FAIL_GET_USERINFO;
				handler.sendMessage(message);
				break;
			}
		}
	}
	
	static class MyHandler extends Handler{

        WeakReference<UserInfoActivity> mActivity;
        
        public MyHandler(UserInfoActivity layout) {
        	mActivity = new WeakReference<UserInfoActivity>(layout);
        }

        @Override
        public void handleMessage(Message msg) {
        	UserInfoActivity theAct = mActivity.get();
            if(theAct == null)
            {
                return;
            }
            switch (msg.what) {
                case Values.SUCCESS_TO_SEARCH:
                    Data data = (Data)msg.obj;
                    theAct.getSuccess(data);
                    break;
                case Values.FAIL_TO_SEARCH:
                	Toast.makeText(theAct, "搜索失败", Toast.LENGTH_SHORT).show();
                	theAct.finish();
                	break;
                default:
                    break;
            }
        }
    }
	
	MyHandler handler = new MyHandler(this);
}
