package com.shuyou;

import com.shuyou.fragments.AllLabelFragment;
import com.shuyou.fragments.HotLabelFragment;
import com.shuyou.widget.SlidePagerView;
import com.shuyou.widget.SlidePagerView.OnSlidePagerSelectedListener;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * 搜索页面
 * @author jiabin
 *
 */
public class SearchActivity extends FragmentActivity implements OnClickListener , OnSlidePagerSelectedListener {
    private EditText searchEditText;

    private ImageView searchButton;

    private Animation shake;

    private ArrayList<Fragment> viewPagers;

    private HotLabelFragment hotLabelFragment;

    private AllLabelFragment allLabelFragment;

    private SlidePagerView slidePagerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        LinearLayout tabsLayout = (LinearLayout) findViewById(R.id.search_label_tab_layout);
        shake = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.shake);
        searchEditText = (EditText) findViewById(R.id.search_edit);
        searchButton = (ImageView) findViewById(R.id.search_image_searchbutton);
        searchButton.setOnClickListener(this);
        
        hotLabelFragment = new HotLabelFragment();
        allLabelFragment = new AllLabelFragment();
        ArrayList<String> tabNames = new ArrayList<String>();
        tabNames.add("热门");
        tabNames.add("分类");
        viewPagers = new ArrayList<Fragment>();
        viewPagers.add(hotLabelFragment);
        viewPagers.add(allLabelFragment);
        slidePagerView = new SlidePagerView(this, getSupportFragmentManager(), tabNames,
                viewPagers, 0, tabNames.size(), 0);
        slidePagerView.setOnSlidePagerSelectedListener(this);
        View localView = slidePagerView.getSlidePagerView();
        tabsLayout.addView(localView);
        
        InitListener();
    }
    
    @Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
    
    /**
     * @author pengtao
     */
    private void InitListener() {
    	findViewById(R.id.search_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });
    	
    	searchEditText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == KeyEvent.ACTION_UP) {
					if ((keyCode == KeyEvent.KEYCODE_SEARCH) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
						beginSearch();
						return true;
    				}
				}
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if ((keyCode == KeyEvent.KEYCODE_SEARCH) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
						return true;
    				}
				}
				return false;
			}
        	
        });
    }
    
    /**
     * @author pengtao
     */
    private void beginSearch() {
    	String content = searchEditText.getText().toString();
        if (content.trim().equals("")) {
            searchButton.startAnimation(shake);
            searchEditText.startAnimation(shake);
        } else {
            Intent i = new Intent(SearchActivity.this, SearchResultActivity.class);
            i.putExtra("keyword", content);
            i.putExtra("type", "SearchActivity");
            startActivity(i);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_image_searchbutton:
            	beginSearch();
                break;

            default:
                break;
        }
    }
    
    @Override
    public void onSlidePagerSelected(int index, Fragment selectedView) {
        // TODO Auto-generated method stub
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }
}
