package com.yuol.smile.bean;

public class LibLoanBook {
    private String title;
    private String author;
    private String ask_num;
    private String bar_num;
    private String address;
    private String type;
    private String statu;
    private String load_num;
    private String load_time;
    private String return_time;
    private String v_barno;
    public String getV_barno() {
		return v_barno;
	}
	public void setV_barno(String v_barno) {
		this.v_barno = v_barno;
	}
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
	public String getAsk_num() {
		return ask_num;
	}
	public void setAsk_num(String ask_num) {
		this.ask_num = ask_num;
	}
	public String getBar_num() {
		return bar_num;
	}
	public void setBar_num(String bar_num) {
		this.bar_num = bar_num;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatu() {
		return statu;
	}
	public void setStatu(String statu) {
		this.statu = statu;
	}
	public String getLoad_num() {
		return load_num;
	}
	public void setLoad_num(String load_num) {
		this.load_num = load_num;
	}
	public String getLoad_time() {
		return load_time;
	}
	public void setLoad_time(String load_time) {
		this.load_time = load_time;
	}
	public String getReturn_time() {
		return return_time;
	}
	public void setReturn_time(String return_time) {
		this.return_time = return_time;
	}
	
	public LibLoanBook() {	}
	public LibLoanBook(String title, String author, String ask_num,
			String bar_num, String address, String type, String statu,
			String load_num, String load_time, String return_time,String v_barno) {
		this.title = title;
		this.author = author;
		this.ask_num = ask_num;
		this.bar_num = bar_num;
		this.address = address;
		this.type = type;
		this.statu = statu;
		this.load_num = load_num;
		this.load_time = load_time;
		this.return_time = return_time;
		this.v_barno=v_barno;
	}
    
}
