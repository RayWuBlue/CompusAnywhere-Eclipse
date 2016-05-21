package com.yuol.smile.activity;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.yuol.smile.R;
import com.yuol.smile.helper.InitHelper;
import com.yuol.smile.service.Init;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.HttpUtil;

public class Welcome extends Activity{
	
	public RelativeLayout layout;
	private static ImageView img_image;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.act_welcome);
		
		PushManager.getInstance().initialize(this.getApplicationContext());
		
		layout=(RelativeLayout) super.findViewById(R.id.welcome_bg);
		img_image = (ImageView) findViewById(R.id.hello_img);
		initHello();
      
	}

	private void redirect(){
		Animation animation= AnimationUtils.loadAnimation(this,R.anim.scale);
		img_image.startAnimation(animation);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent updateIntent = new Intent(Welcome.this, Init.class); 
		        startService(updateIntent);
		        
				Intent it=null;
				if(new InitHelper(Welcome.this).checkHasInit()){
					it = new Intent(Welcome.this, Main.class);
				}else{
					it = new Intent(Welcome.this, VersionIntro.class);
				}
				startActivity(it);
				finish();
			}
		},3000*1);
	}
	
	private void initHello() {
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what==101){
					if(null!=msg.obj)
						parseResult(msg.obj.toString());
					else
						redirect();
				}
			}
			
		};
/*		dc  = new DataCache(Welcome.this);
		String url = Api.News.getHelloNews();
		String cache = dc.load(url);
		if(null!=cache&&!"".equals(cache)){
			parseResult(cache);
		}else
		{*/
			new Thread(new Runnable() {
				@Override
				public void run() {
					Message msg=new Message();
					msg.what=101;
					String helloUrl = Api.News.getHelloNews();
					msg.obj = HttpUtil.getRequest(helloUrl, null);
					handler.sendMessage(msg);
				}
			}).start();
			
		//}

	}

	private void parseResult(String result) {
		try {
			
			JSONObject object = JSONObject.parseObject(result);
			
			if (null != object.getString("url")&& !"".equals(object.getString("url"))) {

				//BitmapUtils bm;
				//bm = MyApplication.getInstance().getBitmapUtils();
				//bm.configDefaultLoadingImage(R.drawable.white);
				//bm.configDefaultLoadFailedImage(R.drawable.white);
				//bm.display(img_image, Api.HOST + object.getString("url"),
				/*		new BitmapLoadCallBack<View>() {
							@Override
							public void onLoadCompleted(View arg0,String arg1, Bitmap arg2,BitmapDisplayConfig arg3,BitmapLoadFrom arg4) {
								img_image.setImageBitmap(arg2);
								redirect();
							}

							@Override
							public void onLoadFailed(View arg0,String arg1, Drawable arg2) {
								redirect();
							}
						});*/
				
						DisplayImageOptions options = new DisplayImageOptions.Builder()
				        .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
				        .showImageOnFail(R.drawable.white)              //加载失败时的图片  
				        .cacheInMemory()                               //启用内存缓存  
				        .cacheOnDisc()                                 //启用外存缓存  
				        .build();
				
						ImageLoader imageLoader = ImageLoader.getInstance();

						imageLoader.displayImage(Api.HOST + object.getString("url"),img_image,options,new ImageLoadingListener() {
							
							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								
							}
							
							@Override
							public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
								
							}
							
							@Override
							public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
								img_image.setImageBitmap(arg2);
								TextView tv = (TextView)findViewById(R.id.textview);
								tv.setTextColor(getResources().getColor(R.color.white));
								findViewById(R.id.welcome_info_ll).setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_scroll_text));
								redirect();
							}
							
							@Override
							public void onLoadingCancelled(String arg0, View arg1) {
								
							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
			redirect();
		}
	}
}
