package com.shuyou.lib;

import java.util.List;

import com.shuyou.R;
import com.shuyou.lib.AsyncImageLoader.ImageCallback;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BooksDesListViewAdapter extends ArrayAdapter<BooksDesListView> {

	    private ListView listView;
	    private AsyncImageLoader asyncImageLoader;

	    public BooksDesListViewAdapter(Activity activity, List<BooksDesListView> booksDesListView, ListView list) {
	        super(activity, 0, booksDesListView);
	        this.listView = list;
	        asyncImageLoader = new AsyncImageLoader(activity);
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        Activity activity = (Activity) getContext();

	        // Inflate the views from XML
	        View rowView = convertView;
	        BooksDesViewCache viewCache;
	        if (rowView == null) {
	            LayoutInflater inflater = activity.getLayoutInflater();
	            rowView = inflater.inflate(R.layout.shuyou_listview_tab_booksdes, null);
	            viewCache = new BooksDesViewCache(rowView);
	            rowView.setTag(viewCache);
	        } else {
	            viewCache = (BooksDesViewCache) rowView.getTag();
	        }
	        BooksDesListView booksDesListView = getItem(position);
	        
	        viewCache.getContentPosition().setVisibility(View.VISIBLE);
	        viewCache.getImageView().setVisibility(View.VISIBLE);

	        // Load the image and set it on the ImageView
	        String imageUrl = booksDesListView.getImageUrl();
	        ImageView imageView = viewCache.getImageView();
	        if(imageUrl.equals("null")) {
	        	imageView.setVisibility(View.GONE);
	        } else {
		        imageView.setTag(imageUrl);
		        Drawable cachedImage = asyncImageLoader.loadDrawable(imageUrl, new ImageCallback() {
		            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
		                ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);
		                if (imageViewByTag != null) {
		                    imageViewByTag.setImageDrawable(imageDrawable);
		                }
		            }
		        });
				if (cachedImage == null) {
					imageView.setImageResource(R.drawable.shuyou_bookcover);
				}else{
					imageView.setImageDrawable(cachedImage);
				}
	        }
	        
	        if(booksDesListView.getBookName().equals("null")) {
	        	viewCache.getContentPosition().setVisibility(View.GONE);
	        } else {
		        // Set the text on the TextView
		        TextView textBooksName = viewCache.getBooksNameTextView();
		        textBooksName.setText(booksDesListView.getBookName());
		        
		        TextView textAuthorName = viewCache.getAuthorNameTextView();
		        textAuthorName.setText(booksDesListView.getAuthorName());
		        
		        TextView textBooksLabel = viewCache.getBooksLabelTextView();
		        textBooksLabel.setText(booksDesListView.getLabel());
		        
		        TextView textBooksNumber = viewCache.getBooksNumberTextView();
		        textBooksNumber.setText(booksDesListView.getNumber());
	        }
	        

	        return rowView;
	    }

}
