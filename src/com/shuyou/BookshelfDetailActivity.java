package com.shuyou;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.shuyou.lib.AsyncImageLoader;
import com.shuyou.lib.AsyncImageLoader.ImageCallback;
import com.shuyou.lib.internal.ChangeToEnum;
import com.shuyou.lib.internal.ChangeToEnum.EnumCode;
import com.shuyou.net.BookDetail;
import com.shuyou.net.ServerInfo;
import com.shuyou.net.UserInfo;
import com.shuyou.tabui.BookshelfUploadShuyouTab;
import com.shuyou.utils.AnimationUtil;
import com.shuyou.values.Values;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BookshelfDetailActivity extends Activity{

	private final static String TAG = "BookshelfDetailActivity";
	
	ServerInfo si;
	String type;
	String bookName = null;
	String bookId   = null;
	String codeSecret = null;
	String bookOwnerName = null;
	String userId = null;
	Dialog dialogConfirm = null;
	
	private RelativeLayout loading = null;

	private ImageView iv;

	private AsyncImageLoader asyncImageLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bookshelfdetail);
		
		loading = (RelativeLayout) this.findViewById(R.id.bookshelfdetail_loading_layout);
        new AnimationUtil().showLoadingDialog(loading);
        
		Intent i = getIntent();
		bookName = i.getStringExtra("bookName");
		bookId   = i.getStringExtra("bookId");
		type     = i.getStringExtra("type");
		TextView textBookName = (TextView)findViewById(R.id.bookshelfdetail_bookname);
		textBookName.setText(bookName);
		asyncImageLoader = new AsyncImageLoader(this);
		findViewById(R.id.bookshelfdetail_back).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		RelativeLayout dialogConfirmView = (RelativeLayout)getLayoutInflater()
				.inflate(R.layout.shuyou_dialog_lendborrow_confirm, null);
		dialogConfirm = new Dialog(this, R.style.ShuyouDialog);
		dialogConfirm.setContentView(dialogConfirmView);
		
		DealThreeSituations();
		
		getBookshelfDetail(type, bookId);
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
	 * 对三个不同activity传来的东西进行个性化处理
	 */
	private void DealThreeSituations() {
		/*未加载成功前，按钮不可用*/
		findViewById(R.id.bookshelfdetail_toprightbutton).setEnabled(false);
		findViewById(R.id.bookshelfdetail_bottom_button).setEnabled(false);
		if(type.equals("upload")) {
			/*下架操作*/
			TextView tmpText = (TextView)findViewById(R.id.bookshelfdetail_toprighttext);
			tmpText.setText("下架");
			findViewById(R.id.bookshelfdetail_toprightbutton).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					findViewById(R.id.bookshelfdetail_toprightbutton).setEnabled(false);
					dialogConfirm.show();
					TextView tv = (TextView)dialogConfirm.findViewById(R.id.dialog_lendborrow_text);
					tv.setText("确认下架该书？");
					dialogConfirm.findViewById(R.id.dialog_lendborrow_ok).setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							AfterXiaJia();
						}
					});
					dialogConfirm.findViewById(R.id.dialog_lendborrow_cancel).setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialogConfirm.dismiss();
							findViewById(R.id.bookshelfdetail_toprightbutton).setEnabled(true);
						}
					});
					
				}
				
			});
			/*查看借出二维码*/
			tmpText = (TextView)findViewById(R.id.bookshelfdetail_bottom_button_name);
			tmpText.setText("借出二维码");
			findViewById(R.id.bookshelfdetail_bottom_button).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(BookshelfDetailActivity.this, LendScanCodeActivity.class);
					i.putExtra("bookName", bookName);
					i.putExtra("code", codeSecret);
					startActivity(i);
				}
				
			});
			/*只有已传书籍的详情可以修改备注*/
			findViewById(R.id.bookshelfdetail_array_image).setVisibility(View.VISIBLE);
			findViewById(R.id.bookshelfdetail_comment_text_area).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TextView tmpText = (TextView)findViewById(R.id.bookshelfdetail_comment);
					Intent i = new Intent(BookshelfDetailActivity.this, EditCommentActivity.class);
					i.putExtra("type", "BookshelfDetailActivity");
					i.putExtra("content", tmpText.getText().toString());
					i.putExtra("bookId", bookId);
					startActivityForResult(i, 0);
				}
			});
			findViewById(R.id.bookshelfdetail_array_image).setEnabled(false);
		} else if(type.equals("lend")) {
			findViewById(R.id.bookshelfdetail_toprightbutton).setVisibility(View.GONE); //借出书籍没有右上角按钮
			findViewById(R.id.bookshelfdetail_erweima_logo).setVisibility(View.GONE);   //借出书籍没有二维码图标
			/*借出去的书可以查看书的主人*/
			TextView tmpText = (TextView)findViewById(R.id.bookshelfdetail_bottom_button_name);
			tmpText.setText("查看借书人");
			findViewById(R.id.bookshelfdetail_bottom_button).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(BookshelfDetailActivity.this, UserInfoActivity.class);
					i.putExtra("userid", userId);
					startActivity(i);
				}
				
			});
		} else if(type.equals("borrow")) {
			/*借入书籍的赞操作*/
			TextView tmpText = (TextView)findViewById(R.id.bookshelfdetail_toprighttext);
			tmpText.setText("赞");
			
			findViewById(R.id.bookshelfdetail_toprightbutton).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					/*不让用户重复点赞*/
					findViewById(R.id.bookshelfdetail_toprightbutton).setEnabled(false);
					AfterZan();
				}
				
			});
			/*借入书的详情可显示书主*/
			findViewById(R.id.bookshelfdetail_owner_position).setVisibility(View.VISIBLE); 
			/*借入书可以查看换书的二维码*/
			tmpText = (TextView)findViewById(R.id.bookshelfdetail_bottom_button_name);
			tmpText.setText("还书二维码");
			findViewById(R.id.bookshelfdetail_bottom_button).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(BookshelfDetailActivity.this, ReturnScanCodeActivity.class);
					i.putExtra("bookName", bookName);
					i.putExtra("bookOwnerName", bookOwnerName);
					i.putExtra("userid", userId);
					i.putExtra("code", codeSecret);
					startActivity(i);
				}
				
			});
			findViewById(R.id.bookshelfdetail_bottom_button).setEnabled(false); //只有在加载了code_secret后才能跳转
		}
	}
	
	/**
	 * 用户点击“下架”之后的操作
	 */
	private void AfterXiaJia() {
		//开启线程，向服务器传送下架该书信息
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				map.put("bookid", bookId);
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("post", Values.XIAJIA_API, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.what = Values.XIAJIA_SUCCESS;
					message.obj = changeToEnum.getData();
					handler.sendMessage(message);
					break ;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.XIAJIA_FAILED;
					handler.sendMessage(message);
					break;
				}
			}
		}.start();
	}
	/**
	 * 用户点击”赞“之后的操作
	 */
	private void AfterZan() {
		//开启线程，向服务器传送已赞信息
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				map.put("bookid", bookId);
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("post", Values.ZAN_API, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.what = Values.ZAN_SUCCESS;
					message.obj = changeToEnum.getData();
					handler.sendMessage(message);
					break ;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				case ZAN_ALREADY:
					message.what = Values.ZAN_ALREADY;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.ZAN_FAILED;
					handler.sendMessage(message);
					break;
				}
			}
		}.start();
	}
	/**
	 * 赞成功后的动作
	 */
	private void ZanSuccess() {
		TextView tmpZanText = (TextView)findViewById(R.id.bookshelfdetail_zancount);
		String tmpZanString = tmpZanText.getText().toString();
		int tmpZanInt = Integer.parseInt(tmpZanString);
		++ tmpZanInt;
		tmpZanText.setText(tmpZanInt + "");
		TextView tmpText = (TextView)findViewById(R.id.bookshelfdetail_toprighttext);
		tmpText.setText("已赞");
	}
	/**
	 * 得到书的详情
	 * @param type
	 * @param bookid
	 * @param sessionid
	 * @param platform_userid
	 */
	private void getBookshelfDetail(final String type, final String bookid) {
		new Thread() {

			@Override
			public void run() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("type", type);
				map.put("bookid", bookid);
				map.put("sessionid", UserInfo.sessionid);
				map.put("userid", UserInfo.userid);
				ChangeToEnum changeToEnum = new ChangeToEnum(TAG);
				EnumCode code = changeToEnum.getUrl("get", Values.BOOKSHELF_DETAIL_URL, map);
				Message message = new Message();
				switch(code) {
				case OPERATE_SUCCESS:
					message.what = Values.SUCCESS_TO_GETDETAIL;
					message.obj = changeToEnum.getData().getBookDetail();
					handler.sendMessage(message);
					break ;
				case NETWORK_ERROR:
					message.what = Values.NETWORK_ERROR;
					handler.sendMessage(message);
					break;
				default:
					message.what = Values.FAIL_TO_GETDETAIL;
					handler.sendMessage(message);
					break;
				}
			}

		}.start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == RESULT_OK){
			String str = data.getExtras().getString("content");
			TextView tmpText = (TextView)findViewById(R.id.bookshelfdetail_comment);
			tmpText.setText(str);
		}
	}
	
	/**
	 * 加载详情成功后
	 * @param tData
	 */
	private void LoadingSuccess(BookDetail tBookDetail) {
		BookDetail bookDetail = tBookDetail;
		TextView tmpText = null;
		/*上传的书有借出二维码，借入的书有还书二维码*/
		if(type.equals("upload")) {
			codeSecret = bookDetail.getCode_secret();
		} else if(type.equals("borrow")) {
			tmpText = (TextView)findViewById(R.id.bookshelfdetail_owner);
			tmpText.setVisibility(View.VISIBLE);
			bookOwnerName = bookDetail.getNick_name();
			tmpText.setText(bookOwnerName);
			codeSecret = bookDetail.getCode_secret();
			
			String isAlreadyZan = bookDetail.getAready_zan();
			tmpText = (TextView)findViewById(R.id.bookshelfdetail_toprighttext);
			/*根据传回信息判断该用户是否已赞该书*/
			if(isAlreadyZan.equals("1")) {
				tmpText.setText("已赞");
				findViewById(R.id.bookshelfdetail_toprightbutton).setEnabled(false);
			}
			userId = tBookDetail.getUserid();
		} else if(type.equals("lend")) {
			userId = tBookDetail.getBorrow_userid();
		}
		tmpText = (TextView)findViewById(R.id.bookshelfdetail_publishdate);
		tmpText.setText(bookDetail.getPublish_date());
		tmpText = (TextView)findViewById(R.id.bookshelfdetail_author);
		tmpText.setText(bookDetail.getAuthor());
		tmpText = (TextView)findViewById(R.id.bookshelfdetail_label);
		tmpText.setText(bookDetail.getLabel());
		tmpText = (TextView)findViewById(R.id.bookshelfdetail_publisher);
		tmpText.setText(bookDetail.getPublisher());
		tmpText = (TextView)findViewById(R.id.bookshelfdetail_zancount);
		tmpText.setText(bookDetail.getZan_count());
		tmpText = (TextView)findViewById(R.id.bookshelfdetail_comment);
		tmpText.setText(bookDetail.getComment());
		tmpText = (TextView)findViewById(R.id.bookshelfdetail_profile);
		tmpText.setText(bookDetail.getProfile());
		//豆瓣分数的显示
		float score = (float) (Float.parseFloat(bookDetail.getScore()) / 2.0);
		RatingBar scoreBar = (RatingBar)findViewById(R.id.bookshelfdetail_bookscore_ratingbar);
		scoreBar.setRating(score);
		TextView textScoreDec = (TextView)findViewById(R.id.bookshelfdetail_bookscore_dec);
		textScoreDec.setText(bookDetail.getScore().substring(0,1));
		TextView textScoreFra = (TextView)findViewById(R.id.bookshelfdetail_bookscore_fra);
		textScoreFra.setText(bookDetail.getScore().substring(1,3));
		
		iv = (ImageView) findViewById(R.id.bookshelfdetail_booklogo);
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
		
		findViewById(R.id.bookshelfdetail_array_image).setEnabled(true); //只有加载成功了备注，才能就行修改
		findViewById(R.id.bookshelfdetail_bottom_button).setEnabled(true);
	}
	
	/**
	 * 静态handler,防止内存泄漏错误
	 * @author chris
	 *
	 */
	static class MyInnerHandler extends Handler {
        WeakReference<BookshelfDetailActivity> mActivity;

        MyInnerHandler(BookshelfDetailActivity mAct) {
        	mActivity = new WeakReference<BookshelfDetailActivity>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
        	BookshelfDetailActivity theAct = mActivity.get();
        	switch (msg.what) {
			case Values.FAIL_TO_GETDETAIL:
				Toast.makeText(theAct, "获取详情失败", Toast.LENGTH_SHORT).show();
				break;
			case Values.SUCCESS_TO_GETDETAIL:
				theAct.LoadingSuccess((BookDetail)msg.obj);
				theAct.findViewById(R.id.bookshelfdetail_toprightbutton).setEnabled(true);
				theAct.findViewById(R.id.bookshelfdetail_bottom_button).setEnabled(true);
				break;
			case Values.NETWORK_ERROR:
				Toast.makeText(theAct, "网络异常", Toast.LENGTH_SHORT).show();
				break;
			case Values.ZAN_SUCCESS:
				theAct.ZanSuccess();
				break;
			case Values.ZAN_FAILED:
				Toast.makeText(theAct, "操作失败", Toast.LENGTH_SHORT).show();
				theAct.findViewById(R.id.bookshelfdetail_toprightbutton).setEnabled(true);
				break;
			case Values.ZAN_ALREADY:
				Toast.makeText(theAct, "您已赞过该书", Toast.LENGTH_SHORT).show();
				TextView tmpText = (TextView)theAct.findViewById(R.id.bookshelfdetail_toprighttext);
				tmpText.setText("已赞");
				break;
			case Values.XIAJIA_SUCCESS:
				Toast.makeText(theAct, "下架成功", Toast.LENGTH_SHORT).show();
				theAct.dialogConfirm.dismiss();
				Intent i = new Intent(theAct, BookshelfUploadShuyouTab.class);
				i.putExtra("bookId", theAct.bookId);
				theAct.setResult(RESULT_OK, i);
				theAct.finish();
				break;
			case Values.XIAJIA_FAILED:
				Toast.makeText(theAct, "下架失败", Toast.LENGTH_SHORT).show();
				theAct.findViewById(R.id.bookshelfdetail_toprightbutton).setEnabled(true);
				break;
			default:
				break;
			}
            new AnimationUtil().dismissLoadingDialog(theAct.loading);
	    }
	 }
	 MyInnerHandler handler = new MyInnerHandler(this);

}
