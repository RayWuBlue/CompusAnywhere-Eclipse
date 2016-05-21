package com.yuol.smile.bean;

public class Yelpag {
	private int id;
	private String position;
	private String department;
	private String tel;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public Yelpag(){
		
	}
	public Yelpag(int id, String position, String department, String tel) {
		super();
		this.id = id;
		this.position = position;
		this.department = department;
		this.tel = tel;
	}
	

}
