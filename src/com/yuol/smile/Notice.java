package com.yuol.smile;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yuol.smile.activity.NewsContent;
import com.yuol.smile.adapter.IndexAdapter;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.base.IndexItemBase;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.DataCache;
import com.yuol.smile.utils.T;
import com.yuol.smile.utils.TimeUtil;
import com.yuol.smile.widgets.XListView;
import com.yuol.smile.widgets.XListView.IXListViewListener;

public class Notice extends BaseActivity {
	private XListView lv;
	private List<IndexItemBase> list;

	private IndexAdapter adapter;
	private int page = 1;
	private ProgressBar pb;
	public  final static String SER_KEY = "com.pw.schoolknow.ser";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_smile_news);
		list = new ArrayList<IndexItemBase>();
		adapter=new IndexAdapter(Notice.this,list);
		initView();
	}
	public void initView() {
		setTitle("系统公告");
		lv=(XListView) findViewById(R.id.index_lv);
		pb = (ProgressBar)findViewById(R.id.detail_loading);
		lv.setPullLoadEnable(true);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				IndexItemBase item=(IndexItemBase) lv.getItemAtPosition(position);
				Intent it=new Intent(Notice.this,NewsContent.class);
				Bundle mBundle = new Bundle();  
			    mBundle.putSerializable(SER_KEY,item);
			    mBundle.putBoolean("hide_toolbar",true);
			    it.putExtras(mBundle);  
				startActivity(it);
			}
		});
		IXListViewListenerImp xlvl = new IXListViewListenerImp();
		lv.setXListViewListener(xlvl);
		xlvl.onRefresh();
	}
	class IXListViewListenerImp implements IXListViewListener{
		@Override
		public void onRefresh() {
			if(!NetHelper.isNetConnected(Notice.this)){
				T.showShort(Notice.this,R.string.net_error_tip);
				lv.stopRefresh();
				lv.stopLoadMore();
				return;
			}
			lv.setRefreshTime(""+TimeUtil.getUpdateTime(System.currentTimeMillis()));
			page = 1;
			requestNews();
		}
		@Override
		public void onLoadMore() {
			if(!NetHelper.isNetConnected(Notice.this)){
				T.showShort(Notice.this,R.string.net_error_tip);
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
	//异步载入新闻
	private boolean parseResult(String result) {

		JSONObject jsonResult = JSON.parseObject(result);
		JSONArray jsonNewsList = jsonResult.getJSONArray("list");
		if(jsonNewsList==null){
			lv.stopRefresh();
			lv.stopLoadMore();
			return false;
		}
		if(page==1){
			list.clear();
			adapter.notifyDataSetChanged();
			lv.stopRefresh();
			lv.stopLoadMore();
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
		HttpUtils http = new HttpUtils();
		final String url=Api.News.getNewsList(Api.News.NOTICE_ID,page);//系统公告的ID为46
		http.send(HttpRequest.HttpMethod.GET,url,new RequestCallBack<String>(){
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	try {
		        		if(parseResult(responseInfo.result)&&page==1)
		        			new DataCache(Notice.this).save(url, responseInfo.result);
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						pb.setVisibility(View.GONE);
						lv.stopRefresh();
						lv.stopLoadMore();
					}
		        }

		        @Override
		        public void onStart() {
		        	pb.setVisibility(View.VISIBLE);
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	T.showShort(Notice.this, "请求超时,请稍后重试");
		        	pb.setVisibility(View.GONE);
		        	lv.stopRefresh();
					lv.stopLoadMore();
		        }
		});
	}
	
}
