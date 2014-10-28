package com.chuxin.family.tabloid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.utils.CxLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Xcfh on 2014/9/24.
 */
public class StartSelfReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


            StartSelfReceiver.startSelf(context, intent);

//        context.sendBroadcast(new Intent(CxGlobalConst.ACTION_TABLOID_RECIVER));
    }

    public static void startSelf(Context context, Intent intent){

        CxLog.i(StartSelfReceiver.class.getSimpleName(), "Action = " + (intent == null ? "By Application" : intent.getAction()));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        intentFilter.addAction(CxGlobalConst.ACTION_TABLOID_RECIVER);
        context.getApplicationContext().registerReceiver(new DateChangeReceiver(), intentFilter);
    }
}
