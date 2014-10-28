package com.chuxin.family.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.chuxin.family.parse.CxManageAccountParser;
import com.chuxin.family.parse.been.CxLogin;
import com.chuxin.family.parse.been.CxLogoutResponce;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.utils.CxLog;

public class AccountApi extends ConnectionManager {
	private static AccountApi instance = new AccountApi();
	private final static String PATH_ACCOUNT_LOGIN = HttpApi.HTTP_SERVER_PREFIX + "Account/login";
	private final static String PATH_ACCOUNT_REGISTER = HttpApi.HTTP_SERVER_PREFIX +"Account/register";
	
	//add path for logout account by shichao
	private static final String PATH_ACCOUNT_LOGOUT = HttpApi.HTTP_SERVER_PREFIX+"Account/logout";
	
	private static final String ACTIVE_PATH = HttpApi.HTTP_SERVER_PREFIX+"Stat/enable";
	
	private AccountApi() {
	}
	
	public static AccountApi getInstance() {
		return instance;
	}
	
	/**
	 * 注册接口
	 * @param via 册账户的途径：weibo / qq
	 * @param account 第三方uid
	 * @param token 第三方授权token
	 * @param name （可选）用户名称
	 * @param gender （可选）用户性别
	 * @param birth（可选）用户生日
	 * @param client_version（可选）客户端版本
	 * @param lang（可选）客户端语言
	 */
	public void doRegister(String via, String account, String token, 
			String name, String gender, String birth, String client_version,
			String lang, String raw_icon, final JSONCaller call) throws Exception{
		if (TextUtils.isEmpty(via) || TextUtils.isEmpty(account)
				|| TextUtils.isEmpty(token)) {
			throw new Exception("any one param of previous three params can not null");
		}
		//
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("via", via));
		paramList.add(new BasicNameValuePair("account", account));
		paramList.add(new BasicNameValuePair("token", token));
		if (!TextUtils.isEmpty(name)) {
			paramList.add(new BasicNameValuePair("name", name));
		}
		
		if (!TextUtils.isEmpty(gender)) {
			paramList.add(new BasicNameValuePair("gender", gender));
		}
		
		if (!TextUtils.isEmpty(birth)) {
			paramList.add(new BasicNameValuePair("birth", birth));
		}
		
		if (!TextUtils.isEmpty(client_version)) {
			paramList.add(new BasicNameValuePair("client_version", client_version));
		}
		
		if (!TextUtils.isEmpty(lang)) {
			paramList.add(new BasicNameValuePair("lang", lang));
		}
		if (!TextUtils.isEmpty(raw_icon)) {
			paramList.add(new BasicNameValuePair("raw_icon", raw_icon));
		}
		int size = paramList.size();
		NameValuePair[] params = (NameValuePair[])paramList.toArray(new NameValuePair[size]);
		JSONCaller tempCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				CxLog.i("AccountApi_men", "注册："+result.toString());
				CxParseBasic registerParse = null;
				try {
					registerParse = CxManageAccountParser.perseForRegister(result);
				} catch (Exception e) {
					e.printStackTrace();
				}
				call.call(registerParse);
				return 0;
			}
		};
		this.doHttpPost(PATH_ACCOUNT_REGISTER, tempCallback, params);
		
	}
	
	/*登录接口*/
	public void doLogin(String via, String account, String token, int gender,
			final ConnectionManager.JSONCaller callback) throws Exception{
		if ( (null == via) || (null == account) || (null == token) ) {
			throw new Exception("Any login param can not null"); 
		}
//		final String url = PATH_ACCOUNT_LOGIN;
		NameValuePair[] params = { 
			new BasicNameValuePair("via", via),
			new BasicNameValuePair("account", account),
			new BasicNameValuePair("gender", ""+gender),
			new BasicNameValuePair("token", token)
		};
		
		this.doHttpPost(PATH_ACCOUNT_LOGIN, new ConnectionManager.JSONCaller() {
			
			@Override
			public int call(Object data) {
				
				if (null == data) {
					callback.call(null);
					return -1;
				}
				CxLog.i("AccountApi_men", "登录："+data.toString());
				CxLogin login = CxManageAccountParser.parseForLogin(data);
				callback.call(login);
				
				return 0;
			}
		}, params);
	}
	
	/*账号登出，get请求*/
	public void requestLogout(final JSONCaller callback){
		//
		JSONCaller callJsonParse = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				//json解析
				JSONObject tempObj = (JSONObject)result;
				if (null == tempObj) {
					callback.call(null);
				}
				int rc = -1;
				String msg = "";
				try {
					rc = tempObj.getInt("rc");
					msg = tempObj.getString("msg");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				CxLogoutResponce logout = new CxLogoutResponce();
				logout.setMsg(msg);
				logout.setRc(rc);
				callback.call(logout);
				return 0;
			}
		};
		this.doHttpGet(PATH_ACCOUNT_LOGOUT, callJsonParse);
	}
	
	public void sendActiveAction(String token, final JSONCaller callback) throws Exception{
		if ( (null == token) || (null == callback) ){
			throw new Exception("any one of device token or callback can not be null");
		}
		NameValuePair[] params = {new BasicNameValuePair("d", "A" + token)};
		JSONCaller call = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("AccountApi_men", "设备激活："+result.toString());
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
					jObj = null;
					e.printStackTrace();
				}
				
				if (null == jObj) {
					callback.call(null);
					return -2;
				}
				
				try {
					int rc = -1;
					try {
						rc = jObj.getInt("rc");
					} catch (Exception e1) {
						CxLog.i("", ""+e1.toString());
					}
					CxParseBasic reportResult = new CxParseBasic();
					reportResult.setRc(rc);
					try {
						reportResult.setMsg(jObj.getString("msg"));
					} catch (Exception e) {
						CxLog.i("", ""+e.toString());
					}
					try {
						reportResult.setTs(jObj.getInt("ts"));
					} catch (Exception e) {
						CxLog.i("", ""+e.toString());
					}
					callback.call(reportResult);
				} catch (Exception e) {
					CxLog.i("", ""+e.toString());
					callback.call(null);
				}
				
				return 0;
			}
		};
		this.doHttpPost(ACTIVE_PATH, call, params);
	}
	
}
