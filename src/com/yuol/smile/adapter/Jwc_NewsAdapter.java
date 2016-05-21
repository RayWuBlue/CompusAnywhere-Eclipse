package com.yuol.smile.adapter;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuol.smile.R;
import com.yuol.smile.bean.JwcNews;

public class Jwc_NewsAdapter extends BaseAdapter {
    private List<JwcNews> jwcNews;
    private Context context;
	public Jwc_NewsAdapter(List<JwcNews> list,Context context){
		this.jwcNews=list;
		this.context=context;
	}
	@Override
	public int getCount() {
		return jwcNews.size();
	}

	@Override
	public Object getItem(int position) {
		return jwcNews.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null)
		     convertView = LayoutInflater.from(context).inflate(R.layout.item_jwc_news, null);
		TextView title = (TextView) convertView.findViewById(R.id.jwc_news_title);
		TextView time=(TextView) convertView.findViewById(R.id.jwc_news_time);
		title.setText(jwcNews.get(position).getTitle());
		time.setText(jwcNews.get(position).getTime());
		return convertView;
	}
}
