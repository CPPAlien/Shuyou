package com.shuyou.weibo;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * 该类提供了授权回收接口，帮助开发者主动取消用户的授权。
 * @see <a href="http://open.weibo.com/wiki/Oauth2/revokeoauth2">授权回收</a>
 * 
 * @author jiabin
 */
public class LogoutAPI extends AbsOpenAPI {

	private static final String TAG = LogoutAPI.class.getName();

    /** 注销地址（URL） */
    private static final String REVOKE_OAUTH_URL = "https://api.weibo.com/oauth2/revokeoauth2";
    
    /**
     * 构造函数。
     * 
     * @param oauth2AccessToken Token 实例
     */
    public LogoutAPI(Oauth2AccessToken oauth2AccessToken) {
        super(oauth2AccessToken);
    }

    /**
     * 异步取消用户的授权。
     * 
     * @param listener 请求后的回调接口
     */
    public void logout(RequestListener listener) {
        if (mAccessToken != null && mAccessToken.isSessionValid() && listener != null) {
            WeiboParameters params = new WeiboParameters();
            params.add("access_token", mAccessToken.getToken());
            request(REVOKE_OAUTH_URL, params, HTTPMETHOD_POST, listener);
        } else {
            LogUtil.e(TAG, "Logout args error!");
        }
    }
}
