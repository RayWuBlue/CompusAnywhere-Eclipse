package com.yuol.smile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuol.smile.Notice;
import com.yuol.smile.R;
import com.yuol.smile.helper.LoginHelper;
import com.yuol.smile.helper.VersionHelper;
import com.yuol.smile.utils.AndroidShare;
import com.yuol.smile.utils.T;
import com.yuol.smile.widgets.RoundedImageView;

public class User extends Activity {
	
	private RoundedImageView userImg;
	private TextView userName;
	public LoginHelper lh;
	//BitmapUtils bm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_user);		
		userImg=(RoundedImageView)findViewById(R.id.myzone_user_img);
		
		lh=new LoginHelper(User.this);

	    System.out.println("HEAD_IMAGE:"+lh.getHeadImg());
	    //设置用户名和性别
	    userName=(TextView) findViewById(R.id.myzone_user_name);
	    if(lh.hasLogin())
	    	userName.setText(lh.getNickname());
	    else
	    	userName.setText("请先登录");
	    userImg.setOnClickListener(new userHeadClick());

	}

	//个性设置

	@Override
	protected void onResume() {
		//bm.display(userImg,lh.getHeadImg());
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageForEmptyUri(R.drawable.default_head)         //没有图片资源时的默认图片  
        .showImageOnFail(R.drawable.default_head)              //加载失败时的图片  
        .cacheInMemory()                               //启用内存缓存  
        .cacheOnDisc()                                 //启用外存缓存  
        .build();

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(lh.getHeadImg(),userImg,options);
		
		userName.setText(lh.getNickname());
		super.onResume();
	}

	public class userHeadClick implements OnClickListener{
		public void onClick(View v) {
			Intent it = null;
			if(lh.hasLogin())
				it = new Intent(User.this,UserInfo.class);
			else
				it = new Intent(User.this,Login.class);
			startActivity(it);
		}
	}

	public void onClick(View v){
		Intent it = null;
		switch (v.getId()) {
		case R.id.btn_about:
			it = new Intent(User.this,About.class);
			break;
		case R.id.btn_feedback:
			it = new Intent(User.this,Suggest.class);
			break;
		case R.id.btn_check_update:
			VersionHelper vh=new VersionHelper(User.this);
			if(vh.checkUpdate()){
				vh.updateTip();
			}else{
				T.showShort(User.this, "已经是最新版本");
			}
			break;
		case R.id.btn_info_me:
			if(lh.hasLogin())
				it = new Intent(User.this,UserInfo.class);
			else
				it = new Intent(User.this,Login.class);
			break;
		case R.id.btn_collect:
			it = new Intent(User.this,MyCollection.class);
			break;
		/*case R.id.btn_notice:
			it = new Intent(User.this,Notice.class);
			break;*/
		case R.id.btn_intro:
			AndroidShare as = new AndroidShare(User.this,"小伙伴们，快来使用长大校园通吧！超好用","");
			as.show();
			break;
		default:
			break;
		}
		if(it!=null)
		startActivity(it);
	}

}
