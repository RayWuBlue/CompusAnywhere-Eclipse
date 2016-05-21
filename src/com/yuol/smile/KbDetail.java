package com.yuol.smile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.yuol.smile.adapter.KbDetailAdapter;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.bean.Course;
import com.yuol.smile.helper.ScheduleHelper;
import com.yuol.smile.utils.TimeUtil;
import com.yuol.smile.widgets.MyAlertDialog;
import com.yuol.smile.widgets.MyAlertDialog.MyDialogInt;
import com.yuol.smile.widgets.MyPopMenu;

public class KbDetail extends BaseActivity {
	private TabHost tabHost;
	private TabWidget tabWidget;
	private ListView monday;
	private ListView tuesday;
	private ListView wednesday;
	private ListView thrusday;
	private ListView friday;
	private ListView saturday;
	private ListView sunday;
	private Course[][] kb_content = new Course[7][6];
	private MyAlertDialog mad;

	private LinearLayout pageNav;
	private String[] week={"一","二","三","四","五","六","七"};
	private TextView[] tv=new TextView[week.length];
	private int current=TimeUtil.getDayOfWeek()-1;
	private MyPopMenu popmenu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kb_detail_main);
		initview();
		initTabHost();
		initPageNav();
	}

	public void initPageNav(){
		
		pageNav=(LinearLayout) super.findViewById(R.id.schedule_pagenav_layout);
		LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		param.weight=1;
		param.setMargins(15,0,15,0);
		for(int i=0;i<week.length;i++){
			tv[i]=new TextView(this);
			tv[i].setText(week[i]);
			tv[i].setTextColor(Color.WHITE);
			tv[i].setGravity(Gravity.CENTER);
			tv[i].setLayoutParams(new LayoutParams(100,100));
			pageNav.addView(tv[i],param);
			tv[i].setOnClickListener(new OnPageNavClick());
			tv[i].setTag(i);
		}
		setPageNav(current);
		tabHost.setCurrentTab(current);
	}
	
	public class OnPageNavClick implements OnClickListener{
		@Override
		public void onClick(View view) {
			current=(Integer) view.getTag();
			//viewpager.setCurrentItem(current);
			setPageNav(current);
			tabHost.setCurrentTab(current);
		}
	}
	
	public void setPageNav(int index){
		for(int i=0;i<week.length;i++){
			tv[i].setTextColor(getResources().getColor(R.color.blue));
			tv[i].setBackgroundResource(0);
		}
		tv[index].setTextColor(getResources().getColor(R.color.white));
		tv[index].setBackgroundResource(R.drawable.shape_bg_round);
	}
	
	private void initTabHost() {
		tabHost = (TabHost) findViewById(R.id.kb_tabhost);
		tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec("tab_1").setIndicator("一")
				.setContent(R.id.kb_monday));
		tabHost.addTab(tabHost.newTabSpec("tab_2").setIndicator("二")
				.setContent(R.id.kb_tuesday));
		tabHost.addTab(tabHost.newTabSpec("tab_3").setIndicator("三")
				.setContent(R.id.kb_wednesday));
		tabHost.addTab(tabHost.newTabSpec("tab_4").setIndicator("四")
				.setContent(R.id.kb_thrusday));
		tabHost.addTab(tabHost.newTabSpec("tab_5").setIndicator("五")
				.setContent(R.id.kb_friday));
		tabHost.addTab(tabHost.newTabSpec("tab_6").setIndicator("六")
				.setContent(R.id.kb_saturday));
		tabHost.addTab(tabHost.newTabSpec("tab_7").setIndicator("日")
				.setContent(R.id.kb_sunday));

	}

	private PointF startPoint = new PointF();

	// 控制tabhost切换的触摸监听
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:// 压下屏幕
			startPoint.set(event.getX(), event.getY());
			break;

		case MotionEvent.ACTION_MOVE:// 手指在屏幕移动
			break;
		case MotionEvent.ACTION_UP:// 手指离开屏
			float dx = event.getX() - startPoint.x;// 得到在x轴的移动距离
			int nextViewId = 0;
			if (dx > 100) {
				// System.out.println("当前页面:" + tabHost.getCurrentTab());
				if (tabHost.getCurrentTab() == 0) {
					nextViewId = tabWidget.getChildCount() - 1;
				} else {
					nextViewId = tabHost.getCurrentTab() - 1;
				}

				// 左进右出
				Animation right_out = AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.right_out);
				Animation left_in = AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.left_in);
				tabHost.getCurrentView().setAnimation(right_out);
				tabHost.setCurrentTab(nextViewId);
				tabHost.getCurrentView().setAnimation(left_in);
 ;
				setPageNav(nextViewId);
			}
			if (dx < -100) {
				// System.out.println("切换页面!");
				if (tabHost.getCurrentTab() == tabWidget.getTabCount() - 1) {
					nextViewId = 0;
				} else {
					nextViewId = tabHost.getCurrentTab() + 1;
				}

				// 左出右进
				Animation left_out = AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.left_out);
				tabHost.getCurrentView().setAnimation(left_out);
				tabHost.setCurrentTab(nextViewId);
				// System.out.println("下一个页面:" + tabHost.getCurrentTab());
				Animation right_in = AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.right_in);
				tabHost.getCurrentView().setAnimation(right_in);
				setPageNav(nextViewId);
			}

			break;
		case MotionEvent.ACTION_POINTER_UP:// 有手指离开屏幕,但屏幕还有触点
			break;

		case MotionEvent.ACTION_POINTER_DOWN:// 当屏幕上还有触点,再有一个手指压下屏幕
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	private void setKBContent() {
		ScheduleHelper sh = new ScheduleHelper(KbDetail.this);
		if ("".equals(sh.getCurrentKBClassname())) {
			mad = new MyAlertDialog(this);
			mad.setTitle("消息提示");
			mad.setMessage("本地还没有课程表信息，是否从教务处导入？");
			mad.setLeftButton("确定", new MyDialogInt() {
				@Override
				public void onClick(View view) {
					mad.dismiss();
					Intent it;
					it = new Intent(KbDetail.this, JwcLoginActivity.class);
					Bundle bd = new Bundle();
					bd.putInt("type",JwcLoginActivity.TYPE_KB);
					it.putExtras(bd);
					startActivity(it);
					finish();
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
			return;
		}
		String str = sh.getCurrentKB();
		Document html = Jsoup.parse(str);
		Element table = html.select("#dgKb").get(0);
		Elements mElements = table.select("tr");
		Element mElement = null;
		Element mElement_ = null;
		Document doc = null;
		String content = "";
		List<Course> courses = new ArrayList<Course>();
		for (int i = 1; i < 7; i++) {
			mElement = mElements.get(i);
			for (int j = 1; j < 8; j++) {
				mElement_ = mElement.child(j);
				content = mElement_.toString();
				content = content.replace("<br />", "|");// 用”|“纪录换行符，便于提取
				doc = Jsoup.parse(content);
				content = doc.select("font").get(0).text();
				if (!content.equals("")) {
					try {
						courses = getCourse(content, sh.getCurrentKBClassname());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (courses.size() > 0) {
						kb_content[j - 1][i - 1] = courses.get(0);// 部分课表在同一时间有不知一个课程，现在只显示了第一个，之后加上周次后判断动态显示
					}
				}
			}
		}
	}

	private void initview() {
		setTitle("我的课表");
		addButton(R.drawable.more, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popmenu=new MyPopMenu(KbDetail.this);
				popmenu.addItems(new String[]{"更换课表","公共选修课"});
				popmenu.showAsDropDown(v);
				popmenu.setOnItemClickListener(new MyPopMenu.MyPopMenuImp() {
					
					@Override
					public void onItemClick(int index) {
						Intent it = null;
						Bundle bd = null;
						switch (index) {
						case 0:
							bd = new Bundle();
							bd.putInt("type", JwcLoginActivity.TYPE_KB);
							it = new Intent(KbDetail.this,JwcLoginActivity.class);
							it.putExtras(bd);
							break;
						case 1:
							bd = new Bundle();
							bd.putInt("type", JwcLoginActivity.TYPE_COURSE);
							it = new Intent(KbDetail.this,JwcLoginActivity.class);
							it.putExtras(bd);
							break;
						default:
							break;
						}
						if(it!=null)
							startActivity(it);
					}
				});
				
			}
		});
		setKBContent();
		monday = (ListView) findViewById(R.id.kb_monday);
		tuesday = (ListView) findViewById(R.id.kb_tuesday);
		wednesday = (ListView) findViewById(R.id.kb_wednesday);
		thrusday = (ListView) findViewById(R.id.kb_thrusday);
		friday = (ListView) findViewById(R.id.kb_friday);
		saturday = (ListView) findViewById(R.id.kb_saturday);
		sunday = (ListView) findViewById(R.id.kb_sunday);
		monday.setAdapter(new KbDetailAdapter(KbDetail.this, kb_content, 1));
		tuesday.setAdapter(new KbDetailAdapter(KbDetail.this, kb_content, 2));
		wednesday.setAdapter(new KbDetailAdapter(KbDetail.this, kb_content, 3));
		thrusday.setAdapter(new KbDetailAdapter(KbDetail.this, kb_content, 4));
		friday.setAdapter(new KbDetailAdapter(KbDetail.this, kb_content, 5));
		saturday.setAdapter(new KbDetailAdapter(KbDetail.this, kb_content, 6));
		sunday.setAdapter(new KbDetailAdapter(KbDetail.this, kb_content, 7));
	}

	public List<Course> getCourse(String str_kb, String str_name) {
		List<Course> list = new ArrayList<Course>();
		if (str_kb.contains("体育"))
			return list;
		String[] entity_arr = str_kb.split("\\|");
		for (int i = 0; i < entity_arr.length / 2; i++) {
			Course course = new Course();
			String[] fields_1 = entity_arr[i * 2].split(" ");
			course.setName(fields_1[0]);
			course.setTeacher(fields_1[1]);
			String[] fields_2 = entity_arr[i * 2 + 1].split(" ");
			boolean hasClass = false;
			for (int j = 0; j < fields_2.length; j++) {
				if (j == fields_2.length - 1 || course.getAddress() != null)
					break;
				if (fields_2[j].equals(str_name))
					hasClass = true;
				Pattern p = Pattern.compile("-*-|武培*-|文理*-");
				Matcher m = p.matcher(fields_2[j]);
				if (m.find()) {
					course.setAddress(fields_2[j]);
					String time_str = "";
					for (int k = j + 1; k < fields_2.length; k++) {
						time_str += fields_2[k];
					}
					course.setTime(time_str);
				}
			}
			if (hasClass)
				list.add(course);
		}
		return list;

	}

}
