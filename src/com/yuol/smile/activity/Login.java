package com.yuol.smile.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuol.smile.R;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.SendPostWithSession;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.MyProgressBar;

@SuppressLint("HandlerLeak")
public class Login extends BaseActivity implements OnClickListener {

	private EditText uid = null;
	private TextView pwd = null;
	private Button login = null;
	private Button reg = null;
	private MyProgressBar mpb;

	private static final int Verify_State = 0;
	private static final int Login_State = 1;
	//BitmapUtils bm;
	private LoginHelper lh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.act_login);

		setTitle("登录");
		
		uid = (EditText) super.findViewById(R.id.login_edit_uid);
		pwd = (TextView) super.findViewById(R.id.login_edit_pwd);
		login = (Button) super.findViewById(R.id.login_btn_login);
		reg = (Button) super.findViewById(R.id.login_btn_reg);
		login.setOnClickListener(this);
		reg.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (view == login) {
			final String stuid = uid.getText().toString();
			final String password = pwd.getText().toString();
			if (!TextUtils.isEmpty(stuid) && !TextUtils.isEmpty(password)) {

				mpb = new MyProgressBar(Login.this);
				mpb.setMessage("正在登录中...");
				new Thread(new Runnable() {
					@Override
					public void run() {
						Message msg = new Message();
						msg.what = Login_State;
						List<BasicNameValuePair> loginParams = new ArrayList<BasicNameValuePair>();
						loginParams.add(new BasicNameValuePair("username",stuid));
						loginParams.add(new BasicNameValuePair("password",password));
						loginParams.add(new BasicNameValuePair("remember", "on"));
						//msg.obj = HttpUtil.postRequest(Api.User.login(),loginParams);
						msg.obj = new SendPostWithSession().executeRequest(Api.User.login(),loginParams);
						handler.sendMessage(msg);
					}
				}).start();
			} else {
				T.showShort(this, "请填写完整");
				return;
			}
		} else if (view == reg) {
			// 跳转到注册页面
			Intent it = new Intent(Login.this, Register.class);
			startActivity(it);
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Login_State:
				JSONObject ret;
				System.out.println("登录数据：" + msg.obj.toString());
				if (!(msg.obj.toString() == null || msg.obj.toString().equals("")))
					ret = JSON.parseObject(msg.obj.toString());
				else {
					T.showShort(Login.this, "数据返回出错");
					return;
				}
				if (ret.getString("retCode").equals("1")) {
					if (new LoginHelper(Login.this).login(msg.obj.toString(),SendPostWithSession.PHPSESSID)) {
						T.showLong(Login.this, "登陆成功");
						finish();
					}
				} else {
					String error = ret.getString("error");
					if (error != null)
						T.showLong(Login.this, error);
				}
				break;
			case Verify_State:

				break;
			default:
				break;
			}
			mpb.dismiss();
		}

	};


}
