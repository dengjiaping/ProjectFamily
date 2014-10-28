package com.chuxin.family.neighbour;

import java.util.List;

import org.json.JSONObject;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.parse.CxNeighbourParser;
import com.chuxin.family.parse.CxZoneParser;
import com.chuxin.family.parse.been.data.FeedListData;
import com.chuxin.family.parse.been.data.InvitationList;
import com.chuxin.family.utils.CxLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class CxNbCacheData extends SQLiteOpenHelper {
	
	private static final String sNbDbName = "nbCacheData";
	private static final int sNbbVersion = 1;
	private static final String sNbTableName = "nbTableName";
	private static final String sNbDataUid = "nbTableUid";
	private static final String sNbDataPairId = "nbTablePairId";
	private static final String sNbDataContent = "nbTableContent";
	
	private Context context;

	private CxNbCacheData(Context context, String name, CursorFactory factory,
			int version) {
		
		super(context, name, factory, version);
		
	}
	
	public CxNbCacheData(Context context){
		
		super(context, sNbDbName, null, sNbbVersion);	
		this.context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlStr = "create table "+ sNbTableName +" ( " 
		+BaseColumns._ID + " integer primary key autoincrement, "
		+sNbDataUid+ " text, "
		+sNbDataPairId + " text,"
		+sNbDataContent + " text"
		+" )";
		db.execSQL(sqlStr);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sqlStr = " drop table if exists "+ sNbTableName;
		db.execSQL(sqlStr);
		onCreate(db);
	}
	
	
	
	
	/**
	 * 存储网络返回的帖子列表的data部分(只保存一页数据，android暂定15条为最大缓存数据）
	 * @param jsonStr
	 * @return
	 */
	public synchronized boolean insertNbData(String pairId,String jsonStr){
		if (TextUtils.isEmpty(jsonStr)) {
			return false;
		}
		CxLog.i("insertNbData_men", pairId+">>>>>>>"+jsonStr);
		//先删除以前的
		SQLiteDatabase dataBase = this.getWritableDatabase();
		long rowId = -1;
		try {
			dataBase.delete(sNbTableName,sNbDataPairId + " = ?", new String[]{pairId});
			
			//再插入新数据
			ContentValues values = new ContentValues();
			values.put(sNbDataPairId, pairId);
			values.put(sNbDataUid, CxGlobalParams.getInstance().getUserId());
			values.put(sNbDataContent, jsonStr);
			rowId = dataBase.insert(sNbTableName, null, values);
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
	public InvitationList queryCacheData(String mPairId){
		String uid = CxGlobalParams.getInstance().getUserId();
		String pairId = mPairId;
		
		if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(pairId)) {
			return null;
		}
		
		String selection = sNbDataUid+" = ? and " + sNbDataPairId + " = ?";
		String []selectArgs = {uid, pairId};
		SQLiteDatabase base = this.getReadableDatabase();
		Cursor dataCursor = base.query(true, sNbTableName, 
				null, selection, selectArgs, null, null, null, null);
		
		if ( (null == dataCursor) || (dataCursor.getCount() < 1) ) {
			dataCursor.close();
			base.close();
			return null;
		}
		dataCursor.moveToFirst();
		String feedsStr = dataCursor.getString(dataCursor.getColumnIndex(sNbDataContent));
		dataCursor.close();
		base.close();
		if (TextUtils.isEmpty(feedsStr)) {
			return null;
		}
		CxLog.i("queryCacheData_men", pairId+">>>>>>>"+feedsStr);
		CxNeighbourParser cacheDataParser = new CxNeighbourParser();
		InvitationList list = null;
		try {
			if("0".equals(mPairId)){
				list = cacheDataParser.getNbInvitationList(-1,new JSONObject(feedsStr),context).getData();
			}else{
				list = cacheDataParser.getNbHomeInvitationList(-1,new JSONObject(feedsStr),context,"0").getData();
			}

		} catch (Exception e) {
		}
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
