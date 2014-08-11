package com.shuyou;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
import com.shuyou.db.DBService;
import com.shuyou.db.Preferences;
import com.shuyou.lib.internal.ChangeToEnum;
import com.shuyou.lib.internal.ChangeToEnum.EnumCode;
import com.shuyou.net.BookDetail;
import com.shuyou.net.Data;
import com.shuyou.net.SearchLabel;
import com.shuyou.net.UserInfo;
import com.shuyou.utils.FileUtils;
import com.shuyou.utils.LogHelper;
import com.shuyou.utils.SDCardUtils;
import com.shuyou.values.Values;
import com.shuyou.weibo.AccessTokenKeeper;
import com.shuyou.weibo.RequestListener;
import com.shuyou.weibo.UsersAPI;
import com.shuyou.weibo.WeiboConstants;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	private static final String TAG = "LoginActivity";

	private SHARE_MEDIA whichPlatform = null;
	private String whichPlatformSign = null;
	// private String whichPlatformAS = null;
	private String platform_userid = null;

	private UMSocialService controller = null;

	private LinearLayout loginRenren = null;
	private LinearLayout loginSina = null;
	private LinearLayout loginDouban = null;
	private LinearLayout loginQQ = null;
	private ProgressBar loginProgress = null;

	private DBService dbService;

	public static boolean isHotWordsReady = false;

	// private LocationClient mLocClient;

	private Preferences mPref;

	private LinearLayout ll_version;

	private String autoLogin_platform = null;

	private String tencentAppId = "100569159";

	private Tencent mTencent;

	private String QQopenid = "";

	/** 微博 Web 授权类，提供登陆等功能 */
	private WeiboAuth mWeiboAuth;

	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private SsoHandler mSsoHandler;

	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
	private Oauth2AccessToken mAccessToken;

	/** 微博用户信息对应的回调 */
	private UserInofRequestListener mUserInofRequestListener = new UserInofRequestListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		dbService = DBService.getInstance(this);
		mPref = Preferences.getInstance(this);
		loginRenren = (LinearLayout) findViewById(R.id.login_renren);
		loginSina = (LinearLayout) findViewById(R.id.login_sina);
		loginDouban = (LinearLayout) findViewById(R.id.login_douban);
		loginQQ = (LinearLayout) findViewById(R.id.login_QQ);
		loginProgress = (ProgressBar) findViewById(R.id.login_progressbar);
		ll_version = (LinearLayout) findViewById(R.id.login_version);

		mTencent = Tencent.createInstance(tencentAppId,
				this.getApplicationContext());

		mWeiboAuth = new WeiboAuth(this, WeiboConstants.APP_KEY,
				WeiboConstants.REDIRECT_URL, WeiboConstants.SCOPE);

		loginRenren.setOnClickListener(this);
		loginSina.setOnClickListener(this);
		loginDouban.setOnClickListener(this);
		loginQQ.setOnClickListener(this);
		controller = UMServiceFactory
				.getUMSocialService("", RequestType.SOCIAL);

		autoLogin_platform = getIntent().getStringExtra("autoLogin_platform");
		if (autoLogin_platform != null) {
			loginRenren.setVisibility(View.INVISIBLE);
			loginSina.setVisibility(View.INVISIBLE);
			loginDouban.setVisibility(View.INVISIBLE);
			loginQQ.setVisibility(View.INVISIBLE);
			ll_version.setVisibility(View.VISIBLE);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (autoLogin_platform.equals("renren")) {
						loginRenren.performClick();
					} else if (autoLogin_platform.equals("sina")) {
						loginSina.performClick();
					} else if (autoLogin_platform.equals("douban")) {
						loginDouban.performClick();
					} else if (autoLogin_platform.equals("QQ")) {
						loginQQ.performClick();
					}
				}
			}, 2000);

		}

		new ClearCacheThread().start();

		// mLocClient = ((ShuyouApplication)getApplication()).mLocationClient;
		// ((ShuyouApplication)getApplication()).act = LoginActivity.this;

		// 设置定位参数
		// setLocationOption();

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

	private void setBtnEnable(boolean enable) {
		loginRenren.setEnabled(enable);
		loginSina.setEnabled(enable);
		loginDouban.setEnabled(enable);
		loginQQ.setEnabled(enable);
	}

	@Override
	public void onClick(View v) {
		setBtnEnable(false);
		switch (v.getId()) {
		case R.id.login_renren:
			whichPlatform = SHARE_MEDIA.RENREN;
			whichPlatformSign = "renren";
			// whichPlatformAS = "renren_as";
			break;
		case R.id.login_sina:
			 whichPlatform = SHARE_MEDIA.SINA;
			 //whichPlatformSign = "sina";
			 // whichPlatformAS = "sina_as";
			 // break;
			 weibologin();
			 return;
			 //break;
		case R.id.login_douban:
			whichPlatform = SHARE_MEDIA.DOUBAN;
			whichPlatformSign = "douban";
			// whichPlatformAS = "douban_as";
			break;
		case R.id.login_QQ:
			QQlogin();
			return;
			// break;
		default:
			return;
		}
		// 判定本地是否有信息，如果有，直接进行登录操作
		SharedPreferences sp_login = getSharedPreferences("umeng_socialize",
				Context.MODE_PRIVATE);

		platform_userid = sp_login.getString(whichPlatformSign, "not_exsit");
		// String accessToken = sp_login.getString(whichPlatformAS,
		// "not_exsit");

		if (platform_userid.equals("not_exsit")) {
			doOauth();
		} else {
			doLogin(platform_userid, whichPlatformSign);
		}
	}

	private void weibologin() {
		String loginInfo = mPref.getLoginInfo();
		String loginUid = mPref.getLoginUid();
		if (loginInfo.equals("sina")) {
			doLogin(loginUid, loginInfo);
			return;
		}
		mSsoHandler = new SsoHandler(LoginActivity.this, mWeiboAuth);
		mSsoHandler.authorize(new AuthListener());
	}

	private void getWeiboInfo() {
		// 获取 Token
		Oauth2AccessToken accessToken = AccessTokenKeeper
				.readAccessToken(LoginActivity.this);
		if (accessToken != null && accessToken.isSessionValid()) {
			//
			long uid = Long.parseLong(accessToken.getUid());
			new UsersAPI(mAccessToken).show(uid, mUserInofRequestListener);
		}
	}

	private void QQlogin() {
		String loginInfo = mPref.getLoginInfo();
		String loginUid = mPref.getLoginUid();
		if (loginInfo.equals("QQ")) {
			doLogin(loginUid, loginInfo);
			return;
		}

		if (!mTencent.isSessionValid()) {
			mTencent.login(this, "get_simple_userinfo", new IUiListener() {

				@Override
				public void onError(UiError arg0) {
					setBtnEnable(true);
				}

				@Override
				public void onComplete(JSONObject json) {
					LogHelper.i("jiabin", json.toString());
					String ret = "";
					String msg = "";
					String openid = "";
					try {
						ret = json.getString("ret");
						msg = json.getString("msg");
						openid = json.getString("openid");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (ret.equals("0")) {
						Toast.makeText(getApplicationContext(), "授权成功",
								Toast.LENGTH_SHORT).show();
						QQopenid = openid;
						doLogin(openid, "QQ");
					}
				}

				@Override
				public void onCancel() {
					setBtnEnable(true);
				}
			});
		} else {
			doLogin(mTencent.getOpenId(), "QQ");
		}
	}

	protected void getQQinfo() {
		mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null,
				Constants.HTTP_GET, new IRequestListener() {

					@Override
					public void onUnknowException(Exception arg0, Object arg1) {
						Toast.makeText(getApplicationContext(),
								"异常:" + arg0.toString(), Toast.LENGTH_SHORT)
								.show();
						setBtnEnable(true);
					}

					@Override
					public void onSocketTimeoutException(
							SocketTimeoutException arg0, Object arg1) {
						Toast.makeText(getApplicationContext(),
								"异常:" + arg0.toString(), Toast.LENGTH_SHORT)
								.show();
						setBtnEnable(true);
					}

					@Override
					public void onNetworkUnavailableException(
							NetworkUnavailableException arg0, Object arg1) {
						Toast.makeText(getApplicationContext(),
								"异常:" + arg0.toString(), Toast.LENGTH_SHORT)
								.show();
						setBtnEnable(true);
					}

					@Override
					public void onMalformedURLException(
							MalformedURLException arg0, Object arg1) {
						Toast.makeText(getApplicationContext(),
								"异常:" + arg0.toString(), Toast.LENGTH_SHORT)
								.show();
						setBtnEnable(true);
					}

					@Override
					public void onJSONException(JSONException arg0, Object arg1) {
						Toast.makeText(getApplicationContext(),
								"异常:" + arg0.toString(), Toast.LENGTH_SHORT)
								.show();
						setBtnEnable(true);
					}

					@Override
					public void onIOException(IOException arg0, Object arg1) {
						Toast.makeText(getApplicationContext(),
								"异常:" + arg0.toString(), Toast.LENGTH_SHORT)
								.show();
						setBtnEnable(true);
					}

					@Override
					public void onHttpStatusException(HttpStatusException arg0,
							Object arg1) {
						Toast.makeText(getApplicationContext(),
								"异常:" + arg0.toString(), Toast.LENGTH_SHORT)
								.show();
						setBtnEnable(true);
					}

					@Override
					public void onConnectTimeoutException(
							ConnectTimeoutException arg0, Object arg1) {
						Toast.makeText(getApplicationContext(),
								"异常:" + arg0.toString(), Toast.LENGTH_SHORT)
								.show();
						setBtnEnable(true);
					}

					@Override
					public void onComplete(JSONObject json, Object obj) {
						LogHelper.i("jiabin", json.toString());
						// LogHelper.i("jiabin", obj.toString());
						String nickname = "";
						String gender = "";
						try {
							nickname = json.getString("nickname");
							gender = json.getString("gender");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (!nickname.equals("")) {
							Intent i = new Intent(LoginActivity.this,
									RegisterActivity.class);
							i.putExtra("type", "LoginActivity");
							if (gender.equals("男")) {
								i.putExtra("sex", "男");
							} else {
								i.putExtra("sex", "女");
							}
							i.putExtra("nickname", nickname);
							i.putExtra("platform_userid", QQopenid);
							i.putExtra("platform_kind", "QQ");
							startActivity(i);
							finish();
						}
					}
				}, null);
	}

	private void getHotWords() {
		new Thread() {

			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("get", Values.HOTWORDS_URL,
						map);
				switch (code) {
				case OPERATE_SUCCESS:
					ArrayList<BookDetail> list = changeToEnum.getData()
							.getBook_list();
					dbService.saveHotWords(list);
					break;
				default:
					break;
				}
			}
		}.start();
	}

	private void getSearchLabel() {

		new Thread() {
			@Override
			public void run() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("get",
						Values.SEARCHLABEL_URL, map);
				switch (code) {
				case OPERATE_SUCCESS:
					ArrayList<SearchLabel> list = changeToEnum.getData()
							.getLabel_list();
					dbService.saveSearchLabel(list);
					break;
				default:
					break;
				}
			}

		}.start();
	}

	private void doLogin(final String platform_userid,
			final String platform_kind) {
		loginProgress.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				Map<String, String> map = new HashMap<String, String>();
				LogHelper.v("pengtao", "platform_userid = "+platform_userid+",platform_kind = "+platform_kind);
				map.put("platform_userid", platform_userid);
				map.put("platform_kind", platform_kind);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("post", Values.LOGIN_URL,
						map);
				Message message = new Message();
				switch (code) {
				case OPERATE_SUCCESS:
					mPref.saveLoginInfo(platform_kind);
					mPref.saveLoginUid(platform_userid);

					Data data = changeToEnum.getData();
					if (data.getHave_info().equals("0"))// 服务器没有账户
					{
						if (platform_kind.equals("QQ")) {
							message.what = Values.NEED_TO_REGISTER_QQ;
						} else if (platform_kind.equals("sina")) {
							message.what = Values.NEED_TO_REGISTER_SINA;
						} else {
							message.what = Values.NEED_TO_REGISTER;
						}

						handler.sendMessage(message);
					} else if (data.getHave_info().equals("1"))// 账户已注册
					{
						message.what = Values.SUCCESS_TO_LOGIN;
						message.obj = data;
						handler.sendMessage(message);
					}
					break;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.FAIL_TO_LOGIN;
					handler.sendMessage(message);
					break;
				}
			}

		}.start();
	}

	private void doOauth() {
		controller.doOauthVerify(LoginActivity.this, whichPlatform,
				new UMAuthListener() {

					@Override
					public void onStart(SHARE_MEDIA arg0) {
						// TODO Auto-generated method stub
						LogHelper.i(TAG, "oauth onStart");
					}

					@Override
					public void onError(SocializeException e, SHARE_MEDIA arg1) {
						setBtnEnable(true);
						LogHelper.i(TAG, "oauth onError:" + e.toString());
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						if (value != null
								&& !TextUtils.isEmpty(value.getString("uid"))) {
							Toast.makeText(LoginActivity.this, "授权成功.",
									Toast.LENGTH_SHORT).show();
							// value.getString("access_secret")
							LogHelper.i("jiabin", value.toString());
							doLogin(value.getString("uid"), whichPlatformSign);
						} else {
							Toast.makeText(LoginActivity.this, "授权失败",
									Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onCancel(SHARE_MEDIA arg0) {
						setBtnEnable(true);
						LogHelper.i(TAG, "onCancel");
					}
				});
	}

	/**
	 * 更具已经具有的sessionid跳转到RegisterActivity.java页面，进行个人信息的注册
	 * 
	 * @param sessionid
	 */
	private void gotoPersonalInfo() {
		controller.getPlatformInfo(LoginActivity.this, whichPlatform,
				new UMDataListener() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
					}

					@Override
					public void onComplete(int status, Map<String, Object> info) {
						if (status == 200 && info != null) {
							StringBuilder sb = new StringBuilder();
							Set<String> keys = info.keySet();
							for (String kStr : keys) {
								sb.append(kStr + "="
										+ info.get(kStr).toString() + "\r\n");
							}
							LogHelper.d("TestData", sb.toString());
							Intent i = new Intent(LoginActivity.this,
									RegisterActivity.class);
							i.putExtra("type", "LoginActivity");
							if (info.get("gender").toString().equals("1")) {
								i.putExtra("sex", "男");
							} else {
								i.putExtra("sex", "女");
							}
							i.putExtra("nickname", info.get("screen_name")
									.toString());
							i.putExtra("platform_userid", info.get("uid")
									.toString());
							i.putExtra("platform_kind", whichPlatformSign);
							startActivity(i);
							finish();
						} else
							setBtnEnable(true);
						Toast.makeText(getApplicationContext(),
								"发生错误" + status, Toast.LENGTH_SHORT).show();
					}
				});
	}

	// 静态handler,防止内存泄漏错误
	static class MyInnerHandler extends Handler {
		WeakReference<LoginActivity> mActivity;

		MyInnerHandler(LoginActivity mAct) {
			mActivity = new WeakReference<LoginActivity>(mAct);
		}

		@Override
		public void handleMessage(Message msg) {
			LoginActivity theAct = mActivity.get();
			switch (msg.what) {
			case Values.FAIL_TO_LOGIN:
				Toast.makeText(theAct, "登录失败", Toast.LENGTH_SHORT).show();
				theAct.loginProgress.setVisibility(View.INVISIBLE);
				theAct.setBtnEnable(true);
				break;
			case Values.SUCCESS_TO_LOGIN:
				// 进入首页，已有booklist等等
				Data data = (Data) msg.obj;
				UserInfo.sessionid = data.getSessionid();
				UserInfo.userid = data.getUserid();
				theAct.getHotWords();
				theAct.getSearchLabel();
				Intent i = new Intent(theAct, MainActivity.class);
				i.putExtra("data", data);
				theAct.startActivity(i);
				theAct.finish();
				break;
			case Values.NEED_TO_REGISTER_QQ:
				theAct.getQQinfo();
				break;
			case Values.NEED_TO_REGISTER_SINA:
				theAct.getWeiboInfo();
				break;
			case Values.NEED_TO_REGISTER:
				theAct.gotoPersonalInfo();
				break;
			case Values.NETWORK_ERROR:
				Toast.makeText(theAct, "网络连接错误", Toast.LENGTH_SHORT).show();
				theAct.loginProgress.setVisibility(View.INVISIBLE);
				theAct.setBtnEnable(true);
				break;
			case Values.LOCATION_ERROR:
				Toast.makeText(theAct, "位置信息获取失败", Toast.LENGTH_LONG).show();
				theAct.loginProgress.setVisibility(View.INVISIBLE);
				theAct.setBtnEnable(true);
				break;
			default:
				break;
			}
		}
	}

	MyInnerHandler handler = new MyInnerHandler(this);

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// finish();

		super.onActivityResult(requestCode, resultCode, data);
		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			Toast.makeText(LoginActivity.this,
					R.string.weibosdk_demo_toast_auth_canceled,
					Toast.LENGTH_LONG).show();
			setBtnEnable(true);
		}

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				// 显示 Token
				// updateTokenView(false);

				// 保存 Token 到 SharedPreferences
				String uid = mAccessToken.getUid();
				String token = mAccessToken.getToken();
				LogHelper.i("jiabin", uid + " : " + token);
				AccessTokenKeeper.writeAccessToken(LoginActivity.this,
						mAccessToken);
				Toast.makeText(LoginActivity.this,
						R.string.weibosdk_demo_toast_auth_success,
						Toast.LENGTH_SHORT).show();
				// getWeiboInfo();
				doLogin(uid, "sina");
			} else {
				// 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
				String code = values.getString("code");
				String message = getString(R.string.weibosdk_demo_toast_auth_failed);
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(LoginActivity.this,
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
			setBtnEnable(true);
		}

	}

	private class UserInofRequestListener implements RequestListener {

		@Override
		public void onComplete(String response) {
			LogHelper.i(TAG, "userInfo Response: " + response);

			if (TextUtils.isEmpty(response) || response.contains("error_code")) {
				try {
					JSONObject obj = new JSONObject(response);
					String errorMsg = obj.getString("error");
					String errorCode = obj.getString("error_code");
					String message = "error_code: " + errorCode
							+ "error_message: " + errorMsg;
					LogHelper.e(TAG, "userInfo Failed: " + message);
					// showToast(false, message);
					Toast.makeText(getApplicationContext(), "error:" + message,
							Toast.LENGTH_LONG).show();
					setBtnEnable(true);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				String screen_name = "";
				String gender = "";
				String id = "";
				try {
					JSONObject obj = new JSONObject(response);
					screen_name = obj.getString("screen_name");
					gender = obj.getString("gender");
					id = obj.getString("id");
					LogHelper.i("jiabin", id + " : " + screen_name + " : " + gender);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!screen_name.equals("")) {
					Intent i = new Intent(LoginActivity.this,
							RegisterActivity.class);
					i.putExtra("type", "LoginActivity");
					if (gender.equalsIgnoreCase("m")) {
						i.putExtra("sex", "男");
					} else {
						i.putExtra("sex", "女");
					}
					i.putExtra("nickname", screen_name);
					i.putExtra("platform_userid", id);
					i.putExtra("platform_kind", "sina");
					startActivity(i);
					finish();
				}

			}
		}

		@Override
		public void onComplete4binary(ByteArrayOutputStream responseOS) {
			LogHelper.i("jiabin", responseOS.toString());
		}

		@Override
		public void onIOException(IOException e) {
			LogHelper.i("jiabin", e.toString());
			Toast.makeText(getApplicationContext(), "异常:" + e.toString(),
					Toast.LENGTH_SHORT).show();
			setBtnEnable(true);
		}

		@Override
		public void onError(WeiboException e) {
			LogHelper.i("jiabin", e.toString());
			Toast.makeText(getApplicationContext(), "异常:" + e.toString(),
					Toast.LENGTH_SHORT).show();
			setBtnEnable(true);
		}

	}

	/**
	 * 清除详情页缓存线程，删除目录是耗时操作，需开线程
	 * 
	 * @author jiabin
	 * 
	 */
	private class ClearCacheThread extends Thread {

		@Override
		public void run() {
			super.run();
			FileUtils.clearCache(SDCardUtils.getBookDetailInfoCachePath());
		}
	}
}
