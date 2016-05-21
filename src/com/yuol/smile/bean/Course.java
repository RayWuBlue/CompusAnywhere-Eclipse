package com.yuol.smile.bean;
public class Course {
	private String time;
	private String address;
	private String teacher;
	private String name;
	private boolean Single;
	public boolean isSingle() {
		return Single;
	}
	public void setSingle(boolean single) {
		Single = single;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Course(){}
	public Course(String name,String teacher,String time,String address){
		this.name=name;
		this.time=time;
		this.teacher=teacher;
		this.address=address;
	}
	
}
