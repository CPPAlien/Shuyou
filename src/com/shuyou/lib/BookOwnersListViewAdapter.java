package com.shuyou.lib;

import java.util.List;

import com.shuyou.R;
import com.shuyou.lib.AsyncImageLoader.ImageCallback;
import com.shuyou.values.Values;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BookOwnersListViewAdapter extends ArrayAdapter<BookOwnersListView> {

	    private ListView listView;
	    private AsyncImageLoader asyncImageLoader;
	    private String bookName;

	    public BookOwnersListViewAdapter(Activity activity, List<BookOwnersListView> bookOwnersListView, ListView listView, String bookName) {
	        super(activity, 0, bookOwnersListView);
	        this.listView = listView;
	        asyncImageLoader = new AsyncImageLoader(activity);
	        this.bookName = bookName;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        Activity activity = (Activity) getContext();

	        // Inflate the views from XML
	        View rowView = convertView;
	        BookOwnersViewCache viewCache;
	        if (rowView == null) {
	            LayoutInflater inflater = activity.getLayoutInflater();
	            rowView = inflater.inflate(R.layout.shuyou_listview_bookowners, null);
	            viewCache = new BookOwnersViewCache(rowView);
	            rowView.setTag(viewCache);
	        } else {
	            viewCache = (BookOwnersViewCache) rowView.getTag();
	        }
	        BookOwnersListView bookOwnersView = getItem(position);

	        // Load the image and set it on the ImageView
	        String imageUrl = bookOwnersView.getImageUrl();
	        ImageView imageView = viewCache.getLogoImageView();
	        imageView.setTag(imageUrl);
	        Drawable cachedImage = null;
	        final BookOwnersViewCache tmpViewCache = viewCache;
	        final String sex = bookOwnersView.getSex();
	        if(imageUrl != null && !imageUrl.equals("")){
	        	asyncImageLoader.shutdownSave();
	        	cachedImage = asyncImageLoader.loadDrawable(imageUrl, new ImageCallback() {
		            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
		                ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);
		                if (imageViewByTag != null) {
		                    imageViewByTag.setImageDrawable(imageDrawable);
		                    if (sex.equals(Values.MALE)) {
		                    	tmpViewCache.getSexLogoImageView().setImageResource(R.drawable.shuyou_boy_logo);
		    				} else {
		    					tmpViewCache.getSexLogoImageView().setImageResource(R.drawable.shuyou_girl_logo);
		    				}
		                }
		            }
		        });
	        }
			if (cachedImage == null) {
				if (sex.equals(Values.MALE)) {
					viewCache.getLogoImageView().setImageResource(R.drawable.shuyou_boy_logo);
				} else {
					viewCache.getLogoImageView().setImageResource(R.drawable.shuyou_girl_logo);
				}
			}else{
				/*有用户自定义图片，则需把默认图片作为性别标示显示*/
				if (sex.equals(Values.MALE)) {
					viewCache.getSexLogoImageView().setImageResource(R.drawable.shuyou_boy_logo);
				} else {
					viewCache.getSexLogoImageView().setImageResource(R.drawable.shuyou_girl_logo);
				}
				imageView.setImageDrawable(cachedImage);
			}
			
			/*隐藏用户个人信息*/
			viewCache.getPhoneNumPosition().setVisibility(View.GONE);
			viewCache.getQqPosition().setVisibility(View.GONE);
			viewCache.getWechatPosition().setVisibility(View.GONE);
			
			viewCache.getImagePhone().setVisibility(View.VISIBLE);
			viewCache.getImageQQ().setVisibility(View.VISIBLE);
			viewCache.getImageWechat().setVisibility(View.VISIBLE);
			viewCache.getImageMessage().setVisibility(View.VISIBLE);
			
	        // Set the text on the TextView
	        TextView textNickName = viewCache.getNickNameTextView();
	        textNickName.setText(bookOwnersView.getNickName()+"");
	        
	        String phoneNum = bookOwnersView.getPhoneNum();
	        if(phoneNum == null) {
	        	viewCache.getPhoneNumPosition().setVisibility(View.GONE);
	        	viewCache.getImagePhone().setVisibility(View.GONE);
	        	viewCache.getImageMessage().setVisibility(View.GONE);
	        } else if (phoneNum.equals("")) {
	        	viewCache.getPhoneNumPosition().setVisibility(View.GONE);
	        	viewCache.getImagePhone().setVisibility(View.GONE);
	        	viewCache.getImageMessage().setVisibility(View.GONE);
	        } else {
	        	TextView textPhoneNum = viewCache.getPhoneNumTextView();
		        textPhoneNum.setText(phoneNum);
	        }
	        
	        String qq = bookOwnersView.getQq();
	        if(qq == null) {
	        	viewCache.getQqPosition().setVisibility(View.GONE);
	        	viewCache.getImageQQ().setVisibility(View.GONE);
	        } else if (qq.equals("")){
	        	viewCache.getQqPosition().setVisibility(View.GONE);
	        	viewCache.getImageQQ().setVisibility(View.GONE);
	        } else {
		        TextView textQq = viewCache.getQQTextView();
		        textQq.setText(qq);
	        }
	        
	        String wechat = bookOwnersView.getWechat();
	        if(wechat == null) {
	        	viewCache.getWechatPosition().setVisibility(View.GONE);
	        	viewCache.getImageWechat().setVisibility(View.GONE);
	        } else if(wechat.equals("")) {
	        	viewCache.getWechatPosition().setVisibility(View.GONE);
	        	viewCache.getImageWechat().setVisibility(View.GONE);
	        } else {
		        TextView textWechat = viewCache.getWechatTextView();
		        textWechat.setText(wechat);
	        }
	        
	        TextView textComment = viewCache.getCommentTextView();
	        textComment.setText(bookOwnersView.getComment());
	        
	        final BookOwnersViewCache finalViewCache = viewCache;
	        
	        viewCache.getImagePlus().setOnClickListener(new OnClickListener(){
	        	private boolean isVisible = true;
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(isVisible) {
						finalViewCache.getLinearPlus().setVisibility(View.VISIBLE);
					}
					else {
						finalViewCache.getLinearPlus().setVisibility(View.INVISIBLE);
					}
					isVisible = !isVisible;	
				}
	        });
	        
	        final String finalPhoneNum = phoneNum;
	        final Activity finalActivity = activity;
	        viewCache.getImagePhone().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + finalPhoneNum));
					finalActivity.startActivity(intent);
				}
	        	
	        });
	        
	        viewCache.getImageMessage().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Uri uri = Uri.parse("smsto:"+finalPhoneNum);            
					Intent it = new Intent(Intent.ACTION_SENDTO, uri);            
					it.putExtra("sms_body", "同学你好，我想借你书友上的《"+bookName+"》这本书，方便吗?");
					finalActivity.startActivity(it); 
				}
	        	
	        });
	        
	        final String finalQQ = qq;
	        viewCache.getImageQQ().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ClipboardManager cmb = (ClipboardManager) finalActivity.getSystemService(Service.CLIPBOARD_SERVICE);
					cmb.setText(finalQQ);
					Toast.makeText(finalActivity, "已经复制QQ号到剪贴板", Toast.LENGTH_SHORT).show();
				}
	        	
	        });
	        
	        final String finalWechat = wechat;
	        viewCache.getImageWechat().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ClipboardManager cmb = (ClipboardManager) finalActivity.getSystemService(Service.CLIPBOARD_SERVICE);
					cmb.setText(finalWechat);
					Toast.makeText(finalActivity, "已经复制微信号到剪贴板", Toast.LENGTH_SHORT).show();
				}
	        	
	        });

	        return rowView;
	    }

}
