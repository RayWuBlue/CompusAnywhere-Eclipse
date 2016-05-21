package com.yuol.smile.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

import com.yuol.smile.JobNewsDetailActivity;
import com.yuol.smile.R;
import com.yuol.smile.adapter.Job_NewsAdapter;
import com.yuol.smile.bean.JobNews;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.DataCache;
import com.yuol.smile.utils.T;
import com.yuol.smile.utils.TimeUtil;
import com.yuol.smile.widgets.XListView;
import com.yuol.smile.widgets.XListView.IXListViewListener;

public class JobNewsFragment extends Fragment {
	private View rootview = null;
	private String url = "";
	private XListView lv = null;
	private Job_NewsAdapter adapter = null;
	private ArrayList<JobNews> list;
	private int cloumn_num = 0;
	private int pagepart = 1;
	private int page = 1;
	private ProgressBar pb;
	private DataCache dc;
	private Activity activity;
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.fragment_job_news, container,false);
		init();
		return rootview;
	}

	class OnItemClickListenerimpl implements OnItemClickListener {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			//if(position>list.size())return;
			JobNews jobnews = (JobNews) parent.getItemAtPosition(position);
			Intent intent = new Intent(getActivity(),JobNewsDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("link", jobnews.getLink());
			bundle.putString("cloumn", jobnews.getCloumn());
			bundle.putString("title", jobnews.getTitle());
			bundle.putString("time", jobnews.getTime());
			intent.putExtras(bundle);
			getActivity().startActivity(intent);
		}
	}
	 
	private void init() {

		list = new ArrayList<JobNews>();
		cloumn_num = getArguments().getInt("job_news_cloumn");

		adapter = new Job_NewsAdapter(list, getActivity());
		lv = (XListView) rootview.findViewById(R.id.frame_listview_jobnews);
		pb = (ProgressBar)rootview.findViewById(R.id.detail_loading);
		lv.setAdapter(adapter);
		lv.setPullLoadEnable(true);
		lv.setOnItemClickListener(new OnItemClickListenerimpl());
		
		IXListViewListenerImp xlvl = new IXListViewListenerImp();
		lv.setXListViewListener(xlvl);
		dc = new DataCache(activity);
		xlvl.onRefresh();
	}


	public static JobNewsFragment getinstant(int num) {
		JobNewsFragment jobNews = new JobNewsFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("job_news_cloumn", num);
		jobNews.setArguments(bundle);
		return jobNews;
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
			page = 1;
			new AsyncLoadNews().execute();
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
				new AsyncLoadNews().execute();
			}
			
		}
		
	}
	//异步载入新闻
	public class AsyncLoadNews extends AsyncTask<Integer, Void, Document>{
		
		@Override
		protected void onPreExecute() {
			pb.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}
		@Override
		protected Document doInBackground(Integer... params) {
			Document doc = null;
			try {
				switch (cloumn_num) {
				case 0:
					url = Api.News.getJobNewsNotify(page);
					break;
				case 1:
					url = Api.News.getJobNewsContext(page);
					break;
				case 2:
					url = Api.News.getJobNewsInfo(page);
					break;
				}
				doc = Jsoup.connect(url).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return doc;
		}

		@Override
		protected void onPostExecute(Document result) {
			super.onPostExecute(result);

			if(page==1)
				list.clear();

			Elements tr_all = null;
			Element tr = null;
			Element link = null;
			String title = "";
			String time = "";
			String click = "";
			String click_ = "";
			String url_detail;
			JobNews jobnews;
			try {
				tr_all = result.select("#list_left").select("tr").get(1)
						.select("tbody").get(0).children();
				int startnum = (pagepart == 1 ? 0 : 15);
				for (int i = 0; i < 15; i++) {
					tr = tr_all.get(i + startnum);
					System.out.println(tr);
					link = tr.select("a").first();
					// 获取title
					title = link.text();
					// 获取url
					url_detail = link.attr("abs:href");
					// 获取time
					time = tr.select("tbody").get(0).select("td").get(2).text()
							.trim();
					// 获取访问�?
					click_ = link.nextElementSibling().text().trim();
					click = Pattern.compile("[^0-9]").matcher(click_)
							.replaceAll("").trim();
					if (url_detail == "")
						url_detail = "http://jyxx.yangtzeu.edu.cn"
								+ link.attr("href");// 缓存文件无法获取绝对地址
					jobnews = new JobNews(title, time, url_detail, click);
					list.add(jobnews);
				}
				adapter.notifyDataSetChanged();	
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}finally{
				lv.stopRefresh();
				lv.stopLoadMore();
				pb.setVisibility(View.GONE);
			}
			
		}
		
	}
}

