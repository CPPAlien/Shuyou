package com.shuyou.net;

import java.io.Serializable;

public class BookDetail implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7009164848547937787L;
	private String isbn;
	private String zan_count;
	private String book_link;
	private String book_name;
	private String refresh_time;
	private String score;
	private String label;
	private String author;
	private String profile;
	private String publisher;
	private String publish_date;
	private String status;
	private String bookid;
	private String add_time;
	private String comment;
	private String userid;
	private String nick_name;
	private String time;
	private String limit_time;
	private String aready_zan;
	private String left_days;
	private String current_available_count;
	private String code_secret;
	private String borrow_userid;
	
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getZan_count() {
		return zan_count;
	}
	public void setZan_count(String zan_count) {
		this.zan_count = zan_count;
	}
	public String getBook_link() {
		return book_link;
	}
	public void setBook_link(String book_link) {
		this.book_link = book_link;
	}
	public String getBook_name() {
		return book_name;
	}
	public void setBook_name(String book_name) {
		this.book_name = book_name;
	}
	public String getRefresh_time() {
		return refresh_time;
	}
	public void setRefresh_time(String refresh_time) {
		this.refresh_time = refresh_time;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getPublish_date() {
		return publish_date;
	}
	public void setPublish_date(String publish_date) {
		this.publish_date = publish_date;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBookid() {
		return bookid;
	}
	public void setBookid(String bookid) {
		this.bookid = bookid;
	}
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getLimit_time() {
		return limit_time;
	}
	public void setLimit_time(String limit_time) {
		this.limit_time = limit_time;
	}
	public String getAready_zan() {
		return aready_zan;
	}
	public void setAready_zan(String aready_zan) {
		this.aready_zan = aready_zan;
	}
	public String getLeft_days() {
		return left_days;
	}
	public void setLeft_days(String left_days) {
		this.left_days = left_days;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getCurrent_available_count() {
		return current_available_count;
	}
	public void setCurrent_available_count(String current_available_count) {
		this.current_available_count = current_available_count;
	}
	public String getCode_secret() {
		return code_secret;
	}
	public void setCode_secret(String code_secret) {
		this.code_secret = code_secret;
	}
	public String getBorrow_userid() {
		return borrow_userid;
	}
	public void setBorrow_userid(String borrow_userid) {
		this.borrow_userid = borrow_userid;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
    @Override
    public String toString() {
        return "BookDetail [isbn=" + isbn + ", zan_count=" + zan_count + ", book_link=" + book_link
                + ", book_name=" + book_name + ", refresh_time=" + refresh_time + ", score="
                + score + ", label=" + label + ", author=" + author + ", profile=" + profile
                + ", publisher=" + publisher + ", publish_date=" + publish_date + ", status="
                + status + ", bookid=" + bookid + ", add_time=" + add_time + ", comment=" + comment
                + ", userid=" + userid + ", nick_name=" + nick_name + ", time=" + time
                + ", limit_time=" + limit_time + ", aready_zan=" + aready_zan + ", left_days="
                + left_days + ", current_available_count=" + current_available_count
                + ", code_secret=" + code_secret + ", borrow_userid=" + borrow_userid + "]";
    }
	
}