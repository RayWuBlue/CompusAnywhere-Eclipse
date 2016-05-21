package com.yuol.smile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.helper.ScheduleHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.MyAlertDialog;
import com.yuol.smile.widgets.MyAlertDialog.MyDialogInt;
import com.yuol.smile.widgets.MyProgressBar;

public class KbQuery extends BaseActivity {
	private String url=Api.Jwc.getJwcKb();
    private Spinner sp_year=null;
    private Spinner sp_semester=null;
    private Spinner sp_type=null;
    private Spinner sp_department=null;
    private Spinner sp_class=null;
    private Button kb_query_sumbit=null;
    private String[] items_year=new String[6];
    private int c_year;
    private int c_semester;//0表示上学期，1表示下学期
    private Map<String,String> items_semester=null;
    private Map<String, String> items_type=null;
    private Map<String, String> items_department=null;
    private Map<String, String> items_class=null;
    private String __VIEWSTATE;
    private String __EVENTVALIDATION;
    private String kb_doc=null;
    private Map<String, String> params = new LinkedHashMap<String, String>();
    private Myhandler myhandler;
    private SharedPreferences sp_cache;
    private Map<String,String> map;//cookie
	private MyProgressBar mpb;
	private MyAlertDialog mad;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kb_query_main);
		initJsoup();
		initTerm();//自动识别当前年份学期
		initview();
		map=new HashMap<String,String>();
		map.put("ASP.NET_SessionId",new LoginHelper(KbQuery.this).getJwcCookie());
	}
	private void initview(){
		setTitle("添加课表");
		sp_cache = this.getSharedPreferences("share", MODE_PRIVATE); 
		items_semester=new HashMap<String,String>();
		items_type=new HashMap<String,String>();
		items_semester.put("下学期","2");
		items_semester.put("上学期","1");
		items_type.put("学生课表","1");
		items_type.put("教师课表","2");
		kb_query_sumbit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mpb = new MyProgressBar(KbQuery.this);
				mpb.setMessage("正在查询...");
				new MyThread(2).start();
				
			}
		});
		sp_year=(Spinner)super.findViewById(R.id.kb_year);
		sp_semester=(Spinner)super.findViewById(R.id.kb_semester);
		sp_type=(Spinner)super.findViewById(R.id.kb_type);
		sp_department=(Spinner)super.findViewById(R.id.kb_department);
		sp_department.setAdapter(null);
		sp_class=(Spinner)super.findViewById(R.id.kb_class);
		if(items_year==null)
			System.out.println("item null");
		sp_year.setAdapter(new ArrayAdapter<String>(KbQuery.this,android.R.layout.simple_spinner_item,items_year));
		sp_semester.setAdapter(new ArrayAdapter<String>(KbQuery.this,android.R.layout.simple_spinner_item,new ArrayList<String>(items_semester.keySet())));
		sp_type.setAdapter(new ArrayAdapter<String>(KbQuery.this,android.R.layout.simple_spinner_item,new ArrayList<String>(items_type.keySet())));
		sp_class.setAdapter(null);
		sp_year.setSelection(4);//一共显示六年，当前年份处于第二年
		sp_semester.setSelection(c_semester);
		sp_class.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(arg2!=0){
					kb_query_sumbit.setEnabled(true);
					kb_query_sumbit.setText("查询");
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		sp_department.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				kb_query_sumbit.setText("正在获取班级信息...");
				new MyThread(1).start();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}
	private void initJsoup(){
		kb_query_sumbit=(Button)super.findViewById(R.id.kb_query_sumbit);
		kb_query_sumbit.setEnabled(false);
		kb_query_sumbit.setText("正在获取院系信息...");
		myhandler=new Myhandler();
		new MyThread(0).start();
		
	}
	public class Myhandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0://初始化院系spinner
				List<String> list = new ArrayList<String>(items_department.keySet());
				sp_department.setAdapter(new ArrayAdapter<String>(KbQuery.this,android.R.layout.simple_spinner_item,list));
				kb_query_sumbit.setText("请选择院系");
				kb_query_sumbit.setEnabled(false);
				break;
			case 1://获取班级class
				sp_class.setAdapter(new ArrayAdapter<String>(KbQuery.this,android.R.layout.simple_spinner_item,new ArrayList<String>(items_class.keySet())));
				kb_query_sumbit.setText("请选择班级");
				kb_query_sumbit.setEnabled(false);
				break;
			case 2:
				ScheduleHelper sh = new ScheduleHelper(KbQuery.this);
				sh.setCurrentKB(sp_class.getSelectedItem().toString(), kb_doc);
				T.show(KbQuery.this, "添加成功！", Toast.LENGTH_SHORT);
				mpb.dismiss();
				finish();
				break;
			case 3:  
				kb_query_sumbit.setEnabled(false);
				mad = new MyAlertDialog(KbQuery.this);
				mad.setTitle("消息提示");
				mad.setMessage("请确保你的网络可用，并选择院系及班级");
				mad.setLeftButton("重试", new MyDialogInt() {
					@Override
					public void onClick(View view) {
						mad.dismiss();
						kb_query_sumbit.setEnabled(true);
						new MyThread(0).start();
						return;
					}
				});
				mad.setRightButton("取消", new MyDialogInt() {
					@Override
					public void onClick(View view) {
						mad.dismiss();
						finish();
					}
				});
				break;
			case 4:
				finish();
				break;
			case 5:
				Toast.makeText(KbQuery.this, "本地暂无课表，查询后将自动缓存", Toast.LENGTH_LONG).show();
			}
		}
		
	}

	public class MyThread extends Thread{
		private int statu;
		public MyThread(int statu){
			this.statu=statu;
	}
		@Override
		public void run() {
			int k;
			try{
				Document doc=null;
				switch(statu){
				case 0://查询院系
					for(k=0;k<3;k++){//三次尝试
						System.out.println("第"+(k+1)+"次尝试");
						try{
							items_department=new LinkedHashMap<String, String>();
							//Jsoup.connect(url).data(params).cookies(map).timeout(10*1000).post();			
							doc=Jsoup.connect(url).cookies(map).get();
							if(doc==null)
								return;
							__VIEWSTATE=doc.select("#__VIEWSTATE").val();
							__EVENTVALIDATION=doc.select("#__EVENTVALIDATION").val();
							Elements ele_year=doc.select("#selDepart").select("option");
							for(int i=0;i<ele_year.size();i++){
								Element e=ele_year.get(i);
								items_department.put(e.text(),e.val());
							}
							if(items_department.size()!=0){
								myhandler.sendEmptyMessage(0);
								break;
							}
						}catch(Exception e){
							System.out.println("第"+(k+1)+"次尝试失败");
						}
					}
					if(k==3){
						myhandler.sendEmptyMessage(3);
					}
					break;
				case 1://查询班级
					items_class=new LinkedHashMap<String, String>();
					for(k=0;k<3;k++){
						try{
							setParams("selDepart",sp_year.getSelectedItem().toString(),items_semester.get(sp_semester.getSelectedItem().toString()),items_department.get(sp_department.getSelectedItem().toString()),"174837",items_type.get(sp_type.getSelectedItem().toString()));
						    doc = Jsoup.connect(url).data(params).cookies(map).timeout(20 * 1000).post();
						    __VIEWSTATE=doc.select("#__VIEWSTATE").val();
							__EVENTVALIDATION=doc.select("#__EVENTVALIDATION").val();
							Elements ele_class=doc.select("#selClass").select("option");
							
							for(int i=0;i<ele_class.size();i++){
								Element e=ele_class.get(i);
								items_class.put(e.text(),e.val());
							}
							if(items_class.size()!=0){
								myhandler.sendEmptyMessage(1);
								break;
							}
	                    }catch(Exception e){}
					}
					if(k==3){
						myhandler.sendEmptyMessage(3);
					}
					break;
				case 2://查询课表
					for(k=0;k<3;k++){
						try{
							setParams("btQuery",sp_year.getSelectedItem().toString(),items_semester.get(sp_semester.getSelectedItem().toString()),items_department.get(sp_department.getSelectedItem().toString()),items_class.get(sp_class.getSelectedItem().toString()),items_type.get(sp_type.getSelectedItem().toString()));
							doc=Jsoup.connect(url).data(params).cookies(map).timeout(20 * 1000).post();
							kb_doc=doc.select("#dgKb").get(0).toString();
							if(kb_doc!=null&&!kb_doc.equals("")){
								myhandler.sendEmptyMessage(2);
								break;
							}
						}catch(Exception e){}
					}
					if(k==3){
						myhandler.sendEmptyMessage(3);
					}
					break;
				case 3://获取缓存
						myhandler.sendEmptyMessage(5);

				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	private void setParams(String eventTarget, String selYear,
			String selTerm, String selDepart, String selClass,String selKblb) {

		params.put("__EVENTTARGET", eventTarget);
		params.put("__EVENTARGUMENT", "");
		params.put("__LASTFOCUS", "");
		params.put("__VIEWSTATE",__VIEWSTATE);
		params.put("__EVENTVALIDATION",__EVENTVALIDATION);
		params.put("selYear", selYear);
		params.put("selTerm", selTerm);
		params.put("selKblb:",selKblb);
		params.put("selDepart", selDepart);
		params.put("selClass", selClass);
	}

	private void initTerm(){//自动识别当前学期
		Calendar cal=Calendar.getInstance();
		int c_month=cal.get(Calendar.MONTH);
		
		//System.out.println(cal.get(Calendar.MONTH)+"年"+cal.get(Calendar.YEAR)+"月"+cal.get(Calendar.WEDNESDAY)+"星期");
		if(c_month>=8){
			  c_year=cal.get(Calendar.YEAR);
			  c_semester=0;
		 }else{
			 c_year=cal.get(Calendar.YEAR)-1;
			 c_semester=1;
		 }
		for(int i=0;i<6;i++){
			items_year[i]=String.valueOf(c_year-4+i);
		}
		
	}

}
