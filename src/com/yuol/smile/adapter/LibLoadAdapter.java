package com.yuol.smile.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuol.smile.LibLoadInfo;
import com.yuol.smile.LibLoadInfo.MyThread;
import com.yuol.smile.R;
import com.yuol.smile.bean.LibLoanBook;

public class LibLoadAdapter extends BaseAdapter {
    private List<LibLoanBook> list;
    private LibLoadInfo entity;
    private MyThread mythread;
    public LibLoadAdapter(List<LibLoanBook> list,LibLoadInfo entity,MyThread thread){
    	this.list=list;
    	this.entity=entity;
    	this.mythread=thread;
    }
	@Override
	public int getCount() {
		if(list!=null)
		   return list.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO 自动生成的方法存�?
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO 自动生成的方法存�?
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		//ReLoadListener listener=null;
		if(arg1==null){
			arg1=LayoutInflater.from(entity).inflate(R.layout.lib_load_item,null);
			//listener=new ReLoadListener(arg0);
		}
		((TextView)arg1.findViewById(R.id.book_title)).setText(list.get(arg0).getTitle());
		((TextView)arg1.findViewById(R.id.book_author)).setText(list.get(arg0).getAuthor());
		((TextView)arg1.findViewById(R.id.ask_num)).setText(list.get(arg0).getAsk_num());
		((TextView)arg1.findViewById(R.id.bar_code)).setText(list.get(arg0).getBar_num());
		((TextView)arg1.findViewById(R.id.book_address)).setText(list.get(arg0).getAddress());
		((TextView)arg1.findViewById(R.id.book_type)).setText(list.get(arg0).getType());
		((TextView)arg1.findViewById(R.id.book_statu)).setText(list.get(arg0).getStatu());
		((TextView)arg1.findViewById(R.id.book_statu)).setTag(arg0);
		((TextView)arg1.findViewById(R.id.book_statu)).setOnClickListener(new ReLoadListener(arg0));
		((TextView)arg1.findViewById(R.id.load_num)).setText(list.get(arg0).getLoad_num());
		((TextView)arg1.findViewById(R.id.load_time)).setText(list.get(arg0).getLoad_time());
		((TextView)arg1.findViewById(R.id.return_time)).setText(list.get(arg0).getReturn_time());
        if(arg0%2==0)
        	arg1.setBackgroundColor(entity.getResources().getColor(R.color.act_bg));
        else 
        	arg1.setBackgroundColor(entity.getResources().getColor(R.color.white));
		return arg1;
	}
	private class ReLoadListener implements OnClickListener{
		private int position;
		public ReLoadListener(int position){
			this.position=position;
		}
		@Override
		public void onClick(View arg0) {
			AlertDialog.Builder builder_login=new Builder(entity);
			builder_login.setTitle("书籍续�?");
			builder_login.setMessage(""+list.get(position).getTitle());
			builder_login.setCancelable(false);
			builder_login.setNegativeButton("取消",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder_login.setPositiveButton("确定",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					entity.v_barno=list.get(position).getV_barno();
					new Thread(mythread).start();
				}
			});
			Dialog dialog_login=builder_login.create();
			dialog_login.show();
			
		}
		
	}
}
