package com.shuyou;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ConfigActivity extends Activity implements OnClickListener {

	private FeedbackAgent agent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
		findViewById(R.id.config_feedback).setOnClickListener(this);
		findViewById(R.id.config_checkUpdate).setOnClickListener(this);
		findViewById(R.id.config_about).setOnClickListener(this);
		findViewById(R.id.config_back).setOnClickListener(this);
		agent = new FeedbackAgent(this);
		agent.sync();// 服务器反馈获取
		UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                // TODO Auto-generated method stub
                switch (updateStatus) {
                    case 0: // has update
                        UmengUpdateAgent.showUpdateDialog(ConfigActivity.this, updateInfo);
                        break;
                    case 1: // has no update
                        Toast.makeText(ConfigActivity.this, "没有更新", Toast.LENGTH_SHORT).show();
                        break;
                    case 2: // none wifi
                        Toast.makeText(ConfigActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 3: // time out
                        Toast.makeText(ConfigActivity.this, "检查更新超时，请检查网络", Toast.LENGTH_SHORT).show();
                        break;
                }
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.config_feedback:
			agent.startFeedbackActivity();// 友盟反馈界面
			break;
		case R.id.config_checkUpdate:
			UmengUpdateAgent.forceUpdate(this);
			break;
		case R.id.config_about:
			startActivity(new Intent(ConfigActivity.this,
					AboutActivity.class));
			break;
		case R.id.config_back:
			finish();
			break;

		default:
			break;
		}

	}

}
