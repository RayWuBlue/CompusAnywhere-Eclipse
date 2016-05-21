package com.yuol.smile.helper;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.yuol.smile.activity.Login;
import com.yuol.smile.utils.Api;


public class LoginHelper{
	
	public SharedPreferences share;
	public SharedPreferences.Editor edit=null;
	private Context context;
	private static final String FILENAME="login";
	private static  String	u_stuid;
	private static String u_name;
	private static String u_class;
	private static String u_cookie;
	private static String __VIEWSTATE;
	private static String __EVENTVALIDATION;
	
	@SuppressLint("CommitPrefEdits")
	public LoginHelper(Context context){
		this.context=context;
		this.share=this.context.getSharedPreferences(FILENAME,
				Context.MODE_PRIVATE);
		this.edit=share.edit();
	}
	
	/*
	 * {"uid":"58",
"nickname":"蔚蓝",
"sex":"0",
"birthday":"0000-00-00",
"qq":"",
"score":"69",
"login":"40",
"reg_ip":"2130706433",
"reg_time":"1419923757",
"last_login_ip":"3232235784",
"last_login_time":"1423551536",
"status":"1",
"signature":"",
"tox_money":"7",
"pos_province":"0",
"pos_city":"0",
"pos_district":"0",
"pos_community":"0"}
	 * 
	 * */
	public boolean login(String jsonData,String session){
		try {
			System.out.println("JSON_USER:"+jsonData);
			JSONObject jsonResult = new JSONObject(jsonData);
			JSONObject JsonData = jsonResult.getJSONObject("info");
			edit.putString("uid",JsonData.getString("uid"));
			edit.putString("nickname",JsonData.getString("nickname"));
			edit.putString("username",JsonData.getString("uid"));
			edit.putString("user_image",Api.DOMAIN+JsonData.getJSONObject("user_image").getString("avatar64"));
			edit.putString("sex",JsonData.getString("sex"));
			edit.putString("token",jsonResult.getString("token"));
			edit.putString("PHPSESSID", session);
			edit.putString("birthday",JsonData.getString("birthday"));
			edit.putString("qq",JsonData.getString("qq"));
			edit.commit();
			return true;
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return false;		
	}

	public boolean JwcLogin(String u_stuid,String u_name,String u_class,String u_cookie,String p1,String p2){
		if(!TextUtils.isEmpty(u_stuid)&&!TextUtils.isEmpty(u_name)&&!TextUtils.isEmpty(u_class)
				&&!TextUtils.isEmpty(u_cookie)&&!TextUtils.isEmpty(p1)&&!TextUtils.isEmpty(p2)){
			LoginHelper.u_stuid = u_stuid;
			LoginHelper.u_name = u_name;
			LoginHelper.u_class = u_class;
			LoginHelper.u_cookie  =u_cookie;
			LoginHelper.__VIEWSTATE = p1;
			LoginHelper.__EVENTVALIDATION =  p2;
			return true;	
		}
		else
			return false;
	}
	
	public void JwcLogout(){

			LoginHelper.u_stuid = "";
			LoginHelper.u_name = "";
			LoginHelper.u_class = "";
			LoginHelper.u_cookie  ="";
			LoginHelper.__VIEWSTATE = "";
			LoginHelper.__EVENTVALIDATION =  "";
			
			edit.putString("stuid","");
			edit.commit();
			
			SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPreferences",context.MODE_PRIVATE);
			Editor editor=sharedPreferences.edit();
			editor.putString("jwc_user_id","");
			editor.putString("jwc_user_pwd","");
			editor.commit();
			
	}
	
	public boolean hasLogin(){
		if(share.contains("uid")){
			return true;
		}
		return false;		
	}
	public boolean hasJwcLogin(){
		if(u_stuid!=null&&!u_stuid.equals(""))
			return true;
		else
			return false;		
	}
	//强制跳转登陆页面
	public void ToLogin(){
		Intent it=new Intent(context,Login.class);
		it.putExtra("param","2");
		context.startActivity(it);
	}
	
	public Boolean logout(){
		if(share.contains("token")){
			this.clear();
			return true;
		}
		return false;
	}
	
	public void clear(){
		edit.clear().commit();
	}

	
	public String getJwcId(){
		return u_stuid;
	}
	public String getJwcName(){
		return u_name;
	}
	public String getJwcClass(){
		return u_class;
	}
	public String getJwcCookie(){
		return u_cookie;
	}
	public String getJwcP1(){
		return __VIEWSTATE;
	}
	public String getJwcP2(){
		return __EVENTVALIDATION;
	}

	public String getUid(){
		return share.getString("uid","");
	}
	
	public String getPHPSESSID(){
		return share.getString("PHPSESSID","");
	}

	public String getNickname(){
		return share.getString("nickname","");
	}
	
	public void setNickname(String nick){
		edit.putString("nickname",nick);
		edit.commit();
	}
	
	public String getUsername(){
		return share.getString("username","");
	}
	
	public void setUsername(String username){
		edit.putString("username",username);
		edit.commit();
	}
	
	
	public String getHeadImg(){
		return share.getString("user_image","");
	}
	public void setHeadImg(String img){
		edit.putString("user_image",img);
		edit.commit();
	}
	public  String getStuId(){
		return share.getString("stuid","");
	}
	public  void setStuId(String stuid){
		edit.putString("stuid",stuid);
		edit.commit();
	}
	
	public  Boolean isBoy(){
		return share.getString("sex","").equals("男");
	}
	
	public  String getToken(){
		return share.getString("token","");
	}
	
	public  String getSex(){
		String sex = share.getString("sex","");
		if("0".equals(sex))
			return "保密";
		else if("1".equals(sex))
			return "男";
		else
			return "女";
	}
	
	public  void setSex(String sex){
		edit.putString("sex",sex);
		edit.commit();
	}
	public  String getQQ(){
		String qq = share.getString("qq","");
		return qq;
	}
	
	public  void setBirthday(String birthday){
		edit.putString("birthday",birthday);
		edit.commit();
	}
	public  String getBirthday(){
		String birthday = share.getString("birthday","");
		return birthday;
	}
	
	public  void setQQ(String qq){
		edit.putString("qq",qq);
		edit.commit();
	}
	
	public String getCollege(){
		return share.getString("college",CollegeHelper.collegeName[0]);
	}
	
	public void setCollege(String college){
		edit.putString("college",college);
		edit.commit();
	}
	
	//是否已经绑定学号
	public boolean hasBindStuid(){
		String stuid=getStuId();
		if(stuid.length()==14&&!stuid.trim().equals("")){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 设置设备识别码
	 * @param client
	 */
	public void setClient(String client){
		edit.putString("clientId", client);
		edit.commit();
	}
	
	public String getClient(){
		return share.getString("clientId","");
	}
	
	public  void setUserid(String userid){
		edit.putString("userid", userid);
		edit.commit();
	}
	public String getUserid(){
		return share.getString("userid","");
	}
	

}
