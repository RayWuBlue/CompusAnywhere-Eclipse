package com.yuol.smile;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.yuol.smile.adapter.IndicatorFragmentPagerAdapter;
import com.yuol.smile.fragment.JwcNewsFragment;

public class JwcActivity extends FragmentActivity {

	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private ViewPager mViewPager;
	private TextView indicator_jwc_notify;
	private TextView indicator_jwc_context;
	private int pageIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jwc);
		mViewPager = (ViewPager) findViewById(R.id.found_view_pager);
		initView();
		initFragment();
	}

	private void initView() {
		indicator_jwc_notify = (TextView)findViewById(R.id.indicator_jwc_notify);
		indicator_jwc_context = (TextView)findViewById(R.id.indicator_jwc_context);
		

		
		indicator_jwc_notify.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(pageIndex!=0)
				mViewPager.setCurrentItem(0);
			}
		});
		indicator_jwc_context.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(pageIndex!=1)
						mViewPager.setCurrentItem(1);		
					}
				});
		
		mViewPager.setCurrentItem(0);	
		indicator_jwc_notify.setTextColor(getResources().getColor(R.color.jwc));
	}

	private void resetIndicatorTextViewColor()
	{
		indicator_jwc_notify.setTextColor(getResources().getColor(R.color.text_gray));
		indicator_jwc_context.setTextColor(getResources().getColor(R.color.text_gray));
	}
	
	private void initFragment() {
			fragments.clear();// Çå¿Õ
			for(int i=0;i<2;i++){
				Bundle data = new Bundle();
				data.putInt("jwc_news_cloumn",i);
				JwcNewsFragment jwcfragment = new JwcNewsFragment();
				jwcfragment.setArguments(data);
				fragments.add(jwcfragment);
			}
			IndicatorFragmentPagerAdapter mAdapetr = new IndicatorFragmentPagerAdapter(
					getSupportFragmentManager(), fragments);
			mViewPager.setAdapter(mAdapetr);
			mViewPager.setOnPageChangeListener(pageListener);
		}
	
	public OnPageChangeListener pageListener = new OnPageChangeListener() {

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int position, float screenOffset, int offsetPx) {
	}

	@Override
	public void onPageSelected(int position) {
		resetIndicatorTextViewColor();
		if(position==0)
			indicator_jwc_notify.setTextColor(getResources().getColor(R.color.jwc));
		else if(position==1)
			indicator_jwc_context.setTextColor(getResources().getColor(R.color.jwc));
		pageIndex = position;
	}
	};
}
