package com.shuyou.fragments;

import java.util.ArrayList;

import com.shuyou.R;
import com.shuyou.SearchResultActivity;
import com.shuyou.db.DBService;
import com.shuyou.net.SearchLabel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;

public class AllLabelFragment extends Fragment {

    private View rootView;
    
    private DBService dbService;
    
    private ArrayList<SearchLabel> list;
    
    private String[][] labelNames = new String[6][40];
    private String[][] labelIds = new String[6][40];
    
    private int labelCount[] = new int[6];
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        dbService = DBService.getInstance(getActivity());
        list = dbService.getSearchLabel();

        for(int i = 0; i < list.size(); ++i) {
        	String typeString = list.get(i).getType();
        	int typeInt = Integer.parseInt(typeString);
        	labelNames[typeInt-1][labelCount[typeInt-1]] = list.get(i).getName();
        	labelIds[typeInt-1][labelCount[typeInt-1]] = list.get(i).getId();
        	++labelCount[typeInt-1];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.search_all_label, null);
        return rootView;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
            //设置组视图的显示文字
            private String[] generalsTypes = new String[] { "文学", "流行", "文化", "生活", "经营", "科技" };
            //子视图显示文字
            private String[][] generals = labelNames;
            //自己定义一个获得文字信息的方法
            TextView getTextView() {
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, changeDpToPx(50));
                TextView textView = new TextView(getActivity());
                textView.setLayoutParams(lp);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setPadding(changeDpToPx(80), 0, 0, 0);
                textView.setTextSize(20);
                textView.setTextColor(Color.BLACK);
                return textView;
            }

            
            //重写ExpandableListAdapter中的各个方法
            @Override
            public int getGroupCount() {
                // TODO Auto-generated method stub
                return generalsTypes.length;
            }

            @Override
            public Object getGroup(int groupPosition) {
                // TODO Auto-generated method stub
                return generalsTypes[groupPosition];
            }

            @Override
            public long getGroupId(int groupPosition) {
                // TODO Auto-generated method stub
                return groupPosition;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                // TODO Auto-generated method stub
               // return generals[groupPosition].length;
            	return labelCount[groupPosition];
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                // TODO Auto-generated method stub
                return generals[groupPosition][childPosition];
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                // TODO Auto-generated method stub
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded,
                    View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                LinearLayout ll = new LinearLayout(getActivity());
                ll.setOrientation(0);
                TextView textView = getTextView();
                textView.setTextColor(Color.BLACK);
                textView.setText(getGroup(groupPosition).toString());
                textView.setBackgroundResource(R.drawable.shuyou_all_label_big_press);
                ll.addView(textView);

                return ll;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition,
                    boolean isLastChild, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                LinearLayout ll = new LinearLayout(
                        getActivity());
                ll.setOrientation(0);

                TextView textView = getTextView();
                textView.setText(getChild(groupPosition, childPosition)
                        .toString());
                
                textView.setBackgroundResource(R.drawable.shuyou_all_label_press);
                textView.setPadding(changeDpToPx(50), 0, 0, 0);
                ll.addView(textView);
                
                return ll;
            }

            @Override
            public boolean isChildSelectable(int groupPosition,
                    int childPosition) {
                // TODO Auto-generated method stub
                return true;
            }

        };

        ExpandableListView expandableListView = (ExpandableListView) getActivity().findViewById(R.id.list);
        expandableListView.setAdapter(adapter);
        
        //设置item点击的监听器
        expandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {

                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("type", "AllLabelFragment");
                i.putExtra("labelid", labelIds[groupPosition][childPosition]);
                startActivity(i);

                return false;
            }
        });
	}
	
	private int changeDpToPx(int padding_in_dp) {
		final float scale = getResources().getDisplayMetrics().density;
		int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
		return padding_in_px;
	}
    
}
