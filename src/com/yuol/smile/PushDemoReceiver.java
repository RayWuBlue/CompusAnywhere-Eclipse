package com.yuol.smile;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.yuol.smile.activity.Main;
import com.yuol.smile.activity.WebContent;
import com.yuol.smile.service.UpdateService;
import com.yuol.smile.utils.Api;

public class PushDemoReceiver extends BroadcastReceiver {

	Context context;
	private static final int NOTIFY_ID = 0;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Bundle bundle = intent.getExtras();
		Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {

		case PushConsts.GET_MSG_DATA:
			// 获取透传数据
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");
			
			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");

			// smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
			boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
			System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
			
			if (payload != null&&payload.length>0) {
				String data = new String(payload);
				JSONObject jResult = JSONObject.parseObject(data);
				if("update".equals(jResult.getString("action"))){
					setUpNotification("检测到新版本","点击更新到最新版本呢",new Intent(context,Main.class));
					Intent updateIntent = new Intent(context, UpdateService.class); 
	                context.startService(updateIntent);
				}else if("news".equals(jResult.getString("action"))){
					Intent it = new Intent(context,WebContent.class);
					Bundle bd = new Bundle();
					int id = Integer.parseInt(jResult.getString("id"));
					bd.putString("title",jResult.getString("title"));
					bd.putString("url", Api.News.getNewsDetail(id));
					it.putExtras(bd);
					setUpNotification(jResult.getString("title"),jResult.getString("content"),it);
				}
				Log.d("GetuiSdkDemo", "Got Payload:" + data);
			}
			break;
		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
			// 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
			
			break;
		case PushConsts.THIRDPART_FEEDBACK:
			/*String appid = bundle.getString("appid");
			String taskid = bundle.getString("taskid");
			String actionid = bundle.getString("actionid");
			String result = bundle.getString("result");
			long timestamp = bundle.getLong("timestamp");

			Log.d("GetuiSdkDemo", "appid = " + appid);
			Log.d("GetuiSdkDemo", "taskid = " + taskid);
			Log.d("GetuiSdkDemo", "actionid = " + actionid);
			Log.d("GetuiSdkDemo", "result = " + result);
			Log.d("GetuiSdkDemo", "timestamp = " + timestamp);*/
			break;
		default:
			break;
		}
	}
	
	

	/**
	 * 创建通知
	 */
	private void setUpNotification(String title,String content,Intent it) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, title, when);
		// 放置在"正在运行"栏目中
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		

		RemoteViews contentView = new RemoteViews(context.getPackageName(),
				R.layout.part_notification);
		contentView.setTextViewText(R.id.name, title);
		contentView.setTextViewText(R.id.content, content);
		// 指定个性化视图
		mNotification.contentView = contentView;

		// 下面两句是 在按home后，点击通知栏，返回之前activity 状态;
		// 有下面两句的话，假如service还在后台下载， 在点击程序图片重新进入程序时，直接到下载界面，相当于把程序MAIN 入口改了 - -
		// 是这么理解么。。。
		// intent.setAction(Intent.ACTION_MAIN);
		// intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,it, PendingIntent.FLAG_UPDATE_CURRENT);
		// 指定内容意图
		mNotification.contentIntent = contentIntent;	  
		mNotificationManager.notify(NOTIFY_ID, mNotification);
	}
	
}
