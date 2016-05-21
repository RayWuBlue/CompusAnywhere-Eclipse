package com.yuol.smile.bean;

public class LibBook {
	private String title;
	private String author;
	private String publishing;
	private String page;
	private String price;
	private String callNum;
	private String url;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getPublishing() {
		return publishing;
	}
	public void setPublishing(String publishing) {
		this.publishing = publishing;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getCallNum() {
		return callNum;
	}
	public void setCallNum(String callNum) {
		this.callNum = callNum;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public LibBook(){}
	public LibBook(String title,String author,String publishing,String page,String price,String callNum,String url){
		this.title=title;
		this.author=author;
		this.publishing=publishing;
		this.page=page;
		this.price=price;
		this.callNum=callNum;
		this.url=url;
	}

}
