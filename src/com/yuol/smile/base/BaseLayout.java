package com.yuol.smile.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuol.smile.R;

/**
 * titleBar布局基类文件
 * @author wei8888go
 *
 */
public class BaseLayout extends RelativeLayout {
	private LayoutInflater inflater;
	public TextView TitleText;
	public ImageButton backBtn;
	public LinearLayout toolbar;
	private Context activity;
	public View titleView;
	public View getTitleView() {
		return titleView;
	}
	public View base;

	public BaseLayout(Context context) {
		super(context);
		this.activity = context;
	}
	
	public BaseLayout(Context context, int layoutId) {
		super(context);
		this.activity = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		p.addRule(RelativeLayout.ALIGN_PARENT_TOP);p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		titleView = inflater.inflate(R.layout.part_titlebar, null);
		addView(titleView, p);
		
		RelativeLayout.LayoutParams p1 = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT);
		p1.addRule(RelativeLayout.BELOW, titleView.getId());
		View contView = inflater.inflate(layoutId, null, false);
		base=contView;
		addView(contView, p1);
		TitleText=(TextView) findViewById(R.id.titleText);
		backBtn = (ImageButton)findViewById(R.id.backButton);
		toolbar = (LinearLayout)findViewById(R.id.toolbar);
	}
	public void setTitleBarColor(int color){
		titleView.setBackgroundColor(color);
	}
	public View addButton(String text,OnClickListener event){
		return this.addButton(text,0,event);
	}
	public View addButton(int imgRes,OnClickListener event){
		return this.addButton(null,imgRes,event);
	}
	public View addButton(String text,int imgRes,OnClickListener event){
		View newButton = inflater.inflate(R.layout.item_toolbar_button, null);
		ImageView img = (ImageView)newButton.findViewById(R.id.btn_image);
		TextView tv = (TextView)newButton.findViewById(R.id.btn_text);
		if(imgRes!=0){
			img.setVisibility(View.VISIBLE);
			img.setImageResource(imgRes);
			img.setOnClickListener(event);
		}else{
			img.setVisibility(View.GONE);
		}
		if(text!=null){
			tv.setVisibility(View.VISIBLE);
			tv.setText(text);
			tv.setOnClickListener(event);
		}else{
			tv.setVisibility(View.GONE);
		}
		toolbar.addView(newButton);
		return newButton;
	}
	public void setBackPressedEvent(OnClickListener onclick){
		backBtn.setOnClickListener(onclick);
	}
}
