package com.yuol.smile.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuol.smile.R;
import com.yuol.smile.bean.YUNewsItem;
import com.yuol.smile.utils.TimeUtil;

public class YUNewsAdapter extends BaseAdapter {
	List<YUNewsItem> newsList;
	Context activity;
	LayoutInflater inflater = null;
	public YUNewsAdapter(Context activity, List<YUNewsItem> newsList) {
		this.activity = activity;
		this.newsList = newsList;
		inflater = LayoutInflater.from(activity);
	}
	@Override
	public int getCount() {
		return newsList == null ? 0 : newsList.size();
	}

	@Override
	public YUNewsItem getItem(int position) {
		if (newsList != null && newsList.size() != 0) {
			return newsList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.item_yu_news, null);
			mHolder = new ViewHolder();
			mHolder.title = (TextView)view.findViewById(R.id.item_title);
			mHolder.publishTime = (TextView)view.findViewById(R.id.item_publish_time);
			mHolder.right_image = (ImageView)view.findViewById(R.id.right_image);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		//获取position对应的数据
		YUNewsItem news = getItem(position);
		mHolder.title.setText(news.getTitle());
		mHolder.publishTime.setText(TimeUtil.friendlyFormat(news.getPublishTime()));
		if(!TextUtils.isEmpty(news.getImageRight())){
			mHolder.right_image.setVisibility(View.VISIBLE);
			//bm.display(mHolder.right_image,news.getImageRight());
			
			DisplayImageOptions options = new DisplayImageOptions.Builder()
	        .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
	        .showImageOnFail(R.drawable.white)              //加载失败时的图片  
	        .cacheInMemory()                               //启用内存缓存  
	        .cacheOnDisc()                                 //启用外存缓存  
	        .build();

			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(news.getImageRight(),mHolder.right_image,options);
			
		}else
			mHolder.right_image.setVisibility(View.GONE);
		return view;
	}


	static class ViewHolder {

		TextView title;

		TextView publishTime;

		ImageView right_image;

	}

}
