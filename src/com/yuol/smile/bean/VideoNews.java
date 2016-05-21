package com.yuol.smile.bean;

import java.io.Serializable;

/*
 * 城市实体类
 */
public class VideoNews implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2005295701925847160L;
	
	private int id;
	
	private String title;
	
	private String time;
	
	private String click;
	
	private String date;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getClick() {
		return click;
	}
	public void setClick(String click) {
		this.click = click;
	}
}
