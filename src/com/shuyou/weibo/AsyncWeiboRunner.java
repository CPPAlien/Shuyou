package com.shuyou.weibo;

import java.io.ByteArrayOutputStream;

import android.content.Context;

import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * 异步回调框架类。 TODO：（To be design...）
 * 
 * @author jiabin
 */
public class AsyncWeiboRunner {

	/**
	 * 根据 URL 异步请求数据，并在获取到数据后通过 {@link RequestListener}
	 * 接口进行回调。请注意：该回调函数是运行在后台线程的。 另外，在调用该方法时，成功时，会调用
	 * {@link RequestListener#onComplete}，
	 * {@link RequestListener#onComplete4binary} 并不会被回调，请区分
	 * {@link #request4Binary}。
	 * 
	 * @param url
	 *            服务器地址
	 * @param params
	 *            存放参数的容器
	 * @param httpMethod
	 *            "GET" or "POST"
	 * @param listener
	 *            回调对象
	 */
	public static void request(final String url, final WeiboParameters params,
			final String httpMethod, final RequestListener listener) {

		new Thread() {
			@Override
			public void run() {
				try {
					String resp = HttpManager.openUrl(url, httpMethod, params,
							params.getValue("pic"));
					if (listener != null) {
						listener.onComplete(resp);
					}
				} catch (WeiboException e) {
					if (listener != null) {
						listener.onError(e);
					}
				}
			}
		}.start();

	}

	/**
	 * 根据 URL 异步请求数据，并在获取到数据后通过 {@link RequestListener}
	 * 接口进行回调。请注意：该回调函数是运行在后台线程的。 另外，在调用该方法时，成功时，会调用
	 * {@link RequestListener#onComplete4binary}，
	 * {@link RequestListener#onComplete} 并不会被回调，请区分 {@link #request}。
	 * 
	 * @param url
	 *            服务器地址
	 * @param params
	 *            存放参数的容器
	 * @param httpMethod
	 *            "GET" or "POST"
	 * @param listener
	 *            回调对象
	 */
	public static void request4Binary(final Context context, final String url,
			final WeiboParameters params, final String httpMethod,
			final RequestListener listener) {

		new Thread() {
			@Override
			public void run() {
				try {
					ByteArrayOutputStream resp = HttpManager.openUrl4Binary(
							context, url, httpMethod, params,
							params.getValue("pic"));
					if (listener != null) {
						listener.onComplete4binary(resp);
					}
				} catch (WeiboException e) {
					if (listener != null) {
						listener.onError(e);
					}
				}
			}
		}.start();
	}
}
