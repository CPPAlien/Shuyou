package com.shuyou.lib;

import com.shuyou.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BookOwnersViewCache {

	    private View baseView;
	    private ImageView imageLogo;
	    private ImageView imageLogoSex;
	    private TextView textNickName;
	    private TextView textPhoneNum;
	    private LinearLayout phoneNumPosition;
	    private TextView textQQ;
	    private LinearLayout qqPosition;
	    private TextView textWechat;
	    private LinearLayout wechatPosition;
	    private TextView textComment;
	    private ImageView imagePlus;
	    private LinearLayout linearPlus;
	    private ImageView imageQQ;
	    private ImageView imageWechat;
	    private ImageView imagePhone;
	    private ImageView imageMessage;

	    public BookOwnersViewCache(View baseView) {
	        this.baseView = baseView;
	    }
	    
	    public ImageView getLogoImageView() {
	        if (imageLogo == null) {
	        	imageLogo = (ImageView) baseView.findViewById(R.id.listview_bookowners_logo);
	        }
	        return imageLogo;
	    }
	    
	    public ImageView getSexLogoImageView() {
	        if (imageLogoSex == null) {
	        	imageLogoSex = (ImageView) baseView.findViewById(R.id.listview_bookowners_sexflag);
	        }
	        return imageLogoSex;
	    }
	    
	    public TextView getNickNameTextView() {
	        if (textNickName == null) {
	            textNickName = (TextView) baseView.findViewById(R.id.listview_bookowners_nickname_content);
	        }
	        return textNickName;
	    }
	    
	    public TextView getPhoneNumTextView() {
	        if (textPhoneNum == null) {
	        	textPhoneNum = (TextView) baseView.findViewById(R.id.listview_bookowners_phonenum_content);
	        }
	        return textPhoneNum;
	    }
	    
	    public LinearLayout getPhoneNumPosition() {
	    	if (phoneNumPosition == null) {
	    		phoneNumPosition = (LinearLayout) baseView.findViewById(R.id.listview_bookowners_phonenum_position);
	    	}
	    	return phoneNumPosition;
	    }
	    
	    public TextView getQQTextView() {
	        if (textQQ == null) {
	            textQQ = (TextView) baseView.findViewById(R.id.listview_bookowners_qq_content);
	        }
	        return textQQ;
	    }
	    
	    public LinearLayout getQqPosition() {
	    	if (qqPosition == null) {
	    		qqPosition = (LinearLayout) baseView.findViewById(R.id.listview_bookowners_qq_position);
	    	}
	    	return qqPosition;
	    }
	    
	    public TextView getWechatTextView() {
	    	if (textWechat == null) {
	    		textWechat = (TextView) baseView.findViewById(R.id.listview_bookowners_wechat_content);
	        }
	        return textWechat;
	    }
	    
	    public LinearLayout getWechatPosition() {
	    	if (wechatPosition == null) {
	    		wechatPosition = (LinearLayout) baseView.findViewById(R.id.listview_bookowners_wechat_position);
	    	}
	    	return wechatPosition;
	    }
	    
	    public TextView getCommentTextView() {
	    	if (textComment == null) {
	    		textComment = (TextView) baseView.findViewById(R.id.listview_bookowners_comment_content);
	        }
	        return textComment;
	    }
	    
	    public ImageView getImagePlus() {
	    	if (imagePlus == null) {
	    		imagePlus = (ImageView) baseView.findViewById(R.id.listview_bookowners_plus_image);
	        }
	        return imagePlus;
	    }
	    
	    public LinearLayout getLinearPlus() {
	    	if (linearPlus == null) {
	    		linearPlus = (LinearLayout) baseView.findViewById(R.id.listview_bookowners_plus_linearLayout);
	        }
	        return linearPlus;
	    }
	    
	    public ImageView getImageQQ() {
	    	if (imageQQ == null) {
	    		imageQQ = (ImageView) baseView.findViewById(R.id.listview_bookowners_qq_image);
	        }
	        return imageQQ;
	    }
	    
	    public ImageView getImageWechat() {
	    	if (imageWechat == null) {
	    		imageWechat = (ImageView) baseView.findViewById(R.id.listview_bookowners_wechat_image);
	        }
	        return imageWechat;
	    }
	    
	    public ImageView getImagePhone() {
	    	if (imagePhone == null) {
	    		imagePhone = (ImageView) baseView.findViewById(R.id.listview_bookowners_phone_image);
	        }
	        return imagePhone;
	    }
	    
	    public ImageView getImageMessage() {
	    	if (imageMessage == null) {
	    		imageMessage = (ImageView) baseView.findViewById(R.id.listview_bookowners_message_image);
	        }
	        return imageMessage;
	    }

}