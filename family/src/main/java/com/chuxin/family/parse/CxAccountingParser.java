package com.chuxin.family.parse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.chuxin.family.models.AccountChart;
import com.chuxin.family.models.ChargeHome;
import com.chuxin.family.models.ChargeMonthDetail;
import com.chuxin.family.models.ChargeYearDetail;
import com.chuxin.family.parse.been.CxAccountDetailDayList;
import com.chuxin.family.parse.been.CxAccountDetailMonthList;
import com.chuxin.family.parse.been.CxAccountHomeList;
import com.chuxin.family.parse.been.CxAccountPieList;
import com.chuxin.family.parse.been.data.AccountDetailDayData;
import com.chuxin.family.parse.been.data.AccountDetailMonthData;
import com.chuxin.family.parse.been.data.AccountDetailMonthItem;
import com.chuxin.family.parse.been.data.AccountHomeData;
import com.chuxin.family.parse.been.data.AccountHomeItem;
import com.chuxin.family.parse.been.data.AccountPieItem;
import com.chuxin.family.utils.CxLog;

public class CxAccountingParser {

	
	public CxAccountHomeList getAccountHomeList(JSONObject obj, Context ctx,boolean isNative){
		if (null == obj) {
			return null;
		}
		int rc = -1;
		try {
			rc = obj.getInt("rc");
		} catch (JSONException e) {
		}
		if (-1 == rc) {
			return null;
		}
		
		CxAccountHomeList list=new CxAccountHomeList();
		list.setRc(rc);
		
		try {
			if (!obj.isNull("msg")) {
				list.setMsg(obj.getString("msg"));
			}
			if (!obj.isNull("ts")) {
				list.setTs(obj.getInt("ts"));
			}			
		} catch (JSONException e) {
		}
		
		if (0 != rc) {
			return list;
		}

		JSONObject dataObj = null;
		try {
			dataObj = obj.getJSONObject("data");
		} catch (JSONException e) {
		}
		if (null == dataObj) {			
			return list;
		}
		
		
//		// 访问成功的情况需要存入数据库
		if(!isNative){
			 //chargehomeJsonObject  网络返回的result， 记得要做一个转换
			CxLog.i("men", "obj存上了");
			    ChargeHome chargehome = new ChargeHome(obj, ctx);
			    chargehome.put();  

		}
		
		
		
//		if (0 == offset) { // 只保存第一屏
//			try {
//				RkNbCacheData cache = new RkNbCacheData(ctx);
//				cache.insertNbData("0",obj.toString());
//			} catch (Exception e) {
//			}
//		}
		
		AccountHomeData data=new AccountHomeData();
		
		try {
			if (!dataObj.isNull("month_out")) {
				data.setMonthOut(dataObj.getString("month_out"));
			}
			if (!dataObj.isNull("month_in")) {
				data.setMonthIn(dataObj.getString("month_in"));
			}
			if (!dataObj.isNull("month_surplus")) {
				data.setMonthSurplus(dataObj.getString("month_surplus"));
			}
			if (!dataObj.isNull("year_surplus")) {
				data.setYearSurplus(dataObj.getString("year_surplus"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		JSONArray detailObj=null;
		try {
			detailObj=dataObj.getJSONArray("last_record");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(detailObj==null || detailObj.length()<1){
			return list;
		}
		
		ArrayList<AccountHomeItem> items=new ArrayList<AccountHomeItem>();
		for(int i=0;i<detailObj.length();i++){
			JSONObject itemObj=null;
			try {
				itemObj=detailObj.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(itemObj==null){
				continue;
			}
			
			AccountHomeItem item=new AccountHomeItem();
			try {
			
				item.setDataStr(itemObj.toString());

				if(!itemObj.isNull("author")){
					item.setAuthor(itemObj.getString("author"));
				}
				
				if(!itemObj.isNull("date")){
					item.setDate(itemObj.getString("date"));
				}
				if(!itemObj.isNull("type")){
					item.setType(itemObj.getInt("type"));
				}
				if(!itemObj.isNull("money")){
					item.setMoney(itemObj.getString("money"));
				}
				if(!itemObj.isNull("category")){
					item.setCategory(itemObj.getInt("category"));
				}			
				if(!itemObj.isNull("id")){
					item.setId(itemObj.getString("id"));
				}
				if(!itemObj.isNull("from")){
					item.setFrom(itemObj.getInt("from"));
				}
				if(!itemObj.isNull("desc")){
					item.setDesc(itemObj.getString("desc"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			items.add(item);
		}
		data.setList(items);
		list.setData(data);

		return list;
	}
	
	public AccountHomeItem getAccountItem(String result){
		
		if(TextUtils.isEmpty(result)){
			return null;
		}
		
		JSONObject itemObj=null;
		try {
			itemObj=new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(itemObj==null){
			return null;
		}
		
		AccountHomeItem item=new AccountHomeItem();
		try {
			
			if(!itemObj.isNull("author")){
				item.setAuthor(itemObj.getString("author"));
			}
			
			if(!itemObj.isNull("date")){
				item.setDate(itemObj.getString("date"));
			}
			if(!itemObj.isNull("type")){
				item.setType(itemObj.getInt("type"));
			}
			if(!itemObj.isNull("money")){
				item.setMoney(itemObj.getString("money"));
			}
			if(!itemObj.isNull("category")){
				item.setCategory(itemObj.getInt("category"));
			}			
			if(!itemObj.isNull("id")){
				item.setId(itemObj.getString("id"));
			}
			if(!itemObj.isNull("from")){
				item.setFrom(itemObj.getInt("from"));
			}
			if(!itemObj.isNull("desc")){
				item.setDesc(itemObj.getString("desc"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return item;
	}
	
	
	public CxAccountDetailMonthList getAccountMonths(JSONObject obj,Context context,String date,boolean isNative,int type){
		if (null == obj) {
			return null;
		}
		int rc = -1;
		try {
			rc = obj.getInt("rc");
		} catch (JSONException e) {
		}
		if (-1 == rc) {
			return null;
		}
		
		CxAccountDetailMonthList list=new CxAccountDetailMonthList();
		list.setRc(rc);
		
		try {
			if (!obj.isNull("msg")) {
				list.setMsg(obj.getString("msg"));
			}
			if (!obj.isNull("ts")) {
				list.setTs(obj.getInt("ts"));
			}			
		} catch (JSONException e) {
		}
		
		if (0 != rc) {
			return list;
		}

		JSONObject dataObj = null;
		try {
			dataObj = obj.getJSONObject("data");
		} catch (JSONException e) {
		}
		if (null == dataObj) {			
			return list;
		}
		
		if(!isNative && type==0){
			 //chargehomeJsonObject  网络返回的result， 记得要做一个转换
//			RkLog.i("men", dataObj.toString());
			CxLog.i("men", date+"obj存上了");
			    ChargeYearDetail chargehome = new ChargeYearDetail(obj, date, context);
			    chargehome.put();  

		}
		
		AccountDetailMonthData data=new AccountDetailMonthData();
		
		try {
			if (!dataObj.isNull("out")) {
				data.setOut(dataObj.getString("out"));
			}
			if (!dataObj.isNull("in")) {
				data.setIn(dataObj.getString("in"));
			}
			if (!dataObj.isNull("surplus")) {
				data.setSurplus(dataObj.getString("surplus"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONArray detailObj=null;
		try {
			detailObj=dataObj.getJSONArray("month");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(detailObj==null || detailObj.length()<1){
			list.setData(data);
			return list;
		}
		
		ArrayList<AccountDetailMonthItem> items=new ArrayList<AccountDetailMonthItem>();
		for(int i=0;i<detailObj.length();i++){
			JSONObject itemObj=null;
			try {
				itemObj=detailObj.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(itemObj==null){
				continue;
			}
			
			AccountDetailMonthItem item=new AccountDetailMonthItem();
			try {
				if(!itemObj.isNull("month")){
					item.setMonth(itemObj.getString("month"));
				}
				if(!itemObj.isNull("in")){
					item.setIn(itemObj.getString("in"));
				}
				if(!itemObj.isNull("out")){
					item.setOut(itemObj.getString("out"));
				}
				if(!itemObj.isNull("surplus")){
					item.setSurplus(itemObj.getString("surplus"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			items.add(item);
		}
		data.setList(items);
		list.setData(data);
		return list;
	}
	
	
	
	
	
	
	public CxAccountDetailDayList getAccountDays(JSONObject obj,Context context,String date,boolean isNative,int type){
		if (null == obj) {
			return null;
		}
		int rc = -1;
		try {
			rc = obj.getInt("rc");
		} catch (JSONException e) {
		}
		if (-1 == rc) {
			return null;
		}
		
		CxAccountDetailDayList list=new CxAccountDetailDayList();
		list.setRc(rc);
		
		try {
			if (!obj.isNull("msg")) {
				list.setMsg(obj.getString("msg"));
			}
			if (!obj.isNull("ts")) {
				list.setTs(obj.getInt("ts"));
			}			
		} catch (JSONException e) {
		}
		
		if (0 != rc) {
			return list;
		}

		JSONArray dataArr = null;
		try {
			dataArr = obj.getJSONArray("data");
		} catch (JSONException e) {
		}
		if (null == dataArr || dataArr.length()<1) {			
			return list;
		}
		
		
		if(!isNative && type==0){
			 //chargehomeJsonObject  网络返回的result， 记得要做一个转换
			CxLog.i("men", "obj存上了");
			    ChargeMonthDetail chargehome = new ChargeMonthDetail(obj, date, context);
			    chargehome.put();  

		}
		
		ArrayList<AccountDetailDayData> datas=new ArrayList<AccountDetailDayData>();
		
		
		for(int i=0;i<dataArr.length();i++){
			
			JSONObject dataObj=null;
			try {
				dataObj=dataArr.getJSONObject(i);
			} catch (JSONException e1) {
				e1.printStackTrace();
			};
			
			if(dataObj==null){
				continue;
			}		
			AccountDetailDayData data=new AccountDetailDayData();
			try {
				if (!dataObj.isNull("day")) {
					data.setDate(dataObj.getString("day"));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			JSONArray detailObj=null;
			try {
				detailObj=dataObj.getJSONArray("record");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(detailObj==null || detailObj.length()<1){
				datas.add(data);
				continue;
			}
			
			ArrayList<String> items=new ArrayList<String>();
			for(int j=0;j<detailObj.length();j++){
				JSONObject itemObj=null;
				try {
					itemObj=detailObj.getJSONObject(j);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if(itemObj==null){
					continue;
				}
				
//				AccountHomeItem item=new AccountHomeItem();
//				try {
//					if(!dataObj.isNull("date")){
//						item.setDate(dataObj.getString("date"));
//					}
//					if(!itemObj.isNull("type")){
//						item.setType(itemObj.getInt("type"));
//					}
//					if(!itemObj.isNull("money")){
//						item.setMoney(itemObj.getString("money"));
//					}
//					if(!itemObj.isNull("category")){
//						item.setCategory(itemObj.getInt("category"));
//					}
//					if(!itemObj.isNull("id")){
//						item.setId(itemObj.getString("id"));
//					}
//					if(!itemObj.isNull("from")){
//						item.setFrom(itemObj.getInt("from"));
//					}
//					if(!itemObj.isNull("desc")){
//						item.setDesc(itemObj.getString("desc"));
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
				items.add(itemObj.toString());
			}
			
			data.setList(items);
			datas.add(data);		
		}		
		list.setData(datas);
		return list;
	}
	
	
	
	public CxAccountPieList getAccountPieList(JSONObject obj){
		if (null == obj) {
			return null;
		}
		int rc = -1;
		try {
			rc = obj.getInt("rc");
		} catch (JSONException e) {
		}
		if (-1 == rc) {
			return null;
		}
		
		CxAccountPieList list=new CxAccountPieList();
		list.setRc(rc);
		
		try {
			if (!obj.isNull("msg")) {
				list.setMsg(obj.getString("msg"));
			}
			if (!obj.isNull("ts")) {
				list.setTs(obj.getInt("ts"));
			}			
		} catch (JSONException e) {
		}
		
		if (0 != rc) {
			return list;
		}

		JSONArray dataArr = null;
		try {
			dataArr = obj.getJSONArray("data");
		} catch (JSONException e) {
		}
		if (null == dataArr || dataArr.length()<1) {			
			return list;
		}
		
		
		ArrayList<AccountPieItem> items=new ArrayList<AccountPieItem>();
		for(int i=0;i<dataArr.length();i++){
			JSONObject dataObj=null;
			try {
				dataObj=dataArr.getJSONObject(i);
			} catch (JSONException e1) {
				e1.printStackTrace();
			};
			
			if(dataObj==null){
				continue;
			}		
			AccountPieItem data=new AccountPieItem();
			try {
				if (!dataObj.isNull("type")) {
					data.setType(dataObj.getInt("type"));
				}
				if (!dataObj.isNull("money")) {
					data.setMoney(dataObj.getString("money"));
				}
				if (!dataObj.isNull("category")) {
					data.setCategory(dataObj.getInt("category"));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			items.add(data);
			
		}
		
		
		list.setItems(items);
		
		
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
