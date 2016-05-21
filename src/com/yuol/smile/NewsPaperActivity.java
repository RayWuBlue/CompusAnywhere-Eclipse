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
import com.yuol.smile.fragment.NewsPaperFragment;

public class NewsPaperActivity extends FragmentActivity {

	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private ViewPager mViewPager;
	private TextView indicator_importance;
	private TextView indicator_total;
	private TextView indicator_newspaper_school;
	private TextView indicator_newspaper_associate;

	private int pageIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newspaper);
		mViewPager = (ViewPager) findViewById(R.id.found_view_pager);
		initView();
		initFragment();
	}

	private void initView() {

		indicator_importance = (TextView) findViewById(R.id.indicator_importance);
		indicator_total = (TextView) findViewById(R.id.indicator_total);
		indicator_newspaper_school = (TextView) findViewById(R.id.indicator_newspaper_school);
		indicator_newspaper_associate = (TextView) findViewById(R.id.indicator_newspaper_associate);

		indicator_importance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pageIndex != 0)
					mViewPager.setCurrentItem(0);
			}
		});
		indicator_total.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pageIndex != 1)
					mViewPager.setCurrentItem(1);
			}
		});
		indicator_newspaper_school.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pageIndex != 0)
					mViewPager.setCurrentItem(2);
			}
		});
		indicator_newspaper_associate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pageIndex != 1)
					mViewPager.setCurrentItem(3);
			}
		});
		
		mViewPager.setCurrentItem(0);	
		indicator_importance.setTextColor(getResources().getColor(R.color.newspaper));
	}

	private void resetIndicatorTextViewColor() {
		indicator_importance.setTextColor(getResources().getColor(R.color.text_gray));
		indicator_total.setTextColor(getResources().getColor(R.color.text_gray));
		indicator_newspaper_school.setTextColor(getResources().getColor(R.color.text_gray));
		indicator_newspaper_associate.setTextColor(getResources().getColor(R.color.text_gray));
	}

	private void initFragment() {

		fragments.clear();// Çå¿Õ
		for (int i = 0; i < 4; i++)
			fragments.add(NewsPaperFragment.getInstant(i,
					"http://cjdxb.cuepa.cn/index.php"));
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
		public void onPageScrolled(int position, float screenOffset,
				int offsetPx) {
		}

		@Override
		public void onPageSelected(int position) {
			resetIndicatorTextViewColor();
			if (position == 0) {
				indicator_importance
						.setTextColor(getResources().getColor(R.color.newspaper));
			} else if (position == 1) {
				indicator_total.setTextColor(getResources().getColor(R.color.newspaper));
			} else if (position == 2) {
				indicator_newspaper_school.setTextColor(getResources().getColor(R.color.newspaper));
			} else {
				indicator_newspaper_associate.setTextColor(getResources().getColor(R.color.newspaper));
			}
			pageIndex = position;
		}
	};
}
