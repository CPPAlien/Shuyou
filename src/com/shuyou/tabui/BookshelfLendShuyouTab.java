package com.shuyou.tabui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shuyou.R;
import com.shuyou.lib.BookshelfListView;
import com.shuyou.lib.BookshelfListViewAdapter;
import com.shuyou.lib.PullToRefreshListView;
import com.shuyou.lib.PullToRefreshBase.OnRefreshListener;
import com.shuyou.lib.internal.ChangeToEnum;
import com.shuyou.lib.internal.ChangeToEnum.EnumCode;
import com.shuyou.net.BookDetail;
import com.shuyou.net.Data;
import com.shuyou.net.UserInfo;
import com.shuyou.utils.AnimationUtil;
import com.shuyou.values.Share;
import com.shuyou.values.Values;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class BookshelfLendShuyouTab extends Activity{

	private final static String TAG = "BookshelfLendShuyouTab";
	
	private RelativeLayout loading = null;
	ArrayList<BookDetail> booklist;
	List<BookshelfListView> bookItemList;
	private PullToRefreshListView refreshList = null;
	private ListView list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shuyou_list_bookslend);
		
		loading = (RelativeLayout) this.findViewById(R.id.list_bookslend_loading_layout);
        new AnimationUtil().showLoadingDialog(loading);
		
		refreshList = (PullToRefreshListView)findViewById(R.id.list_bookshelflend);
		list = refreshList.getRefreshableView();
		refreshList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				getMyBookshelf("lend_book", "");   //刷新请求第一页
			}
			
		});
		list.setDivider(null);
		
		/*预先加载空书架界面*/
		List<BookshelfListView> tmpList = new ArrayList<BookshelfListView>();
		BookshelfListViewAdapter tmpAdapter = new BookshelfListViewAdapter(BookshelfLendShuyouTab.this, 
				booklist, tmpList, list, "lend");
		for(int i = 0; i < 4; ++i) {
			BookshelfListView bookItem = new BookshelfListView("null", "null", "null");
			tmpList.add(bookItem);
		}
		list.setAdapter(tmpAdapter);
		
		getMyBookshelf("lend_book", "");
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	/**
     * 得到我的书籍信息，已传书籍，借入书籍，借出书籍
     * @param books_type
     * @param last_time
     * @param sessionid
     * @param userid
     */
    private void getMyBookshelf(final String books_type, final String last_time) {

		new Thread() {

			@Override
			public void run() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("books_type", books_type);
				map.put("last_time", last_time);
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("get", Values.MY_BOOKSHELF_URL, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.what = Values.SUCCESS_TO_GETBOOKS;
					message.obj = changeToEnum.getData();
					handler.sendMessage(message);
					break ;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.FAIL_TO_GETBOOKS;
					handler.sendMessage(message);
					break;
				}
			}

		}.start();
	}
	
	/**
	 * 根据传回的书，把书加入list中
	 */
	private void AddBooksToShelf() {
		int booklistSize = booklist.size();
		/*得到三个排列的完整书架*/
		int j = 0;
		for(int i = 0; i + 2 < booklistSize; i = i + 3) {
			BookshelfListView bookItem = new BookshelfListView(booklist.get(0+i).getBook_link(), 
					 booklist.get(1+i).getBook_link(), booklist.get(2+i).getBook_link());
			bookItemList.add(bookItem);
			++ j;
		}
		/*把不足三本的排上书架*/
		if(1 == (booklistSize % 3)) { //如果多出一本
			BookshelfListView bookItem = new BookshelfListView(booklist.get(booklistSize - 1).getBook_link(), 
					 "null", "null");
			bookItemList.add(bookItem);
			++ j;
		} else if(2 == (booklistSize % 3)){                    //如果多出二本
			BookshelfListView bookItem = new BookshelfListView(booklist.get(booklistSize - 2).getBook_link(), 
					booklist.get(booklistSize - 1).getBook_link(), "null");
			bookItemList.add(bookItem);
			++ j;
		}
		for(int i = j; i < 4; ++i) {  //不足4行的，补足四行
			BookshelfListView bookItem = new BookshelfListView("null", "null", "null");
			bookItemList.add(bookItem);
		}
	}
	
	/**
	 * 成功后的动作
	 * @param tData
	 */
	private void LoadingSuccess(Data tData) {
		Data data = tData;
		booklist = data.getBook_list();
		if(booklist == null) {
			return ;
		}
		bookItemList = new ArrayList<BookshelfListView>();
		list = refreshList.getRefreshableView();
		AddBooksToShelf();
		
		BookshelfListViewAdapter bookshelfViewAdapter = new BookshelfListViewAdapter(BookshelfLendShuyouTab.this, 
				booklist, bookItemList, list, "lend");
		list.setDivider(null);
		list.setAdapter(bookshelfViewAdapter);
	}
	
	/**
	 * 静态handler,防止内存泄漏错误
	 * @author chris
	 *
	 */
	static class MyInnerHandler extends Handler {
        WeakReference<BookshelfLendShuyouTab> mActivity;

        MyInnerHandler(BookshelfLendShuyouTab mAct) {
        	mActivity = new WeakReference<BookshelfLendShuyouTab>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
        	BookshelfLendShuyouTab theAct = mActivity.get();
            switch (msg.what) {
			case Values.FAIL_TO_GETBOOKS:
				Toast.makeText(theAct, "获取信息失败", Toast.LENGTH_SHORT).show();
				break;
			case Values.SUCCESS_TO_GETBOOKS:
				theAct.LoadingSuccess((Data)msg.obj);
				break;
			case Values.NETWORK_ERROR:
				Toast.makeText(theAct, "网络异常", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
            new AnimationUtil().dismissLoadingDialog(theAct.loading);
			theAct.refreshList.onRefreshComplete();
	    }
	 }
	 MyInnerHandler handler = new MyInnerHandler(this);
}
