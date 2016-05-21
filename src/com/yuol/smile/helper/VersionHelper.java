package com.yuol.smile.helper;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.yuol.smile.service.UpdateService;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.MyAlertDialog;
import com.yuol.smile.widgets.MyAlertDialog.MyDialogInt;

public class VersionHelper {
	
	public static final String PackageName="com.yuol.smile";
	
	public SharedPreferences share;
	public SharedPreferences.Editor edit=null;
	private Context context;
	private static final String FILENAME="version";
	private int serverVer = 0;
	public VersionHelper(Context context){
		this.context=context;
		this.share=this.context.getSharedPreferences(FILENAME,
				Context.MODE_PRIVATE);
		this.edit=share.edit();
	}
	
	/**
	 * 
	 * @param jsonData 服务器下载数据
	 * @param uflag   是否更新
	 * @return
	 */
	public boolean update(String jsonData,boolean uflag){
		try {
			JSONObject JsonData = new JSONObject(jsonData);
			/*edit.putString("vercode",JsonData.getString("code"));
			edit.putString("vername",JsonData.getString("name"));*/
			edit.putString("vercode",JsonData.getString("name"));
			edit.putString("vername",JsonData.getString("code"));
			edit.putString("size",JsonData.getString("size"));
			edit.putString("verinfo", JsonData.getString("info"));
			edit.putString("filename",JsonData.getString("url"));
			edit.putBoolean("uflag",uflag);
			edit.commit();
			return true;
			
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return false;		
	}
	
	/**
	 * 检测是否有更新
	 * @return
	 */
	public boolean checkUpdate(){

			if(getUpdateVersion()>getVerCode(context)){
				return true;
		}
		return false;
	}
	
	/**
	 * 更新完成
	 */
	public  void hadUpdate(){
		edit.putBoolean("uflag",false);
		edit.commit();
	}
	
	//获取下载地址
	public String  getLoadUrl(){
		String url=Api.DOWNLOAD;
		return url;
	}
	
	//获得当前推荐更新版本
	public int  getUpdateVersion(){
		return Integer.parseInt(share.getString("vercode","0"));
	}
	
	
	
	/**
	 * 当前版本号
	 * @param context
	 * @return
	 */
	public static int getVerCode(Context context) {  
        int verCode = -1;  
        try {  
            verCode = context.getPackageManager().getPackageInfo(  
            		PackageName, 0).versionCode;  
        } catch (NameNotFoundException e) {  
        }  
        return verCode;  
    } 
	
	/**
	 * 当前版本名称
	 * @param context
	 * @return
	 */
	 public static String getVerName(Context context) {  
         String verName = "";  
         try {  
             verName = context.getPackageManager().getPackageInfo(  
            		 PackageName, 0).versionName;  
         } catch (NameNotFoundException e) {  
         }  
         return verName;    
	 }
	
	/**
	 * 提示更新
	 * @param context
	 */
	public  void updateTip(){
		if(checkUpdate()){
				final MyAlertDialog mad=new MyAlertDialog(context);
				mad.setTitle("版本更新");
				mad.setMessage("版本:"+share.getString("vername","")+"\n大小:"+Long.parseLong(share.getString("size","0"))/(1024*1024)+"MB"
						+"\n\n更新内容:\n"+share.getString("verinfo",""));
				mad.setLeftButton("立即更新",new MyDialogInt() {
					@Override
					public void onClick(View view) {
						mad.dismiss();
						
						//hadUpdate();
						
						T.showLong(context,"正在下载更新");
						Intent updateIntent = new Intent(context, 
		                         UpdateService.class); 
		                context.startService(updateIntent);
		                
					}
				});
				mad.setRightButton("下次提示",new MyDialogInt() {
					@Override
					public void onClick(View view) {
						mad.dismiss();
					}
				});
			
		}
		
	}
	
	public void clear(){
		edit.clear().commit();
	}
}
