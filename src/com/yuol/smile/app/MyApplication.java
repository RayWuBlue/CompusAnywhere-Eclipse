package com.yuol.smile.app;



import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.yuol.smile.config.PathConfig;
import com.yuol.smile.config.ServerConfig;
import com.yuol.smile.db.SQLHelper;
import com.yuol.smile.utils.FileUtils;

public class MyApplication extends Application {
	
	
	private static MyApplication mApplication;

	private HttpClient httpClient;
	
	private SQLHelper sqlHelper;
	private Gson mGson;
	
	public synchronized static MyApplication getInstance() {
		return mApplication;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		
		//创建根目录
		FileUtils.createPath(PathConfig.BASEPATH);
		
		
		//进行异常捕获
		if(!ServerConfig.DEVELOP_MODE){
			CrashHandler crashHandler = CrashHandler.getInstance();  
	        crashHandler.init(this); 
		}
		
		httpClient = this.createHttpClient();
		initImageLoader(getApplicationContext());
	}
	
	public synchronized Gson getGson() {
		if (mGson == null)
			// 不转换没有 @Expose 注解的字段
			mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
					.create();
		return mGson;
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		shutdownHttpClient();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		shutdownHttpClient();
	}
	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	// 创建HttpClient实例
	private HttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		//schReg.register(new Scheme("https",SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
				params, schReg);

		return new DefaultHttpClient(connMgr, params);
	}
	
	// 关闭连接管理器并释放资源
	private void shutdownHttpClient() {
		if (httpClient != null && httpClient.getConnectionManager() != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	// 对外提供HttpClient实例
	public HttpClient getHttpClient() {
		return httpClient;
	}

	/** 获取Application */
	public static MyApplication getApp() {
		return mApplication;
	}
	
	/** 获取数据库Helper */
	public SQLHelper getSQLHelper() {
		if (sqlHelper == null)
			sqlHelper = new SQLHelper(mApplication);
		return sqlHelper;
	}
}
