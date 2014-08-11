/**
 * 实现侧滑效果的动画
 * 对应
 * filter_slide_in.xml
 * filter_slide_out.xml
 * other_slide_in.xml
 * other_slide_out.xml
 */
package com.shuyou.utils;

import com.shuyou.R;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TabHost;

public class FilterAnimation implements AnimationListener 
{
    Context context;

    RelativeLayout filterLayout;
    TabHost otherLayout;

    private Animation filterSlideIn, filterSlideOut, otherSlideIn, otherSlideOut;

    private static int otherLayoutWidth, otherLayoutHeight;

    private boolean isOtherSlideOut = false;

    private int deviceWidth;

    private int margin;

    public FilterAnimation(Context context) 
    {
        this.context = context;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels; // as my animation is x-axis related so i gets the device width and will use that width,so that this sliding menu will work fine in all screen resolutions
    }

    public void initializeFilterAnimations(RelativeLayout filterLayout)
    {
        this.filterLayout = filterLayout;
        filterSlideIn = AnimationUtils.loadAnimation(context, R.anim.filter_slide_in);
        filterSlideOut = AnimationUtils.loadAnimation(context, R.anim.filter_slide_out);
    }

    public void initializeOtherAnimations(TabHost otherLayout)
    {       
        this.otherLayout = otherLayout;

        otherLayoutWidth = otherLayout.getWidth();
        otherLayoutHeight = otherLayout.getHeight();

        otherSlideIn = AnimationUtils.loadAnimation(context, R.anim.other_slide_in);
        otherSlideIn.setAnimationListener(this);

        otherSlideOut = AnimationUtils.loadAnimation(context, R.anim.other_slide_out);
        otherSlideOut.setAnimationListener(this);
    }

    public void toggleSliding()
    {
        if(isOtherSlideOut) //check if findLayout is already slided out so get so animate it back to initial position
        {
        	SlidingHide();
        }
        else //slide findLayout Out and filterLayout In
        {
        	SlidingDisplay();
        }
    }
    
    public boolean isDisplayed() {
    	return isOtherSlideOut;
    }
    
    /*显示侧滑菜单*/
    public void SlidingDisplay()
    {
    	 otherLayout.startAnimation(otherSlideOut);
         filterLayout.setVisibility(View.VISIBLE);
         filterLayout.startAnimation(filterSlideIn);
    }
    
    /*隐藏侧滑菜单*/
    public void SlidingHide()
    {
    	filterLayout.startAnimation(filterSlideOut);
        filterLayout.setVisibility(View.INVISIBLE);
        otherLayout.startAnimation(otherSlideIn);
    }

    @Override
    public void onAnimationEnd(Animation animation) 
    {
        if(isOtherSlideOut) //Now here we will actually move our view to the new position,because animations just move the pixels not the view
        {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(otherLayoutWidth, otherLayoutHeight);
            otherLayout.setLayoutParams(params);
            isOtherSlideOut = false;
        }
        else
        {   
            margin = (deviceWidth * 70) / 100; 
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(otherLayoutWidth, otherLayoutHeight);

            params.leftMargin = margin;

            params.rightMargin = -margin; //same margin from right side (negavite) so that our layout won't get shrink
            otherLayout.setLayoutParams(params);

            isOtherSlideOut = true;
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) 
    {

    }

    @Override
    public void onAnimationStart(Animation animation) 
    {

    }

    private void dimOtherLayout()
    {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.5f);
        alphaAnimation.setDuration(400);
        alphaAnimation.setFillAfter(true);
        otherLayout.startAnimation(alphaAnimation);
    }
}