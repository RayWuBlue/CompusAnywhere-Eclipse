package com.yuol.smile.bean;

public class Score {
	private String s_name;
	private String s_num;
	private String s_credit;
	private String s_year;
	private String s_term;
	private String s_type;
	public String getS_name() {
		return s_name;
	}
	public void setS_name(String s_name) {
		this.s_name = s_name;
	}
	public String getS_num() {
		return s_num;
	}
	public void setS_num(String s_num) {
		this.s_num = s_num;
	}
	public String getS_credit() {
		return s_credit;
	}
	public void setS_credit(String s_credit) {
		this.s_credit = s_credit;
	}
	public String getS_year() {
		return s_year;
	}
	public void setS_year(String s_year) {
		this.s_year = s_year;
	}
	public String getS_term() {
		return s_term;
	}
	public void setS_term(String s_term) {
		this.s_term = s_term;
	}
	public String getS_type() {
		return s_type;
	}
	public void setS_type(String s_type) {
		this.s_type = s_type;
	}
	public Score(){};
	public Score(String s_name,String s_num,String s_credit,String s_year,String s_term,String s_type){
		this.s_name=s_name;
		this.s_num=s_num;
		this.s_credit=s_credit;
		this.s_year=s_year;
		this.s_type=s_type;
		this.s_term=s_term;
	}

}
