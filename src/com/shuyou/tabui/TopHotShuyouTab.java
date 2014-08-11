package com.shuyou.tabui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shuyou.BookInfoActivity;
import com.shuyou.MainActivity;
import com.shuyou.R;
import com.shuyou.lib.BooksDesListView;
import com.shuyou.lib.BooksDesListViewAdapter;
import com.shuyou.lib.PullToRefreshListView;
import com.shuyou.lib.PullToRefreshBase.OnRefreshListener;
import com.shuyou.lib.internal.ChangeToEnum;
import com.shuyou.lib.internal.ChangeToEnum.EnumCode;
import com.shuyou.net.BookDetail;
import com.shuyou.net.Data;
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
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TopHotShuyouTab extends Activity{
	
	private final static String TAG = "TopHotShuyouTab";
	
	private Data   data = null;
	private ArrayList<BookDetail> booklist = new ArrayList<BookDetail>();
	private TextView listFooter = null;
	private BooksDesListViewAdapter booksDesListAdapter = null;
	
	private PullToRefreshListView refreshList = null;
	private ListView list;
	private int whichPage = 1;
	private boolean isRefreshed = false;
	private RelativeLayout loading = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shuyou_tab_tophot);
		
		loading = (RelativeLayout) this.findViewById(R.id.tab_tophot_loading_layout);
        new AnimationUtil().showLoadingDialog(loading);
		
		/*创建Tabhost时，Activity会被oncreate一次，滑动到该activity,又会被oncreate一次，所以只让它请求一次*/
		
		List<BooksDesListView> booksDesList = new ArrayList<BooksDesListView>();
		for(int i = 0; i < 4; ++ i) {
			BooksDesListView tmpBooksDes = new BooksDesListView("null", "null", "null","null","null");
			booksDesList.add(tmpBooksDes);
		}
		
		refreshList = (PullToRefreshListView)findViewById(R.id.tophot_list);
		list = refreshList.getRefreshableView();
		refreshList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				isRefreshed = true;
				getIndexBooks("-1", "-1");   //刷新请求第一页
			}
			
		});
		
		booksDesListAdapter = new BooksDesListViewAdapter(TopHotShuyouTab.this, 
				booksDesList, list);
		
		listFooter = new TextView(TopHotShuyouTab.this);
		/*设置底部加载更多风格*/
		listFooter.setBackgroundDrawable(getResources().getDrawable(R.drawable.shuyou_listfooter_bg));
		listFooter.setShadowLayer((float)0.5, (float)1, (float)1, R.color.black);
		listFooter.setPadding(30, 15, 0, 10);
		listFooter.setGravity(Gravity.CENTER);
		listFooter.setText("加载更多");
		listFooter.setTextSize((float) 20.0);
		
		listFooter.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(MainActivity.filterAnimation.isDisplayed()) {
					MainActivity.filterAnimation.SlidingHide();
					return ;
				}
				// TODO Auto-generated method stub
				//如果第一页加载出现异常，则用户可以选择加载更多来重新加载第一页
				listFooter.setText("正在加载...");
				listFooter.setEnabled(false);
				getIndexBooks(data.getLast_zan_count(), data.getLast_zan_time());
			}
		});
		listFooter.setEnabled(false);
		
		list.addFooterView(listFooter);
		
		list.setDivider(null);
		list.setAdapter(booksDesListAdapter);
		
		list.setEnabled(false);
		list.setOnItemClickListener(new OnItemClickListener(){
		
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
				if(MainActivity.filterAnimation.isDisplayed()) {
					MainActivity.filterAnimation.SlidingHide();
					return ;
				}
				try {
					Intent i = new Intent();
					i.putExtra("type", "ViewBookDetail");
					i.putExtra("bookIsbn", booklist.get(position).getIsbn());
					i.putExtra("bookName", booklist.get(position).getBook_name());
					i.putExtra("bookAuthor", booklist.get(position).getAuthor());
					i.putExtra("bookLabel", booklist.get(position).getLabel());
					i.setClass(TopHotShuyouTab.this, BookInfoActivity.class);
					startActivity(i);
				} catch (IndexOutOfBoundsException e) {
					return ;
				}
			}
		});
		
		getIndexBooks("-1", "-1");
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}


	/**
	 * 加载更多最热图书，每次加载20本
	 * @param last_zan_count
	 */
	private void getIndexBooks(final String last_zan_count, final String last_zan_time) {
		final Map<String, String> map = new HashMap<String, String>();
		//TODO
		map.put("books_type", "hot");
		map.put("userid", UserInfo.userid);
		map.put("sessionid", UserInfo.sessionid);
		
		map.put("last_zan_count", last_zan_count);
		map.put("last_zan_time", last_zan_time);
		
		new Thread() {
			@Override
			public void run() {
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("get", Values.GET_INDEX_BOOK, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.what = Values.HOT_LOADING_SUCCESS;
					message.obj = changeToEnum.getData();
					handler.sendMessage(message);
					break;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.HOT_LOADING_FAIL;
					handler.sendMessage(message);
					break;
				}
			}

		}.start();
	}
	
	/**
	 * 加载接下来的页成功的动作
	 * @param tData
	 */
	private void LoadingNextSuccess(Data tData) {
		data = tData;
		//如果是刷新的，则先清空以前的数据再显示
		if(isRefreshed) {
			booksDesListAdapter.clear();
			booklist.clear();
			booksDesListAdapter.notifyDataSetChanged();
			isRefreshed = false;
			listFooter.setText("加载更多");
			listFooter.setEnabled(true);
		}
		if(whichPage == 1) {   //清空空书架
			booksDesListAdapter.clear();
			++ whichPage;
		}
		//如果为空，或返回的书小于20本则说明书全部加载
		if((data.getBook_list() == null) || (data.getBook_list().size() < 20)) {
			listFooter.setText("已全部加载");
			listFooter.setEnabled(false);
		} else {
			listFooter.setText("加载更多");
			listFooter.setEnabled(true);
		}
		int count = 0;
		for (BookDetail ib : data.getBook_list()) {
			booklist.add(ib);
			BooksDesListView booksDes = new BooksDesListView(ib.getBook_link(), ib.getBook_name(), ib.getAuthor(),
					ib.getLabel(), ib.getCurrent_available_count());
			booksDesListAdapter.add(booksDes);
			booksDesListAdapter.notifyDataSetChanged();
			++ count;
		}
		for(int i = count; i < Values.MIN_LINE_DISPLAY; ++i)   //不足四行的，要补气四行
    	{
    		BooksDesListView booksDes = new BooksDesListView("null", "null", "null","null", "null");
			booksDesListAdapter.add(booksDes);
			booksDesListAdapter.notifyDataSetChanged();
    	}
		list.setEnabled(true);
		new AnimationUtil().dismissLoadingDialog(loading);
	}

	/**
	 * 静态handler,防止内存泄漏错误
	 * @author chris
	 *
	 */
	 static class MyInnerHandler extends Handler {
	        WeakReference<TopHotShuyouTab> mActivity;
	
	        MyInnerHandler(TopHotShuyouTab mAct) {
	        	mActivity = new WeakReference<TopHotShuyouTab>(mAct);
	        }
	
	        @Override
	        public void handleMessage(Message msg) {
	        	TopHotShuyouTab theAct = mActivity.get();
	        	switch(msg.what) {
				case Values.HOT_LOADING_SUCCESS:
					LogHelper.i(TAG, "HOT_LOADING_SUCCESS");
					theAct.LoadingNextSuccess((Data)msg.obj);
					LogHelper.i(TAG, "HOT_LOADING_SUCCESS_END");
					break;
				case Values.HOT_LOADING_FAIL:
					Toast.makeText(theAct, "加载最热错误", Toast.LENGTH_SHORT).show();
					theAct.listFooter.setEnabled(true);
					theAct.listFooter.setText("加载更多");
				case Values.NETWORK_ERROR:
					Toast.makeText(theAct, "网络连接异常", Toast.LENGTH_SHORT).show();
					theAct.listFooter.setEnabled(true);
					theAct.listFooter.setText("加载更多");
					break;
				default:
					break;
	        	}
	        	LogHelper.i(TAG, "HOT_LOADING_SUCCESS_1");
	        	theAct.refreshList.onRefreshComplete();
	        	LogHelper.i(TAG, "HOT_LOADING_SUCCESS_2");
	        }
	 }
	 MyInnerHandler handler = new MyInnerHandler(this);

}
