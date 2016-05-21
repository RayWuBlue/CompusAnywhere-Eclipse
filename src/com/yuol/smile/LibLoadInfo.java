package com.yuol.smile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yuol.smile.adapter.LibLoadAdapter;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.bean.LibLoanBook;
public class LibLoadInfo  extends BaseActivity{
	public static String v_barno;//书籍条码
	private final static String url="http://online.yangtzeu.edu.cn/app/app_data.php";
	private String html;
	private Document doc;
	private MyHandler myhandler;
	private TextView userNo;
	private TextView userName;
	private TextView userType;
	private TextView userStatu;
	private TextView userEffect;
	private TextView comLoad;
	private TextView speLoad;
	private TextView subLoad;
	private TextView aheLoad;
	private TextView colLoad;
	private TextView interLoad;
	private ListView lv;
	private List<LibLoanBook> list;
	private LibLoadAdapter adapter;
	private String CardNo;
	private String RdRecno;
	private String RegName;
	private String w_Recno;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lib_load_main);
		init();
		new Thread(new MyThread(0)).start();
	}
	private void init(){	
		Intent it=getIntent();
		setTitle("图书借阅");
		setTitleBarColor(getResources().getColor(R.color.title_bar_blue));
		CardNo=it.getStringExtra("str_CardNo");
		RdRecno=it.getStringExtra("str_RdRecno");
		RegName=it.getStringExtra("str_RegName");
		w_Recno=it.getStringExtra("str_w_Recno");
		userNo=(TextView)super.findViewById(R.id.lib_user_no);
		userName=(TextView)super.findViewById(R.id.lib_user_name);
		userType=(TextView)super.findViewById(R.id.lib_user_type);
		userStatu=(TextView)super.findViewById(R.id.lib_user_statu);
		userEffect=(TextView)super.findViewById(R.id.lib_user_effect);
		comLoad=(TextView)super.findViewById(R.id.lib_com_load);
		speLoad=(TextView)super.findViewById(R.id.lib_spe_load);
		subLoad=(TextView)super.findViewById(R.id.lib_sub_num);
		aheLoad=(TextView)super.findViewById(R.id.lib_ahe_load);
		colLoad=(TextView)super.findViewById(R.id.lib_col_load);
		interLoad=(TextView)super.findViewById(R.id.lib_inter_load);
		lv=(ListView)super.findViewById(R.id.lib_borr_lv);
		myhandler=new MyHandler();
		list=new ArrayList<LibLoanBook>();
		adapter=new LibLoadAdapter(list, this,new MyThread(1));
		lv.setAdapter(adapter);
	}
	
	
	private class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				setUserData();
				adapter.notifyDataSetChanged();
				break;
			case 1:
				new Builder(LibLoadInfo.this).setTitle("续借成功").setMessage(msg.getData().getString("msg"))
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					}
				}).show();
				new Thread(new MyThread(0)).start();
				break;
			case 2:
				new Builder(LibLoadInfo.this).setTitle("续借失败").setMessage(msg.getData().getString("msg"))
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					}
				}).show();
				break;
			
			}
			
		}
	}
	public class MyThread implements Runnable{
		private int statu;
		public MyThread(int statu){
			this.statu=statu;
		}
		@Override
		public void run() {
			switch(statu){
			case 0://查询借阅信息
				HttpPost request=new HttpPost(url);
				List<NameValuePair> data=new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("act","lib"));
				data.add(new BasicNameValuePair("m","info_borrow"));
				data.add(new BasicNameValuePair("CardNo",CardNo));
				data.add(new BasicNameValuePair("RdRecno",RdRecno));
				data.add(new BasicNameValuePair("RegName",RegName));
				data.add(new BasicNameValuePair("w_Recno",w_Recno));
				try{
					request.setEntity(new UrlEncodedFormEntity(data,HTTP.UTF_8));
					HttpResponse response=new DefaultHttpClient().execute(request);
					if(response.getStatusLine().getStatusCode()==200){
						html=EntityUtils.toString(response.getEntity());
						System.out.println(html);
						setBookData();
						myhandler.sendEmptyMessage(0);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			case 1://续
				HttpPost post=new HttpPost(url);
				List<NameValuePair> data_reload=new ArrayList<NameValuePair>();
				data_reload.add(new BasicNameValuePair("act","lib"));
				data_reload.add(new BasicNameValuePair("m","reloan"));
				data_reload.add(new BasicNameValuePair("CardNo",CardNo));
				data_reload.add(new BasicNameValuePair("RdRecno",RdRecno));
				data_reload.add(new BasicNameValuePair("RegName",RegName));
				data_reload.add(new BasicNameValuePair("w_Recno",w_Recno));
				data_reload.add(new BasicNameValuePair("v_barno",v_barno));
				try{
					post.setEntity(new UrlEncodedFormEntity(data_reload,HTTP.UTF_8));
					HttpResponse response=new DefaultHttpClient().execute(post);
					if(response.getStatusLine().getStatusCode()==200){
						html=EntityUtils.toString(response.getEntity());
						Message msg=new Message();
						msg.what=2;
						Bundle bundle=new Bundle();
						if(Pattern.compile("续借处理已完成").matcher(html).find()){
							msg.what=1;
							bundle.putString("msg", "续借处理已完成，正在更新结果。");
						}
						else if(Pattern.compile("您有过期未还文献").matcher(html).find()){
							bundle.putString("msg", "有过期文献，不能续借！");
						}
						else if(Pattern.compile("已超过规定续借次数").matcher(html).find()){
							bundle.putString("msg", "已超过规定续借次数，不能续借！");
						}
						else{
							bundle.putString("msg", "无法续借该书籍。");
						}
						msg.setData(bundle);
						 myhandler.sendMessage(msg);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			}
			
			
		
		}
		
	}
	public void setUserData(){
		try{
			Elements eles_user=doc.select("td[bgcolor=#ffffff]");
			userNo.setText(eles_user.get(0).text());
			userName.setText(eles_user.get(1).text());
			userType.setText(eles_user.get(2).text());
			userStatu.setText(eles_user.get(4).text());
			userEffect.setText(eles_user.get(3).text());
			comLoad.setText(eles_user.get(5).text());
			speLoad.setText(eles_user.get(6).text());
			subLoad.setText(eles_user.get(7).text());
			aheLoad.setText(eles_user.get(8).text());
			colLoad.setText(eles_user.get(9).text());
			interLoad.setText(eles_user.get(10).text());
		}catch(Exception e){
			
		}
		
	}
	private void setBookData(){
		doc=Jsoup.parse(html);
		Elements eles_book=doc.select("td[bgcolor=white]");
		Elements eles_barno=doc.select("input[name=v_barno]");
		int num=eles_book.size()/10;
		list.clear();
		for(int i=0;i<num;i++){
			LibLoanBook book=new LibLoanBook();
			book.setTitle(eles_book.get(i*10).text());
			book.setAuthor(eles_book.get(i*10+1).text());
			book.setAsk_num(eles_book.get(i*10+2).text());
			book.setBar_num(eles_book.get(i*10+3).text());
			book.setAddress(eles_book.get(i*10+4).text());
			book.setType(eles_book.get(i*10+5).text());
			book.setStatu(eles_book.get(i*10+6).text());
			book.setLoad_time(eles_book.get(i*10+7).text());
			book.setReturn_time(eles_book.get(i*10+8).text());
			book.setLoad_num(eles_book.get(i*10+9).text());
			book.setV_barno(eles_barno.get(i).attr("value"));
			System.out.println(eles_barno.get(i).attr("value"));
			list.add(book);
		}
	}

}
