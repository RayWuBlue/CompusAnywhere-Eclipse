package com.yuol.smile.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuol.smile.R;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.AndroidShare;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.DownloadManageUtil;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.MyProgressBar;

public class WebContent extends BaseActivity {

	private WebSettings webSettings = null;
	private WebView web = null;
	private MyProgressBar mpb;
	@ViewInject(R.id.part_sorry_tip_layout)
	private RelativeLayout clickReLoad;
	public Context mcontext;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_web_content);
		setTitle(getIntent().getStringExtra("title"));
		ViewUtils.inject(this);
		mcontext = this;
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
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(false);
		webSettings.setLoadWithOverviewMode(true);
		web.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		web.loadUrl(getIntent().getStringExtra("url"));
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
			mpb = new MyProgressBar(WebContent.this);
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
			DownloadManageUtil.DownloadFile(mcontext, url,"/yuol/DownLoad");
		}
	}

	public void onClick(View v){
		switch (v.getId()) {
		case R.id.intro:
			AndroidShare as = new AndroidShare(WebContent.this,"#"+getIntent().getStringExtra("title")+"#"+getIntent().getStringExtra("url"),"");
			as.show();
			break;
		default:
			break;
		}
	}
	
}
