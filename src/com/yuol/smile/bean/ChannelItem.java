package com.yuol.smile.bean;

import java.io.Serializable;

/** 
 * ITEM的对应可序化队列属性
 *  */
public class ChannelItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6465237897027410019L;

	/** 
	 * 栏目对应ID
	 *  */
	public Integer id;

	public int coverRes;
	/** 
	 * 栏目对应NAME
	 *  */
	public String name;
	
	/** 
	 * 栏目在整体中的排序顺序  rank
	 *  */
	public Integer orderId;
	/** 
	 * 栏目是否选中
	 *  */
	private Integer selected;
	public  Class<?> clazz;
	public ChannelItem() {
	}
	
	public ChannelItem(int id,int coverRes, String name, int orderId, int selected) {
		this.id = Integer.valueOf(id);
		this.coverRes = coverRes;
		this.name = name;
		this.orderId = Integer.valueOf(orderId);
		this.selected = this.orderId = Integer.valueOf(selected);
	}

	public ChannelItem(int id, int coverRes,String name, int orderId, int selected, Class<?> clazz) {
		this.id = Integer.valueOf(id);
		this.coverRes = coverRes;
		this.name = name;
		this.orderId = Integer.valueOf(orderId);
		this.selected = this.orderId = Integer.valueOf(selected);
		this.clazz = clazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public int getCoverRes() {
		return coverRes;
	}

	public int getId() {
		return this.id.intValue();
	}

	public String getName() {
		return this.name;
	}

	public int getOrderId() {
		return this.orderId.intValue();
	}

	public Integer getSelected() {
		return selected;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public void setCoverRes(int coverRes) {
		this.coverRes = coverRes;
	}

	public void setId(int paramInt) {
		this.id = Integer.valueOf(paramInt);
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String paramString) {
		this.name = paramString;
	}


	public void setOrderId(int paramInt) {
		this.orderId = Integer.valueOf(paramInt);
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public void setSelected(Integer selected) {
		this.selected = selected;
	}

	public String toString() {
		return "ChannelItem [id=" + this.id + ", name=" + this.name
				+ ", selected=" + this.clazz + "]";
	}
}