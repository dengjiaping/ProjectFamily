
package com.chuxin.family.service;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.chuxin.family.R;
import com.chuxin.family.app.CxDialogActivity;
import com.chuxin.family.calendar.CxCalendarFragment;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.kids.CxKidFragment;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.more.CxMoreFragment;
import com.chuxin.family.neighbour.CxNeighbourFragment;
import com.chuxin.family.net.AccountApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.net.LongPollingApi;
import com.chuxin.family.parse.been.CxLogin;
import com.chuxin.family.parse.been.CxPollingMessageStatus;
import com.chuxin.family.parse.been.data.CxPollingDataField;
import com.chuxin.family.parse.been.data.FeedListData;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.views.chat.ChatFragment;
import com.chuxin.family.views.login.CxThirdAccessToken;
import com.chuxin.family.views.login.CxThirdAccessTokenKeeper;
import com.chuxin.family.views.reminder.CxReminderList;
import com.chuxin.family.zone.CxUsersPairZone;
import com.chuxin.family.zone.CxZoneCacheData;

import net.simonvt.menudrawer.CxBaseSlidingMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shichao.wang 备注：1、注意前台和后台的监听 2、注意用户帐号切换和 3、
 */
public class CxBackgroundService extends Service {

    public static boolean started = false; // service默认没有启动

    // private boolean finishPollingFlag = false; //
    // 外部操作使longpolling结束的标志位。默认false不结束polling拉取
    private final String ALERM_ACTION = "alerm_wakeup";

    CxGlobalParams mGlobalParam;

    ServiceObserverGlobal mGlobalObserver;

    public static boolean hasNotified = false; // 结对确认界面是否已经打开

    private DownloadCompleteReceiver receiver;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        started = true;

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetStateReceiver, mFilter);

        /*
         * receiver = new DownloadCompleteReceiver(); registerReceiver(receiver,
         * new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE));
         */
        CxLog.w("", "back service oncreate()");
        Notification notification = new Notification(R.drawable.cx_fa_app_icon,
                "Chuxin Service Started.", System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                CxBackgroundService.class), 0);
        notification.setLatestEventInfo(this, "Chuxin Service", "", contentIntent);
        CxBackgroundService.this.startForeground(0, notification);

        // 设置观察者
        mGlobalParam = CxGlobalParams.getInstance(); // 获取model的subject实例
        mGlobalObserver = new ServiceObserverGlobal(); // 生成观察者实例
        // 设置观察目标
        List<String> tags = new ArrayList<String>();
        tags.add(CxGlobalParams.APP_STATUS); // 应用在前/后台的监听
        tags.add(CxGlobalParams.IS_LOGIN); // 用户登录登出（切换帐号）
        /* 目前暂时对应用状态和用户状态进行监听 */
        mGlobalObserver.setListenTag(tags); // 设置观察目标
        mGlobalObserver.setMainThread(false); // 设置不是UI线程执行update
        mGlobalParam.registerObserver(mGlobalObserver); // 注册观察者

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CxLog.w("", "back service onStart()");
        // 唤醒种类：0表示周期唤醒；1表示开机启动；2表示登录后启动（应用在前台）
        if (null != intent) {
            int wakeType = intent.getIntExtra("source", 0); // 默认为0。
            // 根据启动service的情况决定是否启动longpolling
            if (2 == wakeType) { // 只有登录的情况（应用在前台）开启long polling
                if (CxGlobalParams.getInstance().isAppStatus()) {
                    CxLog.i("longpolling",
                            "%%%%%%%%%%%%%%onStart ready to run long polling task%%%%%%%%%%%");
                    new Thread(pollingTask).start();
                } else {
                    // 执行小报(应用在前台时候不需要执行小报的广播
                    // 触发小报定时
                    Intent newIntent;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        newIntent = new Intent("android.intent.action.DATE_CHANGED");
                    } else {
                        newIntent = new Intent(CxGlobalConst.ACTION_TABLOID_RECIVER);
                    }
                    sendBroadcast(newIntent);
                    CxLog.i("longpolling", "to start xiaobao");
                }
            }
        }

        // 发出周期唤醒
        Intent broadIntent = new Intent(ALERM_ACTION);
        AlarmManager tempAlarm = (AlarmManager) CxBackgroundService.this
                .getSystemService(ALARM_SERVICE);
        tempAlarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, // 暂定1分钟唤醒一次
                PendingIntent.getBroadcast(CxBackgroundService.this, 300, broadIntent, 0));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // finishPollingFlag= true;
        Intent broadIntent = new Intent(ALERM_ACTION);
        AlarmManager tempAlarm = (AlarmManager) CxBackgroundService.this
                .getSystemService(ALARM_SERVICE);
        tempAlarm.cancel(PendingIntent.getBroadcast(CxBackgroundService.this, 300, broadIntent, 0));

        mGlobalParam.unRegisterObsercer(mGlobalObserver); // 注销观察者

        unregisterReceiver(mNetStateReceiver);

        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        super.onDestroy();
    }

    // 初始加载各项数据
    Runnable startTask = new Runnable() {

        @Override
        public void run() {
            if (started) { // 已经初始化了
                return;
            }
            // 1、加载本地提醒
            Message reminderMessage = CxReminderList.mReminderListAllHandler.obtainMessage(0);
            reminderMessage.sendToTarget();
            // 3、非后台模式启动longpolling

            // 4、后台模式需要对push消息进行处理

        }
    };

    private boolean hasLongPolling = false;

    // long polling具体操作
    final Runnable pollingTask = new Runnable() {

        @Override
        public void run() {
            Log.i("longpolling", "entry thread for long polling");

            synchronized (pollingTask) {
                if (hasLongPolling) {
                    CxLog.i("longpolling", "running thread ,so go out long polling");
                    return;
                }
            }
            hasLongPolling = true;
            Log.i("longpolling", "***********longpolling start************");
            CxServiceParams param = CxServiceParams.getInstance();
            // 初始时，加载提醒，聊天，二人空间的初始时间戳
            SharedPreferences mReminderSharedPreferences = getSharedPreferences(
                    CxGlobalConst.S_REMINDER_PREFS_NAME, 0);
            SharedPreferences mCalendarSharedPreferences = getSharedPreferences(
                    CxGlobalConst.S_CALENDAR_PREFS_NAME, 0);

            int firstRts = mReminderSharedPreferences.getInt(
                    CxGlobalConst.S_REMINDER_FIRST_RTS_KEY, 0);
            int firstCals = mCalendarSharedPreferences.getInt(
                    CxGlobalConst.S_CALENDAR_REMIND_UPDATE_TIME, 0);
            if (firstRts != 0) {
                param.setRts(firstRts);
            }
            if (firstCals != 0) {
                param.setCalendarTs(firstCals);
            }

            CxZoneCacheData cacheData = new CxZoneCacheData(CxBackgroundService.this);
            List<FeedListData> feeds = cacheData.queryCacheData();
            if ((null == feeds) || (feeds.size() < 1)) {
                param.setSpaceTs(0);
            } else {
                FeedListData feedData = feeds.get(0);
                if ((null != feedData) && (null != feedData.getCreate())) {
                    param.setSpaceTs(Long.parseLong(feedData.getCreate()));
                } else {
                    param.setSpaceTs(0);
                }
            }

            ServiceCallback back = new ServiceCallback();
            back.complete = false;
            CxPollingMessageStatus tempStatus;

            while (CxGlobalParams.getInstance().isAppStatus()) { // 默认是不结束long
                // polling
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    hasLongPolling = false;
                    CxLog.e("long polling", " sleep error");
                    return;
                }

                param = CxServiceParams.getInstance();
                // 先获取polling消息状态
                LongPollingApi pollApi = LongPollingApi.getInstance();
                /*
                 * if (null == pollApi) { //long polling初始化失败（没有网络登录成功就会初始化失败）
                 * RkLog.e("", "long polling init fail--------------------");
                 * return ; }
                 */

                back.complete = false;
                back.result = null;

                boolean sendRes = false;
                try {
                    sendRes = pollApi.pollingStatus(back);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!sendRes) {
                    continue;
                }

                while (!back.complete) { // 阻塞等待网络完成
                    if (!CxGlobalParams.getInstance().isAppStatus()) {
                        try {
                            pollApi.disposeRequest();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        hasLongPolling = false;
                        return;
                    }
                    try {
                        Thread.sleep(300);
                    } catch (Exception e) {
                        e.printStackTrace();
                        hasLongPolling = false;
                        CxLog.e("long polling", " sleep error");
                        return;
                    }
                } // end while(back)

                if (!CxGlobalParams.getInstance().isAppStatus()) {
                    CxLog.w("service", "jump out while loop");
                    hasLongPolling = false;
                    return;
                }
                // 匹对各个时间戳，如果有更新，就要拉取
                back.complete = false;
                tempStatus = null;
                try {
                    tempStatus = (CxPollingMessageStatus) back.result;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null == tempStatus) {
                    continue;
                }

                int rc = tempStatus.getRc();
                // 这里暂时不写全，有各种超时或者异常，涉及连接处理
                // RkLog.i("",
                // "long polling is running----------------"+System.currentTimeMillis());
                if (0 != rc) {
                    continue;
                }
                CxPollingDataField dataField = tempStatus.getData();
                if (null == dataField) {
                    continue;
                }

                if (!ChatFragment.mShowReceiveMessage && !ChatFragment.mShowLoading && ChatFragment.mShowWhichTitle != 0) {
//                	CxLog.i("ChatFragment_men", ChatFragment.mShowWhichTitle+">>>>>>>>service");
                    ChatFragment.mShowWhichTitle = 0;
                    Message chatMessage = ChatFragment.mChatHandler.obtainMessage(ChatFragment.UPDATE_CHAT_TITLE);
                    chatMessage.sendToTarget();
                }


                int pair = dataField.getPair();
                int chat_ts = dataField.getChat_ts();
                int space_ts = dataField.getSpace_ts();
                int rts = dataField.getRts();
                int match = dataField.getMatch();
                int edit_ts = dataField.getEdit_ts();
                int group = dataField.getGroup();
                int space_tips = dataField.getSpace_tips();
                int single_mode = dataField.getSingle_mode();
                int calendar_ts = dataField.getCalendar_ts();
                int kid_tips = dataField.getKid_tips();

                CxLog.i("longpolling receive", "pair=" + pair + ",chat_ts=" + chat_ts
                        + ",space_ts=" + space_ts + ",rts=" + rts + ",match=" + match + ", group="
                        + group + ", space_tips=" + space_tips + ", calendar_ts=" + calendar_ts + ", single_mode=" + single_mode);

                param.setMatch(match); // match原值返还long polling协议
                param.setEditTs(edit_ts);
                param.setSpaceTs(space_ts);
                // param.setGroup(group);
                if (!CxGlobalParams.getInstance().isAppStatus()) {
                    hasLongPolling = false;
                    return;
                }
                int localRts = mReminderSharedPreferences.getInt(CxGlobalConst.S_REMINDER_RTS_KEY,
                        0);
                // RkLog.i("receive", "localRts="+localRts);
                // 逐个时间戳匹对是否有更新
                if (rts != param.getRts() || rts > localRts) { // 有变化
                    param.setRts(rts);
                    // 告知提醒
                    if (null != CxReminderList.mReminderListAllHandler) {
                        Message reminderMessage = CxReminderList.mReminderListAllHandler
                                .obtainMessage(CxReminderList.REMINDER_LONGPOLLING);
                        reminderMessage.arg1 = rts;
                        reminderMessage.sendToTarget();
                    }
                }
                int localCals = mCalendarSharedPreferences.getInt(
                        CxGlobalConst.S_CALENDAR_REMIND_UPDATE_TIME, 0);
                CxLog.i("long polling localCals", "localCals=" + localCals + ":calendar_ts=" + calendar_ts);
                if (calendar_ts != param.getCalendarTs()) {
                    param.setCalendarTs(calendar_ts);
                    if (null != CxCalendarFragment.calendarHandler && calendar_ts > localCals) {
                        Message calendarMessage = CxCalendarFragment.calendarHandler
                                .obtainMessage(CxCalendarFragment.getInstance().LONGPOLLING_REFRESH_DATA);
                        calendarMessage.arg1 = calendar_ts;
                        calendarMessage.sendToTarget();
                    }
                }
                if (chat_ts != param.getChatTs()) {
                    param.setChatTs(chat_ts);
                    // 告知聊天
                    // ChatLogFetcher.fetchNewMessages(mContext, chat_ts);

                    // VoiceTip.tip(mContext, VoiceTip.VOICE_TIP_MODE_VIBRATE |
                    // VoiceTip.VOICE_TIP_MODE_VOICE);
                    // 此处不应发出声音，只有确保收到消息才发出声音
                    if (null != ChatFragment.mChatHandler) {
//                    	CxLog.i("ChatFragment_men", ChatFragment.mShowWhichTitle+">>>>>>>>ChatFragment.FETCH_NEW_MESSAGE");
                        Message chatMessage = ChatFragment.mChatHandler
                                .obtainMessage(ChatFragment.FETCH_NEW_MESSAGE);
                        chatMessage.arg1 = chat_ts;
                        chatMessage.sendToTarget();
                    }
                }
                if (space_ts != param.getSpaceTs()) {
                    param.setSpaceTs(space_ts);
                    // TODO 告知二人空间
                }

                if (group != param.getGroup()) {
                    param.setGroup(group);
                    // 告知各页面进行UI刷新
                    CxGlobalParams.getInstance().setGroup(group);
                    if (null != CxBaseSlidingMenu.mBaseSlidingMenuHandler) {
                        Message baseMsg = CxBaseSlidingMenu.mBaseSlidingMenuHandler
                                .obtainMessage(CxBaseSlidingMenu.UPDATE_UNREAD_MESSAGE);
                        baseMsg.sendToTarget();
                    }
                    if (null != ChatFragment.mChatHandler) {
                        Message chatMsg = ChatFragment.mChatHandler
                                .obtainMessage(ChatFragment.UPDATE_HOME_MENU);
                        chatMsg.sendToTarget();
                    }
                    if (null != CxUsersPairZone.mRkUserPairZone) {
                        Message zoneMsg = CxUsersPairZone.mRkUserPairZone
                                .obtainMessage(CxUsersPairZone.UPDATE_HOME_MENU);
                        zoneMsg.sendToTarget();
                    }
                    if (null != CxNeighbourFragment.mNbHandler) {
                        Message nbMsg = CxNeighbourFragment.mNbHandler
                                .obtainMessage(CxNeighbourFragment.UPDATE_HOME_MENU);
                        nbMsg.sendToTarget();
                    }
                    if (null != CxMoreFragment.mRkMoreHandler) {
                        Message mfMsg = CxMoreFragment.mRkMoreHandler
                                .obtainMessage(CxMoreFragment.UPDATE_HOME_MENU);
                        mfMsg.sendToTarget();
                    }
                    if (null != CxKidFragment.mKidHandler) {
                        Message mfMsg = CxKidFragment.mKidHandler
                                .obtainMessage(CxKidFragment.UPDATE_HOME_MENU);
                        mfMsg.sendToTarget();
                    }
                }

                if (space_tips != param.getSpace_tips()) {
                    param.setSpace_tips(space_tips);
                    // 告知各页面进行UI刷新
                    CxGlobalParams.getInstance().setSpaceTips(space_tips);
                    if (null != CxBaseSlidingMenu.mBaseSlidingMenuHandler) {
                        Message baseMsg = CxBaseSlidingMenu.mBaseSlidingMenuHandler
                                .obtainMessage(CxBaseSlidingMenu.UPDATE_SPACE_UNREAD_MESSAGE);
                        baseMsg.sendToTarget();
                    }
                    if (null != ChatFragment.mChatHandler) {
                        Message chatMsg = ChatFragment.mChatHandler
                                .obtainMessage(ChatFragment.UPDATE_HOME_MENU);
                        chatMsg.sendToTarget();
                    }
                    if (null != CxUsersPairZone.mRkUserPairZone) {
                        Message zoneMsg = CxUsersPairZone.mRkUserPairZone
                                .obtainMessage(CxUsersPairZone.UPDATE_HOME_MENU);
                        zoneMsg.sendToTarget();
                    }
                    if (null != CxNeighbourFragment.mNbHandler) {
                        Message nbMsg = CxNeighbourFragment.mNbHandler
                                .obtainMessage(CxNeighbourFragment.UPDATE_HOME_MENU);
                        nbMsg.sendToTarget();
                    }
                    if (null != CxMoreFragment.mRkMoreHandler) {
                        Message mfMsg = CxMoreFragment.mRkMoreHandler
                                .obtainMessage(CxMoreFragment.UPDATE_HOME_MENU);
                        mfMsg.sendToTarget();
                    }
                    if (null != CxKidFragment.mKidHandler) {
                        Message mfMsg = CxKidFragment.mKidHandler
                                .obtainMessage(CxKidFragment.UPDATE_HOME_MENU);
                        mfMsg.sendToTarget();
                    }
                }

                if (kid_tips != CxGlobalParams.getInstance().getKid_tips()) {
                    // 告知各页面进行UI刷新
                    CxGlobalParams.getInstance().setKid_tips(kid_tips);

                    int version_type = CxGlobalParams.getInstance().getVersion_type();
                    if (version_type != 1) {
                        if (null != CxBaseSlidingMenu.mBaseSlidingMenuHandler) {
                            Message baseMsg = CxBaseSlidingMenu.mBaseSlidingMenuHandler
                                    .obtainMessage(CxBaseSlidingMenu.UPDATE_KID_UNREAD_MESSAGE);
                            baseMsg.sendToTarget();
                        }
                        if (null != ChatFragment.mChatHandler) {
                            Message chatMsg = ChatFragment.mChatHandler
                                    .obtainMessage(ChatFragment.UPDATE_HOME_MENU);
                            chatMsg.sendToTarget();
                        }
                        if (null != CxUsersPairZone.mRkUserPairZone) {
                            Message zoneMsg = CxUsersPairZone.mRkUserPairZone
                                    .obtainMessage(CxUsersPairZone.UPDATE_HOME_MENU);
                            zoneMsg.sendToTarget();
                        }
                        if (null != CxNeighbourFragment.mNbHandler) {
                            Message nbMsg = CxNeighbourFragment.mNbHandler
                                    .obtainMessage(CxNeighbourFragment.UPDATE_HOME_MENU);
                            nbMsg.sendToTarget();
                        }
                        if (null != CxMoreFragment.mRkMoreHandler) {
                            Message mfMsg = CxMoreFragment.mRkMoreHandler
                                    .obtainMessage(CxMoreFragment.UPDATE_HOME_MENU);
                            mfMsg.sendToTarget();
                        }
                        if (null != CxKidFragment.mKidHandler) {
                            Message mfMsg = CxKidFragment.mKidHandler
                                    .obtainMessage(CxKidFragment.UPDATE_HOME_MENU);
                            mfMsg.sendToTarget();
                        }
                    }
                }

                if (CxGlobalParams.getInstance().getPair() != pair) { // pair有变动
                    // ，且本地是未结对时才设置进subject
                    // （新结对协议后，如果是被解除方的应用处于前台状态，需要主动点击确定解除后才换到邀请界面；
                    // 否则只有等下次进来才换到邀请界面）
                    if (0 == CxGlobalParams.getInstance().getPair()) { // 本地未结对，表明从未结对变为结对
                        CxGlobalParams.getInstance().setPair(pair); /*
                                                                     * 只有从未结对转为结对状态才设置进subject
                                                                     * ，
                                                                     * 让观察者主动调用；
                                                                     */
                        CxGlobalParams.getInstance().setDismissPair(false);
                    } else { /*
                              * 从结对变为未结对,表明从结对到未结对(被动解除结对)的状况需要弹窗让用户确认，
                              * 因为主动接触方在解除结对成功时已经设置进了subject （主动解除的方不在long
                              * polling中监听解除结对的情况了）
                              */
                        CxGlobalParams.getInstance().setPair(pair);
                        if (CxGlobalParams.getInstance().isDismissPair()) { // 主动解除结对
                            CxGlobalParams.getInstance().setDismissPair(false);

                        } else {
                            // 只有被动解除结对才弹窗确认
                            if (!CxBackgroundService.hasNotified) { // 保证只显示一次
                                CxBackgroundService.hasNotified = true;
                                Intent comfirmIntent = new Intent(CxBackgroundService.this,
                                        CxDialogActivity.class);
                                comfirmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                comfirmIntent.putExtra(CxGlobalConst.S_DIALOG_INTENT,
                                        CxGlobalParams.getInstance().isDismissPair());
                                startActivity(comfirmIntent);

                            }
                        }
                    }
                }

                if (CxGlobalParams.getInstance().getSingle_mode() != single_mode) { // pair有变动

                    int mode = CxGlobalParams.getInstance().getSingle_mode();
                    if (mode == 1) { // 从单人模式状态结对了
                        CxGlobalParams.getInstance().setSingle_mode(single_mode);
                    } else {
                        CxGlobalParams.getInstance().setSingle_mode(single_mode);
                    } // 没有else 因为现在还不支持从结对状态回到单人模式状态
                }

                /*
                 * 以上代码接收到新时间戳立即就设置为下次请求的时间戳的做法， 避免了重复拉取到新时间戳，子任务只需要执行完long
                 * polling 发送过去的更新消息即可
                 */

            } // end while finishPollingFlag
            hasLongPolling = false; // 停掉long polling
            // Log.i("longpolling",
            // "***********longpolling finish************");
        }
    };

    public void setReceiver(DownloadCompleteReceiver receiver) {
        this.receiver = receiver;
    }

    class ServiceObserverGlobal extends CxObserverInterface {

        /* 全局监听主要事项有：1、登录与否 2、前后台的监听 */
        @Override
        public void receiveUpdate(String actionTag) {
            if (this.getListenTag().contains(CxGlobalParams.APP_STATUS)) { // 应用处于前台或者后台的状态发生变化
//                if (!mGlobalParam.isAppStatus()) { // 处于后台要关闭long polling
//                    // finishPollingFlag = true;
//                    // pollingTask停止
//                } else {
//                    // finishPollingFlag = false;
//                    // Log.i("longpolling",
//                    // "%%%%%%%%%%%%%%Observer ready to run long polling task%%%%%%%%%%%");
//                    new Thread(pollingTask).start(); // 开启long polling
//                }
                if (mGlobalParam.isAppStatus())
                    new Thread(pollingTask).start(); // 开启long polling
            }

//            if (this.getListenTag().contains(CxGlobalParams.IS_LOGIN)) { // 登入登出变化
//                if (!mGlobalParam.isLogin()) {
//                    // finishPollingFlag = true;
//                } else {
//                    // finishPollingFlag = false;
//                }
//            }

        }

    }

    // 网络
    private BroadcastReceiver mNetStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (CxGlobalParams.getInstance().isLoginNetSuccess()) {
                return;
            }
            String action = intent.getAction();
            if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                return;
            }

            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();

            if (info != null && info.isAvailable()) {
                CxThirdAccessToken localToken = CxThirdAccessTokenKeeper.readAccessToken(context);
                String plantName = localToken.getPlatName();
                String plantUid = localToken.getUid();
                String plantToken = localToken.getToken();
                if (TextUtils.isEmpty(plantToken) || TextUtils.isEmpty(plantToken)
                        || TextUtils.isEmpty(plantToken)) {
                    return;
                }
                try {
                    AccountApi.getInstance().doLogin(plantName, plantUid, plantToken,
                            CxGlobalParams.getInstance().getVersion(), autoLogin);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    JSONCaller autoLogin = new JSONCaller() {

        @Override
        public int call(Object result) {
            if (null == result) { // 属于异常情况
                return -1;
            }
            CxLogin loginResult = null;
            try {
                loginResult = (CxLogin) result;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (loginResult.getRc() != 0) {
                return 1;
            }
            CxGlobalParams.getInstance().setLoginNetSuccess(true);

            return 0;
        }
    };

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                CxLog.i("version check", "is not ACTION_DOWNLOAD_COMPLETE");
                return;
            }

            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id != CxGlobalParams.getInstance().getUpdateTaskID()) {
                CxLog.i("version check", "is not my application");
                return;
            }

            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Bundle extras = intent.getExtras();
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
            Cursor c = downloadManager.query(q);

            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    String path = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                    CxLog.i("download file", "" + path);
                    if (TextUtils.isEmpty(path)) {
                        return;
                    }
                    Uri uri = Uri.parse(path);
                    Intent install = new Intent();
                    install.setAction("android.intent.action.VIEW");
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.setDataAndType(uri, "application/vnd.android.package-archive");
                    startActivity(install);

                }
            }

        }
    }

}
