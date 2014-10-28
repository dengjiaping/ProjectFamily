package com.hmammon.photointerface.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * 查询绑定信息类
 * Created by Xcfh on 2014/10/22.
 */
public class GetDeviceInfo extends Entity<GetDeviceInfo> {

    private String familyId; // 家庭ID

    @Override
    public JSONObject beJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Field[] fields = this.getClass().getDeclaredFields();
        System.out.println("fields = " + fields.length);
        for (Field field : fields) {
            if (String.class.equals(field.getType())) {
                try {
                    jsonObject.put(field.getName(), String.valueOf(field.get(this)));
                } catch (IllegalAccessException e) {
                    jsonObject.put(field.getName(), String.valueOf("null"));
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }

    @Override
    public GetDeviceInfo beObject(JSONObject json) throws IllegalArgumentException {
        if (json == null) throw new IllegalArgumentException("方法不能置入一个空的JSON进行解析");
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (json.has(field.getName())) {
                try {
                    field.set(this, json.getString(field.getName()));
                } catch (IllegalAccessException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }
}
