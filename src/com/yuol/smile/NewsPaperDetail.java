package com.yuol.smile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class NewsPaperDetail extends Activity {
	private Intent intent=null;
    private WebView news_content=null;
    private String str_content="";
    private Myhandler myhandler;
    private TextView title=null;
    private TextView time=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newspaper_detail_main);
	    intent=getIntent();
	    myhandler=new Myhandler();
	    initview();
	    new mythread().start();
	}
	private void initview(){
		title=(TextView)super.findViewById(R.id.newspaper_title);
		title.setText(intent.getStringExtra("title"));
		time=(TextView)super.findViewById(R.id.newspaper_time);
		time.setText(intent.getStringExtra("time"));
		news_content=(WebView)super.findViewById(R.id.newspaper_detail_body);
	}
	private class Myhandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			str_content = "<html><body>"+str_content+"</body></html>";
			news_content.loadDataWithBaseURL(null,str_content, "text/html","utf-8", null);
			System.out.println("电子校报:"+str_content);
			findViewById(R.id.detail_loading).setVisibility(View.GONE);
		}
		
	}
	private class mythread extends Thread{

		@Override
		public void run() {
			try{
				Document doc=Jsoup.connect(intent.getStringExtra("link")).get();
				Element content=doc.select(".neirong").get(0);
				Elements imgs=content.select("img");//鎶婄浉瀵瑰湴鍧?浆鎹负缁濆鍦板潃
				for (Element img : imgs) {
					String str=img.absUrl("src");
					img.attr("src",str);
				}
				str_content=content.toString();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			myhandler.sendEmptyMessage(0);
		
		}	
	}
	public void onClick(View v){
		finish();
	}
}
