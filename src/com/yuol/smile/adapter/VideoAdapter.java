package com.yuol.smile.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuol.smile.R;
import com.yuol.smile.base.IndexItemBase;
import com.yuol.smile.utils.TimeUtil;

public class VideoAdapter extends BaseAdapter {
	private Context context;
	private List<IndexItemBase> list;
	private LayoutInflater inflater;
	//private BitmapUtils bm;
	
	public VideoAdapter(Context context,List<IndexItemBase> list){
		this.list=list;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		//bm = new BitmapUtils(context);
		//bm.configDefaultLoadingImage(R.drawable.trend_pic_loading);
		//bm.configDefaultLoadFailedImage(R.drawable.trend_pic_loading);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		IndexItemBase item=list.get(position);
		if (convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_video_news, null, false);
			holder.title=(TextView) convertView.findViewById(R.id.item_title);
			holder.time=(TextView) convertView.findViewById(R.id.item_publish_time);
			holder.video_image=(ImageView) convertView.findViewById(R.id.video_image);			
			holder.view=(TextView) convertView.findViewById(R.id.item_view);			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(item.getTitle());
		holder.time.setText(TimeUtil.friendlyFormat(item.getTime()));//将时间戳转化为友好的格式：如几小时前
		holder.view.setText(item.getView()+"人浏览");
		String cover = item.getCover();
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
        .showImageOnFail(R.drawable.white)              //加载失败时的图片  
        .cacheInMemory()                               //启用内存缓存  
        .cacheOnDisc()                                 //启用外存缓存  
        .build();

		ImageLoader imageLoader = ImageLoader.getInstance();

		
		
		if(cover!=null&&!"".equals(cover)){
				holder.video_image.setVisibility(View.VISIBLE);
				imageLoader.displayImage(cover,holder.video_image,options);
				
		}else{
			holder.video_image.setImageDrawable(context.getResources().getDrawable(R.drawable.nothing));
		}
		return convertView;
	}
	
	static class ViewHolder {
		TextView title;
		TextView time;
		TextView view;
		ImageView video_image;
	}

}
