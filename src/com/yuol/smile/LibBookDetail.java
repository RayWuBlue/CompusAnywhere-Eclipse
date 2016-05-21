package com.yuol.smile;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yuol.smile.adapter.LibCollectAdapter;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.bean.LibCollectInfo;

public class LibBookDetail extends BaseActivity {

    private TextView bk_name;
    private TextView bk_author;
    private TextView bk_press;
    private TextView bk_no;
    private TextView bk_page;
    private TextView bk_price;
    private TextView bk_theme;
    private TextView bk_ask;
    private TextView bk_classify;
    private ListView lv;
    private List<LibCollectInfo> list;
    private LibCollectAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lib_bookdetail_main);
		initview();
		setData();
	}
	public void initview(){
		setTitle("ÊéÄ¿ÏêÇé");
		setTitleBarColor(getResources().getColor(R.color.title_bar_blue));
		bk_name=(TextView)super.findViewById(R.id.book_name);
		bk_author=(TextView)super.findViewById(R.id.book_author);
		bk_press=(TextView)super.findViewById(R.id.book_press);
		bk_no=(TextView)super.findViewById(R.id.book_no);
		bk_page=(TextView)super.findViewById(R.id.book_pageCount);
		bk_price=(TextView)super.findViewById(R.id.book_price);
		bk_theme=(TextView)super.findViewById(R.id.book_theme);
		bk_ask=(TextView)super.findViewById(R.id.book_askNo);
		bk_classify=(TextView)super.findViewById(R.id.book_classify);
		lv=(ListView)super.findViewById(R.id.book_lv);
		list=new ArrayList<LibCollectInfo>();
		adapter=new LibCollectAdapter(this, list);
		lv.setAdapter(adapter);
	}
	public void setData(){
		Intent intent=getIntent();
		String html=intent.getStringExtra("html");
		Document doc=Jsoup.parse(html);
		Elements eles=doc.select("table[bgcolor=#008080]").get(0).select("tr");
		bk_name.setText(eles.get(0).select("a").get(0).text());
		bk_author.setText(eles.get(1).select("a").get(0).text());
		bk_press.setText(eles.get(2).select("td").first().text().substring(4));
		bk_no.setText(eles.get(3).select("td").first().text().substring(7));
		bk_page.setText(eles.get(4).select("td").first().text().substring(3));
		bk_price.setText(eles.get(5).select("td").first().text().substring(3));
		bk_theme.setText(eles.get(6).select("a").get(0).text());
		bk_ask.setText(eles.get(7).select("a").get(0).text());
		bk_classify.setText(eles.get(9).select("a").get(0).text());
		Elements eles_lv=doc.select("table[bgcolor=#008080]").get(1).select("tr");
		for (int i=1;i<eles_lv.size();i++) {
			Elements eles_td=eles_lv.get(i).select("td");
			LibCollectInfo lib=new LibCollectInfo();
			lib.setBarCode(eles_td.get(0).text());
			System.out.println(eles_td.get(0).text());
			lib.setAddress(eles_td.get(1).text());
			lib.setType(eles_td.get(2).text());
			lib.setStatu(eles_td.get(3).text());
			lib.setReturnDate(eles_td.get(4).text());
			lib.setExplain(eles_td.get(5).text());
			list.add(lib);
		}
		adapter.notifyDataSetChanged();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

    
}
