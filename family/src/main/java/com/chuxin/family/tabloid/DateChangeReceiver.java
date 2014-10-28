package com.chuxin.family.tabloid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.chuxin.family.parse.been.data.TabloidCateConfData;
import com.chuxin.family.parse.been.data.TabloidCateConfObj;
import com.chuxin.family.utils.CxLog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

/**
 * 系统日期变化时，设置小报定时器
 * * @author dujy
 */
public class DateChangeReceiver extends android.content.BroadcastReceiver {
    private String TAG = "DateChangeReceiver";
    //private Context mContext;
    private static final String ACTION_TIMEZONE_CHANGED = Intent.ACTION_DATE_CHANGED;

    @Override
    public void onReceive(Context context, Intent intent) {
        //	mContext = context;
//        CxLog.d(TAG, "DateChangeReceiver.java开始响应, 用户新注册或系统日期变化啦!");

        String action = intent.getAction();
        System.out.println("---->action = " + action);

        final Context ctx = context;
        final Intent it = intent;
        new Thread(new Runnable() {

            @Override
            public void run() {
//                setTabloidReminder(ctx);
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/tabloid.log");
                    if (!file.exists()) file.createNewFile();
                    String text = Calendar.getInstance().getTime().toString() + "  Action = " + (it == null ? "By Application" : it.getAction());
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    StringBuilder sb = new StringBuilder();
                    byte[] buf = new byte[128];
                    while (bis.read(buf) != -1) {
                        sb.append(new String(buf, "UTF-8"));
                    }
                    sb.append("\n").append(text);
                    bis.close();
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bos.write(sb.toString().getBytes("UTF-8"));
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tryTabloidReminder(ctx);
            }
        }).start();


    }

    public void tryTabloidReminder(Context context) {
        TabloidDao dao = new TabloidDao(context.getApplicationContext());
        TabloidCateConfData pubConf = dao.getPubConf();
        if (pubConf == null) {
            CxLog.e(TAG, "小报分类的配置文件为空!");
            return;
        }
        String notifiTime = pubConf.getNotification_time();
        notifiTime = notifiTime.replaceAll("\\[|\\]", "");        // 去掉左右中括号
        String[] arr = notifiTime.split(",");

        int hourSet = Integer.valueOf(arr[0]);
        int minuteSet = Integer.valueOf(arr[1]);
        CxLog.d(TAG, "hourSet:" + hourSet + "  minSet:" + minuteSet);
        Calendar c = Calendar.getInstance();
//        System.out.println("Calendar.HOUR_OF_DAY" + Calendar.HOUR);
//        System.out.println("Calendar.MINUTE" + Calendar.MINUTE);
        if (hourSet == c.get(Calendar.HOUR_OF_DAY) && minuteSet == c.get(Calendar.MINUTE)) {
//            String codeStr = c.get(Calendar.MONTH) + "" + c.get(Calendar.DAY_OF_MONTH);
//            int requestCode = Integer.valueOf(codeStr);
            context.getApplicationContext().sendBroadcast(new Intent("com.chuxin.family.tabloid.SEND_TABLOID"));
            CxLog.i("tryTabloidReminder", "小报消息触发！");
        }


    }

    /**
     * 设置“我家小报”提醒
     */
    public void setTabloidReminder(Context ctx) {
        TabloidDao dao = new TabloidDao(ctx.getApplicationContext());
//		List<TabloidCateConfObj> cateList = dao.getCateConfList(0);
//		if(cateList==null || cateList.size()==0){
//			return;
//		}
        //System.out.println("开始执行提醒操作!");
        AlarmManager aManager = (AlarmManager) ctx.getApplicationContext().getSystemService(Service.ALARM_SERVICE);
        Intent intent = new Intent("com.chuxin.family.tabloid.SEND_TABLOID");

        // 每天要提醒的时间 （每天的几点几分开始提醒）
        TabloidCateConfData pubConf = dao.getPubConf();
        if (pubConf == null) {
            CxLog.e(TAG, "小报分类的配置文件为空!");
            return;
        }
        String notifiTime = pubConf.getNotification_time();
        notifiTime = notifiTime.replaceAll("\\[|\\]", "");        // 去掉左右中括号
        String[] arr = notifiTime.split(",");

        int hourSet = Integer.valueOf(arr[0]);
        int minuteSet = Integer.valueOf(arr[1]);
        CxLog.d(TAG, "hourSet:" + hourSet + "  minSet:" + minuteSet);

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // 用"月+日"作为定时器的requestCode  (这样做，就是为了下一天再定义时可以覆盖)
        String codeStr = month + "" + day;
        int requestCode = Integer.valueOf(codeStr);

        // 设置提醒时间
        c.set(Calendar.HOUR_OF_DAY, hourSet);
        c.set(Calendar.MINUTE, minuteSet);


        // 如果今天还没到提醒时间，先创建今天的提醒(如果以前已定义，则会覆盖)
        if (hourSet >= hour && minuteSet > minute) {
            PendingIntent pi = PendingIntent.getBroadcast(ctx.getApplicationContext(), requestCode, intent, 0);
            aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

//			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			System.out.println("今日提醒设置完成：" + sdf.format(c.getTime()));
        }


    }

}
