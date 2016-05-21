package com.yuol.smile.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuol.smile.R;
import com.yuol.smile.activity.WeiboContent;
import com.yuol.smile.base.SchoolFellowBase;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.RegUtils;
import com.yuol.smile.utils.T;
import com.yuol.smile.utils.TimeUtil;
import com.yuol.smile.utils.ViewHolder;
import com.yuol.smile.widgets.EmotionBox;

public class SchoolFellowAdapter extends BaseAdapter {
	
	private List<SchoolFellowBase> list;
	private Context context;
	public ListView lv;
	public SchoolFellowAdapter(Context context,List<SchoolFellowBase> list){
		this.list=list;
		this.context=context;
	}

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
	
	public void update(){
		notifyDataSetChanged();
	}
	


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
	        convertView = LayoutInflater.from(context)
	          .inflate(R.layout.item_school_fellow_lv, parent, false);
	    }
		 ImageView head = ViewHolder.get(convertView, R.id.schoolfellow_item_user_img);
		 TextView nick=ViewHolder.get(convertView, R.id.schoolfellow_item_user_nick);
		 TextView time=ViewHolder.get(convertView, R.id.schoolfellow_item_time);
		 TextView content=ViewHolder.get(convertView, R.id.schoolfellow_item_content);
		 
		 LinearLayout ll_single=ViewHolder.get(convertView, R.id.image_layout_single);

		 TextView commentBtn=ViewHolder.get(convertView, R.id.schoolfellow_item_bar_comment);
		 TextView praiseBtn=ViewHolder.get(convertView, R.id.schoolfellow_item_bar_praise);
		 
		 SchoolFellowBase item=list.get(position);
		 

		 praiseBtn.setText(item.getLikeNum());
		 
		 content.setOnClickListener(new onBarClickListener(position));
		 commentBtn.setOnClickListener(new onBarClickListener(position));
		 praiseBtn.setOnClickListener(new onBarClickListener(position));
		 
		 Drawable drawable=null;
		 if(item.isHasLike()){
			 drawable = context.getResources().getDrawable(R.drawable.timeline_icon_like);
		 }else{
			 drawable = context.getResources().getDrawable(R.drawable.timeline_icon_like_disable);
		 }
		 drawable.setBounds(0, 0, 45, 45);
	     praiseBtn.setCompoundDrawables(drawable, null, null, null);
		
	     drawable = context.getResources().getDrawable(R.drawable.timeline_icon_comment);
	     drawable.setBounds(0, 0, 45, 45);
	     commentBtn.setCompoundDrawables(drawable, null, null, null);
	     
		 nick.setText(item.getNickname());
		 time.setText(TimeUtil.getSquareTime(Long.parseLong(item.getTime())));
		 commentBtn.setText(item.getCommentNum());

		 //BitmapHelper.setHead(headBm, head, item.getUimage());
		 //bm.display(head, item.getUimage());
			DisplayImageOptions options = new DisplayImageOptions.Builder()
	        .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
	        .showImageOnFail(R.drawable.white)              //加载失败时的图片  
	        .cacheInMemory()                               //启用内存缓存  
	        .cacheOnDisc()                                 //启用外存缓存  
	        .build();

			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(item.getUimage(),head,options);
		 //获取发布内容中的图片地址
		 final List<String> imgList=item.getImgList();
		 ImageView image_single=ViewHolder.get(convertView, R.id.item_img_single);
		 if(imgList!=null){
			 ll_single.setVisibility(View.VISIBLE);
			 //bm.display(image_single, imgList.get(0));
			 imageLoader.displayImage(imgList.get(0),image_single,options);
		 }else{
			 ll_single.setVisibility(View.GONE);
		 }
		content.setText(EmotionBox.convertNormalStringToSpannableString(context,
				RegUtils.replaceImage(item.getContent())),BufferType.SPANNABLE);
		return convertView;
	}




	//心情工具条按钮事件
	class onBarClickListener implements OnClickListener{
		public int position;
		public onBarClickListener(int position){
			this.position=position;
		}
		public void onClick(final View v) {
			final SchoolFellowBase item = (SchoolFellowBase)getItem(position);
			switch(v.getId()){
			case R.id.schoolfellow_item_content:
			case R.id.schoolfellow_item_bar_comment:
				Intent it=new Intent(context,WeiboContent.class);
				Bundle mBundle = new Bundle();  
			    mBundle.putSerializable(Api.SER_KEY,item);
			    mBundle.putInt("position", position);
			    it.putExtras(mBundle); 
				context.startActivity(it);
				break;
			case R.id.schoolfellow_item_bar_praise:
				new AsyncTask<Integer, Void, String>() {
					@Override
					protected String doInBackground(Integer... params) {
						return GetUtil.getRes(Api.Weibo.postLike(new LoginHelper(context).getUid(), params[0]+""));
					}
					@Override
					protected void onPostExecute(String result) {
						JSONObject ret =  JSON.parseObject(result);
						if(ret.getInteger("retCode")==200){
							int likeNum = Integer.parseInt(item.getLikeNum())+1;
							item.setLikeNum(likeNum+"");
							((TextView)v).setText(item.getLikeNum());
							item.setHasLike(true);
						     Drawable drawable = context.getResources().getDrawable(R.drawable.timeline_icon_like);
						     drawable.setBounds(0, 0, 45, 45);
						     ((TextView)v).setCompoundDrawables(drawable, null, null, null);
						}
						else
							T.showShort(context, ret.getString("error"));
					}
				}.execute(item.getId());
				break;
			default:
				break;
			}
		}
		
	}
}
