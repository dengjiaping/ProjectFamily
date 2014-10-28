package com.chuxin.family.net;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.pair.CxPairRequest;
import com.chuxin.family.parse.CxPairParser;
import com.chuxin.family.parse.been.CxCall;
import com.chuxin.family.parse.been.CxPairApprove;
import com.chuxin.family.parse.been.CxPairInit;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.data.CxPairApproveData;
import com.chuxin.family.service.CxServiceParams;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;

/**
 * 配对相关的网络请求,一共4个接口init, invite, approve, dismiss，全部post请求
 * @author shichao
 *
 */
public class CxPairApi extends ConnectionManager {

	private static CxPairApi mPairApi;
//	private final String mInitPath = HttpApi.HTTP_SERVER_PREFIX + "/Pair/init";
//	private final String mInvitePath = HttpApi.HTTP_SERVER_PREFIX + "/Pair/invite";
	private final String mApprovePath = HttpApi.HTTP_SERVER_PREFIX + "/Pair/approve";
	private final String mDismissPath = HttpApi.HTTP_SERVER_PREFIX + "/Pair/dismiss";
	private final String mPairCallPath = HttpApi.HTTP_SERVER_PREFIX + "/Pair/call";
	
	private CxPairApi(){};
	
	public static CxPairApi getInstance(){
		if (null == mPairApi) {
			mPairApi = new CxPairApi();
		}
		return mPairApi;
	}
	
	/**
	 * (1)检查"结对标识（手机号）"是否被别人使用过。 如果已使用，中断；如果未使用，绑定
	 * (2)检查是否被邀请过
	 * @param srcPhoneNumber 自己的号码，可以为空（为空的情况是在long polling里面）
	 * @param callback
	 */
	/*public void getInitStatus(String srcPhoneNumber, final JSONCaller callback){
		//对应协议里面的init请求接口，这个接口可以允许srcPhoneNumber为空
		
		JSONCaller tempCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				//对返回数据进行解析
				RkPairParser parser = new RkPairParser();
				RkPairInit pairInit = parser.parseInit(result);
				if ( (null != pairInit) && (null != pairInit.getData())) { //match参数修改
					RkServiceParams param = RkServiceParams.getInstance();
					param.setMatch(pairInit.getData().size());
				}
				callback.call(pairInit);
				return 0;
			}
		};
		if (null == srcPhoneNumber) { //long polling调用
			this.doHttpPost(mInitPath, tempCallback);
			return;
		}
		NameValuePair[] params = {new BasicNameValuePair("src", srcPhoneNumber)};
		this.doHttpPost(mInitPath, tempCallback, params);
	}*/
	
	/**
	 * 增加一条邀请记录(或替换之前的邀请记录),对应invite接口
	 * @param targetPhoneNumber 被邀请方标识
	 * @param callback
	 */
	/*public void addInvite(String targetPhoneNumber, final JSONCaller callback){
		//对应协议里面的invite请求接口
		NameValuePair[] params = {new BasicNameValuePair("target", targetPhoneNumber)};
		JSONCaller tempCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				//对返回数据进行解析
				RkPairParser parser = new RkPairParser();
				RkPairInit pairInvite = parser.parseInit(result);
				
				callback.call(pairInvite);
				return 0;
			}
		};
		this.doHttpPost(mInvitePath, tempCallback, params);
	}*/
	
	/**
	 * 同意结对申请(拒绝不用处理)
	 * @param targetUid 对方的uid
	 */
	public void approveInvite(String targetUid, final JSONCaller callback 
			/*final Context context*/) throws Exception{
		//对应协议里面的approve请求接口
		if ((null == targetUid) || (null == callback) ){
			throw new Exception("target_uid or callback can not null");
		}
		
//		RkLoadingUtil.getInstance().showLoading(RkPairRequest.getInstance().getActivity(), false);
		
		
		NameValuePair[] params = {new BasicNameValuePair("target_uid", targetUid)};

		this.doHttpPost(mApprovePath, callback, params);
		
	}
	
	/**
	 * 解除结对
	 * @param callback
	 */
	public void dismissInvite(final JSONCaller callback){
		//对应协议里面的dismiss请求接口
		JSONCaller requestCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				if (null == result) {
					callback.call(null);
					return -1;
				}
				JSONObject dismissObj = null;
				try {
					dismissObj = (JSONObject)result;
				} catch (Exception e) {
					e.printStackTrace();
					callback.call(-2);
					return -2;
				}
				CxLog.i("RkPairApi_men", dismissObj.toString());
				CxParseBasic dismissResult = new CxParseBasic();
				int rc = -1;
				try {
					rc = dismissObj.getInt("rc");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dismissResult.setRc(rc);
				try {
					dismissResult.setMsg(dismissObj.getString("msg"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					dismissResult.setTs(dismissObj.getInt("ts"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				callback.call(dismissResult);
				return 0;
			}
		};
		this.doHttpPost(mDismissPath, requestCallback);
	}
	
	//新结对协议：获取自己的邀请码或者获取对应邀请码的人
	public void callPair(String code, final JSONCaller callback){
		
		if (TextUtils.isEmpty(code)) { //获取自己的邀请码
			JSONCaller tempCall = new JSONCaller() {
				
				@Override
				public int call(Object result) {
					CxCall parseResult = null;
					try {
						parseResult = CxPairParser.parseForCall(result);
					} catch (Exception e) {
					}
					CxLog.i("RkPairApi_men", result.toString());
					callback.call(parseResult);
					return 0;
				}
			};
			this.doHttpPost(mPairCallPath, tempCall);
			return;
		}
		//以下是获取对应邀请码的人
		NameValuePair[] params = {new BasicNameValuePair("code", code)};
		JSONCaller tempCall = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				CxCall parseResult = null;
				try {
					parseResult = CxPairParser.parseForCall(result);
				} catch (Exception e) {
				}
				CxLog.i("RkPairApi_men", result.toString());
				callback.call(parseResult);
				return 0;
			}
		};
		this.doHttpPost(mPairCallPath, tempCall, params);
	}
	
}
