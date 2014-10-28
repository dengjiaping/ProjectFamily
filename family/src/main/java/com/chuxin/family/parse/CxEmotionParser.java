package com.chuxin.family.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.parse.been.CxEmotionConfigList;
import com.chuxin.family.parse.been.data.EmotionItem;
import com.chuxin.family.parse.been.data.EmotionList;
import com.chuxin.family.parse.been.data.EmotionSet;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.views.chat.EmotionCacheData;
import com.chuxin.family.R;

public class CxEmotionParser {

	
	
	public CxEmotionConfigList getEmotionConfigResult(Context context,JSONObject obj,int emotionVersion){
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
		
		CxEmotionConfigList config=new CxEmotionConfigList();
		config.setRc(rc);
		
		try {
			if (!obj.isNull("msg")) {
				config.setMsg(obj.getString("msg"));
			}
		} catch (JSONException e) {
		}

		try {
			config.setTs(obj.getInt("ts"));
		} catch (JSONException e) {
		}

		if (0 != rc) {
			return config;
		}
		
		
		JSONObject dataObj = null;
		try {
			dataObj = obj.getJSONObject("data");
		} catch (JSONException e1) {
		}
		if (null == dataObj) {
			return config;
		}	
		
		try {
			if (!dataObj.isNull("url")) {
				config.setUrl(dataObj.getString("url"));
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		// emotion_config部分
		JSONObject emotionObj = null;
		try {
			emotionObj = dataObj.getJSONObject("emotions_config");
		} catch (JSONException e) {
		}
		if(emotionObj==null){
			return config;
		}
		
		EmotionList list=new EmotionList();
		int tempVersion=-1;
		try {
			if (!emotionObj.isNull("version")) {
				list.setVersion(emotionObj.getInt("version"));
				tempVersion=emotionObj.getInt("version");
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		JSONArray emotionArray = null;

		try {
			emotionArray = emotionObj.getJSONArray("emotions");
		} catch (JSONException e1) {
		}
		
		if (null == emotionArray || emotionArray.length()<1) {
			config.setList(list);
			return config;
		}
		
	
		if(emotionVersion<tempVersion){
			//不是最新版则把最新版的配置文件下载到本地
//			String folderName = "chuxin"+File.separator+"emotion";
//	        String fileName = context.getResources().getString(R.string.cx_fa_emotion_config_name);			      
//	        File path =context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//	        File file = new File(path, folderName + File.separator + fileName);
//			
//			try {
//				if(!file.getParentFile().exists()){
//					file.getParentFile().mkdirs();
//					file.createNewFile();
//				}					
//				FileOutputStream fos = new FileOutputStream(file);
//				fos.write(obj.toString().getBytes());
//				fos.close();	
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			
			EmotionCacheData cacheData=new EmotionCacheData(context);
			cacheData.insertNbData(CxGlobalParams.getInstance().getUserId(), tempVersion, obj.toString());
			
		}
	
		ArrayList<EmotionSet> setList=new ArrayList<EmotionSet>();
		
		for(int i=0;i<emotionArray.length();i++){
			JSONObject setObj=null;
			try {
				setObj = emotionArray.getJSONObject(i);
			} catch (JSONException e) {
			}
			if(setObj==null){
				continue;
			}
			
			EmotionSet set=new EmotionSet();
			
			try {
				if(!setObj.isNull("lockCateImage")){
					set.setLockCateImage(setObj.getString("lockCateImage"));
				}
				if(!setObj.isNull("norCateImage")){
					set.setNorCateImage(setObj.getString("norCateImage"));
				}
				if(!setObj.isNull("is_show")){
					set.setShow(setObj.getBoolean("is_show"));
				}
				if(!setObj.isNull("resourceUrl")){
					set.setResourceUrl(setObj.getString("resourceUrl"));
				}
				if(!setObj.isNull("guideImage")){
					set.setGuideImage(setObj.getString("guideImage"));
				}
				if(!setObj.isNull("guideString")){
					set.setGuideString(setObj.getString("guideString"));
				}
				if(!setObj.isNull("imageWidth")){
					set.setImageWidth(setObj.getInt("imageWidth"));
				}
				if(!setObj.isNull("imageHeight")){
					set.setImageHeight(setObj.getInt("imageHeight"));
				}
				if(!setObj.isNull("emoCellSize_width")){
					set.setEmoCellSize_width(setObj.getInt("emoCellSize_width"));
				}
				if(!setObj.isNull("emoCellSize_height")){
					set.setEmoCellSize_height(setObj.getInt("emoCellSize_height"));
				}
				if(!setObj.isNull("isSupportMix")){
					set.setIsSupportMix(setObj.getInt("isSupportMix"));
				}
				if(!setObj.isNull("countPerPage")){
					set.setCountPerPage(setObj.getInt("countPerPage"));
				}
				if(!setObj.isNull("categoryId")){
					set.setCategoryId(setObj.getInt("categoryId"));
				}
				if(!setObj.isNull("version")){
					set.setVersion(setObj.getInt("version"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			JSONArray itemArr=null;
			try {
				itemArr = setObj.getJSONArray("emotions");
			} catch (JSONException e1) {
			}
			
			if (null == itemArr || itemArr.length()<1) {
				continue;
			}
			
			//单个表情解析
			ArrayList<EmotionItem> items=new ArrayList<EmotionItem>();
			for(int j=0;j<itemArr.length();j++){
				JSONObject itemObj=null;
				try {
					itemObj = itemArr.getJSONObject(j);
				} catch (JSONException e1) {
				}				
				if (null == itemObj) {
					continue;
				}
				
				EmotionItem item=new EmotionItem();
				
				try {
					if(!itemObj.isNull("image")){
						item.setImage(itemObj.getString("image"));
					}
					if(!itemObj.isNull("imageName")){
						item.setImageName(itemObj.getString("imageName"));
					}
					if(!itemObj.isNull("type")){
						item.setType(itemObj.getString("type"));
					}
					if(!itemObj.isNull("textTip")){
						item.setTextTip(itemObj.getString("textTip"));
					}
					if(!itemObj.isNull("imageId")){
						item.setImageId(itemObj.getInt("imageId"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				items.add(item);
				
			}		
			set.setItems(items);	
			setList.add(set);
		}
		list.setDatas(setList);
		config.setList(list);	
		return config;
	}
	
	
	
}
