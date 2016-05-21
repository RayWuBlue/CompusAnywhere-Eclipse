package com.yuol.smile.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuol.smile.R;
import com.yuol.smile.bean.Score;

public class ScoreAdapter extends BaseAdapter {
    private List<Score> list;
    private Context context;
	public ScoreAdapter(Context context,List<Score> list2) {
		this.list = list2;
		this.context=context;
	}
	@Override
	public int getCount() {
		if(list!=null)
		return list.size();
		return 0;
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
		if(convertView==null)
			convertView=LayoutInflater.from(context).inflate(R.layout.jwc_score_item,null);
        if(list.get(position)!=null){
        	((TextView)convertView.findViewById(R.id.score_credit)).setText("学分："+list.get(position).getS_credit());
            ((TextView)convertView.findViewById(R.id.score_name)).setText("课程名称："+list.get(position).getS_name());
            String score;
            score = list.get(position).getS_num();
			if (Double.parseDouble(score)>=80)
            	((TextView)convertView.findViewById(R.id.score_num)).setTextColor(context.getResources().getColor(R.color.green));
            else if (Double.parseDouble(score)<60)
            	((TextView)convertView.findViewById(R.id.score_num)).setTextColor(context.getResources().getColor(R.color.red));
            ((TextView)convertView.findViewById(R.id.score_num)).setText("成绩："+score);
            //((TextView)convertView.findViewById(R.id.score_term)).setText("学期："+list.get(position).getS_term());
            ((TextView)convertView.findViewById(R.id.score_type)).setText("课程类别："+list.get(position).getS_type());
           // ((TextView)convertView.findViewById(R.id.score_year)).setText("年度："+list.get(position).getS_year());
            
        }
        if(position%2==0)
        	convertView.setBackgroundColor(context.getResources().getColor(R.color.act_bg));
        else
        	convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
		return convertView;
	}
	
}
