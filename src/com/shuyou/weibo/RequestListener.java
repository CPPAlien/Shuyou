package com.shuyou.weibo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.sina.weibo.sdk.exception.WeiboException;

/**
 * 发起访问请求时所需的回调接口。 TODO：（To be design...）
 * 
 * @author jiabin
 */
public interface RequestListener {
	

	/**
	 * 当获取服务器返回的字符串后，该函数被调用。
	 * 
	 * @param response
	 *            服务器返回的字符串
	 */
	public void onComplete(String response);

	/**
	 * 当获取服务器返回的文件流后，该函数被调用。
	 * 
	 * @param responseOS
	 *            服务器返回的文件流
	 */
	public void onComplete4binary(ByteArrayOutputStream responseOS);

	/**
	 * 当访问服务器时，发生 I/O 异常时，该函数被调用。
	 * 
	 * @param e
	 *            I/O 异常对象
	 */
	public void onIOException(IOException e);

	/**
	 * 当访问服务器时，其它异常时，该函数被调用。
	 * 
	 * @param e
	 *            微博自定义异常对象
	 */
	public void onError(WeiboException e);
}
