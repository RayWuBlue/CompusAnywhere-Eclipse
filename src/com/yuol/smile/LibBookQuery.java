package com.yuol.smile;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yuol.smile.base.BaseActivity;
public class LibBookQuery extends BaseActivity {
	private static Context context;
	private Myhandler myhandler;
	private RadioGroup rg_method=null;
	private EditText bq_text=null;
	private EditText bq_year_from=null;
	private EditText bq_year_to=null;
	private Spinner bq_pagenum=null;
	private RadioGroup rg_search_lib=null;
	private RadioGroup rg_search_way=null;
    private Button bq_sumbit=null;
    private TextView btn_detail=null;
    private View morell = null;
    private  String html;
    private String v_index;
    private String v_value;
    private String FLD_DAT_BEG;
    private String FLD_DAT_END;
    private String v_seldatabase;
    private String v_pagenum;
    private String v_LogicSrch;
    private boolean expanded = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lib_bookquery_main);
		initJsoup();
		initview();
	}
	private void initview(){
		setTitle("图书查询");
		addButton("图书查询", new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LibBookQuery.this,LibraryActivity.class));
			}
		});
		setTitleBarColor(getResources().getColor(R.color.title_bar_blue));
		morell = super.findViewById(R.id.lib_morell);
		rg_method=(RadioGroup)super.findViewById(R.id.bq_method);
		bq_text=(EditText)super.findViewById(R.id.bq_text);
		btn_detail=(TextView)super.findViewById(R.id.lib_more);
		bq_year_to=(EditText)super.findViewById(R.id.bq_year_to);
		bq_year_from=(EditText)super.findViewById(R.id.bq_year_from);
		rg_search_lib=(RadioGroup)super.findViewById(R.id.bq_search_lib);
		rg_search_way=(RadioGroup)super.findViewById(R.id.bq_search_way);
		bq_sumbit=(Button)super.findViewById(R.id.bq_sumbit);
		bq_sumbit.setOnClickListener(new submitOnClickListener());
		bq_pagenum=(Spinner)super.findViewById(R.id.bq_pagenum);
		bq_pagenum.setAdapter(new ArrayAdapter<String>(LibBookQuery.this,android.R.layout.simple_spinner_item,new String[]{"10","15","20"}));
	
		btn_detail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!expanded){
				btn_detail.setText("收起>");
				morell.setVisibility(View.VISIBLE);
				}
				else{
					btn_detail.setText("详细搜索>");
					morell.setVisibility(View.GONE);
					}
				expanded=!expanded;
			}
		});
	}
	private void initJsoup(){
		context=LibBookQuery.this;
		myhandler=new Myhandler();
	}
	private class submitOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(bq_text.getText().toString()==null||bq_text.getText().toString().equals(""))
				Toast.makeText(LibBookQuery.this,"请输入搜索关键词",Toast.LENGTH_SHORT).show();
			else{
			    new Mythread().start();
			}
		}
		
	}
	private void getDoc(){
		v_value=bq_text.getText().toString();
		FLD_DAT_BEG=bq_year_from.getText().toString();
		FLD_DAT_END=bq_year_to.getText().toString();
		v_pagenum=bq_pagenum.getSelectedItem().toString();
	    Looper.prepare();
		getRgStatu();
		HttpPost post=new HttpPost("http://online.yangtzeu.edu.cn/app/app_data.php");
		ArrayList<NameValuePair> list=new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("act","lib"));
		list.add(new BasicNameValuePair("m","search"));
		list.add(new BasicNameValuePair("v_index",v_index));
		list.add(new BasicNameValuePair("v_value",v_value));
		list.add(new BasicNameValuePair("FLD_DAT_BEG",FLD_DAT_BEG));
		list.add(new BasicNameValuePair("FLD_DAT_END",FLD_DAT_END));
		list.add(new BasicNameValuePair("v_pagenum",v_pagenum));
		list.add(new BasicNameValuePair("v_seldatabase",v_seldatabase));
		list.add(new BasicNameValuePair("v_LogicSrch",v_LogicSrch));
		list.add(new BasicNameValuePair("submit",""));
		list.add(new BasicNameValuePair("reset",""));
		try {
			post.setEntity(new UrlEncodedFormEntity(list,"gb2312"));
			HttpResponse response=new DefaultHttpClient().execute(post);
			if(response.getStatusLine().getStatusCode()==200){
				html=EntityUtils.toString(response.getEntity());
				System.out.println("杩炴帴鎴愬姛"+html);
			}
			else System.out.println(response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
		}		
		//Looper.loop(); 
	}
	private void getRgStatu(){
		switch(rg_method.getCheckedRadioButtonId()){
		case R.id.bq_method_1:
			v_index="TITLE";
			break;
		case R.id.bq_method_2:
			v_index="AUTHOR";
			break;
		case R.id.bq_method_3:
			v_index="SUBJECT";
			break;
		case R.id.bq_method_4:
			v_index="CLASSNO";
			break;
		case R.id.bq_method_5:
			v_index="ISBN";
			break;
		case R.id.bq_method_6:
			v_index="CALLNO";
			break;
		}
		switch(rg_search_lib.getCheckedRadioButtonId()){
		case R.id.bq_search_lib_1:
			v_seldatabase="0";
			break;
		case R.id.bq_search_lib_2:
			v_seldatabase="1";
			break;
		case R.id.bq_search_lib_3:
			v_seldatabase="2";
			break;
		}
		switch(rg_search_way.getCheckedRadioButtonId()){
		case R.id.bq_search_way_1:
			v_LogicSrch="0";
			break;
		case R.id.bq_search_way_2:
			v_LogicSrch="1";
			break;
		}
	}
	public  class Myhandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				redictTo();
				break;
			case 1:
				break;
			}
		}
		
	}
	public class Mythread extends Thread{
		@Override
		public void run() {
			getDoc();
			if(html==null||html.equals("")){
				myhandler.sendEmptyMessage(1);
			}else
			myhandler.sendEmptyMessage(0);
		}
		
	}
	private  void redictTo(){
		Intent intent=new Intent(LibBookQuery.this,LibBookList.class);
		Bundle bundle=new Bundle ();
		bundle.putString("html",html);
		bundle.putString("v_index", v_index);
		bundle.putString("v_value", v_value);
		bundle.putString("v_pagenum", v_pagenum);
		bundle.putString("FLD_DAT_BEG", FLD_DAT_BEG);
		bundle.putString("FLD_DAT_END", FLD_DAT_END);
		bundle.putString("v_seldatabase", v_seldatabase);
		bundle.putString("v_LogicSrch", v_LogicSrch);
		intent.putExtras(bundle);
		startActivity(intent);
	}


}
