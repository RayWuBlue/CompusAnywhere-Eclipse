package com.yuol.smile.adapter;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuol.smile.R;
import com.yuol.smile.bean.ChannelItem;
import com.yuol.smile.widgets.RoundedImageView;

public class HomeChannelAdapter extends BaseAdapter {

	private List<ChannelItem> channels;
	LayoutInflater inflater = null;
	private Context activity;
	private int[] color = new int[]{R.drawable.shape_fill_circle_blue,R.drawable.shape_fill_circle_green,R.drawable.shape_fill_circle_yellow,R.drawable.shape_fill_circle_purple};
	private String[] str_color = new String[]{"#ff578e","#3b93bb","#40af5d",
												"#57ceff","#646464","#00335f",
												"#0ea7ff","#00deb2","#0097c9",
												"#00bc6a","#ff8282","#6cc2d4",
			};
	public HomeChannelAdapter(Context activity, List<ChannelItem> channels) {
		this.activity = activity;
		this.channels = channels;
		inflater = LayoutInflater.from(activity);
	}

	@Override
	public int getCount() {
		return channels.size();
	}

	@Override
	public Object getItem(int position) {
		return channels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return channels.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.item_home_channel_gv, null);
			mHolder = new ViewHolder();
			mHolder.title = (TextView)view.findViewById(R.id.item_title);
			mHolder.image = (ImageView)view.findViewById(R.id.item_image);
			mHolder.bg = view.findViewById(R.id.item_image_background);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		//获取position对应的数据
		ChannelItem item = (ChannelItem)getItem(position);
		mHolder.title.setText(item.getName());
		mHolder.image.setBackgroundResource(item.getCoverRes());
		//Random rand = new Random();
		//mHolder.bg.setBackgroundColor(Color.parseColor(str_color[position]));
		mHolder.bg.setBackgroundColor(activity.getResources().getColor(R.color.blue));
		return view;
	}

	static class ViewHolder {

		TextView title;

		ImageView image;
		
		View bg;
	}
}
