package com.yuol.smile.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuol.smile.R;
import com.yuol.smile.adapter.CommentAdapter;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.db.DBUtil;
import com.yuol.smile.db.SQLHelper;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.AndroidShare;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.DownloadManageUtil;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.MyAlertMenu;
import com.yuol.smile.widgets.MyProgressBar;
import com.yuol.smile.widgets.NoScrollListView;
import com.yuol.smile.widgets.MyAlertMenu.MyDialogMenuInt;

public class NewsContent extends BaseActivity {

	
	private NoScrollListView lv;
	private CommentAdapter adapter;
	private List<Map<String, Object>> list;
	
	private WebSettings webSettings = null;
	private WebView web = null;
	private MyProgressBar mpb;
	//private IndexItemBase item;

	private LoginHelper loginHelper;

	@ViewInject(R.id.part_sorry_tip_layout)
	private RelativeLayout clickReLoad;

	//private IndexItemBase temmItem;

	public Context mcontext;

	private Cursor result = null;
	private Boolean IsCollected = false;
	private ImageView btnCollect;

	private ImageView imageCover;
	private TextView imageText;
	private TextView comment_num;
	private Bundle bd;
	private int id;
	private String title;
	private String cover;
	private String time;
	private int comment;
	protected MyAlertMenu mam;
	private TextView text_no_comment;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_news_content);

		ViewUtils.inject(this);
		
		mcontext = this;

		loginHelper = new LoginHelper(this);

		text_no_comment = (TextView) findViewById(R.id.no_comment_tip);
		text_no_comment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!loginHelper.hasLogin())
					loginHelper.ToLogin();
				else
				{
					Intent it = new Intent(NewsContent.this, NewsComment.class);
					Bundle mBundle = new Bundle();
					mBundle.putString("id", id+"");
					it.putExtras(mBundle);
					startActivity(it);
				}
			}
		});
		
		lv = (NoScrollListView) super.findViewById(R.id.news_content_comment_lv);
		list = new ArrayList<Map<String, Object>>();
		adapter = new CommentAdapter(this, list);
		lv.setAdapter(adapter);
		findViewById(R.id.comment_more).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(NewsContent.this, NewsComment.class);
				Bundle mBundle = new Bundle();
				mBundle.putString("id", id+"");
				it.putExtras(mBundle);
				startActivity(it);
			}
		});
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final int p = arg2; 
				mam=new MyAlertMenu(NewsContent.this, new String[]{"回复","复制"});
				mam.setOnItemClickListener(new MyDialogMenuInt() {
					@SuppressLint("NewApi")
					public void onItemClick(int position) {
						switch(position){
						case 0:
							Intent it = new Intent(NewsContent.this, NewsComment.class);
							Bundle mBundle = new Bundle();
							mBundle.putString("nickname", list.get(p).get("name").toString());
							mBundle.putString("id", id+"");
							it.putExtras(mBundle);
							startActivity(it);
							break;
						case 1:
							ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
							String contentVal=list.get(p).get("content").toString().trim();
							int start=contentVal.indexOf("</at>");
							if(start!=-1){
								start+=5;
								contentVal=contentVal.substring(start);
							}
							cmb.setText(contentVal);
							T.showShort(NewsContent.this,"已复制到剪贴板");
							break;
						}
					}
				
			});
			}
			
		});
		new AsyncLoadComment().execute("getNew");
		
		bd = getIntent().getExtras();
		
		id = bd.getInt("id");
		
		title = bd.getString("title");

		cover = bd.getString("cover");
		
		time = bd.getString("time");
		
		comment = bd.getInt("comment");
		
		//title = bd.getString("title");
		/*temmItem = (IndexItemBase) getIntent().getSerializableExtra(
				NewsActivity.SER_KEY);*/

		/*if (temmItem != null && item == null) {
			item = temmItem;
		}else{
			finish();
		}*/

		web = (WebView) super.findViewById(R.id.news_content_webview);

		// 点击图片重新加载
		clickReLoad.setOnClickListener(new OnClickListener() {
			@Override
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
		web.loadUrl(Api.News.getNewsDetail(id));
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

		//String img = item.getCover();
		View cover_ll = findViewById(R.id.news_cover_ll);
		cover_ll.setVisibility(View.VISIBLE);
		imageCover = (ImageView) cover_ll.findViewById(R.id.news_cover);
		imageText = (TextView) cover_ll.findViewById(R.id.news_text);
		if (null!=cover&&!"".equals(cover)){
			//BitmapUtils bm = MyApplication.getInstance().getBitmapUtils();
			//bm.display(imageCover, img);
			DisplayImageOptions options = new DisplayImageOptions.Builder()
	        .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
	        .showImageOnFail(R.drawable.white)              //加载失败时的图片  
	        .cacheInMemory()                               //启用内存缓存  
	        .cacheOnDisc()                                 //启用外存缓存  
	        .build();
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(cover,imageCover,options);			
		}else
		{
			imageCover.setVisibility(View.GONE);
			imageText.setBackgroundColor(getResources().getColor(R.color.blue));
			imageText.setGravity(Gravity.CENTER);
		}
		imageText.setText(title);
		initcollect();

	}

	public class AsyncLoadComment extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String uid = new LoginHelper(NewsContent.this).getUid();
			if (!TextUtils.isEmpty(uid))
				return GetUtil.getRes(Api.News.getNewsComment(id+"", uid, 1));
			else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null) {
				return;
			}
			
			try {
				JSONObject jsonobj = JSON.parseObject(result);
				int sum = Integer.parseInt(jsonobj.getString("total_count"));
				comment_num.setText(sum+"");
				if(sum>10){
					findViewById(R.id.comment_more).setVisibility(View.VISIBLE);
				}else
				{
					findViewById(R.id.comment_more).setVisibility(View.GONE);
				}
				JSONArray data = jsonobj.getJSONArray("list");
				
				if (sum == 0) {
					
					text_no_comment.setVisibility(View.VISIBLE);
					text_no_comment.setText("还没有人评论，还不来抢沙发~");
					return;
				}else{
					findViewById(R.id.no_comment_tip).setVisibility(View.GONE);
				}
				
				for (int i = 0; i < data.size(); i++) {
					JSONObject commentItem = data.getJSONObject(i);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", commentItem.getInteger("id"));
					JSONObject user = commentItem.getJSONObject("user");
					if (user != null) {
						map.put("name", user.getString("nickname"));
						map.put("head_image",
								Api.DOMAIN + user.getString("avatar64"));
					} else {
						map.put("name", "游客");
						map.put("head_image", Api.User.getDefaultUserImage());
					}

					map.put("time", commentItem.getString("create_time"));
					map.put("content", commentItem.getString("content"));
					map.put("uid", commentItem.getInteger("uid"));
					list.add(map);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			adapter.notifyDataSetChanged();
		}

	}
	
	private void initToolbar() {
		comment_num = (TextView) addButton(comment+"",R.drawable.comment,new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(NewsContent.this, NewsComment.class);
				Bundle mBundle = new Bundle();
				mBundle.putString("id", id+"");
				it.putExtras(mBundle);
				startActivity(it);
			}
		}).findViewById(R.id.btn_text);
		
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
				AndroidShare as = new AndroidShare(NewsContent.this, "#"+title + "#"
						+ Api.News.getNewsDetail(id), "");
				as.show();
			}
		});
	}

	private void Oncollect() {
		DBUtil dbu = DBUtil.getInstance(NewsContent.this);
		if (!IsCollected) {
			try {
				ContentValues values = new ContentValues();
				values.put("news_id", id);
				values.put("news_title", title);
				values.put("news_time", time);
				values.put("news_column", "校园资讯");
				//List<String> list = temmItem.getImgList();
				values.put("news_image", cover);
				long insert_result = dbu.insertData(SQLHelper.TABLE_COLLECT,
						values);
				if (insert_result != -1) {
					IsCollected = true;
					T.show(NewsContent.this, "收藏成功", Toast.LENGTH_SHORT);
					btnCollect.setImageResource(R.drawable.colllected);
				} else
					T.show(NewsContent.this, "收藏失败", Toast.LENGTH_SHORT);
				System.out.println("插入结果：" + insert_result);
			} catch (SQLException e) {
				T.show(NewsContent.this, "收藏失败，请测试或反馈信息", Toast.LENGTH_SHORT);
				e.printStackTrace();
			}
		} else {
			T.show(NewsContent.this, "取消收藏", Toast.LENGTH_SHORT);
			dbu.execSQL("delete from collect_tb where news_id="+ id);
			IsCollected = false;
			btnCollect.setImageResource(R.drawable.colllect);
		}

	}

	private void initcollect() {
		/*
		 * db = openOrCreateDatabase("news_db.db", Context.MODE_PRIVATE, null);
		 * db.execSQL("");
		 */
		DBUtil dbu = DBUtil.getInstance(NewsContent.this);
		result = dbu.rawQuery("SELECT news_id FROM collect_tb where news_id="+ id);
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
			
			findViewById(R.id.comment_ll).setVisibility(View.VISIBLE);
			super.onPageFinished(view, url);
			// html加载完成之后，添加监听图片的点击js函数
			addImageClickListner();

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			web.setVisibility(View.GONE);
			mpb = new MyProgressBar(NewsContent.this);
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
		if(mpb!=null)
			mpb.dismiss();
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
