package com.yuol.smile.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuol.smile.R;
import com.yuol.smile.bean.VideoNews;

public class VideoNewsAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<VideoNews> newsList;
	private LayoutInflater inflater = null;

	public VideoNewsAdapter(Context mContext, ArrayList<VideoNews> cityList) {
		this.mContext = mContext;
		this.newsList = cityList;
		inflater = LayoutInflater.from(mContext);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return newsList == null ? 0 : newsList.size();
	}

	@Override
	public VideoNews getItem(int position) {
		// TODO Auto-generated method stub
		if (newsList != null && newsList.size() != 0) {
			return newsList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.video_news_item, null);
			mHolder = new ViewHolder();
			mHolder.news_title = (TextView) view.findViewById(R.id.news_title);
			// header
			mHolder.news_time = (TextView) view.findViewById(R.id.news_time);
			mHolder.news_click = (TextView) view.findViewById(R.id.news_click);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		VideoNews news = getItem(position);
		mHolder.news_title.setText(news.getTitle());
		mHolder.news_time.setText(news.getTime());
		mHolder.news_click.setText(news.getClick());
		return view;
	}

	class ViewHolder {
		TextView news_title;
		TextView news_time;
		TextView news_click;
	}
}
