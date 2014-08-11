package com.shuyou;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.shuyou.application.ShuyouApplication;
import com.shuyou.db.Preferences;
import com.shuyou.net.Data;
import com.shuyou.tabui.TopHotShuyouTab;
import com.shuyou.tabui.TopNewShuyouTab;
import com.shuyou.utils.FilterAnimation;
import com.shuyou.utils.LogHelper;
import com.shuyou.weibo.AccessTokenKeeper;
import com.shuyou.weibo.LogoutAPI;
import com.shuyou.weibo.RequestListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.tauth.Tencent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends TabActivity implements OnClickListener {
	// 页卡内容
	private ViewPager mPager;
	// Tab页面列表
	private List<View> listViews;
	// 当前页卡编号
	private int currIndex = 0;
	// 动画图片宽度
	private int bmpW;
	// 动画图片偏移量
	private int offset = 0;
	// 动画图片
	private ImageView cursor;
	private LocalActivityManager manager = null;

	private TabHost mTabHost;
	private TextView tabTopNew = null;
	private TextView tabTopHot = null;

	private boolean doubleBackToExitPressedOnce = false;
	private RelativeLayout filterLayout = null;

	private AlertDialog.Builder logoutBuilder = null;

	private UMSocialService controller = null;
	private Preferences mPref;

	public static FilterAnimation filterAnimation = null;

	private FeedbackAgent agent = null;

	private Tencent mTencent;
	private String tencentAppId = "100569159";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		tabTopNew = (TextView) findViewById(R.id.main_tab_text1);
		tabTopHot = (TextView) findViewById(R.id.main_tab_text2);
		tabTopNew.setOnClickListener(new MyOnClickListener(0));
		tabTopHot.setOnClickListener(new MyOnClickListener(1));
		tabTopNew.setTextColor(getResources().getColor(R.color.white));

		mTabHost = getTabHost();
		mTabHost.addTab(mTabHost.newTabSpec("A").setIndicator("A")
				.setContent(new Intent(this, TopNewShuyouTab.class)));
		mTabHost.addTab(mTabHost.newTabSpec("B").setIndicator("B")
				.setContent(new Intent(this, TopHotShuyouTab.class)));

		mTabHost.setCurrentTab(0);

		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		InitImageView();
		InitViewPager();
		initLogoutDialog();

		InitButtonListener();
		mPref = Preferences.getInstance(this);

		controller = UMServiceFactory
				.getUMSocialService("", RequestType.SOCIAL);
		mTencent = Tencent.createInstance(tencentAppId,
				this.getApplicationContext());

		/* 侧滑菜单实现 */
		filterLayout = (RelativeLayout) findViewById(R.id.slidemenu_layout);
		filterAnimation = new FilterAnimation(this);

		initializeAnimations();
		initCheckUpdate();

		agent = new FeedbackAgent(this);
		agent.sync();
		
		/*设置用户已使用标识，则以后使用跳过引导图*/
		Preferences mPref = null;
		mPref = Preferences.getInstance(this);
		if(mPref.getUsedFlag()) {
			mPref.SetUsedFlag();
			findViewById(R.id.main_guide).setVisibility(View.VISIBLE);
		} 
	}

	private void initCheckUpdate() {
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				// TODO Auto-generated method stub
				switch (updateStatus) {
				case 0: // has update
					UmengUpdateAgent.showUpdateDialog(MainActivity.this,
							updateInfo);
					break;
				case 1: // has no update
					Toast.makeText(MainActivity.this, "没有更新",
							Toast.LENGTH_SHORT).show();
					break;
				case 2: // none wifi
					Toast.makeText(MainActivity.this, "没有wifi连接， 只在wifi下更新",
							Toast.LENGTH_SHORT).show();
					break;
				case 3: // time out
					Toast.makeText(MainActivity.this, "检查更新超时，请检查网络",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
	}

	private void initLogoutDialog() {
		if (logoutBuilder == null) {
			logoutBuilder = new AlertDialog.Builder(this);
		}

		logoutBuilder.setMessage("确定要注销登录吗？");
		logoutBuilder.setCancelable(false);
		logoutBuilder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						String platform = mPref.getLoginInfo();
						if (platform == null || platform.equals("")) {
							return;
						}

						if (platform.equals("QQ")) {
							mTencent.logout(getApplicationContext());
							startActivity(new Intent(MainActivity.this,
									LoginActivity.class));
							mPref.saveLoginInfo("");
							mPref.saveLoginUid("");
							finish();
						}
						if (platform.equals("sina")) {
							new LogoutAPI(AccessTokenKeeper
									.readAccessToken(MainActivity.this))
									.logout(new LogOutRequestListener());
							startActivity(new Intent(MainActivity.this,
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

						controller.deleteOauth(MainActivity.this, sm,
								new SocializeClientListener() {

									@Override
									public void onStart() {
										// TODO Auto-generated method stub

									}

									@Override
									public void onComplete(int arg0,
											SocializeEntity arg1) {
										startActivity(new Intent(
												MainActivity.this,
												LoginActivity.class));
										mPref.saveLoginInfo("");
										mPref.saveLoginUid("");
										finish();
									}
								});
					}
				});
		logoutBuilder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		logoutBuilder.create();
	}

	/**
	 * 初始化侧滑菜单动画
	 */
	private void initializeAnimations() { /*
										 * Setting GlobolLayoutListener,when
										 * layout is completely set this
										 * function will get called and we can
										 * have our layout object with correct
										 * width & height,else if you simply try
										 * to get width/height of your layout in
										 * onCreate it will return 0
										 */
		final ViewTreeObserver filterObserver = filterLayout
				.getViewTreeObserver();

		filterObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				filterLayout.getViewTreeObserver()
						.removeGlobalOnLayoutListener(this);
				DisplayMetrics displayMetrics = getResources()
						.getDisplayMetrics();

				int deviceWidth = displayMetrics.widthPixels;
				int filterLayoutWidth = (deviceWidth * 70) / 100; // 把侧滑菜单显示70%，与动画效果中移动的70%一致

				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						filterLayoutWidth,
						RelativeLayout.LayoutParams.MATCH_PARENT);
				filterLayout.setLayoutParams(params);

				filterAnimation.initializeFilterAnimations(filterLayout);
			}
		});

		final ViewTreeObserver findObserver = mTabHost.getViewTreeObserver();

		findObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				mTabHost.getViewTreeObserver().removeGlobalOnLayoutListener(
						this);

				filterAnimation.initializeOtherAnimations(mTabHost);
			}
		});
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.main_cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.shuyou_tab_selected).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		/* 获取图书信息 */
		Intent i = getIntent();
		Data data = (Data) i.getSerializableExtra("data");
		mPager = (ViewPager) findViewById(R.id.main_vPager);
		listViews = new ArrayList<View>();
		MyPagerAdapter mpAdapter = new MyPagerAdapter(listViews);
		LogHelper.i("MainData", data + "");
		Intent intent1 = new Intent(MainActivity.this, TopNewShuyouTab.class);
		intent1.putExtra("bookinfo", data);
		listViews.add(getView("new", intent1));

		Intent intent2 = new Intent(MainActivity.this, TopHotShuyouTab.class);
		listViews.add(getView("hot", intent2));
		mPager.setAdapter(mpAdapter);
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

		FeedbackAgent agent = new FeedbackAgent(this);
		agent.sync();
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			if (filterAnimation.isDisplayed()) {
				filterAnimation.SlidingHide();
				return;
			}
			mPager.setCurrentItem(index);
		}
	};

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量

		@Override
		public void onPageSelected(int arg0) {

			Animation animation = null;
			switch (arg0) {
			case 0:
				mTabHost.setCurrentTab(0);
				if (currIndex == 1) {
					tabTopNew.setTextColor(getResources().getColor(
							R.color.white));
					tabTopHot.setTextColor(getResources().getColor(
							R.color.black));
					animation = new TranslateAnimation(one, 0, 0, 0);
				}
				break;
			case 1:
				mTabHost.setCurrentTab(1);
				if (currIndex == 0) {
					tabTopNew.setTextColor(getResources().getColor(
							R.color.black));
					tabTopHot.setTextColor(getResources().getColor(
							R.color.white));
					animation = new TranslateAnimation(offset, one, 0, 0);
				}
				break;
			default:
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	@Override
	public void onBackPressed() {
		if (filterAnimation.isDisplayed()) {
			filterAnimation.SlidingHide();
			return;
		}
		Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
		if (doubleBackToExitPressedOnce) {
			finish();
		}
		doubleBackToExitPressedOnce = true;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000); // 2秒后执行线程
	}

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.main_userinfo:
			filterAnimation.toggleSliding();
			break;
		case R.id.main_saoma_tool:
			startActivity(new Intent(MainActivity.this,
					BarCodeScanActivity.class));
			findViewById(R.id.main_guide).setVisibility(View.GONE);
			break;
		case R.id.main_search:
			startActivity(new Intent(MainActivity.this, SearchActivity.class));
			break;
		case R.id.slidemenu_mybooks:
			filterAnimation.SlidingHide();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					startActivity(new Intent(MainActivity.this,
							BookshelfActivity.class));
				}
			}, 350); // 2秒后执行线程

			break;
		case R.id.slidemenu_mylove:
			break;
		case R.id.slidemenu_userinfo:
			filterAnimation.SlidingHide();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent i = new Intent(MainActivity.this,
							RegisterActivity.class);
					i.putExtra("type", "MainActivity");
					startActivity(i);
				}
			}, 350);

			break;
		case R.id.slidemenu_search:
			filterAnimation.toggleSliding();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					startActivity(new Intent(MainActivity.this,
							SearchActivity.class));
				}
			}, 350);
			break;
		case R.id.slidemenu_feedback:
			filterAnimation.toggleSliding();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					startActivity(new Intent(MainActivity.this,
							FeedbackActivity.class));
					//agent.startFeedbackActivity();
				}
			}, 350);
			break;
		case R.id.slidemenu_update:
			UmengUpdateAgent.forceUpdate(this);
			break;
		case R.id.slidemenu_logout:// 注销登录
			// 对话框
			if (logoutBuilder != null) {
				logoutBuilder.show();
			}
			break;
		default:
			filterAnimation.toggleSliding();
			break;
		}

	}

	/**
	 * 个人和搜索按钮监听器
	 */
	private void InitButtonListener() {
		// 左上角个人按钮
		findViewById(R.id.main_userinfo).setOnClickListener(this);

		// 主页扫码按钮
		findViewById(R.id.main_saoma_tool).setOnClickListener(this);

		findViewById(R.id.main_search).setOnClickListener(this);

		// 侧滑菜单我的书籍按钮
		findViewById(R.id.slidemenu_mybooks).setOnClickListener(this);

		// 侧滑菜单想看的书按钮
		findViewById(R.id.slidemenu_mylove).setOnClickListener(this);

		// 侧滑菜单个人资料按钮
		findViewById(R.id.slidemenu_userinfo).setOnClickListener(this);

		// 侧滑搜索按钮
		findViewById(R.id.slidemenu_search).setOnClickListener(this);

		// 侧滑反馈按钮
		findViewById(R.id.slidemenu_feedback).setOnClickListener(this);

		// 注销按钮
		findViewById(R.id.slidemenu_logout).setOnClickListener(this);

		findViewById(R.id.slidemenu_update).setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShuyouApplication.getInstance().onDestory();
	}

	/**
	 * 登出按钮的监听器，接收登出处理结果。（API 请求结果的监听器）
	 */
	private class LogOutRequestListener implements RequestListener {

		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				try {
					JSONObject obj = new JSONObject(response);
					String value = obj.getString("result");

					if ("true".equalsIgnoreCase(value)) {
						AccessTokenKeeper.clear(MainActivity.this);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onComplete4binary(ByteArrayOutputStream responseOS) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onIOException(IOException e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(WeiboException e) {
			// TODO Auto-generated method stub

		}

	}

}
