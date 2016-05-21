package com.yuol.smile.bean;

public class ImageNews {
	private String newsTitle;
	private String imgUrl;
	private String newsId;

	public String getNewsId() {
		return newsId;
	}

	public void setNewsId(String newsId) {
		this.newsId = newsId;
	}

	public ImageNews(String newsId,String newsTitle, String imgUrl) {
		this.newsId = newsId;
		this.newsTitle = newsTitle;
		this.imgUrl = imgUrl;
	}

	public String getNewsTitle() {
		return newsTitle;
	}

	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

}
