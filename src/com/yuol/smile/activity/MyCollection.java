package com.yuol.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yuol.smile.R;
import com.yuol.smile.adapter.CollectAdapt;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.bean.NewsBean;
import com.yuol.smile.db.DBUtil;
import com.yuol.smile.utils.UiHelper;

public class MyCollection extends BaseActivity {
    private ListView lv=null;
    private ListAdapter adapter=null;
    private List<NewsBean> list=null;
    private TextView loading=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mycollection);
		setTitle("我的收藏");
		loading=(TextView)super.findViewById(R.id.collect_loading);
		initlist();
		lv=(ListView)super.findViewById(R.id.mycollection_lv);
		adapter=new CollectAdapt(this,R.layout.mycollection_item,list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListenerimpl());
	}
	public void initlist(){
		AsyncTask_Collect asyncTask=new AsyncTask_Collect(MyCollection.this,loading);
		asyncTask.execute();
		try {
			list= asyncTask.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private class OnItemClickListenerimpl implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			NewsBean news = (NewsBean) parent.getItemAtPosition(position);
			UiHelper.redirect(view.getContext(), news);
		}
		
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			MyCollection.this.finish();
			break;
		}
		return true;
 }
	class AsyncTask_Collect extends AsyncTask<Void, String, List<NewsBean>> {
	    private SQLiteDatabase db=null;
	    private List<NewsBean> list=null;
	    private Cursor result=null;
	    private Context context;
	    private TextView loading=null;
	    public AsyncTask_Collect(Context context,TextView loading){
	    	this.context=context;
	    	this.loading=loading;
	    }
		@Override
		protected List<NewsBean> doInBackground(Void... params) {
			try {
				DBUtil dbu = DBUtil.getInstance(MyCollection.this);
				
				result = dbu.rawQuery("SELECT * FROM collect_tb");
				
				list= new ArrayList<NewsBean>();
				//result.moveToFirst();
				while (result.moveToNext()) {
				    NewsBean news = new NewsBean();  
				    news.setColumn(result.getString(result.getColumnIndex("news_column")));
				    news.setId(result.getInt(result.getColumnIndex("news_id")));
				    news.setImage(result.getString(result.getColumnIndex("news_image")));
				    news.setTime(result.getString(result.getColumnIndex("news_time")));
				    news.setTitle(result.getString(result.getColumnIndex("news_title")));
				    list.add(news);
				}
				result.close();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				System.out.println(e);
				e.printStackTrace();
			}
	        return list;
		}
		@Override
		protected void onPostExecute(List<NewsBean> result) {
			if(result==null||result.size()==0)
				loading.setText("你还没有收藏任何新闻。");
			else
				loading.setVisibility(View.GONE);
			super.onPostExecute(result);
		}

	}

	
}
