package com.yuol.smile.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.yuol.smile.R;
import com.yuol.smile.YUNewsDetail;
import com.yuol.smile.adapter.YUNewsAdapter;
import com.yuol.smile.bean.YUNewsItem;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.DataCache;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.T;
import com.yuol.smile.utils.TimeUtil;
import com.yuol.smile.widgets.XListView;
import com.yuol.smile.widgets.XListView.IXListViewListener;

public class YUNewsFragment extends Fragment {
	private static final int LOAD_NEWS = 0;
	private Context activity;
	private YUNewsAdapter adapter;
	private int page = 0;
	private int type;
	private XListView lv;
	private List<YUNewsItem> list;
	private List<YUNewsItem> net_list;
	private TextView tv_loading_failed;
	private ProgressBar mpb;
	private IXListViewListenerImp xlvl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = getArguments().getInt("id");
		list = new ArrayList<YUNewsItem>();
		net_list = new ArrayList<YUNewsItem>();
		adapter = new YUNewsAdapter(activity, list);
		xlvl = new IXListViewListenerImp();
		
	}
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if(isVisibleToUser){
			handler.sendEmptyMessage(LOAD_NEWS);
		}
		super.setUserVisibleHint(isVisibleToUser);
	}
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			xlvl.onRefresh();
		}
	};
	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_yu_news, null);
		lv =(XListView)view.findViewById(R.id.mListView);
		mpb = (ProgressBar)view.findViewById(R.id.detail_loading);
		tv_loading_failed =(TextView) view.findViewById(R.id.tv_loading_failed);

		lv.setAdapter(adapter);
		lv.setPullLoadEnable(true);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(activity,YUNewsDetail.class);
				intent.putExtra("com.pw.schoolknow.ser",list.get(position-1));
				startActivity(intent);
			}
		});
		lv.setXListViewListener(xlvl);
		initCacheData();
		return view;
	}

	private void initCacheData() {
		DataCache dc = new DataCache(activity);
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
			if(!NetHelper.isNetConnected(getActivity())){
				T.showShort(getActivity(),R.string.net_error_tip);
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
			if(!NetHelper.isNetConnected(getActivity())){
				T.showShort(getActivity(),R.string.net_error_tip);
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
			T.showShort(activity, "没有新闻");
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
		HttpUtils http = new HttpUtils();
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
	        		if(parseResult(result,net_list)&&page==0)
	        			new DataCache(activity).save(url, result);
	        			
	        		int news_num = net_list.size()-list.size();
	        			if(news_num>0)
	        				T.showShort(activity, "更新"+news_num+"消息");
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
