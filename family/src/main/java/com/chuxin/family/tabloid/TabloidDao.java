package com.chuxin.family.tabloid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.parse.been.data.TabloidCateConfData;
import com.chuxin.family.parse.been.data.TabloidCateConfObj;
import com.chuxin.family.parse.been.data.TabloidData;
import com.chuxin.family.parse.been.data.TabloidObj;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxUserProfileKeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 小报数据库处理类
 * @author dujy
 *
 */
public class TabloidDao {
	private final String TAG 	= "TabloidDao";

	private Context mContext;
	TabloidDaoHelper helper ;
	
	public TabloidDao(Context context) {
		// 如果数据被回收，重获取用户数据
		if(CxGlobalParams.getInstance().getUserId()==null){
			CxUserProfileKeeper upk = new CxUserProfileKeeper();
			upk.readProfile(context);
		}
		
		helper = new TabloidDaoHelper(context);
		
		this.mContext = context;
    }

	/**
	 * 得到当前配置文件的版本
	 * @return
	 */
	public String getVersion(){
		String ver ="0";
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String userId = CxGlobalParams.getInstance().getUserId();
		
		String sql 				 = "select version from t_tabloid_config where uid= "+userId+" limit 1";
		Cursor cursor 			 = db.rawQuery(sql, null);
		
		if(cursor.moveToNext()){
			ver = cursor.getString(cursor.getColumnIndex("version"));
		}
		
		cursor.close();
		db.close();
		
		return ver;
	}
	
	/**
	 * 插入全部配置
	 * @param version
	 * @param max_amount
	 * @param fetch_resource_time
	 * @param notification_time
	 * @return
	 */
	public void insertPubConfig(TabloidCateConfData confData){
			SQLiteDatabase db = helper.getWritableDatabase();
			ContentValues values ;
				
			values = new ContentValues();
			values.put("uid", CxGlobalParams.getInstance().getUserId());
			values.put("version",  						confData.getVersion());
			values.put("max_amount", 				confData.getMax_amount());
			values.put("fetch_resource_time", 	confData.getFetch_resource_time());
			values.put("notification_time", 			confData.getNotification_time());
				
			db.insert("t_tabloid_config", null, values);
				
			db.close();
	}
	
	/**
	 * 得到全局的配置对象
	 *   注意：
	 *        返回的TabloidCateConfData中，只有全局部分的数据，没有每个分类的数据!
	 * @return
	 */
	public TabloidCateConfData getPubConf(){
		// 得到所有的分类ID
		String userId = CxGlobalParams.getInstance().getUserId();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql 				 = "select * from t_tabloid_config where uid= "+userId;
		Cursor cursor 			 = db.rawQuery(sql, null);
		
		TabloidCateConfData conf = null;
		
		if(cursor.moveToNext()){
			conf = new TabloidCateConfData();
			
			 String version 						= cursor.getString(cursor.getColumnIndex("version"));
			 int max_amount 					= cursor.getInt(cursor.getColumnIndex("max_amount"));
			 String fetch_resource_time 	= cursor.getString(cursor.getColumnIndex("fetch_resource_time"));
			 String notification_time 		= cursor.getString(cursor.getColumnIndex("notification_time"));
			 
			 conf.setVersion(version);
			 conf.setMax_amount(max_amount);
			 conf.setFetch_resource_time(fetch_resource_time);
			 conf.setNotification_time(notification_time);
		}
		cursor.close();
		db.close();
		
		return conf;
	}
	
	
	/**
	 * 删除全部配置
	 * @return
	 */
	public void delPubConfig(){
		
		String userId = CxGlobalParams.getInstance().getUserId();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql 				 = "delete  from t_tabloid_config where uid= "+ userId ;
		db.execSQL(sql);
		
		db.close();
		
	}
	
	/**
	 * 批量增加分类配置
	 * @param confObjList : 分类配置列表
	 * @return
	 */
	public void insertCateConfBatch( List<TabloidCateConfObj> confObjList){
		
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values ;
		
		String userId = CxGlobalParams.getInstance().getUserId();
		
		for(TabloidCateConfObj obj : confObjList){
			values = new ContentValues();
			
			values.put("uid", userId);
			values.put("title", 						obj.getTitle());
			values.put("img", 						obj.getImg());
			values.put("category_id", 			obj.getCategory_id());
			values.put("notification_week", obj.getNotification_week());
			values.put("notification_status", obj.getNotification_status());
			
			db.insert("t_tabloid_category", null, values);
		}
		db.close();
		
	}
	
	/**
	 * 得到某个分类对象
	 * @param id
	 * @return
	 */
	public TabloidCateConfObj getCateById(int id){
		TabloidCateConfObj obj = null;
		
		String userId = CxGlobalParams.getInstance().getUserId();
		
		// 得到所有的分类ID
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql 				 = "select * from t_tabloid_category where category_id=" + id +"and uid= "+userId;
		Cursor cursor 			 = db.rawQuery(sql, null);
		if(cursor.moveToNext()){
			obj = new TabloidCateConfObj();
			
			 String title = cursor.getString(cursor.getColumnIndex("title"));
			 String img = cursor.getString(cursor.getColumnIndex("img"));
			 String notification_status = cursor.getString(cursor.getColumnIndex("notification_status"));
			 String notification_week = cursor.getString(cursor.getColumnIndex("notification_week"));
			
			 obj.setCategory_id(id);
			 obj.setTitle(title);
			 obj.setImg(img);
			 obj.setNotification_status(notification_status);
			 obj.setNotification_week(notification_week);
		}
		cursor.close();
		db.close();
		return obj;
	}
	
	/**
	 * 得到库中的分类列表，以及每个分类下还未使用的小报ID（主要供向后端发请求时使用）
	 *   说明：
	 *       这应该是一个service, 为了其它地方使用方便，所以把部分逻辑也放在这处理了
	 * @return
	 * 		arr[0] :  分类的id
	 *     arr[1]：每个分类下对应的未使用的小报的ID
	 */
	public String[] getCategorieIdsAndTabloidIds(){
		String[] arr = new String[2];
		// 得到所有的分类ID
		String userId = CxGlobalParams.getInstance().getUserId();
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql 				 = "select * from t_tabloid_category where notification_status='true' and uid= "+userId;
		Cursor cursor 			 = db.rawQuery(sql, null);
		
		List<Integer> cateIds = new ArrayList<Integer>();
		while(cursor.moveToNext()){
			int category_id = cursor.getInt(cursor.getColumnIndex("category_id"));
			cateIds.add(category_id);
		}
		cursor.close();
		
		// 从库中得到每个分类还未读的最小id
		sql 		= "select  category_id, count(*) num from t_tabloid  where uid= "+ userId+" group by category_id";
		cursor 	= db.rawQuery(sql, null);
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		while(cursor.moveToNext()){
			int category_id 	= cursor.getInt(cursor.getColumnIndex("category_id"));
			int num		 		= cursor.getInt(cursor.getColumnIndex("num"));				// 还剩下多少数据

			map.put(category_id, 	num);				// 要取多少条
		}
		cursor.close();
		db.close();
		
		// 合并（防止有些分类在t_tabloid表中没有）
		for(Integer cateId : cateIds){
			Integer num	 			= map.get(cateId);		// 现在还有多少条
			Integer fetchAmount = 10;							// 要取多少条
			
			if(num==null){
				CxLog.i("TabloidDataProcess_men", ">>>"+fetchAmount);
			}else{
				CxLog.i("TabloidDataProcess_men", num+"");
			}
			
			
			// 小于5条再去服务端取数据
			if(num!=null && num>5){
				continue;
			}
			
			if(num!=null){
				fetchAmount = fetchAmount - num;		// 最多取10条。所以, 要取的条数=10-现在还有多少条
			}

			if(arr[0]==null || arr[0].equals("")){
				arr[0] = String.valueOf(cateId);
				arr[1] = String.valueOf( fetchAmount);
			}else{
				arr[0] = arr[0] + "," + cateId;
				arr[1] = arr[1] + "," + fetchAmount;
			}
			
		}
		return arr;
	}
	
	/**
	 * 得到所有的分类配置
	 * @param flag             类型:  0-全部分类  1-用户预订的分类   2-用户预订的分类，并且今天可以发送的
	 * @return
	 */
	public  List<TabloidCateConfObj> getCateConfList(int flag){
		List<TabloidCateConfObj> list = new ArrayList<TabloidCateConfObj>();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String userId = CxGlobalParams.getInstance().getUserId();
		
		String sql 	= "select * from t_tabloid_category where uid= "+userId;
		if(flag >= 1){
			sql = sql + " and notification_status='true'";
		}
		
		Cursor cursor 	= db.rawQuery(sql, null);
		
		TabloidCateConfObj obj ;
		
		Calendar c = Calendar.getInstance();
		int week = c.get(Calendar.DAY_OF_WEEK);
		
		// 处理周日为得到的星期几与我们的相差一天的问题
		week = week - 1;
		if(week==0){
			week = 7;
		}
		
		while(cursor.moveToNext()){
			obj = new TabloidCateConfObj();
			
			int category_id 					=  cursor.getInt(cursor.getColumnIndex("category_id"));
			String title							= cursor.getString(cursor.getColumnIndex("title"));
			String img							= cursor.getString(cursor.getColumnIndex("img"));
			String notification_week	= cursor.getString(cursor.getColumnIndex("notification_week"));
			String notification_status	= cursor.getString(cursor.getColumnIndex("notification_status"));
			
			// 如果只要今天可以发送的分类(已预定，并且今天可以发送)
			if(flag == 2){
				// 如果今天不能发送，则继续下一条
				if(notification_week.indexOf(String.valueOf(week)) == -1 ){
					continue;		
				}
			}
			
			obj.setCategory_id(category_id);
			obj.setTitle(title);
			obj.setImg(img);
			obj.setNotification_week(notification_week);
			obj.setNotification_status(notification_status);
			
			list.add(obj);
		}
		cursor.close();
		db.close();
		
		return list;
	}
	/**
	 * 删除所有的配置项
	 * @return
	 */
	public void delAllCateConf(){
		String userId = CxGlobalParams.getInstance().getUserId();
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql 				 = "delete  from t_tabloid_category where uid= "+userId;
		db.execSQL(sql);
		
		db.close();
		
	}
	
	/**
	 * 更新某条分类的提醒状态
	 * @param cateId
	 * @param status
	 * @return
	 */
	public void updateNotificationStatus(int cateId, String status){
		String userId = CxGlobalParams.getInstance().getUserId();
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql 				 = "update  t_tabloid_category  set notification_status='" + status + "' where category_id=" + cateId+" and uid= "+userId;
		db.execSQL(sql);
		
		db.close();
	}
	
	/**
	 * 批量增加小报内容
	 * @param CateId        ： 分类ID
	 * @param tabloidList ： 小报数据列表
	 * @return
	 */
	public void insertTabloidBatch( List<TabloidData> tabloidList){
		CxLog.d(TAG, "小报数据开始入库, 共有:" + tabloidList.size() + "个分类数据");
		
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values;
		
		for(TabloidData obj : tabloidList){
			int category_id = obj.getCategory_id();
			//RkLog.e(TAG, "size:" + obj.getTabloids().size() + "  category_id:" + category_id);
			
			for(TabloidObj tObj: obj.getTabloids()){
				values = new ContentValues();
				//RkLog.e(TAG, "category_id:" +category_id + "  tabloid_id:" + tObj.getId() + " content:" + tObj.getText() );
				values.put("uid", CxGlobalParams.getInstance().getUserId());
				values.put("category_id", 	category_id);
				values.put("tabloid_id", 		tObj.getId());
				values.put("content", 		tObj.getText());
				
				db.insert("t_tabloid", null, values);
			}
		}
		db.close();
		
	}
	
	/**
	 * 删除一条小报内容 (已读的需要删除)
	 * @param cateId
	 * @param tandloidId
	 * @return
	 */
	public void delTabloidById(int cateId, int tabloidId){
		String userId = CxGlobalParams.getInstance().getUserId();
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql 				 = "delete from  t_tabloid  where category_id=" + cateId + " and tabloid_id=" + tabloidId +" and uid= "+ userId;
		db.execSQL(sql);
		
		db.close();
		
	}

	/**
	 * 从库中取一条数据发给用户(取某个分类中ID最小的那个)
	 * @param cateId
	 * @return
	 */
	public TabloidObj getTabloidByCateIdForSend(int cateId){
		TabloidObj obj = null;
		String userId = CxGlobalParams.getInstance().getUserId();
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql 				 = "select * from t_tabloid where category_id=" + cateId +" and uid="+ userId + " order by tabloid_id  limit 1";
		Cursor cursor 			 = db.rawQuery(sql, null);
		if(cursor.moveToNext()){
			obj = new TabloidObj();
			
			 Integer tabloid_id = cursor.getInt(cursor.getColumnIndex("tabloid_id"));
			 String text = cursor.getString(cursor.getColumnIndex("content"));
			
			 obj.setId(tabloid_id);
			 obj.setText(text);
		}
		cursor.close();
		db.close();		
				
		return obj;
	}
	
}
