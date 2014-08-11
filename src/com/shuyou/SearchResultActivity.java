package com.shuyou;

import com.shuyou.lib.BooksDesListView;
import com.shuyou.lib.BooksDesListViewAdapter;
import com.shuyou.lib.internal.ChangeToEnum;
import com.shuyou.lib.internal.ChangeToEnum.EnumCode;
import com.shuyou.net.BookDetail;
import com.shuyou.net.Data;
import com.shuyou.net.UserInfo;
import com.shuyou.utils.AnimationUtil;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultActivity extends Activity {
	private String TAG = "SearchResultActivity";

    private Handler mHandler;
    private RelativeLayout loading;
    
    private ListView list = null;
    private BooksDesListViewAdapter booksDesListAdapter = null;
    private ArrayList<BookDetail> booklist = new ArrayList<BookDetail>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_result);
        
        InitListDisplay();
        
        mHandler = new MyHandler(this);
        loading = (RelativeLayout) this.findViewById(R.id.searchresult_loading_layout);
        new AnimationUtil().showLoadingDialog(loading);
        
        Intent i = getIntent();
        String type = i.getStringExtra("type");
        if(type.equals("SearchActivity")) {
        	String keyword = i.getStringExtra("keyword");
        	new SearchThread(keyword).start();
        } else if(type.equals("AllLabelFragment")) {
        	String labelid = i.getStringExtra("labelid");
        	new SearchByLabelThread(labelid,"1").start();
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
    
    /*初始化listview的显示*/
    private void InitListDisplay()
    {
    	List<BooksDesListView> booksDesList = new ArrayList<BooksDesListView>();
		for(int i = 0; i < 4; ++ i) {
			BooksDesListView tmpBooksDes = new BooksDesListView("null", "null", "null","null","null");
			booksDesList.add(tmpBooksDes);
		}
		list = (ListView)findViewById(R.id.searchresult_listview);
		booksDesListAdapter = new BooksDesListViewAdapter(SearchResultActivity.this, 
				booksDesList, list);
		list.setAdapter(booksDesListAdapter);
		
		list.setEnabled(false);
		
		list.setOnItemClickListener(new OnItemClickListener(){
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
				try {
					Intent i = new Intent();
					i.putExtra("type", "ViewBookDetail");
					i.putExtra("bookIsbn", booklist.get(position).getIsbn());
					i.putExtra("bookName", booklist.get(position).getBook_name());
					i.putExtra("bookAuthor", booklist.get(position).getAuthor());
					i.putExtra("bookLabel", booklist.get(position).getLabel());
					i.setClass(SearchResultActivity.this, BookInfoActivity.class);
					startActivity(i);
				} catch(IndexOutOfBoundsException e) {
					return ;
				}
			}
		});
		
		findViewById(R.id.searchresult_back).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
        });
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    class SearchThread extends Thread{

        private String keyword;
        
        public SearchThread(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public void run() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("query", keyword);
            map.put("sessionid", UserInfo.sessionid);
            map.put("userid", UserInfo.userid);
            ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
			EnumCode code = changeToEnum.getUrl("get", Values.SEARCH_URL, map);
			Message message = new Message();
			switch(code) {
			case OPERATE_SUCCESS:
				message.obj = changeToEnum.getData();
				message.what = Values.SUCCESS_TO_SEARCH;
                mHandler.sendMessage(message);
				break;
			case NETWORK_ERROR:
				message.what = Values.NETWORK_ERROR;
                mHandler.sendMessage(message);
				break;
			default:
				message.what = Values.FAIL_TO_SEARCH;
	            mHandler.sendMessage(message);
				break;
			}
        }
        
    }
    
    class SearchByLabelThread extends Thread{

        private String labelid;
        private String page;
        
        public SearchByLabelThread(String labelid, String page) {
            this.labelid = labelid;
            this.page = page;
        }

        @Override
        public void run() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("labelid", labelid);
            map.put("page", page);
            map.put("sessionid", UserInfo.sessionid);
            map.put("userid", UserInfo.userid);
            ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
			EnumCode code = changeToEnum.getUrl("get", Values.SEARCH_BY_LABEL_URL, map);
			Message message = new Message();
			switch(code) {
			case OPERATE_SUCCESS:
				message.obj = changeToEnum.getData();
				message.what = Values.SUCCESS_TO_SEARCH;
                mHandler.sendMessage(message);
				break;
			case NETWORK_ERROR:
				message.what = Values.NETWORK_ERROR;
                mHandler.sendMessage(message);
				break;
			default:
				message.what = Values.FAIL_TO_SEARCH;
	            mHandler.sendMessage(message);
				break;
			}
        }
        
    }
    
    private void SearchSuccess(Data data)
    {
		if(null == data.getBook_list())
		{
			return ;
		}
    	booksDesListAdapter.clear();
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
    	//如果未查到内容，则结束此activity
    	if (0 == count) {
    		Toast.makeText(SearchResultActivity.this, "查无此书", Toast.LENGTH_SHORT).show();
    		finish();
    	}
    	list.setEnabled(true);
    }
    
    static class MyHandler extends Handler{

        WeakReference<SearchResultActivity> mActivity;
        
        public MyHandler(SearchResultActivity layout) {
        	mActivity = new WeakReference<SearchResultActivity>(layout);
        }

        @Override
        public void handleMessage(Message msg) {
            SearchResultActivity theAct = mActivity.get();
            if(theAct == null)
            {
                return;
            }
            switch (msg.what) {
                case Values.SUCCESS_TO_SEARCH:
                    Data data = (Data)msg.obj;
                    theAct.SearchSuccess(data);
                    break;
                case Values.FAIL_TO_SEARCH:
                	Toast.makeText(theAct, "搜索失败", Toast.LENGTH_SHORT).show();
                	theAct.finish();
                	break;
                case Values.NETWORK_ERROR:
                	Toast.makeText(theAct, "网络连接异常", Toast.LENGTH_SHORT).show();
                	theAct.finish();
                	break;
                default:
                    break;
            }
            new AnimationUtil().dismissLoadingDialog(theAct.loading);
        }
        
    }
    
}
