package com.yuol.smile.widgets;

import android.content.Context;
import android.webkit.WebView;

public class NoScrollWebView extends WebView
{
	public NoScrollWebView(Context context,android.util.AttributeSet attrs)
	{
		super(context, attrs);
	}
	/**
	 * ÉèÖÃ²»¹ö¶¯
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}

}
