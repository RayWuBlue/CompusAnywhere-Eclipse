package com.yuol.smile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.db.DBUtil;
import com.yuol.smile.db.SQLHelper;
import com.yuol.smile.utils.AndroidShare;
import com.yuol.smile.utils.T;

public class JwcNewsDetail extends BaseActivity {
	private Intent intent=null;
    private WebView news_content=null;
    private String str_content="";
    private Myhandler myhandler;
    public String url;
    private String title_str;
    private String id;
    private int trynum=0;
    private ProgressBar pb;
    
    
    private ImageButton btnCollect;
	 private Cursor result=null;
    private Boolean IsCollected=false;

	
	private void Oncollect(){
		DBUtil dbu = DBUtil.getInstance(JwcNewsDetail.this);
		if(!IsCollected){
			try { 
				ContentValues values = new ContentValues();
				values.put("news_id",JwcNewsDetail.this.id);
				values.put("news_title",JwcNewsDetail.this.title_str);
				values.put("news_time",intent.getStringExtra("time"));
				values.put("news_column","教务处新闻");
				values.put("news_image","");
				long insert_result=dbu.insertData(SQLHelper.TABLE_COLLECT, values);
				if(insert_result!=-1)
			       {
					   IsCollected=true;
					   T.show(JwcNewsDetail.this, "收藏成功", Toast.LENGTH_SHORT);
					   btnCollect.setImageResource(R.drawable.colllected);
			       }
				else T.show(JwcNewsDetail.this, "收藏失败", Toast.LENGTH_SHORT);
				System.out.println("插入结果："+insert_result);
			} catch (SQLException e) {
				T.show(JwcNewsDetail.this, "收藏失败，请测试或反馈信息", Toast.LENGTH_SHORT);
				e.printStackTrace();
			}
		}
		else {
			 T.show(JwcNewsDetail.this, "取消收藏", Toast.LENGTH_SHORT);
			 dbu.execSQL("delete from collect_tb where news_id="+id);
			  IsCollected=false;
			  btnCollect.setImageResource(R.drawable.colllect);
		}
	
}

	private void initcollect(){
	/*db = openOrCreateDatabase("news_db.db", Context.MODE_PRIVATE, null);
	db.execSQL("");*/
	DBUtil dbu = DBUtil.getInstance(JwcNewsDetail.this);
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jwc_news_detail_mian);
	    intent=getIntent();
	    myhandler=new Myhandler();
	    new mythread().start();
		initview();
		initcollect();
	}
	private void initview(){
		//btnCollect = (ImageButton)findViewById(R.id.collect);
		news_content=(WebView)super.findViewById(R.id.jwc_news_detail_body);
		//news_title = (TextView)super.findViewById(R.id.title);
		pb =(ProgressBar) super.findViewById(R.id.detail_loading);
		 WebSettings webSettings = news_content.getSettings();    
		 
		 webSettings.setBuiltInZoomControls(true);
		 
        url=intent.getStringExtra("link");
        id=url.substring(url.lastIndexOf("/")+1,url.length()-5);
        title_str=intent.getStringExtra("title");
        setTitle(title_str);
        //news_title.setText(title_str);
        
        btnCollect = (ImageButton)addButton(R.drawable.colllect, new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Oncollect();
			}
		}).findViewById(R.id.btn_image);
		addButton(R.drawable.intro_to_gay, new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						AndroidShare as = new AndroidShare(JwcNewsDetail.this,"#"+title_str+"#"+url ,"");
						as.show();
					}
				});
	}
	private class Myhandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				news_content.loadDataWithBaseURL(null,str_content, "text/html","utf-8", null);
				pb.setVisibility(View.GONE);
				break;
			case 1:
				if(trynum<3){
					new  mythread().start();
					trynum++;
				}
				break;
			}
			
		}
	}
	private class mythread extends Thread{
		@Override
		public void run() {
			Document doc=null;
			try{
				System.out.println(intent.getStringExtra("link"));
				doc=Jsoup.connect(intent.getStringExtra("link")).timeout(8*1000).get();	
				Element content=doc.getElementById("arc_word");
				content.select("a[href^=admin/]");
/*				if(hrefs!=null&&hrefs.size()!=0){//把内容中的链接相对地换成绝对地址
					for (Element element : hrefs) {
						element.attr("href","http://jwc.yangtzeu.edu.cn:8080/"+element.attr("href"));
					}
				}*/
			str_content=content.html().trim();
			str_content = str_content.replace("href=\"", "href=\"http://jwc.yangtzeu.edu.cn:8080");
			str_content = str_content.replace("src=\"", "src=\"http://jwc.yangtzeu.edu.cn:8080");
			System.out.println("教务处新闻详情："+str_content);
			myhandler.sendEmptyMessage(0);
			}
			catch(Exception e){
				myhandler.sendEmptyMessage(1);
			}
			
		
		}
		
	}
}
