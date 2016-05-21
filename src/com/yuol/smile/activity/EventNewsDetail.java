package com.yuol.smile.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuol.smile.R;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.bean.EventBean;
import com.yuol.smile.db.DBUtil;
import com.yuol.smile.db.SQLHelper;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.DownloadManageUtil;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.MyProgressBar;

public class EventNewsDetail extends BaseActivity {

	private WebSettings webSettings = null;
	private WebView web = null;
	private MyProgressBar mpb;
	private EventBean item;

	private LoginHelper loginHelper;

	@ViewInject(R.id.part_sorry_tip_layout)
	private RelativeLayout clickReLoad;

	private EventBean temmItem;

	public Context mcontext;

	private Cursor result = null;
	private Boolean IsCollected = false;
	private ImageView btnCollect;

	private ImageView imageCover;
	private TextView imageText;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_event_news_content);

		ViewUtils.inject(this);
		
		mcontext = this;

		loginHelper = new LoginHelper(this);

		temmItem = (EventBean) getIntent().getSerializableExtra(Api.SER_KEY);

		if (temmItem != null && item == null) {
			item = temmItem;
		} 

		((TextView)findViewById(R.id.people)).setText("发起人："+item.getPeople());
		((TextView)findViewById(R.id.time)).setText("时间："+item.getTime());
		((TextView)findViewById(R.id.address)).setText("地点："+item.getAddress());
		((TextView)findViewById(R.id.limit)).setText("截止日期："+item.getDeadline());
		
		/*((TextView)findViewById(R.id.people)).setText(item.getPeople());
		((TextView)findViewById(R.id.people)).setText(item.getPeople());
		((TextView)findViewById(R.id.people)).setText(item.getPeople());*/
		
		web = (WebView) super.findViewById(R.id.news_content_webview);

		// 点击图片重新加载
		clickReLoad.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (!NetHelper.isNetConnected(mcontext)) {
					T.showShort(mcontext, R.string.net_error_tip);
				} else {
					onCreate(null);
				}

			}
		});

		// 网络故障不加载
		if (!NetHelper.isNetConnected(this)) {
			web.setVisibility(View.GONE);
			clickReLoad.setVisibility(View.VISIBLE);
			return;
		} else {
			web.setVisibility(View.VISIBLE);
			clickReLoad.setVisibility(View.GONE);
		}

		webSettings = web.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBlockNetworkImage(false);
		webSettings.setUseWideViewPort(false);
		webSettings.setLoadWithOverviewMode(true);
		// web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// web.setHorizontalScrollBarEnabled(false);
		web.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

		// web.loadUrl(ServerConfig.HOST+"/schoolknow/temmItem.php?id="+item.getId());
		// http://localhost/index.php?s=/blog/article/detail/id/140.html
		//web.loadUrl(Api.News.getNewsDetail(item.getId()));
		
		String s = "<html><head><meta charset=\"utf-8\" /><title>活动</title><style type=\"text/css\">body{margin: 0px;padding: 8px;}img{width:100%;}</style></head><body >"
				+item.getExplain()+ "</body></html>";
		
		web.loadDataWithBaseURL(null,s, "text/html","utf-8", null);
		web.addJavascriptInterface(new JavascriptInterface(this),
				"imagelistner");
		web.setDownloadListener(new MyWebViewDownLoadListener());
		web.setWebViewClient(new MyWebViewClient());

		web.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return false;
			}
		});
		
		initToolbar();

		String img = item.getCover();

		if (null!=img&&!"".equals(img)){
			View cover_ll = findViewById(R.id.news_cover_ll);
			cover_ll.setVisibility(View.VISIBLE);
			imageCover = (ImageView) cover_ll.findViewById(R.id.news_cover);
			imageText = (TextView) cover_ll.findViewById(R.id.news_text);
			//BitmapUtils bm = MyApplication.getInstance().getBitmapUtils();
			//bm.display(imageCover, img);
			DisplayImageOptions options = new DisplayImageOptions.Builder()
	        .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
	        .showImageOnFail(R.drawable.white)              //加载失败时的图片  
	        .cacheInMemory()                               //启用内存缓存  
	        .cacheOnDisc()                                 //启用外存缓存  
	        .build();

			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(img,imageCover,options);
			imageText.setText(item.getTitle());
		}
		initcollect();

	}

	private void initToolbar() {

		
		View view = addButton(R.drawable.colllect, new OnClickListener() {

			@Override
			public void onClick(View v) {
				Oncollect();
			}
		});
		
		btnCollect = (ImageView) view.findViewById(R.id.btn_image);
		
		addButton(R.drawable.intro_to_gay, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain"); 
				intent.putExtra(Intent.EXTRA_SUBJECT, item.getTitle());
				intent.putExtra(Intent.EXTRA_TEXT, item.getTitle());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, item.getTitle()));
				
			}
		});
	}

	private void Oncollect() {
		DBUtil dbu = DBUtil.getInstance(EventNewsDetail.this);
		if (!IsCollected) {
			try {
				ContentValues values = new ContentValues();
				values.put("news_id", EventNewsDetail.this.temmItem.getId());
				values.put("news_title", EventNewsDetail.this.temmItem.getTitle());
				values.put("news_time", EventNewsDetail.this.temmItem.getTime());
				values.put("news_column", "校园活动");
				//List<String> list = temmItem.getImgList();
				values.put("news_image", temmItem.getCover());
				long insert_result = dbu.insertData(SQLHelper.TABLE_COLLECT,
						values);
				if (insert_result != -1) {
					IsCollected = true;
					T.show(EventNewsDetail.this, "收藏成功", Toast.LENGTH_SHORT);
					btnCollect.setImageResource(R.drawable.colllected);
				} else
					T.show(EventNewsDetail.this, "收藏失败", Toast.LENGTH_SHORT);
				System.out.println("插入结果：" + insert_result);
			} catch (SQLException e) {
				T.show(EventNewsDetail.this, "收藏失败，请测试或反馈信息", Toast.LENGTH_SHORT);
				e.printStackTrace();
			}
		} else {
			T.show(EventNewsDetail.this, "取消收藏", Toast.LENGTH_SHORT);
			dbu.execSQL("delete from collect_tb where news_id="
					+ temmItem.getId());
			IsCollected = false;
			btnCollect.setImageResource(R.drawable.colllect);
		}

	}

	private void initcollect() {
		/*
		 * db = openOrCreateDatabase("news_db.db", Context.MODE_PRIVATE, null);
		 * db.execSQL("");
		 */
		DBUtil dbu = DBUtil.getInstance(EventNewsDetail.this);
		int id = EventNewsDetail.this.temmItem.getId();
		result = dbu.rawQuery("SELECT news_id FROM collect_tb where news_id="
				+ id);
		result.moveToFirst();
		if (result != null && result.getCount() >= 1) {
			IsCollected = true;
			btnCollect.setImageResource(R.drawable.colllected);
		} else {
			IsCollected = false;
			btnCollect.setImageResource(R.drawable.colllect);
		}
		result.close();
	}

	// 注入js函数监听
	private void addImageClickListner() {
		// 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
		web.loadUrl("javascript:(function(){"
				+ "var objs = document.getElementsByTagName(\"img\"); "
				+ "for(var i=0;i<objs.length;i++)  " + "{"
				+ "    objs[i].onclick=function()  " + "    {  "
				+ "        window.imagelistner.openImage(this.src);  "
				+ "    }  " + "}" + "})()");
	}

	// js通信接口
	public class JavascriptInterface {

		private Context context;

		public JavascriptInterface(Context context) {
			this.context = context;
		}

		public void openImage(String img) {
			Intent intent = new Intent();
			intent.putExtra("imgsrc", img);
			intent.setClass(context, ImgPreview.class);
			context.startActivity(intent);
		}
	}

	// 监听
	@SuppressLint("SetJavaScriptEnabled")
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			web.setVisibility(View.VISIBLE);
			mpb.dismiss();
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageFinished(view, url);
			// html加载完成之后，添加监听图片的点击js函数
			addImageClickListner();

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			web.setVisibility(View.GONE);
			mpb = new MyProgressBar(EventNewsDetail.this);
			mpb.setMessage("正在加载中...");
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			super.onReceivedError(view, errorCode, description, failingUrl);

		}
	}

	/**
	 * 调用系统浏览器进行下载
	 * 
	 */
	private class MyWebViewDownLoadListener implements DownloadListener {
		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			// 调用其他浏览器下载
			/*
			 * Uri uri = Uri.parse(url); Intent intent = new
			 * Intent(Intent.ACTION_VIEW, uri); startActivity(intent);
			 */

			// 调用系统下载
			DownloadManageUtil.DownloadFile(mcontext, url, "/yuol/DownLoad");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// DownloadUtil.unregisterReceiver(this);
	}
	@Override
	public void onPause() {// 继承自Activity
		super.onPause();
		web.onPause();
	}

	@Override
	public void onResume() {// 继承自Activity
		super.onResume();
		web.onResume();
	}



}
