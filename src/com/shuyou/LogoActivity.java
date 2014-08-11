package com.shuyou;

import com.shuyou.db.Preferences;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LogoActivity extends Activity {

	private Preferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = Preferences.getInstance(this);
		String loginInfo = mPref.getLoginInfo();
		if (loginInfo.equals("")) {
			setContentView(R.layout.activity_logo);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mPref.getUsedFlag()) {
						/*如果无用户登录信息，并且使用标示为true，则说明用户为首次使用，进入引导图*/
						startActivity(new Intent(LogoActivity.this,
								GuideActivity.class));
					} else {
						startActivity(new Intent(LogoActivity.this,
								LoginActivity.class));
					}
					finish();
				}
			}, 3000);
		}
		else{
			Intent i = new Intent(LogoActivity.this,LoginActivity.class);
			i.putExtra("autoLogin_platform", loginInfo);
			startActivity(i);
			finish();
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
	
	

}
