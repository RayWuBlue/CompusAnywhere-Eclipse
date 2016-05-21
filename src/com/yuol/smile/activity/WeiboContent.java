package com.yuol.smile.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuol.smile.R;
import com.yuol.smile.adapter.Comment2Adapter;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.base.SchoolFellowBase;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.RegUtils;
import com.yuol.smile.utils.T;
import com.yuol.smile.utils.TimeUtil;
import com.yuol.smile.utils.ViewHolder;
import com.yuol.smile.widgets.EmotionBox;
import com.yuol.smile.widgets.EmotionBox.EmotionBoxClickListener;
import com.yuol.smile.widgets.MyAlertMenu;
import com.yuol.smile.widgets.MyAlertMenu.MyDialogMenuInt;

@SuppressLint("HandlerLeak")
public class WeiboContent extends BaseActivity {

	private List<Map<String, Object>> list;
	
	private Comment2Adapter adapter;

	private ListView lv;
	
	public static EmotionBox emotionBox;

	public SchoolFellowBase item;
	public int position;

	public LoginHelper lh;

	public Context mcontext;

	public  static String  targetUser ="";

	public MyAlertMenu mam;

	public String userid = "";
	
	public String userUid = "";

	private String input;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_schoolfellow_content);

		Intent getIntent = getIntent();
		item = (SchoolFellowBase) getIntent
				.getSerializableExtra(Api.SER_KEY);
		position = getIntent.getIntExtra("position", 0);

		setTitle(item.getNickname());

		lh = new LoginHelper(this);
		mcontext = WeiboContent.this;
		userUid = item.getUid();

		lv = (ListView) super
				.findViewById(R.id.schoolfellow_content_comment_lv);

		list = new ArrayList<Map<String, Object>>();

		// View
		// head=SchoolFellowSquare.adapter.getView(position,null,null,false);
		View head = getView(mcontext, item);
		lv.addHeaderView(head);
		adapter = new Comment2Adapter(this, list);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, View arg1,
					int position, long arg3) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) parent
						.getItemAtPosition(position);
				final String copyVal = map.get("content").toString();
				mam = new MyAlertMenu(mcontext, new String[] { "回复", "复制" });
				mam.setOnItemClickListener(new MyDialogMenuInt() {
					@SuppressLint("NewApi")
					public void onItemClick(int position) {
						switch (position) {
						case 1:
							// 得到剪贴板管理器
							ClipboardManager cmb = (ClipboardManager) mcontext
									.getSystemService(Context.CLIPBOARD_SERVICE);
							String contentVal = copyVal.trim();
							int start = contentVal.indexOf("</at>");
							if (start != -1) {
								start += 5;
								contentVal = contentVal.substring(start);
							}
							cmb.setText(contentVal);
							T.showShort(mcontext, "已经复制到粘贴板");
							break;
						}
					}
				});

			}
		});

		emotionBox = new EmotionBox(this);
		emotionBox.setEditHint("@"+item.getNickname());
		emotionBox.setOnClick(new EmotionBoxClickListener() {
			public void onClick(String EditTextVal, View view) {
				 input = EditTextVal;
				if (!lh.hasLogin()) {
					T.showShort(mcontext, "请登录后再发布评论");
				} else if (EditTextVal.trim().length() == 0) {
					T.showShort(mcontext, "发布的内容不能为空");
				} else if (EditTextVal.length() > 255) {
					T.showShort(mcontext, "发布的内容不能超过256个字符");
				} else if (!NetHelper.isNetConnected(mcontext)) {
					T.showShort(mcontext, R.string.net_error_tip);
					return;
				} else {
					if(input!=null&&!"".equals(input.trim())){
						new Thread(new Runnable() {
							public void run() {
								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("token", lh
										.getToken()));
								params.add(new BasicNameValuePair("weibo_id", item
										.getId() + ""));
								if(!targetUser.equals(item.getNickname())) 
									input="@"+targetUser+"："+input;
								
								params.add(new BasicNameValuePair("content", input
										.trim()));
								// /token/"+token+"/weibo_id/"+weibo_id+"/content/"+content
								String str = GetUtil.sendPost(
										Api.Weibo.postComment(), params);
								Message msg = new Message();
								msg.what = 102;
								msg.obj = str;
								handler.sendMessage(msg);
							}
						}).start();
					}

				}
			}
		});
		// 启动线程获取评论
		new AsyncLoadComment().execute();
		
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			System.out.println("评论数据：" + msg.obj.toString());
			JSONObject res = JSON.parseObject(msg.obj.toString());
			if (msg.what == 102) {
				if (res.getInteger("retCode") == 200) {
					T.showShort(mcontext, "发布成功");
					emotionBox.SetValue("");
					// 更新界面
					list.clear();
					new AsyncLoadComment().execute();
				} else {
					T.showShort(mcontext, res.getString(res.getString("error")));
				}
			}
		}

	};

	// 异步加载评论
	class AsyncLoadComment extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... params) {
			String data = GetUtil.getRes(Api.Weibo.getWeiboComment(item.getId()
					+ ""));
			return data;
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			System.out.println("WEIBO__;" + result);
			try {
				JSONObject jsonResult = JSON.parseObject(result);
				List<Map<String, Object>> JsonData = new ArrayList<Map<String, Object>>();
				if (jsonResult.getInteger("retCode") == 200) {
					JSONArray jsonWeiboList = jsonResult.getJSONArray("list");

					for (int i = 0; i < jsonWeiboList.size(); i++) {
						JSONObject weiboItem = jsonWeiboList.getJSONObject(i);
						Map<String, Object> data = new HashMap<String, Object>();
						JSONObject jsonUser = weiboItem.getJSONObject("user");
						System.out.println("WEIBO__;"
								+ weiboItem.toJSONString());
						data.put("id", weiboItem.getString("id"));

						if (jsonUser != null) {
							data.put("nick", jsonUser.getString("nickname"));
							data.put("uid", jsonUser.getString("uid"));
							data.put("user_image",
									Api.DOMAIN + jsonUser.getString("avatar64"));
						} else {
							data.put("nick", "游客");
							data.put("uid", 0);
							data.put("user_image",
									Api.User.getDefaultUserImage());
						}
						data.put("content", weiboItem.getString("content"));
						data.put("time", weiboItem.getString("create_time"));

						JsonData.add(data);
					}
					list.addAll(JsonData);
					adapter.notifyDataSetInvalidated();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public  View getView(final Context context, final SchoolFellowBase item) {
		final View convertView = LayoutInflater.from(context).inflate(
				R.layout.item_school_fellow_lv_2, null, false);
		ImageView head = ViewHolder.get(convertView,
				R.id.schoolfellow_item2_user_img);
		TextView nick = ViewHolder.get(convertView,
				R.id.schoolfellow_item2_user_nick);
		TextView time = ViewHolder.get(convertView,
				R.id.schoolfellow_item2_time);
		TextView content = ViewHolder.get(convertView,
				R.id.schoolfellow_item2_content);
		
		LinearLayout ll_single = ViewHolder.get(convertView,
				R.id.image_layout_single);
		//commentTip.setText("评论( " + item.getCommentNum() + " )");
		setTitle("评论( " + item.getCommentNum() + " )");
		nick.setText(item.getNickname());
		time.setText(TimeUtil.getCommentTime(Long.parseLong(item.getTime())));

		//bm.display(head, item.getUimage());
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageForEmptyUri(R.drawable.white)         //没有图片资源时的默认图片  
        .showImageOnFail(R.drawable.white)              //加载失败时的图片  
        .cacheInMemory()                               //启用内存缓存  
        .cacheOnDisc()                                 //启用外存缓存  
        .build();

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(item.getUimage(),head,options);


		// 获取发布内容中的图片地址
		final List<String> imgList = item.getImgList();
		ImageView image_single = ViewHolder.get(convertView,
				R.id.item_img_single);
		if (imgList != null) {
			if (imgList.size() == 1) {
				ll_single.setVisibility(View.VISIBLE);
				//bm.display(image_single, imgList.get(0));
				imageLoader.displayImage(imgList.get(0),image_single,options);
			} 
		} else {
			ll_single.setVisibility(View.GONE);
		}
		String str = item.getContent();

		content.setText(EmotionBox.convertNormalStringToSpannableString(
				context, RegUtils.replaceImage(str)),
				BufferType.SPANNABLE);
		return convertView;
	}

}
