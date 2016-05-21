package com.yuol.smile.adapter;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuol.smile.R;
import com.yuol.smile.bean.LibCollectInfo;

public class LibCollectAdapter extends BaseAdapter {

	private Context context;
	private List<LibCollectInfo> list;
	public LibCollectAdapter(Context context, List<LibCollectInfo> list) {
		this.context = context;
		this.list=list;
	}

	@Override
	public int getCount() {
		if(list!=null)
			return list.size();
	    else
	    	return 0;
	}

	@Override
	public LibCollectInfo getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null)
		     convertView = LayoutInflater.from(context).inflate(R.layout.lib_book_collect_item, null);
		((TextView) convertView.findViewById(R.id.book_item_barCode)).setText(list.get(position).getBarCode());
		((TextView) convertView.findViewById(R.id.book_item_address)).setText(list.get(position).getAddress());
		((TextView) convertView.findViewById(R.id.book_item_explain)).setText(list.get(position).getExplain());
		((TextView) convertView.findViewById(R.id.book_item_statu)).setText(list.get(position).getStatu());
		((TextView) convertView.findViewById(R.id.book_item_type)).setText(list.get(position).getType());
		((TextView) convertView.findViewById(R.id.book_item_returnDate)).setText(list.get(position).getReturnDate());
		return convertView;
	}
	

}
