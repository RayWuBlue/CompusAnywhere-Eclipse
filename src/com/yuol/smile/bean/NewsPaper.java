package com.yuol.smile.bean;
public class NewsPaper {
	private String issue_num;
	private String issue_time;
	private String url;
	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl_detail() {
		return url_detail;
	}
	public void setUrl_detail(String url_detail) {
		this.url_detail = url_detail;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	private String url_detail;
	private String content;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIssue_num() {
		return issue_num;
	}
	public void setIssue_num(String issue_num) {
		this.issue_num = issue_num;
	}
	public String getIssue_time() {
		return issue_time;
	}
	public void setIssue_time(String issue_time) {
		this.issue_time = issue_time;
	}
	public NewsPaper(){};
	public NewsPaper(String num,String time,String url){
		this.issue_num=num;
		this.issue_time=time;
		this.url=url;
	}
	public NewsPaper(String title,String url_detail){
		this.title=title;
		this.url_detail=url_detail;
		
	}

}
