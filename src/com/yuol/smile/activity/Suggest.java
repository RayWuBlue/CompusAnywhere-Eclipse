package com.yuol.smile.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuol.smile.R;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.MyProgressBar;

public class Suggest extends BaseActivity {
	
	private EditText content;
	private LoginHelper lh;
	private MyProgressBar mpb;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.act_suggest);
		setTitle("反馈信息");
		addButton("发送", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String SuggestVal=content.getText().toString();
				if(SuggestVal.length()<=0){
					T.showShort(Suggest.this,"请输入你要反馈的内容");
				}else if(SuggestVal.length()>256){
					T.showShort(Suggest.this,"反馈内容限制在256字符内");
				}else{
					if(NetHelper.isNetConnected(Suggest.this)){
						mpb=new MyProgressBar(Suggest.this);
						mpb.setMessage("正在上传反馈信息...");
						new AsyncUpload().execute(SuggestVal);
					}else{
						T.showShort(Suggest.this,R.string.net_error_tip);
					}
				}
				
			}
		});
		content=(EditText) super.findViewById(R.id.suggest_content);
		lh=new LoginHelper(this);
	}

	
	public class AsyncUpload extends AsyncTask<String,Void,String>{

		@Override
		protected String doInBackground(String... str) {
			List<NameValuePair> params=new ArrayList<NameValuePair>(); 
			params.add(new BasicNameValuePair("uid",lh.getUid()));
			params.add(new BasicNameValuePair("token",lh.getToken()));
			params.add(new BasicNameValuePair("content",str[0]));
			return GetUtil.sendPost(Api.FEEDBACK, params);
		}

		protected void onPostExecute(String result) {
			mpb.dismiss();
			T.show(getApplicationContext(), result, Toast.LENGTH_LONG);
			JSONObject jsonResult = JSON.parseObject(result);
			System.out.println("SUggest:"+jsonResult.toString());
			if(jsonResult.getInteger("retCode")==1){
				T.showLong(Suggest.this,"感谢您的反馈,您的支持是我们前进的最大动力!");
				finish();
			}else{
				T.showLong(Suggest.this,"反馈失败,请重新尝试");
			}
			super.onPostExecute(result);
		}
		
		
		
	}

}
