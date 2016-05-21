package com.yuol.smile.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuol.smile.R;
import com.yuol.smile.bean.NewsBean;

public class CollectAdapt extends BaseAdapter {
    private int resource;
    private List<NewsBean> news;
    private LayoutInflater layout;
    private Context context;
    static class ListItemView{
    	public TextView titleView;
    	public TextView timeView;
    	private ImageView imagview;
    	private TextView cloumnView;
    }
	public CollectAdapt(Context context, int resource, List<NewsBean> news) {
		this.resource = resource;
		this.news = news;
		this.layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context=context;
	}

	@Override
	public int getCount() {
		if(news!=null)
		return news.size();
		return 0;
	}
  
	@Override
	public Object getItem(int position) {
		return news.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView lItemView=null;
		if(convertView == null){
        	convertView=layout.inflate(resource, null);
        	lItemView=new ListItemView();
        	lItemView.titleView=(TextView) convertView.findViewById(R.id.collect_listitem_title);
        	lItemView.timeView=(TextView) convertView.findViewById(R.id.collect_listitem_date); 
        	lItemView.cloumnView=(TextView) convertView.findViewById(R.id.collect_listitem_cloumn);
        	convertView.setTag(lItemView);
        }else{
        	lItemView=(ListItemView) convertView.getTag();
        }
		 NewsBean one=news.get(position);
		 lItemView.titleView.setText(one.getTitle());
		 lItemView.timeView.setText(one.getTime());
		 lItemView.cloumnView.setText(one.getColumn());
	return convertView;
	}
	/**
	 * 添加新闻
	 * @param list
	 */
    public void addNewsBean(List<NewsBean> list){
    	//news.clear();
    	for(NewsBean n:list){	
    	news.add(n);
    	}
    }
}
