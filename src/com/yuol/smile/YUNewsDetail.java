package com.yuol.smile;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.yuol.smile.activity.ImgPreview;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.bean.YUNewsItem;
import com.yuol.smile.db.DBUtil;
import com.yuol.smile.db.SQLHelper;
import com.yuol.smile.utils.AndroidShare;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.HttpUtil;
import com.yuol.smile.utils.T;


@SuppressLint("JavascriptInterface")
public class YUNewsDetail extends BaseActivity {
	//private FrameLayout customview_layout;
	private String news_url;
	private String news_title;
	private String news_date;
	
	private YUNewsItem news;
	//private TextView action_comment_count;
	WebView webView;
	
	private ImageButton btnCollect;
	 private Cursor result=null;
     private Boolean IsCollected=false;
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yunews_detail);
		getData();
		initView();
		initWebView();
	}

	private void getData() {
		news = (YUNewsItem) getIntent().getSerializableExtra("com.pw.schoolknow.ser");
		news_url = Api.getUrlById(news.getId());
		news_title = news.getTitle();
		news_date = String.valueOf(news.getPublishTime());
	}


	
	private void initWebView() {
		webView = (WebView)findViewById(R.id.wb_details);
		new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		if (!TextUtils.isEmpty(news_url)) {
			WebSettings settings = webView.getSettings();
			//settings.setJavaScriptEnabled(true);
			//settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
			settings.setSupportZoom(false);
			settings.setBuiltInZoomControls(false);
			webView.setBackgroundResource(R.color.transparent);
			webView.addJavascriptInterface(new JavascriptInterface(getApplicationContext()),"imagelistner");
			webView.setWebViewClient(new MyWebViewClient());
			new MyAsnycTask().execute(news_url, news_title, news_date);
		}
	}

	private void initView() {
		btnCollect = (ImageButton)addButton(R.drawable.colllect, new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Oncollect();
			}
		}).findViewById(R.id.btn_image);
		
		addButton(R.drawable.intro_to_gay, new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AndroidShare as = new AndroidShare(YUNewsDetail.this,"#"+news.getTitle()+"#"+ Api.getUrlById(news.getId()),"");
				as.show();
			}
		});
		setTitle(news_title);
		//初始化收藏
		initcollect();
	}

	private class MyAsnycTask extends AsyncTask<String, String,String>{

		@Override
		protected String doInBackground(String... urls) {
			String data=getNewsDetails(urls[0],urls[1],urls[2]);
			return data;
		}

		@Override
		protected void onPostExecute(String data) {
			findViewById(R.id.detail_loading).setVisibility(View.GONE);
			webView.loadDataWithBaseURL (null, data, "text/html", "utf-8",null);
		}
	}
	
	private String getNewsDetails(String url, String news_title,String news_date) {
		String style = "<style type=\"text/css\">"+"img{width: 100% !important;height: auto; }"+"</style>";
		String data = style+"<body style='background:ffffff;'>" +
				"<h2 style='font-size:18px;'>" + news_title + "</h2>";
		data = data + "<p align='left' style='margin-left:10px'>" 
				+ "<span style='font-size:10px;'>" 
				+ news_date
				+ "</span>" 
				+ "</p>";
		//data = data + "<hr size='1' />";
		try {
			String str = HttpUtil.getRequest(url, null);
			JSONObject jObject = new JSONObject(str);
			
			
			String news_body = jObject.getString("body");
			System.out.println("长大新闻："+news_body);
	    	//去掉width属性
			news_body = news_body.replaceAll(
						"(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
			//去掉height属性
			news_body=news_body.replaceAll(
						"(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
			
			System.out.println("长大新闻去掉宽高后："+news_body);
			data = data +jObject.getString("body")+"</body>";
			//data = data + ;
		} catch (Exception e) {
			Log.d("INFORMATION",e.toString());
		}
		return data;
	}
	
	private void Oncollect(){
		DBUtil dbu = DBUtil.getInstance(YUNewsDetail.this);
		if(!IsCollected){
			try {
				ContentValues values = new ContentValues();
				values.put("news_id",YUNewsDetail.this.news.getId());
				values.put("news_title",YUNewsDetail.this.news_title);
				values.put("news_time",YUNewsDetail.this.news.getPublishTime());
				values.put("news_column","长大新闻");
				values.put("news_image",YUNewsDetail.this.news.getImageRight());
				long insert_result=dbu.insertData(SQLHelper.TABLE_COLLECT, values);
				if(insert_result!=-1)
			       {
					   IsCollected=true;
					   T.show(YUNewsDetail.this, "收藏成功", Toast.LENGTH_SHORT);
					   btnCollect.setImageResource(R.drawable.colllected);
			       }
				else T.show(YUNewsDetail.this, "收藏失败", Toast.LENGTH_SHORT);
				System.out.println("插入结果："+insert_result);
			} catch (SQLException e) {
				T.show(YUNewsDetail.this, "收藏失败，请测试或反馈信息", Toast.LENGTH_SHORT);
				e.printStackTrace();
			}
		}
		else {
			 T.show(YUNewsDetail.this, "取消收藏", Toast.LENGTH_SHORT);
			 dbu.execSQL("delete from collect_tb where news_id="+news.getId());
			  IsCollected=false;
			  btnCollect.setImageResource(R.drawable.colllect);
		}
	
}

	private void initcollect(){
	/*db = openOrCreateDatabase("news_db.db", Context.MODE_PRIVATE, null);
	db.execSQL("");*/
	DBUtil dbu = DBUtil.getInstance(YUNewsDetail.this);
	int id=YUNewsDetail.this.news.getId();
	result=dbu.rawQuery( "SELECT news_id FROM collect_tb where news_id="+id);
	result.moveToFirst();
	if(result != null && result.getCount() >= 1)
    	{
		  IsCollected=true;
		  btnCollect.setImageResource(R.drawable.colllected);
    	}
	else
		{
		   IsCollected=false;
		   btnCollect.setImageResource(R.drawable.colllect);
		}
     result.close(); 
}
	
	private void addImageClickListner() {
		webView.loadUrl("javascript:(function(){"
				+ "var objs = document.getElementsByTagName(\"img\");"
				+ "var imgurl=''; " + "for(var i=0;i<objs.length;i++)  " + "{"
				+ "imgurl+=objs[i].src+',';"
				+ "    objs[i].onclick=function()  " + "    {  "
				+ "        window.imagelistner.openImage(imgurl);  "
				+ "    }  " + "}" + "})()");
	}
	
	public class JavascriptInterface {

		private Context context;

		public JavascriptInterface(Context context) {
			this.context = context;
		}

		public void openImage(String img) {
			Intent intent = new Intent();
			intent.putExtra("imgsrc", img); 
			intent.setClass(context,  ImgPreview.class);
			context.startActivity(intent);
		}
	}
	
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			//view.getSettings().setJavaScriptEnabled(true);
			super.onPageFinished(view, url);
			addImageClickListner();
			webView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			//view.getSettings().setJavaScriptEnabled(true);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

}
