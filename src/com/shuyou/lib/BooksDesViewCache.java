package com.shuyou.lib;

import com.shuyou.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BooksDesViewCache {

	    private View baseView;
	    private TextView textBooksName;
	    private TextView textAuthorName;
	    private TextView textLabel;
	    private TextView textNumber;
	    private ImageView imageView;
	    private LinearLayout contentPosition;

	    public BooksDesViewCache(View baseView) {
	        this.baseView = baseView;
	    }
	    
	    public ImageView getImageView() {
	        if (imageView == null) {
	            imageView = (ImageView) baseView.findViewById(R.id.tab_booksdes_book_logo);
	        }
	        return imageView;
	    }
	    
	    public TextView getBooksNameTextView() {
	        if (textBooksName == null) {
	            textBooksName = (TextView) baseView.findViewById(R.id.tab_booksdes_bookname);
	        }
	        return textBooksName;
	    }
	    
	    public TextView getAuthorNameTextView() {
	        if (textAuthorName == null) {
	            textAuthorName = (TextView) baseView.findViewById(R.id.tab_booksdes_author);
	        }
	        return textAuthorName;
	    }
	    public TextView getBooksLabelTextView() {
	        if (textLabel == null) {
	            textLabel = (TextView) baseView.findViewById(R.id.tab_booksdes_label);
	        }
	        return textLabel;
	    }
	    public TextView getBooksNumberTextView() {
	    	if (textNumber == null) {
	            textNumber = (TextView) baseView.findViewById(R.id.tab_booksdes_number);
	        }
	        return textNumber;
	    }
	    public LinearLayout getContentPosition() {
	    	if (contentPosition == null) {
	    		contentPosition = (LinearLayout) baseView.findViewById(R.id.tab_booksdes_book_content_position);
	        }
	        return contentPosition;
	    }

}