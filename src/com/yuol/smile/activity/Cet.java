package com.yuol.smile.activity;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yuol.smile.R;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.utils.EncodeUtil;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.widgets.MyAlertDialog;
import com.yuol.smile.widgets.MyPopMenu;
import com.yuol.smile.widgets.MyProgressBar;
import com.yuol.smile.widgets.MyAlertDialog.MyDialogInt;
import com.yuol.smile.widgets.MyPopMenu.MyPopMenuImp;

public class Cet extends BaseActivity {
	
	private EditText name=null;
	private EditText zkz=null;
	private Button btn=null;
	private MyProgressBar mpb;
    private MyPopMenu popmenu;
    Map<String,String> ck;
    private int type = 0;
	private MyAlertDialog mad;
	private String phoneNumber = "1066335577";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_cet);
		setTitle("学信网(默认)");
		addButton("查询方式", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popmenu=new MyPopMenu(Cet.this);
				popmenu.addItems(new String[]{"学信网(默认)","99宿舍网查询","短信息查询"});
				popmenu.showAsDropDown(v);
				popmenu.setOnItemClickListener(new MyPopMenuImp() {
					@Override
					public void onItemClick(int index) {
						switch (index) {
						case 0:
							type = 0;
							name.setVisibility(View.VISIBLE);
							setTitle("学信网(默认)");
							break;
						case 1:
							type = 1;
							name.setVisibility(View.VISIBLE);
							setTitle("99宿舍网查询");
							break;
						case 2:
							type = 2;
							name.setVisibility(View.GONE);
							setTitle("短信息查询");
							break;
						default:
							break;
						}
					}});
				
			}
		});
		name=(EditText) super.findViewById(R.id.cet_edit_id);
		zkz=(EditText) super.findViewById(R.id.cet_edit_pwd);
		btn=(Button) super.findViewById(R.id.cet_login_btn);
		btn.setOnClickListener(new onclicklistener());
	}
	private void initAlert(){
		 mad=new MyAlertDialog(this);
		 mad.setTitle("消息提示");
		 mad.setMessage("中国移动、联通、电信手机用户:发送A加15位准考证号（如A110000122100101）到 1066335577\n"
+"资费：全国1元/条，不含通信费。客服电话: 010-68083018\n"
+"特别提示：河北地区的中国移动手机用户发送 8加15位准考证号（如8110000122100101）到 10661660？"); 
		 mad.setLeftButton("确定",new MyDialogInt() {
			@Override
			public void onClick(View view) {
				mad.dismiss();
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNumber));          
	            intent.putExtra("sms_body", "A"+zkz.getText().toString());
	            startActivity(intent);
				return;
			}
		});
		mad.setRightButton("取消",new MyDialogInt() {
			@Override
			public void onClick(View view) {
				mad.dismiss();
			}
		});
	}
	public class onclicklistener implements OnClickListener{
		@Override
		public void onClick(View v) {
			final String u=name.getText().toString();
			final String p=zkz.getText().toString();
			System.out.println("U:"+u+" P:"+p);
			if(type==0){
				mpb=new MyProgressBar(Cet.this);
				mpb.setMessage("正在查询中...");
				new	 Thread(new Runnable() {
					@Override
					public void run() {
						Message msg=new Message();
						msg.what=102;
						try {
							msg.obj = Jsoup.connect("http://www.chsi.com.cn/cet/query")
									.data("zkzh", p).data("xm", u) // 请求参数
									.userAgent("I ’ m jsoup") // 设置 User-Agent
									.cookie("auth", "token")// 设置 cookie
									.timeout(3000) // 设置连接超时时间
									.header("Referer", "http://www.chsi.com.cn/cet/").get();
						} catch (IOException e) {
							e.printStackTrace();
						} // 使用POST方法访问URL
						handler.sendMessage(msg);
					}
				}).start();
				
			}else if(type == 1){
				
				mpb=new MyProgressBar(Cet.this);
				mpb.setMessage("正在查询中...");
				new	 Thread(new Runnable() {
					@Override
					public void run() {
						String url="http://online.yangtzeu.edu.cn/weixin/func//sljcx_api.php?name="+EncodeUtil.ToUtf8(u)+"&id="+p;
						Message msg=new Message();
						msg.what=102;
						//GetUtil.getRes(url);//两次请求才可取得正确的数据
						msg.obj=GetUtil.getRes(url);
						handler.sendMessage(msg);
					}
				}).start();
			}else{
				initAlert();
			}
		}
		
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==102){
				Intent it=new Intent(Cet.this,CetData.class);
				if(type==0){
					it.putExtra("entry",2);
					Document doc = (Document)msg.obj;
					it.putExtra("cet",parseScore(doc));
				}else{
					it.putExtra("entry",1);
					it.putExtra("cet",msg.obj.toString());
				}
				startActivity(it);
			}
			mpb.dismiss();
			super.handleMessage(msg);
		}
	};

	private String parseScore(Document doc) {
		System.out.println("CET DOC:"+doc.toString());
		String name = null,school = null,type = null,id = null,time = null;
		Element content = doc.getElementById("c");
		Element element = null;
		if(content!=null)
			element = content.getElementsByClass("error").first();

		if ((element != null)) {
				Toast.makeText(Cet.this,"查询出错",Toast.LENGTH_SHORT).show();
		} else {

			Element cetTable = doc.getElementsByClass("cetTable").first();
			Elements elements = cetTable.getElementsByTag("tr");
			for (Element elementtwo : elements) {

				if (elementtwo.getElementsByTag("th").text().equals(getResources().getString(R.string.name_3))) {

					name = elementtwo.getElementsByTag("td").text();
				} else if (elementtwo.getElementsByTag("th").text()
						.equals(getResources().getString(R.string.school))) {

					school = elementtwo.getElementsByTag("td").text();
				} else if (elementtwo.getElementsByTag("th").text()
						.equals(getResources().getString(R.string.type))) {

					type = elementtwo.getElementsByTag("td").text();
				} else if (elementtwo.getElementsByTag("th").text()
						.equals(getResources().getString(R.string.id_2))) {

					id = elementtwo.getElementsByTag("td").text();
				} else if (elementtwo.getElementsByTag("th").text().equals("考试时间：")) {

					time = elementtwo.getElementsByTag("td").text();
					System.out.println("考试时间:"+time);
					
				} else if (elementtwo.getElementsByTag("th").text()
						.equals(getResources().getString(R.string.sum_2))) {

					String result = name+","+school+","+type+","+id+","+time+","
							+elementtwo.getElementsByTag("td").text().replace("听力：", ",").replace("阅读：", ",").replace("写作与翻译：", ",");
					return result;
				}
			}

		}
		return null;
	}

}
