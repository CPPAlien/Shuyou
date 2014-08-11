package com.shuyou;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.shuyou.lib.AsyncImageLoader;
import com.shuyou.lib.AsyncImageLoader.ImageCallback;
import com.shuyou.lib.internal.ChangeToEnum;
import com.shuyou.lib.internal.ChangeToEnum.EnumCode;
import com.shuyou.net.BookDetail;
import com.shuyou.net.Data;
import com.shuyou.net.ServerInfo;
import com.shuyou.net.UserInfo;
import com.shuyou.utils.FileUtils;
import com.shuyou.values.Values;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class BookInfoActivity extends Activity{	
	String isbn = null;
	String bookName = null;
	String type = null;
	ServerInfo si = null;
	private AsyncImageLoader asyncImageLoader;
	private TextView textBookName;
	private TextView textPublicDate;
	private TextView textAuthor;
	private TextView textLabel;
	private TextView textPublisher;
	private TextView textZancount;
	private TextView textProfile;
	private RatingBar scoreBar;
	private TextView textScoreDec;
	private TextView textScoreFra;
	private ImageView iv;
	
	private String TAG = "BookInfoActivity";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bookinfo);
		asyncImageLoader = new AsyncImageLoader(this);
		initView();
		Intent i = getIntent();
		type = i.getStringExtra("type");
			
		//返回按钮
		findViewById(R.id.bookinfo_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		if(type.equals("ViewBookDetail")) {
			DealWithViewBookDetail(i);
		}
		else if(type.equals("UploadViewDetail")) {
			final BookDetail bd = (BookDetail)i.getSerializableExtra("bookdetail");
			TextView textTitle = (TextView)findViewById(R.id.bookinfo_title);
			textTitle.setText("本书详情");
			TextView buttonText = (TextView)findViewById(R.id.bookinfo_confirm_button_text);
			buttonText.setText("确认上传");
			findViewById(R.id.bookinfo_confirm_button).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TextView tmpText = (TextView)findViewById(R.id.bookinfo_comment);
					uploadBook(bd.getIsbn(), tmpText.getText().toString());
					findViewById(R.id.bookinfo_confirm_button).setEnabled(false);
					findViewById(R.id.bookinfo_confirm_button_progressbar).setVisibility(View.VISIBLE);
				}
				
			});
			showBookInfo(bd);
			findViewById(R.id.bookinfo_comment_erea).setVisibility(View.VISIBLE);
			findViewById(R.id.bookinfo_comment_erea).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					TextView tmpText = (TextView)findViewById(R.id.bookinfo_comment);
					Intent i = new Intent(BookInfoActivity.this, EditCommentActivity.class);
					i.putExtra("type", "BookInfoActivity");
					i.putExtra("content", tmpText.getText().toString());
					i.putExtra("bookId", bd.getBookid());
					
					startActivityForResult(i, 0);
				}
			
			});
			
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
	
	private void DealWithViewBookDetail(Intent i) {
		isbn = i.getStringExtra("bookIsbn");
		bookName = i.getStringExtra("bookName");
		
		/*根据已有的一些书籍信息，初始化显示*/
		TextView textBookName = (TextView)findViewById(R.id.bookinfo_bookname);
		textBookName.setText(i.getStringExtra("bookName"));
		TextView textAuthor = (TextView)findViewById(R.id.bookinfo_author);
		textAuthor.setText(i.getStringExtra("bookAuthor"));
		TextView textLabel = (TextView)findViewById(R.id.bookinfo_label);
		textLabel.setText(i.getStringExtra("bookLabel"));
		
		//借阅本书按钮
		findViewById(R.id.bookinfo_confirm_button).setEnabled(true);
		findViewById(R.id.bookinfo_confirm_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(BookInfoActivity.this, BookOwnersActivity.class);
				i.putExtra("isbn", isbn);
				i.putExtra("bookName", bookName);
				startActivity(i);
			}
		});
		
		BookDetail bd = FileUtils.readDetailBookInfo(isbn);
		if(bd == null){
			startThread();
		}else{
			showBookInfo(bd);
		}
	}
	
	private void startThread(){
		new Thread()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				map.put("isbn", isbn);
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("get", Values.GET_BOOK_DETAIL_URL, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.obj = changeToEnum.getData();
					message.what = Values.LOAD_BOOK_INFO_SUCCESS;
					handler.sendMessage(message);
					break;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.LOAD_BOOK_INFO_FAIL;
					handler.sendMessage(message);
					break;
				}	
			}
		}.start();
	}
	
	private void uploadBook(final String isbn, final String comment) {
		new Thread() {

			@Override
			public void run() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("isbn", isbn);
				map.put("comment", comment);
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("post", Values.UPLOAD_BOOK_URL, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.what = Values.SUCCESS_TO_UPLOAD;
					message.obj = changeToEnum.getData().getBookDetail();
					handler.sendMessage(message);
					break ;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				case BOOK_ON_LINE:
					message.what = Values.FAIL_TO_UPLOAD;
					message.obj = ",您已经上传过该书";
					handler.sendMessage(message);
					break;
				case ADD_TOO_MANY:
					message.what = Values.FAIL_TO_UPLOAD;
					message.obj = ",最多能上传60本书";
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.FAIL_TO_UPLOAD;
					message.obj = "";
					handler.sendMessage(message);
					break;
				}
			}

		}.start();
	}
	
	private void initView(){
		textBookName = (TextView)findViewById(R.id.bookinfo_bookname);
		textPublicDate = (TextView)findViewById(R.id.bookinfo_publishdate);
		textAuthor = (TextView)findViewById(R.id.bookinfo_author);
		textLabel = (TextView)findViewById(R.id.bookinfo_label);
		textPublisher = (TextView)findViewById(R.id.bookinfo_publisher);
		textZancount = (TextView)findViewById(R.id.bookinfo_zancount);
		textProfile = (TextView)findViewById(R.id.bookinfo_profile);
		scoreBar = (RatingBar)findViewById(R.id.bookinfo_bookscore_ratingbar);
		textScoreDec = (TextView)findViewById(R.id.bookinfo_bookscore_dec);
		textScoreFra = (TextView)findViewById(R.id.bookinfo_bookscore_fra);
		iv = (ImageView)findViewById(R.id.bookinfo_booklogo);
	}
	
	private void showBookInfo(BookDetail bookDetail) {

		textBookName.setText(bookDetail.getBook_name());		
		textPublicDate.setText(bookDetail.getPublish_date());		
		textAuthor.setText(bookDetail.getAuthor());		
		textLabel.setText(bookDetail.getLabel());		
		textPublisher.setText(bookDetail.getPublisher());		
		textZancount.setText(bookDetail.getZan_count());		
		textProfile.setText(bookDetail.getProfile());
		//豆瓣分数的显示
		float score = (float) (Float.parseFloat(bookDetail.getScore()) / 2.0);
		scoreBar.setRating(score);
		textScoreDec.setText(bookDetail.getScore().substring(0,1));
		textScoreFra.setText(bookDetail.getScore().substring(1,3));
		//异步显示图书logo
		final String logoUrl = bookDetail.getBook_link();
		Drawable cachedImage = asyncImageLoader.loadDrawable(logoUrl, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				if(imageDrawable == null){
					return;
				};
				iv.setImageDrawable(imageDrawable);
			}
		});
		if (cachedImage == null) {
			iv.setImageResource(R.drawable.ic_launcher);
		}else{
			iv.setImageDrawable(cachedImage);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == RESULT_OK){
			String str = data.getExtras().getString("content");
			TextView tmpText = (TextView)findViewById(R.id.bookinfo_comment);
			tmpText.setText(str);
		}
	}
	
	static class MyInnerHandler extends Handler {
        WeakReference<BookInfoActivity> mActivity;

        MyInnerHandler(BookInfoActivity mAct) {
        	mActivity = new WeakReference<BookInfoActivity>(mAct);
        }
        
		@Override
		public void handleMessage(Message msg) {
			BookInfoActivity theAct = mActivity.get();
			switch(msg.what) {
				case Values.LOAD_BOOK_INFO_SUCCESS:
					Data data = (Data)msg.obj;
					FileUtils.writeDetailBookInfo(theAct, data.getBookDetail());
					theAct.showBookInfo(data.getBookDetail());
					break;
				case Values.LOAD_BOOK_INFO_FAIL:
					theAct.findViewById(R.id.bookinfo_confirm_button).setEnabled(false);
					Toast.makeText(theAct, "获取书记详情失败", Toast.LENGTH_SHORT).show();
					break;
				case Values.SUCCESS_TO_UPLOAD:
					Toast.makeText(theAct, "上传成功", Toast.LENGTH_SHORT).show();
					theAct.finish();
					break;
				case Values.FAIL_TO_UPLOAD:
					Toast.makeText(theAct, "上传失败" + msg.obj, Toast.LENGTH_SHORT).show();
					break;
				case Values.NETWORK_ERROR:
					Toast.makeText(theAct, "网络连接超时", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
			theAct.findViewById(R.id.bookinfo_confirm_button).setEnabled(true);
			theAct.findViewById(R.id.bookinfo_confirm_button_progressbar).setVisibility(View.INVISIBLE);
		}
	}
	MyInnerHandler handler = new MyInnerHandler(this);
	
	
}
