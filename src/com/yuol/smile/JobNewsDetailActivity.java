package com.yuol.smile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class JobNewsDetailActivity extends Activity {
	private Intent intent=null;
    //private TextView news_cloumn=null;
    private WebView news_content=null;
    private String str_content="";
    private Myhandler myhandler;
    private ProgressBar detail_loading=null;
    private TextView title=null;
    private TextView time=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.job_news_detail_mian);
		detail_loading=(ProgressBar)super.findViewById(R.id.detail_loading);
	    intent=getIntent();
	    myhandler=new Myhandler();
	    new mythread().start();
		initview();
	}
	private void initview(){
		//news_cloumn.setText(intent.getStringExtra("cloumn"));
		title=(TextView)super.findViewById(R.id.job_news_title);
		title.setText(intent.getStringExtra("title"));
		time=(TextView)super.findViewById(R.id.job_news_time);
		time.setText(intent.getStringExtra("time"));
		news_content=(WebView)super.findViewById(R.id.job_news_detail_body);
	}
	private class Myhandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			news_content.loadDataWithBaseURL(null,str_content, "text/html","utf-8", null);
			detail_loading.setVisibility(View.GONE);
		}
		
	}
	public void onClick(View v){
		finish();
	}
	private class mythread extends Thread{

		@Override
		public void run() {
			try{
				Document doc=Jsoup.connect(intent.getStringExtra("link")).get();
				Element content=doc.getElementById("Zoom");
				 str_content=content.html();
				 //Element time=content.lastElementSibling();
			}
			catch(Exception e){
				//Tools.sendErrorInfo(e.getMessage(), JobNewsDetailActivity.this);
			}
			JobNewsDetailActivity.this.myhandler.sendEmptyMessage(0);
		
		}
		
	}
}
