package com.yuol.smile.fragment;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

import com.yuol.smile.JwcNewsDetail;
import com.yuol.smile.R;
import com.yuol.smile.activity.WebContent;
import com.yuol.smile.adapter.Jwc_NewsAdapter;
import com.yuol.smile.bean.JwcNews;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.T;
import com.yuol.smile.utils.TimeUtil;
import com.yuol.smile.widgets.XListView;
import com.yuol.smile.widgets.XListView.IXListViewListener;

public class JwcNewsFragment extends Fragment {
	private View rootview = null;
	private XListView lv = null;
	private Jwc_NewsAdapter adapter = null;
	private List<JwcNews> list;
	private int cloumn_num = 0;
	private int page = 1;
	private ProgressBar pb;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.fragment_jwc_news, container,false);
		list = new ArrayList<JwcNews>();

		adapter = new Jwc_NewsAdapter(list, getActivity());
		lv = (XListView) rootview.findViewById(R.id.frame_listview_jwcnews);
		pb = (ProgressBar)rootview.findViewById(R.id.detail_loading);
		lv.setPullLoadEnable(true);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				JwcNews jwcnews = (JwcNews) parent.getItemAtPosition(position);
				if (jwcnews != null) {
					Intent intent = null; 
					if(jwcnews.getLink().contains("jwc")){
					intent = new Intent(getActivity(),JwcNewsDetail.class);
					Bundle bundle = new Bundle();
					bundle.putString("link", jwcnews.getLink());
					bundle.putString("title", jwcnews.getTitle());
					bundle.putString("time", jwcnews.getTime());
					intent.putExtras(bundle);
					getActivity().startActivity(intent);
					}else
					{
						intent = new Intent(getActivity(),WebContent.class);
						Bundle bundle = new Bundle();
						bundle.putString("url", jwcnews.getLink());
						bundle.putString("title", jwcnews.getTitle());
						intent.putExtras(bundle);
						getActivity().startActivity(intent);
					}
					
				}
			}
		});
		
		IXListViewListenerImp xlvl = new IXListViewListenerImp();
		lv.setXListViewListener(xlvl);
		
		setUserVisibleHint(true);
		
		return rootview;
	}

	 @Override
	 public void setUserVisibleHint(boolean isVisibleToUser) {
	        super.setUserVisibleHint(isVisibleToUser);
	        if (isVisibleToUser) {
				new AsyncLoadJwcNews().execute();
	        } else {
	           onPause();
	        }
	    }
	
	public static JwcNewsFragment getinstant(int num) {
		JwcNewsFragment jwcNews = new JwcNewsFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("jwc_news_cloumn", num);
		jwcNews.setArguments(bundle);
		return jwcNews;
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
			new AsyncLoadJwcNews().execute();
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
				new AsyncLoadJwcNews().execute();
			}
			
		}
		
	}
	
	private class AsyncLoadJwcNews extends AsyncTask<Integer, Void, String> {

		private void getDataByDoc(Document doc) {
			JwcNews jwcnews;
			Elements news_ul = doc.select("#list_r ul li");
			System.out.println("Elements:"+news_ul);
			for (Element news : news_ul) {
				Element link = news.select("a").first();
				String linkText =  link.text();
				System.out.println(linkText);//调试输出
				String linkUrl = link.attr("abs:href");
				Element element_time =news.select("span").first();
				String link_time = element_time.text();
				jwcnews = new JwcNews(linkText, link_time,linkUrl);
				list.add(jwcnews);
			}
		}

		@Override
		protected String doInBackground(Integer... params) {
			cloumn_num = getArguments().getInt("jwc_news_cloumn");
			if (cloumn_num == 0) 
					return GetUtil.getRes(Api.News.getJwcNewsNotify(page));
			 else
					return GetUtil.getRes(Api.News.getJwcNewsLatest(page));
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if(page==1)list.clear();
				Document doc = Jsoup.parse(result);
				getDataByDoc(doc);
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				System.out.println("解析出现异常");
				e.printStackTrace();
			}finally{
				lv.stopRefresh();
				lv.stopLoadMore();
				pb.setVisibility(View.GONE);
			}

		}

	}
}
