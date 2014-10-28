package com.chuxin.family.tabloid;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.net.CxTabloidApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.TabloidParse;
import com.chuxin.family.parse.been.CxTabloid;
import com.chuxin.family.parse.been.CxTabloidCateConf;
import com.chuxin.family.utils.CxLog;

/**
 * 用户每日首次打开应用时，先调用此类获取小报数据
 * 说明：这里面的代码跟RKTabloidActivity.java相似，由于时间急，就先拷贝一份单写。不再在代码中做区分了。以后有空最后修改一下
 *
 * @author dujy
 */
public class TabloidDataProcess {
    private String TAG = "TabloidDataProcess";
    private Context mContext;

    public TabloidDataProcess(Context context) {
        this.mContext = context;
    }


    /**
     * 用户每天第一次打开应用时，去拉取数据
     *
     * @return true : 今天的下一次打开，将不再执行本操作
     * false: 今天的下一次打开，还会再执行本操作
     */
    public boolean getDataFromServerAtFirstTimeEveryday() {
        // 如果用户未登陆，则不做任何事
//		if(!RkGlobalParams.getInstance().isLogin()){
//			RkLog.e(TAG, "用户还未登陆!");
//			return false;
//		}

        CxLog.d(TAG, "应用今天首次打开, 触发获取小报数据!");
        CxTabloidApi tabloidApi = new CxTabloidApi();
        // 获取本地缓存的数据
        TabloidDao dao = new TabloidDao(mContext);
        String version = dao.getVersion();
        version = "0";
        CxLog.i("TabloidDataProcess_men", version);
        tabloidApi.getCategoryConfig(getCateConfCaller, version);    // 拉取配置数据

        return true;
    }

    public JSONCaller getCateConfCaller = new JSONCaller() {
        @Override
        public int call(Object result) {
            CxTabloidCateConf cateConf = null;
            try {
                cateConf = new TabloidParse().parseCateConf(result);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cateConf == null) {
                return -2;
            }
            CxLog.i("TabloidDataProcess_men", result.toString());
            // 请求成功
            if (cateConf.getRc() == 0) {

                // 更新DB (跟库中的版本进行对比，只有版本更新了，才需要更新里面的配置数据)
                TabloidDao dao = new TabloidDao(mContext);
                String v2 = dao.getVersion();

                if (!cateConf.getData().getVersion().equals(v2)) {
                    // 更新全局配置(先删除，再更新)
                    dao.delPubConfig();
                    dao.insertPubConfig(cateConf.getData());

                    // 先删除所有的配置，然后再添加新获取的数据
                    dao.delAllCateConf();
                    dao.insertCateConfBatch(cateConf.getData().getConfig());
                }

                getTabloidDataFromServer();                                                    // 拉取小报数据

                /**
                 *  处理定时
                 *    说明：
                 *        有两个场景有用，其它场景属于重复定义闹钟，但新闹钟会覆盖过去的定义
                 *        1. 新用户第一次登陆进来
                 *        2.用户杀死应用后，几天后再次进来
                 */
                DateChangeReceiver dcr = new DateChangeReceiver();
                dcr.setTabloidReminder(mContext);
            }
            return 0;
        }
    };

    /**
     * 从服务端获取小报数据
     */
    public void getTabloidDataFromServer() {
        CxTabloidApi tabloidApi = new CxTabloidApi();
        TabloidDao dao = new TabloidDao(mContext);
        String[] arr = dao.getCategorieIdsAndTabloidIds();
        String categorie_ids = arr[0];
        String tabloid_ids = arr[1];

        CxLog.d(TAG, "categorie_ids:" + categorie_ids + "    tabloid_ids:" + tabloid_ids);
        tabloidApi.getCategoryList(getTabloidDataCaller, categorie_ids, tabloid_ids);
    }

    public JSONCaller getTabloidDataCaller = new JSONCaller() {
        @Override
        public int call(Object result) {
            CxTabloid tabloidData = null;
            try {
                tabloidData = new TabloidParse().parseTabloid(result);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (tabloidData == null) {
                return -2;
            }
            CxLog.i("TabloidDataProcess_men", result.toString());
            // 请求成功
            if (tabloidData.getRc() == 0) {

                // 将数据插入到库中
                TabloidDao dao = new TabloidDao(mContext);
                dao.insertTabloidBatch(tabloidData.getDataList());


                // 触发定时
                Intent intent;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    intent = new Intent("android.intent.action.DATE_CHANGED");
                } else {
                    intent = new Intent(CxGlobalConst.ACTION_TABLOID_RECIVER);
                }
                mContext.sendBroadcast(intent);
            }
            return 0;
        }
    };

}
