package com.yuol.smile.adapter;
import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
public class NewsPaperPagerAdaper extends FragmentPagerAdapter {
	private ArrayList<Fragment> list;
	private ArrayList<String> titles;
	public NewsPaperPagerAdaper(FragmentManager fm,ArrayList<Fragment> list,ArrayList<String> titles) {
		super(fm);
		this.list=list;
		this.titles=titles;
	}
		@Override
		public Fragment getItem(int position) {
				return list.get(position);
		}
		@Override
		public int getCount() {
			return 4;
		}
    	
		@Override
		public CharSequence getPageTitle(int position) {
			return titles.get(position);
		}
    }