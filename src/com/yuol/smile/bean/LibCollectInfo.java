package com.yuol.smile.bean;

public class LibCollectInfo {
	private String barCode;
	private String address;
	private String type;
	private String statu;
    private String returnDate;
    private String explain;
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
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
	public String getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}
	public String getExplain() {
		return explain;
	}
	public void setExplain(String explain) {
		this.explain = explain;
	}
	public LibCollectInfo() {
		// TODO 自动生成的构造函数存�?
	}
	public LibCollectInfo(String barCode, String address, String type,
			String statu, String returnDate, String explain) {
		super();
		this.barCode = barCode;
		this.address = address;
		this.type = type;
		this.statu = statu;
		this.returnDate = returnDate;
		this.explain = explain;
	}

}
