package com.yuol.smile.base;

import java.io.Serializable;
import java.util.List;

public class SchoolFellowBase implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String uid;   //用户id
	private String nickname;   //昵称
	private String time;     //发布时间
	private String content;  //说说内容
	private String commentNum;
	public String likeNum;
	public String repostNum;
	public String getRepostNum() {
		return repostNum;
	}

	public void setRepostNum(String repostNum) {
		this.repostNum = repostNum;
	}

	private String uimage;
	private List<String> imgList; 
	public List<String> getImgList() {
		return imgList;
	}

	public void setImgList(List<String> imgList) {
		this.imgList = imgList;
	}

	public String getUimage() {
		return uimage;
	}

	public void setUimage(String uimage) {
		this.uimage = uimage;
	}

	public boolean hasLike;
	
	public SchoolFellowBase(int id, String uid, String nickname,String uimage,String time, String content,List<String> imgList, String commentNum,String likeNum,String repostNum,Boolean hasLike) {
		this.id = id;
		this.uid = uid;
		this.nickname = nickname;
		this.uimage = uimage;
		this.time = time;
		this.content = content;
		this.imgList = imgList;
		this.commentNum = commentNum;
		this.likeNum=likeNum;
		this.repostNum=repostNum;
		this.hasLike=false;
		this.hasLike = hasLike;
	}
	
	public SchoolFellowBase(int id, String uid, String nickname,String uimage,String time, String content,List<String> imgList, String commentNum,String like) {
		this.id = id;
		this.uid = uid;
		this.nickname = nickname;
		this.time = time;
		this.content = content;
		this.commentNum = commentNum;
		this.likeNum=like;
		this.hasLike=false;
	}
	
	public SchoolFellowBase(int id, String uid, String nickname,String uimage,
			String time, String content, String commentNum,String like,boolean haslike) {
		this.id = id;
		this.uid = uid;
		this.nickname = nickname;
		this.uimage = uimage;
		this.time = time;
		this.content = content;
		this.commentNum = commentNum;
		this.likeNum=like;
		this.hasLike=haslike;
	}
	
	public SchoolFellowBase(int parseInt, String string, String string2,
			String string3, String string4, String string5) {
		// TODO Auto-generated constructor stub
	}

	public SchoolFellowBase(int id, String uid, String nickname,String time, String content, String commentNum,String like,boolean haslike) {
		this.id = id;
		this.uid = uid;
		this.nickname = nickname;
		this.time = time;
		this.content = content;
		this.commentNum = commentNum;
		this.likeNum=like;
		this.hasLike=haslike;
	}

	public SchoolFellowBase(int int1, String string, String string2,
			String string3, String string4, String string5, String string6) {
		// TODO Auto-generated constructor stub
	}

	public String getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(String likeNum) {
		this.likeNum = likeNum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(String commentNum) {
		this.commentNum = commentNum;
	}

	public boolean isHasLike() {
		return hasLike;
	}

	public void setHasLike(boolean hasLike) {
		this.hasLike = hasLike;
	}
	
	
	
	
	

}
