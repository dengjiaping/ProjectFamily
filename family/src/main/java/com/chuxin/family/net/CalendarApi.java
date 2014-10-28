
package com.chuxin.family.net;

import com.chuxin.family.utils.CxLog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CalendarApi extends ConnectionManager {

    private static final String TAG = "CalendarApi";

    private static final CalendarApi sInstance = new CalendarApi();

    private static final String PATH_CALENDAR_CYCLE_LIST = HttpApi.HTTP_SERVER_PREFIX
            + "/Calendar/cycle_list";

    private static final String PATH_CALENDAR_MONTH_LIST = HttpApi.HTTP_SERVER_PREFIX
            + "/Calendar/month_list";

    private static final String PATH_CALENDAR_REMIND_LIST = HttpApi.HTTP_SERVER_PREFIX
            + "/Calendar/remind_list";

    private static final String PATH_CALENDAR_MEMORY_DAY = HttpApi.HTTP_SERVER_PREFIX
            + "/Calendar/memory_day";

    private static final String PATH_CREATE_CLENDAR = HttpApi.HTTP_SERVER_PREFIX
            + "/Calendar/create";

    private static final String PATH_UPDATE_CALENDAR = HttpApi.HTTP_SERVER_PREFIX
            + "/Calendar/update";

    private static final String PATH_DELETE_CALENDAR = HttpApi.HTTP_SERVER_PREFIX
            + "/Calendar/delete";

    private static final String PATH_CALENDAR_READ = HttpApi.HTTP_SERVER_PREFIX + "/Calendar/read";

    private CalendarApi() {
    }

    public static CalendarApi getInstance() {
        return sInstance;
    }

    /**
     * 全部周期日历列表
     * 
     * @param date- 时间戳：37263268392
     * @param callback
     */
    public void doCalendarCycleList(final int date, final ConnectionManager.JSONCaller callback) {
        NameValuePair[] params = {
            new BasicNameValuePair("date", String.valueOf(date)),
        };
        final String url = PATH_CALENDAR_CYCLE_LIST;
        doHttpGet(url, new ConnectionManager.JSONCaller() {

            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject)data;
                CxLog.d(TAG, "THE RESULT of " + url + ":" + result.toString());
                try {
//                    int rc = result.getInt("rc");
//                    if (rc != 0) {// 视为失败
//                        callback.call(null);
//                        return 0;
//                    }

                    if (callback != null) {
//                        JSONObject cycle_list_data = result.getJSONObject("data");
                        callback.call(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return 0;
            }
        }, params);
    }

    /**
     * 当月非周期日历列表
     * 
     * @param date - 月份格式：201412
     * @param callback
     */
    public void doCalendarMonthList(final String date, final ConnectionManager.JSONCaller callback) {
        NameValuePair[] params = {
            new BasicNameValuePair("date", date),
        };
        final String url = PATH_CALENDAR_MONTH_LIST;
        doHttpGet(url, new ConnectionManager.JSONCaller() {

            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject)data;
                CxLog.d(TAG, "THE RESULT of " + url + ":" + result.toString());
                try {
                    if (callback != null) {
//                        JSONArray calendar_month_list = result.getJSONArray("data");
                        callback.call(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return 0;
            }
        }, params);
    }

    /**
     * 今天及之后的提醒列表
     * 
     * @param date- 日历的更新时间(根据此时间判断要不要返数据给前端) 时间戳
     * @param callback
     */
    public void doCalendarRemindList(final int date, final ConnectionManager.JSONCaller callback) {
        NameValuePair[] params = {
            new BasicNameValuePair("date", String.valueOf(date)),
        };
        final String url = PATH_CALENDAR_REMIND_LIST;
        doHttpGet(url, new ConnectionManager.JSONCaller() {

            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject)data;
                CxLog.d(TAG, "THE RESULT of " + url + ":" + result.toString());
                try {
                    int rc = result.getInt("rc");
                    if (rc != 0) {
                        callback.call(null);
                        return 0;
                    }

                    if (callback != null) {
                        JSONObject calendar_month_list = result.getJSONObject("data");
                        callback.call(calendar_month_list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        }, params);
    }

    /**
     * 纪念日列表
     * 
     * @param callback
     */
    public void doCalendarMemoryday(final ConnectionManager.JSONCaller callback) {
        NameValuePair[] params = {
        };
        final String url = PATH_CALENDAR_MEMORY_DAY;
        doHttpGet(url, new ConnectionManager.JSONCaller() {

            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject)data;
                CxLog.d(TAG, "THE RESULT of " + url + ":" + result.toString());
                try {
                    int rc = result.getInt("rc");
                    if (rc != 0) {
                        callback.call(null);
                        return 0;
                    }

                    CxLog.i("CalendarApi_men", data.toString());
                    
                    if (callback != null) {
                        JSONObject calendar_month_list = result.getJSONObject("data");
                        callback.call(calendar_month_list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        }, params);
    }

    /**
     * 更新日历项
     * 
     * @param id - 日历项id
     * @param type - 分类，0:事项 1:纪念日
     * @param target - 显示对象，0:自己 1:对方 2:双方
     * @param content - 日历内容
     * @param day_type - 纪念日类别，0：无 1：生日 2：其它
     * @param is_lunar - 是否农历，0：不是 1：是
     * @param is_remind - 是否提醒，0:不提醒 1:提醒
     * @param base_ts - 基准触发时间 (时间戳)
     * @param cycle - 循环周期类型，0：不循环，1：按天，2：按周，3：按月，4：按年
     * @param advance - 提前量
     */
    public void doUpdateCalendar(final String id, final int type, final int target,
            final String content, final int day_type, final int is_lunar, final int is_remind,
            final int base_ts, final int cycle, final int advance,
            final ConnectionManager.JSONCaller callback) {

        NameValuePair[] params = {
                new BasicNameValuePair("id", id),
                new BasicNameValuePair("type", String.valueOf(type)),
                new BasicNameValuePair("target", String.valueOf(target)),
                new BasicNameValuePair("content", String.valueOf(content)),
                new BasicNameValuePair("day_type", String.valueOf(day_type)),
                new BasicNameValuePair("is_lunar", String.valueOf(is_lunar)),
                new BasicNameValuePair("is_remind", String.valueOf(is_remind)),
                new BasicNameValuePair("base_ts", String.valueOf(base_ts)),
                new BasicNameValuePair("cycle", String.valueOf(cycle)),
                new BasicNameValuePair("advance", String.valueOf(advance)),
        };

        final String url = PATH_UPDATE_CALENDAR;
        doHttpPost(url, new ConnectionManager.JSONCaller() {

            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject)data;
                Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
                try {
                    int rc = result.getInt("rc");
                    if (rc != 0) {
                        callback.call(result);
                        return 0;
                    }

                    if (callback != null) {
                        JSONObject reminder = result.getJSONObject("data");
                        callback.call(reminder);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        }, params);
    }
    
    /**
     * 更新日历项
     * 
     * @param id - 日历项id
     * @param status - #日历状态，0：有效的，1：无效的，2：过期的
     */
    public void doUpdateCalendar(final String id, final int status,
            final ConnectionManager.JSONCaller callback) {

        NameValuePair[] params = {
                new BasicNameValuePair("id", id),
                new BasicNameValuePair("advance", String.valueOf(status)),
        };

        final String url = PATH_UPDATE_CALENDAR;
        doHttpPost(url, new ConnectionManager.JSONCaller() {

            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject)data;
                Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
                try {
                    int rc = result.getInt("rc");
                    if (rc != 0) {
                        callback.call(result);
                        return 0;
                    }

                    if (callback != null) {
                        JSONObject reminder = result.getJSONObject("data");
                        callback.call(reminder);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        }, params);
    }

    /**
     * 创建日历项
     * 
     * @param type - 分类，0:事项 1:纪念日
     * @param target - 显示对象，0:自己 1:对方 2:双方
     * @param content - 日历内容
     * @param day_type - 纪念日类别，0：无 1：生日 2：其它
     * @param is_lunar - 是否农历，0：不是 1：是
     * @param is_remind - 是否提醒，0:不提醒 1:提醒
     * @param base_ts - 基准触发时间 (时间戳)
     * @param cycle - 循环周期类型，0：不循环，1：按天，2：按周，3：按月，4：按年
     * @param advance - 提前量(秒)
     */
    public void doCreateCalendar(final int type, final int target, final String content,
            final int day_type, final int is_lunar, final int is_remind, final int base_ts,
            final int cycle, final int advance, final ConnectionManager.JSONCaller callback) {
        CxLog.i(TAG, "doCreateCalendar param="+"type="+type+"target="+target+"content="+
            content+"day_type="+day_type+"is_lunar"+is_lunar+"is_remind="+is_remind+"base_ts="+base_ts+"cycle="+cycle+"advance="+advance);
        NameValuePair[] params = {
                new BasicNameValuePair("type", String.valueOf(type)),
                new BasicNameValuePair("target", String.valueOf(target)),
                new BasicNameValuePair("content", String.valueOf(content)),
                new BasicNameValuePair("day_type", String.valueOf(day_type)),
                new BasicNameValuePair("is_lunar", String.valueOf(is_lunar)),
                new BasicNameValuePair("is_remind", String.valueOf(is_remind)),
                new BasicNameValuePair("base_ts", String.valueOf(base_ts)),
                new BasicNameValuePair("cycle", String.valueOf(cycle)),
                new BasicNameValuePair("advance", String.valueOf(advance)),
        };

        final String url = PATH_CREATE_CLENDAR;
        doHttpPost(url, new ConnectionManager.JSONCaller() {

            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject)data;
                Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
                try {
                    int rc = result.getInt("rc");
                    if (rc != 0) {
                        callback.call(result);
                        return 0;
                    }

                    if (callback != null) {
                        JSONObject reminder = result.getJSONObject("data");
                        callback.call(reminder);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        }, params);
    }

    /**
     * 删除日历项
     * 
     * @param id 日历项id
     * @param callback
     */
    public void doDeleteReminder(final String id, final ConnectionManager.JSONCaller callback) {

        NameValuePair[] params = {
            new BasicNameValuePair("id", id),
        };

        final String url = PATH_DELETE_CALENDAR;
        doHttpGet(url, new ConnectionManager.JSONCaller() {

            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject)data;
                Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
                try {
                    int rc = result.getInt("rc");
                    if (rc != 0) {
                        callback.call(result);
                        return 0;
                    }

                    if (callback != null) {
                        callback.call(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        }, params);
    }

    /**
     * 标记为已读
     * @param date - 阳历日期20140120
     * @param callback
     */
    public void doCalendarRead(final String date,
            final ConnectionManager.JSONCaller callback) {

        NameValuePair[] params = {
            new BasicNameValuePair("date", date),
        };

        final String url = PATH_CALENDAR_READ;
        doHttpGet(url, new ConnectionManager.JSONCaller() {

            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject)data;
                Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
                try {
                    int rc = result.getInt("rc");
                    if (rc != 0) {
                        callback.call(result);
                        return 0;
                    }

                    if (callback != null) {
                        callback.call(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        }, params);
    }

}
