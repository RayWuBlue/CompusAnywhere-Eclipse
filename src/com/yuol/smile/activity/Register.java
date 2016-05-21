package com.yuol.smile.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuol.smile.R;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.HttpUtil;
import com.yuol.smile.utils.PatternUtils;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.MyProgressBar;

public class Register extends BaseActivity {

	private MyProgressBar mpb;

	private EditText email, pwd, pwd2, nickname, stuid,username;
			//edt_register_verify;
	//private ImageView img_register_verify;
	private Button btn1;
	//BitmapUtils bm;

	List<CharSequence> sexSelect = new ArrayList<CharSequence>();

	private static final int REGISTER_INFO = 1;
	private static final int UPLOAD_HRAD_IMAGE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_register);
		setTitle("在线注册");

		this.nickname = (EditText) super.findViewById(R.id.register_nickname);
		this.username = (EditText) super.findViewById(R.id.register_username);

		this.email = (EditText) super.findViewById(R.id.register_email);
		this.pwd = (EditText) super.findViewById(R.id.register_pwd);
		this.pwd2 = (EditText) super.findViewById(R.id.register_pwd2);
		//this.edt_register_verify = (EditText) super
			//	.findViewById(R.id.edt_register_verify);
		this.btn1 = (Button) super.findViewById(R.id.register_btn_reg1);

		this.btn1.setOnClickListener(new onBtnClickListener());

		//this.img_register_verify = (ImageView) findViewById(R.id.img_register_verify);
		/*img_register_verify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initVerifyCode();
			}
		});*/


		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item, this.sexSelect);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		initVerifyCode();
	}

	public void initVerifyCode() {
		//bm.closeCache();
		//bm.display(img_register_verify, Api.User.verify());
		
	//	DisplayImageOptions options = new DisplayImageOptions.Builder()
       // .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
      //  .showImageOnFail(R.drawable.white)              //加载失败时的图片  
      //  .build();

		//ImageLoader imageLoader = ImageLoader.getInstance();
		//imageLoader.displayImage(Api.User.verify(),img_register_verify,options);
	}

	public class onBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			final String emailVal = Editval(email);
			final String pwdVal = Editval(pwd);
			final String pwd2Val = Editval(pwd2);
			final String nicknameVal = Editval(nickname);
		//	final String verifyVal = Editval(edt_register_verify);
			final String usernameVal = Editval(username);
			if (v == btn1) {
				// 注册第一步
				if (emailVal.equals("")) {
					T.showShort(Register.this, "请输入邮箱");
				} else if (!PatternUtils.CheckEmail(emailVal)) {
					T.showShort(Register.this, "邮箱格式有误");
				} else if (emailVal.length() >= 50) {
					T.showShort(Register.this, "邮箱长度限制为50字符内");
				} else if (pwdVal.equals("")) {
					T.showShort(Register.this, "密码不能为空");
				}else if (usernameVal.equals("")) {
					T.showShort(Register.this, "用户名不能为空");
				} else if (nicknameVal.equals("")) {
					T.showShort(Register.this, "昵名不能为空");
				} /*else if (verifyVal.equals("")) {
					T.showShort(Register.this, "验证码不能为空");
				}*/ else if (pwdVal.length() < 8 || pwdVal.length() > 16) {
					T.showShort(Register.this, "密码限制为8-16字符");
				}else if (!PatternUtils.CheckPwd(pwdVal)) {
					T.showShort(Register.this, "密码只能包含字母和数字");
				} else if (!pwdVal.equals(pwd2Val)) {
					T.showShort(Register.this, "2次输入的密码不一致");
				} else {
					mpb = new MyProgressBar(Register.this);
					mpb.setMessage("正在注册...");
					new Thread(new Runnable() {
						@Override
						public void run() {
							Message msg = new Message();
							msg.what = REGISTER_INFO;
							// String p="action=ce&email="+Editval(email);

							// String
							// t=GetUtil.getRes(ServerConfig.HOST+"/schoolknow/register.php?"+p);
							// $username = '', $nickname = '', $password = '',
							// $email = '', $verify = '', $type = 'start'
							List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
							params.add(new BasicNameValuePair("username",
									usernameVal));
							params.add(new BasicNameValuePair("nickname",
									nicknameVal));
							params.add(new BasicNameValuePair("password",
									pwdVal));
							params.add(new BasicNameValuePair("email", emailVal));
							//params.add(new BasicNameValuePair("verify",verifyVal));
							String result = HttpUtil.postRequest(
									Api.User.register(), params);
							msg.obj = result;
							handler.sendMessage(msg);
						}
					}).start();
				}
			}
		}

	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REGISTER_INFO:
				String result = msg.obj.toString();
				System.out.println("注册消息：" + result);
				JSONObject jsonResult = JSON.parseObject(result);
				if (jsonResult.getString("retCode").equals("0")) {
					T.showShort(Register.this, jsonResult.getString("error"));
					if (jsonResult.getString("error").equals("验证码输入错误。"))
						initVerifyCode();
				} else if (jsonResult.getString("retCode").equals("200")) {
					T.showShort(Register.this, "注册成功");
					finish();
				}
				break;
			case UPLOAD_HRAD_IMAGE:

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent it = new Intent(Register.this, Main.class);
						it.putExtra("param", "2");
						startActivity(it);
						finish();
					}
				}, 2000);
			default:
				break;
			}
			mpb.dismiss();

		}

	};

	/**
	 * 获取输入框的值
	 * 
	 * @param et
	 * @return
	 */
	public String Editval(EditText et) {
		return et.getText().toString();

	}

}
