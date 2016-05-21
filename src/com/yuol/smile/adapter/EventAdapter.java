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
import com.yuol.smile.widgets.RoundedImageView;

public class EventAdapter extends BaseAdapter {
	
	private List<IndexItemBase> list;
	private LayoutInflater inflater;
	public EventAdapter(Context context,List<IndexItemBase> list){
		this.list=list;
		this.inflater = LayoutInflater.from(context);
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
			convertView = inflater.inflate(R.layout.item_event_news, null, false);
			holder.title=(TextView) convertView.findViewById(R.id.item_title);
			holder.user_nickname=(TextView) convertView.findViewById(R.id.item_user_nickname);
			holder.cover=(ImageView) convertView.findViewById(R.id.item_image);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		System.out.println("活动标题:"+item.getTitle());
		holder.title.setText(item.getTitle());
		holder.user_nickname.setText(item.getTime());
		String url_cover = item.getCover();
		String url_user_iamge = item.getCover();
				holder.cover.setVisibility(View.VISIBLE);
				DisplayImageOptions options = new DisplayImageOptions.Builder()
		        .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
		        .showImageOnFail(R.drawable.white)              //加载失败时的图片  
		        .cacheInMemory()                               //启用内存缓存  
		        .cacheOnDisc()                                 //启用外存缓存  
		        .build();

				ImageLoader imageLoader = ImageLoader.getInstance();
				if(url_cover!=null&&!"".equals(url_cover)){
						imageLoader.displayImage(url_cover,holder.cover,options);
				}else{
					holder.cover.setVisibility(View.GONE);
				}
		return convertView;
	}
	
	static class ViewHolder {
	//	 TextView view;
//		 TextView attention;
		 TextView title;
		 TextView user_nickname;
//		 TextView type;
		 //TextView people;
//		 TextView time;
//		 TextView deadline;
//		 TextView summary;
		 ImageView cover;
		 RoundedImageView user_head;
		 //Button btnJoin;
	}

}
