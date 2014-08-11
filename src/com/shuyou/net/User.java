package com.shuyou.net;

import java.io.Serializable;

public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userid;
	private String comment;
	private String nick_name;
	private String sex;
	private String head_pic;
	private String tel;
	private String qq;
	private String mic_msg;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public String getHead_pic() {
		return head_pic;
	}
	public void setHead_pic(String head_pic) {
		this.head_pic = head_pic;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getMic_msg() {
		return mic_msg;
	}
	public void setMic_msg(String mic_msg) {
		this.mic_msg = mic_msg;
	}
    @Override
    public String toString() {
        return "User [userid=" + userid + ", comment=" + comment + ", nick_name=" + nick_name
                + ", sex=" + sex + ", head_pic=" + head_pic + ", tel=" + tel + ", qq=" + qq
                + ", mic_msg=" + mic_msg + "]";
    }
	
	
}
