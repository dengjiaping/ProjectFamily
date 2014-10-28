package com.chuxin.family.views.chat;

import java.util.ArrayList;
import java.util.List;

import com.chuxin.family.global.CxGlobalParams;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class EmoticonDao {

	private EmoticonDaoHelper helper;
	private Context context;
	public static Uri uri = Uri.parse("content://com.itcedar.safe.applock");

	public EmoticonDao(Context context) {
		helper = new EmoticonDaoHelper(context);
		this.context=context;
	}
	
	public Emoticon find(String uid,String name){
		
		Emoticon emoticon=null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("emoticon", null, "emoname=? and uid=?", new String[]{name,uid}, null, null, null);
		if(cursor.moveToNext()){
			emoticon=new Emoticon();
			String emoName=cursor.getString(cursor.getColumnIndex("emoname"));
			emoticon.setEmoName(emoName);

			String isDown=cursor.getString(cursor.getColumnIndex("isdown"));
			emoticon.setIsDown(isDown);
			
			
		}
		cursor.close();
		db.close();
		return emoticon;
	}
	
	

	


	
	public long add(Emoticon emoticon){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
//		System.out.println(emoticon.getEmoName());
		
		
		values.put("emoname", emoticon.getEmoName());
		values.put("isdown", emoticon.getIsDown());
		values.put("uid", CxGlobalParams.getInstance().getUserId());
		values.put("pairid", CxGlobalParams.getInstance().getPairId());

		long id = db.insert("emoticon", null, values);
		db.close();
//		context.getContentResolver().notifyChange(uri, null);
		return id;
	}
	
	public boolean update(Emoticon emoticon){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("isdown", emoticon.getIsDown());
		int affectraw = db.update("emoticon", values, "emoname=? and uid=?", new String[]{emoticon.getEmoName(),CxGlobalParams.getInstance().getUserId()});
		db.close();
		if(affectraw==1){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean delete(String emoname){
		SQLiteDatabase db = helper.getWritableDatabase();
		int affectedrow = db.delete("emoticon", "emoname=?", new String[]{emoname});
		db.close();
		if(affectedrow==1){
//			context.getContentResolver().notifyChange(uri, null);
			return true;
		}else{
			return false;
		}
	}
	
	
	public void clear(){
		SQLiteDatabase db = helper.getWritableDatabase();
		int delete = db.delete("emoticon", null, null);
		db.close();
	}
	
	
	
	
}
