package com.yuol.smile.bean;

public class JobNews {
	private String title;
	private String time;
	private String link;
	private String click;
	private String context;
	private String cloumn;
	public JobNews(String title,String time,String link,String click){
		this.title=title;
		this.time=time;
		this.link=link;
		this.click=click;
	}
	public JobNews(){}
	public JobNews(String title,String time,String link,String click,String context,String cloumn){
		this.time=time;
		this.title=title;
		this.link=link;
		this.click =click;
		this.context=context;
		this.cloumn=cloumn;
	}
	public String getCloumn() {
		return cloumn;
	}
	public void setCloumn(String cloumn) {
		this.cloumn = cloumn;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
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
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getClick() {
		return click;
	}
	public void setClick(String click) {
		this.click = click;
	}
	
}
