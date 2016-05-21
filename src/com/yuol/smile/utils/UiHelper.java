package com.yuol.smile.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.yuol.smile.YUNewsDetail;
import com.yuol.smile.activity.NewsContent;
import com.yuol.smile.base.IndexItemBase;
import com.yuol.smile.bean.NewsBean;
import com.yuol.smile.bean.YUNewsItem;

public class UiHelper {
	public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	public static void redirect(Context context, NewsBean news) {
		Intent intent = null;
		Bundle mBundle = new Bundle();

		if ("长大新闻".equals(news.getColumn())) {
			intent = new Intent(context, YUNewsDetail.class);
			intent.putExtra("id", news.getId());
			intent.putExtra("title", news.getTitle());
			intent.putExtra("time", news.getTime());
			intent.putExtra("witer", news.getWriter());
			intent.putExtra("column", news.getColumn());
			intent.putExtra("click", news.getClick());
			intent.putExtra("image", news.getImage());
			intent.putExtra("body", news.getContent());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			YUNewsItem item = new YUNewsItem();
			item.setId(news.getId());
			item.setTitle(news.getTitle());
			item.setPublishTime(news.getTime());
			item.setImageRight(news.getImage());

			mBundle.putSerializable(Api.SER_KEY, item);

		} else if ("校园资讯".equals(news.getColumn())) {

			intent = new Intent(context, NewsContent.class);
			mBundle.putInt("id", news.getId());
			mBundle.putString("title", news.getTitle());
			mBundle.putString("cover", news.getImage());

		} else if ("校园活动".equals(news.getColumn())) {

			intent = new Intent(context, NewsContent.class);
			mBundle.putInt("id", news.getId());
			mBundle.putString("title", news.getTitle());
			mBundle.putString("cover", news.getImage());
		}

		intent.putExtras(mBundle);
		context.startActivity(intent);
	}

	public static void imageShow(final Context context, WebView webView) {
		webView.getSettings().setJavaScriptEnabled(true);
	}
}
