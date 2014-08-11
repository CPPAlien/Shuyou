package com.shuyou.db;

import java.util.ArrayList;

import com.shuyou.net.BookDetail;
import com.shuyou.net.JsonParser;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences公共类
 * @author jiabin
 * */
public class Preferences {
	public static final int CURRENT_VERSION_CODE = 1;//当前小版本
	
	private static final String Preference_ShuYou = "ShuYouPrefs";

	private SharedPreferences mPrefs;

	private static Preferences m_stInstance;

	private Context m_Context;

	public static synchronized Preferences getInstance(Context context) {
		if (m_stInstance == null)
			m_stInstance = new Preferences(context);

		return m_stInstance;
	}

	private Preferences(Context context) {
		m_Context = context.getApplicationContext();
		doLoadPrefs();
	}

	public void doLoadPrefs() {
		mPrefs = m_Context.getSharedPreferences(Preference_ShuYou, 0);
	}
	
	public void saveLoginInfo(String platform){
		if (mPrefs == null) {
            mPrefs = m_Context.getSharedPreferences(Preference_ShuYou, Context.MODE_PRIVATE);
        }
		Editor editor = mPrefs.edit();
        editor.putString("platform", platform);
        editor.commit();
	}
	
	public String getLoginInfo(){
		if (mPrefs == null) {
            mPrefs = m_Context.getSharedPreferences(Preference_ShuYou, Context.MODE_PRIVATE);
        }
		return mPrefs.getString("platform", "");
	}
	
	public void saveLoginUid(String uid){
		if (mPrefs == null) {
            mPrefs = m_Context.getSharedPreferences(Preference_ShuYou, Context.MODE_PRIVATE);
        }
		Editor editor = mPrefs.edit();
        editor.putString("uid", uid);
        editor.commit();
	}
	
	public String getLoginUid(){
		if (mPrefs == null) {
            mPrefs = m_Context.getSharedPreferences(Preference_ShuYou, Context.MODE_PRIVATE);
        }
		return mPrefs.getString("uid", "");
	}
	
	public void saveHotWords(String hotJsonString)
	{
		if (mPrefs == null) {
            mPrefs = m_Context.getSharedPreferences(Preference_ShuYou, Context.MODE_PRIVATE);
        }
		Editor editor = mPrefs.edit();
        editor.putString("hotJsonString", hotJsonString);
        editor.commit();
	}
	public ArrayList<BookDetail> loadHotWords()
	{
		 if (mPrefs == null) {
			 mPrefs = m_Context.getSharedPreferences(Preference_ShuYou, Context.MODE_PRIVATE);
	     }
		 ArrayList<BookDetail> list = new ArrayList<BookDetail>();
		 String hotJsonString = mPrefs.getString("hotJsonString", null);
		 if(hotJsonString != null)
		 {
			 list = (ArrayList<BookDetail>) JsonParser.parseList(hotJsonString, BookDetail.class);
		 }
		 return list;
	}
	
	/**
	 * 获得用户是否第一次使用应用表示
	 * @return
	 * false 不是第一次使用
	 * true  是第一次使用
	 */
	public boolean getUsedFlag()
	{
		if (mPrefs == null) {
            mPrefs = m_Context.getSharedPreferences(Preference_ShuYou, Context.MODE_PRIVATE);
        }
		return mPrefs.getBoolean("UsedFlag", true);
	}
	
	public void SetUsedFlag() {
		if (mPrefs == null) {
            mPrefs = m_Context.getSharedPreferences(Preference_ShuYou, Context.MODE_PRIVATE);
        }
		Editor editor = mPrefs.edit();
        editor.putBoolean("UsedFlag", false);
        editor.commit();
	}
}
