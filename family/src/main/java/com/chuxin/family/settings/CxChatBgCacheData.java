package com.chuxin.family.settings;

import java.util.List;

import org.json.JSONObject;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.parse.CxChatBgParser;
import com.chuxin.family.parse.CxNeighbourParser;
import com.chuxin.family.parse.CxZoneParser;
import com.chuxin.family.parse.been.CxChatBgList;
import com.chuxin.family.parse.been.data.FeedListData;
import com.chuxin.family.parse.been.data.InvitationList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class CxChatBgCacheData extends SQLiteOpenHelper {
	
	private static final String dbName = "cbgCacheData";//cbg为chatbackground 缩写
	private static final int dbVersion = 1;
	private static final String tableName = "cbgTableName";
	private static final String dataUid = "uid";
	private static final String dataPairId = "cbgTablePairId";
	private static final String dataUrl="resourceUrl";
	private static final String dataVersion="version";
	private static final String dataContent = "bgs";
	
	private Context context;

	private CxChatBgCacheData(Context context, String name, CursorFactory factory,
			int version) {
		
		super(context, name, factory, version);
		
	}
	
	public CxChatBgCacheData(Context context){
		
		super(context, dbName, null, dbVersion);	
		this.context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlStr = "create table "+ tableName +" ( " 
		+BaseColumns._ID + " integer primary key autoincrement, "
		+dataUid+ " text, "
		+dataPairId+ " text, "
		+dataUrl + " text,"
		+dataVersion + " text,"
		+dataContent + " text"
		+" )";
		db.execSQL(sqlStr);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sqlStr = " drop table if exists "+ tableName;
		db.execSQL(sqlStr);
		onCreate(db);
	}
	
	

	public synchronized boolean insertNbData(String uid,String resourceUrl,String version,String jsonStr){
		if (TextUtils.isEmpty(jsonStr)) {
			return false;
		}
		//先删除以前的
		SQLiteDatabase dataBase = this.getWritableDatabase();
		long rowId = -1;
		try {
			dataBase.delete(tableName,dataUid + " = ?", new String[]{uid});
			
			//再插入新数据
			ContentValues values = new ContentValues();
			values.put(dataUid, uid);
			values.put(dataUrl, resourceUrl);
			values.put(dataVersion, version);
			values.put(dataContent, jsonStr);
			rowId = dataBase.insert(tableName, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dataBase.close();
		}
		
		if (-1 == rowId) {
			return false;
		}
		return true;
	}
	
	/**
	 * 返回网络返回存储的帖子列表的data部分(数据库只保存一屏的数据）
	 * @return
	 */
	public CxChatBgList queryCacheData(String uid){

		
		if (TextUtils.isEmpty(uid)) {
			return null;
		}
		
		String selection = dataUid+" = ?";
		String []selectArgs = {uid};
		SQLiteDatabase base = this.getReadableDatabase();
		Cursor dataCursor = base.query(true, tableName, 
				null, selection, selectArgs, null, null, null, null);
		
		if ( (null == dataCursor) || (dataCursor.getCount() < 1) ) {
			dataCursor.close();
			base.close();
			return null;
		}
		dataCursor.moveToFirst();
		String feedsStr = dataCursor.getString(dataCursor.getColumnIndex(dataContent));
		dataCursor.close();
		base.close();
		if (TextUtils.isEmpty(feedsStr)) {
			return null;
		}
	
		CxChatBgParser cacheDataParser = new CxChatBgParser();
		CxChatBgList list = null;
		try {
			
			list = cacheDataParser.getChatBgConfig(new JSONObject(feedsStr), context, true);
		} catch (Exception e) {
		}
		return list;
	}
	
	
	
	
	
	public int queryCacheVersion(String uid){
		
		
		if (TextUtils.isEmpty(uid)) {
			return 0;
		}
		
		String selection = dataUid+" = ? ";
		String []selectArgs = {uid};
		SQLiteDatabase base = this.getReadableDatabase();
		Cursor dataCursor = base.query(true, tableName,
				null, selection, selectArgs, null, null, null, null);
		
		if ( (null == dataCursor) || (dataCursor.getCount() < 1) ) {
			dataCursor.close();
			base.close();
			return 0;
		}
		dataCursor.moveToFirst();
		int version = dataCursor.getInt(dataCursor.getColumnIndex(dataVersion));
		dataCursor.close();
		base.close();
	
		return version;
	}
	
	public String queryCacheUrl(String uid){
		
		
		if (TextUtils.isEmpty(uid)) {
			return null;
		}
		
		String selection = dataUid+" = ? ";
		String []selectArgs = {uid};
		SQLiteDatabase base = this.getReadableDatabase();
		Cursor dataCursor = base.query(true, tableName,
				null, selection, selectArgs, null, null, null, null);
		
		if ( (null == dataCursor) || (dataCursor.getCount() < 1) ) {
			dataCursor.close();
			base.close();
			return null;
		}
		dataCursor.moveToFirst();
		String url = dataCursor.getString(dataCursor.getColumnIndex(dataUrl));
		dataCursor.close();
		base.close();
		
		return url;
	}
	
	
	
	
	
	
	
	

}
