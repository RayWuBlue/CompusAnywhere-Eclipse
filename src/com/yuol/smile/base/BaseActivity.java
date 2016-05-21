package com.yuol.smile.base;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout.LayoutParams;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuol.smile.R;
import com.yuol.smile.utils.SmartBarUtils;

/**
 * 基类activity
 * @author wei8888go
 *
 */

public abstract class BaseActivity extends Activity{
	protected ImageLoader imageLoader;
	protected BaseLayout baseLy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageLoader = ImageLoader.getInstance();
		if(SmartBarUtils.hasSmartBar()){
			View decorView = getWindow().getDecorView();
			SmartBarUtils.hide(decorView);
		}
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) { 
		    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); 
		    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		    
		}
	}
	private int getStatusBarHeight() {    
		Resources resources = getResources();    
		int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");    
		int height = resources.getDimensionPixelSize(resourceId);    
		Log.v("dbw", "Status height:" + height);    
		return height;
	}
	/**
	 * 设置布局文件
	 */
	@Override
	public void setContentView(int layoutId) {
		
		baseLy = new BaseLayout(this, layoutId);
	
		
		setContentView(baseLy);
		baseLy.setTitleBarColor(getResources().getColor(R.color.blue));
		baseLy.setBackPressedEvent(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title){
		if(baseLy!=null){
			baseLy.TitleText.setText(title);
		}
	}
	public void setTitleBarColor(int color){
		baseLy.setTitleBarColor(color);
	}
	public View addButton(String text,OnClickListener event){
		return this.addButton(text,0,event);
	}
	public View addButton(int imgRes,OnClickListener event){
		return this.addButton(null,imgRes,event);
	}
	public View addButton(String text,int imgRes,OnClickListener event){
		return baseLy.addButton(text,imgRes,event);
	}
	public void setBackPressedEvent(OnClickListener onclick){
		baseLy.setOnClickListener(onclick);
	}
}
