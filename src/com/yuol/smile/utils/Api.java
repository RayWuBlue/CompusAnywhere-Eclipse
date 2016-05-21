package com.yuol.smile.utils;

import java.util.ArrayList;
import java.util.List;

import com.yuol.smile.R;
import com.yuol.smile.bean.NewsChannelItem;

public final class Api {
	public static List<NewsChannelItem> YUNewsChannels = new ArrayList<NewsChannelItem>();

	public static List<NewsChannelItem> SmileNewsChannels = new ArrayList<NewsChannelItem>();

	static {
		
		YUNewsChannels.add(new NewsChannelItem(3, "综合新闻", 1, 1));
		YUNewsChannels.add(new NewsChannelItem(32, "校园热点", 2, 1));
		YUNewsChannels.add(new NewsChannelItem(6, "学在长大", 3, 1));
		YUNewsChannels.add(new NewsChannelItem(2, "长大要闻", 4, 1));
		YUNewsChannels.add(new NewsChannelItem(1, "通知通告", 5, 1));
		YUNewsChannels.add(new NewsChannelItem(4, "媒体长大", 1, 0));
		YUNewsChannels.add(new NewsChannelItem(5, "学术信息", 2, 0));
		YUNewsChannels.add(new NewsChannelItem(33, "院系传真", 3, 0));
		YUNewsChannels.add(new NewsChannelItem(34, "长大人物", 4, 0));
		YUNewsChannels.add(new NewsChannelItem(35, "学者论坛", 5, 0));
		YUNewsChannels.add(new NewsChannelItem(8, "高教视野", 6, 0));
		YUNewsChannels.add(new NewsChannelItem(36, "校友动态", 7, 0));
		YUNewsChannels.add(new NewsChannelItem(41, "长大之声", 8, 0));
		
		SmileNewsChannels.add(new NewsChannelItem(42, "悦读", 1, 1));
		SmileNewsChannels.add(new NewsChannelItem(45, "社团", 2, 1));
		SmileNewsChannels.add(new NewsChannelItem(47, "原创", 3, 1));
		
	}
	
	public static final String getUrlById(int id){
				return "http://news.yangtzeu.edu.cn/app/conn.php?id="+id;
	}

	public static final String DOMAIN = "http://online.yangtzeu.edu.cn";
	//public static final String DOMAIN = "http://www.yuolwx.com";
	
	public static final String HOST = DOMAIN+"/2015app/v/";

	public static final String JWC_HOST = "http://jwc2.yangtzeu.edu.cn:8080/";
	
	public static final String FEEDBACK = HOST+"index.php?s=/app/index/feedback";
	
	public static final String XKCX = "http://jwc2.yangtzeu.edu.cn:8080/xkinfo.aspx";

	//public static final String XK = "http://jwc2.yangtzeu.edu.cn:8080/xkinfo.aspx";
	
	public static final String UPDATE = HOST+"index.php?s=/app/index/appUpload";
	
	public static final String DOWNLOAD = HOST+"index.php?s=/app/index/appdownload";
	
	public static final String CITY = "荆州";

	public static final String URL_SONG =  "http://radio.yangtzeu.edu.cn/diange.asp";

	public static final String SER_KEY = "com.yuol.smile";
	
	public static final class Weibo{
		/*
		 * 获取微博广场的信息
		 * 
		 * */
		public static final String getWeiboIndex() {
			String url = HOST+"index.php?s=/weibo/index/appindex";
			return url;
		}
		/*
		 * 获得微博评论信息
		 * 
		 * */
		public static final String getWeiboComment(String weiboId) {
			String url = HOST+"index.php?s=/weibo/index/appLoadComment/weibo_id/"+weiboId;
			return url;
		}
		/*
		 * 提交微博评论信息
		 * 
		 * */
		public static final String postWeiboComment(String id,String uid) {
			String url = HOST+"";
			return url;
		}
		/*
		 * 发布微博
		 * 
		 * */
		public static final String postWeibo(){
		 String url;
		 url =HOST+ "index.php?s=/weibo/index/appDoSend";
		return url;
	 }
		/*
		 * 发布微博
		 * 
		 * */
		public static final String postWeiboImage(){
			String url;
			//http://localhost/index.php?s=/weibo/index/upload
			url =HOST+ "index.php?s=/Home/File/appUploadPicture";
			return url;
		}
		/*
		 *点赞
		 * 
		 * */
		public static final String postLike(String uid,String id){
			//http://localhost/index.php?s=/weibo/index/applike/uid/3/id/403
		 String url;
		 url =HOST+ "index.php?s=/weibo/index/applike/uid/"+uid+"/id/"+id;
		return url;
	 }
		/*
		 *微博评论
		 * 
		 * */
		public static final String postComment(){
		 String url;
		 //http://localhost/index.php?s=/weibo/index/appDoComment/token/Zfiorz0wAQpdWB7SFqHbvK6TsM1YN89GnPxlt3IE/weibo_id/403/content/hehehehehhehehehe
		 url =HOST+ "index.php?s=/weibo/index/appDoComment";
		return url;
	 }
	}
	
	public static final class Goods{
		/*
		 * 获得商品列表
		 * 
		 * */
		public static final String getGoodsList(int goodType,int pageindex) {
			String url = HOST+"index.php?s=/shop/index/appgoods/category_id/"+goodType+"/page/"+pageindex;
			return url;
		}
		/*
		 * 获取商品的详细信息
		 * 
		 * */
		public static final String getGoodsDetail(String goodType,String id) {
			String url = HOST+"index.php?s=/shop/index/appgoodsdetail/id/"+id;
			return url;
		}
		/*
		 * 获取商品的评论信息
		 * 
		 * */
		public static final String getGoodsComment(String id,String uid) {
			String url = HOST+"index.php?s=/shop/index/appcommentlist/id/"+id+"/uid/"+uid;
			return url;
		}
		/*
		 *提交商品的评论信息
		 * 
		 * */
		public static final String postGoodsComment(String id,String uid) {
			String url = HOST+"";
			return url;
		}
	}
	
	public static final class News{
		//系统公告ID
		public static final int NOTICE_ID = 46;
		/*
		 * 获取新闻首页信息（全部新闻）
		 * 
		 * */
		public static final String getNewsIndex(int page){
			String url = HOST+"index.php?s=/blog/index/appindex/page/"+page;
			return url;
		}
		/*
		 * 获取新闻列表
		 * 
		 * */
		public static final String getNewsList(int type,int page){
			//http://localhost/index.php?s=/blog/article/applists/category/45/page/3.html
			String url = HOST+"index.php?s=/blog/article/applists/category/"+type+"/page/"+page+".html";
			return url;
		}
		/*
		 * 获取新闻详细信息
		 * 
		 * */
		public static final String getNewsDetail(int id) {
			String url = HOST+"index.php?s=/blog/article/appdetail/id/"+id;
			return url;
		}
		/*
		 * 获取新闻详细信息（分页显示）
		 * 
		 * */
		public static final String getNewsDetail(String id,String page) {
			String url = HOST+"index.php?s=/blog/article/appdetail/id/"+id+"/p/"+page;
			return url;
		}
		/*
		 * 获取新闻评论信息
		 * 
		 * */
		public static final String getNewsComment(String id,String uid,int page) {
			String url = HOST+"index.php?s=/blog/article/appcommentlist/id/"+id+"/uid/"+uid+"/page/"+page;
			return url;
		}
		/*
		 * 提交新闻评论信息
		 * 
		 * */
		public static final String postNewsComment(String app,String mod,String row_id,String content,String uid) {
			//http://localhost/index.php?s=/blog/article/appAddComment/app/Blog/mod/Article/row_id/166/content/asdasdfas/uid/59
			String url = HOST+"index.php?s=/blog/article/appAddComment/app/"+app+"/mod/"+mod+"/row_id/"+row_id+"/content/"+content+"/uid/"+uid;
			System.out.println("URL_COMMENT:"+url);
			return url;
		}
		/*
		 * 获取APP滚动新闻首页信息
		 * 
		 * */
		public static final String getScrollNews(){
			String url = HOST+"index.php?s=/blog/index/appscroll";
			return url;
		}
		/*
		 * 获取新闻网新闻
		 * 
		 * */		
		public static final String getYUNews(int page,int typeId){
			String url = "http://news.yangtzeu.edu.cn/app/conn.php?pagesize=" + page*10 + "&typeid="+typeId;
			System.out.println("长大新闻:"+url);
			return url;
		}
		 /* 获取新闻网新闻频道
		 * 
		 * */		
		public static final List<NewsChannelItem> getYUNewsChannels(){
			return YUNewsChannels;
		}
		 /* 获取悦读新闻频道
		 * 
		 * */		
		public static final List<NewsChannelItem> getSmileNewsChannels(){
			return SmileNewsChannels;
		}
		/*
		 * 获取视频新闻列表
		 * 
		 * */		
		public static final String getVideoNews(int page){
			String url = "http://news.yangtzeu.edu.cn/app/conn.php?pagesize=" + page*10 +"&typeid=40";
			return url;
		}
		/*
		 * 获取视频新闻播放地址
		 * 
		 * */		
		public static final String getVideoNewsDetail(String filename){
			String url="http://vod.yangtzeu.edu.cn/out/%E9%95%BF%E5%A4%A7%E6%96%B0%E9%97%BB%E5%90%88/%E9%95%BF%E5%A4%A7%E6%96%B0%E9%97%BB"+filename+".flv";
			return url;
		}
		/*
		 * 获取教务处通知新闻列表
		 * 
		 * */		
		public static final String getJwcNewsNotify(int page){
			String url = "http://jwc.yangtzeu.edu.cn:8080/list/47-"+page+".aspx";
			return url;
		}
		/*
		 * 获取教务处新闻动态
		 * 
		 * */		
		public static final String getJwcNewsLatest(int page){
			String url = "http://jwc.yangtzeu.edu.cn:8080/list/49-"+page+".aspx";
			return url;
		}
		/*
		 * 获取教务处新闻动态
		 * 
		 * */		
		public static final String getJobNewsNotify(int page){
			String url = "http://jyxx.yangtzeu.edu.cn/news/zxzx/index"+(page>1?"_"+page:"")+".html";
			return url;
		}
		/*
		 * 获取教务处新闻动态
		 * 
		 * */		
		public static final String getJobNewsContext(int page){
			String url = "http://jyxx.yangtzeu.edu.cn/news/xjzp/index"+(page>1?"_"+page:"")+".html";
			return url;
		}
		/*
		 * 获取教务处新闻动态
		 * 
		 * */		
		public static final String getJobNewsInfo(int page){
			String url = "http://jyxx.yangtzeu.edu.cn/news/zpxx/index"+(page>1?"_"+page:"")+".html";
			return url;
		}
		/*
		 * 获取你好长大新闻
		 * 
		 * */		
		public static final String getHelloNews(){
			String url = HOST+"index.php?s=/app/index/Picturehello";
			return url;
		}
	}

	public static final class Forum{
		/*
		 * 获得所有板块分类
		 * 
		 * */
		public static final String getForumIndex(){
			String url = HOST+"index.php?s=/forum/index/appindex";
			return url;
		}
		/*
		 * 获得指定板块帖子的列表
		 * 
		 * */
		public static final String getForumList(String forumId){
			String url = HOST+"index.php?s=/forum/index/appforum/id/"+forumId;
			return url;
		}
		/*
		 * 调用帖子的详细内容
		 * 
		 * */
		public static final String getForumDetail(String threadId){
			String url = HOST+"index.php?s=/forum/index/appdetail/id/"+threadId;
			return url;
		}
		/*
		 * 获得帖子的评论内容
		 * 
		 * */
		public static final String getForumComment(String id,String uid) {
			String url = HOST+"index.php?s=/shop/index/appcommentlist/id/"+id+"/uid/"+uid;
			return url;
		}
		/*
		 * 提交帖子的评论内容
		 * 
		 * */
		public static final String postForumComment(String id,String uid) {
			String url = HOST+"";
			return url;
		}
		
	}
	
	public static final class Event{
		/*
		 * 获得所有板块分类
		 * 
		 * */
		public static final String getEventIndex(int page){
			String url = HOST+"index.php?s=/event/index/appindex/page/"+page;
			return url;
		}

	}
	
	public static final class User{
		/*
		 *验证码
		 * 
		 * */
		public static final String verify(){
			String url = HOST+"index.php?s=/home/user/verify";
			return url;
		}
		/*
		 *用户登录
		 * 
		 * */
		public static final String login(){
			String url = HOST+"index.php?s=/home/user/applogin";
			return url;
		}
		/*
		 *用户注册
		 * 
		 * */
		public static final String register(){
			String url = HOST+"index.php?s=/home/user/appregister";
			return url;
		}
		/*
		 *用户头像上传
		 * 
		 * */
		public static final String uploadHeadImage(){
			String url = HOST+"index.php?s=/UserCenter/Config/doUploadAvatar";
			return url;
		}
		//UserCenter/Config/doUploadAvatar
		/*
		 *用户注销
		 * 
		 * */
		public static final String logout(){
			String url = HOST+"index.php?s=/home/user/applogout";
			return url;
		}
		
		public static final String getDefaultUserImage(){
			String url = HOST+"Addons/Avatar/default_64_64.jpg";
			return url;
		}
		
	}

	public static final class Jwc{
		/*
		 *验证码
		 * 
		 * */
		public static final String getSchedule(){
			String url = JWC_HOST+"index.php?s=/home/user/verify";
			return url;
		}
		/*
		 *验证码
		 * 
		 * */
		public static final String getJwcKb(){
			String url = JWC_HOST+"kb.aspx";
			return url;
		}
		public static final String getJwcCourseSelect(){
			String url = JWC_HOST+"xkinfo.aspx";
			return url;
		}
		public static final String getJwcXkAdd(String id){
			String url = JWC_HOST+"Xk2.aspx?rwh="+id+"&action=1";
			return url;
		}
		public static final String getJwcXkDel(String id){
			String url = JWC_HOST+"Xk2.aspx?rwh="+id+"&action=2";
			return url;
		}
		public static final String getJwcClass(){
			String url = JWC_HOST+"kb.aspx";
			return url;
		}
		public static final String getJwcLoginVerify(){
			String url = JWC_HOST+"verifycode.aspx";
			return url;
		}
		public static final String getJwcLogin(){
			String url = JWC_HOST+"login.aspx";
			return url;
		}		
		public static final String getJwcScore(){
			String url = JWC_HOST+"cjcx.aspx";
			return url;
		}
	}

	public static final class UserConfig{
		/*
		 *验证码
		 * 
		 * */
		public static final String changePassword(String old_password, String new_password,String uid,String token){
			String url = HOST+"index.php?s=/app/index/changePassword/old_password/"+old_password+"/new_password/"+new_password+"/uid/"+uid+"/token/"+token;
			return url;
		}
		/*
		 *验证码
		 * 
		 * */
		public static final String uploadTempAvatar(String uid,String token){
			String url = HOST+"index.php?s=/app/index/uploadTempAvatar/uid/"+uid+"/token/"+token;
			return url;
		}
		
		public static final String modifynickname(){
			String url = HOST+"index.php?s=/app/index/modifynickname";
			return url;
		}
		public static final String modifysex(String uid,String token,String newsex){
			String url = HOST+"index.php?s=/app/index/modifysex/uid/"+uid+"/token/"+token+"/newsex/"+newsex;
			return url;
		}
		public static final String modifybirthday(String uid,String token,String newbirthday){
			String url = HOST+"index.php?s=/app/index/modifybirthday/uid/"+uid+"/token/"+token+"/newbirthday/"+newbirthday;
			return url;
		}
		public static final String modifyqq(){
			String url = HOST+"index.php?s=/app/index/modifyqq";
			return url;
		}
	}
	
	
	public static int getWeatherIcon(String string) {
		if(string.contains("雨"))
			return R.drawable.rainy;
		else if(string.contains("雪"))
			return R.drawable.snowy;
		else if(string.contains("雾"))
			return R.drawable.foggy;
		else if(string.contains("云"))
			return R.drawable.cloudy;
		else if(string.contains("晴"))
				return R.drawable.sunny;
			
		return 0;
	}

	public static String getWeatherUrl(String cityCode) {
		return "http://www.weather.com.cn/data/cityinfo/"+ cityCode + ".html";
	}

}
