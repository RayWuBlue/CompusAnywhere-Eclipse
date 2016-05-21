package com.yuol.smile.adapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuol.smile.R;
import com.yuol.smile.bean.JobNews;

public class Job_NewsAdapter extends BaseAdapter {
    private ArrayList<JobNews> jobNews;
    private Context context;
	public Job_NewsAdapter(ArrayList<JobNews> jobNews,Context context){
		this.jobNews=jobNews;
		this.context=context;
	}
	@Override
	public int getCount() {
		return jobNews.size();
	}

	@Override
	public Object getItem(int position) {
		return jobNews.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_job_news, null);
		}
		((TextView)convertView.findViewById(R.id.job_news_title)).setText(jobNews.get(position).getTitle());
		((TextView)convertView.findViewById(R.id.job_news_time)).setText(jobNews.get(position).getTime());
		((TextView)convertView.findViewById(R.id.job_news_click)).setText(jobNews.get(position).getClick());
		return convertView;
	}
	/**
	 * 添加新闻
	 * @param list
	 */
    public void addNews(List<JobNews> list){
    	for(JobNews n:list){	
    		jobNews.add(n);
    	}
    }

}
