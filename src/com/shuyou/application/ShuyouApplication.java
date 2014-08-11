package com.shuyou.application;

import android.app.Activity;
import android.app.Application;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.shuyou.utils.FileUtils;
import com.shuyou.utils.SDCardUtils;

public class ShuyouApplication extends Application {

	public LocationClient mLocationClient = null;
	public GeofenceClient mGeofenceClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	public Activity act;
	
	public static int code = 0;
	public static double latitude = 0;
	public static double longitude = 0;
	public static String province = "";
	public static String city = "";
	public static String region = "";
	
	private static ShuyouApplication APP_INSTANCE;
	
	@Override
	public void onCreate() {
		APP_INSTANCE = this;
		mLocationClient = new LocationClient( this );
		/**
		 * 这里的AK和应用签名包名绑定，如果使用在自己的工程中需要替换为自己申请的Key
		 */
		mLocationClient.setAK("98593affa43dd7e3bd449aea1d35e435");
		mLocationClient.registerLocationListener( myListener );
		
		super.onCreate(); 
	}
	
	public static ShuyouApplication getInstance(){
		return APP_INSTANCE;
	}
	
	public void onDestory(){
		FileUtils.deleteExpiredImage(SDCardUtils.getBookPicsCachePath(), 300);//维持bookpic文件夹300张图片上限
	}
	
	/**
	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return ;}
			
			code = location.getLocType();
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				/**
				 * 地址信息
				 */
				province = location.getProvince();
				city = location.getCity();
				region = location.getDistrict();
			}
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ; 
			}
		}
	}
	
}