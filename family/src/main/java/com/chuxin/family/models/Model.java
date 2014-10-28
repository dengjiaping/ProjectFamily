package com.chuxin.family.models;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.utils.Globals;
import com.chuxin.family.utils.CxLog;
/**
 * 
 * @author shichao.wang
 *
 */
public class Model {

	private static final String TAG = "Model";
	public String mId;
	public JSONObject mData;
	public Boolean mInsert = false;
	protected String mTable = "?";
	public static Context mContext;

	public int mFlag;

	public String getId() {
		return mId;
	}

	public int getFlag() {
		return mFlag;
	}

	public void setFlag(int mFlag) {
		this.mFlag = mFlag;
	}

	public void init() {
//		Class cls = getClass();
		SQLiteDatabase db = getDatabase();
		try {
		    db.execSQL("create table if not exists "
					+ mTable
					+ " ( `k` varchar(128) primary key, `v` blob, `sort` integer, `flag` integer)");
		} catch (Exception e) {
			 e.printStackTrace();
		} finally {
		    db.close();
		}
	}

	public void put() {
		SQLiteDatabase db = getDatabase();

	    try {
	        db.beginTransaction();
            String sql = "replace into " + mTable
            		+ " (k, v, flag) values (?, ?, ?);";
            CxLog.d(TAG, "mId = " + mId);
            CxLog.d(TAG, "mFlag = " + mFlag);
            CxLog.d(TAG, "sql = " + sql);
            CxLog.d(TAG, "mData = " + mData.toString());
            db.execSQL(sql, new Object[] { mId, mData.toString(), mFlag });

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		db.close();
	}

	   public void update() {
	        SQLiteDatabase db = getDatabase();

	        try {
	            db.beginTransaction();
	            String sql = "update " + mTable
	                    + " set v=? where k=?";
	            CxLog.d(TAG, "sql = " + sql);
	            db.execSQL(sql, new Object[] { mData.toString(), mId });

	            db.setTransactionSuccessful();
	            db.endTransaction();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        db.close();
	    }
	
	public void drop(String id) {
		try {
            SQLiteDatabase db = getDatabase();

            db.beginTransaction();
            String sql = "delete from " + mTable + " where k=?;";
            Log.d("TEST", "sql = " + sql);
            db.execSQL(sql, new String[] { id });
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	public void drop() {
		drop(mId);
	}

	public void dropAll() {
		SQLiteDatabase db = getDatabase();

	    db.beginTransaction();
		String sql = "delete from " + mTable + " where 1=1;";
		Log.d("TEST", "sql = " + sql);
		db.execSQL(sql, new String[] {});
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	public SQLiteDatabase getDatabase() {
	    Globals globals = Globals.getInstance();
		return globals.openOrCreateMyDatabase(mContext);
	}

	public Model get(String id) {
		Class cls = getClass();
		SQLiteDatabase db = getDatabase();
		Cursor cursor = db.query(mTable, new String[] { "v", "flag" }, "k=?",
				new String[] { id }, null, null, null);
		if (!cursor.moveToFirst()) {
			// no data
			if(cursor != null){
				cursor.close();
			}
			if(db != null){
			    db.close();
			}
			return null;
		} else {
			assert (cursor.getCount() == 1);
			String value = cursor.getString(0);
			String flag = cursor.getString(1);
			CxLog.d(TAG, "get by id flag = " + flag);
			Model model = null;
			try {
				model = (Model) cls.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			model.mId = id;
			model.mFlag = Integer.parseInt(flag);
			try {
				model.mData = new JSONObject(value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			model.mInsert = false;
			cursor.close();
//			db.endTransaction();
			db.close();
			return model;
		}
	}

	public List<Model> gets(String where, String[] fields, String order,
			int limit, int offset) {
		Class cls = getClass();
		SQLiteDatabase db = getDatabase();
		Cursor cursor = db.query(mTable, new String[] { "k", "v", "flag" },
				where, fields, null, null, null);
		if (!cursor.moveToFirst()) {
			// no data
			if(cursor != null){
				cursor.close();
			}
			if(db != null){
			    db.close();
			}
			return null;
		} else {
			List<Model> models = new LinkedList<Model>();
			for (int i = 0; i < cursor.getCount(); i++) {
				String key = cursor.getString(0);
				String value = cursor.getString(1);
				String flag = cursor.getString(2);

				Model model = null;
				try {
					model = (Model) (cls.newInstance());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				Log.d(mTable, "gets() fetch id=" + key);
				Log.d(mTable, "gets() fetch flag=" + flag);
				model.mId = key;
				model.mFlag = Integer.parseInt(flag);
				try {
					model.mData = new JSONObject(value);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				model.mInsert = false;

				models.add(model);
				cursor.moveToNext();
			}
			cursor.close();
//			db.endTransaction();
			db.close();
			return models;
		}
	}
	public List<Model> gets(String sql) {
	    Class cls = getClass();
	    SQLiteDatabase db = getDatabase();
	    Cursor cursor = db.rawQuery(sql, null);
	    if (!cursor.moveToFirst()) {
	        // no data
	        if(cursor != null){
	            cursor.close();
	        }
	        if(db != null){
	            db.close();
	        }
	        return null;
	    } else {
	        List<Model> models = new LinkedList<Model>();
	        for (int i = 0; i < cursor.getCount(); i++) {
	            String key = cursor.getString(0);
	            String value = cursor.getString(1);
	            String flag = cursor.getString(3);
	            
	            Model model = null;
	            try {
	                model = (Model) (cls.newInstance());
	            } catch (InstantiationException e) {
	                e.printStackTrace();
	            } catch (IllegalAccessException e) {
	                e.printStackTrace();
	            }
	            Log.d(mTable, "gets() fetch id=" + key);
	            Log.d(mTable, "gets() fetch flag=" + flag);
	            model.mId = key;
	            model.mFlag = Integer.parseInt(flag);
	            try {
	                model.mData = new JSONObject(value);
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	            model.mInsert = false;
	            
	            models.add(model);
	            cursor.moveToNext();
	        }
	        cursor.close();
//	        db.endTransaction();
	        db.close();
	        return models;
	    }
	}
}
