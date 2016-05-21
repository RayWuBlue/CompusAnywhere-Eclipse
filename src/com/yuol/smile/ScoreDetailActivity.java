package com.yuol.smile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.TextView;

import com.yuol.smile.adapter.ScoreAdapter;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.bean.Score;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.MyPopMenu;
import com.yuol.smile.widgets.MyPopMenu.MyPopMenuImp;

public class ScoreDetailActivity extends BaseActivity implements OnItemSelectedListener{
  
    private MyHandler myHandler;
    private TextView jwc_name;
    private TextView jwc_class;
    private String __VIEWSTATE;
    private String __EVENTVALIDATION;
    private String c_year;
    private String c_term;
    private Document doc;
    private List<Score> list;
    private ListView lv;
    private ScoreAdapter adapter;
    private String url= Api.Jwc.getJwcScore();
    private Map<String,String> params;
    private Map<String,String> map;//cookie
    private String jd;//绩点
    private TextView tv_jd;
/*    private Spinner sp_year;
    private Spinner sp_term;*/
    private String currentYear;
    private String currentTermId;
    private LoginHelper lh;
    private MyPopMenu popmenu;
    private TextView score_term;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_detail);
		lh = new LoginHelper(ScoreDetailActivity.this);
		//getIntent();
		myHandler = new MyHandler();
		initView();
		getScore();
		
	}
	private void initView(){
		setTitle("我的成绩");
		initTerm();// 初试话年度，学期
		
		score_term = (TextView) addButton("查询设置",new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popmenu=new MyPopMenu(ScoreDetailActivity.this);
				Calendar cal=Calendar.getInstance();
				final int year=cal.get(Calendar.YEAR);
				int t = 0,c = (year-2012+1)*2;
				String[] menuArray = new String[c];
				for(int i = year;i>=2012;i--)
					for(int j = 1;j<=2;j++){
						if(j==1)menuArray[t] = i+" "+"上学期";
						else menuArray[t] = i+" "+"下学期";
						t++;
					}
				popmenu.addItems(menuArray);
				popmenu.showAsDropDown(v);
				popmenu.setOnItemClickListener(new MyPopMenuImp() {
					@Override
					public void onItemClick(int index) {
						currentYear = (year-index/2)+"";
						currentTermId = (index%2+1)+"";
						score_term.setText(currentYear+"年"+(currentTermId.equals("1")?"上学期":"下学期"));
						new MyThread(2).start();
					}});
			}
		}).findViewById(R.id.btn_text);	
		tv_jd=(TextView)super.findViewById(R.id.jwc_point);
		jwc_name=(TextView)super.findViewById(R.id.jwc_name);
		jwc_class=(TextView)super.findViewById(R.id.jwc_class);
		jwc_name.setText("姓名："+lh.getJwcName());
		jwc_class.setText("班级："+lh.getJwcClass());
		tv_jd.setText("计算中..");
		list=new ArrayList<Score>();
		map=new HashMap<String,String>();
		map.put("ASP.NET_SessionId",lh.getJwcCookie());
		lv=(ListView)super.findViewById(R.id.score_list);
		adapter=new ScoreAdapter(ScoreDetailActivity.this, list);
		lv.setAdapter(adapter);
		
		score_term.setText(currentYear+"年"+("1".equals(currentTermId)?"上学期":"下学期"));
	}
	private void getScore(){
		__VIEWSTATE=lh.getJwcP1();
		__EVENTVALIDATION=lh.getJwcP2();
		System.out.println(__VIEWSTATE);
		new MyThread(2).start();
	}

	public class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case 0://显示当前学期成绩
				adapter.notifyDataSetChanged();
				new MyThread(1).start();
				break;
			case 1://出现异常
				T.showShort(ScoreDetailActivity.this, "查询失败");
				break;
			case 2://显示绩点
				tv_jd.setText(jd);
				break;
			case 3://及时更新lv
				adapter.notifyDataSetChanged();
				break;
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
			switch(statu){
			case 1://绩点查询
				params.remove("__EVENTTARGET");
				params.put("Button2","必修课成绩列表");
				try{
					doc=Jsoup.connect(url).data(params).cookies(map).timeout(10*1000).post();				
                    jd=doc.select("#jd").get(0).text().trim();
                    myHandler.sendEmptyMessage(2);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			case 2://其他学期成绩查询
				queryxq(currentYear,currentTermId,"btXqcj",0);
			    break;
			case 3:
				queryxq(currentYear,currentTermId,"",1);
				break;
			case 4:
				queryxq(currentYear,currentTermId,"btAllcj",0);
				break;
			case 5:
				queryxq(currentYear,currentTermId,"btXqcj",2);
				break;
				
			
			
			}
			
		}
	}
	private void initTerm(){//自动识别当前学期
		Calendar cal=Calendar.getInstance();
		int c_month=cal.get(Calendar.MONTH);
		if(c_month<9)
			currentTermId = 1+"";
		else
			currentTermId = 2+"";
		currentYear = (cal.get(Calendar.YEAR)-1)+"";
	}

	private int queryxq(String year,String term,String __EVENTTARGET,int type){//学期查询
		try {
			list.clear();
			myHandler.sendEmptyMessage(3);//及时更新lv
			doc=Jsoup.connect(url).cookies(map).timeout(10*1000).post();
			__VIEWSTATE=doc.select("#__VIEWSTATE").val();
			__EVENTVALIDATION=doc.select("#__EVENTVALIDATION").val();
			params=new HashMap<String,String>();
			params.put("__VIEWSTATE",__VIEWSTATE);
			params.put("__EVENTVALIDATION",__EVENTVALIDATION);
			params.put("selYear",year);
			params.put("selTerm",term);
			params.put("__EVENTARGUMENT","");
			if(type!=0){
			   params.put("__EVENTTARGET","");//其他查询
			   if(type==1)
				   params.put("Button1","学位课成绩列表");
			   else
				 params.put("Button2","必修课成绩列表"); 
			}
			else
				params.put("__EVENTTARGET",__EVENTTARGET);//学期查询
			
			//再次请求查询
			doc=Jsoup.connect(url).data(params).cookies(map).timeout(10*1000).post();				
			Elements trs=doc.select("#dgCj").get(0).select("tr");
			if(trs.size()==1){
				myHandler.sendEmptyMessage(2);
				return 0;
			}
			
			for (int i=1;i<trs.size();i++) {
				Elements tds=trs.get(i).select("td");
				Score s=new Score(tds.get(0).text(),tds.get(1).text(),tds.get(2).text(),tds.get(3).text(), tds.get(4).text(),tds.get(5).text());
			    list.add(s);
			}
			__VIEWSTATE=doc.select("#__VIEWSTATE").val();
			__EVENTVALIDATION=doc.select("#__EVENTVALIDATION").val();
			
			} catch (Exception e) {
				e.printStackTrace();
				myHandler.sendEmptyMessage(1);
				return 2;
			}
		    myHandler.sendEmptyMessage(0);
			return 1;
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
		
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}


}
