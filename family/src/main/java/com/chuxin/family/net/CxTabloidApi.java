package com.chuxin.family.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.utils.CxLog;

/**
 * 我家小报
 * @author dujy
 *
 */
public class CxTabloidApi  extends ConnectionManager {
	private String TAG = "RkTabloidApi";
	
	private final String mTabloidGetConfigPath 		= HttpApi.HTTP_SERVER_PREFIX + "User/tabloid/get_config";				// 得到配置文件
	private final String mTabloidGetNewPath 			= HttpApi.HTTP_SERVER_PREFIX + "User/tabloid/fetch";						// 获取小报内容
	private final String mTabloidForwardPath			= HttpApi.HTTP_SERVER_PREFIX + "User/tabloid/forward";					// 转发统计
	private final String mTabloidUpdateStatusPath =  HttpApi.HTTP_SERVER_PREFIX + "User/tabloid/update_status";		// 更新订阅状态
	
	
	/**
	 * 更新订阅状态
	 * @param callback
	 * @param category_ids	: 分类ID	示例：1,2,3,4 符合正则：[0-9]
	 * @param statuses			: 状态  		示例：1,0,1,0 符合正则：[0-1] #0关闭，1打开
	 */
	public void updateCategoryStatus(final JSONCaller callback , String category_ids, String statuses){
		NameValuePair[] params = { 
				new BasicNameValuePair("category_ids", 	category_ids),
				new BasicNameValuePair("statuses", 			statuses)
		};
		
		CxLog.d(TAG, "开始更新小报分类订阅状态:" + mTabloidUpdateStatusPath);
		this.doHttpGet(mTabloidUpdateStatusPath, callback, params);
	}
	
	
	/**
	 * 得到所有的分类配置信息
	 * @return
	 */
	public void getCategoryConfig(final JSONCaller callback , String version){
		NameValuePair[] params = {  new BasicNameValuePair("version", String.valueOf(version)) };
		
		CxLog.d(TAG, "开始网络请求" + mTabloidGetConfigPath);
		this.doHttpGet(mTabloidGetConfigPath, callback, params);
	}
	
	/**
	 * 得到指定类型的小报列表
	 * @param categorie_ids : 类别ID (字符串) 示例：1,2,3,4 符合正则：[0-9] 0 表示关闭所有订阅
	 * @param amounts:	要取多少条数据 
	 * @return
	 * 		每种类型返回20条数据
	 */
	public void getCategoryList(final JSONCaller callback , String categorie_ids, String amounts){
		
			NameValuePair[] params = {
					new BasicNameValuePair("category_ids", 	categorie_ids),
					new BasicNameValuePair("amounts", 			amounts)
			};
		
			this.doHttpGet(mTabloidGetNewPath, callback, params);
	}
	
	
	/**
	 * 用户转发小报
	 *     说明：这个接口后端主要用于统计，不做任何其它处理
	 * @param callback
	 * @param category_id
	 * @param tabloid_id
	 */
	public void forward(final JSONCaller callback, int category_id, int tabloid_id){
		NameValuePair[] params = {
				new BasicNameValuePair("category_id", String.valueOf(category_id) ),
				new BasicNameValuePair("tabloid_id", 	String.valueOf(tabloid_id) )
		};
		
		this.doHttpGet(mTabloidForwardPath, callback, params);
	}
}
