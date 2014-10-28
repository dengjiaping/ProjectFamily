package com.chuxin.family.net;
/*package com.chuxin.family.net;

import com.chuxin.family.utils.RkLog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
*//**
 * 密邻网络接口
 * @author shichao
 *
 *//*
public class RkNeighboursApi extends ConnectionManager {

	private final String NEIGHBOR_LIST = HttpApi.HTTP_SERVER_PREFIX + "Group/list"; //密邻列表
	private final String NEIGHBOR_QUERY = HttpApi.HTTP_SERVER_PREFIX + "Group/query"; //密邻列表
	private final String NEIGHBOR_INVITE = HttpApi.HTTP_SERVER_PREFIX + "Group/invite"; //添加密邻

	
	private static RkNeighboursApi mNeighborApi;
	
	private RkNeighboursApi(){};
	
	public static RkNeighboursApi getInstance(){
		if (null == mNeighborApi) {
		    mNeighborApi = new RkNeighboursApi();
		}
		return mNeighborApi;
	}
	*//**
	 * 密邻列表
	 * @param offset 偏移量 缺省0
	 * @param limit  返回数目上限值 缺省10
	 * @param callback 返回数据接口
	 * @param ctx
	 *//*
	
	public void requestNeighborList(final int offset, final int limit, final JSONCaller callback){
	    NameValuePair[] params = {
	            new BasicNameValuePair("offset", "" + offset),
	            new BasicNameValuePair("limit", "" + limit)
	    };
        JSONCaller netCallback = new JSONCaller() {
            
            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject) data;
                try {
                    int rc = result.getInt("rc");
                    if(rc != 0){
                        callback.call(null);
                        return -1;
                    }
                    JSONArray array = result.getJSONArray("data");
                    callback.call(array);
                } catch (JSONException e) {
                    RkLog.e("requestNeighborList", e.getMessage());
                }
                
                return 0;
            }
        };
        this.doHttpGet(NEIGHBOR_LIST, netCallback, params);
	}
	
	public void requestNeighbourQuery(final String id, final JSONCaller callback) throws Exception{
		 if (TextUtils.isEmpty(id)) {
	            throw new Exception("neighbour id can not be null");
	        }
		    NameValuePair[] params = {new BasicNameValuePair("id", id)};
		    
		    JSONCaller netCallback = new JSONCaller(){

	            @Override
	            public int call(Object data) {
	                JSONObject result = (JSONObject)data;
	                try {
	                    int rc = result.getInt("rc");
	                    if(rc != 0 ){
	                        callback.call(null);
	                        return 0;
	                    }
	                    callback.call(result);
	                } catch (JSONException e) {
	                    RkLog.e("requestNeighborQuery", e.getMessage());
	                }
	                return 0;
	            }
		        
		    };
		    this.doHttpGet(NEIGHBOR_QUERY, netCallback, params);
	}
	
	*//**
	 * 密邻添加
	 * @param id 密邻号
	 * @param callback
	 * @throws Exception
	 *//*
	public void requestNeighborInvite(final String id, final JSONCaller callback) throws Exception{
       if (TextUtils.isEmpty(id)) {
            throw new Exception("inivite id can not be null");
        }
	    NameValuePair[] params = {new BasicNameValuePair("id", id)};
	    
	    JSONCaller netCallback = new JSONCaller(){

            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject)data;
                try {
                    int rc = result.getInt("rc");
                    if(rc != 0 ){
                        callback.call(null);
                        return 0;
                    }
                    callback.call(result);
                } catch (JSONException e) {
                    RkLog.e("requestNeighborInvite", e.getMessage());
                }
                return 0;
            }
	        
	    };
	    this.doHttpGet(NEIGHBOR_INVITE, netCallback, params);
	}
}
*/