package com.yuol.smile.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yuol.smile.R;
import com.yuol.smile.adapter.CommentAdapter;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.EmotionBox;
import com.yuol.smile.widgets.EmotionBox.EmotionBoxClickListener;
import com.yuol.smile.widgets.MyAlertDialog;
import com.yuol.smile.widgets.MyAlertDialog.MyDialogInt;
import com.yuol.smile.widgets.MyAlertMenu;
import com.yuol.smile.widgets.MyAlertMenu.MyDialogMenuInt;
import com.yuol.smile.widgets.MyProgressBar;

public class NewsComment extends BaseActivity {

	private PullToRefreshListView lv;

	private List<Map<String, Object>> list;

	private EmotionBox emotionBox;

	//private IndexItemBase item;

	private MyProgressBar mpb;

	private MyAlertMenu mam;
	
	private CommentAdapter adapter;

	private int page = 1;

	private MyAlertDialog mad;

	private LoginHelper loginHelper;

	private String id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_comment);
		Init();
		firstLoad();
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String val = msg.obj.toString();
			JSONObject result = null;
			result = JSONObject.parseObject(val);
			if (result != null && result.getString("retCode").equals("200")) {
				T.showShort(NewsComment.this, "评论成功");
				emotionBox.SetValue("");
				firstLoad();
			} else {
				T.showShort(NewsComment.this, "评论失败,请确定网络正常后再次尝试");
			}
		}
	};

	public void Init() {

		id = getIntent().getExtras().getString("id");
		
		loginHelper = new LoginHelper(this);
		/*item = (IndexItemBase) getIntent().getSerializableExtra(
				NewsActivity.SER_KEY);*/
		
		mpb = new MyProgressBar(this);
		mpb.setMessage("正在载入数据..");
		
		lv = (PullToRefreshListView) super.findViewById(R.id.comment_lv);
		emotionBox = new EmotionBox(NewsComment.this);
		emotionBox.setEditHint("我来说两句~");
		String nick = getIntent().getExtras().getString("nickname");
		if(null!=nick)
			emotionBox.SetValue("@"+nick+":");
		
		emotionBox.setOnClick(new EmotionBoxClickListener() {
			@Override
			public void onClick(final String EditTextVal, View view) {

				if (loginHelper.hasLogin()) {

					if (EditTextVal.length() > 128) {
						T.showShort(NewsComment.this, "评论长度不能大于128个字符");
						return;
					}else if(EditTextVal.length()==0){
						T.showShort(NewsComment.this, "评论不能为空");
						return;
					}

					final List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("newsid", id+ ""));
					params.add(new BasicNameValuePair("uid", loginHelper
							.getUid()));
					params.add(new BasicNameValuePair("comment", EditTextVal
							.trim()));
					new Thread(new Runnable() {
						@Override
						public void run() {
							Message msg = new Message();
							msg.what = 102;
							try {
								msg.obj = GetUtil.sendGet(Api.News
										.postNewsComment("Blog", "Article",
												id + "", URLEncoder
														.encode(EditTextVal
																.trim(),
																"UTF-8"),
												loginHelper.getUid()), "");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							handler.sendMessage(msg);
						}
					}).start();
				} else {
					mad = new MyAlertDialog(NewsComment.this);
					mad.setTitle("消息提示");
					mad.setMessage("您还没有登录不能发布评论，是否前往登录页面?");
					mad.setLeftButton("确定", new MyDialogInt() {
						@Override
						public void onClick(View view) {
							loginHelper.ToLogin();
							NewsComment.this.finish();
						}
					});
					mad.setRightButton("取消", new MyDialogInt() {
						@Override
						public void onClick(View view) {
							mad.dismiss();
							finish();
						}
					});
				}
			}
		});

		lv.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				emotionBox.HideEmotionBox();
				return false;
			}
		});

		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(
						getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				page = 1;
				list.clear();
				new AsyncLoadComment().execute("getNew");
			}
		});

		lv.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (list.size() != 0) {
					page++;
					new AsyncLoadComment().execute("readMore");
				}
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Map<String, Object> map = list.get(arg2-1);
				final String nick=map.get("name").toString();
				final String copyVal=map.get("content").toString();
				mam=new MyAlertMenu(NewsComment.this, new String[]{"回复","复制"});
				mam.setOnItemClickListener(new MyDialogMenuInt() {
					@SuppressLint("NewApi")
					public void onItemClick(int position) {
						switch(position){
						case 0:
							emotionBox.SetValue("@"+nick+":");
							break;
						case 1:
							ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
							String contentVal=copyVal.trim();
							int start=contentVal.indexOf("</at>");
							if(start!=-1){
								start+=5;
								contentVal=contentVal.substring(start);
							}
							cmb.setText(contentVal);
							T.showShort(NewsComment.this,"已复制到剪贴板");
							break;
						}
					}
				
			});
			}
			
		});
		list = new ArrayList<Map<String, Object>>();
		adapter = new CommentAdapter(this, list);
		lv.setAdapter(adapter);
	}

	public void firstLoad() {
		page = 1;
		list.clear();
		new AsyncLoadComment().execute("getNew");
	}

	public class AsyncLoadComment extends AsyncTask<String, Void, String> {

		

		@Override
		protected String doInBackground(String... params) {
			String uid = new LoginHelper(NewsComment.this).getUid();
			System.out.println("Comment:"+Api.News.getNewsComment(
						id + "", uid, page));
			if (!TextUtils.isEmpty(uid))
				return GetUtil.getRes(Api.News.getNewsComment(
						id + "", uid, page));
			else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null) {
				mad = new MyAlertDialog(NewsComment.this);
				mad.setTitle("消息提示");
				mad.setMessage("您还没有登录不能发布评论，是否前往登录页面?");
				mad.setLeftButton("确定", new MyDialogInt() {
					@Override
					public void onClick(View view) {
						loginHelper.ToLogin();
						finish();
					}
				});
				mad.setRightButton("取消", new MyDialogInt() {
					@Override
					public void onClick(View view) {
						mad.dismiss();
					}
				});
				return;
			}
			try {
				JSONObject jsonobj = JSONObject.parseObject(result);
				int sum = Integer.parseInt(jsonobj.getString("total_count"));
				if(sum>0)
					findViewById(R.id.no_comment_tip).setVisibility(View.GONE);
				
				setTitle(sum+"条评论");
				JSONArray data = jsonobj.getJSONArray("list");
				for (int i = 0; i < data.size(); i++) {
					JSONObject commentItem = data.getJSONObject(i);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", commentItem.getInteger("id"));
					JSONObject user = commentItem.getJSONObject("user");
					if (user != null) {
						map.put("name", user.getString("nickname"));
						map.put("head_image",
								Api.DOMAIN + user.getString("avatar64"));
					} else {
						map.put("name", "游客");
						map.put("head_image", Api.User.getDefaultUserImage());
					}

					map.put("time", commentItem.getString("create_time"));
					map.put("content", commentItem.getString("content"));
					map.put("uid", commentItem.getInteger("uid"));
					list.add(map);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			adapter.notifyDataSetChanged();
			mpb.dismiss();
			lv.onRefreshComplete();
		}

	}


	
	@Override
	public void onBackPressed() {
		if(emotionBox.isHasShow())
			emotionBox.HideEmotionBox();
		else
			finish();
	}
}
