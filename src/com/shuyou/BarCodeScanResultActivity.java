package com.shuyou;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.shuyou.lib.internal.ChangeToEnum;
import com.shuyou.lib.internal.ChangeToEnum.EnumCode;
import com.shuyou.net.BookDetail;
import com.shuyou.net.UserInfo;
import com.shuyou.values.Values;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BarCodeScanResultActivity extends Activity {

	private static final String TAG = "BarCodeScanResultActivity";
	
	private Dialog dialogUsrInfo = null;
	private Dialog dialogConfirm = null;
	
	private String barcode;
	private String barcodeFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_test);
		
		LinearLayout dialogUsrInfoView = (LinearLayout)getLayoutInflater()
				.inflate(R.layout.shuyou_dialog_scanresult_confirm, null);
		dialogUsrInfo = new Dialog(BarCodeScanResultActivity.this, R.style.ShuyouDialog);
		dialogUsrInfo.setContentView(dialogUsrInfoView);
		
		RelativeLayout dialogConfirmView = (RelativeLayout)getLayoutInflater()
				.inflate(R.layout.shuyou_dialog_lendborrow_confirm, null);
		dialogConfirm = new Dialog(this, R.style.ShuyouDialog);
		dialogConfirm.setContentView(dialogConfirmView);
		
		barcode = getIntent().getStringExtra("barcode");
		barcodeFormat = getIntent().getStringExtra("barcodeFormat");
		if((barcodeFormat.equals("EAN_13") && barcode.startsWith("978"))){
			/*如果是扫描书的一维码，显示编辑备注内容*/
			getUploadBookDetail(barcode);
			dialogUsrInfo.show();
			dialogUsrInfo.setCancelable(false); //弹窗不能被关闭
			
		} else if (barcodeFormat.equals("QR_CODE")
				&& barcode.startsWith("#shuyou#")) {
			// 判断是借书还是还书
			dialogUsrInfo.show();
			dialogUsrInfo.setCancelable(false); //弹窗不能被关闭
			dialogUsrInfo.findViewById(R.id.dialog_scanresult_button).setVisibility(View.GONE);
			
			String temp = barcode.substring(8);
			String type = temp.substring(0, 1);// type:1代表用户上传的书，是借书二维码。3代表借入的书，是还书二维码
			
			if (type.endsWith("l")) {// 调用借书接口
				final String days = temp.substring(1, 3);
				final String str_secret = temp.substring(3);
				dialogConfirm.show();
				TextView tv = (TextView)dialogConfirm.findViewById(R.id.dialog_lendborrow_text);
				tv.setText("确认借入？");
				dialogConfirm.findViewById(R.id.dialog_lendborrow_ok).setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						lendBook(str_secret, days);
					}
				});
				dialogConfirm.findViewById(R.id.dialog_lendborrow_cancel).setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
			} else if (type.endsWith("r")) { // 调用还书接口
				final String str_secret = temp.substring(1);
				dialogConfirm.show();
				TextView tv = (TextView)dialogConfirm.findViewById(R.id.dialog_lendborrow_text);
				tv.setText("接收该书？");
				dialogConfirm.findViewById(R.id.dialog_lendborrow_ok).setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						returnBook(str_secret);
					}
				});
				dialogConfirm.findViewById(R.id.dialog_lendborrow_cancel).setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
				
			} else {
				Toast.makeText(getApplicationContext(), "格式不正确", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "该码格式不正确", Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	/**
	 * @author Chris
	 * @des 用户上传书后获得书的详情
	 */
	private void getUploadBookDetail(final String isbn) {
		
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				map.put("isbn", isbn);
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("get", Values.GET_BOOK_DETAIL_URL, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.what = Values.SUCCESS_TO_GET;
					message.obj = changeToEnum.getData().getBookDetail();
					handler.sendMessage(message);
					break ;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.FAIL_TO_GET;
					handler.sendMessage(message);
					break;
				}
			}
			
		}.start();
		
	}
    
    private void returnBook(final String str_secret) {
		new Thread(){
			@Override
			public void run() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("str_secret", str_secret);
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("post", Values.RETURN_BOOK_URL, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.what = Values.SUCCESS_TO_RETURN;
					message.obj = changeToEnum.getData();
					handler.sendMessage(message);
					break ;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.FAIL_TO_RETURN;
					handler.sendMessage(message);
					break;
				}
			}
			
		}.start();
	}

	private void lendBook(final String str_secret, final String limit_time) {
		new Thread() {
			
			@Override
			public void run() {			
				Map<String, String> map = new HashMap<String, String>();
				map.put("str_secret", str_secret);
				map.put("limit_time", limit_time);
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("post", Values.BORROW_BOOK_URL, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.what = Values.SUCCESS_TO_BORROW;
					message.obj = changeToEnum.getData();
					handler.sendMessage(message);
					break ;
				case BORROW_NO_MORE:
					message.what = Values.FAIL_TO_BORROW;
					message.obj = "，借入超过借出2本";
					handler.sendMessage(message);
					break;
				case BORROW_TOO_MANY:
					message.what = Values.FAIL_TO_BORROW;
					message.obj = "，借入书籍最多为8本";
					handler.sendMessage(message);
					break;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.FAIL_TO_BORROW;
					message.obj = "";
					handler.sendMessage(message);
					break;
				}
			}

		}.start();
	}
	
    @Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (dialogUsrInfo != null) {
        	dialogUsrInfo.dismiss();
        	dialogUsrInfo = null;
        }
        if (dialogConfirm != null) {
        	dialogConfirm.dismiss();
        	dialogConfirm = null;
        }
		finish();
	}
    
	/**
	 * 静态handler,防止内存泄漏错误
	 * @author chris
	 *
	 */
	static class MyInnerHandler extends Handler {
        WeakReference<BarCodeScanResultActivity> mActivity;

        MyInnerHandler(BarCodeScanResultActivity mAct) {
        	mActivity = new WeakReference<BarCodeScanResultActivity>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
        	BarCodeScanResultActivity theAct = mActivity.get();
        	switch (msg.what) {
        	case Values.FAIL_TO_GET:
				Toast.makeText(theAct, "处理失败", Toast.LENGTH_SHORT).show();
				theAct.finish();
				break;
			case Values.SUCCESS_TO_GET:
				BookDetail ib = (BookDetail)msg.obj;
				Intent i = new Intent();
				i.putExtra("type", "UploadViewDetail");
				i.putExtra("bookdetail", ib);
				i.setClass(theAct, BookInfoActivity.class);
				theAct.startActivity(i);
				break;
			case Values.SUCCESS_TO_BORROW:
        		Toast.makeText(theAct, "借书成功", Toast.LENGTH_SHORT).show();
        		theAct.finish();
        		break;
        	case Values.FAIL_TO_BORROW:
        		Toast.makeText(theAct, "借书失败" + msg.obj, Toast.LENGTH_SHORT).show();
        		theAct.finish();
        		break;
        	case Values.SUCCESS_TO_RETURN:
        		Toast.makeText(theAct, "还书成功", Toast.LENGTH_SHORT).show();
        		theAct.finish();
        		break;
        	case Values.FAIL_TO_RETURN:
        		Toast.makeText(theAct, "还书失败", Toast.LENGTH_SHORT).show();
        		theAct.finish();
        		break;
			case Values.NETWORK_ERROR:
				Toast.makeText(theAct, "网络连接超时", Toast.LENGTH_SHORT).show();
				theAct.finish();
				break;

			default:
				break;
			}
	    }
	 }
	 MyInnerHandler handler = new MyInnerHandler(this);
    
}
