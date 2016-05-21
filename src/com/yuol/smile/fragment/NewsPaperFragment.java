package com.yuol.smile.fragment;

import java.util.ArrayList;

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
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.yuol.smile.NewsPaperDetail;
import com.yuol.smile.R;
import com.yuol.smile.adapter.PaperNowAdapter;
import com.yuol.smile.bean.NewsPaper;

public class NewsPaperFragment extends Fragment {
	private String url;
	private View rootview = null;
	private ListView lv = null;
	private PaperNowAdapter adapter = null;
	private Document doc;
	private int cloumn_num;
	private ArrayList<NewsPaper> list = new ArrayList<NewsPaper>();
	private ProgressBar pb;
	
	public NewsPaperFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.fragment_newspaper_now, container,false);
		init();
		return rootview;
	}

	private void init() {
		lv = (ListView) rootview.findViewById(R.id.paper_part_list);
		pb = (ProgressBar)rootview.findViewById(R.id.detail_loading);
		adapter = new PaperNowAdapter(list, getActivity());
		lv.setAdapter(adapter);
		cloumn_num = getArguments().getInt("cloumn_num");
		url = getArguments().getString("url");
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent it = new Intent(getActivity(), NewsPaperDetail.class);
				Bundle bundle = new Bundle();
				bundle.putString("link", list.get(position).getUrl_detail());
				bundle.putString("time", list.get(position).getIssue_time());
				bundle.putString("title", list.get(position).getTitle());
				it.putExtras(bundle);
				getActivity().startActivity(it);
			}
		});
		if(list.size()==0)
			new AsyncLoadNews().execute();
	}

	public static NewsPaperFragment getInstant(int cloumn, String url) {
		System.out.println("cloumn_num" + cloumn);
		NewsPaperFragment fragment = new NewsPaperFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("cloumn_num", cloumn);
		bundle.putString("url", url);
		fragment.setArguments(bundle);
		return fragment;
	}

	public class AsyncLoadNews extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				System.out.println("url" + url);
				doc = Jsoup.connect(url).get();
				Element link = doc.select("[href^=textlist]").get(0);
				Elements els = doc.select("[height=22]");
				Element e_num = doc.select("[style^=padding-left:30px]").get(0);
				String issue_time = "";
				String issue_num = "";
				if (els != null && els.size() > 2) {
					String issue_time_str[] = els.get(1).select("div").text()
							.split(" ");
					issue_time = issue_time_str[0];
				}

				if (e_num != null) {
					String str = e_num.text().trim();
					issue_num = str.substring(6, str.length() - 9);
					System.out.println("issue_num" + issue_num);
				}
				System.out.println("time" + issue_time);
				url = link.attr("abs:href");// 获取文字版地�?
				doc = Jsoup.connect(url).get();
				Elements tables = doc.select("[style*=BORDER-RIGHT: #9d9c77]");
				NewsPaper mPaper;
				String title;
				String url;
				Element table = tables.get(cloumn_num);
				Elements links = table.select(".yaowen");
				for (Element a : links) {
					title = a.text().trim();
					url = a.attr("abs:href");
					mPaper = new NewsPaper();
					mPaper.setTitle(title);
					mPaper.setUrl_detail(url);
					mPaper.setIssue_time(issue_time);
					mPaper.setIssue_num(issue_num);
					list.add(mPaper);
				}

				/*
				 * //不存在本地数据库就更�?NewsDB newsDB=new NewsDB(Index.this);
				 * if(!newsDB.hasExistNews(item.getId())){ new
				 * NewsDB(Index.this).insert(item); }
				 */

			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();
			pb.setVisibility(View.GONE);
		}

	}
}
