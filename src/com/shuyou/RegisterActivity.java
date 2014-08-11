package com.shuyou;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;

import com.shuyou.db.Preferences;
import com.shuyou.lib.AsyncImageLoader;
import com.shuyou.lib.AsyncImageLoader.ImageCallback;
import com.shuyou.lib.internal.ChangeToEnum;
import com.shuyou.lib.internal.ChangeToEnum.EnumCode;
import com.shuyou.net.Data;
import com.shuyou.net.StaticHttpClient;
import com.shuyou.net.User;
import com.shuyou.net.UserInfo;
import com.shuyou.utils.FileUtils;
import com.shuyou.values.Values;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "RegisterActivity";
	private boolean isUseDefaultLogo = true;
	private String platform_userid;
	private String sex;
	private String nickname;
	private String platform_kind;
	private boolean isGetInfoSuccess = false;
	private static String is_update = "0";

	private TextView textNickName = null;
	private TextView textSex = null;
	private TextView textPhoneNum = null;
	private TextView textQQ = null;
	private TextView textWechat = null;
	private ImageView imageLogo = null;

	private EditText editNickName = null;
	private EditText editPhoneNum = null;
	private EditText editQQ = null;
	private EditText editWechat = null;
	private RadioButton boy = null;
	private RadioButton girl = null;

	private ProgressBar registerProgress = null;
	private Dialog dialogUsrInfo = null;

	private RelativeLayout dialogNotice = null;
	private Dialog noticeDialog = null;

	private Preferences mPref;
	private UMSocialService controller = null;
	private Tencent mTencent;
	private String tencentAppId = "100569159";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		isGetInfoSuccess = false;

		mPref = Preferences.getInstance(this);
		controller = UMServiceFactory
				.getUMSocialService("", RequestType.SOCIAL);
		mTencent = Tencent.createInstance(tencentAppId,
				this.getApplicationContext());

		Init();

		if (getIntent().getStringExtra("type").equals("LoginActivity")) {
			is_update = "0";
			noticeDialog.show();

			Intent i = getIntent();
			platform_userid = i.getStringExtra("platform_userid");
			sex = i.getStringExtra("sex");
			nickname = i.getStringExtra("nickname");
			platform_kind = i.getStringExtra("platform_kind");
			textNickName.setText(nickname);
			textSex.setText(sex);
			SetUsrLogoBySex(sex);
			findViewById(R.id.register_usrlogo).setEnabled(false);
		} else if (getIntent().getStringExtra("type").equals("MainActivity")) {
			setEnable(false);
			is_update = "1";
			findViewById(R.id.register_back_area).setVisibility(View.VISIBLE);
			findViewById(R.id.register_confirm).setEnabled(false);
			TextView titleText = (TextView) findViewById(R.id.register_title_text);
			titleText.setText("个人资料");
			findViewById(R.id.register_confirm_button).setVisibility(View.GONE);
			findViewById(R.id.register_back_area).setOnClickListener(this);
			findViewById(R.id.register_confirm).setOnClickListener(this);
			new GetUserInfoThread().start();
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
	
	/**
	 * 根据传入的性别值来设置头像
	 * @param sex
	 */
	private void SetUsrLogoBySex(String sex) {
		if (false == isUseDefaultLogo) {
			return ;
		}
		if (sex.equals(Values.MALE)) {
			imageLogo.setImageResource(R.drawable.shuyou_boy_logo);
		} else {
			imageLogo.setImageResource(R.drawable.shuyou_girl_logo);
		}
	}

	/**
	 * 根据获得的第三方数据，初始化显示
	 */
	private void Init() {
		findViewById(R.id.register_usrlogo).setOnClickListener(this);
		findViewById(R.id.register_nickname).setOnClickListener(this);
		findViewById(R.id.register_sex).setOnClickListener(this);
		findViewById(R.id.register_age).setOnClickListener(this);
		findViewById(R.id.register_phonenum).setOnClickListener(this);
		findViewById(R.id.register_qq).setOnClickListener(this);
		findViewById(R.id.register_wechat).setOnClickListener(this);
		findViewById(R.id.register_confirm_button).setOnClickListener(this);

		registerProgress = (ProgressBar) findViewById(R.id.register_progress);
		dialogNotice = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.shuyou_dialog_userinfo_notice, null);

		noticeDialog = new Dialog(this, R.style.ShuyouDialog);
		noticeDialog.setContentView(dialogNotice);
		noticeDialog.setCanceledOnTouchOutside(true);
		textNickName = (TextView) findViewById(R.id.register_con_nickname);
		textSex = (TextView) findViewById(R.id.register_con_sex);
		textPhoneNum = (TextView) findViewById(R.id.register_con_phonenum);
		textQQ = (TextView) findViewById(R.id.register_con_qq);
		textWechat = (TextView) findViewById(R.id.register_con_wechat);
		imageLogo = (ImageView) findViewById(R.id.register_con_usrlogo);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.register_usrlogo:
			//showLogoSetDialog();
			showInfoDialog(R.layout.shuyou_dialog_userinfo_logo, 1);
			break;
		case R.id.register_nickname:
			showInfoDialog(R.layout.shuyou_dialog_userinfo_nickname, 2);
			break;
		case R.id.register_sex:
			showInfoDialog(R.layout.shuyou_dialog_userinfo_sex, 3);
			break;
		case R.id.register_age:
			// showInfoDialog(R.layout.shuyou_dialog_register_age, 4);
			break;
		case R.id.register_phonenum:
			showInfoDialog(R.layout.shuyou_dialog_userinfo_phonenum, 5);
			break;
		case R.id.register_qq:
			showInfoDialog(R.layout.shuyou_dialog_userinfo_qq, 6);
			break;
		case R.id.register_wechat:
			showInfoDialog(R.layout.shuyou_dialog_userinfo_wechat, 7);
			break;
		case R.id.dialog_nickname_confirm_button:
			if (editNickName.getText().toString().equals("")) {
				final TextView tmpTv = (TextView) dialogUsrInfo
						.findViewById(R.id.dialog_nickname_title);
				tmpTv.setText("请填昵称");
				tmpTv.setTextColor(getResources().getColor(R.color.red));
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						tmpTv.setText(getResources().getString(
								R.string.nickname_setting));
						tmpTv.setTextColor(getResources().getColor(
								R.color.dialog_text));
					}
				}, 1000); // 1秒后修改回来
			} else {
				textNickName.setText(editNickName.getText().toString());
				dialogUsrInfo.dismiss();
			}

			break;
		case R.id.dialog_sex_confirm_button:
			if (boy.isChecked()) {
				textSex.setText(Values.MALE);
				sex = Values.MALE;
			} else {
				textSex.setText(Values.FEMALE);
				sex = Values.FEMALE;
			}
			SetUsrLogoBySex(sex);
			dialogUsrInfo.dismiss();
			break;
		case R.id.dialog_phonenum_confirm_button:
			String tmpPhoneNum = editPhoneNum.getText().toString();
			/* 检测电话号码的正确性 */
			/*
			 * Pattern p = Pattern.compile
			 * ("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			 */
			Pattern p = Pattern.compile("^(1)\\d{10}$");
			Matcher m = p.matcher(tmpPhoneNum);
			if (m.matches() || tmpPhoneNum.equals("")) {
				textPhoneNum.setText(tmpPhoneNum);
				dialogUsrInfo.dismiss();
			} else {
				final TextView tmpTv = (TextView) dialogUsrInfo
						.findViewById(R.id.dialog_phonenum_title);
				tmpTv.setText("号码有误");
				tmpTv.setTextColor(getResources().getColor(R.color.red));
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						tmpTv.setText(getResources().getString(
								R.string.phonenum_setting));
						tmpTv.setTextColor(getResources().getColor(
								R.color.dialog_text));
					}
				}, 1000); // 1秒后修改回来
			}
			break;
		case R.id.dialog_qq_confirm_button:
			textQQ.setText(editQQ.getText().toString());
			dialogUsrInfo.dismiss();
			break;
		case R.id.dialog_wechat_confirm_button:
			textWechat.setText(editWechat.getText().toString());
			dialogUsrInfo.dismiss();
			break;
		case R.id.dialog_userinfo_logo_camera:
			Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 判断存储卡是否可以用，可用进行存储
			if (hasSdcard()) {
				intentFromCapture.putExtra(
						MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(Environment.getExternalStorageDirectory(), Values.IMAGE_FILE_NAME)));
			}
			startActivityForResult(intentFromCapture, Values.CAMERA_REQUEST_CODE);
			dialogUsrInfo.dismiss();
			break;
		case R.id.dialog_userinfo_logo_picture:
			Intent intentFromGallery = new Intent();
			intentFromGallery.setType("image/*"); // 设置文件类型
			intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intentFromGallery, Values.IMAGE_REQUEST_CODE);
			dialogUsrInfo.dismiss();
			break;
		case R.id.dialog_userinfo_logo_cancel:
			dialogUsrInfo.dismiss();
			break;
		case R.id.register_confirm_button:
			if (textPhoneNum.getText().equals("")
					&& textQQ.getText().equals("")
					&& textWechat.getText().equals("")) { // 三个联系方式至少填写一个
				noticeDialog.show();
			} else {
				registerProgress.setVisibility(View.VISIBLE);
				String nick_name = textNickName.getText().toString();
				String sex = textSex.getText().toString();
				String tel = textPhoneNum.getText().toString();
				String QQ = textQQ.getText().toString();
				String wechat = textWechat.getText().toString();
				UserInfo.nickName = nick_name;
				UserInfo.sex = sex;
				UserInfo.phoneNum = tel;
				UserInfo.QQ = QQ;
				UserInfo.weChat = wechat;

				sendPersonalInfo(nick_name, sex, tel, QQ, wechat, "", "",
						platform_userid, platform_kind);
			}
			break;
		case R.id.register_back_area:
			if (isGetInfoSuccess) {
				String nick_name1 = textNickName.getText().toString();
				String sex1 = textSex.getText().toString();
				String tel1 = textPhoneNum.getText().toString();
				String QQ1 = textQQ.getText().toString();
				String wechat1 = textWechat.getText().toString();
				if (textPhoneNum.getText().equals("")
						&& textQQ.getText().equals("")
						&& textWechat.getText().equals("")) { // 三个联系方式至少填写一个
					noticeDialog.show();
				} else {
					sendPersonalInfo(nick_name1, sex1, tel1, QQ1, wechat1,
							UserInfo.sessionid, UserInfo.userid, "", "");
					finish();
				}
			} else {
				finish();
			}
			break;
		case R.id.register_confirm:

			break;
		default:
			break;
		}
	}

	/**
	 * 根据id弹出相应窗口
	 * 
	 * @param id
	 */
	private void showInfoDialog(int id, int whichInfo) {
		LinearLayout dialogUsrInfoView = (LinearLayout) getLayoutInflater()
				.inflate(id, null);
		dialogUsrInfo = new Dialog(this, R.style.ShuyouDialog);
		dialogUsrInfo.setContentView(dialogUsrInfoView);
		dialogUsrInfo.show();
		dialogUsrInfo.setCanceledOnTouchOutside(true);
		switch (whichInfo) {
		case 1:
			dialogUsrInfo.findViewById(R.id.dialog_userinfo_logo_camera)
			.setOnClickListener(this);
			dialogUsrInfo.findViewById(R.id.dialog_userinfo_logo_picture)
			.setOnClickListener(this);
			dialogUsrInfo.findViewById(R.id.dialog_userinfo_logo_cancel)
			.setOnClickListener(this);
			break;
		case 2:
			editNickName = (EditText) dialogUsrInfo
					.findViewById(R.id.dialog_nickname_setting);
			editNickName.setText(textNickName.getText().toString());
			dialogUsrInfo.findViewById(R.id.dialog_nickname_confirm_button)
					.setOnClickListener(this);
			break;
		case 3:
			boy = (RadioButton) dialogUsrInfo
					.findViewById(R.id.dialog_userinfo_sex_boy);
			girl = (RadioButton) dialogUsrInfo
					.findViewById(R.id.dialog_userinfo_sex_girl);

			if (textSex.getText().toString().equals(Values.FEMALE)) {
				girl.setChecked(true);
			} else {
				boy.setChecked(true);
			}
			dialogUsrInfo.findViewById(R.id.dialog_sex_confirm_button)
					.setOnClickListener(this);
			break;
		case 4:
			// 地点选择保留
			break;
		case 5:
			editPhoneNum = (EditText) dialogUsrInfo
					.findViewById(R.id.dialog_phonenum_setting);
			editPhoneNum.setText(textPhoneNum.getText().toString());
			dialogUsrInfo.findViewById(R.id.dialog_phonenum_confirm_button)
					.setOnClickListener(this);
			break;
		case 6:
			editQQ = (EditText) dialogUsrInfo
					.findViewById(R.id.dialog_qq_setting);
			editQQ.setText(textQQ.getText().toString());
			dialogUsrInfo.findViewById(R.id.dialog_qq_confirm_button)
					.setOnClickListener(this);
			break;
		case 7:
			editWechat = (EditText) dialogUsrInfo
					.findViewById(R.id.dialog_wechat_setting);
			editWechat.setText(textWechat.getText().toString());
			dialogUsrInfo.findViewById(R.id.dialog_wechat_confirm_button)
					.setOnClickListener(this);
			break;
		default:
			break;
		}
	}

	private void getUserInfoSuccess(Data data) {
		AsyncImageLoader asyncImageLoader = new AsyncImageLoader(RegisterActivity.this);
		User user = data.getUser();

		textNickName.setText(user.getNick_name());
		textSex.setText(user.getSex());
		textPhoneNum.setText(user.getTel());
		textQQ.setText(user.getQq());
		textWechat.setText(user.getMic_msg());
		/*获取头像*/
		Drawable cachedImage = asyncImageLoader.loadDrawable(Values.PICSAVEPATH + UserInfo.userid+Values.PICSAVEFORMAT, new ImageCallback() {
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				// TODO Auto-generated method stub
				isUseDefaultLogo = false;
				imageLogo.setImageDrawable(imageDrawable);
			}
		});
		if (cachedImage == null) {
			SetUsrLogoBySex(user.getSex());
		}else{
			isUseDefaultLogo = false;
			imageLogo.setImageDrawable(cachedImage);
		}
		
		setEnable(true);
	}
	
	private void setEnable(boolean isEnable) {
		findViewById(R.id.register_nickname).setEnabled(isEnable);
		findViewById(R.id.register_sex).setEnabled(isEnable);
		findViewById(R.id.register_age).setEnabled(isEnable);
		findViewById(R.id.register_phonenum).setEnabled(isEnable);
		findViewById(R.id.register_qq).setEnabled(isEnable);
		findViewById(R.id.register_wechat).setEnabled(isEnable);
		findViewById(R.id.register_usrlogo).setEnabled(isEnable);
	}

	/**
	 * 向服务器发送个人信息
	 * @param nick_name
	 * @param sex
	 * @param tel
	 * @param QQ
	 * @param mic_msg
	 * @param sessionid
	 * @param platform_userid
	 * @param platform_kind
	 */
	private void sendPersonalInfo(String nick_name, String sex, String tel,
			String QQ, String mic_msg, String sessionid, String userid,
			String platform_userid, String platform_kind) {
		final Map<String, String> map = new HashMap<String, String>();

		map.put("nick_name", nick_name);
		if (sex.equals(Values.MALE)) {
			map.put("sex", "1");
		} else {
			map.put("sex", "0");
		}
		map.put("tel", tel);
		map.put("QQ", QQ);
		map.put("mic_msg", mic_msg);
		map.put("sessionid", sessionid);
		map.put("userid", userid);
		map.put("is_update", is_update);
		map.put("platform_userid", platform_userid);
		map.put("platform_kind", platform_kind);

		new Thread() {

			@Override
			public void run() {
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("get",
						Values.PERSONAL_INFO_URL, map);
				Message message = new Message();
				switch (code) {
				case OPERATE_SUCCESS:
					message.what = Values.SUCCESS_TO_REGISTER;
					message.obj = changeToEnum.getData();
					handler.sendMessage(message);
					break;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.FAIL_TO_REGISTER;
					handler.sendMessage(message);
					break;
				}
			}
		}.start();
	}

	class GetUserInfoThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map<String, String> map = new HashMap<String, String>();
			map.put("userid", UserInfo.userid);
			map.put("sessionid", UserInfo.sessionid);
			map.put("checkuserid", UserInfo.userid);
			ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
			EnumCode code = changeToEnum.getUrl("get", Values.GET_USERINFO_URL, map);
			Message message = new Message();
			switch (code) {
			case OPERATE_SUCCESS:
				message.obj = changeToEnum.getData();
				message.what = Values.SUCCESS_TO_GET_USERINFO;
				handler.sendMessage(message);
				break;
			case NETWORK_ERROR:
				message.what = Values.NETWORK_ERROR;
				handler.sendMessage(message);
				break;
			default:
				message.what = Values.FAIL_TO_GET_USERINFO;
				handler.sendMessage(message);
				break;
			}
		}
	}

	/**
	 * 静态handler,防止内存泄漏错误
	 * @author chris
	 */
	static class MyInnerHandler extends Handler {
		WeakReference<RegisterActivity> mActivity;

		MyInnerHandler(RegisterActivity mAct) {
			mActivity = new WeakReference<RegisterActivity>(mAct);
		}

		@Override
		public void handleMessage(Message msg) {
			RegisterActivity theAct = mActivity.get();
			switch (msg.what) {
			case Values.FAIL_TO_REGISTER:
				Toast.makeText(theAct, "注册失败",
						Toast.LENGTH_SHORT).show();
				break;
			case Values.SUCCESS_TO_REGISTER:
				if (is_update.equals("0")) { // 是注册，要获得sessionid和userid,并跳转到main
					Intent i = new Intent(theAct, MainActivity.class);
					Data data = (Data) msg.obj;
					UserInfo.sessionid = data.getSessionid();
					UserInfo.userid = data.getUserid();
					i.putExtra("data", data);
					theAct.startActivity(i);
					theAct.finish();
				} else if (is_update.equals("1")) {
					theAct.finish();
				}
				break;
			case Values.SUCCESS_TO_GET_USERINFO:
				theAct.getUserInfoSuccess((Data) msg.obj);
				theAct.findViewById(R.id.register_confirm).setEnabled(true);
				theAct.isGetInfoSuccess = true;
				break;
			case Values.FAIL_TO_GET_USERINFO:
				Toast.makeText(theAct, "获取失败", Toast.LENGTH_SHORT).show();
				break;
			case Values.SUCCESS_TO_UPLOAD_PIC:
				Toast.makeText(theAct, "头像上传成功", Toast.LENGTH_SHORT).show();
				theAct.imageLogo.setImageDrawable(theAct.imageLogoDrawable);
				break;
			case Values.FAIL_TO_UPLOAD_PIC:
				Toast.makeText(theAct, "头像上传失败", Toast.LENGTH_SHORT).show();
				break;
			case Values.NETWORK_ERROR:
				Toast.makeText(theAct, "网络连接错误", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			theAct.registerProgress.setVisibility(View.INVISIBLE);
		}
	}

	MyInnerHandler handler = new MyInnerHandler(this);
	
	private boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case Values.IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case Values.CAMERA_REQUEST_CODE:
				if (hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
									+ Values.IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(RegisterActivity.this, "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}

				break;
			case Values.RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	File tempFile = null;
	Drawable imageLogoDrawable = null;
	private void getImageToView(Intent data) {
		isUseDefaultLogo = false;
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			imageLogoDrawable = new BitmapDrawable(photo);
			BufferedOutputStream photoStream;
			tempFile = FileUtils.getCacheFile(RegisterActivity.this, Values.PICSAVEPATH + UserInfo.userid + Values.PICSAVEFORMAT);

			try {
				tempFile = new File(tempFile.getAbsolutePath());
				photoStream = new BufferedOutputStream(new FileOutputStream(tempFile));
				photo.compress(Bitmap.CompressFormat.JPEG, 20, photoStream);
				new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Map<String, String> map = new HashMap<String, String>();
						map.put("userid", UserInfo.userid);
						map.put("sessionid", UserInfo.sessionid);
						ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
						changeToEnum.SaveFile(tempFile);
						EnumCode code = changeToEnum.getUrl("uploadfile", Values.GET_USERINFO_URL, map);
						Message message = new Message();
						switch (code) {
						case OPERATE_SUCCESS:
							message.obj = changeToEnum.getData();
							message.what = Values.SUCCESS_TO_UPLOAD_PIC;
							handler.sendMessage(message);
							break;
						case NETWORK_ERROR:
							message.what = Values.NETWORK_ERROR;
							handler.sendMessage(message);
							break;
						default:
							message.what = Values.FAIL_TO_UPLOAD_PIC;
							handler.sendMessage(message);
							break;
						}
					}
				}.start();
				photoStream.flush();
				photoStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * 裁剪图片方法实现
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	@Override
	public void onBackPressed() {
		if (is_update.equals("0")) { // 如果是在注册页面,则退出时需清除本地Umeng数据
			String platform = mPref.getLoginInfo();
			if (platform == null || platform.equals("")) {
				finish();
			}
			if (platform.equals("QQ")) {
				mTencent.logout(getApplicationContext());
				startActivity(new Intent(RegisterActivity.this,
						LoginActivity.class));
				mPref.saveLoginInfo("");
				mPref.saveLoginUid("");
				finish();
			}
			if (platform.equals("sina")) {
				//mTencent.logout(getApplicationContext());
				startActivity(new Intent(RegisterActivity.this,
						LoginActivity.class));
				mPref.saveLoginInfo("");
				mPref.saveLoginUid("");
				finish();
			}
			SHARE_MEDIA sm = SHARE_MEDIA.NULL;
			if (platform.equals("renren")) {
				sm = SHARE_MEDIA.RENREN;
			} else if (platform.equals("sina")) {
				sm = SHARE_MEDIA.SINA;
			} else if (platform.equals("douban")) {
				sm = SHARE_MEDIA.DOUBAN;
			}
			controller.deleteOauth(RegisterActivity.this, sm, new SocializeClientListener() {
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
				}

				@Override
				public void onComplete(int arg0, SocializeEntity arg1) {
					startActivity(new Intent(RegisterActivity.this,
							LoginActivity.class));
					mPref.saveLoginInfo("");
					mPref.saveLoginUid("");
					finish();
				}});
		} else {
			finish();
		}
	}

}