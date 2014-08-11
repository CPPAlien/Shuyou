package com.shuyou.weibo;

import com.sina.weibo.sdk.exception.WeiboException;

/**
 * 通过 OpenAPI 进行 HTTP 请求时，如果发生异常，则以该异常进行抛出。
 * 
 * @author jiabin
 * TODO: （To be design...）
 */
public class WeiboHttpException extends WeiboException {

private static final long serialVersionUID = 1L;
    
    /** HTTP请求出错时，服务器返回的错误状态码 */
    private final int mStatusCode;

    /**
     * 构造函数。
     * 
     * @param message    HTTP请求出错时，服务器返回的字符串
     * @param statusCode HTTP请求出错时，服务器返回的错误状态码
     */
    public WeiboHttpException(String message, int statusCode) {
        super(message);
        mStatusCode = statusCode;
    }
    
    /**
     * HTTP请求出错时，服务器返回的错误状态码。
     * 
     * @return 服务器返回的错误状态码
     */
    public int getStatusCode() {
        return mStatusCode;
    }
}
