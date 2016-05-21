package com.yuol.smile.bean;

import java.io.Serializable;

public class NewsBean implements Serializable {
   private Integer id;
   private String title;
   private String writer;
   private String time;
   private String click;
   private String content;
   private String image="";
   private String column;
public String getColumn(){
	   return column;
   }
public void setColumn(String column){
	this.column=column;
}
public String getContent() {
	return content;
    }
public void setContent(String content) {
	this.content = content;
}
public NewsBean(){}
public NewsBean(Integer id, String title,  String content, String image,String writer,String column,String time,String click) {
	this.id = id;
	this.title = title;
	this.time=time;	
	this.writer = writer;
	this.click=click;
	this.image=image;
	this.column=column;
	this.content=content;
}
public String getImage() {
	return image;
}
public void setImage(String image) {
	this.image = image;
}
public String getClick() {
	return click;
}
public void setClick(String click) {
	this.click = click;
}
public String getTime() {
	return time;
}
public void setTime(String time) {
	this.time = time;
}
public String getWriter() {
	return writer;
}
public void setWriter(String writer) {
	this.writer = writer;
}
public Integer getId() {
	return id;
}
public void setId(Integer id) {
	this.id = id;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
@Override
public String toString() {
	return "News [id=" + id + ", title=" + title 
			+ ", writer=" + writer + ",+column="+column+" time=" + time + ", click=" + click + "]";
}
 
}
