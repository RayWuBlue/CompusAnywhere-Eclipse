package com.yuol.smile.activity;

import java.io.File;

import org.apache.http.HttpException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.yuol.smile.JwcLoginActivity;
import com.yuol.smile.R;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.config.DecodeConfig;
import com.yuol.smile.config.PathConfig;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.BitmapUtil;
import com.yuol.smile.utils.FileUtils;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.ImageUtil;
import com.yuol.smile.utils.T;
import com.yuol.smile.utils.TimeUtil;
import com.yuol.smile.widgets.MyAlertDialog;
import com.yuol.smile.widgets.MyAlertMenu;
import com.yuol.smile.widgets.MyAlertMenu.MyDialogMenuInt;
import com.yuol.smile.widgets.RoundedImageView;

public class UserInfo extends BaseActivity {

	private MyAlertMenu mam;

	private RoundedImageView head;

	private TextView nickname;

	private LoginHelper lh;

	private MyAlertDialog sexMad;

	private MyAlertDialog modify;

	public String picName;

	private Button exit;

	private TextView tv_stuid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_user_info);
		setTitle("个人信息");
		lh = new LoginHelper(this);
		head = (RoundedImageView) findViewById(R.id.user_info_head);
		nickname = (TextView) super.findViewById(R.id.user_info_nickname);
		tv_stuid = (TextView)findViewById(R.id.user_info_stuid);
		exit = (Button) super.findViewById(R.id.setting_exit_login_btn);
		exit.setOnClickListener(new ExitLogin());

	}

	@Override
	protected void onResume() {
		nickname.setText(lh.getNickname());
		String stuid = lh.getJwcId();
		if(stuid!=null&&!"".equals(stuid))
			tv_stuid.setText(stuid);
		else
			tv_stuid.setText("未绑定");
		imageLoader.displayImage(lh.getHeadImg(), head);
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// 更新昵称
		String temp_name = lh.getNickname().trim();
		if (temp_name.length() == 0) {
			nickname.setText("匿名用户");
		} else {
			nickname.setText(lh.getNickname());
		}
	}

	public void onClick(View v) {
		Bundle bd ;
		Intent it;
		switch (v.getId()) {
		case R.id.user_info_head_layout:
			mam = new MyAlertMenu(UserInfo.this, new String[] { "从相册选择",
					"从照相机选择", "取消" });
			mam.setOnItemClickListener(new MyDialogMenuInt() {
				@Override
				public void onItemClick(int position) {
					switch (position) {
					case 0:
						// 选择图库
						Intent it = new Intent(Intent.ACTION_PICK, null);
						it.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
						startActivityForResult(it, 1);
						break;
					case 1:
						// 选择相机
						picName = String.valueOf(TimeUtil.getCurrentTime())
								+ ".jpg";
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						FileUtils.createPath(PathConfig.SavePATH);
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(new File(PathConfig.SavePATH,
										picName)));
						startActivityForResult(intent, 2);
						break;
					case 2:
						break;
					default:
						break;
					}
				}
			});
			break;
		case R.id.user_info_nick_layout:
			bd = new Bundle();
			bd.putString("name", "昵名");
			bd.putString("value", lh.getNickname());
			it = new Intent(UserInfo.this, ModifyValue.class);
			it.putExtras(bd);
			startActivityForResult(it, 4);
			break;
		case R.id.user_info_stuid_layout:
			mam = new MyAlertMenu(UserInfo.this, new String[] { "重新绑定","注销", "取消" });
			mam.setOnItemClickListener(new MyDialogMenuInt() {
				@Override
				public void onItemClick(int position) {
					switch (position) {
					case 0:
						// 选择图库
						Intent it = new Intent(UserInfo.this,JwcLoginActivity.class);
						it.putExtra("type", JwcLoginActivity.TYPE_BIND_STUID);
						startActivity(it);
						break;
					case 1:
						lh.JwcLogout();
						tv_stuid.setText("");
						break;
					case 2:
						break;
					default:
						break;
					}
				}
			});
			break;
		default:
			break;
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// 从相册获取
		case 1:
			if (data != null) {
				startPhotoZoom(data.getData());
			}
			break;
		// 相机拍照
		case 2:
			File temp = new File(PathConfig.SavePATH, picName);
			if (temp.exists()) {
				startPhotoZoom(Uri.fromFile(temp));
			}
			break;
		// 取得裁剪后的图片
		case 3:
			if (data != null) {
				setPicToView(data);
			}
		case 4:
			if (data != null) {
				String name = data.getStringExtra("name");
				if ("昵名".equals(name))
					nickname.setText(data.getStringExtra("value"));
			}
			break;
		default:
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 180);
		intent.putExtra("outputY", 180);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			head.setImageBitmap(ImageUtil.toRoundCorner(photo, 10));

			// 将头像保存在sd卡head文件中
			final LoginHelper lh = new LoginHelper(UserInfo.this);
			final String path = PathConfig.HEADPATH;
			final String name = DecodeConfig.decodeHeadImg(lh.getUid())
					+ ".jpg";
			try {
				BitmapUtil.saveImg(photo, path, name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			File f = new File(path + name);
			RequestParams params = new RequestParams();
			params.addBodyParameter("pic", f);
			HttpUtils http = new HttpUtils();
			http.send(
					HttpMethod.POST,
					Api.UserConfig.uploadTempAvatar(lh.getUid(), lh.getToken()),
					params, new RequestCallBack<String>() {
						public void onFailure(HttpException arg0, String arg1) {
							T.showLong(UserInfo.this, "头像上传失败，请重新尝试");
						}

						public void onSuccess(ResponseInfo<String> arg0) {
							System.out.println("图片上传:" + arg0.result);
							JSONObject retObject = JSONObject
									.parseObject(arg0.result);
							lh.setHeadImg(Api.DOMAIN
									+ retObject.getString("data"));
							T.showLong(UserInfo.this, "头像上传成功");
						}

						@Override
						public void onFailure(
								com.lidroid.xutils.exception.HttpException arg0,
								String arg1) {
							// TODO Auto-generated method stub
							
						}
					});

			// ByteArrayOutputStream stream = new ByteArrayOutputStream();
			// photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
			// byte[] b = stream.toByteArray();
			// 将图片流以字符串形式存储下来
			// tp = new String(Base64Coder.encodeLines(b));

		}
	}

	public class AsyncUpdateInfo extends AsyncTask<String, Void, String> {

		public TextView tv;
		public String param;

		public AsyncUpdateInfo(TextView tv) {
			this.tv = tv;
		}

		protected String doInBackground(String... str) {
			param = str[1];

			if ("sex".equals(str[0]))
				return GetUtil.sendGet(Api.UserConfig.modifysex(lh.getUid(),
						lh.getToken(), str[1]), null);
			else if ("birthday".equals(str[0]))
				return GetUtil.sendGet(
						Api.UserConfig.modifybirthday(lh.getUid(),
								lh.getToken(), str[1]), null);
			else
				return "";
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			System.out.println("修改信息:" + result);
			if (result != null && !"".equals(result)) {
				JSONObject jsonResult;
				try {
					jsonResult = JSONObject.parseObject(result);
					if (jsonResult.getInteger("retCode") == 1) {
						tv.setText(param);
						if (param.equals("男"))
							lh.setSex("1");
						else if (param.equals("女"))
							lh.setSex("2");
						else
							lh.setSex("0");
						T.showShort(UserInfo.this, "修改成功");
					} else {
						T.showShort(UserInfo.this, "修改失败,请重新尝试");
					}
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}

	}

	public class ExitLogin implements OnClickListener {
		public void onClick(View arg0) {
			new LoginHelper(UserInfo.this).logout();
			T.showShort(UserInfo.this, "成功退出当前账号");
			finish();
		}
	}

	
}
