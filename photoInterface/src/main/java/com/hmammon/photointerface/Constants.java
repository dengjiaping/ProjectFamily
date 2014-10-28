package com.hmammon.photointerface;

/**
 * 电子相册接口URL
 * Created by Xcfh on 2014/10/17.
 */
public final class Constants {
    public static boolean DEBUG = BuildConfig.DEBUG;

    public static String URI_BIND;// 绑定

    public static String URI_UNBIND;// 解绑

    public static String URI_GETDEV;// 获得设备列表

    public static String URI_UPLOAD;// 上传图片

    static {
        if (DEBUG) {
            URI_BIND = "http://192.168.3.133:8080/familyphoto/device/FamilyDevice_binding";

            URI_UNBIND = "http://192.168.3.133:8080/familyphoto/device/FamilyDevice_unbind";

            URI_GETDEV = "http://192.168.3.133:8080/familyphoto/device/FamilyDevice_getDevices";

            URI_UPLOAD = "http://192.168.3.133:8080/familyphoto/file/Upload_upload";
        }
    }
}
