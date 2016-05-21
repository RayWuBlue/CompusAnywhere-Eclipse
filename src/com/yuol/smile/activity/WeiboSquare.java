package com.yuol.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuol.smile.R;
import com.yuol.smile.adapter.SchoolFellowAdapter;
import com.yuol.smile.base.SchoolFellowBase;
import com.yuol.smile.config.PathConfig;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.helper.NetHelper;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.FileUtils;
import com.yuol.smile.utils.GetUtil;
import com.yuol.smile.utils.T;
import com.yuol.smile.utils.TimeUtil;
import com.yuol.smile.widgets.XListView;
import com.yuol.smile.widgets.XListView.IXListViewListener;

public class WeiboSquare extends Activity {

	private XListView lv;
	private List<SchoolFellowBase> list;
	public static SchoolFellowAdapter adapter;

	//public MyProgressBar mpb;

	public String num = "0";
	public Context mcontext;
	/*
	 * 
	 * */
	public String loadMoreUrl = null;
	public int loadCount = 1;
	public int page = 1;
	public int lastId;
	public int total_count;


	private IXListViewListenerImp dataListener;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_schoolfellow);


		mcontext = this;

		list = new ArrayList<SchoolFellowBase>();
		lv = (XListView) super.findViewById(R.id.school_fellow_listview);
		lv.setPullLoadEnable(true);

		FileUtils.createPath(PathConfig.BASEPATH + "/data");

		//mpb = new MyProgressBar(this);
		//mpb.setMessage("正在加载中...");
		adapter = new SchoolFellowAdapter(WeiboSquare.this, list);
		lv.setAdapter(adapter);
		dataListener = new IXListViewListenerImp();
		lv.setXListViewListener(dataListener);
		dataListener.onRefresh();
/*		if (!NetHelper.isNetConnected(this)) {
			try {
				List<SchoolFellowBase> dbItemList = SchoolfellowDB.getInstance(
						mcontext).getList();
				list.addAll(dbItemList);
				adapter.notifyDataSetInvalidated();
				T.showLong(mcontext, R.string.net_error_tip);
				mpb.dismiss();
				return;
			} catch (Exception e) {

			}
		} else {
			new AsyncloadSF().execute("getNews", num);
		}*/
	}

	class IXListViewListenerImp implements IXListViewListener {
		public void onRefresh() {
			//mpb.show();
			//mpb.setMessage("正在加载中...");
			if (!NetHelper.isNetConnected(mcontext)) {
				T.showShort(mcontext, R.string.net_error_tip);
				lv.stopRefresh();
				lv.stopLoadMore();
				//mpb.dismiss();
				return;
			}
			list.clear();
			loadCount = 1;
			page = 1;
			lastId = 0;
			new AsyncloadSF().execute("getNews");
			lv.setRefreshTime(""+ TimeUtil.getUpdateTime(TimeUtil.getCurrentTime()));
		}

		public void onLoadMore() {
			if (!NetHelper.isNetConnected(mcontext)) {
				T.showShort(mcontext, R.string.net_error_tip);
				lv.stopRefresh();
				lv.stopLoadMore();
				return;
			}
			new AsyncloadSF().execute("readMore");
		}

	}

/*	@Override
	protected void HandleTitleBarEvent(int buttonId, View v) {
		switch (buttonId) {
		case 2:
			Intent it = new Intent(SchoolFellowSquare.this, NewAsk.class);
			it.putExtra("param", "schoolfellow");
			startActivity(it);
			break;
		default:
			break;
		}
	}*/
	
	public class AsyncloadSF extends AsyncTask<String, Void, String> {
		private String loadMoreInPage() {
			String url;
			if (page > 1)
				url = Api.HOST+"index.php?s=/weibo/index/appindex/page/"+ page + ".html";
			else
				url = Api.HOST+"index.php?s=/weibo/index/apploadweibo/page/"+ page+ "/loadCount/"+ loadCount+ "/lastId/"+ lastId;
			return GetUtil.getRes(url);
		}

		protected String doInBackground(String... params) {
			String result = "";
			if ("getNews".equals(params[0])) {
				result = GetUtil.getRes(Api.Weibo.getWeiboIndex());
				System.out.println("Get请求结果：" + result);
			} else {
				if (page == 1 && loadCount < 3) {
					loadCount++;
					result = loadMoreInPage();
				} else {
					page++;
					result = loadMoreInPage();
				}
			}
			return result;
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			/*
			 * if(!result.trim().equals("[]")){ try { List<Map<String,Object>>
			 * dataList=new JsonHelper(result).parseJson( new
			 * String[]{"id","uid","nn","ct","tm","num"});
			 * for(Map<String,Object> map:dataList){ SchoolFellowBase base=new
			 * SchoolFellowBase(Integer.parseInt(map.get("id").toString()),
			 * map.get
			 * ("uid").toString(),map.get("nn").toString(),map.get("tm").toString
			 * (), map.get("ct").toString(),map.get("num").toString());
			 * list.add(base);
			 * 
			 * //保存在数据库 SchoolfellowDB.getInstance(mcontext).save(base); } }
			 * catch (Exception e) { e.printStackTrace(); }
			 * adapter.notifyDataSetChanged(); }
			 */

			/*
			 * private int id; private int type; private String title; private
			 * String intro; private String address; private long timesamp;
			 */
			if (result != null && !result.trim().equals("[]")) {
				try {
					//org.json.JSONObject json = new org.json.JSONObject(result);
					//System.out.println("############"+json.toString());
					System.out.println("SCHOOL-JSONDATA:"+result);
					JSONObject jsonResult = JSON.parseObject(result);
					System.out.println("JSON-RESULT:"+jsonResult.toJSONString());
					JSONArray weiboList = jsonResult.getJSONArray("data");
					if (loadMoreUrl == null&& jsonResult.getString("loadMoreUrl") != null)
						loadMoreUrl = jsonResult.getString("loadMoreUrl");
					if(jsonResult.getInteger("lastId")!=null)
						lastId = jsonResult.getInteger("lastId");
					Log.d("HTTP_LOAD", "lastId:" + lastId);
					for (int i = 0; i < weiboList.size(); i++) {
						JSONObject weiboItem = weiboList.getJSONObject(i);
						JSONObject weiboUser = weiboItem.getJSONObject("user");

						List<String> imgList = null;
						if ("image".equals(weiboItem.getString("type"))) {
							JSONArray jsonImgList = weiboItem
									.getJSONArray("imgs");
							imgList = new ArrayList<String>();
							for (int j = 0; j < jsonImgList.size(); j++) {
								imgList.add(Api.DOMAIN + jsonImgList.getString(j));
							}
						}
						String like_user = weiboItem.getString("like_user");
						Boolean hasLike = false;
						//用户点过赞的突出显示
						if(like_user!=null&&!"".equals(like_user)){
							String uid = new LoginHelper(WeiboSquare.this).getUid();
							hasLike = like_user.contains(uid);
							//游客不可点赞
							if(uid==null||"".equals(uid))
								hasLike = false;
							}
						System.out.println("HAS_LIKE:"+hasLike+"  like_user:"+weiboItem.getString("like_user"));
						SchoolFellowBase base = new SchoolFellowBase(
								weiboItem.getInteger("id"),
								weiboUser.getString("uid"),
								weiboUser.getString("nickname"),
								Api.DOMAIN+weiboUser.getString("avatar64"),//头像图片路径注意一下
								weiboItem.getString("create_time"),
								weiboItem.getString("content"), imgList,
								weiboItem.getString("comment_count"),
								weiboItem.getString("like_num"),
								weiboItem.getString("repost_count"),
								hasLike
								);
						list.add(base);
						// 保存在数据库
						// SchoolfellowDB.getInstance(mcontext).save(base);
					}
				} catch (Exception e) {
					System.out.println("ERROR:"+e.getMessage());
					e.printStackTrace();
				}
				adapter.notifyDataSetChanged();
			} else {
				T.showShort(WeiboSquare.this, "无更多内容");
			}
			//mpb.dismiss();
			lv.stopRefresh();
			lv.stopLoadMore();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		dataListener.onRefresh();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
