package com.yuol.smile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuol.smile.R;
import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.helper.VersionHelper;
import com.yuol.smile.utils.T;

public class Setting extends BaseActivity {
	
	private RelativeLayout versionLayout,about,suggest;
	private TextView version;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.act_setting);
		setTitle("设置");
		
		version=(TextView) super.findViewById(R.id.setting_check_version);
		version.setText(VersionHelper.getVerName(this));
		
		versionLayout=(RelativeLayout) super.findViewById(R.id.setting_check_version_layout);
		versionLayout.setOnClickListener(new LayoutOnclick());
		
		about=(RelativeLayout) super.findViewById(R.id.setting_check_about_layout);
		about.setOnClickListener(new LayoutOnclick());
		
		suggest=(RelativeLayout) super.findViewById(R.id.setting_check_suggest_layout);
		suggest.setOnClickListener(new LayoutOnclick());
	}

	
	public class LayoutOnclick implements OnClickListener{
		public void onClick(View v) {
			if(v==versionLayout){
				VersionHelper vh=new VersionHelper(Setting.this);
				if(vh.checkUpdate()){
					vh.updateTip();
				}else{
					T.showShort(Setting.this, "已经是最新版本");
				}
			}else if(v==about){
				Intent it=new Intent(Setting.this,About.class);
				startActivity(it);
			}
		}
		
	}


}
