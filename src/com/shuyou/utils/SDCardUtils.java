package com.shuyou.utils;

import android.os.Environment;

public class SDCardUtils {

	public static String getBookPicsCachePath() {
        return getShuyouCacheDir() + "bookPics/";
    }
	
	public static String getBookDetailInfoCachePath(){
		return getShuyouCacheDir() + "bookInfos/";
	}
	
	/**
     * SdCard cache目录
     * 
     * @return
     */
    public static String getShuyouCacheDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.shuyou/cache/";
    }
    
    public static boolean isExsit() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
