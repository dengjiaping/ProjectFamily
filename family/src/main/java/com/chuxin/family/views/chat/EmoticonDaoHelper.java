package com.chuxin.family.views.chat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class EmoticonDaoHelper extends SQLiteOpenHelper {

	public EmoticonDaoHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		
	}
	
	public EmoticonDaoHelper(Context context){
		super(context, "emoticon.db", null, 1);
	}
	
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table emoticon (_id integer primary key autoincrement,emoname varchar(10),isdown varchar(10)," +
				"uid varchar(10),pairid varchar(10))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
