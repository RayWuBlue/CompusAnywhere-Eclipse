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
import com.yuol.smile.fragment.JobNewsFragment;

public class JobActivity extends FragmentActivity {

	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private ViewPager mViewPager;
	private TextView indicator_job_notify;
	private TextView indicator_job_context;
	private TextView indicator_job_info;
	private int pageIndex = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job);
		mViewPager = (ViewPager) findViewById(R.id.found_view_pager);
		initView();
		initFragment();
	}

	private void initView() {
		indicator_job_notify = (TextView)findViewById(R.id.indicator_job_notify);
		indicator_job_context = (TextView)findViewById(R.id.indicator_job_context);
		indicator_job_info = (TextView)findViewById(R.id.indicator_job_info);
		
		indicator_job_notify.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(pageIndex!=0)
				mViewPager.setCurrentItem(0);
			}
		});
		indicator_job_context.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(pageIndex!=1)
						mViewPager.setCurrentItem(1);		
					}
				});
		indicator_job_info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(pageIndex!=2)
				mViewPager.setCurrentItem(2);		
			}
		});
		
		mViewPager.setCurrentItem(0);	
		indicator_job_notify.setTextColor(getResources().getColor(R.color.job));
		
	}

	private void resetIndicatorTextViewColor()
	{
		indicator_job_notify.setTextColor(getResources().getColor(R.color.text_gray));
		indicator_job_context.setTextColor(getResources().getColor(R.color.text_gray));
		indicator_job_info.setTextColor(getResources().getColor(R.color.text_gray));
	}
	
	private void initFragment() {
			fragments.clear();// Çå¿Õ
			
			
			for(int i=0;i<3;i++){
				Bundle data = new Bundle();
				data.putInt("job_news_cloumn",i);
				JobNewsFragment jobfragment = new JobNewsFragment();
				jobfragment.setArguments(data);
				fragments.add(jobfragment);
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
			indicator_job_notify.setTextColor(getResources().getColor(R.color.job));
		else if(position==1)
			indicator_job_context.setTextColor(getResources().getColor(R.color.job));
		else
			indicator_job_info.setTextColor(getResources().getColor(R.color.job));
		pageIndex = position;
	}
	};
}
