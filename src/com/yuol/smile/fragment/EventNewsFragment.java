package com.yuol.smile.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yuol.smile.R;
import com.yuol.smile.activity.EventNewsDetail;
import com.yuol.smile.activity.NewsContent;
import com.yuol.smile.adapter.EventAdapter;
import com.yuol.smile.base.IndexItemBase;
import com.yuol.smile.bean.EventBean;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.DataCache;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.T;

public class EventNewsFragment extends Fragment {
	private Context activity;
	private PullToRefreshListView lv;
	private List<IndexItemBase> list;
	private EventAdapter adapter;
	private int page = 1;
	private ProgressBar pb;
	private int type;
	private boolean loaded = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = getArguments().getInt("id");
		list = new ArrayList<IndexItemBase>();
		adapter=new EventAdapter(activity,list);
		
	}
	private void initCacheData() {
		DataCache dc = new DataCache(activity);
		String url=Api.Event.getEventIndex(page);
		String cache = dc.load(url);
		System.out.println("取到缓存:"+cache);
		if(!"".equals(cache))
			parseResult(cache);
	}
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_smile_news, null);
		
		lv=(PullToRefreshListView) root.findViewById(R.id.index_lv);
		pb = (ProgressBar)root.findViewById(R.id.detail_loading);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				IndexItemBase item=list.get(position-1);
				Intent it=new Intent(activity,NewsContent.class);
				Bundle mBundle = new Bundle();
				mBundle.putInt("id",item.getId());
				mBundle.putString("title", item.getTitle());
				mBundle.putString("cover", item.getCover());
				mBundle.putString("time", item.getTime());
				mBundle.putInt("comment", item.getComment_num());
			    it.putExtras(mBundle);  
				startActivity(it);
			}
		});
		
		OnRefreshListener<ListView> onFresh = new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(activity, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				if(!NetHelper.isNetConnected(activity)){
					T.showShort(activity,R.string.net_error_tip);
					lv.onRefreshComplete();
					return;
				}
				page = 1;
				requestNews();
			}
		};
		
		lv.setOnRefreshListener(onFresh);
		lv.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if(!NetHelper.isNetConnected(activity)){
					T.showShort(activity,R.string.net_error_tip);
					lv.onRefreshComplete();
					return;
				}
				if(list.size()!=0){
					page++;
					requestNews();
				
			}
				}
		});
		if(!loaded){
			initCacheData();
			firstLoad();
		}
		return root;
	}

	public void firstLoad(){
		if(!NetHelper.isNetConnected(activity)){
			T.showShort(activity,R.string.net_error_tip);
			lv.onRefreshComplete();
			return;
		}
		page = 1;
		requestNews();
	}
	//异步载入新闻
	private boolean parseResult(String result) {
		JSONObject jsonResult = JSON.parseObject(result);
		JSONArray jsonNewsList = jsonResult.getJSONArray("list");
		if(jsonNewsList==null){
			lv.onRefreshComplete();
			T.showShort(activity, "没有新闻");
			return false;
		}
		if(page==1){
			list.clear();
			adapter.notifyDataSetChanged();
			lv.onRefreshComplete();
		}
		for(int i=0;i<jsonNewsList.size();i++){

			JSONObject jsonNewsItem = jsonNewsList.getJSONObject(i);

			IndexItemBase item=new IndexItemBase();
			item.setId( Integer.parseInt(jsonNewsItem.getString("id")));
			item.setIntro( jsonNewsItem.getString("description"));
			item.setSource(jsonNewsItem.getString("source"));
			item.setComment_num( Integer.parseInt(jsonNewsItem.getString("comment")));
			item.setTitle(jsonNewsItem.getString("title"));
			item.setTime(jsonNewsItem.getString("update_time"));
			item.setView(jsonNewsItem.getString("view"));
			
			System.out.println("TIME:"+jsonNewsItem.getString("update_time"));
			
			/*List<String> imgList = new ArrayList<String>();
			JSONArray jsonImgList = jsonNewsItem.getJSONArray("imgList");
			if(jsonImgList!=null&&jsonImgList.size()>0)
			for(int j = 0;j<jsonImgList.size();j++){
				String str = jsonImgList.getString(j);
				if(!str.contains("http://"))str = Api.DOMAIN+str;
				imgList.add(str);//此处用于调试是使用
				//imgList.add(jsonImgList.getString(j));
				System.out.println(str);
			}
			item.setImgList(imgList);*/
			
			item.setCover(jsonNewsItem.getString("cover_url"));
			
			list.add(item);

		}
		adapter.notifyDataSetChanged();
		return true;
	}
	private void requestNews() {
		final String url=Api.News.getNewsList(type,page);
		new AsyncTask<Void, Void, String>() {
			@Override
			protected void onPreExecute() {
				pb.setVisibility(View.VISIBLE);
			};
			@Override
			protected String doInBackground(Void... arg0) {
				return GetUtil.getRes(url);
			}
			@Override
			protected void onPostExecute(String result) {
	        	System.out.println("活动:"+result);
	        	try {
	        		if(parseResult(result)&&page==1)
	        			new DataCache(activity).save(url, result);
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					pb.setVisibility(View.GONE);
					lv.onRefreshComplete();
				}
			}
		}.execute();

	}
}
