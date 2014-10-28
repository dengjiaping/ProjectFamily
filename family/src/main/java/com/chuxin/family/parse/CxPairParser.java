package com.chuxin.family.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.parse.been.CxCall;
import com.chuxin.family.parse.been.CxPairApprove;
import com.chuxin.family.parse.been.CxPairInit;
import com.chuxin.family.parse.been.data.CxCallDataField;
import com.chuxin.family.parse.been.data.CxPairApproveData;
import com.chuxin.family.parse.been.data.CxPairInitData;

public class CxPairParser{

	/**
	 * init接口的网络应答解析
	 * @param obj
	 * @return
	 */
	public CxPairInit parseInit(Object obj){
		if (null == obj) {
			return null;
		}
		CxPairInit pairInit = new CxPairInit();
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
		pairInit.setRc(rc);
		try {
			String msg = jObj.getString("msg");
			int ts = jObj.getInt("ts");
			pairInit.setMsg(msg);
			pairInit.setTs(ts);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		if (0 != rc) {
			return pairInit;
		}
		
		//解析data字段
		JSONArray tempArray = null;
		try {
			tempArray = jObj.getJSONArray("data");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if ( (null == tempArray) || ( 0 >= tempArray.length()) ){
			return pairInit;
		}
		
		List<CxPairInitData> data = new ArrayList<CxPairInitData>();
		int len = tempArray.length();
		for(int i = 0; i < len; i++){
			CxPairInitData tempDataField = new CxPairInitData();
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
				String tempIdentifie = tempElement.getString("identifie");
				tempDataField.setIdentifie(tempIdentifie);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String tempUid = tempElement.getString("uid");
				tempDataField.setUid(tempUid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String tempName = tempElement.getString("name");
				tempDataField.setName(tempName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			data.add(tempDataField);
		}//end for(i)
		pairInit.setData(data);
		
		return pairInit;
	}
	
	/**
	 * 解析"增加一条邀请记录"的数据 ,对应invite接口
	 * @param obj
	 * @return
	 */
	public CxPairInit parseInvite(Object obj){
		if (null == obj) {
			return null;
		}
		
		CxPairInit invite = new CxPairInit();
		JSONObject jObj = (JSONObject)obj;
		int rc = -2;
		if(jObj.has("rc")){
			try {
				invite.setRc(jObj.getInt("rc"));
				if (jObj.has("msg")) {
					invite.setMsg(jObj.getString("msg"));
				}
				if (jObj.has("ts")) {
					invite.setTs(jObj.getInt("ts"));
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (-2 == rc) {
			invite.setRc(-2);
		}
		
		return invite;
	}
	
	/**
	 * 同意结对的解析（对应approve接口应答解析）
	 * @param obj
	 * @return
	 */
	public CxPairApprove parseApprove(Object obj){
		JSONObject jObj = (JSONObject)obj;
		if (null == jObj) {
			return null;
		}
		
		if (!jObj.has("rc")) {
			return null;
		}
		int rc = -1;
		CxPairApprove approve = new CxPairApprove();
		try {
			rc = jObj.getInt("rc");
			approve.setRc(rc);
		} catch (Exception e) {
			e.printStackTrace();
			return approve;
		}
		
		try {
			approve.setMsg(jObj.getString("msg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			approve.setTs(jObj.getInt("ts"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		CxPairApproveData data = new CxPairApproveData();
		try {
			JSONObject dataObj = jObj.getJSONObject("data");
			
			data.setPair_id(dataObj.getString("pair_id"));
			data.setPartner_id(dataObj.getString("partner_id"));
			if(!dataObj.isNull("mode")){
				data.setMode(dataObj.getInt("mode"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		approve.setData(data);
		
		return approve;
	}

	public static CxCall parseForCall(Object obj){
		if (null == obj) {
			return null;
		}
		JSONObject jObj = null;
		try {
			jObj = (JSONObject)obj;
		} catch (Exception e) {
		}
		if (null == jObj) {
			return null;
		}
		int rc = -1;
		try {
			rc = jObj.getInt("rc");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (-1 == rc) {
			return null;
		}
		CxCall result = new CxCall();
		result.setRc(rc);
		try {
			result.setMsg(jObj.getString("msg"));
		} catch (JSONException e) {
		}
		try {
			result.setTs(jObj.getInt("ts"));
		} catch (JSONException e) {
		}
		if (0 != rc) {
			return result;
		}
		JSONObject dataObj = null;
		try {
			dataObj = jObj.getJSONObject("data");
		} catch (JSONException e) {
		}
		if (null == dataObj) {
			return result;
		}
		
		CxCallDataField dataField = new CxCallDataField();
		try {
			JSONArray matchsArray = dataObj.getJSONArray("matchs");
			List<CxPairInitData> matchePeople = new ArrayList<CxPairInitData>();
			for(int i = 0; i < matchsArray.length(); i++){
				CxPairInitData singleMathc = new CxPairInitData();
				JSONObject tempObj = (JSONObject)matchsArray.get(i);
				try {
					if (!tempObj.isNull("identifie")) {
						singleMathc.setIdentifie(tempObj.getString("identifie"));
					}
				} catch (Exception e) {
				}
				try {
					if (!tempObj.isNull("name")) {
						singleMathc.setName(tempObj.getString("name"));
					}
					
				} catch (Exception e) {
				}
				try {
					//icon
					if (!tempObj.isNull("icon")) {
						singleMathc.setIcon(tempObj.getString("icon"));
					}
				} catch (Exception e) {
				}
				try {
					if (!tempObj.isNull("uid")) {
						singleMathc.setUid(tempObj.getString("uid"));
					}
				} catch (Exception e) {
				}
				matchePeople.add(singleMathc);
			} //end for
			dataField.setMatchs(matchePeople);
		} catch (JSONException e) {
		}
		
		try {
			if (!dataObj.isNull("invite_code")) {
				dataField.setInvite_code(dataObj.getString("invite_code"));
			}
		} catch (JSONException e) {
		}
		result.setData(dataField);
		
		return result;
	}
	
}
