package com.yuol.smile.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuol.smile.R;
import com.yuol.smile.activity.WeiboContent;
import com.yuol.smile.utils.RegUtils;
import com.yuol.smile.utils.TimeUtil;
import com.yuol.smile.widgets.EmotionBox;

public class Comment2Adapter extends BaseAdapter {
	
	private List<Map<String,Object>> list;
	private LayoutInflater inflater;
	private Context context;
	

	
	public Comment2Adapter(Context context,List<Map<String,Object>> list){
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
			convertView = inflater.inflate(R.layout.item_comment_2, null, false);
			holder.name=(TextView) convertView.findViewById(R.id.comment_2_nick);
			holder.time=(TextView) convertView.findViewById(R.id.comment_2_time);
			holder.content=(TextView) convertView.findViewById(R.id.comment_2_content);
			holder.head=(ImageView) convertView.findViewById(R.id.comment_2_head);
			holder.content_attach =(TextView) convertView.findViewById(R.id.comment_2_content_attach);
			holder.btn_response =(Button) convertView.findViewById(R.id.btn_response);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final String str_name=map.get("nick").toString();
		if(str_name.trim().length()==0){
			holder.name.setText("匿名用户");
		}else{
			holder.name.setText(str_name);
		}
		
		holder.time.setText(TimeUtil.getCommentTime(Long.parseLong(map.get("time").toString())));
		
		//bm.display(holder.head, map.get("user_image").toString());		
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
        .showImageOnFail(R.drawable.white)              //加载失败时的图片  
        .cacheInMemory()                               //启用内存缓存  
        .cacheOnDisc()                                 //启用外存缓存  
        .build();

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(map.get("head_image").toString(),holder.head,options);
		

		holder.btn_response.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				WeiboContent.targetUser = str_name;
				WeiboContent.emotionBox.SetValue("");
				WeiboContent.emotionBox.setEditHint("@" +WeiboContent.targetUser);
			}
		});
		String str = map.get("content").toString();
		if(str.contains("@")){
			int start = str.indexOf("@");
			int end = str.indexOf("：");
			holder.content_attach.setVisibility(View.VISIBLE);
			holder.content_attach.setText(str.substring(start, end));
			str = str.substring(end,str.length());
		}else{
			holder.content_attach.setVisibility(View.GONE);
		}
		
		holder.content.setText(EmotionBox.convertNormalStringToSpannableString(context,
				RegUtils.replaceImage(str)),BufferType.SPANNABLE);
		holder.content.setMovementMethod(LinkMovementMethod.getInstance());
		
		
		return convertView;
	}
	
	static class ViewHolder {
		ImageView head;
		Button btn_response;
		TextView content_attach;
		TextView name;
		TextView time;
		TextView content;
	}

}
