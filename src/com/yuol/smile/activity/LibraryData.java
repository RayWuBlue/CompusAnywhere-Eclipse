package com.yuol.smile.activity;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.yuol.smile.R;
import com.yuol.smile.adapter.LibraryShowAdapter;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.helper.JsonHelper;

public class LibraryData extends BaseActivity {
	
	private ListView listview;
	private List<Map<String,Object>> list;
	private String params;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.act_library_data);
		setTitle("Õº Èπ›");
		
		Intent it=getIntent();
		listview=(ListView) super.findViewById(R.id.library_data_lv);
		String json=it.getStringExtra("jsondata");
		try {
			list=new JsonHelper(json).parseJson(
					new String[]{"book","borrow","return","num","id","code"}, 
					new String[]{"","","","","",""});
		} catch (Exception e) {
			e.printStackTrace();
		}
		params="uid="+it.getStringExtra("uid")+"&pwd="+it.getStringExtra("pwd");
		listview.setAdapter(new LibraryShowAdapter(this, list,params));
	}


}
