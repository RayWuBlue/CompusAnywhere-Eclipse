package com.yuol.smile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "database.db";// 数据库名称
	public static final int VERSION = 1;
	
	public static final String TABLE_CHANNEL = "channel";//数据表 
	public static final String TABLE_COLLECT = "collect_tb";//收藏数据表 
	public static final String ID = "id";//
	public static final String NAME = "name";
	public static final String ORDERID = "orderId";
	public static final String SELECTED = "selected";
	private Context context;
	public SQLHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		this.context = context;
	}

	public Context getContext(){
		return context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 创建数据库后，对数据库的操作
		String sql = "create table if not exists "+TABLE_CHANNEL +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ID + " INTEGER , " +
				NAME + " TEXT , " +
				ORDERID + " INTEGER , " +
				SELECTED + " SELECTED)";
		
		String sql2 = "create table if not exists "+TABLE_COLLECT +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ID + " INTEGER , " +
				"news_id" + " INTEGER , " +
				"news_title" + " TEXT , " +
				"news_time" + " TEXT , " +
				"news_click" + " TEXT , " +
				"news_column" + " TEXT , " +
				"news_body" + " TEXT , " +
				"news_image" + " TEXT , " +
				"news_writer" + " TEXT)";
		db.execSQL(sql);
		db.execSQL(sql2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 更改数据库版本的操作
		onCreate(db);
	}

}
