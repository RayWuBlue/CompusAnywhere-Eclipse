package com.yuol.smile;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.utils.T;

public class LibraryActivity extends BaseActivity {
	private EditText user;
	private EditText pwd;
	private EditText id;
    private Button bt_login;
	private SharedPreferences sharedPreferences;
	private String str_CardNo;
	private String str_RdRecno;
	private String str_RegName;
	private String str_w_Recno;
	private String str_pwd;
	private boolean hasCache=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.libaray_main);
		initview();
		hasCache=getCache();
	}
	private void initview(){
		setTitle("登录图书馆");
		setTitleBarColor(getResources().getColor(R.color.title_bar_blue));
		user=(EditText)super.findViewById(R.id.lib_login_user);
		id=(EditText)super.findViewById(R.id.lib_login_id);
		pwd=(EditText)super.findViewById(R.id.lib_login_pwd);
		bt_login=(Button)super.findViewById(R.id.lib_login_bt);
		bt_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(hasCache&&pwd.getText().toString().equals(str_pwd)){
					Intent it=new Intent(LibraryActivity.this,LibLoadInfo.class);
					it.putExtra("str_CardNo", str_CardNo);
					it.putExtra("str_RdRecno", str_RdRecno);
					it.putExtra("str_RegName", str_RegName);
					it.putExtra("str_w_Recno", str_w_Recno);
					startActivity(it);
				}
				else{
					String user_str=user.getText().toString();
					String id_str=id.getText().toString();
					String pwd_str=pwd.getText().toString();
					if(pwd_str.equals("")||(user_str.equals("")&&id_str.equals("")))
						T.showShort(LibraryActivity.this,"登录失败");
					else
					   login(user_str,id_str,pwd_str);
				}
			}
		});
		
	}

	public void login(String user_str,String id_str,String pwd_str){
		
		
		try {
			AsyncTask_LibLogin asynctask=new AsyncTask_LibLogin();
			asynctask.execute(user_str,id_str,pwd_str);
			String res=asynctask.get();
		    
			Matcher mat_CardNo= Pattern.compile("CardNo=\\w+?;").matcher(res);
			Matcher mat_RdRecno=Pattern.compile("RdRecno=\\w+?;").matcher(res);
			Matcher mat_RegName=Pattern.compile("RegName=\\w+?;").matcher(res);
			Matcher mat_w_Recno=Pattern.compile("w_Recno=\\w+?;").matcher(res);
			if(mat_CardNo.find()){
				str_CardNo=mat_CardNo.group().substring(7,mat_CardNo.group().length()-1);
				if(mat_RdRecno.find())
				    str_RdRecno=mat_RdRecno.group().substring(8, mat_RdRecno.group().length()-1);
				if(mat_RegName.find())
				   str_RegName=mat_RegName.group().substring(8, mat_RegName.group().length()-1);
				if(mat_w_Recno.find())
				   str_w_Recno=mat_w_Recno.group().substring(8, mat_w_Recno.group().length()-1);
				System.out.println(str_CardNo+str_RdRecno+str_RegName+str_w_Recno);
				Intent it=new Intent(this,LibLoadInfo.class);
				it.putExtra("str_CardNo", str_CardNo);
				it.putExtra("str_RdRecno", str_RdRecno);
				it.putExtra("str_RegName", str_RegName);
				it.putExtra("str_w_Recno", str_w_Recno);
				startActivity(it);
				saveCache(str_CardNo,str_RdRecno,str_RegName,str_w_Recno);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	
	}
	public void saveCache(String CardNo ,String RdRecno,String RegName,String w_Recno){
		Editor editor=sharedPreferences.edit();
		editor.putString("lib_CardNo", CardNo);
		editor.putString("lib_RdRecno", RdRecno);
		editor.putString("lib_RegName", RegName);
		editor.putString("lib_w_Recno", w_Recno);
		editor.putString("lib_pwd", pwd.getText().toString());
		editor.commit();
	}
	public boolean getCache(){
		sharedPreferences=this.getSharedPreferences("sharedPreferences",MODE_PRIVATE);
		str_CardNo=sharedPreferences.getString("lib_CardNo","");
		if(str_CardNo.equals(""))
			return false;
		else{
			str_RdRecno=sharedPreferences.getString("lib_RdRecno","");
			str_RegName=sharedPreferences.getString("lib_RegName","");
			str_w_Recno=sharedPreferences.getString("lib_w_Recno","");
		    str_pwd=sharedPreferences.getString("lib_pwd","");
			user.setText(str_RegName);
			id.setText(str_CardNo);
			pwd.setText(str_pwd);
			return true;
		}
	}

	class AsyncTask_LibLogin extends AsyncTask<String, Integer, String> {
	    private String login_url="http://online.yangtzeu.edu.cn/app/app_data.php";
	    private HttpPost post=null;
	    private HttpResponse response=null;
	    private ArrayList<NameValuePair> list=new ArrayList<NameValuePair>();
	    private String login_result="";
	    
		@Override
		protected String doInBackground(String... params) {
			System.out.println("提交表单为："+params[0]+params[1]+params[2]);
			list.add(new BasicNameValuePair("act","lib"));
			list.add(new BasicNameValuePair("m","log"));
			list.add(new BasicNameValuePair("v_newuser","0"));
			list.add(new BasicNameValuePair("v_regname",params[0]));
			list.add(new BasicNameValuePair("v_cardno",params[1]));
			list.add(new BasicNameValuePair("v_passwd",params[2]));
			post=new HttpPost(login_url);
			try {
				post.setEntity(new UrlEncodedFormEntity(list,HTTP.UTF_8));
				response=new DefaultHttpClient().execute(post);
				if(response.getStatusLine().getStatusCode()==200){
					login_result=EntityUtils.toString(response.getEntity());				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return login_result;
		}
	}
}
