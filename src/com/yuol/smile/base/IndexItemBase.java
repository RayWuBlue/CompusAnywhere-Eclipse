package com.yuol.smile.base;

import java.io.Serializable;

public class IndexItemBase implements Comparable<IndexItemBase>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int INFO_EVENT = 1;
	public static final int INFO_NEWS = 2;
	public static final int INFO_INFROM = 3;
	public static final int INFO_READ = 4;

	private int id;
	private String view;
	private int comment_num;

	private String title;

	private String source;
	private String intro;
	private String time;
	private String cover;
	public IndexItemBase() {}
	
	public IndexItemBase(int id, String title) {
		this.id = id;
		this.title = title;
	}

	public IndexItemBase(int id, String view, String title,String intro, String source,int comment_num,String time,String cover) {
		super();
		this.id = id;
		this.title = title;
		this.intro = intro;
		this.time = time;
		this.view = view;
		this.cover = cover;
		this.source = source;
		this.comment_num = comment_num;
	}

	@Override
	public int compareTo(IndexItemBase item) {
		return time.equals(item.getTime() )? -1 : 1;
	}

	public int getComment_num() {
		return comment_num;
	}

	public String getCover() {
		return cover;
	}

	public int getId() {
		return id;
	}

	public String getIntro() {
		return intro;
	}

	public String getSource() {
		return source;
	}

	public String getTime() {
		return time;
	}


	public String getTitle() {
		return title;
	}

	public String getView() {
		return view;
	}

	public void setComment_num(int comment_num) {
		this.comment_num = comment_num;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}


	public void setId(int id) {
		this.id = id;
	}



	public void setIntro(String intro) {
		this.intro = intro;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setView(String view) {
		this.view = view;
	}




}
