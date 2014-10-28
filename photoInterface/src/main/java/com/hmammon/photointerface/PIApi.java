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

    /**
     * 绑定设备
     *
     * @param bindInfo     绑定设备的信息
     * @param sendListener 监听接口
     */
    public void bindDevice(BindInfo bindInfo, NetApi.SendListener sendListener) {
        NetApi.getInstace(context).send(bindInfo, URI.create(Constants.URI_BIND), sendListener);
    }

    /**
     * 上传照片
     *
     * @param uploadInfo   上传照片的信息
     * @param sendListener 监听接口
     */
    public void uploadPhoto(UploadInfo uploadInfo, NetApi.SendListener sendListener) {
        NetApi.getInstace(context).upload(uploadInfo, sendListener);
    }

    /**
     * 解绑设备
     *
     * @param unbindInfo   解绑的信息
     * @param sendListener 监听接口
     */
    public void unBindDevice(UnbindInfo unbindInfo, NetApi.SendListener sendListener) {
        NetApi.getInstace(context).send(unbindInfo, URI.create(Constants.URI_UNBIND), sendListener);
    }

    /**
     * 获取设备列表
     *
     * @param getDeviceInfo 获得设备列表需要的信息
     * @param sendListener  监听接口
     */
    public void getDevice(GetDeviceInfo getDeviceInfo, NetApi.SendListener sendListener) {
        NetApi.getInstace(context).send(getDeviceInfo, URI.create(Constants.URI_GETDEV), sendListener);
    }


}
