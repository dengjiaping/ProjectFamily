package com.hmammon.photointerface.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * 绑定信息
 * Created by Xcfh on 2014/10/16.
 */
public class BindInfo extends Entity<BindInfo> {

    private String familyId;
    private String deviceId;
    private String nickname;
    private String bindingTime;
    private String bindingUid;

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
    public BindInfo beObject(JSONObject json) throws IllegalArgumentException {
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

    public UnbindInfo beUnbindInfo() {
        UnbindInfo unbindInfo = new UnbindInfo();
        unbindInfo.setFamilyId(familyId);
        unbindInfo.setDeviceId(deviceId);
        return unbindInfo;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBindingTime() {
        return bindingTime;
    }

    public void setBindingTime(String bindingTime) {
        this.bindingTime = bindingTime;
    }

    public String getBindingUid() {
        return bindingUid;
    }

    public void setBindingUid(String bindingUid) {
        this.bindingUid = bindingUid;
    }
}
