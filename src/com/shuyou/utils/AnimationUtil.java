package com.shuyou.utils;


import com.shuyou.R;

import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimationUtil {

    /**
     * 显示loading界面
     * @author jiabin
     * @param loadingLayout
     */
    public void showLoadingDialog(View loadingLayout) {
        if (loadingLayout == null) {
            return;
        }
        ImageView image1 = (ImageView) loadingLayout.findViewById(R.id.loading_image1);
        ImageView image2 = (ImageView) loadingLayout.findViewById(R.id.loading_image2);
        ImageView image3 = (ImageView) loadingLayout.findViewById(R.id.loading_image3);
        TextView textView = (TextView) loadingLayout.findViewById(R.id.loading_txt);
        image1.setBackgroundResource(R.anim.loading_anim1);
        image2.setBackgroundResource(R.anim.loading_anim2);
        image3.setBackgroundResource(R.anim.loading_anim3);
        AnimationDrawable anim1 = (AnimationDrawable) image1.getBackground();
        AnimationDrawable anim2 = (AnimationDrawable) image2.getBackground();
        AnimationDrawable anim3 = (AnimationDrawable) image3.getBackground();
        anim1.start();
        anim2.start();
        anim3.start();
        loadingLayout.setVisibility(View.VISIBLE);
        Resources res = loadingLayout.getResources();
        String[] string = res.getStringArray(R.array.loading_string);

        double a = Math.random() * 10;
        a = Math.ceil(a);
        int randomNum = new Double(a).intValue();
        textView.setText(string[randomNum % string.length]);
    }
    
    /**
     * 隐藏loading界面
     * @author jiabin
     * @param loadingLayout
     */
    public void dismissLoadingDialog(View loadingLayout) {
        if (loadingLayout == null) {
            return;
        }
        loadingLayout.setVisibility(View.GONE);
    }
    
}
