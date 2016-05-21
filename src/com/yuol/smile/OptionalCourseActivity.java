package com.yuol.smile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.MyProgressBar;

public class OptionalCourseActivity extends BaseActivity {
  
    final static int MAX_OPTIONAL_COURSE = 10; 
	private List<String> group;           //组列
	private List<List<String>> child;     //子列 
	private ExpandableListView timeTableList;
    private ArrayList<String[]> hide_params = new ArrayList<String[]>();
	private ContactsInfoAdapter myOptionalCourseAdapter;
    private MyHandler myHandler;
    private String __VIEWSTATE;
    private String __EVENTVALIDATION;
    private String url= Api.XKCX;
    private Map<String,String> params = new HashMap<String,String>();
    private Map<String,String> map;//cookie
	private MyProgressBar mpb;
    LoginHelper lh;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_optional_course);
		//intent = getIntent();
		myHandler = new MyHandler();
		lh = new LoginHelper(OptionalCourseActivity.this);
		initView();
		initData();
	}
	
    private void initView()
    {
    	
		setTitle("公选课");
    	map=new HashMap<String,String>();
		map.put("ASP.NET_SessionId", lh.getJwcCookie());
    	group = new ArrayList<String>();  
        child = new ArrayList<List<String>>();
		timeTableList = (ExpandableListView)findViewById(R.id.list_time_table);
		myOptionalCourseAdapter = new ContactsInfoAdapter(group,child,this);
		timeTableList.setAdapter(myOptionalCourseAdapter);
		timeTableList.setCacheColorHint(0); // 设置拖动列表的时候防止出现黑色背景
    }
	private void initData(){
		__VIEWSTATE=lh.getJwcP1();
		__EVENTVALIDATION=lh.getJwcP2();
		
		mpb = new MyProgressBar(OptionalCourseActivity.this);
		mpb.setMessage("正在加载中...");
		new MyThread().start();
	}

	public class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case 0://显示当前学期成绩
				myOptionalCourseAdapter.notifyDataSetChanged();
				mpb.dismiss();
				break;
			case 1://出现异常
				Toast.makeText(OptionalCourseActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
				break;
			}
			
		}
		
	}
    
	private void addInfo(String g,String[] c){  
        group.add(g);  
        List<String> childitem = new ArrayList<String>();  
        for(int i=0;i<c.length;i++){  
            childitem.add(c[i]);  
        }  
        child.add(childitem);  
    }
    
	public class MyThread extends Thread{
		@Override
		public void run() {
			queryOptionalCourse();
		}
	}

	private void getHiddenParams() {
		
		try {
			Document doc=Jsoup.connect(url).cookies(map).timeout(10*1000).post();
			Elements hidden_inputs = doc.select("input[type=hidden]");
			for (Element hidden_eles : hidden_inputs) {
				
				System.out.println("隐藏参数："+hidden_eles);
				
				String[] arr = new String[] { hidden_eles.attr("name"),
						hidden_eles.attr("value") };
				hide_params.add(arr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private int queryOptionalCourse(){//学期查询
		try {
			getHiddenParams();
	        Calendar a=Calendar.getInstance();

	        	if(a.get(Calendar.MONTH)+1<9)
	                params.put("selTerm", "2");
	        	else
	        		params.put("selTerm", "1");
	        	
	        	System.out.println("查询的年份："+a.get(Calendar.YEAR));
	        	
	                params.put("selYear",""+(a.get(Calendar.YEAR)-1));
	                params.put("__EVENTTARGET", "btQuery");
	                params.put("selXiaoqu", "1");
	                params.put("__VIEWSTATE", __VIEWSTATE);
	                params.put("__EVENTVALIDATION", __EVENTVALIDATION);
	                
	                for (String[] arr : hide_params) 
	                    params.put(arr[0], arr[1]);
	                

	                params.put("ASP.NET_SessionId",lh.getJwcCookie());
	                Document doc=Jsoup.connect(url).data(params).cookies(map).timeout(10*1000).post();  
	                //System.out.println("公选课结果:"+doc);
                        Elements eles = doc.select("a[target=_blank]");
                        for (Element ele : eles) {
                        	
                            String[] str = new String[6];
                            
                            String courseTitle = ele.text();
                            str[0] = "课程名称："+ele.text();
                            Element ele_next = ele.parent().parent().nextElementSibling();
                            System.out.println(ele_next);
                            str[1] = "课程学分："+ele_next.child(0).text();
                            System.out.println(str[1]);
                            ele_next = ele_next.nextElementSibling();
                            str[2] = "时间地点："+ele_next.child(0).text();
                            ele_next = ele_next.nextElementSibling();
                            str[3] ="任课教师："+ ele_next.child(0).text();
                            ele_next = ele_next.nextElementSibling();
                            str[4] ="课程类别："+ ele_next.child(0).text();
                            ele_next = ele_next.nextElementSibling();
                            str[5] ="选修类型："+ ele_next.child(0).text();
                            addInfo(courseTitle,str);
                            for(int i=0;i<6;i++)
                            	Log.d("NetResponse","查询到的已选课程"+i+":"+str[i]);
                    }
		    myHandler.sendEmptyMessage(0);
		}catch (Exception e) {
			Log.d("NetResponse","错误信息:"+e);
			myHandler.sendEmptyMessage(1);
		}
			return 1;
	}

	class ContactsInfoAdapter extends BaseExpandableListAdapter{  
		  
		private List<String> group;           //组列 
		private List<List<String>> child; 
		private Context activity;
	    public ContactsInfoAdapter(List<String> group2, List<List<String>> child2,
				OptionalCourseActivity optionalCourseActivity) {
	    	group = group2;
	    	child = child2;
	    	activity = optionalCourseActivity;
		}


		@Override  
	    public Object getChild(int groupPosition, int childPosition) {  
	        return child.get(groupPosition).get(childPosition);  
	    }  
	      
	    @Override  
	    public long getChildId(int groupPosition, int childPosition) {  
	        return childPosition;
	    }  

	    @Override  
	    public int getChildrenCount(int groupPosition) {  
	        return child.get(groupPosition).size();  
	    }
	    
	    @Override  
	    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {  
	        String string = child.get(groupPosition).get(childPosition);   
	        return getGenericView(string);  
	    }  
	      
	    //----------------Group----------------//  
	    @Override  
	    public Object getGroup(int groupPosition) {  
	        return group.get(groupPosition);  
	    }                 

	    @Override  
	    public long getGroupId(int groupPosition) {  
	        return groupPosition;  
	    }     
	      
	    @Override  
	    public int getGroupCount() {  
	        return group.size();  
	    }     
	      
	    @Override  
	    public View getGroupView(int groupPosition, boolean isExpanded,  
	            View convertView, ViewGroup parent) {  
	        String string = group.get(groupPosition);    
	        TextView text;
//	        if (convertView == null) { 
	            convertView = LayoutInflater.from(activity).inflate(R.layout.list_optional_course_item, null); 
	            text =(TextView)convertView.findViewById(R.id.item_name);
	            text.setText(string);
/*	            convertView.setTag(text); 
	        } else { 
	        	text = (TextView) convertView.getTag(); 
	        } */

	        View indicator = (View)convertView.findViewById(R.id.indicator);
	        
	        
	        if (isExpanded)
	        	indicator.setBackgroundResource(R.drawable.qz_icon_navbar_drop_down);
	        else 
	        	indicator.setBackgroundResource(R.drawable.lbs_goto_arrow);
	        return convertView; 
	    }  

	    public View getGenericView(String s) {    
	        // Layout parameters for the ExpandableListView    
	        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(    
	                ViewGroup.LayoutParams.MATCH_PARENT, 60);  

	        TextView text = new TextView(activity);
	        text.setLayoutParams(lp);
	        text.setSingleLine(true);
	        // Center the text vertically    
	        text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);    
	        // Set the text starting position
	        text.setTextColor(R.color.text_gray);
	        text.setPadding(40, 0, 0, 0);    
	        text.setText(s);
			return text;
	    }    
	      
	      
	    @Override  
	    public boolean hasStableIds() {  
	        return false;  
	    }         

	    @Override  
	    public boolean isChildSelectable(int groupPosition, int childPosition) {  
	        return true;  
	    }
	      
	}


}
