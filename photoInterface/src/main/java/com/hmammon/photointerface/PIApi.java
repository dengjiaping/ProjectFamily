package com.hmammon.photointerface;

import android.content.Context;

import com.hmammon.photointerface.domain.BindInfo;
import com.hmammon.photointerface.domain.GetDeviceInfo;
import com.hmammon.photointerface.domain.UnbindInfo;
import com.hmammon.photointerface.domain.UploadInfo;

import java.net.URI;

/**
 * 面向APP的接口调用
 * Created by Xcfh on 2014/10/21.
 */
public class PIApi {
    private static PIApi instance;
    private Context context;

    private PIApi(Context context) {
        this.context = context;
    }

    public static PIApi getInstance(Context context) {
        if (instance == null) instance = new PIApi(context);
        return instance;
    }

    public void bindDevice(BindInfo bindInfo, NetApi.SendListener sendListener) {
        NetApi.getInstace(context).send(bindInfo, URI.create(Constants.URI_BIND), sendListener);
    }

    public void uploadPhoto(UploadInfo uploadInfo, NetApi.SendListener sendListener) {
        NetApi.getInstace(context).upload(uploadInfo, sendListener);
    }

    public void unBindDevice(UnbindInfo unbindInfo, NetApi.SendListener sendListener) {
        NetApi.getInstace(context).send(unbindInfo, URI.create(Constants.URI_UNBIND), sendListener);
    }

    public void getDevice(GetDeviceInfo getDeviceInfo, NetApi.SendListener sendListener) {
        NetApi.getInstace(context).send(getDeviceInfo, URI.create(Constants.URI_GETDEV), sendListener);
    }


}
