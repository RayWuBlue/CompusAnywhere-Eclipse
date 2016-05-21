package com.yuol.smile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.MyAlertDialog;
import com.yuol.smile.widgets.MyAlertDialog.MyDialogInt;
import com.yuol.smile.widgets.MyPopMenu;
import com.yuol.smile.widgets.MyProgressBar;

public class CourseSelect extends BaseActivity {
	private String url = Api.Jwc.getJwcCourseSelect();
	private String __VIEWSTATE;
	private String __EVENTVALIDATION;
	private Document doc = null;
	private String course_doc = null;
	private Myhandler myhandler;
	private SharedPreferences sp_cache;
	private Map<String, String> params;
	private Map<String, String> map;
	private List<Map<String, String>> list;
	// private Map<String,String> map;//cookie
	private MyProgressBar mpb;
	private MyAlertDialog mad;
	private ListView lv;
	private CourseSelectAdapter adapter;
	private MyPopMenu popmenu;
	private final int MODE_PRO = 0;
	private final int MODE_CMN = 1;
	private final int MODE_SEL = 2;
	private int mode = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_course_select);
		initview();
		myhandler = new Myhandler();
		list = new ArrayList<Map<String, String>>();

		lv = (ListView) findViewById(R.id.course_select_lv);
		adapter = new CourseSelectAdapter(CourseSelect.this, list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final int position = arg2;
				final TextView tv_title = (TextView) arg1.findViewById(R.id.course_select_title);
				
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final Map<String, String> item = list.get(arg2);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						System.out.println("选课地址："
								+ Api.Jwc.getJwcXkAdd(item.get("id")));
						Connection conn = Jsoup.connect(Api.Jwc.getJwcXkAdd(item.get("id")));
						//Connection conn1 = conn.data(params);
						Connection conn2 = conn.cookies(map);
						Connection conn3 = conn2.timeout(5 * 1000);
						Message msg = new Message();
						
						try {
							Document result = conn3.post();
							if(null!=result){
								msg.obj = "选课结果:"+result;
									System.out.println("选课结果:"+result);
									
									if(result.toString().contains("成功")){
										myhandler.sendEmptyMessage(3);
									}
									else
										myhandler.sendEmptyMessage(4);
							}
						} catch (IOException e) {
							e.printStackTrace();
							System.out.println("选课出错！"+e.getMessage());
							msg.obj = "错误:"+e.getMessage();
							myhandler.sendEmptyMessage(4);
						}
					}
				}).start();
				return false;
			}
		});

		
		map = new HashMap<String, String>();
		map.put("ASP.NET_SessionId",
				new LoginHelper(CourseSelect.this).getJwcCookie());


		
		query();
	}

	void query(){
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					doc = Jsoup.connect(url).cookies(map).get();
					myhandler.sendEmptyMessage(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	private void initview() {
		setTitle("所有选课");
		addButton("已选课程", new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				popmenu=new MyPopMenu(CourseSelect.this);
				popmenu.addItems(new String[]{"公选课","专选课","查看已选"});
				popmenu.showAsDropDown(arg0);
				popmenu.setOnItemClickListener(new MyPopMenu.MyPopMenuImp() {
					
					@Override
					public void onItemClick(int index) {
						switch (index) {
						case 0:
							mode = MODE_CMN;
							break;
						case 1:
							mode = MODE_PRO;
							break;
						case 2:
							mode = MODE_SEL;
							break;
						default:
							break;
						}
						myhandler.sendEmptyMessage(0);
					}
				});
			}
		});
		sp_cache = this.getSharedPreferences("share", MODE_PRIVATE);
	}

	public class Myhandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:

				if (doc == null)
					return;

				__VIEWSTATE = doc.select("#__VIEWSTATE").val();
				__EVENTVALIDATION = doc.select("#__EVENTVALIDATION").val();

				System.out.println("参数信息：" + __VIEWSTATE);
				System.out.println("参数信息：" + __EVENTVALIDATION);
				
				mpb = new MyProgressBar(CourseSelect.this);
				mpb.setMessage("正在查询...");
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {

							params = new LinkedHashMap<String, String>();
							// params.put("__EVENTTARGET", "btAddzy");
							if(mode == MODE_CMN)
								params.put("__EVENTTARGET", "btAddgx");
							else if(mode == MODE_PRO)
								params.put("__EVENTTARGET", "btAddzy");
							else if(mode == MODE_SEL)
								params.put("__EVENTTARGET", "btQuery");
							
							params.put("__EVENTARGUMENT", "");
							params.put("__LASTFOCUS", "");
							params.put("__VIEWSTATE", __VIEWSTATE);
							params.put("__EVENTVALIDATION", __EVENTVALIDATION);

							Calendar cal = Calendar.getInstance();
							int c_month = cal.get(Calendar.MONTH);

							params.put("selYear", cal.get(Calendar.YEAR) + "");

							if (c_month >= 8) {
								params.put("selTem", "2");
							} else {
								params.put("selTerm", "1");
							}
							params.put("selXiaoqu", "1");

							Connection conn = Jsoup.connect(url);
							Connection conn1 = conn.data(params);
							Connection conn2 = conn1.cookies(map);
							Connection conn3 = conn2.timeout(20 * 1000);
							Document doc_query = conn3.post();
							Elements course_table = doc_query.select("#AddGrid");
							if (course_table != null && course_table.size() > 0)
								course_doc = course_table.get(0).toString();
							else{
								course_table = doc_query.select("#HG1");
								course_doc = course_table.get(0).toString();
								System.out.println("已选课程:"+course_doc);
							}
							if (course_doc != null && !course_doc.equals("")) {
								myhandler.sendEmptyMessage(1);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();

				break;
			case 1:
				System.out.println("选课信息：" + course_doc);
				parseResult(course_doc);
				break;
			case 2:
				T.show(CourseSelect.this, "教务处连接失败!", Toast.LENGTH_LONG);
				if(mpb!=null)
					mpb.dismiss();
				finish();
				break;
			case 3:
				
				T.show(CourseSelect.this, "操作成功!", Toast.LENGTH_LONG);
				if(mpb!=null)
					mpb.dismiss();
				break;
			case 4:
				T.show(CourseSelect.this, "操作失败!", Toast.LENGTH_LONG);
				if(mpb!=null)
					mpb.dismiss();
				break;
			}
			
			// mpb.dismiss();
		}

		private void parseResult(String course_doc) {
			list.clear();
			Document html = Jsoup.parse(course_doc);
			Elements mElements = html.select("tr");

			for (int i = 1; i < mElements.size(); i++) {
				System.out.println("课程："+mElements.get(i));
				
				Map<String, String> map = new HashMap<String, String>();
				Element e = mElements.get(i).select("td").get(1).select("a")
						.first();
				if(e==null)continue;
				String title = e.text();
				String link = e.attr("href");
				String id = link.substring(link.indexOf("no=")+3,
						link.indexOf("&"));

				map.put("id", id);
				map.put("title", title);
				
				Element ebtn = mElements.get(i).select("td").get(0).select("input").first();
				
				if(ebtn!=null){
					String disable = ebtn.attr("disabled");
					
					if(disable!=null&&"disabled".equals(disable))
						map.put("sel", "false");
				}
				
				list.add(map);
			}
			
			adapter.notifyDataSetChanged();
			if(mpb!=null)
				mpb.dismiss();
		}

	}

	
	class CourseSelectAdapter extends BaseAdapter {


		private class OnCourseClickListener implements OnClickListener {
			private String id;
			private String title;
			
			public OnCourseClickListener(String id,String title){
				this.id = id;
				this.title = title;
			}
			@Override
			public void onClick(View arg0) {
				if(mode==MODE_SEL){
					mad = new MyAlertDialog(CourseSelect.this);
					mad.setTitle("确认删除");
					mad.setMessage("是否确认删除" + title + "?");
					mad.setLeftButton("确定", new MyDialogInt() {
						@Override
						public void onClick(View view) {

							new Thread(new Runnable() {
								@Override
								public void run() {
									System.out.println("选课地址："
											+ Api.Jwc.getJwcXkAdd(id));
									Connection conn = Jsoup.connect(Api.Jwc.getJwcXkDel(id));
									//Connection conn1 = conn.data(params);
									Connection conn2 = conn.cookies(map);
									Connection conn3 = conn2.timeout(5 * 1000);
									Message msg = new Message();
									
									try {
										Document result = conn3.post();
										if(null!=result){
											msg.obj = "选课结果:"+result;
												System.out.println("选课结果:"+result);
												
												if(result.toString().contains("成功")){
													myhandler.sendEmptyMessage(3);
												}
												else
													myhandler.sendEmptyMessage(4);
										}
									} catch (IOException e) {
										e.printStackTrace();
										msg.obj = "错误:"+e.getMessage();
										myhandler.sendEmptyMessage(4);
									}
									
								}
							}).start();
							mad.dismiss();
						}
					});
					mad.setRightButton("取消", new MyDialogInt() {
						@Override
						public void onClick(View view) {
							mad.dismiss();
						}
					});
					
				}else
				{
					mad = new MyAlertDialog(CourseSelect.this);
					mad.setTitle("确认选课");
					mad.setMessage("是否确认添加" + title + "?");
					mad.setLeftButton("确定", new MyDialogInt() {
						@Override
						public void onClick(View view) {
	
							new Thread(new Runnable() {
								@Override
								public void run() {
									System.out.println("选课地址："
											+ Api.Jwc.getJwcXkAdd(id));
									Connection conn = Jsoup.connect(Api.Jwc.getJwcXkAdd(id));
									//Connection conn1 = conn.data(params);
									Connection conn2 = conn.cookies(map);
									Connection conn3 = conn2.timeout(5 * 1000);
									Message msg = new Message();
									
									try {
										Document result = conn3.post();
										if(null!=result){
											msg.obj = "选课结果:"+result;
												System.out.println("选课结果:"+result);
												
												if(result.toString().contains("成功")){
													myhandler.sendEmptyMessage(3);
												}
												else
													myhandler.sendEmptyMessage(4);
										}
									} catch (IOException e) {
										e.printStackTrace();
										System.out.println("选课出错！"+e.getMessage());
										msg.obj = "错误:"+e.getMessage();
										myhandler.sendEmptyMessage(4);
									}
									
								}
							}).start();
							mad.dismiss();
						}
					});
					mad.setRightButton("取消", new MyDialogInt() {
						@Override
						public void onClick(View view) {
							mad.dismiss();
						}
					});
				}
			}
		}
		
		private List<Map<String, String>> list;
		private LayoutInflater inflater;
		public CourseSelectAdapter(Context context,
				List<Map<String, String>> list) {
			this.list = list;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
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
			ViewHolder holder = null;
			final Map<String, String> item = list.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.item_course_select,
						null, false);
				holder.title = (TextView) convertView
						.findViewById(R.id.course_select_title);
				holder.btn = (Button) convertView
						.findViewById(R.id.course_select_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setText(item.get("title"));
			if(item.get("sel")!=null&&"false".equals(item.get("sel"))){
				holder.title.setTextColor(CourseSelect.this.getResources().getColor(R.color.text_gray_lighter));
				holder.btn.setVisibility(View.GONE);
			}else
			{
				holder.title.setTextColor(CourseSelect.this.getResources().getColor(R.color.text_gray));
				holder.btn.setVisibility(View.VISIBLE);
			}
			if(mode==MODE_PRO||mode==MODE_CMN)
				holder.btn.setText("添加");
			else
				holder.btn.setText("删除");
			holder.btn.setOnClickListener(new OnCourseClickListener(item.get("id"),item.get("title")));
			return convertView;
		}

		class ViewHolder {
			TextView title;
			Button btn;
		}

	}

	@Override
	protected void onDestroy() {
		if(mpb!=null)
		mpb.dismiss();
		super.onDestroy();
	}
	

}
