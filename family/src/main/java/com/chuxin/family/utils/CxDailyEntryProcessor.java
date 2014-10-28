package com.chuxin.family.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public final class CxDailyEntryProcessor extends AsyncTask<Object, Integer, Integer> {

	private Context mCtx;

	private DailyEventInterface mEveInterface;
	
	public CxDailyEntryProcessor(Context ctx, 
			DailyEventInterface eveInterface) throws Exception{
		if ((null == ctx) || (null == eveInterface)) {
			throw new Exception("any one of ctx or eveInterface can not be null");
		}
		this.mCtx = ctx;
		this.mEveInterface = eveInterface;
	}
	
	@Override
	protected Integer doInBackground(Object... params) {
		SharedPreferences sp = mCtx.getSharedPreferences("DayLoad", Context.MODE_PRIVATE);
		String dateStr = sp.getString("date", null);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currDateStr = sdf.format(new Date());
		if (currDateStr.equalsIgnoreCase(dateStr)) {
			return 1;
		}
		
		boolean res = false;
		try {
			res = mEveInterface.doDailyEvent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!res) {
			return -1;
		}
		Editor edt = sp.edit();
		edt.putString("date", currDateStr);
		edt.commit();
		return 0;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		
		super.onPreExecute();
	}
	
	public interface DailyEventInterface{
		public abstract boolean doDailyEvent(); //如果成功返回true,如果失败返回false
	}

}
