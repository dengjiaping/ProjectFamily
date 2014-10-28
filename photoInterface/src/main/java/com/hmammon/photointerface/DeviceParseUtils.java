package com.hmammon.photointerface;

import com.hmammon.photointerface.domain.BindInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 解析查询的设备列表
 * Created by Xcfh on 2014/10/22.
 */
public class DeviceParseUtils {

    private ArrayList<BindInfo> bindInfoArrayList;

    private JSONObject json;

    private int length = 0;

    public DeviceParseUtils(JSONObject json) {
        this.json = json;
        bindInfoArrayList = new ArrayList<>();
        parse();
    }

    private void parse() {
        if (!json.has("data")) return;
        try {
            JSONArray jsonArray = json.getJSONArray("data");
            length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                bindInfoArrayList.add(new BindInfo().beObject(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getLength() {
        return length;
    }

    public BindInfo getBindInfo(int index) {
        return bindInfoArrayList.get(index);
    }

    public BindInfo[] getAllBindInfoes(){
        BindInfo[] bindInfoes = new BindInfo[length];
       return bindInfoArrayList.toArray(bindInfoes);
    }
}
