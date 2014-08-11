package com.shuyou.lib.internal;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.alibaba.fastjson.JSONException;
import com.shuyou.net.Data;
import com.shuyou.net.JsonParser;
import com.shuyou.net.ServerInfo;
import com.shuyou.net.StaticHttpClient;
import com.shuyou.utils.LogHelper;
import com.shuyou.values.Values;
/**
 *  
 * @author Chris
 *
 */
public class ChangeToEnum {
	public enum EnumCode{
		/************系统级返回码*************/
		//操作成功
		OPERATE_SUCCESS("10000"),
		//操作失败
		OPERATE_FAIL("10001"),
		//参数错误
		PARAMS_ERROR("10002"),
		//服务器内部错误
		SYSTEM_ERROR("10003"),
		//无效的session
		INVALID_SESSION("10004"),
		
		/**********模块返回码，业务逻辑相关***********/
		//已经绑定过
		ALREADY_BIND("20001"),
		//没有找到该书
		NOT_FOUND("20002"),
		//已经添加过
		ALREADY_ADD("20003"),
		//登录后获取图书列表失败
		GET_BOOK_FAIL_AFTER_LOGIN("20004"),
		//登录后获取推送列表失败
		GET_PUSH_FAIL_AFTER_LOGIN("20005"),
		//未授权
		INVALID_TOKEN("20006"),
		//修改图书时 书不是当前用户所拥有
		NOT_YOUR_BOOK("20007"),
		//手机格式错误
		TEL_FORMAT_ERROR("20008"),
		//已经注
		ALREADY_REGISTER("20009"),
		//未注册
		NOT_REGISTER("20010"),
		
		/********** Controllers_Book类的错误码**********/
		//没有找到该书
		BOOK_NOT_FOUND("30001"),
		//用户已经上传过该书
		BOOK_ON_LINE("30002"),
		//二维码信息错误
		TWO_CODE_ERROR("30003"),
		//用户自己上传的书
		BOOK_OWN("30004"),
		//该书已经借出
		BOOK_LENT("30005"),
		//该用户没有借入此书
		BORROWED_OTHER("30006"),
		//该用户没有借出此书
		LENT_OTHER("30007"),
		//没有该书的借出记录
		BOOK_NOT_BORROWED("30008"),
		//该用户已赞过某借入的书
		ZAN_ALREADY("30009"),
		//此书不是该用户上传
		BOOK_OTHER("30010"),
		//借入的书超过借出的两本
		BORROW_NO_MORE("30011"),
		//借入的书超过八本
		BORROW_TOO_MANY("30012"),
		//添加想看的书已超过30本
		WANT_TOO_MANY("30013"),
		//上传的书超过30本
		ADD_TOO_MANY("30014"),
		
		/****************网络错误*************************/
		NETWORK_ERROR("40000");
		
		
		private String value = null;
		
		EnumCode(String value) {
			this.value = value;
		}
		
		private String getValue() {
			return this.value;
		}
	}
	
	private String url = null;
	private String tag = null;
	private Data   data = null;
	private File file = null;
	/*设置一个日志打印表示*/
	public ChangeToEnum(String tag) {
		this.tag = tag;
	}
	/*如果有使用上传文件，则需调用此接口传入需上传得文件*/
	public void SaveFile(File file) {
		this.file = file;
	}
	/**
	 * 与服务端交互
	 * @param whichWay
	 * @param url
	 * @param map
	 * @return
	 */
	public EnumCode getUrl(String whichWay, String url, Map<String, String> map) {
		this.url = url;
		String strResult = null;
		ServerInfo si = null;
		try {
			if (whichWay.equals("post")) {
				strResult = StaticHttpClient.doPost(
						url, map);
				if (null == strResult) {
					LogHelper.v(this.tag, this.url+":"+"null == strResult");
					return EnumCode.NETWORK_ERROR;
				}
			} else if(whichWay.equals("get")) {
				strResult = StaticHttpClient.doGet(
						url, map);
				if (null == strResult) {
					LogHelper.v(this.tag, this.url+":"+"null == strResult");
					return EnumCode.NETWORK_ERROR;
				}			
			} else if(whichWay.equals("uploadfile")) {
				strResult = StaticHttpClient.UploadPicToServer(Values.UPLOAD_PIC_URL, map, file);
				if (null == strResult) {
					LogHelper.v(this.tag, this.url+":"+"null == strResult");
					return EnumCode.NETWORK_ERROR;
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogHelper.v(this.tag, this.url+":"+"ClientProtocolException");
			return EnumCode.NETWORK_ERROR;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogHelper.v(this.tag, this.url+":"+"IOException");
			return EnumCode.NETWORK_ERROR;
		}
		try {
			si = (ServerInfo) JsonParser.parseObject(strResult,
				ServerInfo.class);
		} catch (JSONException e) {
			e.printStackTrace();
			LogHelper.v(this.tag, this.url+":"+"JSONException");
			return EnumCode.NETWORK_ERROR;
		}
		LogHelper.i(this.tag, this.url+":"+si);
		if (null == si) {
			LogHelper.v(this.tag, this.url+":"+"null == si");
			return EnumCode.NETWORK_ERROR;
		}
		String status = si.getStatus();
		if (null == status) {
			LogHelper.v(this.tag, this.url+":"+"null == status");
			return EnumCode.NETWORK_ERROR;
		}
		Data data = si.getData();
		if(null == data) {
			LogHelper.v(this.tag, this.url+":"+"null == data");
			return EnumCode.NETWORK_ERROR;
		}
		this.data = data;
		return DealStatus(status);
	}
	
	public Data getData() {
		return this.data;
	}
	/**
	 * 根据返回吗打印日志，并返回相应的enum值
	 * @param status
	 * @return
	 */
	private EnumCode DealStatus(String status) {
		for(EnumCode cte : EnumSet.allOf(EnumCode.class)) {
			if (status.equals(cte.getValue())) {
				LogHelper.v(this.tag, this.url+":"+cte.getValue());
				return cte;
			}
		}
		return EnumCode.NETWORK_ERROR;
	}
}
