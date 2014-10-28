
package com.chuxin.family.parse;

import org.json.JSONObject;

import com.chuxin.family.parse.been.CxPollingMessageStatus;
import com.chuxin.family.parse.been.data.CxPollingDataField;
import com.chuxin.family.utils.CxLog;

public class CxPollingMessageStatusParser {

    private static final String TAG = "RkPollingMessageStatusParser";

    public CxPollingMessageStatus parse(JSONObject obj) throws Exception {
        if (null == obj) {
            return null;
        }

        CxPollingMessageStatus message = new CxPollingMessageStatus();
        int rc = obj.getInt("rc");
        message.setRc(rc);
        if (0 != rc) { // 不正常的情况到此为止
            return message;
        }

        try {
            int ts = obj.getInt("ts");
            message.setTs(ts);
            if (!obj.isNull("msg")) {
                String msg = obj.getString("msg");
                message.setMsg(msg);
            }
        } catch (Exception e) {
        }

        JSONObject tempObj = obj.getJSONObject("data");
        if (null == tempObj) { // 也属于非正常情况
            return message;
        }

        CxPollingDataField tempDataField = new CxPollingDataField();
        try {
            int tempPair = tempObj.getInt("pair");
            tempDataField.setPair(tempPair);
        } catch (Exception e) {
            CxLog.e(TAG, "pair" + e.getMessage());
        }
        try {
            int tempMatch = tempObj.getInt("match");
            tempDataField.setMatch(tempMatch);
        } catch (Exception e) {
            CxLog.e(TAG, "match" + e.getMessage());
        }
        try {
            int tempSpace_ts = tempObj.getInt("space_ts");
            tempDataField.setSpace_ts(tempSpace_ts);
        } catch (Exception e) {
            CxLog.e(TAG, "space_ts" + e.getMessage());
        }
        try {
            int tempChat_ts = tempObj.getInt("chat_ts");
            tempDataField.setChat_ts(tempChat_ts);
        } catch (Exception e) {
            CxLog.e(TAG, "chat_ts" + e.getMessage());
        }
        try {
            int tempEdit_ts = tempObj.getInt("edit_ts");
            tempDataField.setEdit_ts(tempEdit_ts);
        } catch (Exception e) {
            CxLog.e(TAG, "edit_ts" + e.getMessage());
        }
        try {
            int tempRts = tempObj.getInt("rts");
            tempDataField.setRts(tempRts);
        } catch (Exception e) {
            CxLog.e(TAG, "rts" + e.getMessage());
        }

        try {
            int group = tempObj.getInt("group");
            tempDataField.setGroup(group);
        } catch (Exception e) {
            CxLog.e(TAG, "group" + e.getMessage());
        }

        try {
            int space_tips = tempObj.getInt("space_tips");
            tempDataField.setSpace_tips(space_tips);
        } catch (Exception e) {
            CxLog.e(TAG, "space_tips：" + e.getMessage());
        }

        try {
            int single_mode = tempObj.getInt("single_mode");
            tempDataField.setSingle_mode(single_mode);
        } catch (Exception e) {
            CxLog.e(TAG, "single_mode：" + e.getMessage());
        }

        try {
            int tempCals = tempObj.getInt("calendar_ts");
            tempDataField.setCalendar_ts(tempCals);
        } catch (Exception e) {
            CxLog.e(TAG, "calendar_ts" + e.getMessage());
        }
        
        try {
        	int kid_tips = tempObj.getInt("child_tips");
        	tempDataField.setKid_tips(kid_tips);
        } catch (Exception e) {
        	CxLog.e(TAG, "kid_tips" + e.getMessage());
        }

        message.setData(tempDataField);

        return message;
    }

}
