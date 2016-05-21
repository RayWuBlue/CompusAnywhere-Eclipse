package com.yuol.smile.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.yuol.smile.R;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.SendPostWithSession;
import com.yuol.smile.utils.T;

public class ModifyValue extends Activity {
	
	private EditText et;
	private LoginHelper lh;
	private Button btn_confirm;
	private TextView title;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.act_modify_value);
		lh=new LoginHelper(this);
		et=(EditText) super.findViewById(R.id.update_username_edit);
		title=(TextView) super.findViewById(R.id.modify_title);
		title.setText(getIntent().getStringExtra("name"));
		btn_confirm = (Button)findViewById(R.id.modify_comfirm);
		btn_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String val=et.getText().toString().trim();
				if(null==val||val.length()==0){
					T.showShort(ModifyValue.this, "不能为空");	
				}else{
					if(!NetHelper.isNetConnected(ModifyValue.this)){
						T.showShort(ModifyValue.this,R.string.net_error_tip);
					}else if(val.equals(lh.getNickname())){
						finish();
					}else{
						v.setEnabled(false);
						new AsyncUpdate().execute(v);
					}
				}
			}
		});
		et.setText(getIntent().getStringExtra("value"));
	}
	public class AsyncUpdate extends AsyncTask<View,Void,String>{

		private View v;
		protected String doInBackground(View... v) {
			this.v=v[0];
			String name = getIntent().getStringExtra("name");
			if ("昵名".equals(name)){
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("uid",lh.getUid()));
				params.add(new BasicNameValuePair("token",lh.getToken()));
				params.add(new BasicNameValuePair("newnick",et.getText().toString().trim()));
				String url =Api.UserConfig.modifynickname();
				lh.setNickname(et.getText().toString().trim());
				return new SendPostWithSession().executeRequest(url,params);
				}
			else if ("QQ".equals(name)){
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("uid",lh.getUid()));
				params.add(new BasicNameValuePair("token",lh.getToken()));
				params.add(new BasicNameValuePair("newqq",et.getText().toString().trim()));
				String url =Api.UserConfig.modifyqq();
				lh.setQQ(et.getText().toString().trim());
				return new SendPostWithSession().executeRequest(url,params);
			}
			else
				return "";
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			System.out.println("修改用户信息："+result);
			//JSONObject jObj = JSONObject.parseObject(result);
			//if(jObj!=null&&jObj.getInteger("retCode")==1){
				T.showShort(ModifyValue.this, "修改成功");
				String name = getIntent().getStringExtra("name");
				Intent  it = new Intent(ModifyValue.this,UserInfo.class);
				Bundle bd = new Bundle();
				bd.putString("value",et.getText().toString().trim());
				if ("昵名".equals(name))
					bd.putString("name","昵名");
				else if ("QQ".equals(name))
					bd.putString("name","QQ");
				it.putExtras(bd);
				setResult(RESULT_OK, it);
				//startActivity(it);
				finish();
			//}else{
			//	v.setEnabled(true);
			//	T.showShort(ModifyValue.this, "修改失败,请重新尝试");
			//}
		}
	}
}
