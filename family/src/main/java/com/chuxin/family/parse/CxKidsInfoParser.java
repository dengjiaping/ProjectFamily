package com.chuxin.family.parse;

import com.chuxin.family.parse.been.CxKidsInfoData;
import com.chuxin.family.parse.been.data.KidFeedChildrenData;
import com.chuxin.family.utils.CxLog;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * 孩子资料网络数据解析 对应接口/Child/update
 * @author shichao
 *
 */
public class CxKidsInfoParser {
    
    public CxKidsInfoData parserForKidInfo(Object obj, Context ctx, boolean isNative){
        if(null == obj){
            return null;
        }
        JSONObject kidInfoObj = null;
        
        try {
            kidInfoObj = (JSONObject)obj;
        } catch (Exception e) {
            try {
                kidInfoObj = new JSONObject((String)obj);
            } catch (JSONException e1) {
                CxLog.w("", ""+e.toString());
            }
        }
        if(null == kidInfoObj){
            return null;
        }
        int rc = -1;
        try {
            rc = kidInfoObj.getInt("rc");
        } catch (JSONException e) {
            CxLog.w("", ""+e.toString());
        }
        if(-1 == rc){
            return null;
        }
        CxKidsInfoData kidInfoData = new CxKidsInfoData();
        kidInfoData.setRc(rc);
        
        try {
            kidInfoData.setMsg(kidInfoObj.getString("msg"));
        } catch (JSONException e) {
            CxLog.w("", ""+e.toString());
        }
        
        try {
            kidInfoData.setTs(kidInfoObj.getInt("ts"));
        } catch (JSONException e) {
            CxLog.w("", ""+e.toString());
        }
        if(0 != rc){
            return kidInfoData;
        }
        
        JSONObject dataObj = null;
        
        try {
            dataObj = kidInfoObj.getJSONObject("data");
        } catch (JSONException e) {
            CxLog.w("", ""+e.toString());
        }
        if( null == dataObj){
            return kidInfoData;
        }
        KidFeedChildrenData data = new KidFeedChildrenData();
        try {
            if(!dataObj.isNull("id")){
                data.setId(dataObj.getString("id"));
            }
        } catch (JSONException e1) {
            CxLog.w("", ""+e1.toString());
        }
        try {
            if (!dataObj.isNull("avata")){
                data.setAvata(dataObj.getString("avata"));
            }
        } catch (JSONException e) {
            CxLog.w("", ""+e.toString());
        }
        
        try {
            if (!dataObj.isNull("name")){
                data.setName(dataObj.getString("name"));
            }
        } catch (JSONException e) {
            CxLog.w("", ""+e.toString());
        }
        
        try {
            if (!dataObj.isNull("nickname")){
                data.setNickname(dataObj.getString("nickname"));
            }
        } catch (JSONException e) {
            CxLog.w("", ""+e.toString());
        }
        
        try {
            if(!dataObj.isNull("gender")){
                data.setGender(dataObj.getInt("gender"));
            } else {
                data.setGender(-1);
            }
        } catch (JSONException e) {
            CxLog.w("", ""+e.toString());
        }
        
        try {
            if (!dataObj.isNull("note")){
                data.setNote(dataObj.getString("note"));
            }
        } catch (JSONException e) {
            CxLog.w("", ""+e.toString());
        }
        
        try {
            if(!dataObj.isNull("birth")){
                data.setBirth(dataObj.getString("birth"));
            }
        } catch (JSONException e) {
            CxLog.w("", ""+e.toString());
        }
        
        //data部分
        //JSONObject dataObject = null;
        
        try {
            if(!dataObj.isNull("data")){
                data.setData(dataObj.getString("data"));
            }
        } catch (JSONException e) {
            CxLog.w("", ""+e.toString());
        }
        
        kidInfoData.setKidInfo(data);
        
        return kidInfoData;
    }

}
