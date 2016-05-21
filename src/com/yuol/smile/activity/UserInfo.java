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
		setTitle("������Ϣ");
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
			tv_stuid.setText("δ��");
		imageLoader.displayImage(lh.getHeadImg(), head);
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// �����ǳ�
		String temp_name = lh.getNickname().trim();
		if (temp_name.length() == 0) {
			nickname.setText("�����û�");
		} else {
			nickname.setText(lh.getNickname());
		}
	}

	public void onClick(View v) {
		Bundle bd ;
		Intent it;
		switch (v.getId()) {
		case R.id.user_info_head_layout:
			mam = new MyAlertMenu(UserInfo.this, new String[] { "�����ѡ��",
					"�������ѡ��", "ȡ��" });
			mam.setOnItemClickListener(new MyDialogMenuInt() {
				@Override
				public void onItemClick(int position) {
					switch (position) {
					case 0:
						// ѡ��ͼ��
						Intent it = new Intent(Intent.ACTION_PICK, null);
						it.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
						startActivityForResult(it, 1);
						break;
					case 1:
						// ѡ�����
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
			bd.putString("name", "����");
			bd.putString("value", lh.getNickname());
			it = new Intent(UserInfo.this, ModifyValue.class);
			it.putExtras(bd);
			startActivityForResult(it, 4);
			break;
		case R.id.user_info_stuid_layout:
			mam = new MyAlertMenu(UserInfo.this, new String[] { "���°�","ע��", "ȡ��" });
			mam.setOnItemClickListener(new MyDialogMenuInt() {
				@Override
				public void onItemClick(int position) {
					switch (position) {
					case 0:
						// ѡ��ͼ��
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
		// ������ȡ
		case 1:
			if (data != null) {
				startPhotoZoom(data.getData());
			}
			break;
		// �������
		case 2:
			File temp = new File(PathConfig.SavePATH, picName);
			if (temp.exists()) {
				startPhotoZoom(Uri.fromFile(temp));
			}
			break;
		// ȡ�òü����ͼƬ
		case 3:
			if (data != null) {
				setPicToView(data);
			}
		case 4:
			if (data != null) {
				String name = data.getStringExtra("name");
				if ("����".equals(name))
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
		// �������crop=true�������ڿ�����Intent��������ʾ��VIEW�ɲü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 180);
		intent.putExtra("outputY", 180);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	/**
	 * ����ü�֮���ͼƬ����
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			head.setImageBitmap(ImageUtil.toRoundCorner(photo, 10));

			// ��ͷ�񱣴���sd��head�ļ���
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
							T.showLong(UserInfo.this, "ͷ���ϴ�ʧ�ܣ������³���");
						}

						public void onSuccess(ResponseInfo<String> arg0) {
							System.out.println("ͼƬ�ϴ�:" + arg0.result);
							JSONObject retObject = JSONObject
									.parseObject(arg0.result);
							lh.setHeadImg(Api.DOMAIN
									+ retObject.getString("data"));
							T.showLong(UserInfo.this, "ͷ���ϴ��ɹ�");
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
			// ��ͼƬ�����ַ�����ʽ�洢����
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
			System.out.println("�޸���Ϣ:" + result);
			if (result != null && !"".equals(result)) {
				JSONObject jsonResult;
				try {
					jsonResult = JSONObject.parseObject(result);
					if (jsonResult.getInteger("retCode") == 1) {
						tv.setText(param);
						if (param.equals("��"))
							lh.setSex("1");
						else if (param.equals("Ů"))
							lh.setSex("2");
						else
							lh.setSex("0");
						T.showShort(UserInfo.this, "�޸ĳɹ�");
					} else {
						T.showShort(UserInfo.this, "�޸�ʧ��,�����³���");
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
			T.showShort(UserInfo.this, "�ɹ��˳���ǰ�˺�");
			finish();
		}
	}

	
}
