package com.yuol.smile.bean;

import java.io.Serializable;

public class YUNewsItem implements Serializable {

	/**
	 * 长大新闻网新闻
	 */
	private static final long serialVersionUID = 367768320660414587L;

	private String type;

	private int id;
	
	private String title;

	private String publishTime;

	private String imageRight;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getImageRight() {
		return imageRight;
	}

	public void setImageRight(String imageRight) {
		this.imageRight = imageRight;
	}
}
