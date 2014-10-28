package com.chuxin.family.app;

import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.tabloid.StartSelfReceiver;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.chuxin.family.R;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.IOException;

/**
 * @author shichao.wang
 */
public class CxApplication extends Application {
    private static CxApplication app;

    public static CxApplication getInstance() {
        return app;
    }

    private static Resources appResources;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
        app = this;
        appResources = getResources();
        CxGlobalParams.getInstance().setVersionName(getString(CxResourceString.getInstance().str_version_name));
        ApplicationInfo appInfo;
        try {
            appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            CxGlobalParams.getInstance().setCid(appInfo.metaData.getString("InstallChannel"));
        } catch (NameNotFoundException e2) {
            e2.printStackTrace();
        }

        try {
            CxGlobalParams.getInstance().setScale(
                    getResources().getDisplayMetrics().scaledDensity);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            CxGlobalParams.getInstance().setSmallImgConner(
                    (int) getResources().getDimension(R.dimen.cx_fa_my_small_img_conner));
            CxGlobalParams.getInstance().setMateSmallImgConner(
                    (int) getResources().getDimension(R.dimen.cx_fa_mate_small_img_conner));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            CxGlobalParams.getInstance().setClientVersion(info.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }

//		String packageName = RkApplication.this.getPackageName();
//		int gender = -1; //版本性别，性别 （可选） 1：男性 0：女性 -1：未设置(2013.09.18余志伟说文档和协议反了，android改）
//		if (TextUtils.equals(packageName, "com.chuxin.family.husband")) {
//			gender = 1;
//		}else if (TextUtils.equals(packageName, "com.chuxin.family.wife")) {
//			gender = 0;
//		}
//		RkGlobalParams.getInstance().setVersion(gender);

        try {
            DisplayMetrics metric = getApplicationContext().getResources().getDisplayMetrics();
            CxGlobalParams.getInstance().setWidth(metric.widthPixels);
            CxGlobalParams.getInstance().setHeight(metric.heightPixels);
        } catch (Exception e) {
            e.printStackTrace();
        }

//		ExceptionCatcher catcher = new ExceptionCatcher();
//		Thread.setDefaultUncaughtExceptionHandler(catcher);
        /**
         * 这里的StrictMode有问题
         */
//        if (CxGlobalConst.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
//        }

        super.onCreate();

            StartSelfReceiver.startSelf(this, null);

        initImageLoader(getApplicationContext());

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    public String findStringById(int stringId) {
        String str = "";
        str = appResources.getString(stringId);
        return str;

    }

    @Override
    public void onTerminate() {
        app = null;
        super.onTerminate();
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public Context getContext() {
        return getApplicationContext();
    }


}
