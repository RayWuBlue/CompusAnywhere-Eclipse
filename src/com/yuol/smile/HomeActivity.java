package com.yuol.smile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.http.client.util.URLEncodedUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuol.smile.activity.Login;
import com.yuol.smile.activity.UserInfo;
import com.yuol.smile.adapter.HomeChannelAdapter;
import com.yuol.smile.bean.ChannelItem;
import com.yuol.smile.bean.ChannelManage;
import com.yuol.smile.bean.ImageNews;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.DataCache;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.HttpUtil;
import com.yuol.smile.widgets.AutoScrollView;
import com.yuol.smile.widgets.RoundedImageView;

public class HomeActivity extends Activity {
	private AutoScrollView asView;
	private GridView gv;
	private List<ChannelItem> channels;
	private HomeChannelAdapter adapter;
	private Button btnRetry;
	private TextView tv_tip ;
	private TextView tv_tp;
	private TextView tv_weather;
	private ImageView img_weather;
	private LoginHelper lh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		lh = new LoginHelper(HomeActivity.this);
		initView();
		//initCacheData();
		new AsyncLoadScrollNews().execute();
		new AsyncLoadWeather().execute();
	}

	private void initView() {
		setTitle("主页");
		asView = new AutoScrollView(HomeActivity.this);
		btnRetry = (Button) findViewById(R.id.home_btn_retry);
		btnRetry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AsyncLoadScrollNews().execute();

			}
		});
		
		tv_tip = (TextView)findViewById(R.id.home_auto_scrollview_tip);
		tv_tip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new AsyncLoadScrollNews().execute();
			}
		});
		
		if(!HttpUtil.isOpenNetwork(HomeActivity.this)){
			tv_tip.setVisibility(View.VISIBLE);
		}
		
		((RelativeLayout) findViewById(R.id.act_rl_scrollview)).addView(asView
				.getView());
		channels = ((ArrayList<ChannelItem>) ChannelManage.defaultUserChannels);
		adapter = new HomeChannelAdapter(this, channels);

		gv = (GridView) findViewById(R.id.home_channel_grid);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (channels.get(position).getClazz() != null) {
					Bundle bd = new Bundle();
					Intent it = new Intent(HomeActivity.this, channels.get(
							position).getClazz());
					bd.putInt("type", channels.get(position).getId());
					it.putExtras(bd);
					startActivity(it);
				}
			}
		});

		tv_tp = (TextView) findViewById(R.id.tv_tp);
		tv_weather = (TextView) findViewById(R.id.tv_weather);
		img_weather = (ImageView) findViewById(R.id.img_weather);

		findViewById(R.id.home_weather_ll).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new AsyncLoadWeather().execute();
					}
				});
	}

	
/*	private void initCacheData() {
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what==101){
					parseResult(msg.obj.toString());
				}
			}
			
		};
		DataCache dc = new DataCache(HomeActivity.this);
		String url = Api.News.getScrollNews();
		String cache = dc.load(url);
		System.out.println("取到缓存:" + cache);
		if (!"".equals(cache))
			parseResult(cache);
		else
		{
			new Thread(new Runnable() {
				@Override
				public void run() {
					Message msg=new Message();
					msg.what=101;
					String scroll_url = Api.News.getScrollNews();
					msg.obj = HttpUtil.getRequest(scroll_url, null);
					handler.sendMessage(msg);
				}
			}).start();
			
		}
	}*/

	private boolean parseResult(String result) {
		try {
			JSONObject jsonResult = JSON.parseObject(result);
			JSONArray jsonNewsList = jsonResult.getJSONArray("list");
			if (jsonNewsList == null || jsonNewsList.size() == 0) {
				asView.loadFailed();
				btnRetry.setVisibility(View.VISIBLE);
				return false;
			}

			for (int i = 0; i < jsonNewsList.size(); i++) {
				JSONObject jsonNewsItem = jsonNewsList.getJSONObject(i);
				ImageNews item = new ImageNews(jsonNewsItem.getString("id"),
						jsonNewsItem.getString("title"), Api.DOMAIN
								+ jsonNewsItem.getString("cover_url"));
				asView.addImage(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			btnRetry.setVisibility(View.VISIBLE);
		}
		return true;
	}

	// 异步载入新闻
	public class AsyncLoadScrollNews extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String json = GetUtil.getRes(Api.News.getScrollNews());
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			//asView.clear();
			if (null!=result&&!"".equals(result)){
				new DataCache(HomeActivity.this).save(Api.News.getScrollNews(), result);
				parseResult(result);
			}
		}

	}

	private String queryStringForGet(String url) {
		HttpGet request = new HttpGet(url);

		String result = null;

		try {
			HttpResponse response = new DefaultHttpClient().execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public class AsyncLoadWeather extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			// http://php.weather.sina.com.cn/xml.php?city=%B1%B1%BE%A9&password=DJOYnieT8234jlsK&day=0
			List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
			list.add(new BasicNameValuePair("city", "荆州"));
			System.out.println("编码为:" + URLEncodedUtils.format(list, "UTF-8"));
			// String weatherUrl = Api.getWeatherUrl(101200801+"");
			String weatherUrl = "http://php.weather.sina.com.cn/iframe/index/w_cl.php?code=js&day=0&dfc=1&charset=utf-8";
			System.out.println("weatherUrl:" + weatherUrl);
			String result = null;
			result = queryStringForGet(weatherUrl);
			System.out.println("weatherJson:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			System.out.println("天气:" + result);
			System.out.println("天气:" + result);
			try {
				String s1 = result.substring(result.indexOf("s1") + 4,
						result.indexOf("s2") - 2);
				String s2 = result.substring(result.indexOf("s2") + 4,
						result.indexOf("f1") - 2);
				String t1 = result.substring(result.indexOf("t1") + 4,
						result.indexOf("t2") - 2);
				String t2 = result.substring(result.indexOf("t2") + 4,
						result.indexOf("p1") - 2);
				String weather = null;
				if (s1.equals(s2))
					weather = s1;
				else
					weather = s1 + "转" + s2;
				String tem = t1 + "°C/" + t2 + "°C";
				System.out.println(weather + "  " + tem);
				tv_tp.setText(tem);
				tv_weather.setText(weather);
				img_weather.setImageResource(Api.getWeatherIcon(weather));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		asView.stopAutoScroll();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
