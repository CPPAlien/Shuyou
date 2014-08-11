package com.shuyou.values;

import android.R.integer;

public class Values {
	
	private static boolean isOnlineSwitch = true;//线上库和测试库切换开关，true为线上库
	
	private final static String OnlineSite = "http://shuyou.duapp.com/";
	
	private final static String TestSite = "http://bookpal.duapp.com/";
	
	private static final String DOMAIN = isOnlineSwitch ? OnlineSite : TestSite;
	
	public final static String PICSAVEPATH = "http://bcs.duapp.com/haveadate-head-pic/";
	public final static String PICSAVEFORMAT = ".jpg";
	
	public final static String MALE   = "男";
	public final static String FEMALE = "女";
	
	public final static int MIN_LINE_DISPLAY = 4;
	
	/*备注最大字数*/
	public final static int MAX_TEXT_COUNT = 140;
	
	public final static int NETWORK_ERROR = 100;
	public final static int LOCATION_ERROR = 101;
	
	/* 头像名称 */
	public static final String IMAGE_FILE_NAME = "faceImage.jpg";

	/* 请求码 */
	public static final int IMAGE_REQUEST_CODE = 0;
	public static final int CAMERA_REQUEST_CODE = 1;
	public static final int RESULT_REQUEST_CODE = 2;
	
	/*LoginActivity*/
	public static final String LOGIN_URL = DOMAIN + "api/Login/android_login";
	public final static int FAIL_TO_LOGIN = 0;
	public final static int SUCCESS_TO_LOGIN = 1;
	public final static int NEED_TO_REGISTER = 2;
	public final static int NEED_TO_REGISTER_QQ = 3;
	public final static int NEED_TO_REGISTER_SINA = 4;
	public final static String HOTWORDS_URL = DOMAIN + "api/Search/getHotWords";
	public final static String SEARCHLABEL_URL = DOMAIN + "api/Search/getLabels";
	
	/*RegisterActivity*/
	public static final String PERSONAL_INFO_URL = DOMAIN + "api/Login/updateUserInfo";
	public final static int FAIL_TO_REGISTER = 0;
	public final static int SUCCESS_TO_REGISTER = 1;
	public final static int SUCCESS_TO_GET_USERINFO = 2;
	public final static int FAIL_TO_GET_USERINFO = 3;
	
	/*UploadPicture*/
	public static final String UPLOAD_PIC_URL = DOMAIN + "api/User/updateHeadPic";
	public final static int SUCCESS_TO_UPLOAD_PIC = 4;
	public final static int FAIL_TO_UPLOAD_PIC = 5;
	
	/*UserInfoActivity*/
	public static final String GET_USERINFO_URL = DOMAIN + "api/User/getUserInfo";
	public final static int SUCCESS_GET_USERINFO = 1;
	public final static int FAIL_GET_USERINFO = 0;
	
	/*TopShuyouTab*/
	public static final String GET_INDEX_BOOK = DOMAIN + "api/User/getIndexBook";
	
	/*TopHotShuyouTab*/
	public final static int HOT_LOADING_SUCCESS = 1;
	public final static int HOT_LOADING_FAIL = 2;
	
	/*TopNewShuyouTab*/
	public final static int NEW_NEXT_LOADING_SUCCESS = 0;
	public final static int NEW_LOADING_FAIL = 1;
	
	/*BookInfoActivity*/
	public final static String BOOK_INDEX_DETAIL = DOMAIN + "api/Book/indexDetail";
	public final static int LOAD_BOOK_INFO_SUCCESS = 0;
	public final static int LOAD_BOOK_INFO_FAIL = 1;
	
	public final static int FAIL_TO_UPLOAD = 2;
	public final static int SUCCESS_TO_UPLOAD = 3;


	
	/*BarCodeScanResultActivity*/
	public static final String UPLOAD_BOOK_URL = DOMAIN + "api/Book/add";
	public static final String BORROW_BOOK_URL = DOMAIN + "api/Book/borrow";
	public static final String RETURN_BOOK_URL = DOMAIN + "api/Book/return";
	public static final String GET_BOOK_DETAIL_URL = DOMAIN + "api/Book/preview";
	
	public final static int SUCCESS_TO_GET = 10;
	public final static int FAIL_TO_GET = 11;
	public final static int SUCCESS_TO_BORROW = 20;
	public final static int FAIL_TO_BORROW = 21;
	public final static int SUCCESS_TO_RETURN = 30;
	public final static int FAIL_TO_RETURN = 31;
	
	/*MyBookShelf*/
	public static final String MY_BOOKSHELF_URL = DOMAIN + "api/User/getMyRelateBook";
	public final static int FAIL_TO_GETBOOKS = 0;
	public final static int SUCCESS_TO_GETBOOKS = 1;
	
	/*BookshelfDetailActivity*/
	public static final String BOOKSHELF_DETAIL_URL = DOMAIN + "api/Book/shelfDetail";
	public final static int FAIL_TO_GETDETAIL = 0;
	public final static int SUCCESS_TO_GETDETAIL = 1;

	public static final String ZAN_API = DOMAIN + "api/Book/zanBook";
	public final static int ZAN_SUCCESS = 2;
	public final static int ZAN_ALREADY = 3;
	public final static int ZAN_FAILED = 4;
	
	public static final String XIAJIA_API = DOMAIN + "api/Book/delete";
	public final static int XIAJIA_SUCCESS = 5;
	public final static int XIAJIA_FAILED = 6;
	
	/*EditCommentActivity*/
	public final static String UPDATE_COMMENT_API = DOMAIN + "api/Book/updateBook";
	public final static int SUCCESS_TO_UPDATE = 1;
	public final static int FAIL_TO_UPDATE = 0;
	
	/*SearchResultAcitivity*/
	public final static String SEARCH_URL = DOMAIN + "api/Search/getSearch";
	public final static int SUCCESS_TO_SEARCH = 1;
	public final static int FAIL_TO_SEARCH = 0;
	
	/*SearchResultAcitivity*/
	public final static String FEEDBACK_URL = DOMAIN + "api/Feedback/feedback";
	public final static int SUCCESS_TO_FEEDBACK = 1;
	public final static int FAIL_TO_FEEDBACK = 0;
	
	public final static String SEARCH_BY_LABEL_URL = DOMAIN + "api/Search/getLabelInfo";
	
	/*BookOwnersActivity*/
	public static final String GET_OWNERS_URL = DOMAIN + "api/Book/isbnUsers";
	public final static int FAIL_TO_LOADOWNERS = 0;
	public final static int SUCCESS_TO_LOADOWNERS = 1;
}
