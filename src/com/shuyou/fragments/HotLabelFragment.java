package com.shuyou.fragments;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shuyou.R;
import com.shuyou.db.DBService;
import com.shuyou.net.BookDetail;
import com.shuyou.view.KeywordsFlow;

public class HotLabelFragment extends Fragment implements OnClickListener{

	private DBService dbService;
    private View rootView;
    private LinearLayout changeBtn;
    private KeywordsFlow keywordsFlow;
    private ArrayList<BookDetail> list;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //keywordsList = Arrays.asList(keywords);
        dbService = DBService.getInstance(getActivity());
        list = dbService.getHotWords();
        numList = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.search_hot_label, null);
        changeBtn = (LinearLayout)rootView.findViewById(R.id.search_hot_changeWordBtn);
        changeBtn.setOnClickListener(this);

        keywordsFlow = (KeywordsFlow)rootView.findViewById(R.id.keywordFlow_framelayout);
        keywordsFlow.SetActivity(this.getActivity());
        keywordsFlow.setDuration(800);
        keywordsFlow.setOnItemClickListener(this);
        feedKeywordsFlow(keywordsFlow, list);
        keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
        return rootView;
    }
    
    private void feedKeywordsFlow(KeywordsFlow keywordsFlow , ArrayList<BookDetail> list)
    {
    	if(list.isEmpty())
    	{
    		return;
    	}
    	Random random = new Random();  
    	int listSize = list.size() < KeywordsFlow.MAX ? list.size() : KeywordsFlow.MAX;
    	
        for (int i = 0; i < listSize; i++) {  
            int ran = random.nextInt(list.size());
            while(isNumRepeat(ran))
            {
            	ran = random.nextInt(list.size());
            }
            String name = list.get(ran).getBook_name();  
            String isbn = list.get(ran).getIsbn();
            
            keywordsFlow.feedKeyword(name, isbn);
        }
        numList.clear();
    }
    private ArrayList<String> numList;//防止重复出现
    private boolean isNumRepeat(int num){
    	if(numList.contains(num+"")){
    		return true;
    	}
    	else
    	{
    		numList.add(num+"");
    		return false;
    	}
    }

	@Override
	public void onClick(View v) {
		
		switch(v.getId()) {
		case R.id.search_hot_changeWordBtn:
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(getActivity().findViewById(R.id.search_edit).getWindowToken(), 0);
			keywordsFlow.rubKeywords();
			feedKeywordsFlow(keywordsFlow, list);
			keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
			break;
		case R.id.keywordFlow_framelayout:
			
			break;
		default:
			break;	
		}
		
		
	}

    
}
