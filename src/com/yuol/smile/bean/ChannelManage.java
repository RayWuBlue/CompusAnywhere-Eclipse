package com.yuol.smile.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.database.SQLException;
import android.util.Log;

import com.yuol.smile.CourseSelect;
import com.yuol.smile.JobActivity;
import com.yuol.smile.JwcActivity;
import com.yuol.smile.JwcLoginActivity;
import com.yuol.smile.KbDetail;
import com.yuol.smile.LibBookQuery;
import com.yuol.smile.NewsPaperActivity;
import com.yuol.smile.PhoneNumberActivity;
import com.yuol.smile.R;
import com.yuol.smile.RadioActivity;
import com.yuol.smile.VideoNewsActivity;
import com.yuol.smile.activity.Cet;
import com.yuol.smile.activity.YangtzeNews;
import com.yuol.smile.db.ChannelDao;
import com.yuol.smile.db.SQLHelper;

public class ChannelManage {
	public static ChannelManage channelManage;
	/**
	 * 默认的用户选择频道列表
	 * */
	public static List<ChannelItem> defaultUserChannels;
	/**
	 * 默认的其他频道列表
	 * */
	public static List<ChannelItem> smileNewsChannels;
	private ChannelDao channelDao;
	/** 判断数据库中是否存在用户数据 */
	private boolean userExist = false;
	static {
		defaultUserChannels = new ArrayList<ChannelItem>();
		smileNewsChannels = new ArrayList<ChannelItem>();

		defaultUserChannels.add(new ChannelItem(0,R.drawable.yu_news, "长大新闻",1, 1,YangtzeNews.class));
		defaultUserChannels.add(new ChannelItem(0,R.drawable.job, "就业招聘",1, 2,JobActivity.class));
		defaultUserChannels.add(new ChannelItem(0,R.drawable.newspper, "电子校报", 1,3,NewsPaperActivity.class));
		defaultUserChannels.add(new ChannelItem(0, R.drawable.jwc,"教务通知",1, 4,JwcActivity.class));
		defaultUserChannels.add(new ChannelItem(0,R.drawable.video, "视频长大", 1,5,VideoNewsActivity.class));
		
		defaultUserChannels.add(new ChannelItem(0,R.drawable.timelda_table, "课表查询",1, 1,KbDetail.class));
		defaultUserChannels.add(new ChannelItem(0,R.drawable.libary, "掌上图书馆",1, 2,LibBookQuery.class));
		defaultUserChannels.add(new ChannelItem(0,R.drawable.phone, "号码查询", 1,3,PhoneNumberActivity.class));
		defaultUserChannels.add(new ChannelItem(0, R.drawable.cet,"四六级",1, 4,Cet.class));
		defaultUserChannels.add(new ChannelItem(JwcLoginActivity.TYPE_SCORE,R.drawable.score, "分数查询", 1,5,JwcLoginActivity.class));
		defaultUserChannels.add(new ChannelItem(0,R.drawable.radio, "广播点歌", 1,6,RadioActivity.class));
		
		defaultUserChannels.add(new ChannelItem(JwcLoginActivity.TYPE_COURSE_SELECT,R.drawable.course_select, "学生选课", 2,7,JwcLoginActivity.class));
	}

	public static String getTitleById(int id){
		for(int i = 0;i<defaultUserChannels.size();i++){
			if(defaultUserChannels.get(i).getId()==id)
				return defaultUserChannels.get(i).getName();
		}
		return "";
	}
	
	private ChannelManage(SQLHelper paramDBHelper) throws SQLException {
		if (channelDao == null)
			channelDao = new ChannelDao(paramDBHelper.getContext());
		return;
	}

	/**
	 * 初始化频道管理类
	 * @param paramDBHelper
	 * @throws SQLException
	 */
	public static ChannelManage getManage(SQLHelper dbHelper)throws SQLException {
		if (channelManage == null)
			channelManage = new ChannelManage(dbHelper);
		return channelManage;
	}

	/**
	 * 清除所有的频道
	 */
	public void deleteAllChannel() {
		channelDao.clearFeedTable();
	}
	/**
	 * 获取其他的频道
	 * @return 数据库存在用户配置 ? 数据库内的用户选择频道 : 默认用户选择频道 ;
	 */
	public List<ChannelItem> getUserChannel() {
		Object cacheList = channelDao.listCache(SQLHelper.SELECTED + "= ?",new String[] { "1" });
		if (cacheList != null && !((List) cacheList).isEmpty()) {
			userExist = true;
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			List<ChannelItem> list = new ArrayList<ChannelItem>();
			for (int i = 0; i < count; i++) {
				ChannelItem navigate = new ChannelItem();
				navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
				navigate.setName(maplist.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		initDefaultChannel();
		return defaultUserChannels;
	}
	
	/**
	 * 获取其他的频道
	 * @return 数据库存在用户配置 ? 数据库内的其它频道 : 默认其它频道 ;
	 */
	public List<ChannelItem> getOtherChannel() {
		Object cacheList = channelDao.listCache(SQLHelper.SELECTED + "= ?" ,new String[] { "0" });
		List<ChannelItem> list = new ArrayList<ChannelItem>();
		if (cacheList != null && !((List) cacheList).isEmpty()){
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			for (int i = 0; i < count; i++) {
				ChannelItem navigate= new ChannelItem();
				navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
				navigate.setName(maplist.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		if(userExist){
			return list;
		}
		cacheList = smileNewsChannels;
		return (List<ChannelItem>) cacheList;
	}
	
	/**
	 * 保存用户频道到数据库
	 * @param userList
	 */
	public void saveUserChannel(List<ChannelItem> userList) {
		for (int i = 0; i < userList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) userList.get(i);
			channelItem.setOrderId(i);
			channelItem.setSelected(Integer.valueOf(1));
			channelDao.addCache(channelItem);
		}
	}
	
	/**
	 * 保存其他频道到数据库
	 * @param otherList
	 */
	public void saveOtherChannel(List<ChannelItem> otherList) {
		for (int i = 0; i < otherList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) otherList.get(i);
			channelItem.setOrderId(i);
			channelItem.setSelected(Integer.valueOf(0));
			channelDao.addCache(channelItem);
		}
	}
	
	/**
	 * 初始化数据库内的频道数据
	 */
	private void initDefaultChannel(){
		Log.d("deleteAll", "deleteAll");
		deleteAllChannel();
		saveUserChannel(defaultUserChannels);
		saveOtherChannel(smileNewsChannels);
	}
}
