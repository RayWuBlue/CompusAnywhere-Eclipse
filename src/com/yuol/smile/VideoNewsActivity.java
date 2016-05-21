package com.yuol.smile;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yuol.smile.adapter.VideoNewsAdapter;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.bean.VideoNews;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.GetUtil;

public class VideoNewsActivity extends BaseActivity {
	private PullToRefreshListView mListView;
	private VideoNewsAdapter mAdapter;
	private int page = 1;
	ArrayList<VideoNews> newsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_news);
		initView();
		initData();
	}

	private void initView() {
		setTitle("视频长大");
		mListView = (PullToRefreshListView) findViewById(R.id.cityListView);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				page = 1;
				new LoadNewsAsyncTask().execute(Api.News.getVideoNews(page));
			}
		});
		mListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				page++;
				new LoadNewsAsyncTask().execute(Api.News.getVideoNews(page));
			}
		});
		
		newsList = new ArrayList<VideoNews>();
		mAdapter = new VideoNewsAdapter(this, newsList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				VideoNews news = new VideoNews();
				news = newsList.get(position);
				Intent it = new Intent();
				it.setAction(Intent.ACTION_VIEW);
				it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				String tvnews_str = news.getTitle().substring(4, 8);
				Uri uri = Uri.parse(Api.News.getVideoNewsDetail(tvnews_str));
				it.setDataAndType(uri, "video/*");
				startActivity(it);
			}
		});

		new LoadNewsAsyncTask().execute(Api.News.getVideoNews(page));
	}

	private void initData() {

	}

	private class LoadNewsAsyncTask extends AsyncTask<Object, Integer, String> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(Object... params) {
			
			return GetUtil.getRes((String) params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			JSONArray jsonArray;
			try {
				jsonArray = new JSONArray(result);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject;
					try {
						jsonObject = jsonArray.getJSONObject(i);
						VideoNews news = new VideoNews();
						news.setId(jsonObject.getInt("id"));
						news.setTitle(jsonObject.getString("title"));
						news.setClick("浏览：" + jsonObject.getString("click"));
						String str = jsonObject.getString("time");
						news.setTime("日期：" + str.substring(0, str.indexOf(" ")));
						newsList.add(news);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			mListView.onRefreshComplete();
/*			mListView.stopLoadMore();
			mListView.stopRefresh();*/
			mAdapter.notifyDataSetChanged();
		}
	}


}
