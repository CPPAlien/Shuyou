package com.shuyou;

import java.util.ArrayList;
import java.util.List;
import com.shuyou.tabui.BookshelfBorrowShuyouTab;
import com.shuyou.tabui.BookshelfLendShuyouTab;
import com.shuyou.tabui.BookshelfUploadShuyouTab;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class BookshelfActivity extends TabActivity {
    //页卡内容
    private ViewPager mPager;
    // Tab页面列表
    private List<View> listViews; 
    // 当前页卡编号
    private int currIndex = 0;
    // 动画图片宽度
    private int bmpW;
    // 动画图片偏移量
    private int offset = 0;
    // 动画图片
    private ImageView cursor;
	private LocalActivityManager manager = null;
    
	private TabHost mTabHost;
	private TextView tabText1 = null;
	private TextView tabText2 = null;
	private TextView tabText3 = null;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mybooks);
		tabText1 = (TextView)findViewById(R.id.mybooks_tab_text1);
		tabText2 = (TextView)findViewById(R.id.mybooks_tab_text2);
		tabText3 = (TextView)findViewById(R.id.mybooks_tab_text3);
		
		tabText1.setOnClickListener(new MyOnClickListener(0));
		tabText2.setOnClickListener(new MyOnClickListener(1));
		tabText3.setOnClickListener(new MyOnClickListener(2));
		
		tabText1.setTextColor(getResources().getColor(R.color.white));
		
		mTabHost = getTabHost();
        mTabHost.addTab(mTabHost.newTabSpec("A").setIndicator(  
                "A").setContent(
                new Intent(this, BookshelfUploadShuyouTab.class)));  
        mTabHost.addTab(mTabHost.newTabSpec("B").setIndicator(  
                "B").setContent(  
                new Intent(this, BookshelfBorrowShuyouTab.class))); 
        mTabHost.addTab(mTabHost.newTabSpec("C").setIndicator(  
                "C").setContent(  
                new Intent(this, BookshelfLendShuyouTab.class))); 

        mTabHost.setCurrentTab(0); 
        
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);

        InitImageView();
        InitViewPager();
        InitButtonListener();
	}
	
	/**
	 * 初始化按钮监听器
	 */
	private void InitButtonListener() {
		findViewById(R.id.mybooks_back).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		findViewById(R.id.mybooks_upload).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(BookshelfActivity.this, BarCodeScanActivity.class);
				startActivityForResult(i, 0);
			}
			
		});
	}
	
	 @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		 /*根据扫码的具体情况，移动到相应的tab*/
		if(requestCode == 0) {
			if(resultCode == 0) {
				mTabHost.setCurrentTab(0);
			} else if(resultCode == 1) {
				mTabHost.setCurrentTab(1);
			} else if(resultCode == 2) {
				mTabHost.setCurrentTab(2);
			}
		}
	}

	/**
     * 初始化动画
     */
    private void InitImageView() {
            cursor = (ImageView) findViewById(R.id.mybooks_cursor);
            bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.shuyou_tab_selected)
                            .getWidth();// 获取图片宽度
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenW = dm.widthPixels;// 获取分辨率宽度
            offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
            Matrix matrix = new Matrix();
            matrix.postTranslate(offset, 0);
            cursor.setImageMatrix(matrix);// 设置动画初始位置
    }
    
	/**
     * 初始化ViewPager
     */
    private void InitViewPager() {
            mPager = (ViewPager) findViewById(R.id.mybooks_vPager);
            listViews = new ArrayList<View>();
            MyPagerAdapter mpAdapter = new MyPagerAdapter(listViews);
            Intent intent1 = new Intent(BookshelfActivity.this, BookshelfUploadShuyouTab.class);
            listViews.add(getView("upload", intent1));
            Intent intent2 = new Intent(BookshelfActivity.this, BookshelfBorrowShuyouTab.class);
            listViews.add(getView("borrow", intent2));
            Intent intent3 = new Intent(BookshelfActivity.this, BookshelfLendShuyouTab.class);
            listViews.add(getView("lend", intent3));
            mPager.setAdapter(mpAdapter);
            mPager.setCurrentItem(0);
            mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }
    
	/**
     * 头标点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
            private int index = 0;

            public MyOnClickListener(int i) {
                    index = i;
            }

            @Override
            public void onClick(View v) {
                    mPager.setCurrentItem(index);
            }
    };
    
    /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
            public List<View> mListViews;

            public MyPagerAdapter(List<View> mListViews) {
                    this.mListViews = mListViews;
            }

            @Override
            public void destroyItem(View arg0, int arg1, Object arg2) {
                    ((ViewPager) arg0).removeView(mListViews.get(arg1));
            }

            @Override
            public void finishUpdate(View arg0) {
            }

            @Override
            public int getCount() {
                    return mListViews.size();
            }

            @Override
            public Object instantiateItem(View arg0, int arg1) {
                    ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
                    return mListViews.get(arg1);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                    return arg0 == (arg1);
            }

            @Override
            public void restoreState(Parcelable arg0, ClassLoader arg1) {
            }

            @Override
            public Parcelable saveState() {
                    return null;
            }

            @Override
            public void startUpdate(View arg0) {
            }
    }
    
    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements OnPageChangeListener {

            int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
            int two = 2 * one;

            @Override
            public void onPageSelected(int arg0) {
                    Animation animation = null;
                    switch (arg0) {
                    case 0:
                        mTabHost.setCurrentTab(0);  
                        if (currIndex == 1) {
                        	tabText1.setTextColor(getResources().getColor(R.color.white));
                        	tabText2.setTextColor(getResources().getColor(R.color.black));
                        	animation = new TranslateAnimation(one, 0, 0, 0);
                        }
                        else if(currIndex == 2) {
                        	tabText1.setTextColor(getResources().getColor(R.color.white));
                        	tabText3.setTextColor(getResources().getColor(R.color.black));
                        	animation = new TranslateAnimation(two, 0, 0, 0);
                        }
                        break;
                    case 1:
                        mTabHost.setCurrentTab(1);
                        if (currIndex == 0) {
                        	tabText1.setTextColor(getResources().getColor(R.color.black));
                        	tabText2.setTextColor(getResources().getColor(R.color.white));
                            animation = new TranslateAnimation(offset, one, 0, 0);
                        } else if(currIndex == 2) {
                        	tabText3.setTextColor(getResources().getColor(R.color.black));
                        	tabText2.setTextColor(getResources().getColor(R.color.white));
                            animation = new TranslateAnimation(two, one, 0, 0);
                        }
                        break;
                    case 2:
                    	mTabHost.setCurrentTab(1);
                        if (currIndex == 0) {
                        	tabText1.setTextColor(getResources().getColor(R.color.black));
                        	tabText3.setTextColor(getResources().getColor(R.color.white));
                            animation = new TranslateAnimation(offset, two, 0, 0);
                        } else if(currIndex == 1) {
                        	tabText2.setTextColor(getResources().getColor(R.color.black));
                        	tabText3.setTextColor(getResources().getColor(R.color.white));
                            animation = new TranslateAnimation(one, two, 0, 0);
                        }
                    	break;
                    default:
                    		break;
                    }
                    currIndex = arg0;
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(300);
                    cursor.startAnimation(animation);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
    }
    
    private View getView(String id,Intent intent)
    {
    	return manager.startActivity(id, intent).getDecorView();
    }
}
