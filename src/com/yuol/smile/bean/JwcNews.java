package com.yuol.smile.bean;

public class JwcNews {
	private String title;
	private String time;
	private String link;
	private String click;
	private String context;

	public JwcNews(String title,String time,String link){
		this.title=title;
		this.time=time;
		this.link=link;
	}
	public JwcNews(){}
	public JwcNews(String title,String time,String link,String click,String context){
		this.time=time;
		this.title=title;
		this.link=link;
		this.click =click;
		this.context=context;

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
