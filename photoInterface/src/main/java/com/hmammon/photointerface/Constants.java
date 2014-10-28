package com.hmammon.photointerface;

/**
 * 电子相册接口URL
 * Created by Xcfh on 2014/10/17.
 */
public final class Constants {
    public static String URI_BIND;

    public static String URI_UNBIND;

    public static String URI_GETDEV;

    public static String URI_UPLOAD;

    static {
        if (BuildConfig.DEBUG) {
            URI_BIND = "http://192.168.3.133:8080/familyphoto/device/FamilyDevice_binding";

            URI_UNBIND = "http://192.168.3.133:8080/familyphoto/device/FamilyDevice_unbind";

            URI_GETDEV = "http://192.168.3.133:8080/familyphoto/device/FamilyDevice_getDevices";

            URI_UPLOAD = "http://192.168.3.133:8080/familyphoto/file/Upload_upload";
        }
    }
}
