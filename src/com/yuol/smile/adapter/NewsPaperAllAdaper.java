package com.yuol.smile.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuol.smile.NewsPaperActivity;
import com.yuol.smile.R;
import com.yuol.smile.bean.NewsPaper;

public class NewsPaperAllAdaper extends BaseAdapter {
    private ArrayList<NewsPaper> list;
    private Context mContext;
    public NewsPaperAllAdaper(ArrayList<NewsPaper> list,Context mContext){
    	this.list=list;
    	this.mContext=mContext;
    }
	@Override
	public int getCount() {
		// TODO 自动生成的方法存�?
		if(list==null)
			return 0;
		else 
			return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存�?
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存�?
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null)
			convertView=LayoutInflater.from(mContext).inflate(R.layout.paper_all_tem,null);
		((TextView)convertView.findViewById(R.id.paper_issue_num)).setText(list.get(position).getIssue_num());
		((TextView)convertView.findViewById(R.id.paper_issue_time)).setText(list.get(position).getIssue_time());
		return convertView;
	}
	public void addNews(List<NewsPaper> list_){
    	//news.clear();
    	for(NewsPaper n:list_){	
    	list.add(n);
    	}
    }

}
