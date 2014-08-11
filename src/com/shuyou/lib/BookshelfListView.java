package com.shuyou.lib;

public class BookshelfListView {
	private String imageUrlLogo1;
	private String imageUrlLogo2;
	private String imageUrlLogo3;

    public BookshelfListView(String imageUrl1, String imageUrl2, String imageUrl3) {
        this.imageUrlLogo1 = imageUrl1;
        this.imageUrlLogo2 = imageUrl2;
        this.imageUrlLogo3 = imageUrl3;
    }
    public String GetImageUrlLogo1() {
    	return imageUrlLogo1;
    }
    public String GetImageUrlLogo2() {
    	return imageUrlLogo2;
    }
    public String GetImageUrlLogo3() {
    	return imageUrlLogo3;
    }
}
