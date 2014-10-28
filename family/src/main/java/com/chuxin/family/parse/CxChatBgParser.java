package com.chuxin.family.parse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.parse.been.CxChatBgList;
import com.chuxin.family.parse.been.data.ChatBgData;
import com.chuxin.family.parse.been.data.ChatBgItem;
import com.chuxin.family.settings.CxChatBgCacheData;
import com.chuxin.family.utils.CxLog;

public class CxChatBgParser {

	public CxChatBgList getChatBgConfig(JSONObject obj,Context context,boolean isNative){
		
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
		
		CxChatBgList list=new CxChatBgList();
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
		
		
		try {
			if (!dataObj.isNull("flag")) {
				list.setFlag(dataObj.getInt("flag"));
			}
			if (!dataObj.isNull("msg")) {
				list.setMessage(dataObj.getString("msg"));
			}
			if (!dataObj.isNull("url")) {
				list.setUrl(dataObj.getString("url"));
			}
			if (!dataObj.isNull("version")) {
				list.setVersion(dataObj.getString("version"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		JSONObject bgsObj = null;
		try {
			bgsObj = dataObj.getJSONObject("chat_bgs_config");
		} catch (JSONException e) {
		}
		if (null == bgsObj) {			
			return list;
		}
		
		ChatBgData data=new ChatBgData();
		
		try {
			if (!bgsObj.isNull("version")) {
				data.setVersion(bgsObj.getInt("version"));
			}
			if (!bgsObj.isNull("resourceUrl")) {
				data.setResourceUrl(bgsObj.getString("resourceUrl"));
			}			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONArray itemArr=null;
		try {
			itemArr=bgsObj.getJSONArray("bgs");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(itemArr==null || itemArr.length()<1){
			list.setData(data);
			return list;
		}
		ArrayList<ChatBgItem> items=new ArrayList<ChatBgItem>();
		for(int i=0;i<itemArr.length();i++){
			JSONObject itemObj=null;
			try {
				itemObj=itemArr.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(itemObj==null){
				continue;
			}
			
			ChatBgItem item=new ChatBgItem();
			
			try {				
				if(!itemObj.isNull("id")){
					item.setId(itemObj.getInt("id"));
				}
				
				if(!itemObj.isNull("bigImage")){
					item.setBigImage(itemObj.getString("bigImage"));
				}
				if(!itemObj.isNull("thumbnail")){
					item.setThumbnail(itemObj.getString("thumbnail"));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			items.add(item);
		}
		
		try {
			if(!isNative){
				CxLog.i("men", "obj存上了");
				CxChatBgCacheData cacheData=new CxChatBgCacheData(context);
				cacheData.insertNbData(CxGlobalParams.getInstance().getUserId(), bgsObj.getString("resourceUrl"), bgsObj.getInt("version")+"", obj.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		data.setItems(items);
		list.setData(data);
		
		return list;
	}
	
	
	
	
}
