package com.hmammon.photointerface;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络状态API
 * Created by Xcfh on 2014/10/23.
 */
public class NetStateApi {

    private static NetStateApi instance;
    private Context context;

    private NetStateApi(Context context) {
        this.context = context;
    }

    public static NetStateApi getInstance(Context context) {
        if (instance == null) instance = new NetStateApi(context);
        return instance;
    }


    public NetStateInfo getNetStateInfo() {
        NetStateInfo netStateInfo = new NetStateInfo();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.isAvailable() && networkInfo.isConnected()) {
                netStateInfo.setNetAvailable(true);
                netStateInfo.setNetType(networkInfo.getType());
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    break;
                }
            }
        }
        return netStateInfo;
    }


    public class NetStateInfo {
        private boolean netAvailable = false;
        private int netType;

        public boolean isNetAvailable() {
            return netAvailable;
        }

        public void setNetAvailable(boolean netAvailable) {
            this.netAvailable = netAvailable;
        }

        public int getNetType() {
            return netType;
        }

        public void setNetType(int netType) {
            this.netType = netType;
        }
    }


}
