package com.chuxin.family.mate;

import org.json.JSONObject;

import com.chuxin.family.parse.CxEmotionParser;
import com.chuxin.family.parse.CxUserInfoParse;
import com.chuxin.family.parse.been.CxEmotionConfigList;
import com.chuxin.family.parse.been.CxFamilyInfoData;
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

public class CxFamilyInfoCacheData extends SQLiteOpenHelper {

	private static final String sDbName = "FamilyInfoCacheData";
	private static final int sVersion = 1;
	private static final String sTableName = "tableName";
	private static final String sDataUid = "uid";
	private static final String sDataContent = "content";
	
	private Context context;
	
	
	public CxFamilyInfoCacheData(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);		
	}
	
	public CxFamilyInfoCacheData(Context context){
		super(context, sDbName, null, sVersion);	
		this.context=context;
	}
	
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlStr = "create table "+ sTableName +" ( " 
		+BaseColumns._ID + " integer primary key autoincrement, "
		+sDataUid+ " text, "
		+sDataContent + " text"
		+" )";
		db.execSQL(sqlStr);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sqlStr = " drop table if exists "+ sTableName;
		db.execSQL(sqlStr);
		onCreate(db);

	}
	
	
	/**
	 * 存储网络返回的帖子列表的data部分(只保存一页数据，android暂定15条为最大缓存数据）
	 * @param jsonStr
	 * @return
	 */
	public synchronized boolean insertData(String uid,String jsonStr){
		if (TextUtils.isEmpty(jsonStr)) {
			return false;
		}
//		uid="abc";//现在暂时默认为abc  
		//版本合并   uid 不再取默认值   因为一个手机上的应用也不会有太多人用 故不会太多数据冗余
		
		CxLog.i("insertNbData_men", uid+">>>>>>>"+jsonStr);
		//先删除以前的
		SQLiteDatabase dataBase = this.getWritableDatabase();
		long rowId = -1;
		try {
			dataBase.delete(sTableName,sDataUid + " = ?", new String[]{uid});
			
			//再插入新数据
			ContentValues values = new ContentValues();
			values.put(sDataUid, uid);
			values.put(sDataContent, jsonStr);
			rowId = dataBase.insert(sTableName, null, values);
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
	public CxFamilyInfoData queryCacheData(String uid){
		
		
		if (TextUtils.isEmpty(uid)) {
			return null;
		}
		
		String selection = sDataUid+" = ? ";
		String []selectArgs = {uid};
		SQLiteDatabase base = this.getReadableDatabase();
		Cursor dataCursor = base.query(true, sTableName,
				null, selection, selectArgs, null, null, null, null);
		
		if ( (null == dataCursor) || (dataCursor.getCount() < 1) ) {
			dataCursor.close();
			base.close();
			return null;
		}
		dataCursor.moveToFirst();
		String feedsStr = dataCursor.getString(dataCursor.getColumnIndex(sDataContent));
		
		dataCursor.close();
		base.close();
		if (TextUtils.isEmpty(feedsStr)) {
			return null;
		}
//		RkLog.i("men", feedsStr);
		CxUserInfoParse cacheDataParser = new CxUserInfoParse();
		CxFamilyInfoData list = null;
		try {			
			list = cacheDataParser.getFamilyInfo( new JSONObject(feedsStr),context,true);
		} catch (Exception e) {
		}
		return list;
	}
	

}
