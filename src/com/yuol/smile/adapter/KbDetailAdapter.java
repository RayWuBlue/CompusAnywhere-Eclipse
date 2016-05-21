package com.yuol.smile.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuol.smile.R;
import com.yuol.smile.bean.Course;

public class KbDetailAdapter extends BaseAdapter {
	private Course[][] kb_content;
	private int week = 0;
	private Context mContext;
	public static String[] lesson_time={"08:00-09:35","10:05-11:40","14:00-15:35","16:05-17:40","19:00-20:35","21:05-21:40"};
    public static String[] lesson_no={"一","二","三","四","五","六"};
	public KbDetailAdapter(Context mContext, Course[][] kb_content,int week) {
		this.mContext=mContext;
		this.kb_content = kb_content;
		this.week=week;
	}
	@Override
	public int getCount() {
		if(kb_content==null)
			return 0;
		else
		   return kb_content.length;
	}

	@Override
	public Object getItem(int position) {
		return this.kb_content[week-1][position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) 
		    convertView = LayoutInflater.from(mContext).inflate(R.layout.kb_item, null);
		if(position<6){
			((TextView)convertView.findViewById(R.id.kb_lesson)).setText("第"+lesson_no[(position)]+"节");
			((TextView)convertView.findViewById(R.id.kb_time)).setText(lesson_time[position]);
			if(kb_content[week-1][position]!=null){
				convertView.findViewById(R.id.kb_layout).setVisibility(View.VISIBLE);
				((TextView) convertView.findViewById(R.id.kb_name)).setText(kb_content[week-1][position].getName());
				((TextView) convertView.findViewById(R.id.kb_teacher)).setText(kb_content[week-1][position].getTeacher());
				String addr = kb_content[week-1][position].getAddress();
				if("".equals(addr)||null==addr)
				{
					((TextView) convertView.findViewById(R.id.kb_class)).setVisibility(View.GONE);
					((TextView) convertView.findViewById(R.id.kb_week)).setVisibility(View.GONE);
				}
				else
				{
					((TextView) convertView.findViewById(R.id.kb_class)).setText(addr);
					((TextView) convertView.findViewById(R.id.kb_week)).setText(kb_content[week-1][position].getTime());
				}
			}else{
				convertView.findViewById(R.id.kb_layout).setVisibility(View.GONE);
			}
		}
		return convertView;
	}
}
