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
import com.yuol.smile.base.IndexItemBase;
import com.yuol.smile.utils.TimeUtil;

public class IndexAdapter extends BaseAdapter {
	
	private List<IndexItemBase> list;
	private LayoutInflater inflater;
	//private BitmapUtils bm;
	
	public IndexAdapter(Context context,List<IndexItemBase> list){
		this.list=list;
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
			convertView = inflater.inflate(R.layout.item_index_news, null, false);
			holder.title=(TextView) convertView.findViewById(R.id.item_title);
			holder.time=(TextView) convertView.findViewById(R.id.item_publish_time);
			holder.intro = (TextView)convertView.findViewById(R.id.item_abstract);
			holder.type = (TextView)convertView.findViewById(R.id.item_type);
			holder.source = (TextView)convertView.findViewById(R.id.item_source);
			holder.right_image=(ImageView) convertView.findViewById(R.id.right_image);			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(item.getTitle());
		holder.time.setText(TimeUtil.friendlyFormat(item.getTime()));//将时间戳转化为友好的格式：如几小时前
		if(!TextUtils.isEmpty(item.getIntro()))
			holder.intro.setText(item.getIntro());
		else
			holder.intro.setVisibility(View.GONE);
		holder.type.setText(item.getView()+"人浏览");
		holder.source.setText(item.getSource());

		/*List<String> imgList = item.getImgList();
		if(imgList!=null&&imgList.size()>0){
			if(imgList.size()<3){
				holder.right_image.setVisibility(View.VISIBLE);
				convertView.findViewById(R.id.item_image_layout).setVisibility(View.GONE);
				bm.display(holder.right_image,imgList.get(0));
			}else{
				holder.right_image.setVisibility(View.GONE);
				convertView.findViewById(R.id.item_image_layout).setVisibility(View.VISIBLE);
				bm.display(holder.image1,imgList.get(0));
				bm.display(holder.image2,imgList.get(1));
				bm.display(holder.image3,imgList.get(2));
			}
		}else{
			holder.right_image.setVisibility(View.GONE);
			convertView.findViewById(R.id.item_image_layout).setVisibility(View.GONE);
		}*/
		String cover = item.getCover();
		if(cover!=null&&!"".equals(cover)){
				holder.right_image.setVisibility(View.VISIBLE);
				//bm.display(holder.right_image,cover);
				DisplayImageOptions options = new DisplayImageOptions.Builder()
		        .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
		        .showImageOnFail(R.drawable.white)              //加载失败时的图片  
		        .cacheInMemory()                               //启用内存缓存  
		        .cacheOnDisc()                                 //启用外存缓存  
		        .build();
		
				ImageLoader imageLoader = ImageLoader.getInstance();

				imageLoader.displayImage(cover,holder.right_image,options);
		}else{
			holder.right_image.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	static class ViewHolder {
		TextView title;
		TextView intro;
		TextView time;
		TextView type;
		TextView source;
		ImageView image1;
		ImageView image2;
		ImageView image3;
		ImageView right_image;
	}

}
