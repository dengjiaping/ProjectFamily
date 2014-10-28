package com.chuxin.family.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.parse.been.CxPairInit;
import com.chuxin.family.parse.been.CxTabloid;
import com.chuxin.family.parse.been.CxTabloidCateConf;
import com.chuxin.family.parse.been.data.CxPairInitData;
import com.chuxin.family.parse.been.data.TabloidCateConfData;
import com.chuxin.family.parse.been.data.TabloidCateConfObj;
import com.chuxin.family.parse.been.data.TabloidData;
import com.chuxin.family.parse.been.data.TabloidObj;
import com.chuxin.family.utils.CxLog;

public class TabloidParse {
	private final String TAG = "TabloidParse";
	
	/**
	 * 获取小报配置接口的网络应答解析
	 * @param obj
	 * @return
	 */
	public CxTabloidCateConf parseCateConf(Object obj){
		if (null == obj) {
			return null;
		}
		CxTabloidCateConf tabloidCateConf = new CxTabloidCateConf();
		JSONObject jObj = (JSONObject)obj;
		int rc = -1;
		try {
			rc = jObj.getInt("rc");
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		if (-1 == rc) {
			return null;
		}
		tabloidCateConf.setRc(rc);
		try {
			String msg = jObj.getString("msg");
			int ts = jObj.getInt("ts");
			tabloidCateConf.setMsg(msg);
			tabloidCateConf.setTs(ts);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		if (0 != rc) {
			return tabloidCateConf;
		}
		
		//解析data字段
		JSONArray tempArray = new JSONArray();
		
		JSONObject dataObj = null;
		
		String version						 = "1";
		int max_amount 				 = 20;
		String fetch_resource_time = "";
		String notification_time 	 = "" ;
		
		try{
			dataObj 						= (JSONObject) jObj.get("data");
			
			tempArray 					= (JSONArray)dataObj.get("config");
			
			version 						= dataObj.getString("version");
			max_amount 				= dataObj.getInt("max_amount");
			fetch_resource_time 	= dataObj.getString("fetch_resource_time");
			notification_time 	 	= dataObj.getString("notification_time") ;
		}catch(Exception e){
			CxLog.e(TAG, "解析json错误, jsonStr:" + obj.toString());
		}
		
		TabloidCateConfData data 				= new TabloidCateConfData();
		List<TabloidCateConfObj> config 	= new ArrayList<TabloidCateConfObj>();
		
		int len = tempArray.length();
		for(int i = 0; i < len; i++){
			TabloidCateConfObj cateConfObj = new TabloidCateConfObj();
			JSONObject tempElement = null;
			try {
				tempElement = (JSONObject)tempArray.get(i);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (null == tempElement) {
				continue;
			}
			try {
				int category_id = tempElement.getInt("category_id");
				cateConfObj.setCategory_id(category_id);;
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String notification_week = tempElement.getString("notification_week");
				cateConfObj.setNotification_week(notification_week);;
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String img = tempElement.getString("img");
				cateConfObj.setImg(img);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String title = tempElement.getString("title");
				cateConfObj.setTitle(title);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String status = tempElement.getString("notification_status");
				cateConfObj.setNotification_status(status);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			config.add(cateConfObj);
		}//end for(i)
		
		data.setConfig(config);
		data.setFetch_resource_time(fetch_resource_time);
		data.setNotification_time(notification_time);
		data.setVersion(version);
		data.setMax_amount(max_amount);
		
		tabloidCateConf.setData(data);
		
		return tabloidCateConf;
	}
	
	
	
	public CxTabloid parseTabloid(Object obj){
		if (null == obj) {
			return null;
		}
		CxTabloid tabloid 	= new CxTabloid();
		JSONObject jObj 	= (JSONObject)obj;
		int rc = -1;
		try {
			rc = jObj.getInt("rc");
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		if (-1 == rc) {
			return null;
		}
		tabloid.setRc(rc);
		try {
			String msg = jObj.getString("msg");
			int ts = jObj.getInt("ts");
			tabloid.setMsg(msg);
			tabloid.setTs(ts);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		if (0 != rc) {
			return tabloid;
		}
		
		//解析data字段
		JSONArray tempArray = new JSONArray();
		JSONArray dataObjArr = null;
		
		List<TabloidData> dataList 	= new ArrayList<TabloidData>();
		
		
		try{
			dataObjArr 	= (JSONArray) jObj.get("data");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		CxLog.d(TAG, "dataObjArr.length:" + dataObjArr.length());
		for(int i=0; i<dataObjArr.length(); i++){
			TabloidData tabloidData 	= new TabloidData();
			
			JSONObject cateDataObj;
			int category_id ;
			
			try {
				cateDataObj 	= (JSONObject)dataObjArr.get(i);
				category_id		= cateDataObj.getInt("category_id");
				tempArray 		= (JSONArray)cateDataObj.get("tabloids");
				
			} catch (JSONException e) {
				CxLog.e(TAG,"json解析异常:" + e.toString());
				continue;
			}
			 					
			CxLog.d(TAG, "tabloids.length:" + tempArray.length());
			for(int j=0; j<tempArray.length();j++){
				TabloidObj tObj = new TabloidObj();
				
				String text 	= "";
				int id 			= -1;
				try {
					JSONObject tempElement = (JSONObject)tempArray.get(j);
					
					text = tempElement.getString("text");
					id 	= tempElement.getInt("id");
				} catch (JSONException e) {
					CxLog.e(TAG, "json解析异常2:" + e.getMessage());
				}
				
				tObj.setId(id);
				tObj.setText(text);
				tabloidData.getTabloids().add(tObj);
			}
			tabloidData.setCategory_id(category_id);
			
			dataList.add(tabloidData);
		}
		
		tabloid.setData(dataList);
		
		return tabloid;
	}
	
}
