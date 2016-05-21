package com.yuol.smile.adapter;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuol.smile.R;
import com.yuol.smile.bean.NewsPaper;

public class PaperNowAdapter extends BaseAdapter {
    private ArrayList<NewsPaper> list;
    private Context context;
	public PaperNowAdapter(ArrayList<NewsPaper> list,Context context){
		this.list=list;
		this.context=context;
	}
	@Override
	public int getCount() {
		if(list==null)
			return 0;
		else
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
		if(convertView==null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_paper_now, null);
		}
		((TextView)convertView.findViewById(R.id.paper_now_time)).setText(list.get(position).getIssue_time());
		((TextView)convertView.findViewById(R.id.paper_now_title)).setText(list.get(position).getTitle());
		((TextView)convertView.findViewById(R.id.paper_now_num)).setText(list.get(position).getIssue_num());
		return convertView;
	}
	

}
