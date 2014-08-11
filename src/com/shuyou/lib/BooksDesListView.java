package com.shuyou.lib;

public class BooksDesListView {
	private String imageUrl;
    private String bookName;
    private String authorName;
    private String label;
    private String number;

    public BooksDesListView(String imageUrl, String bookName, String authorName, String label, String number) {
        this.imageUrl = imageUrl;
        this.bookName = bookName;
        this.authorName = authorName;
        this.label  = label;
        this.number = number;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getBookName() {
        return bookName;
    }
    public String getAuthorName() {
    	return authorName;
    }
    public String getLabel() {
    	return label;
    }
    public String getNumber() {
    	return number;
    }
}
