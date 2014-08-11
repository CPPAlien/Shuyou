package com.shuyou.net;

import java.io.Serializable;

public class SearchLabel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5903803281517682187L;

	private String id;
	
	private String type;//1:文学。2:流行。3:文化。4:生活。5:经管。6:科技。
	
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "SearchLabel [id=" + id + ", type=" + type + ", name=" + name
				+ "]";
	}
	
	
}
