package com.shuyou;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shuyou.lib.BookOwnersListView;
import com.shuyou.lib.BookOwnersListViewAdapter;
import com.shuyou.lib.internal.ChangeToEnum;
import com.shuyou.lib.internal.ChangeToEnum.EnumCode;
import com.shuyou.net.Data;
import com.shuyou.net.User;
import com.shuyou.net.UserInfo;
import com.shuyou.utils.AnimationUtil;
import com.shuyou.utils.LogHelper;
import com.shuyou.values.Values;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class BookOwnersActivity extends Activity{
	private String bookName;
	private RelativeLayout loading = null;

	private final static String TAG = "BookOwnersActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bookowners);
		
		loading = (RelativeLayout) this.findViewById(R.id.bookowners_loading_layout);
        new AnimationUtil().showLoadingDialog(loading);
		
		findViewById(R.id.bookowners_back).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		Intent i = getIntent();
		isbnUsers(i.getStringExtra("isbn"));
		bookName = i.getStringExtra("bookName");
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
	
	private void isbnUsers(final String isbn) {
		new Thread() {

			@Override
			public void run() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("isbn", isbn);
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("get", Values.GET_OWNERS_URL, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.what = Values.SUCCESS_TO_LOADOWNERS;
					message.obj = changeToEnum.getData();
					handler.sendMessage(message);
					break ;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.FAIL_TO_LOADOWNERS;
					handler.sendMessage(message);
					break;
				}
			}

		}.start();

	}
	
	private void LoadBooksSuccess(Data data) {
		List<BookOwnersListView> bookOwners = new ArrayList<BookOwnersListView>();
		ListView list = (ListView)findViewById(R.id.list_bookowners);
		
		if (data != null) {
			//最好判断一下获取的Sessionid是否为空串或者null
			ArrayList<User> userlist = data.getUser_list();
			for (User ib : userlist) {
				BookOwnersListView bookOwner = new BookOwnersListView(Values.PICSAVEPATH + ib.getUserid() + Values.PICSAVEFORMAT, 
						ib.getNick_name(), ib.getTel(), ib.getQq(), ib.getMic_msg(), ib.getComment(), ib.getSex());
				bookOwners.add(bookOwner);
			}
			BookOwnersListViewAdapter listAdapter = new BookOwnersListViewAdapter(BookOwnersActivity.this, bookOwners, list, bookName);
			list.setAdapter(listAdapter);
		}
	}
	
	/**
	 * 静态handler,防止内存泄漏错误
	 * @author chris
	 *
	 */
	static class MyInnerHandler extends Handler {
        WeakReference<BookOwnersActivity> mActivity;

        MyInnerHandler(BookOwnersActivity mAct) {
        	mActivity = new WeakReference<BookOwnersActivity>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
        	BookOwnersActivity theAct = mActivity.get();
        	switch(msg.what){
			case Values.SUCCESS_TO_LOADOWNERS:
				// TODO Auto-generated method stub
				theAct.LoadBooksSuccess((Data)msg.obj);
				break;
			case Values.FAIL_TO_LOADOWNERS:
				Toast.makeText(theAct, "获取数据出错", Toast.LENGTH_SHORT).show();
				break;
			case Values.NETWORK_ERROR:
				Toast.makeText(theAct, "网络异常", Toast.LENGTH_SHORT).show();
				break;
			}
        	new AnimationUtil().dismissLoadingDialog(theAct.loading);
	    }
	 }
	 MyInnerHandler handler = new MyInnerHandler(this);

}
