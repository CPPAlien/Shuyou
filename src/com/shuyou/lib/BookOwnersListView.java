package com.shuyou.lib;

public class BookOwnersListView {
	private String imageUrl;
    private String nickName;
    private String phoneNum;
    private String qq;
    private String wechat;
    private String comment;
    private String sex;

    public BookOwnersListView(String imageUrl, String nickName, 
    		String phoneNum, String qq, String wechat, String comment, String sex) {
        this.imageUrl = imageUrl;
        this.nickName = nickName;
        this.phoneNum = phoneNum;
        this.qq = qq;
        this.wechat = wechat;
        this.comment = comment;
        this.sex = sex;
        
    }
    public String getImageUrl() {
        return imageUrl;
    }
	public String getNickName() {
		return nickName;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public String getQq() {
		return qq;
	}

	public String getWechat() {
		return wechat;
	}

	public String getComment() {
		return comment;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}

    
}
