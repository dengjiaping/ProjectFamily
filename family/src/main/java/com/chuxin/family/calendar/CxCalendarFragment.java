
package com.chuxin.family.calendar;

import com.chuxin.family.R;
import com.chuxin.family.app.CxApplication;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.models.CalendarCycleList;
import com.chuxin.family.models.CalendarDataObj;
import com.chuxin.family.models.CalendarMemoryDay;
import com.chuxin.family.models.CalendarMonthList;
import com.chuxin.family.models.CalendarRemindList;
import com.chuxin.family.models.Model;
import com.chuxin.family.net.CalendarApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.data.CalendarDayData;
import com.chuxin.family.parse.been.data.DateData;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CalendarUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.DateUtil;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.utils.Globals;
import com.chuxin.family.utils.ScreenUtil;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.timessquare.CalendarView;
import com.squareup.timessquare.CalendarView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarView.OnDetailClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CxCalendarFragment extends Fragment {

    

	private final String TAG = "RkCalendarFragment";

    private TextView mTileText;

    private ImageView mTitleImg;

    private PopupWindow mPop;

    private int type = 1; // 该页面状态 1为日历模式 2为纪念日模式

    private int editType = 0; // 跳转到编辑页面的类别 事项或纪念日

    private LinearLayout mTextLayout;

    private ScrollView homepageLayout;

    private TextView homepageText;

    private LinearLayout memorialLayout;

    private TextView memorialText;

    private LinearLayout titleHomepageLayout;

    private LinearLayout titleMemorialLayout;

    private TextView dateDayText;

    private TextView dateMonthText;

    private TextView dateWeekText;

    private TextView dateLunarText;

    private TextView dateSolarText;

    private TextView dateFestivalText;

    private LinearLayout addItemLayout;

    // private LinearLayout addMemorialLayout;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private CalendarDisplayUtility mDisplayUtility = null;

    private int mRemindListUpdateTime = 0;

    private int mCycleUpdateTime = 0;

    private String mMonthDate;

    private int mDay;

    private String mDayStr;

//    private boolean isMemorialFirst;

    private ArrayList<CalendarDataObj> memorialDownDays;

    private ArrayList<CalendarDataObj> memorialUpDays;

    public Map<String, List<CalendarDataObj>> mDayCalendarData = new HashMap<String, List<CalendarDataObj>>();

    private CalendarView mCalendarView;

    private static CxCalendarFragment mRkCalendarFragment;

    public final int UPDATE_DATE_CALENDAR = 1;// 更新日期数据

    public final int LOAD_CALENDAR_DATA = 2;// 加载数据

    public final int REFRESH_CALENDAR_DATA = 3;// 本地添加保存有变化时刷新数据

    public final int CHANGE_DAY_OF_MONTH = 4; //横屏更改本月数据时同步
    
    public final int LONGPOLLING_REFRESH_DATA = 5;// longpolling有变化时刷新数据
    
    public final int UPDATE_MEMO_ADAPTER = 6;// 

    public static Handler calendarHandler;

    private SharedPreferences mCalendarSharedPreferences = null;

    private boolean loadCycleCalFinish = false; // 是否存储完本地周期提醒数据

    private boolean loadMonthCalFinish = false; // 是否存储完本地本月提醒数据
    
    private HashMap<Integer, CalendarDayData>  currentMonthMap=new HashMap<Integer, CalendarDayData>();

    // private int mLocalCalendarTs=0;
    // private boolean mIsLongpolling = false;
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        type = CxCalendarParam.getInstance().getFragment_type();
//        isMemorialFirst = true;
        memorialDownDays = new ArrayList<CalendarDataObj>();
        memorialUpDays = new ArrayList<CalendarDataObj>();

        calendarHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_DATE_CALENDAR:
                    	CxLog.i("calendarHandler", "UPDATE_DATE_CALENDAR  come in");
                    	Calendar dateOfMonth = (Calendar)msg.obj;
                        updateChangeCalendar(dateOfMonth, true);
                        // mCalendarView.selectDate(dateOfMonth);
                        break;
                    case LOAD_CALENDAR_DATA:
                        CxLog.i("calendarHandler", "loadCycleCalFinish:loadMonthCalFinish="
                                + loadCycleCalFinish + ":" + loadMonthCalFinish);
                        if (loadCycleCalFinish && loadMonthCalFinish) {
                            CxLog.i("calendarHandler", "come in");
                            loadLocalCalendar(getActivity());
                            CxCalendarParam.getInstance().setCurrentMonthMap(currentMonthMap);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Integer.parseInt(mMonthDate.substring(0, 4)), Integer.parseInt(mMonthDate.substring(4))-1, mDay);
                            mCalendarView.selectDate(calendar,false);
                            if(Globals.getInstance().getCalendarIsLandSpace()){
	                            android.os.Message calendarMessage = CxCalendarDetailActivity.detailHandler
	                        		.obtainMessage(0,calendar);
	                            calendarMessage.sendToTarget();
                            }
//                            mCalendarView.updateData();
                            updateItemList(getCurrentDayCalendar(mDay));
                        }
                        break;
                    case REFRESH_CALENDAR_DATA:
                        // mLocalCalendarTs = msg.arg1;
                        // mIsLongpolling = true;
                    	
                    	Calendar c1 = (Calendar)msg.obj;
                    	
                    	if(c1!=null){
                    		mMonthDate=DateUtil.getStringMonth(c1.getTime());
                            mDay=c1.get(Calendar.DAY_OF_MONTH);
                            mDayStr = DateUtil.getStringDate(c1.getTime());
                    	}             	
                        CxLog.i(TAG, "refresh come in");
                        loadCalendars();
//                        loadNetMonthList();
                    case CHANGE_DAY_OF_MONTH:
                    	Calendar c = (Calendar)msg.obj;
                    	if(null != c){
                        	mMonthDate=DateUtil.getStringMonth(c.getTime());
                        	mDay=c.get(Calendar.DAY_OF_MONTH);
                        	mDayStr = DateUtil.getStringDate(c.getTime());
                        	mCalendarView.selectDate(c,false);
                    	}
                        break;
                    case LONGPOLLING_REFRESH_DATA:
                        loadReminders();
                    	loadCalendars();
                    	break;
                    case UPDATE_MEMO_ADAPTER:
                    	memorialAdapter.updateAdapter(memorialDownDays,memorialUpDays);
                    	break;
                }
            }
        };

    }

    public static CxCalendarFragment getInstance() {
        if (null == mRkCalendarFragment) {
            mRkCalendarFragment = new CxCalendarFragment();
        }
        return mRkCalendarFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	type=CxCalendarParam.getInstance().getFragment_type();
        View inflate = inflater.inflate(R.layout.cx_fa_fragment_calendar, null);

        CxLog.i("RkCalendar_men", (getActivity() == null) + "");

        initTitle(inflate);
        init(inflate);
        
        changeCalendarState();
        
        initData();

        ((CxMain)getActivity()).closeMenu();

        

        return inflate;
    }

    private void init(View inflate) {

        Globals.getInstance().setCalendarIsLandSpace(false);
        homepageLayout = (ScrollView)inflate.findViewById(R.id.cx_fa_calendar_homepage_layout);
        memorialLayout = (LinearLayout)inflate.findViewById(R.id.cx_fa_calendar_memorial_day_layout);

        dateDayText = (TextView)inflate.findViewById(R.id.cx_fa_calendar_date_day_tv);
        dateMonthText = (TextView)inflate.findViewById(R.id.cx_fa_calendar_date_year_and_month_tv);
        dateWeekText = (TextView)inflate.findViewById(R.id.cx_fa_calendar_date_week_tv);

        dateLunarText = (TextView)inflate.findViewById(R.id.cx_fa_calendar_date_lunar_tv);
        dateSolarText = (TextView)inflate.findViewById(R.id.cx_fa_calendar_date_solar_tv);
        dateFestivalText = (TextView)inflate.findViewById(R.id.cx_fa_calendar_date_festival_tv);

        addItemLayout = (LinearLayout)inflate.findViewById(R.id.cx_fa_calendar_add_item_layout);
        addItemLayout.setOnClickListener(contentListener);

        // addMemorialLayout = (LinearLayout)inflate
        // .findViewById(R.id.cx_fa_calendar_add_memorial_day_layout);
        // addMemorialLayout.setOnClickListener(contentListener);
        
        spaceLayout = (LinearLayout) inflate.findViewById(R.id.cx_fa_calendar_fill_space_layout);
//        spaceLayout.setVisibility(View.GONE);
        

        itemList = (LinearLayout)inflate.findViewById(R.id.cx_fa_calendar_items_layout);

        ListView memorialList = (ListView)inflate.findViewById(R.id.cx_fa_calendar_memorial_day_lv);
        memorialAdapter = new MemorialAdapter();
        memorialList.setAdapter(memorialAdapter);
        mCalendarView = (CalendarView)inflate.findViewById(R.id.cx_fa_calendar_view);
        mCalendarView.setOnDateSelectedListener(new OnDateSelectedListener() {

            @Override
            public void onDateUnselected(Calendar date) {
            }

            @Override
            public void onDateSelected(Calendar date, boolean isRead) {
            	String stringMonth = DateUtil.getStringMonth(date.getTime());
            	CxLog.i("CalendarView_men", ">>>>>>>>>>>>6"+(stringMonth.equals(mMonthDate)));
            	if(stringMonth.equals(mMonthDate)){
            		updateDate(date.getTime());                          
                    updateItemList(getCurrentDayCalendar(mDay));
                    if(!isRead){
                    	updateRead(date, true);
                    }
            	}else{
            		android.os.Message calendarMessage = calendarHandler.obtainMessage
            			(CxCalendarFragment.getInstance().UPDATE_DATE_CALENDAR,date);
            		calendarMessage.sendToTarget();
            	}  
            }
        });
        

        mCalendarView.setOnDetailClickListener(new OnDetailClickListener() {

            @Override
            public void onDetailClick() {
                Globals.getInstance().setCalendarIsLandSpace(true);
                Intent detailIntent = new Intent(getActivity(), CxCalendarDetailActivity.class);
                detailIntent.putExtra("nowDate", mDayStr);
                startActivity(detailIntent);
                getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
            }
        });
    }

    private void updateChangeCalendar(Calendar dateOfMonth, boolean isNowMonth) {
        mRemindListUpdateTime = mCalendarSharedPreferences.getInt(
                CxGlobalConst.S_CALENDAR_REMIND_UPDATE_TIME, 0);
        mCycleUpdateTime = mCalendarSharedPreferences.getInt(
                CxGlobalConst.S_CALENDAR_CYCLE_UPDATE_TIME, 0);

        String monthDate = DateUtil.getStringMonth(dateOfMonth.getTime());
        isNowMonth = (mMonthDate.equals(monthDate));
        updateDate(dateOfMonth.getTime());
        if (!isNowMonth) {
        	CxLog.i("calendarHandler", "isNowMonth  come in:"+isNowMonth);
            loadNetMonthList();
        } else { 
            updateItemList( getCurrentDayCalendar(mDay));
        }
    }

    private void updateRead(final Calendar date, final boolean isNowMonth) {
        String dateStr = DateUtil.getStringDate(date.getTime());
        CxLog.i("updateRead", "dateStr=" + dateStr);
        CalendarApi.getInstance().doCalendarRead(dateStr, new JSONCaller() {

            @Override
            public int call(Object result) {
                if (null == result) {
                    
                    return 0;
                }

                return 0;
            }
        });
    }

    private void initData() {
        mDisplayUtility = new CalendarDisplayUtility(getResources());
        mCalendarSharedPreferences = getActivity().getSharedPreferences(
                CxGlobalConst.S_CALENDAR_PREFS_NAME, 0);
        mRemindListUpdateTime = mCalendarSharedPreferences.getInt(
                CxGlobalConst.S_CALENDAR_REMIND_UPDATE_TIME, 0);
        mCycleUpdateTime = mCalendarSharedPreferences.getInt(
                CxGlobalConst.S_CALENDAR_CYCLE_UPDATE_TIME, 0);
        CxLog.i(TAG, "mRemindListUpdateTime:mCycleUpdateTime" + mRemindListUpdateTime + ":"
                + mCycleUpdateTime);
        Locale locale = Locale.getDefault();
        Calendar today = Calendar.getInstance(locale);
        SimpleDateFormat monthNameFormat = new SimpleDateFormat(
                getString(R.string.month_name_format), locale);
        SimpleDateFormat weekdayNameFormat = new SimpleDateFormat(
                getString(R.string.day_name_format), locale);
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH) + 1;
        int day = today.get(Calendar.DAY_OF_MONTH);
        mDay = day;
        mDayStr = DateUtil.getStringDate(today.getTime());
        int week = today.get(Calendar.DAY_OF_WEEK);

        dateMonthText.setText(year + "年" + month + "月");
        dateDayText.setText(day>9 ? day + "" : "0" + day);
        dateWeekText.setText(DateUtil.getCatipalNumber(week));

        Lunar lunar = new Lunar(today);
        dateLunarText.setText(lunar.getLunarMonthString() + lunar.getLunarDayString());
        dateSolarText.setText(lunar.getTermString());
        if (lunar.isLFestival()) {
            dateFestivalText.setText(lunar.getLFestivalName());
        } else if (lunar.isSFestival()) {
            dateFestivalText.setText(lunar.getSFestivalName());
        } else {
            dateFestivalText.setText("");
        }

        String monthStr = month > 9 ? month + "" : "0" + month;
        mMonthDate = String.valueOf(year) + monthStr;
        CxLog.i(TAG, "mMonthDate>>>" + mMonthDate);
        // dropAll();
        loadCalendars();       
        
        loadReminders();

    }

    private void updateDate(Date date) {
        DateData dd = DateUtil.getNumberTime(date);
        dateMonthText.setText(dd.getYear() + "年" + (dd.getMonth() + 1) + "月");
        dateDayText.setText(dd.getDay() > 9 ? dd.getDay() + "" : "0" + dd.getDay());
        dateWeekText.setText(DateUtil.getCatipalNumber(dd.getWeekday()));
        Lunar lunar = new Lunar(date);
        dateLunarText.setText(lunar.getLunarMonthString() + lunar.getLunarDayString());
        dateSolarText.setText(lunar.getTermString());
        if (lunar.isLFestival()) {
            dateFestivalText.setText(lunar.getLFestivalName());
        } else if (lunar.isSFestival()) {
            dateFestivalText.setText(lunar.getSFestivalName());
        } else {
            dateFestivalText.setText("");
        }
        mDay=dd.getDay();
        mMonthDate = DateUtil.getStringMonth(date);
        mDayStr = DateUtil.getStringDate(date);
        CxLog.i(TAG, "mMonthDate:" + mMonthDate+"mDay:" + mDay+"mDayStr:" + mDayStr);
    }

    private void updateItemList(ArrayList<CalendarDataObj> mCalendarListData) {
        if(null == getActivity()){
            return;
        }
        if (null != itemList) {
            itemList.removeAllViews();
        }
        CxLog.i("men>>>", ">>>>>>>>>>>>>5");
        if ((null != mCalendarListData) && (mCalendarListData.size() > 0)) {
            addItemLayout.setVisibility(View.GONE);           
            itemList.setVisibility(View.VISIBLE);
            int size = mCalendarListData.size();
            if(size==1){
            	spaceLayout.setVisibility(View.VISIBLE);
            	LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.calendar_homepage_items_item_height_2));
            	spaceLayout.setLayoutParams(lp);
            }else if(size==2){
            	spaceLayout.setVisibility(View.VISIBLE);
            	LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT,getResources().getDimensionPixelSize(R.dimen.calendar_homepage_items_item_height));
            	spaceLayout.setLayoutParams(lp);
            }else{
            	spaceLayout.setVisibility(View.GONE);
            }  
            
            for (int i = 0; i < mCalendarListData.size(); i++) {
                View view = addItemView(mCalendarListData.get(i));
                if(null == view){
                    continue;
                }
                itemList.addView(view);
            }
        } else {
        	spaceLayout.setVisibility(View.GONE);
            addItemLayout.setVisibility(View.VISIBLE);
            itemList.setVisibility(View.GONE);
        }
    }

    private void dropAll() {
        CalendarCycleList ccl = new CalendarCycleList(null, getActivity());
        ccl.dropAll();
        CalendarRemindList crl = new CalendarRemindList(null, getActivity());
        crl.dropAll();
    }

    private void loadReminders(){
        loadLocalReminders();
    	CalendarApi.getInstance().doCalendarRemindList(mRemindListUpdateTime, new JSONCaller() {

            @Override
            public int call(Object result) {
                updateListCalendarRemind(result);
                loadLocalReminders();
                return 0;
            }
        });
    }
    private void loadLocalReminders(){
        List<Model> localCalendarReminder = new CalendarRemindList(null, getActivity()).gets("1=1",new String[] {}, null, 0, 0);
        if (null != localCalendarReminder) {
            try {
                Calendar nowCalendar = Calendar.getInstance();
                nowCalendar.setTime(new Date(System.currentTimeMillis()));
                CalendarRemindList calendarreminderlist = (CalendarRemindList)(localCalendarReminder
                        .get(0));
                JSONArray cja = calendarreminderlist.getRemindArray();
                if (null != cja && cja.length() > 0) {
                    for (int j = 0; j < cja.length(); j++) {
                        JSONObject remindObj = cja.getJSONObject(j);
                        CalendarDataObj cdo = new CalendarDataObj(null, getActivity());
                        cdo.mData = remindObj;
                        if(cdo.adjust() == -1){
                            CxLog.v(TAG, "falg 1 drop" + cdo.getBaseTimestamp());
                            CalendarController.getInstance().cancelAlarmReminder(getActivity(),
                                    cdo.getBaseTimestamp());
                        } else {
                            cdo.setFlag(cdo.getBaseTimestamp());
                            cdo.put();
                            if (!(cdo.getTarget() == 1 && cdo.getAuthor().equals(
                                    CxGlobalParams.getInstance().getUserId()))
                                    && cdo.getIsRemind()) {
                                CalendarController.getInstance().setAlarmReminder(getActivity(),
                                        cdo.getRealTimestamp(), cdo.getId(), cdo.getCycle(), cdo.getFlag(),
                                        cdo.getContent());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                CxLog.e(TAG, "error=" + e.getMessage());
            }
        }
                
        
    }
    
    
    
    /**
     * 加载网上日历数据
     */
    private void loadCalendars() {
    	loadCycleCalFinish = false;
    	CalendarApi.getInstance().doCalendarMemoryday(memorialListCaller);
        CalendarApi.getInstance().doCalendarCycleList(mCycleUpdateTime, new JSONCaller() {

            @Override
            public int call(Object result) {
            	
            	try {
					JSONObject data = (JSONObject)result;
					if(data==null){
						sendNetDataFinish1();
						showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null), 0);
						
						return -1;
					}
					int rc=-1;
					if(!data.isNull("rc")){
						rc=data.getInt("rc");
					}
					if(rc==408){
						sendNetDataFinish1();
						showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null), 0);
						return -2;
					}
					if(rc!=0){
						sendNetDataFinish1();
						showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_fail), 0);
						return -3;
					}
					
					JSONObject obj=null;
					if(!data.isNull("data")){
						obj=data.getJSONObject("data");
					}
					
					
	                if (obj.isNull("update_time")) {
	                	sendNetDataFinish1();
	                    return 0;
	                }
					
					CalendarCycleList ccl = new CalendarCycleList(null, getActivity());
	                ccl.mData = obj;
	                ccl.put();
	                mCycleUpdateTime = ccl.getUpdateTime();

	                mCalendarSharedPreferences.edit().putInt(CxGlobalConst.S_CALENDAR_CYCLE_UPDATE_TIME, 
	                		ccl.getUpdateTime()).commit();
	                loadCycleCalFinish = true;
	                if (loadCycleCalFinish && loadMonthCalFinish) {
	                    android.os.Message calendarMessage = calendarHandler.obtainMessage(LOAD_CALENDAR_DATA, null);
	                    calendarMessage.sendToTarget();
	                }
					
				} catch (JSONException e) {					
					e.printStackTrace();
				}

                return 0;
            }
        });
        loadNetMonthList();
    }

    private void loadNetMonthList() {
        loadMonthCalFinish = false;
        CalendarApi.getInstance().doCalendarMonthList(mMonthDate, new JSONCaller() {

            @Override
            public int call(Object result) {
                try {
                	JSONObject data = (JSONObject)result;
                    if(data==null){
                    	sendNetDataFinish2();
                    	showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null), 0);
                    	return -1;
                    }
                    int rc=-1;
                    if(!data.isNull("rc")){
                    	rc=data.getInt("rc");
                    }
                    if(rc==408){
                    	sendNetDataFinish2();
                    	showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null), 0);
                    	return -2;
                    }
                    if(rc!=0){
                    	sendNetDataFinish2();
                    	showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_fail), 0);
                    	return -3;
                    }
        
                    CalendarMonthList cml = new CalendarMonthList(null, mMonthDate, getActivity());
                    cml.mData = data;
                    cml.mId = mMonthDate;
                    cml.mFlag = Integer.valueOf(mMonthDate);
                    cml.put();

                    loadMonthCalFinish = true;
                    if (loadCycleCalFinish && loadMonthCalFinish) {
                        android.os.Message calendarMessage = calendarHandler.obtainMessage(LOAD_CALENDAR_DATA, null);
                        calendarMessage.sendToTarget();
                    }
                } catch (JSONException e) {
                    CxLog.e(TAG, "doCalendarMonthList>>>" + e.getMessage());
                }

                return 0;
            }
        });
    }
    
    private void sendNetDataFinish1(){
    	loadCycleCalFinish = true;
        android.os.Message calendarMessage = calendarHandler.obtainMessage(LOAD_CALENDAR_DATA, null);
        calendarMessage.sendToTarget();
    }
    private void sendNetDataFinish2(){
    	loadMonthCalFinish = true;
    	android.os.Message calendarMessage = calendarHandler.obtainMessage(LOAD_CALENDAR_DATA, null);
        calendarMessage.sendToTarget();
    }
    
    

//    public static List<CalendarDataObj> newCalendarDataObj = new ArrayList<CalendarDataObj>();

    /**
     * 加载本地存储日历数据
     * 
     * @param day
     * @param ctx
     * @return
     */
    public void loadLocalCalendar(Context ctx) {
//        if (null != newCalendarDataObj && newCalendarDataObj.size() > 0) {
//            newCalendarDataObj.clear();
//        }
    	currentMonthMap.clear();
        
        List<Model> localMonthCalendar = new CalendarMonthList(null, mMonthDate, ctx).gets("flag="+ mMonthDate, new String[] {}, null, 0, 0);
        if (null != localMonthCalendar) {
            try {
                CalendarMonthList calendarmonthlist = (CalendarMonthList)(localMonthCalendar.get(0));
                JSONArray cml = calendarmonthlist.getMonthRemindArray();
                if (null != cml && cml.length() > 0) {
                	SimpleDateFormat format=new SimpleDateFormat("dd");
                    CalendarDayData data;
                    for (int j = 0; j < cml.length(); j++) {
                        JSONObject cjo = (JSONObject)cml.get(j);
                        CalendarDataObj c = new CalendarDataObj(cjo, getActivity());
                        CxLog.d(TAG, "loadLocalCalendar: reminder.id=" + c.mId);
                        long baseTimestamp = (long)c.getBaseTimestamp()*1000;
                        int key = Integer.parseInt(format.format(new Date(baseTimestamp)));
                        CxLog.i("CalendarView_men", ">>>>>>>>>>2:"+key);
                        
                        if(!currentMonthMap.containsKey(key)){
                        	data=new CalendarDayData();
                        }else{
                        	data=currentMonthMap.get(key);
                        }
          
                        if(c.getType()==0 && !data.isHasItem()){
                        	data.setHasItem(true);           	
                        }
                    	if(c.getType()==1 && !data.isHasMemorial()){
                    		data.setHasMemorial(true);
                    	}
                        if( !c.getIsRead()  && !c.getAuthor().equals(CxGlobalParams.getInstance().getUserId()) && data.isRead() ){
                        	data.setRead(false);
                        }
                        
                        ArrayList<CalendarDataObj> items;
                        if(data.getItems()==null){
                        	items=new ArrayList<CalendarDataObj>();
                        	data.setItems(items);
                        }else{
                        	items=data.getItems();
                        }
                        items.add(c);   
                        currentMonthMap.put(key, data);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // newCalendarDataObj = new ArrayList<CalendarDataObj>();
        List<Model> localCycleCalendar = new CalendarCycleList(null, ctx).gets("1=1",new String[] {}, null, 0, 0);
        if (null != localCycleCalendar) {
            try {
                Calendar nowCalendar = Calendar.getInstance();
                nowCalendar.setTime(new Date(System.currentTimeMillis()));
                CalendarCycleList calendarcyclelist = (CalendarCycleList)(localCycleCalendar.get(0));
                JSONArray cja = calendarcyclelist.getCycleRemindArray();
                if (null != cja && cja.length() > 0) {
                	CxLog.i("RkCalendarFragment_men", cja.toString());
                	SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
                    CalendarDayData data;
                    for (int j = 0; j < cja.length(); j++) {
                        JSONObject cjo = (JSONObject)cja.get(j);
                        CalendarDataObj c = new CalendarDataObj(cjo, getActivity());
                        CxLog.d(TAG, "loadLocalCalendar: reminder.id=" + c.mId);
                        long baseTimestamp = (long)c.getBaseTimestamp()*1000;   
                        
                        String newDate = format.format(new Date(baseTimestamp));
                        
                        int periodType = c.getCycle();
                        
                        Calendar nextAlarmCalendar = Calendar.getInstance();
                        nextAlarmCalendar.setTimeInMillis(baseTimestamp);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Integer.parseInt(mMonthDate.substring(0, 4)), Integer.parseInt(mMonthDate.substring(4))-1, mDay);
                        int key=0;
                        switch(periodType){
                            case CalendarDataObj.sReminderPeriodDaily:
                                int lastDate = calendar.getActualMaximum(Calendar.DATE);
                                //int day = nextAlarmCalendar.getInstance().get(Calendar.DAY_OF_MONTH);
                                for(int i=1; i<=lastDate;i++){
                                    Calendar calTemp = Calendar.getInstance();
                                    calTemp.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                                    calTemp.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                                    calTemp.set(Calendar.DAY_OF_MONTH, i);
                                    calTemp.set(Calendar.HOUR, nextAlarmCalendar.get(Calendar.HOUR));
                                    calTemp.set(Calendar.MINUTE, nextAlarmCalendar.get(Calendar.MINUTE));
                                    calTemp.set(Calendar.SECOND, nextAlarmCalendar.get(Calendar.SECOND));
                                    if(calTemp.before(nextAlarmCalendar)){
                                        continue;
                                    }
                                    key = i;
                                    addData(key, c, false);
                                }
                                break;
                            case CalendarDataObj.sReminderPeriodWeekly:
                                //int day = nextAlarmCalendar.getInstance().get(Calendar.DAY_OF_WEEK);
                                List<Calendar> cals = CalendarUtil.getInstance().getCurMonthTheWeekCalendars(nextAlarmCalendar, 
                                        Integer.parseInt(mMonthDate.substring(4)));
                                int m=0;
                                for(int i=0; i<cals.size(); i++){
                                    if(null == cals.get(i)){
                                        continue;
                                    }
                                    key = cals.get(i).get(Calendar.DAY_OF_MONTH);
                                    if(cals.get(i).before(nowCalendar)){
                                        addData(key, c, false);
                                    } else if(CalendarUtil.getInstance().isSameMonth(cals.get(i), nowCalendar)){
                                        if(m>0){
                                            addData(key, c, false);
                                        } else {
                                            addData(key, c, true);
                                        }
                                        m+=1;
                                    } else {
                                        addData(key, c, false);
                                    }
                                }
                                break;
                                
                            case CalendarDataObj.sReminderPeriodMonthly:
                                if(CalendarUtil.getInstance().beforeMonth(calendar, nextAlarmCalendar)){
                                    return;
                                }
                                DateData dd = DateUtil.getNumberTime(nextAlarmCalendar.getTime());
                                key = dd.getDay();
                                if(CalendarUtil.getInstance().isSameMonth(nowCalendar, calendar)){
                                    addData(key, c, true);
                                } else {
                                    addData(key, c, false);
                                }
                                break;
                            case CalendarDataObj.sReminderPeriodAnnually:
                                if(CalendarUtil.getInstance().beforeYear(calendar, nextAlarmCalendar)){
                                    return;
                                }
                                DateData dd1 = DateUtil.getNumberTime(nextAlarmCalendar.getTime());
                                int month = Integer.parseInt(mMonthDate.substring(4));
                                int year = Integer.parseInt(mMonthDate.substring(0,4));
                                int nowYear = nowCalendar.get(Calendar.YEAR);
                                //RkLog.i(TAG, "shichao.wang month="+month);
                                if(dd1.getMonth()==(month-1)){
                                    key = dd1.getDay();
                                    if(year == nowYear){
                                        addData(key, c, true);
                                    } else {
                                        addData(key, c, false);
                                    }
                                }
                                
                                break;
                                default:
                                    if(newDate.substring(0, 6).equals(mMonthDate)){
                                        key=Integer.parseInt(newDate.substring(6));
                                     }else{
                                        continue ;
                                     }
                                    addData(key, c, true);
                                    break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    private void addData(int key, CalendarDataObj c, boolean isShowUnRead ){
        CalendarDayData data;
        if (currentMonthMap.get(key) == null) {
            data = new CalendarDayData();
        } else {
            data = currentMonthMap.get(key);
        }

        if (c.getType() == 0 && !data.isHasItem()) {
            data.setHasItem(true);
        }
        if (c.getType() == 1 && !data.isHasMemorial()) {
            data.setHasMemorial(true);
        }
        if (!c.getIsRead() && !c.getAuthor().equals(CxGlobalParams.getInstance().getUserId())
                && data.isRead() && isShowUnRead) {
            data.setRead(false);
        }
        ArrayList<CalendarDataObj> items;
        if (data.getItems() == null) {
            items = new ArrayList<CalendarDataObj>();
            data.setItems(items);
        } else {
            items = data.getItems();
        }
        items.add(c);
        currentMonthMap.put(key, data);
    }

    /**
     * 获取今天的事项或者纪念日
     * 
     * @param day
     * @param allCalendar
     * @return
     */
    public ArrayList<CalendarDataObj> getCurrentDayCalendar(int day) {
        
    	
        CxLog.i("men>>>", ">>>>>>>>>>>>>1");
        if(currentMonthMap==null || currentMonthMap.size()<=0){
        	CxLog.i("men>>>", ">>>>>>>>>>>>>2");
        	return null;
        }
        
        CalendarDayData calendarDayData = currentMonthMap.get(day);
        if(calendarDayData==null || calendarDayData.getItems()==null){
        	CxLog.i("men>>>", ">>>>>>>>>>>>>3");
        	return null;
        }
        ArrayList<CalendarDataObj> mCalendarListData=calendarDayData.getItems();
        CxLog.i("men>>>", ">>>>>>>>>>>>>4"+calendarDayData.getItems().size());
        return mCalendarListData;
    }

    private void updateListCalendarRemind(Object result) {
        if (null == result) {
            return;
        }
        JSONObject remindList = (JSONObject)result;
        if (remindList.isNull("update_time")) {
            //RkLog.i(TAG, "remindList come in");
            return;
        }
        CalendarRemindList crl = new CalendarRemindList(null, getActivity());
        crl.mData = remindList;
        crl.put();
        mRemindListUpdateTime = crl.getUpdateTime();
        CxLog.i(TAG, "mRemindListUpdateTime=" + mRemindListUpdateTime);
        mCalendarSharedPreferences.edit().putInt(CxGlobalConst.S_CALENDAR_REMIND_UPDATE_TIME, crl.getUpdateTime()).commit();
//        JSONArray remindArray = crl.getRemindArray();
//        if (null == remindArray) {
//            return;
//        }
//        for (int i = 0; i < remindArray.length(); i++) {
//            try {
//                JSONObject remindObj = remindArray.getJSONObject(i);
//                CalendarDataObj cdo = new CalendarDataObj(null, getActivity());
//                cdo.mData = remindObj;
//                if(cdo.adjust() == -1){
//                    RkLog.v(TAG, "falg 1 drop" + cdo.getBaseTimestamp());
//                    CalendarController.getInstance().cancelAlarmReminder(getActivity(),
//                            cdo.getBaseTimestamp());
//                } else {
//                    cdo.setFlag(cdo.getBaseTimestamp());
//                    cdo.put();
//                    if (!(cdo.getTarget() == 1 && cdo.getAuthor().equals(
//                            RkGlobalParams.getInstance().getUserId()))
//                            && cdo.getIsRemind()) {
//                        CalendarController.getInstance().setAlarmReminder(getActivity(),
//                                cdo.getRealTimestamp(), cdo.getId(), cdo.getCycle(), cdo.getFlag(),
//                                cdo.getContent());
//                    }
//                }
//            } catch (Exception e) {
//                RkLog.e(TAG, "updateListCalendarRemind" + e.getMessage());
//            }
//        }
    }

    private View addItemView(final CalendarDataObj calendar) {
        if(null == getActivity()){
            return null;
        }
        View view = View.inflate(getActivity(), R.layout.cx_fa_fragment_calendar_item, null);
        LinearLayout itemWhole = (LinearLayout)view.findViewById(R.id.cx_fa_calendar_item_whole_lv);
        itemWhole.setOnClickListener(itemListener);
        CxImageView imageIcon = (CxImageView)view.findViewById(R.id.cx_fa_calendar_item_icon_iv);
        TextView itemContent = (TextView)view.findViewById(R.id.cx_fa_calendar_item_content_tv);
        ImageView memorialImageView = (ImageView)view
                .findViewById(R.id.cx_fa_calendar_item_memorial_iv);
        ImageView clockImageView = (ImageView)view.findViewById(R.id.cx_fa_calendar_item_clock_iv);
        ImageView privateImageView = (ImageView)view
                .findViewById(R.id.cx_fa_calendar_item_private_iv);
        LinearLayout itemTimeAndTarget = (LinearLayout)view
                .findViewById(R.id.cx_fa_calendar_item_time_and_target_layout);
        TextView itemTime = (TextView)view.findViewById(R.id.cx_fa_calendar_item_time_tv);
        TextView itemTarget = (TextView)view.findViewById(R.id.cx_fa_calendar_item_target_tv);
        if (calendar.isShowMemorial()) {
            memorialImageView.setVisibility(View.VISIBLE);
        } else {
            memorialImageView.setVisibility(View.GONE);
        }
        if (calendar.getIsRemind()) {
            clockImageView.setVisibility(View.VISIBLE);
            itemTimeAndTarget.setVisibility(View.VISIBLE);
            if (calendar.getStatus() == 0) {
                itemTime.setText(mDisplayUtility.createNLSReminderPeriodTimeAndMorningTag(calendar));
                itemTarget.setText(mDisplayUtility.createNLSReminderTipLabel(calendar));
            } else {
                itemTime.setText(getString(R.string.cx_fa_calendar_status_remind_expire));
                itemTarget.setText("");
            }
        } else {
            clockImageView.setVisibility(View.GONE);
            itemTimeAndTarget.setVisibility(View.GONE);
        }
        if (calendar.isShowPrivate()) {
            privateImageView.setVisibility(View.VISIBLE);
        } else {
            privateImageView.setVisibility(View.GONE);
        }
        if (TextUtils.equals(calendar.getAuthor(), CxGlobalParams.getInstance().getUserId())) { // 属于自己创建
                                                                                                // cx_fa_hb_icon_small
            imageIcon.displayImage(imageLoader, CxGlobalParams.getInstance().getIconSmall(),
                    CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, CxGlobalParams.getInstance()
                            .getSmallImgConner());
            clockImageView.setBackgroundResource(CxResourceDarwable.getInstance().dr_calendar_clock_img_me);
        } else { // 对方创建
            imageIcon.displayImage(imageLoader, CxGlobalParams.getInstance().getPartnerIconBig(),
            		CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, CxGlobalParams.getInstance()
                            .getMateSmallImgConner());
            clockImageView.setBackgroundResource(CxResourceDarwable.getInstance().dr_calendar_clock_img_oppo);
        }
        itemContent.setText(calendar.getContent());
        itemWhole.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
        
                if (calendar.getType() == 0) { // 事项
                    CalendarController.getInstance().setData(calendar);
                    Intent editIntent = new Intent(getActivity(), CxCalendarItemActivity.class);
                    startActivity(editIntent);
                    getActivity().overridePendingTransition(R.anim.tran_next_in,
                            R.anim.tran_next_out);
                } else { // 纪念日
                    CalendarController.getInstance().setData(calendar);
                    Intent editIntent = new Intent(getActivity(),CxCalendarMemorialDayActivity.class);
                    startActivity(editIntent);
                    getActivity().overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out);

                }

            }
        });
        itemWhole.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                DialogUtil du = DialogUtil.getInstance();
                du.setOnSureClickListener(new OnSureClickListener() {

                    @Override
                    public void surePress() {
                        dropCalendar(calendar.getId(), calendar.getFlag(), calendar.getIsRemind());
                    }
                });
                du.getSimpleDialog(getActivity(), null, getString(R.string.cx_fa_reminder_del_msg),
                        null, null).show();

                return false;
            }
        });
        return view;
    }

    private void dropCalendar(String id, final int flag, final boolean is_remind) {
//        RkLoadingUtil.getInstance().showLoading(getActivity(), true);
    	DialogUtil.getInstance().getLoadingDialogShow(getActivity(), -1);
        CalendarApi.getInstance().doDeleteReminder(id, new JSONCaller() {

            @Override
            public int call(Object result) {
                if (is_remind) {
                    CalendarController.getInstance().cancelAlarmReminder(getActivity(), flag);
                }
                loadCalendars();
//                loadLocalCalendar(getActivity());
//                RkLoadingUtil.getInstance().dismissLoading();
                DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
                return 0;
            }
        });
    }

    private void initTitle(View inflate) {

        ImageButton mMenu = (ImageButton)inflate.findViewById(R.id.cx_fa_calendar_title_menu_ib);
        mTextLayout = (LinearLayout)inflate.findViewById(R.id.cx_fa_calendar_title_text_layout);
        mTileText = (TextView)inflate.findViewById(R.id.cx_fa_calendar_title_text_tv);
        mTitleImg = (ImageView)inflate.findViewById(R.id.cx_fa_calendar_title_text_iv);
        ImageButton mAdd = (ImageButton)inflate.findViewById(R.id.cx_fa_calendar_title_add_ib);

        mMenu.setOnClickListener(titleListener);
        mTextLayout.setOnClickListener(titleListener);
        mAdd.setOnClickListener(titleListener);

    }

    private OnClickListener titleListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_calendar_title_menu_ib:
                    ((CxMain)getActivity()).toggleMenu();
                    break;
                case R.id.cx_fa_calendar_title_text_layout:
                    showTitleList();
                    break;
                case R.id.cx_fa_calendar_title_add_ib:
                    CalendarController controller = CalendarController.getInstance();
                    // controller.reset();
                    Intent editIntent = new Intent(getActivity(), CxCalendarEditActivity.class);
                    editIntent.putExtra(CxCalendarParam.CALENDAR_EDIT_TYPE, editType);
                    editIntent.putExtra(CxCalendarParam.CALENDAR_EDIT_MODE,CxCalendarParam.CALENDAR_EDIT_ADD);
                    editIntent.putExtra(CxCalendarParam.CALENDAR_EDIT_DATE,mDayStr);
                    startActivity(editIntent);
                    getActivity().overridePendingTransition(R.anim.tran_next_in,
                            R.anim.tran_next_out);
                    break;
                case R.id.cx_fa_calendar_title_list_homepage_layout:

                    type = 1;
                    changeCalendarState();
                    mPop.dismiss();
                    break;
                case R.id.cx_fa_calendar_title_list_memorial_day_layout:
                    type = 2;
                    changeCalendarState();
                    mPop.dismiss();
                    break;

                default:
                    break;
            }

        }
    };

    private OnClickListener contentListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_calendar_add_item_layout:
                    Intent editIntent = new Intent(getActivity(), CxCalendarEditActivity.class);
                    editIntent.putExtra(CxCalendarParam.CALENDAR_EDIT_TYPE,CxCalendarParam.CALENDAR_TYPE_ITEM);
                    editIntent.putExtra(CxCalendarParam.CALENDAR_EDIT_MODE,CxCalendarParam.CALENDAR_EDIT_ADD);
                    editIntent.putExtra(CxCalendarParam.CALENDAR_EDIT_DATE,mDayStr);
                    startActivity(editIntent);
                    getActivity().overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out);
                    break;
                // case R.id.cx_fa_calendar_add_memorial_day_layout:
                // Intent editIntent2 = new Intent(getActivity(),
                // RkCalendarEditActivity.class);
                // editIntent2.putExtra(RkCalendarParam.CALENDAR_EDIT_TYPE,
                // RkCalendarParam.CALENDAR_TYPE_MEMORIAL);
                // editIntent2.putExtra(RkCalendarParam.CALENDAR_EDIT_MODE,
                // RkCalendarParam.CALENDAR_EDIT_ADD);
                // startActivity(editIntent2);
                // getActivity().overridePendingTransition(R.anim.tran_next_in,
                // R.anim.tran_next_out);
                // break;

                default:
                    break;
            }

        }
    };

    private OnClickListener itemListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_calendar_item_whole_lv:
                    // TODO 跳转到详情页
                    break;
                default:
                    break;
            }
        }
    };

    private LinearLayout itemList;

    protected void showTitleList() {

        mTitleImg.setImageResource(R.drawable.calendar_btn_up);

        View inflate = View.inflate(getActivity(), R.layout.cx_fa_widget_calendar_title_list, null);
        titleHomepageLayout = (LinearLayout)inflate
                .findViewById(R.id.cx_fa_calendar_title_list_homepage_layout);
        homepageText = (TextView)inflate.findViewById(R.id.cx_fa_calendar_title_list_homepage_tv);
        titleMemorialLayout = (LinearLayout)inflate
                .findViewById(R.id.cx_fa_calendar_title_list_memorial_day_layout);
        memorialText = (TextView)inflate
                .findViewById(R.id.cx_fa_calendar_title_list_memorial_day_tv);

        changeListState();
        titleHomepageLayout.setOnClickListener(titleListener);
        titleMemorialLayout.setOnClickListener(titleListener);

        mPop = new PopupWindow(inflate, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        mPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPop.showAsDropDown(mTextLayout, 0, ScreenUtil.dip2px(getActivity(), 10));

        mPop.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                mTitleImg.setImageResource(R.drawable.calendar_btn_down);
            }
        });

    };

  
    @SuppressWarnings("deprecation")
	private void changeListState() {
        if (type == 1) {
            titleHomepageLayout.setBackgroundResource(R.drawable.cx_fa_calendar_title_list_selected_bg);
            homepageText.setTextColor(Color.rgb(254, 182, 31));
            titleMemorialLayout.setBackgroundDrawable(null);
            memorialText.setTextColor(Color.WHITE);
        } else {
            titleMemorialLayout.setBackgroundResource(R.drawable.cx_fa_calendar_title_list_selected_bg);
            memorialText.setTextColor(Color.rgb(254, 182, 31));
            titleHomepageLayout.setBackgroundDrawable(null);
            homepageText.setTextColor(Color.WHITE);
        }
    }

    private void changeCalendarState() {
        if (type == 1) {
            editType = CxCalendarParam.CALENDAR_TYPE_ITEM;
            mTileText.setText(R.string.cx_fa_calendar_homepage_title1);
            homepageLayout.setVisibility(View.VISIBLE);
            memorialLayout.setVisibility(View.INVISIBLE);
        } else {
            editType = CxCalendarParam.CALENDAR_TYPE_MEMORIAL;
            mTileText.setText(R.string.cx_fa_calendar_homepage_title2);
            memorialLayout.setVisibility(View.VISIBLE);
            homepageLayout.setVisibility(View.INVISIBLE);
        }
    }

    private class MemorialAdapter extends BaseAdapter {

    	private ArrayList<CalendarDataObj> memorialDownDays=new ArrayList<CalendarDataObj>();
    	private ArrayList<CalendarDataObj> memorialUpDays=new ArrayList<CalendarDataObj>();
    	
    	private void  updateAdapter(ArrayList<CalendarDataObj> downDays,ArrayList<CalendarDataObj> upDays){
    	
    		memorialDownDays.clear();
    		memorialUpDays.clear();
    		if(downDays!=null){
    			memorialDownDays=downDays;
    		}
    		if(upDays!=null){
    			memorialUpDays=upDays;
    		}
    		this.notifyDataSetChanged();
    	}

        @Override
        public int getCount() {
            return memorialDownDays.size() + memorialUpDays.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

        	if(position==0){
        	
				View view = View.inflate(getActivity(),R.layout.cx_fa_fragment_neighbour_header, null);
				

				CxImageView nbBackground = (CxImageView) view.findViewById(R.id.cx_fa_neighbour_bg);
				
				int height=(int)(getResources().getDisplayMetrics().widthPixels * 0.46f + 0.5f);				
				FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
				nbBackground.setLayoutParams(params);
				
				
				nbBackground.setImageResource(R.drawable.calendar_image_memorial);


				LinearLayout familyIcon = (LinearLayout) view.findViewById(R.id.cx_fa_neighbour_family_small_icon);
				familyIcon.setVisibility(View.GONE);
				LinearLayout familyText = (LinearLayout) view.findViewById(R.id.cx_fa_neighbour_family_text);
				familyText.setVisibility(View.GONE);
				return view;
        	}
        	
        	int tempPos=position-1;
        	
            if (tempPos < memorialDownDays.size() + memorialUpDays.size()) {

                // ViewHolder holder = null;

                convertView = View.inflate(getActivity(),
                        R.layout.cx_fa_fragment_calendar_memorial_day_item, null);
                
                LinearLayout memorialLayout = (LinearLayout) convertView.findViewById(R.id.cx_fa_calendar_memorial_day_item_layout);
                // holder = new ViewHolder();
                TextView mDateText = (TextView)convertView
                        .findViewById(R.id.cx_fa_calendar_memorial_day_date_tv);
                TextView mContentText = (TextView)convertView
                        .findViewById(R.id.cx_fa_calendar_memorial_day_content_tv);
                TextView mNumberText = (TextView)convertView
                        .findViewById(R.id.cx_fa_calendar_memorial_day_number_tv);

                CalendarDataObj obj = null;
                int size = memorialDownDays.size();
                if (tempPos < memorialDownDays.size()) {
                    obj = memorialDownDays.get(tempPos);
                } else {
                    obj = memorialUpDays.get(tempPos - size);
                }
                long baseTimestamp = (long)obj.getBaseTimestamp() * 1000;
                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                String dateStr = format.format(new Date(baseTimestamp));
                mDateText.setText(dateStr);
                mContentText.setText(obj.getContent());

                Calendar nowC = Calendar.getInstance();
                nowC.setTime(new Date(System.currentTimeMillis()));
                nowC.set(Calendar.HOUR_OF_DAY, 9);
                nowC.set(Calendar.MINUTE, 0);
                nowC.set(Calendar.SECOND, 0);
                long nowTime = nowC.getTime().getTime();
                mNumberText.setText("");
                if (tempPos < size) {

                    Calendar oldC = Calendar.getInstance();
                    oldC.setTimeInMillis(baseTimestamp);
                    oldC.set(Calendar.YEAR, nowC.get(Calendar.YEAR));
                    int day = 0;
                    CxLog.i("RkCalendarFragment_men", obj.getIsLunar() + "");
                    if (obj.getIsLunar() == 1) {
                        String[] split = dateStr.split("\\.");
                        ChineseCalendar CC = new ChineseCalendar(Integer.parseInt(split[0]),
                                (Integer.parseInt(split[1]) - 1), Integer.parseInt(split[2]));
                        int cMonth = CC.get(ChineseCalendar.CHINESE_MONTH);
                        int cDay = CC.get(ChineseCalendar.CHINESE_DATE);
                        
                        mDateText.setText(dateStr+" "+TextUtil.getLunarMonth(cMonth)+TextUtil.getLunarDay(cDay));
                        
                        ChineseCalendar CC2 = new ChineseCalendar(true, nowC.get(Calendar.YEAR),
                                cMonth, cDay);
                        Calendar c1 = Calendar.getInstance();
                        c1.set(CC2.get(Calendar.YEAR), CC2.get(Calendar.MONTH),
                                CC2.get(Calendar.DAY_OF_MONTH), 10, 0,0);
                        long oldTime = c1.getTime().getTime();

                        // RkLog.i("RkCalendarFragment_men",
                        // split[0]+":"+split[1]+":"+split[2]);
                        // RkLog.i("RkCalendarFragment_men",
                        // CC.get(ChineseCalendar.CHINESE_YEAR)+":"+cMonth+":"+cDay);
                        // RkLog.i("RkCalendarFragment_men",
                        // CC2.getSimpleGregorianDateString());

                        if (oldTime < nowTime) {
                            CC2 = new ChineseCalendar(true, nowC.get(Calendar.YEAR) + 1, cMonth,
                                    cDay);
                            c1.set(CC2.get(Calendar.YEAR), CC2.get(Calendar.MONTH),
                                    CC2.get(Calendar.DAY_OF_MONTH), 10, 0,0);
                            oldTime = c1.getTime().getTime();
                        }
                        day = (int)((oldTime - nowTime) / (1000 * 3600 * 24));
                    } else {
                        long oldTime = oldC.getTime().getTime();
                        if (oldTime < nowTime) {
                            oldC.set(Calendar.YEAR, nowC.get(Calendar.YEAR) + 1);
                            oldTime = oldC.getTime().getTime();
                        }
                        day = (int)((oldTime - nowTime) / (1000 * 3600 * 24));
                    }
                    if(day==0){
                    	mNumberText.setText(TextUtil.getNewSpanStr("今天", 14, Color.rgb(177, 44, 44)));
                    }else{
                    	mNumberText.setText(TextUtil.getNewSpanStr(day + "天后", 14, Color.rgb(177, 44, 44)));
                    }
                    
                } else {

                    Calendar oldC = Calendar.getInstance();
                    oldC.setTimeInMillis(baseTimestamp);
                    long oldTime = oldC.getTime().getTime();
                    int day = 0;
                    if (oldTime < nowTime) {
                        day = (int)((nowTime - oldTime) / (1000 * 3600 * 24))+1;                    
                        mNumberText.setText(TextUtil.getNewSpanStr("已"+day + "天", 14, Color.rgb(131, 169, 24)));                       
                    }else{
                    	day = (int)((oldTime - nowTime) / (1000 * 3600 * 24));
                        if(day==0){
                        	mNumberText.setText(TextUtil.getNewSpanStr("今天", 14, Color.rgb(131, 169, 24)));
                        }else{
                             mNumberText.setText(TextUtil.getNewSpanStr(day + "天后", 14, Color.rgb(131, 169, 24)));
                        }
                    }
                }
                
                final CalendarDataObj calendar=obj;
                memorialLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						CalendarController.getInstance().setData(calendar);
	                    Intent editIntent = new Intent(getActivity(),CxCalendarMemorialDayActivity.class);
	                    startActivity(editIntent);
	                    getActivity().overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out);
						
					}
				});

            } else {
                convertView = View.inflate(getActivity(),R.layout.cx_fa_fragment_calendar_memorial_day_item2, null);
                LinearLayout addLayout = (LinearLayout)convertView
                        .findViewById(R.id.cx_fa_calendar_add_memorial_day_layout);
                addLayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent editIntent2 = new Intent(getActivity(), CxCalendarEditActivity.class);
                        editIntent2.putExtra(CxCalendarParam.CALENDAR_EDIT_TYPE,
                                CxCalendarParam.CALENDAR_TYPE_MEMORIAL);
                        editIntent2.putExtra(CxCalendarParam.CALENDAR_EDIT_MODE,
                                CxCalendarParam.CALENDAR_EDIT_ADD);
                        editIntent2.putExtra(CxCalendarParam.CALENDAR_EDIT_DATE,mDayStr);
                        startActivity(editIntent2);
                        getActivity().overridePendingTransition(R.anim.tran_next_in,
                                R.anim.tran_next_out);

                    }
                });
            }

            return convertView;
        }

    }

    // class ViewHolder {
    // TextView mDateText;
    //
    // TextView mContentText;
    //
    // TextView mNumberText;
    // }

    JSONCaller memorialListCaller = new JSONCaller() {

        @Override
        public int call(Object result) {
            if (result == null) {
            	if(type==2){
            		 showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_fail), 0);
            	}
            	showMemorialList();
                return -1;
            }
            // RkLog.i("RkCalendarFragment_men", result.toString());
            updateMemorialList(result);

            showMemorialList();

//            isMemorialFirst = false;
            calendarHandler.sendEmptyMessage(UPDATE_MEMO_ADAPTER);
            return 0;
        }
    };

    private MemorialAdapter memorialAdapter;

	private LinearLayout spaceLayout;

    protected void updateMemorialList(Object result) {
        CalendarMemoryDay memorialList = new CalendarMemoryDay(null, System.currentTimeMillis() + "", getActivity());
        memorialList.mData = (JSONObject)result;
        memorialList.put();
    }

    protected void showMemorialList() {

    	memorialDownDays.clear();
    	memorialUpDays.clear();
    	
        List<Model> gets = new CalendarMemoryDay(null, "", getActivity()).gets("1=1",
                new String[] {}, null, 0, 0);
        if(null==gets){
        	return ;
        }
        CalendarMemoryDay memorialDay = (CalendarMemoryDay)gets.get(0);

        if(memorialDay==null){
        	return ;
        }
        
        try {
            JSONArray downArray = memorialDay.getTimeDownArray();
            // RkLog.i("RkCalendarFragment_men", downArray.toString());
            for (int j = 0; j < downArray.length(); j++) {
                JSONObject cjo = (JSONObject)downArray.get(j);
                CalendarDataObj calendar = new CalendarDataObj(cjo, getActivity());
                CxLog.d(TAG, "loadLocalCalendar: reminder.id=" + calendar.mId);
                memorialDownDays.add(calendar);
            }

            JSONArray upArray = memorialDay.getTimeUpArray();

            // RkLog.i("RkCalendarFragment_men", upArray.toString());
            for (int j = 0; j < upArray.length(); j++) {
                JSONObject cjo = (JSONObject)upArray.get(j);
                CalendarDataObj calendar = new CalendarDataObj(cjo, getActivity());
                CxLog.d(TAG, "loadLocalCalendar: reminder.id=" + calendar.mId);
                memorialUpDays.add(calendar);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param info
     * @param number 0 失败；1 成功；2 不要图。
     */
    private void showResponseToast(String info, int number) {
        Message msg = new Message();
        msg.obj = info;
        msg.arg1 = number;
        new Handler(CxApplication.getInstance().getMainLooper()) {
            public void handleMessage(Message msg) {
                if ((null == msg) || (null == msg.obj)) {
                    return;
                }
                int id = -1;
                if (msg.arg1 == 0) {
                    id = R.drawable.chatbg_update_error;
                } else if (msg.arg1 == 1) {
                    id = R.drawable.chatbg_update_success;
                }
                ToastUtil.getSimpleToast(getActivity(), id, msg.obj.toString(), 1).show();
            };
        }.sendMessage(msg);
    }

}
