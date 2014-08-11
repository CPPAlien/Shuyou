package com.shuyou.lib;

import com.shuyou.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BookshelfViewCache {

	    private View baseView;
	    private ImageView imageView1;
	    private ImageView imageView2;
	    private ImageView imageView3;
	    
	    private RelativeLayout viewPosition1;
	    private RelativeLayout viewPosition2;
	    private RelativeLayout viewPosition3;
	    
	    private LinearLayout timePosition1;
	    private LinearLayout timePosition2;
	    private LinearLayout timePosition3;
	    
	    private TextView time1;
	    private TextView time2;
	    private TextView time3;
	    
	    private ImageView timeImage1;
	    private ImageView timeImage2;
	    private ImageView timeImage3;

	    public BookshelfViewCache(View baseView) {
	        this.baseView = baseView;
	    }
	    
	    public ImageView getImageView1() {
	        if (imageView1 == null) {
	            imageView1 = (ImageView) baseView.findViewById(R.id.bookshelf_logo1);
	        }
	        return imageView1;
	    }
	    
	    public ImageView getImageView2() {
	    	if (imageView2 == null) {
	    		imageView2 = (ImageView) baseView.findViewById(R.id.bookshelf_logo2);
	    	}
	    	return imageView2;
	    }
	    
	    public ImageView getImageView3() {
	    	if (imageView3 == null) {
	    		imageView3 = (ImageView) baseView.findViewById(R.id.bookshelf_logo3);
	    	}
	    	return imageView3;
	    }

	    public RelativeLayout getViewPosition1() {
			if (viewPosition1 == null) {
				viewPosition1 = (RelativeLayout) baseView.findViewById(R.id.bookshelf_logo1_position);
	    	}
			return viewPosition1;
		}
	    
		public RelativeLayout getViewPosition2() {
			if (viewPosition2 == null) {
				viewPosition2 = (RelativeLayout) baseView.findViewById(R.id.bookshelf_logo2_position);
	    	}
			return viewPosition2;
		}

		public RelativeLayout getViewPosition3() {
			if (viewPosition3 == null) {
				viewPosition3 = (RelativeLayout) baseView.findViewById(R.id.bookshelf_logo3_position);
	    	}
			return viewPosition3;
		}	   
		
		public LinearLayout getTimePosition1() {
			if (timePosition1 == null) {
				timePosition1 = (LinearLayout) baseView.findViewById(R.id.bookshelf_logo1_time_position);
	    	}
			return timePosition1;
		}	   
		
		public LinearLayout getTimePosition2() {
			if (timePosition2 == null) {
				timePosition2 = (LinearLayout) baseView.findViewById(R.id.bookshelf_logo2_time_position);
	    	}
			return timePosition2;
		}	      
		
		public LinearLayout getTimePosition3() {
			if (timePosition3 == null) {
				timePosition3 = (LinearLayout) baseView.findViewById(R.id.bookshelf_logo3_time_position);
	    	}
			return timePosition3;
		}
		
		public TextView getTime1() {
			if(time1 == null) {
				time1 = (TextView) baseView.findViewById(R.id.bookshelf_logo1_time);
			}
			return time1;
		}
		
		public TextView getTime2() {
			if(time2 == null) {
				time2 = (TextView) baseView.findViewById(R.id.bookshelf_logo2_time);
			}
			return time2;
		}
		
		public TextView getTime3() {
			if(time3 == null) {
				time3 = (TextView) baseView.findViewById(R.id.bookshelf_logo3_time);
			}
			return time3;
		}
		
		public ImageView getTimeImage1() {
			if(timeImage1 == null) {
				timeImage1 = (ImageView) baseView.findViewById(R.id.bookshelf_logo1_time_image);
			}
			return timeImage1;
		}
		
		public ImageView getTimeImage2() {
			if(timeImage2 == null) {
				timeImage2 = (ImageView) baseView.findViewById(R.id.bookshelf_logo2_time_image);
			}
			return timeImage2;
		}
		
		public ImageView getTimeImage3() {
			if(timeImage3 == null) {
				timeImage3 = (ImageView) baseView.findViewById(R.id.bookshelf_logo3_time_image);
			}
			return timeImage3;
		}

}