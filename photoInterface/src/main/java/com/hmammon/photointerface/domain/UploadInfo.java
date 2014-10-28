package com.hmammon.photointerface.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;

/**
 * 上传信息封装
 * Created by Xcfh on 2014/10/17.
 */
public class UploadInfo extends Entity<UploadInfo> {

    private String familyId;
    private String sharedId;
    private String deviceId;
    private File uploadFile;

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
    public UploadInfo beObject(JSONObject json) throws IllegalArgumentException {
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

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public void setSharedId(String sharedId) {
        this.sharedId = sharedId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setUploadFile(File uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getFamilyId() {
        return familyId;
    }

    public String getSharedId() {
        return sharedId;
    }

    public String getDeviceId() {
        return deviceId;
    }

     public File getUploadFile() {
        return uploadFile;
    }
}
