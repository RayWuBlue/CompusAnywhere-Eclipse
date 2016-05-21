package com.yuol.smile.adapter;
import java.util.List;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yuol.smile.R;
import com.yuol.smile.bean.Yelpag;

public class YelpagAdapter extends BaseAdapter {
	private Context context;
	private List<Yelpag> list;
	public YelpagAdapter(Context context, List<Yelpag> list) {
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
	public Yelpag getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder;
		DialListener diallistener=null;
		CopyListener copylistener=null;
		if(convertView==null){
			viewholder=new ViewHolder();
			diallistener=new DialListener(position);
			copylistener=new CopyListener(position);
			convertView=LayoutInflater.from(context).inflate(R.layout.item_phone_number, null);
			viewholder.copy=(Button) convertView.findViewById(R.id.yp_lv_copy);
			viewholder.dial=(Button) convertView.findViewById(R.id.yp_lv_dial);
			viewholder.dep=(TextView)convertView.findViewById(R.id.yp_lv_dep);
			viewholder.tel=(TextView)convertView.findViewById(R.id.yp_lv_tel);
			viewholder.pos=(TextView)convertView.findViewById(R.id.yp_lv_pos);
			convertView.setTag(viewholder);
		}else{
			viewholder=(ViewHolder)convertView.getTag();
			diallistener=new DialListener(position);
			copylistener=new CopyListener(position);
		}
		viewholder.copy.setTag(position);
		viewholder.dial.setTag(position);
		viewholder.copy.setOnClickListener(copylistener);
		viewholder.dial.setOnClickListener(diallistener);
		viewholder.dep.setText(list.get(position).getDepartment());
		viewholder.pos.setText(list.get(position).getPosition());
		viewholder.tel.setText(list.get(position).getTel());
		return convertView;
	}
	public final class ViewHolder{
		public TextView pos;
		public TextView dep;
		public TextView tel;
		public Button dial;
		public Button copy;
	}
	private class DialListener implements OnClickListener{
		private int position;
		public DialListener(int position){
			this.position=position;
		}
		@Override
		public void onClick(View arg0) {
			Intent it=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+list.get(position).getTel()));
			context.startActivity(it);
		}
		
	}
	private class CopyListener implements OnClickListener{
		private int position;
		public CopyListener(int position){
			this.position=position;
		}
		@Override
		public void onClick(View arg0) {
			ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(list.get(position).getTel());
			Toast.makeText(context,list.get(position).getTel()+"ÒÑ¸´ÖÆµ½¼ôÌù°å", Toast.LENGTH_SHORT).show();
		}
		
	}

}
