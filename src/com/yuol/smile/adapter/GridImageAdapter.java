package com.yuol.smile.adapter;

import java.util.ArrayList;

import com.yuol.smile.R;
import com.yuol.smile.utils.ImageManager2;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class GridImageAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> dataList;
	private DisplayMetrics dm;

	public GridImageAdapter(Context c, ArrayList<String> dataList) {

		mContext = c;
		this.dataList = dataList;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);

	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, dipToPx(65)));
			imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY); 
		} else
			imageView = (ImageView) convertView;
		String path;
		if (dataList != null && position<dataList.size() )
			path = dataList.get(position);
		else
			path = "camera_default";
		if (path.contains("default"))
			imageView.setImageResource(R.drawable.empty_photo);
		else{
            ImageManager2.from(mContext).displayImage(imageView, path,R.drawable.empty_photo,100,100);
		}
		return imageView;
	}
	
	public int dipToPx(int dip) {
		return (int) (dip * dm.density + 0.5f);
	}

}
