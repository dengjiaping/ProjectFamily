package com.chuxin.family.tabloid;

import com.chuxin.family.global.CxGlobalParams;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class TabloidDaoHelper  extends SQLiteOpenHelper {

	public TabloidDaoHelper(Context context, String name,CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	public TabloidDaoHelper(Context context){
		super(context, "tabloid_" + CxGlobalParams.getInstance().getUserId() + ".db", null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建小报配置
		db.execSQL("create table t_tabloid_config (id integer primary key autoincrement,"
				+ "uid varchar(10),"
				+ "version varchar(10),"				// 配置文件的版本
				+ "max_amount integer,"			// 每个分类最多给多少条数据
				+ "fetch_resource_time varchar(30),"			// 获取时间
				+ "notification_time varchar(30))");				// 提醒时间
		
		// 创建小报分类配置表
		db.execSQL("create table t_tabloid_category (id integer primary key autoincrement,"
				+ "uid varchar(10),"
				+ "category_id integer(10),"					// 分类ID
				+ "version  integer, "								// 此配置文件的版本(此处有冗余，每条数据都加了这么一条。原因是不想另外搞个地方存此数据)
				+ "title varchar(50), "								// 分类名称
				+ "img varchar(200),"								// 图标地址
				+ "notification_week  varchar(30),"		// 提醒日期
				+ "notification_status  varchar(10))");	// 是否提醒
		
		// 创建小报内容表
		db.execSQL("create table t_tabloid(id integer primary key autoincrement,"
				+ "uid varchar(10),"
				+ "category_id integer, "			// 分类ID
				+ "tabloid_id integer, "				// 小报的ID
				+ "content text)"						// 小报内容
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
