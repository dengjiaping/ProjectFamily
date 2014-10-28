package com.chuxin.family.mate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.parse.CxUserInfoParse;
import com.chuxin.family.parse.been.CxMateProfile;
/**
 * 备忘资料的本地缓存数据
 * @author shichao.wang
 *
 */
public class CxMateProfileCacheData extends SQLiteOpenHelper {
	private static final String sMateProfileDbName = "mateProfileCacheData";
	private static final int sMateProfileDbVersion = 1;
	private static final String sMateProfileTableName = "mateProfileTableName";
	private static final String sMateProfileDataUid = "mateProfileTableUid";
	private static final String sMateProfileDataPairId = "mateProfileTablePairId";
	private static final String sMateProfileDataContent = "mateProfileTableContent";
	

	public CxMateProfileCacheData(Context context) {
		super(context, sMateProfileDbName, null, sMateProfileDbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlStr = "create table "+ sMateProfileTableName +" ( " 
		+BaseColumns._ID + " integer primary key autoincrement, "
		+sMateProfileDataUid+ " text, "
		+sMateProfileDataPairId + " text,"
		+sMateProfileDataContent + " text"
		+" )";
		db.execSQL(sqlStr);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < newVersion) {
			String sqlStr = " drop table if exists "+ sMateProfileTableName;
			db.execSQL(sqlStr);
			onCreate(db);
		}
	}
	
	/**
	 * 存储网络返回的备忘资料
	 * @param jsonStr
	 * @return
	 */
	public boolean insertMateProfile(String jsonStr){
		if (TextUtils.isEmpty(jsonStr)) {
			return false;
		}
		//先删除以前的
		SQLiteDatabase dataBase = this.getWritableDatabase();
		dataBase.delete(sMateProfileTableName, null, null);
		
		//再插入新数据
		ContentValues values = new ContentValues();
		values.put(sMateProfileDataPairId, CxGlobalParams.getInstance().getPairId());
		values.put(sMateProfileDataUid, CxGlobalParams.getInstance().getUserId());
		values.put(sMateProfileDataContent, jsonStr);
		long rowId = dataBase.insert(sMateProfileTableName, null, values);
		dataBase.close();
		if (-1 == rowId) {
			return false;
		}
		return true;
	}
	
	/**
	 * 返回网络返回存储的帖子列表的data部分(数据库只保存一屏的数据）
	 * @return
	 */
	public CxMateProfile queryCacheData(){
		if (TextUtils.isEmpty(CxGlobalParams.getInstance().getUserId()) 
				|| TextUtils.isEmpty(CxGlobalParams.getInstance().getPairId())) {
			return null;
		}
		String selection = sMateProfileDataUid+" = ? and " + sMateProfileDataPairId + " = ?";
		String []selectArgs = {CxGlobalParams.getInstance().getUserId(), CxGlobalParams.getInstance().getPairId()};
		SQLiteDatabase base = this.getReadableDatabase();
		Cursor dataCursor = base.query(true, sMateProfileTableName, 
				null, selection, selectArgs, null, null, null, null);
		
		if ( (null == dataCursor) || (dataCursor.getCount() < 1) ) {
			dataCursor.close();
			base.close();
			return null;
		}
		dataCursor.moveToFirst();
		String strMateProfile = dataCursor.getString(dataCursor.getColumnIndex(sMateProfileDataContent));
		dataCursor.close();
		base.close();
		if (TextUtils.isEmpty(strMateProfile)) {
			return null;
		}
		
		CxMateProfile mateProfile = null;
		try {
			mateProfile = CxUserInfoParse.parseForUserPartnerProfile(strMateProfile);
		} catch (Exception e) {
		}		
		
		return mateProfile;
	}

}
