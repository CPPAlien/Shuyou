
package com.shuyou.widget;

import com.shuyou.R;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class SlidePagerView implements OnClickListener {

    // Activity的引用
    private FragmentActivity fragementActivity;

    // 当前被选中的RadioButton距离左侧的距离
    private float mCurrentCheckedRadioLeft;

    private MyViewPager mViewPager;

    // Tab底部选中标示的横线
    private ImageView botomBlueImg;

    // Tab的父元素
    private HorizontalScrollView mHorizontalScrollView;

    private List<Button> titleButtons;

    private LinearLayout topTitleLayout;

    private int defaultCheckdIndex;

    private int currentChooseIndex = 0;

    private ArrayList<Fragment> mViews;

    private ArrayList<String> mTabNames;

    private View viewPagerView;

    private OnSlidePagerSelectedListener onSlidePagerSelectedListener;

    private LayoutInflater viewPagerLayout;

    private int titleOneWidth = 100;

    private AlphaAnimation topPointAlpAnimation;// 箭头消失动画

    private int screenWidth;

    private float div;

    private float divHeight;

    public static int screenItemCount = 4;

    private ImageView webGestureLeftImg;

    private ImageView webGestureRightImg;

    // private ImageView moreLine;
    //
    // private FragmentManager manager;

    private boolean isLocalEdit = false;

    public boolean isLocalEdit() {
        return isLocalEdit;
    }

    public void setLocalEdit(boolean isLocalEdit) {
        this.isLocalEdit = isLocalEdit;
    }

    public View getSlidePagerView() {
        return viewPagerView;
    }

    public MyViewPager getmViewPager() {
        return mViewPager;
    }

    /**
     * 添加构造函数，一屏显示几个
     * 
     * @param screenCount 一屏幕有几个Item
     */
    public SlidePagerView(FragmentActivity fragementActivity, FragmentManager manager,
            ArrayList<String> tabNames, ArrayList<Fragment> viewpagerlayoutsParm,
            int defaultCheckd, int screenCount) {
        this(fragementActivity, manager, tabNames, viewpagerlayoutsParm, defaultCheckd,
                screenCount, 0);
    }

    /**
     * 添加构造函数，一屏显示几个
     * 
     * @param screenCount 一屏幕有几个Item
     */
    public SlidePagerView(FragmentActivity fragementActivity, FragmentManager manager,
            ArrayList<String> tabNames, ArrayList<Fragment> viewpagerlayoutsParm,
            int defaultCheckd, int screenCount, int showOffSet) {
        this.fragementActivity = fragementActivity;

        div = fragementActivity.getResources().getDimension(R.dimen.slide_pager_div);
        divHeight = fragementActivity.getResources().getDimension(R.dimen.slide_pager_div_height);
        // LogHelper.i(TAG, "div="+div+"    int:"+(int)div);
        if (div < 1) {
            div = 1;
        }

        viewPagerLayout = LayoutInflater.from(fragementActivity);
        viewPagerView = viewPagerLayout.inflate(R.layout.slidepagerview, null);
        topTitleLayout = (LinearLayout) viewPagerView.findViewById(R.id.top_title_father_layout);
        botomBlueImg = (ImageView) viewPagerView.findViewById(R.id.img1);
        mHorizontalScrollView = (HorizontalScrollView) viewPagerView
                .findViewById(R.id.horizontalScrollView);


        // moreLine = (ImageView)
        // viewPagerView.findViewById(R.id.slide_more_line);

        // 计算titlewidth
        DisplayMetrics dm = new DisplayMetrics();
        fragementActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        if (screenWidth > screenHeight/* && StormApplication.isPad */) {
            screenWidth = screenHeight;
        }

        titleOneWidth = screenWidth / screenCount - (int) div - showOffSet;
        mCurrentCheckedRadioLeft = ((defaultCheckd) * titleOneWidth) + (int) (defaultCheckd * div);

        webGestureLeftImg = (ImageView) viewPagerView
                .findViewById(R.id.top_pager_point_left_img_id);
        webGestureRightImg = (ImageView) viewPagerView
                .findViewById(R.id.top_pager_point_right_img_id);

        webGestureLeftImg.setVisibility(View.GONE);
        webGestureRightImg.setVisibility(View.GONE);

        initTopPointAlpAnimation();

        mViewPager = (MyViewPager) viewPagerView.findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(new MyPagerOnPageChangeListener());

        defaultCheckdIndex = defaultCheckd;
        currentChooseIndex = defaultCheckdIndex;

        setTitleBar(tabNames);

        setViewPagers(viewpagerlayoutsParm, manager);

        RelativeLayout.LayoutParams para = (RelativeLayout.LayoutParams) botomBlueImg
                .getLayoutParams();
        para.width = titleOneWidth;
        // para.leftMargin = ((defaultCheckd) * titleOneWidth) + (int)
        // (defaultCheckd * div);
        botomBlueImg.setLayoutParams(para);
        // mViewPager.setFocusableInTouchMode(true);

        // 设置是否呈现按下箭头后的平滑滚动效果(动画效果)
        mHorizontalScrollView.setSmoothScrollingEnabled(true);

    }

    public SlidePagerView(FragmentActivity fragmentActivity, FragmentManager manager,
            ArrayList<String> tabNames, ArrayList<Fragment> viewpagerlayoutsParm,
            int defaultCheckd) {
        this(fragmentActivity, manager, tabNames, viewpagerlayoutsParm, defaultCheckd,
                screenItemCount);
    }

    /**
     * 初始化箭头消失动画
     */
    private void initTopPointAlpAnimation() {
        topPointAlpAnimation = new AlphaAnimation(1.0f, 0.0f);
        topPointAlpAnimation.setDuration(500);

        topPointAlpAnimation.setAnimationListener(new AnimationListener() {

            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                if (webGestureLeftImg.getVisibility() == View.VISIBLE) {
                    webGestureLeftImg.setVisibility(View.GONE);
                    webGestureRightImg.clearAnimation();
                }
                if (webGestureRightImg.getVisibility() == View.VISIBLE) {
                    webGestureRightImg.setVisibility(View.GONE);
                    webGestureRightImg.clearAnimation();
                }
            }
        });
    }

    /**
     * 修改默认一屏显示4个
     * 
     */
    public SlidePagerView(FragmentActivity fragmentActivity,
            ArrayList<String> tabNames, ArrayList<Fragment> viewpagerlayoutsParm,
            int defaultCheckd) {
        this(fragmentActivity, fragmentActivity.getSupportFragmentManager(), tabNames,
                viewpagerlayoutsParm, defaultCheckd, screenItemCount);
    }

    public MyViewPager getViewPager() {
        return mViewPager;
    }
    
    private void setTitleBar(ArrayList<String> tabNames) {

        mTabNames = tabNames;
        topTitleLayout.removeAllViews();
        if (titleButtons != null) {
            titleButtons.clear();
        } else {
            titleButtons = new ArrayList<Button>();
        }
        for (int i = 0; i < mTabNames.size(); i++) {
            String name = mTabNames.get(i);
            Button radioButton;
            radioButton = (Button) viewPagerLayout.inflate(R.layout.top_tab_seed_rb, null);

            radioButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT));
            radioButton.setText(name);
            radioButton.setWidth(titleOneWidth);
            radioButton.setId(i);
            //radioButton.setTag(channel.getChannel());
            radioButton.setOnClickListener(this);
            titleButtons.add(radioButton);
            if (i > 0) {
                View lineView = new View(fragementActivity);
                lineView.setLayoutParams(new LayoutParams((int) div, (int) divHeight));
                lineView.setBackgroundColor(Color.rgb(219, 219, 219));
                lineView.setPadding(0, 5, 0, 5);
                topTitleLayout.addView(lineView);
            }
            topTitleLayout.addView(radioButton);

        }
        resetTopBgWidth();

    }


    public void resetTopBgWidth() {
        if (titleButtons.size() < screenItemCount) {
            LayoutParams toptitlewParam = topTitleLayout.getLayoutParams();
            toptitlewParam.width = screenWidth;
            topTitleLayout.setLayoutParams(toptitlewParam);
        }
    }

    /**
     * 获取当 前所处的页面
     */
    public int getCurrentItem() {
        if (mViewPager == null) {
            return 0;
        }
        return mViewPager.getCurrentItem();
    }

    private void setViewPagers(ArrayList<Fragment> viewpagerlayouts, FragmentManager manager) {
        mViews = viewpagerlayouts;
        if (mViewPager == null) {
            return;
        }
        
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new AppSectionsPagerAdapter(manager, viewpagerlayouts));// 设置ViewPager的适配器
        mViewPager.setCurrentItem(defaultCheckdIndex);
        currentChooseIndex = defaultCheckdIndex;
    }

    // private void showLeftBack() {
    // webGestureRightImg.clearAnimation();
    // webGestureRightImg.setVisibility(View.GONE);
    // webGestureLeftImg.setVisibility(View.VISIBLE);
    //
    // }
    //
    // private void showRightBack() {
    // webGestureLeftImg.clearAnimation();
    // webGestureLeftImg.setVisibility(View.GONE);
    // webGestureRightImg.setVisibility(View.VISIBLE);
    // }
    //
    // private void showAllBack() {
    // webGestureLeftImg.setVisibility(View.VISIBLE);
    // webGestureRightImg.setVisibility(View.VISIBLE);
    // }
    //
    // private void hideAllBack() {
    // if (webGestureLeftImg.getVisibility() == View.VISIBLE) {
    // webGestureLeftImg.startAnimation(topPointAlpAnimation);
    // }
    // if (webGestureRightImg.getVisibility() == View.VISIBLE) {
    // webGestureRightImg.startAnimation(topPointAlpAnimation);
    // }
    // }

    /**
     * ViewPager的PageChangeListener(页面改变的监听器)
     * 
     */
    private class MyPagerOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            // if (state == 1) {
            // if (titleButtons != null
            // && titleButtons.size() > screenItemCount) {
            // int titleButtonsSize = titleButtons.size();
            // if (currentChooseIndex <= 2) {
            // showRightBack();
            // } else if (currentChooseIndex >= titleButtonsSize - 2) {
            // showLeftBack();
            // } else {
            // showAllBack();
            // }
            // }
            // } else if (state == 0) {
            // if (titleButtons != null
            // && titleButtons.size() > screenItemCount) {
            // hideAllBack();
            // }
            // }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == 0 && positionOffsetPixels == 0) {// 往左滑动
                mViewPager.getParent().requestDisallowInterceptTouchEvent(false);
            } else {
                mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
            }
        }

        /**
         * 当满足翻页条件时记录当前状态
         * 
         */
        @Override
        public void onPageSelected(final int position) {
            currentChooseIndex = position;
            titleScroll(position);
            blankSrollSlow(position);
            if (onSlidePagerSelectedListener != null) {
                onSlidePagerSelectedListener.onSlidePagerSelected(position, mViews.get(position));
            }

        }
    }

    /**
     * 蓝色下划线滑动200秒内滑动
     * 
     */
    protected void blankSrollSlow(int checkedId) {
        final int tempLeftLength = ((checkedId) * titleOneWidth) + (int) (checkedId * div);
        // TranslateAnimation animation = new
        // TranslateAnimation(mCurrentCheckedRadioLeft,
        // tempLeftLength, 0f, 0f);
        // animation.setFillAfter(true);
        // animation.setDuration(200);
        // botomBlueImg.setAnimation(animation);
        blankScroll(tempLeftLength);

    }

    /**
     * 蓝色下划线滑动
     * 
     */
    protected void blankScroll(int tempLeftLength) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) botomBlueImg
                .getLayoutParams();
        params.leftMargin = tempLeftLength;
        botomBlueImg.setLayoutParams(params);
    }

    /**
     * button滑动
     * 
     */
    private void titleScroll(int position) {
        int tempLeftLength = ((position) * titleOneWidth) + (int) ((position) * div);
        if (mViewPager != null) {
            mViewPager.setCurrentItem(position);
        }
        mCurrentCheckedRadioLeft = tempLeftLength;
        mHorizontalScrollView.smoothScrollTo((int) mCurrentCheckedRadioLeft - titleOneWidth * 2, 0);
    }

    public void setViewPageShow(int postindex) {
        titleButtons.get(postindex).performClick();
    }

    public void setCurrTitleButtonShow() {
        int tempLeftLength = ((currentChooseIndex) * titleOneWidth)
                + (int) (currentChooseIndex * div);
        mCurrentCheckedRadioLeft = tempLeftLength;
        mHorizontalScrollView.smoothScrollTo((int) mCurrentCheckedRadioLeft - titleOneWidth * 2, 0);
    }

    public OnSlidePagerSelectedListener getOnSlidePagerSelectedListener() {
        return onSlidePagerSelectedListener;
    }

    public void setOnSlidePagerSelectedListener(
            OnSlidePagerSelectedListener onSlidePagerSelectedListener) {
        this.onSlidePagerSelectedListener = onSlidePagerSelectedListener;
    }

    @Override
    public void onClick(View v) {
        if (!isLocalEdit) {
            int checkedId = v.getId();

            titleScroll(checkedId);
            blankSrollSlow(checkedId);
            if (onSlidePagerSelectedListener != null) {
                onSlidePagerSelectedListener.onSlidePagerSelected(checkedId, mViews.get(checkedId));
            }
        }
    }

    public void moveToEnd(int checkedId) {
        int tempLeftLength = ((checkedId) * titleOneWidth) + (int) (checkedId * div);

        mCurrentCheckedRadioLeft = tempLeftLength;

        mHorizontalScrollView.smoothScrollTo((int) mCurrentCheckedRadioLeft - titleOneWidth * 2, 0);
    }

    public int getCurrentChooseIndex() {
        return currentChooseIndex;
    }

    /**
     * Adapter A {@link FragmentPagerAdapter} that returns a fragment
     * corresponding to one of the primary sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;

        public AppSectionsPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            // LogHelper.i(TAG, "getItem position = " + position + " tag="
            // + fragments.get(position).getTag());
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }

        public ArrayList<Fragment> getFragments() {
            return fragments;
        }

        public void setFragments(ArrayList<Fragment> fragments) {
            this.fragments = fragments;
        }

    }

    public interface OnSlidePagerSelectedListener {

        void onSlidePagerSelected(int index, Fragment selectedView);
    }
}
