package com.hmammon.photointerface.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * 错误信息类
 * Created by Xcfh on 2014/10/22.
 */
public class ErrorInfo extends Entity<ErrorInfo> {

    private String state; // 错误类型
    private String msg;   // 错误信息

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
    public ErrorInfo beObject(JSONObject json) throws IllegalArgumentException {
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


    public void setState(String state) {
        this.state = state;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getState() {
        return state;
    }

    public String getMsg() {
        return msg;
    }
}
