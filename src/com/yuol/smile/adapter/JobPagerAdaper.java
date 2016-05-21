package com.yuol.smile.adapter;
import java.util.ArrayList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
public class JobPagerAdaper extends FragmentPagerAdapter {
	private ArrayList<Fragment> list;
	private ArrayList<String> titles;
	public JobPagerAdaper(FragmentManager fm,ArrayList<Fragment> list,ArrayList<String> titles) {
		super(fm);
		this.list=list;
		this.titles=titles;
	}
		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
				return list.get(position);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;//图个方便
		}
    	
		@Override
		public CharSequence getPageTitle(int position) {
			return titles.get(position);
		}
    }