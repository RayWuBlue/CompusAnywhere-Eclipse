package com.yuol.smile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.yuol.smile.adapter.BookListAdapter;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.bean.LibBook;

public class LibBookList extends BaseActivity {
	private int pagesize;//每页的条�?
	private int totalPage_num;//总页
	public List<LibBook> list_tem = null;;
	private Button bt_next;//下一�?
	private ListView book_lv;
	private BookListAdapter adapter;
    private Myhandler myhandler;
    public Intent intent=null;
    private String html;
    private String html_d;//信息内容
    private int row;//行数
    private String url;
    private List<LibBook> list;
    private TableLayout tablelayout;
    private String c_html="";//当前内容
    private String v_index;
    private String v_value;
    private String v_pagenum;
    private String v_count;
    private String FLD_DAT_BEG;
    private String FLD_DAT_END;
    private String v_LogicSrch;
    private String v_LogicKeyLen;
    private String v_seldatabase;
    private String v_curKey;
    private String v_addr;
    private String v_curdbno;
    private int v_curscr=1;
    private Document doc;
    private Elements tds=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存�?
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lib_book_list_main);
		initview();
		initlv();
		new Mythread(0).start();
	}
	private void initview(){
		setTitle("查询结果");
		setTitleBarColor(getResources().getColor(R.color.title_bar_blue));
		book_lv=(ListView)super.findViewById(R.id.book_list);
		View footView=getLayoutInflater().inflate(R.layout.lib_book_list_footer, null);
		book_lv.addFooterView(footView);
		bt_next=(Button)footView.findViewById(R.id.book_list_next);
		bt_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bt_next.setEnabled(false);
				bt_next.setText("正在加载更多...");
			    new Mythread(2).start();
			}
	});
	}
	private void initlv(){
		myhandler=new Myhandler();
		intent=getIntent();
		v_index=intent.getStringExtra("v_index");
		v_value=intent.getStringExtra("v_value");
		FLD_DAT_BEG=intent.getStringExtra("FLD_DAT_BEG");
		FLD_DAT_END=intent.getStringExtra("FLD_DAT_END");
		v_pagenum=intent.getStringExtra("v_pagenum");
		if(v_pagenum==null||v_pagenum.equals("null")||v_pagenum.equals(""))
			v_pagenum="10";//如果v_pagenum出现异常�?
		v_seldatabase=intent.getStringExtra("v_seldatabase");
		v_LogicSrch=intent.getStringExtra("v_LogicSrch");
		html=intent.getStringExtra("html");
		list=new ArrayList<LibBook>();
		list_tem=new ArrayList<LibBook>();
		adapter=new BookListAdapter(LibBookList.this, list);
		book_lv.setAdapter(adapter);
		book_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					url="http://10.203.1.110/cgi-bin/"+list.get(position).getUrl();
					new Mythread(1).start();

		  }
		});
	}
	private class Myhandler extends Handler{
		private int j;
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				if(row<pagesize){
					bt_next.setText("没有更多");
					bt_next.setEnabled(false);
				}else{
					bt_next.setText("下一页");
					bt_next.setEnabled(true);
				}
				adapter.setData(list);
				adapter.notifyDataSetChanged();
				row=0;
				break;
			case 1:
				Intent it=new Intent(LibBookList.this,LibBookDetail.class);
				it.putExtra("html",html_d);
				startActivity(it);
				break;
			case 2://把获取到的新html提取成list
				new Mythread(3).start();
				break;
			case 4://向后翻页操作的刷�?
				v_curscr++;
				//adapter=new BookListAdapter(CopyOfLibBookList.this, list);
				adapter.setData(list);
				adapter.notifyDataSetChanged();
				System.out.println("adapter大小"+adapter.getCount());
				if(row<pagesize){
					bt_next.setText("没有更多");
					bt_next.setEnabled(false);
				}else{
					bt_next.setText("下一页");
					bt_next.setEnabled(true);
				}
				row=0;
				break;
			case 5://向下翻页失败异常处理
				AlertDialog.Builder builder=new Builder(LibBookList.this);
				builder.setTitle("操作失败");
				builder.setMessage("请确保你的网络可用并重试?");
				builder.setPositiveButton("重试",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Mythread(2).start();//重试
					}
				});
				builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				builder.setCancelable(false);
				builder.show();
				break;
			case 6://
				Toast.makeText(LibBookList.this,"没有更多",Toast.LENGTH_SHORT).show();
				bt_next.setEnabled(false);
				break;
			}
			setTitle("第"+v_curscr+"页");
		}
	}
	public class Mythread extends Thread{
		private int statu;
		public Mythread(int statu){
			this.statu=statu;
		}
		@Override
		public void run() {
			switch(statu){
			case 0://第一次把html提取list
			    doc=Jsoup.parse(html);
				pagesize=Integer.valueOf(intent.getStringExtra("v_pagenum"));
				v_pagenum=String.valueOf(pagesize);
				tds=doc.select("td[bgcolor=#FFFFFF]");
				row=tds.size()/7;
				//list_tem.clear();
				for(int i=0;i<row;i++){
					LibBook book=new LibBook(tds.get(7*i+0).text(),tds.get(7*i+1).text(),tds.get(7*i+2).text(),tds.get(7*i+3).text(),tds.get(7*i+4).text(),tds.get(7*i+5).text(),tds.get(7*i+6).select("a").attr("href"));
					list_tem.add(book);
				}
				list.clear();
				list.addAll(list_tem);
				myhandler.sendEmptyMessage(0);
				break;
			case 1:
				html_d=querydetail(url);
				myhandler.sendEmptyMessage(1);
				break;
			case 2:
				ChangPage();//获取新的html
				myhandler.sendEmptyMessage(2);
				break;
			case 3://新的html提取新list
				int k;
				for(k=0;k<3;k++){
					try{
						//list.clear();//清除旧listint 
						doc=Jsoup.parse(html);
						System.out.println(doc);
						tds=doc.select("td[bgcolor=#FFFFFF]");
						row=tds.size()/7;
						//list_tem.clear();
						for(int i=0;i<row;i++){
							LibBook book=new LibBook(tds.get(7*i+0).text(),tds.get(7*i+1).text(),tds.get(7*i+2).text(),tds.get(7*i+3).text(),tds.get(7*i+4).text(),tds.get(7*i+5).text(),tds.get(7*i+6).select("a").attr("href"));
							list_tem.add(book);
						}
						list.clear();
						list.addAll(list_tem);
						}catch(Exception e){
							e.printStackTrace();
						}
					if(row>0){
						myhandler.sendEmptyMessage(4);
						break;
					}
				}
				if(k==3){
					myhandler.sendEmptyMessage(6);
					System.out.println("三次解析html失败");
				}
				break;
			}
		}
	}
	private String querydetail(String url){
		String html="";
		ArrayList<NameValuePair> list=new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("act","lib"));
		list.add(new BasicNameValuePair("m","detail"));
		list.add(new BasicNameValuePair("url",url.trim()));
		HttpPost post=new HttpPost("http://online.yangtzeu.edu.cn/app/app_data.php");
		try {
			post.setEntity(new UrlEncodedFormEntity(list,"utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try{
			HttpResponse response=new DefaultHttpClient().execute(post);
			if(response.getStatusLine().getStatusCode()==200){
				System.out.println("连接成功");
				html=EntityUtils.toString(response.getEntity());
			 }
			else System.out.println(response.getStatusLine().getStatusCode());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return html;
	}
	private boolean ChangPage(){
		try {
	   doc=Jsoup.parse(html);
	   v_count=doc.select("[NAME=v_count]").attr("value");
	   v_LogicKeyLen=doc.select("[NAME=v_LogicKeyLen]").attr("value");
	   v_curKey=doc.select("[NAME=v_curKey]").attr("value");
	   try {
		v_addr=doc.select("[NAME=v_addr]").attr("value");
	} catch (Exception e) {
		// TODO 自动生成�?catch �?
		System.out.println("addr异常");
		e.printStackTrace();
	}
	   v_curdbno=doc.select("[NAME=v_curdbno]").attr("value");
		ArrayList<NameValuePair> list_pair=new ArrayList<NameValuePair>();
		String str_post="v_index="+v_index+"&v_value="+URLEncoder.encode(v_value,"gbk")+"&v_pagenum="+v_pagenum+"&v_count="+v_count+"&FLD_DAT_BEG="+FLD_DAT_BEG+"&FLD_DAT_END="+FLD_DAT_END+
				"&v_LogicSrch="+v_LogicSrch+"&v_LogicKeyLen="+v_LogicKeyLen+"&v_seldatabase="+v_seldatabase+"&v_curKey="+URLEncoder.encode(v_curKey,"gbk")+"&v_addr="+v_addr+"&v_curdbno="+v_curdbno+"&v_curscr="+String.valueOf(v_curscr);
		System.out.println(str_post);
		list_pair.add(new BasicNameValuePair("act","lib"));
		list_pair.add(new BasicNameValuePair("m","sub_post"));
		list_pair.add(new BasicNameValuePair("post",str_post));
		int k;
		for(k=0;k<3;k++){
			HttpPost post=new HttpPost("http://online.yangtzeu.edu.cn/app/app_data.php");
			post.setEntity(new UrlEncodedFormEntity(list_pair,"utf-8"));
			HttpResponse response=new DefaultHttpClient().execute(post);
			if(response.getStatusLine().getStatusCode()==200)
			   {
				  html=EntityUtils.toString(response.getEntity());
				  System.out.println(html);
				  if(!html.equals("")){
					  return false;
					 
				  }
					 
			   }
		  }
		if(k==3){
			myhandler.sendEmptyMessage(5);
			return false;
		}else
       return true;
			
		}
		catch(Exception e){
			e.printStackTrace();
			  return false;
		}
	}

}
