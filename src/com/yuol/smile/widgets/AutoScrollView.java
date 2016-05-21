package com.yuol.smile.widgets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuol.smile.R;
import com.yuol.smile.activity.NewsContent;
import com.yuol.smile.base.IndexItemBase;
import com.yuol.smile.bean.ImageNews;
import com.yuol.smile.utils.Api;

public class AutoScrollView {
	private View rootView;
	private AutoScrollViewPager viewPager; // android-support-v4中的滑动组件
	private List<ImageView> imageViews; // 滑动的图片集合
	private Context context;
	private List<String> titles; // 图片标题
	private int[] dotResId;
	private List<View> dots; // 图片标题正文的那些点
	private int loadedPic = 0;
	private TextView tv_title;
	private MyAdapter mAdapter;
	//BitmapUtils bm;
	public AutoScrollView(Context context) {
		this.context = context;
		rootView = LayoutInflater.from(context).inflate(R.layout.wg_scroll_view, null);
		dotResId = new int[]{R.id.v_dot0,R.id.v_dot1,R.id.v_dot2,R.id.v_dot3,R.id.v_dot4};
		titles = new ArrayList<String>();
		imageViews = new ArrayList<ImageView>();
		dots = new ArrayList<View>();
		tv_title = (TextView)rootView.findViewById(R.id.tv_title);
		viewPager = (AutoScrollViewPager) rootView.findViewById(R.id.vp);
		
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		viewPager.setAdapter(new MyAdapter());

        viewPager.setInterval(5000);
        viewPager.startAutoScroll();
        
		mAdapter = new MyAdapter();
		viewPager.setAdapter(mAdapter);// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		//bm = new BitmapUtils(context);
		//bm.configDefaultLoadingImage(R.drawable.trend_pic_loading);
		//bm.configDefaultLoadFailedImage(R.drawable.trend_pic_loading);
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;
		public void onPageSelected(int position) {
			tv_title.setText(titles.get(position));
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * 填充ViewPager页面的适配器
	 * 
	 */
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}

	public View getView()
	{
		return rootView;
	}

	public void loadFailed(){
		rootView.setVisibility(View.GONE);
	}

	public void addImage(final ImageNews imgNews) {

		if(loadedPic<5)
		{
			
			
			ImageView imageView = new ImageView(context);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			imageView.setLayoutParams(lp);
			
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//IndexItemBase item=new IndexItemBase(Integer.parseInt(imgNews.getNewsId()),imgNews.getNewsTitle());
					Intent it=new Intent(context,NewsContent.class);
					Bundle mBundle = new Bundle();  

					mBundle.putInt("id",Integer.parseInt(imgNews.getNewsId()));
					mBundle.putString("title", imgNews.getNewsTitle());
					mBundle.putString("cover", imgNews.getImgUrl());
					//mBundle.putString("time", item.getTime());
					//mBundle.putInt("comment", item.getComment_num());

				    it.putExtras(mBundle);  
				    context.startActivity(it);
				    
				    
				}
			});
			DisplayImageOptions options = new DisplayImageOptions.Builder()
	        .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
	        .showImageOnFail(R.drawable.white)              //加载失败时的图片  
	        .cacheInMemory()                               //启用内存缓存  
	        .cacheOnDisc()                                 //启用外存缓存  
	        .build();
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(imgNews.getImgUrl(),imageView,options);
			imageViews.add(imageView);
			titles.add(imgNews.getNewsTitle());
			View dot = rootView.findViewById(dotResId[loadedPic++]);
			dot.setVisibility(View.VISIBLE);
			dots.add(dot);
			mAdapter.notifyDataSetChanged();
		}
		if(loadedPic==1){
			tv_title.setText(imgNews.getNewsTitle());
		}
	}

	public void stopAutoScroll() {
		viewPager.startAutoScroll();
	}
/*	public void clear()
	{
		imageViews.clear();
		for(int i = 0;i<dots.size();i++)
			dots.get(i).setVisibility(View.GONE);
		dots.clear();
		titles.clear();
	}*/
}
