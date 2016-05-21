package com.yuol.smile.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.yuol.smile.HomeActivity;
import com.yuol.smile.R;
import com.yuol.smile.helper.VersionHelper;

@SuppressWarnings("deprecation")
public class Main extends TabActivity implements OnCheckedChangeListener {

	private RadioGroup group;
	private TabHost tabHost;
	
	
	//public static SlidingLayer mSlidingLayer;

	public static final String[] TAB_ITEM = { "tabItem1", "tabItem2",
			"tabItem3" };
	@SuppressWarnings("rawtypes")
	public static final Class[] activity = { HomeActivity.class,
			SmileNews.class, User.class };

	private long mExitTime = 0;
	/*
	 * public TextView tv_title; private TextView tv_content;
	 */


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_main);
		/*
		 * tv_title = (TextView)findViewById(R.id.hello_title); tv_content =
		 * (TextView)findViewById(R.id.hello_content);
		 */

		

		group = (RadioGroup) findViewById(R.id.main_radiogp);
		group.setOnCheckedChangeListener(this);
		tabHost = this.getTabHost();

		TabSpec[] tab = new TabSpec[TAB_ITEM.length];
		for (int i = 0; i < TAB_ITEM.length; i++) {
			tab[i] = tabHost.newTabSpec(TAB_ITEM[i]);
			tab[i].setIndicator(TAB_ITEM[i]).setContent(
					new Intent(this, activity[i]));
			tabHost.addTab(tab[i]);
		}

		// 选择默认页面
		int position = 0;
		try {
			Intent it = getIntent();
			String param = it.getStringExtra("param");
			position = Integer.parseInt(param);
		} catch (Exception e) {
			position = 0;
		} finally {
			tabHost.setCurrentTabByTag(TAB_ITEM[position]);
			RadioButton CurrentBtn = (RadioButton) group.getChildAt(position);
			CurrentBtn.setChecked(true);
		}

		new VersionHelper(Main.this).updateTip();

		/*
		 * if (!NetHelper.isNetConnected(Main.this)) {
		 * mSlidingLayer.openLayer(false); return; }
		 */
		
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_button1:
			tabHost.setCurrentTabByTag(TAB_ITEM[0]);
			break;
		case R.id.radio_button2:
			tabHost.setCurrentTabByTag(TAB_ITEM[1]);
			break;
		case R.id.radio_button3:
			tabHost.setCurrentTabByTag(TAB_ITEM[2]);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return false;
		}
		return super.dispatchKeyEvent(event);
	}



}
