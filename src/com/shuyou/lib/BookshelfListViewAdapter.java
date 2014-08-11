package com.shuyou.lib;

import java.util.ArrayList;
import java.util.List;

import com.shuyou.BookshelfDetailActivity;
import com.shuyou.R;
import com.shuyou.lib.AsyncImageLoader.ImageCallback;
import com.shuyou.net.BookDetail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class BookshelfListViewAdapter extends ArrayAdapter<BookshelfListView> {

	    private ListView listView;
	    private AsyncImageLoader asyncImageLoader;
	    private ArrayList<BookDetail> booklist = null;
	    private String type = null;
	    
	    public BookshelfListViewAdapter(Activity activity, ArrayList<BookDetail> booklist, List<BookshelfListView> bookListView, ListView listView, String type) {
	        super(activity, 0, bookListView);
	        this.listView = listView;
	        this.asyncImageLoader = new AsyncImageLoader(activity);
	        this.booklist = booklist;
	        this.type = type;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        Activity activity = (Activity) getContext();

	        // Inflate the views from XML
	        View rowView = convertView;
	        BookshelfViewCache viewCache;
	        if (rowView == null) {
	            LayoutInflater inflater = activity.getLayoutInflater();
	            rowView = inflater.inflate(R.layout.shuyou_listview_bookshelf, null);
	            viewCache = new BookshelfViewCache(rowView);
	            rowView.setTag(viewCache);
	        } else {
	            viewCache = (BookshelfViewCache) rowView.getTag();
	        }
	        BookshelfListView bookListView = getItem(position);

	        viewCache.getViewPosition1().setVisibility(View.VISIBLE);
	        viewCache.getViewPosition2().setVisibility(View.VISIBLE);
	        viewCache.getViewPosition3().setVisibility(View.VISIBLE);
	        
	        viewCache.getTimePosition1().setVisibility(View.VISIBLE);
        	viewCache.getTimePosition2().setVisibility(View.VISIBLE);
        	viewCache.getTimePosition3().setVisibility(View.VISIBLE);
        	
	        /*如果是已传书籍，则没有时间限制*/
	        if(type.equals("upload")) {
	        	viewCache.getTimePosition1().setVisibility(View.GONE);
	        	viewCache.getTimePosition2().setVisibility(View.GONE);
	        	viewCache.getTimePosition3().setVisibility(View.GONE);
	        }
	        /**
	         * 一列有三本书，当某本缺失，传回"null"，则把该书的位置隐藏
	         */
	        String imageUrl1 = bookListView.GetImageUrlLogo1();
	        ImageView imageView1 = viewCache.getImageView1();
	        if(imageUrl1 == "null") {
	        	viewCache.getViewPosition1().setVisibility(View.GONE);
	        } else { 
		        imageView1.setTag(imageUrl1);
		        Drawable cachedImage1 = asyncImageLoader.loadDrawable(imageUrl1, new ImageCallback() {
		            public void imageLoaded(Drawable imageDrawable, String imageUrl1) {
		                ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl1);
		                if (imageViewByTag != null) {
		                    imageViewByTag.setImageDrawable(imageDrawable);
		                }
		            }
		        });
				if (cachedImage1 == null) {
					imageView1.setImageResource(R.drawable.ic_launcher);
				}else{
					imageView1.setImageDrawable(cachedImage1);
				}
				final int finalPos = position;
				final BookDetail bd = booklist.get(finalPos*3 + 0);
				
				if(!type.equals("upload")) {
					String days = bd.getLeft_days();
					viewCache.getTime1().setText(days+"天");
					if(days.startsWith("-")) {
						
						viewCache.getTime1().setTextColor(Color.RED);
						viewCache.getTimeImage1().setImageResource(R.drawable.shuyou_time_red);
					}
				}
				final Activity finalAct = activity;
				final RelativeLayout imagePosition1 = (RelativeLayout)viewCache.getViewPosition1();
				imageView1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent i = new Intent(finalAct, BookshelfDetailActivity.class);
						
						imagePosition1.setTag(bd.getBookid());
						i.putExtra("bookId", bd.getBookid());
						i.putExtra("bookName", bd.getBook_name());
						i.putExtra("type", type);
						finalAct.startActivityForResult(i, 0);
					}
				});
	        }
			
			// 加载第二个logo，如果传入的连接为"",则说明该位置没有书，删除该位置
	        String imageUrl2 = bookListView.GetImageUrlLogo2();
	        ImageView imageView2 = viewCache.getImageView2();
	        if(imageUrl2 == "null") {
	        	viewCache.getViewPosition2().setVisibility(View.GONE);
	        } else {
		        imageView2.setTag(imageUrl2);
		        Drawable cachedImage2 = asyncImageLoader.loadDrawable(imageUrl2, new ImageCallback() {
		            public void imageLoaded(Drawable imageDrawable, String imageUrl2) {
		                ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl2);
		                if (imageViewByTag != null) {
		                    imageViewByTag.setImageDrawable(imageDrawable);
		                }
		            }
		        });
				if (cachedImage2 == null) {
					imageView2.setImageResource(R.drawable.ic_launcher);
				}else{
					imageView2.setImageDrawable(cachedImage2);
				}
				final int finalPos = position;
				final BookDetail bd = booklist.get(finalPos*3 + 1);
				if(!type.equals("upload")) {
					String days = bd.getLeft_days();
					viewCache.getTime2().setText(days+"天");
					if(days.startsWith("-")) {
						viewCache.getTime2().setTextColor(Color.RED);
						viewCache.getTimeImage2().setImageResource(R.drawable.shuyou_time_red);
					}
					
				}
				final Activity finalAct = activity;
				final RelativeLayout imagePosition2 = (RelativeLayout)viewCache.getViewPosition2();
				imageView2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent i = new Intent(finalAct, BookshelfDetailActivity.class);
						
						imagePosition2.setTag(bd.getBookid());
						i.putExtra("bookId", bd.getBookid());
						i.putExtra("bookName", bd.getBook_name());
						i.putExtra("type", type);
						finalAct.startActivityForResult(i, 0);
					}
				});
	        }
			
			// Load the image and set it on the ImageView
	        String imageUrl3 = bookListView.GetImageUrlLogo3();
	        if(imageUrl3 == "null") {
	        	viewCache.getViewPosition3().setVisibility(View.GONE);
	        } else {
		        ImageView imageView3 = viewCache.getImageView3();
		        imageView3.setTag(imageUrl3);
		        Drawable cachedImage3 = asyncImageLoader.loadDrawable(imageUrl3, new ImageCallback() {
		            public void imageLoaded(Drawable imageDrawable, String imageUrl3) {
		                ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl3);
		                if (imageViewByTag != null) {
		                    imageViewByTag.setImageDrawable(imageDrawable);
		                }
		            }
		        });
				if (cachedImage3 == null) {
					imageView3.setImageResource(R.drawable.ic_launcher);
				}else{
					imageView3.setImageDrawable(cachedImage3);
				}
				final int finalPos = position;
				final BookDetail bd = booklist.get(finalPos*3 + 2);
				if(!type.equals("upload")) {
					String days = bd.getLeft_days();
					viewCache.getTime3().setText(days+"天");
					if(days.startsWith("-")) {
						viewCache.getTime3().setTextColor(Color.RED);
						viewCache.getTimeImage3().setImageResource(R.drawable.shuyou_time_red);
					}
				}
				final Activity finalAct = activity;
				final RelativeLayout imagePosition3 = (RelativeLayout)viewCache.getViewPosition2();
				imageView3.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent i = new Intent(finalAct, BookshelfDetailActivity.class);
						
						imagePosition3.setTag(bd.getBookid());
						i.putExtra("bookId", bd.getBookid());
						i.putExtra("bookName", bd.getBook_name());
						i.putExtra("type", type);
						finalAct.startActivityForResult(i, 0);
					}
				});
	        }
	        

	        return rowView;
	    }
}
