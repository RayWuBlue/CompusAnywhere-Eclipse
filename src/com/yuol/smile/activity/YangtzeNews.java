package com.yuol.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuol.smile.R;
import com.yuol.smile.YUNewsDetail;
import com.yuol.smile.adapter.YUNewsAdapter;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.bean.NewsChannelItem;
import com.yuol.smile.bean.YUNewsItem;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.DataCache;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.T;
import com.yuol.smile.utils.TimeUtil;
import com.yuol.smile.widgets.MyPopMenu;
import com.yuol.smile.widgets.MyPopMenu.MyPopMenuImp;
import com.yuol.smile.widgets.XListView;
import com.yuol.smile.widgets.XListView.IXListViewListener;

public class YangtzeNews extends BaseActivity {
	private YUNewsAdapter adapter;
	private int page = 0;
	private XListView lv;
	private List<YUNewsItem> list;
	private List<YUNewsItem> net_list;
	private TextView tv_loading_failed;
	private ProgressBar mpb;
	private IXListViewListenerImp xlvl;
	private MyPopMenu popmenu;
	private ArrayList<NewsChannelItem>  YUNewsChannelList = (ArrayList<NewsChannelItem>)(Api.News.getYUNewsChannels());
	private int type = YUNewsChannelList.get(0).getId();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_yu_news);
		list = new ArrayList<YUNewsItem>();
		net_list = new ArrayList<YUNewsItem>();
		adapter = new YUNewsAdapter(YangtzeNews.this, list);
		xlvl = new IXListViewListenerImp();
		initView();
	}
	private void initView() {
		setTitle(YUNewsChannelList.get(0).getName());
		addButton("新闻分类", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popmenu=new MyPopMenu(YangtzeNews.this);
				int c =YUNewsChannelList.size();
				String[] menuArray = new String[c];
				for(int i=0;i<YUNewsChannelList.size();i++)
					menuArray[i] = YUNewsChannelList.get(i).getName();
				
				popmenu.addItems(menuArray);
				popmenu.showAsDropDown(v);
				popmenu.setOnItemClickListener(new MyPopMenuImp() {
					@Override
					public void onItemClick(int index) {
						setTitle(YUNewsChannelList.get(index).getName());
						type = YUNewsChannelList.get(index).getId();
						page = 0;
						list.clear();
						initCacheData();
						xlvl.onRefresh();
					}});
				
			}
		});
		lv =(XListView)findViewById(R.id.mListView);
		mpb = (ProgressBar)findViewById(R.id.detail_loading);
		tv_loading_failed =(TextView)findViewById(R.id.tv_loading_failed);

		lv.setAdapter(adapter);
		lv.setPullLoadEnable(true);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(YangtzeNews.this,YUNewsDetail.class);
				intent.putExtra("com.pw.schoolknow.ser",list.get(position-1));
				startActivity(intent);
			}
		});
		lv.setXListViewListener(xlvl);
		//initCacheData();
		xlvl.onRefresh();
	}

	private void initCacheData() {
		DataCache dc = new DataCache(YangtzeNews.this);
		String url=Api.News.getYUNews(page, type);
		String cache = dc.load(url);
		if(!"".equals(cache))
		{
			parseResult(cache,list);
			adapter.notifyDataSetChanged();
		}
	}

	class IXListViewListenerImp implements IXListViewListener{
		
		@Override
		public void onRefresh() {
			if(!NetHelper.isNetConnected(YangtzeNews.this)){
				T.showShort(YangtzeNews.this,R.string.net_error_tip);
				lv.stopRefresh();
				lv.stopLoadMore();
				return;
			}
			lv.setRefreshTime(""+TimeUtil.getUpdateTime(System.currentTimeMillis()));
			page = 0;
			requestNews();
		}
		
		@Override
		public void onLoadMore() {
			if(!NetHelper.isNetConnected(YangtzeNews.this)){
				T.showShort(YangtzeNews.this,R.string.net_error_tip);
				lv.stopRefresh();
				lv.stopLoadMore();
				return;
			}
			if(list.size()!=0){
				page++;
				requestNews();
			}
		}	
	}
	
	private boolean parseResult(String result, List<YUNewsItem> list){
		JSONArray jsonNewsList = JSON.parseArray(result);
		if(jsonNewsList==null){
			lv.stopRefresh();
			lv.stopLoadMore();
			T.showShort(YangtzeNews.this, "没有新闻");
			return false;
		}
		if(page==0){
			list.clear();
			adapter.notifyDataSetChanged();
		}
		for(int i=0;i<jsonNewsList.size();i++){
			JSONObject jsonNewsItem = jsonNewsList.getJSONObject(i);
			YUNewsItem item=new YUNewsItem();
			item.setId( Integer.parseInt(jsonNewsItem.getString("id")));
			item.setImageRight( jsonNewsItem.getString("img"));
			item.setTitle(jsonNewsItem.getString("title"));
			item.setPublishTime(jsonNewsItem.getString("time"));
			item.setType(jsonNewsItem.getString("name"));
			list.add(item);
		}
		//adapter.notifyDataSetChanged();
		return true;
	}
	private void requestNews() {
		final String url=Api.News.getYUNews(page, type);
		
		new AsyncTask<Void, Void, String>() {
			@Override
			protected void onPreExecute() {
				mpb.setVisibility(View.VISIBLE);
			};
			@Override
			protected String doInBackground(Void... arg0) {
				return GetUtil.getRes(url);
			}
			@Override
			protected void onPostExecute(String result) {
				try {
	        		if(parseResult(result,net_list)&&page==1)
	        			new DataCache(YangtzeNews.this).save(url, result);
	        			
	        		int news_num = net_list.size()-list.size();
	        			if(news_num>0)
	        				T.showShort(YangtzeNews.this, "更新"+news_num+"消息");
	        			list.clear();
	        			list.addAll(net_list);
	        			
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					adapter.notifyDataSetChanged();
					mpb.setVisibility(View.GONE);
					lv.stopRefresh();
					lv.stopLoadMore();
				}
			}
		}.execute();
	}

}