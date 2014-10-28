package com.chuxin.family.zone;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.parse.CxZoneParser;
import com.chuxin.family.parse.been.data.FeedListData;
/**
 * 二人空间的本地缓存数据
 * @author shichao.wang
 *
 */
public class CxZoneCacheData extends SQLiteOpenHelper {
	private static final String sZoneDbName = "zoneCacheData";
	private static final int sZoneDbVersion = 1;
	private static final String sZoneTableName = "zoneTableName";
	private static final String sZoneDataUid = "zoneTableUid";
	private static final String sZoneDataPairId = "zoneTablePairId";
	private static final String sZoneDataContent = "zoneTableContent";
	

	public CxZoneCacheData(Context context) {
		super(context, sZoneDbName, null, sZoneDbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlStr = "create table "+ sZoneTableName +" ( " 
		+BaseColumns._ID + " integer primary key autoincrement, "
		+sZoneDataUid+ " text, "
		+sZoneDataPairId + " text,"
		+sZoneDataContent + " text"
		+" )";
		db.execSQL(sqlStr);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < newVersion) {
			String sqlStr = " drop table if exists "+ sZoneTableName;
			db.execSQL(sqlStr);
			onCreate(db);
		}
	}
	
	/**
	 * 存储网络返回的帖子列表的data部分(只保存一页数据，android暂定15条为最大缓存数据）
	 * @param jsonStr
	 * @return
	 */
	public synchronized boolean insertZoneData(String jsonStr){
		if (TextUtils.isEmpty(jsonStr)) {
			return false;
		}
		//先删除以前的
		SQLiteDatabase dataBase = this.getWritableDatabase();
		long rowId = -1;
		try {
			dataBase.delete(sZoneTableName, null, null);
			
			//再插入新数据
			ContentValues values = new ContentValues();
			values.put(sZoneDataPairId, CxGlobalParams.getInstance().getPairId());
			values.put(sZoneDataUid, CxGlobalParams.getInstance().getUserId());
			values.put(sZoneDataContent, jsonStr);
			rowId = dataBase.insert(sZoneTableName, null, values);
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
	public List<FeedListData> queryCacheData(){
		String uid = CxGlobalParams.getInstance().getUserId();
		String pairId = CxGlobalParams.getInstance().getPairId();
		
		if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(pairId)) {
			return null;
		}
		
		String selection = sZoneDataUid+" = ? and " + sZoneDataPairId + " = ?";
		String []selectArgs = {uid, pairId};
		SQLiteDatabase base = this.getReadableDatabase();
		Cursor dataCursor = base.query(true, sZoneTableName, 
				null, selection, selectArgs, null, null, null, null);
		
		if ( (null == dataCursor) || (dataCursor.getCount() < 1) ) {
			dataCursor.close();
			base.close();
			return null;
		}
		dataCursor.moveToFirst();
		String feedsStr = dataCursor.getString(dataCursor.getColumnIndex(sZoneDataContent));
		dataCursor.close();
		base.close();
		if (TextUtils.isEmpty(feedsStr)) {
			return null;
		}
		CxZoneParser cacheDataParser = new CxZoneParser();
		List<FeedListData> feeds = null;
		try {
			feeds = cacheDataParser.getFeedsContent(feedsStr);
		} catch (Exception e) {
		}
		
		return feeds;
	}

}
