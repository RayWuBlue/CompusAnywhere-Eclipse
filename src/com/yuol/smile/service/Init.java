package com.yuol.smile.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.helper.VersionHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.GetUtil;


/**
 * ¼ì²âÊÇ·ñ¸üÐÂ
 * @author wei8888go
 *
 */
public class Init  extends Service {
	
	public LoginHelper lh;
	public VersionHelper vh;
	@Override
	public void onCreate() {
		super.onCreate();
		
		lh=new LoginHelper(this);
		vh=new VersionHelper(this);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg=new Message();
				msg.what=102;
				//msg.obj=GetUtil.getRes(ServerConfig.HOST+"/schoolknow/versionManage.php?current="+VersionHelper.getVerCode(Init.this));
				msg.obj=GetUtil.getRes(Api.UPDATE);
				//System.out.println("msg.obj:"+msg.obj);
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==102){
				String temp=msg.obj.toString();
				if(!temp.equals("")&&temp.length()!=0){
					try {
						System.out.println("temp:"+temp);
						//int version=VersionHelper.getVerCode(Init.this);
						//JSONObject JsonData = new JSONObject(temp);
						
						//String serverCode=JsonData.getString("code").trim();
						
						//int serverVer=Integer.parseInt(serverCode);
						//System.out.println("serverVer:"+serverVer);
						//System.out.println("localVer:"+version);
						vh.update(temp,true);
					}catch (Exception e) {
					}
				}
				stopSelf();
			}
		}
		
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}




}
