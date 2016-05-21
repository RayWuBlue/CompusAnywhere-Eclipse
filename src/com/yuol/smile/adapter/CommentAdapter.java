package com.yuol.smile.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuol.smile.R;
import com.yuol.smile.utils.RegUtils;
import com.yuol.smile.utils.TimeUtil;
import com.yuol.smile.widgets.EmotionBox;

public class CommentAdapter extends BaseAdapter {
	
	private List<Map<String,Object>> list;
	private LayoutInflater inflater;
	private Context context;
	public ListView lv;
	public CommentAdapter(Context context,List<Map<String,Object>> list){
		this.list=list;
		this.context=context;
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
		Map<String,Object> map=list.get(position);
		if (convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_comment_lv, null, false);
			holder.name=(TextView) convertView.findViewById(R.id.comment_lv_user);
			holder.time=(TextView) convertView.findViewById(R.id.comment_lv_time);
			holder.content=(TextView) convertView.findViewById(R.id.comment_lv_content);
			holder.head=(ImageView) convertView.findViewById(R.id.comment_lv_user_head);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(map.get("name").toString());
		holder.time.setText(TimeUtil.friendlyFormat(map.get("time").toString()));
/*		holder.content.setText(EmotionBox.convertNormalStringToSpannableString(context,
				RegUtils.replaceImage(map.get("content").toString())),BufferType.SPANNABLE);*/
		System.out.println("CommentAdapter:"+map.get("content").toString());
		holder.content.setText(EmotionBox.convertNormalStringToSpannableString(context,
				RegUtils.replaceImage(map.get("content").toString())),BufferType.SPANNABLE);
		//holder.content.setText(map.get("content").toString());

		//跳转至个人介绍页面
		String uid=map.get("uid").toString();
		holder.name.setOnClickListener(new Onclick(context,uid));
		holder.head.setOnClickListener(new Onclick(context,uid));
		
		//bm.display(holder.head, map.get("head_image").toString());
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
        .showImageOnFail(R.drawable.white)              //加载失败时的图片  
        .cacheInMemory()                               //启用内存缓存  
        .cacheOnDisc()                                 //启用外存缓存  
        .build();

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(map.get("head_image").toString(),holder.head,options);
		
		return convertView;
	}
	
	/**
	 * 跳转至个人介绍页面
	 * @author peng
	 *
	 */
	class Onclick implements OnClickListener{
		public String uid;
		public Context context;
		public Onclick(Context context,String uid){
			this.uid=uid;
			this.context=context;
		}
		public void onClick(View v) {
		}
		
	}
	
	static class ViewHolder {
		ImageView head;
		TextView name;
		TextView time;
		TextView content;
	}

}
