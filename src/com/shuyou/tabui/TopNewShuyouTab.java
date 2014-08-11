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
import com.shuyou.lib.PullToRefreshBase.OnRefreshListener;
import com.shuyou.lib.internal.ChangeToEnum;
import com.shuyou.lib.internal.ChangeToEnum.EnumCode;
import com.shuyou.lib.PullToRefreshListView;
import com.shuyou.net.BookDetail;
import com.shuyou.net.Data;
import com.shuyou.net.UserInfo;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TopNewShuyouTab extends Activity{
	private String TAG = "TopNewShuyouTab";
	private Intent i = null;
	private Data   data = null;
	private ArrayList<BookDetail> booklist = null;
	private TextView listFooter = null;
	
	
	private PullToRefreshListView refreshList = null;
	private ListView list = null;
	private BooksDesListViewAdapter booksDesListAdapter = null;
	private boolean isRefreshed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shuyou_tab_topnew);
		
		/*排列按最新排序的书籍信息列表*/
		i = getIntent();
		data = (Data) i.getSerializableExtra("bookinfo");
		refreshList = (PullToRefreshListView)findViewById(R.id.topnew_list);
		list = refreshList.getRefreshableView();
		
		refreshList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				isRefreshed = true;
				getIndexBooks("-1");   //刷新请求第一页
			}
			
		});
		
		if(data != null){
			DisplayContent();
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
	 * 把获取到的内容在界面中显示出来
	 */
	private void DisplayContent(){
		//创建一个List集合， List集合元素是BooksDesListView
		List<BooksDesListView> booksDesList = new ArrayList<BooksDesListView>();
		if (data != null) {
			booklist = data.getBook_list();
			for (BookDetail ib : booklist) {
				BooksDesListView booksDes = new BooksDesListView(ib.getBook_link(), ib.getBook_name(), ib.getAuthor(),
						ib.getLabel(), ib.getCurrent_available_count());
				booksDesList.add(booksDes);
			}
		}
        
		booksDesListAdapter = new BooksDesListViewAdapter(TopNewShuyouTab.this, 
				                                    booksDesList, list);
		
		listFooter = new TextView(TopNewShuyouTab.this);
		/*设置低步加载更多风格*/
		listFooter.setBackgroundDrawable(getResources().getDrawable(R.drawable.shuyou_listfooter_bg));
		listFooter.setShadowLayer((float)0.5, (float)1, (float)1, R.color.black);
		listFooter.setPadding(30, 15, 0, 10);
		listFooter.setGravity(Gravity.CENTER);
		listFooter.setText("加载更多");
		listFooter.setTextSize((float) 20.0);
		listFooter.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(MainActivity.filterAnimation.isDisplayed()) {
					MainActivity.filterAnimation.SlidingHide();
					return ;
				}
				LogHelper.v("LAST_TIME", "last_time" + data.getLast_time());
				listFooter.setText("正在加载...");
				listFooter.setEnabled(false);
				getIndexBooks(data.getLast_time());
				
			}
			
		});
		if(booklist.size() < 20) {
			listFooter.setText("已全部加载");
			listFooter.setEnabled(false);
		}
		
		list.addFooterView(listFooter);
		
		list.setDivider(null);
		list.setAdapter(booksDesListAdapter);
		
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(MainActivity.filterAnimation.isDisplayed()) {
					MainActivity.filterAnimation.SlidingHide();
					return ;
				}
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.putExtra("type", "ViewBookDetail");
				i.putExtra("bookIsbn", booklist.get(position).getIsbn());
				i.putExtra("bookName", booklist.get(position).getBook_name());
				i.putExtra("bookAuthor", booklist.get(position).getAuthor());
				i.putExtra("bookLabel", booklist.get(position).getLabel());
				i.setClass(TopNewShuyouTab.this, BookInfoActivity.class);
				startActivity(i);
			}
		});
	}
	
	/**
	 * 加载更多最新图书，每次加载20本
	 * @param last_time
	 */
	private void getIndexBooks(final String last_time) {
		final Map<String, String> map = new HashMap<String, String>();
		map.put("sessionid", UserInfo.sessionid);
		map.put("books_type", "new");
		map.put("userid", UserInfo.userid);
		map.put("last_time", last_time);
		
		new Thread() {

			@Override
			public void run() {
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("get", Values.GET_INDEX_BOOK, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.what = Values.NEW_NEXT_LOADING_SUCCESS;
					message.obj = changeToEnum.getData();
					handler.sendMessage(message);
					break;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.NEW_LOADING_FAIL;
					handler.sendMessage(message);
					break;
				}
			}

		}.start();
	}

	 /**
	  * 成功加载后执行的动作
	  * @param tData
	  */
	 private void LoadingSuccess(Data tData) {
			data = tData;
			//如果是刷新的，则先情况以前的数据再显示
			if(isRefreshed) {
				booksDesListAdapter.clear();
				booklist.clear();
				booksDesListAdapter.notifyDataSetChanged();
				isRefreshed = false;
				listFooter.setText("加载更多");
				listFooter.setEnabled(true);
				
			}
			/*如果为空，或返回书数小于20本，则算是全部加载*/
			if ((data.getBook_list() == null) || (data.getBook_list().size() < 20)) {
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
	 }
	 
	 /**
	 * 静态handler,防止内存泄漏错误
	 * @author chris
	 *
	 */
	 static class MyInnerHandler extends Handler {
        WeakReference<TopNewShuyouTab> mActivity;

        MyInnerHandler(TopNewShuyouTab mAct) {
        	mActivity = new WeakReference<TopNewShuyouTab>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
        	TopNewShuyouTab theAct = mActivity.get();
            switch (msg.what) {
            case Values.NEW_NEXT_LOADING_SUCCESS:
				theAct.LoadingSuccess((Data)msg.obj);
				break;
			case Values.NETWORK_ERROR:
				Toast.makeText(theAct, "网络连接异常", Toast.LENGTH_SHORT).show();
				theAct.listFooter.setEnabled(true);
				theAct.listFooter.setText("加载更多");
				break;
			case Values.NEW_LOADING_FAIL:
				Toast.makeText(theAct, "加载最新推荐错误", Toast.LENGTH_SHORT).show();
				theAct.listFooter.setEnabled(true);
				theAct.listFooter.setText("加载更多");
			default:
				break;
			}
            theAct.refreshList.onRefreshComplete();
	    }
	 }
	 MyInnerHandler handler = new MyInnerHandler(this);
}
