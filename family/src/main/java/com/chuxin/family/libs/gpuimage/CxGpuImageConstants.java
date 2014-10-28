package com.chuxin.family.libs.gpuimage;

import java.io.File;

import android.os.Environment;

public class CxGpuImageConstants {

    public static final String KEY_PICTURE_URI = "pictureUri";

    public static final String sIntentStep1 = "com.chuxin.gpuimage.activityselectphoto";
    public static final String sIntentStep2 = "com.chuxin.gpuimage.activitysyscamera";
    public static final String sIntentStep3 = "com.chuxin.gpuimage.activitycamera";
    public static final String sIntentStep4 = "com.chuxin.gpuimage.activityphotozoom";
    public static final String sIntentStep5 = "com.chuxin.gpuimage.activitygallery";
//    public static final String sIntentStep7 = "com.chuxin.family.photo.ChooeseDeviceActivity";


    public static final int sStep1 = 1;
    public static final int sStep2 = 2;
    public static final int sStep3 = 3;
    public static final int sStep4 = 4;
    public static final int sStep5 = 5;

    public static final int sStep6 = 6;

//    public static final int sStep7 = 7;

    private static final String NOMEDIA = ".nomedia";
    //图片缓存的根目录
    public static final String IMAGE_CACHE_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "chuxin" + File.separator + "image";
}
