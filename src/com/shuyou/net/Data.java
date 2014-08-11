package com.shuyou.net;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3840392843538259677L;
	
	private ArrayList<String> push_msg;
	private ArrayList<BookDetail> book_list;
	private ArrayList<User> user_list;
	private User user;
	private String have_info;
	private String sessionid;
	private String user_id;
	private BookDetail bookDetail;
	private String last_time;
	private String last_zan_count;
	private String last_zan_time;
	private ArrayList<SearchLabel> label_list;
	private String nothing;   //为了避免data为空
	
	public ArrayList<String> getPush_msg() {
		return push_msg;
	}
	public void setPush_msg(ArrayList<String> push_msg) {
		this.push_msg = push_msg;
	}
	public ArrayList<BookDetail> getBook_list() {
		return book_list;
	}
	public void setBook_list(ArrayList<BookDetail> book_list) {
		this.book_list = book_list;
	}

	public String getHave_info() {
		return have_info;
	}
	public void setHave_info(String have_info) {
		this.have_info = have_info;
	}
	public String getSessionid() {
		return sessionid;
	}
	public String getUserid() {
		return user_id;
	}
	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}
	public void setUserid(String userid) {
		this.user_id = userid;
	}
	
	public BookDetail getBookDetail() {
		return bookDetail;
	}
	public void setBookDetail(BookDetail bookDetail) {
		this.bookDetail = bookDetail;
	}
	public String getLast_time() {
		return last_time;
	}
	public void setLast_time(String last_time) {
		this.last_time = last_time;
	}
	public String getLast_zan_count() {
		return last_zan_count;
	}
	public void setLast_zan_count(String last_zan_count) {
		this.last_zan_count = last_zan_count;
	}
	public String getLast_zan_time() {
		return last_zan_time;
	}
	public void setLast_zan_time(String last_zan_time) {
		this.last_zan_time = last_zan_time;
	}
	public ArrayList<User> getUser_list() {
		return user_list;
	}
	public void setUser_list(ArrayList<User> user_list) {
		this.user_list = user_list;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
    public ArrayList<SearchLabel> getLabel_list() {
		return label_list;
	}
	public void setLabel_list(ArrayList<SearchLabel> label_list) {
		this.label_list = label_list;
	}
	public String getNothing() {
		return nothing;
	}
	public void setNothing(String nothing) {
		this.nothing = nothing;
	}
	@Override
	public String toString() {
		return "Data [push_msg=" + push_msg + ", book_list=" + book_list
				+ ", user_list=" + user_list + ", user=" + user
				+ ", have_info=" + have_info + ", sessionid=" + sessionid
				+ ", bookDetail=" + bookDetail + ", last_time=" + last_time
				+ ", last_zan_count=" + last_zan_count + ", last_zan_time="
				+ last_zan_time + ", label_list=" + label_list + ", user_id=" + user_id 
				+ "nothing" + nothing + "]";
	}
	
	
}