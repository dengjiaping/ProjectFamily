// Copyright 2012 Square, Inc.

package com.squareup.timessquare;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import com.chuxin.family.R;
import com.chuxin.family.calendar.Lunar;
import com.chuxin.family.calendar.CxCalendarFragment;
import com.chuxin.family.calendar.CxCalendarParam;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.mate.CxFamilyInfoActivity;
import com.chuxin.family.models.CalendarDataObj;
import com.chuxin.family.net.CxMateProfileApi;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.parse.been.data.CalendarData;
import com.chuxin.family.parse.been.data.CalendarDayData;
import com.chuxin.family.parse.been.data.DateData;
import com.chuxin.family.utils.CalendarUtil;
import com.chuxin.family.utils.DateUtil;
import com.chuxin.family.utils.Globals;
import com.chuxin.family.utils.CxLog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 * 
 * @author shichao.wang 自定义日历控件
 *
 */
public class CalendarView extends LinearLayout { 
    private TextView title;

    private ImageView preMonth;

    private ImageView nextMonth;

    private LinearLayout preMonthLayout;

    private LinearLayout nextMonthLayout;

    private RelativeLayout curMonth;

    private TextView detailMonth;

    CalendarGridView grid;

    private CellClickListener cellClickListener = new CellClickedListener();

    private OnDateSelectedListener dateListener;

 

    private OnInvalidDateSelectedListener invalidDateListener = new DefaultOnInvalidDateSelectedListener();

    private OnDetailClickListener detailClickListener;

    final List<MonthCellDescriptor> selectedCellDess = new ArrayList<MonthCellDescriptor>();

    private List<CalendarCellView> calendarCellViewList = new ArrayList<CalendarCellView>();

    final List<Calendar> selectedCals = new ArrayList<Calendar>();

    final Map<String, List<Calendar>> markCalsMap = new HashMap<String, List<Calendar>>();

    final Map<String, List<Calendar>> markTextCalsMap = new HashMap<String, List<Calendar>>();

    MonthDescriptor currentMonthDes;

    private List<List<MonthCellDescriptor>> currentMonthCells;

    private Locale locale;

    private Calendar today;

    private Calendar monthCounter;

    private Calendar currentMonthRecord;

    private DateFormat monthNameFormat;

    private DateFormat weekdayNameFormat;

    private Context context;
    
    private int positionOfOne;
    
    private boolean is_landspace;

    // private CalendarCellView mCurrentSelectCellView = null;

    private int[] titles = new int[] {
            R.string.mon, R.string.tue, R.string.wed, R.string.thu, R.string.fri, R.string.sat,
            R.string.sun
    };



    public CalendarView(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.month_inner, this, true);
        onFinishInflate();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.calendar_cell);
        is_landspace = typedArray.getBoolean(R.styleable.calendar_cell_is_landspace, false);
        CxLog.i("CalendarView", "mIsLandspace=" + is_landspace);
        LayoutInflater.from(context).inflate(R.layout.month_inner, this, true);
    }





    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        title = (TextView)findViewById(R.id.title);
        preMonth = (ImageView)findViewById(R.id.btn_pre_month);
        nextMonth = (ImageView)findViewById(R.id.btn_next_month);
        preMonthLayout = (LinearLayout)findViewById(R.id.btn_pre_month_ll);
        nextMonthLayout = (LinearLayout)findViewById(R.id.btn_next_month_ll);
        grid = (CalendarGridView)findViewById(R.id.calendar_grid);
        curMonth = (RelativeLayout)findViewById(R.id.btn_today);
        LinearLayout  detailMonthLayout = (LinearLayout)findViewById(R.id.btn_detail);
        detailMonth = (TextView)findViewById(R.id.btn_detail_tv);
        if (is_landspace) {
            detailMonth.setText("简");
        } else {
            detailMonth.setText("详");
        }

        title.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showBirthdayDialog();
            }
        });

        preMonthLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonth();
            }
        });
        nextMonthLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth();
            }
        });

        curMonth.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                currentMonth();
            }
        });
        detailMonthLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (null != detailClickListener) {
                    detailClickListener.onDetailClick();
                }
            }
        });
        initData(context);
    }

    public void nextMonth() {
    	Calendar c=Calendar.getInstance();
    	c.setTime(monthCounter.getTime());
        c.add(MONTH, 1);
        selectDate(c,true);
        
       
    }

    public void currentMonth() {
        System.out.println("today=" + Calendar.getInstance(locale).getTime().toString());
        System.out.println("today1=" + monthCounter.getTime().toString());
        Calendar c = Calendar.getInstance(locale);
        selectDate(c,true);      
    }

    public void previousMonth() {
    	Calendar c=Calendar.getInstance();
    	c.setTime(monthCounter.getTime());
        c.add(MONTH, -1);
        selectDate(c,true);     
    }
    
    private void initData(Context context) {
        locale = Locale.getDefault();
        today = Calendar.getInstance(locale);
        monthCounter = Calendar.getInstance(locale);
//        currentMonthRecord = monthCounter;

        monthNameFormat = new SimpleDateFormat(context.getString(R.string.month_name_format),
                locale);
        weekdayNameFormat = new SimpleDateFormat(context.getString(R.string.day_name_format),
                locale);

        final CalendarRowView headerRow = (CalendarRowView)grid.getChildAt(0);
        for (int offset = 0; offset < 7; offset++) {
            final TextView textView = (TextView)headerRow.getChildAt(offset);
            textView.setText(titles[offset]);
        }

        initAndUpdateCalendar(true);
    }

    private void initAndUpdateCalendar(boolean isFirst) {        
        MonthDescriptor month = new MonthDescriptor(monthCounter.get(MONTH),monthCounter.get(YEAR), 
        		monthCounter, monthNameFormat.format(monthCounter.getTime()));
        List<List<MonthCellDescriptor>> cells = getMonthCells(month, monthCounter,isFirst);
        currentMonthDes = month;
        currentMonthCells = cells;
        // RkLog.i("CalendarView", "initAndUpdateCalendar");
        updateCellViews(month, cells, false);
    }

 
    public List<List<MonthCellDescriptor>> getMonthCells(MonthDescriptor month, Calendar startCal,boolean isFirst) {
        Calendar cal = Calendar.getInstance(locale);
        cal.setTime(startCal.getTime());
        List<List<MonthCellDescriptor>> cells = new ArrayList<List<MonthCellDescriptor>>();
        cal.set(DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(DAY_OF_WEEK);
        int offset = cal.getFirstDayOfWeek() - firstDayOfWeek + 1; // add by
                                                                   // shichao
                                                                   // 调整为按周一为第一天的偏移
        if (offset > 0) {
            offset -= 7;
        }
        
        positionOfOne=-offset;
        
        HashMap<Integer,CalendarDayData> monthMap = CxCalendarParam.getInstance().getCurrentMonthMap();
        
        CxLog.i("CalendarView_men", (monthMap ==null)+">>>"+isFirst); 
        
        if(monthMap !=null && isFirst){
        	monthMap=null;
        }
        
        
        cal.add(Calendar.DATE, offset);
        while ((cal.get(MONTH) < month.getMonth() + 1 || cal.get(YEAR) < month.getYear()) //
                && cal.get(YEAR) <= month.getYear()) {
            //Logr.d("Building week row starting at %s", cal.getTime());
            List<MonthCellDescriptor> weekCells = new ArrayList<MonthCellDescriptor>();
            cells.add(weekCells);
            for (int c = 0; c < 7; c++) {
                Date date = cal.getTime();
                boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();
                boolean isSelected = isCurrentMonth && CalendarUtil.getInstance().isSame(cal, startCal);
                boolean isSelectable = isCurrentMonth;
                boolean isToday = isCurrentMonth && sameDate(cal, today);
                int value = cal.get(DAY_OF_MONTH);
                Lunar lunar = new Lunar(cal);
                String str = "";
                if (lunar.isSFestival()) {
                    str = lunar.getSFestivalName();
                } else if (lunar.isLFestival()) {
                    str = lunar.getLFestivalName();
                } else if (!TextUtils.isDigitsOnly(lunar.getTermString())) {
                    str = lunar.getTermString();
                } else {
                    str = lunar.getLunarDayString();
                }
                
                boolean isShowItem=false;
                boolean isShowMemorial=false;
                boolean isRead=true;
                
                List<CalendarDataObj> mListCalendarDatas = new ArrayList<CalendarDataObj>();
               
                if(monthMap!=null &&  !monthMap.isEmpty() && isCurrentMonth){
 
                	CalendarDayData data = monthMap.get(value);
                	if(data!=null){
                		isShowItem=data.isHasItem();
                    	isShowMemorial=data.isHasMemorial();
                    	isRead=data.isRead();
                    	mListCalendarDatas = data.getItems();
                    	CxLog.i("CalendarView_men", "isShowItem:"+isShowItem+",isShowMemorial:"+isShowMemorial+",isRead:"+isRead); 	
              
                	}	
                }
  
                MonthCellDescriptor monthCellDes = new MonthCellDescriptor(date, str,
                        isCurrentMonth, isSelectable, isSelected, isToday, isShowItem,
                        isShowMemorial, isRead, value);
                monthCellDes.setListCalendarDatas(mListCalendarDatas);
                weekCells.add(monthCellDes);
            
                cal.add(DATE, 1);
            }
        }
        return cells;
    }


    public void updateCellViews(MonthDescriptor month, List<List<MonthCellDescriptor>> cells,
            boolean isUpdateData) {
        //Logr.d("CalendarView Initializing MonthView (%d) for %s", System.identityHashCode(this),
        //        month);
        long start = System.currentTimeMillis();
        title.setText(month.getLabel());
        
        Calendar instance = Calendar.getInstance(locale);
        CxLog.i("Calendar_men", sameDate(monthCounter, instance)+"");
        if(sameDate(monthCounter, instance)){
        	curMonth.setVisibility(View.GONE);
        }else{
        	curMonth.setVisibility(View.VISIBLE);
        }

        final int numRows = cells.size();
        grid.setNumRows(numRows);
        for (int i = 0; i < 6; i++) {
            CalendarRowView weekRow = (CalendarRowView)grid.getChildAt(i + 1);
            weekRow.setCellClickListener(cellClickListener);
            if (i < numRows) {
                weekRow.setVisibility(VISIBLE);
                List<MonthCellDescriptor> week = cells.get(i);
                for (int c = 0; c < week.size(); c++) {
                    MonthCellDescriptor cell = week.get(c);
                    CalendarCellView cellView = (CalendarCellView)weekRow.getChildAt(c);
                    cellView.setCurrentMonth(cell.isCurrentMonth());
   
                    cellView.setSelectable(cell.isSelected());                    
                    cellView.setToday(cell.isToday());
   
                    cellView.setTag(cell);
                    cellView.setDay(cell.getValue());
                    cellView.setText(Integer.toString(cell.getValue()), cell.getLunarDateStr());
                    // }
                    CxLog.i("CalendarView","cell.value=" + cell.getValue() + "ishowItem=" + cell.isShowItem()
                                    + "ishowmemorial=" + cell.isShowMemorial() + "ishowread="+ cell.isHasRead());
                    if (!is_landspace) {
                        cellView.setShowDot(cell.isShowItem(), cell.isShowMemorial());
                    } else {
                    	cellView.updateData(cell.getListCalendarDatas());                   	
                    }
                    if(cell.isSelected() && !cell.isHasRead()){
                    	cellView.setShowTriangle(true);   
                    	cell.setHasRead(true);
                    }else{
                    	cellView.setShowTriangle(cell.isHasRead());
                    }
                    
                }
            } else {
                weekRow.setVisibility(GONE);
            }
        }
        
        //Logr.d("MonthView.init took %d ms", System.currentTimeMillis() - start);
    }

    private class CellClickedListener implements CalendarView.CellClickListener {
        @Override
        public void handleClick(View v, MonthCellDescriptor cell) {

            CxLog.i("CellClickedListener", "come in");
            Date clickedDate = cell.getDate();
            Calendar instance = Calendar.getInstance();
            instance.setTime(clickedDate);
            selectDate(instance,true);
            if(is_landspace){
                List<CalendarDataObj> cdolist = cell.getListCalendarDatas();
                CalendarCellView.mCalendarCellViewHandler.obtainMessage(CalendarCellView.UPDATE_LANDSPACE_ITEMS, cdolist).sendToTarget();
            }
        }
    }

    public boolean selectDate2(Calendar date,boolean isFirst) {
    	monthCounter=date;
    	initAndUpdateCalendar(isFirst);
    	return true;
    }
    
    
    public boolean selectDate(Calendar date,boolean isFirst) {

        if (date == null) {
            throw new IllegalArgumentException("Selected date must be non-null.  " + date);
        }
        if (date.getTime() == null || date.getTimeInMillis()==0) {
            throw new IllegalArgumentException("Selected date must be non-zero.  " + date);
        }
        
        CxLog.i("CalendarView_men", ">>>>>>>>>>>>1"+date.get(YEAR)+":"+date.get(MONTH)+":"+date.get(DAY_OF_MONTH));

        if(sameDate(monthCounter, date)){
        	if(!isFirst){
        		initAndUpdateCalendar(isFirst);
        		CxLog.i("CalendarView_men", ">>>>>>>>>>>>2"+isFirst);
        		
        		if(!is_landspace&& dateListener!=null){
        			HashMap<Integer,CalendarDayData> monthMap = CxCalendarParam.getInstance().getCurrentMonthMap();
        			boolean isRead=true;
        			CalendarDayData data = monthMap.get(monthCounter.get(DAY_OF_MONTH));
        			if(data!=null){
        				isRead=data.isRead();
        				data.setRead(true);
        			}
        			CxLog.i("CalendarView_men", ">>>>>>>>>>>>3"+isRead);
        			dateListener.onDateSelected(date, isRead);
        		}
        	}
        	return true;
        }
  
        if (!(monthCounter.get(YEAR)==date.get(YEAR) && monthCounter.get(MONTH)==date.get(MONTH))) {
        	CxLog.i("CalendarView_men", ">>>>>>>>>>>>4"+isFirst);     
            monthCounter=date;
            initAndUpdateCalendar(isFirst);
            if(dateListener!=null){	
    			dateListener.onDateSelected(monthCounter, true);
    		}
            return false;
        }
        CxLog.i("CalendarView_men", ">>>>>>>>>>>>5"+isFirst); 
        MonthCellDescriptor selectedCell = getMonthCellDesByDate(date);
        MonthCellDescriptor oldCell = getMonthCellDesByDate(monthCounter);
        selectedCell.setSelected(true);
        boolean b=selectedCell.isHasRead();
        selectedCell.setHasRead(true);
        oldCell.setSelected(false);
        monthCounter=date;
        currentMonthDes = new MonthDescriptor(monthCounter.get(MONTH),
                monthCounter.get(YEAR), monthCounter, monthNameFormat.format(monthCounter.getTime()));
        updateDataSetChanged();
        if ( dateListener != null) { 
        	if(!b){
        		HashMap<Integer,CalendarDayData> monthMap = CxCalendarParam.getInstance().getCurrentMonthMap();
        		monthMap.get(monthCounter.get(DAY_OF_MONTH)).setRead(true);
        	}
            dateListener.onDateSelected(date,b);
        }
        return true;
    }
    
    public void updateDataSetChanged() {
        // RkLog.i("CalendarView", "updateDataSetChanged");
        updateCellViews(currentMonthDes, currentMonthCells, false);
    }

    /** Return cell for a given Date. */
    private MonthCellDescriptor getMonthCellDesByDate(Calendar searchCal) {
       
        int number=searchCal.get(DAY_OF_MONTH)+positionOfOne-1;
        int row=(int)(number/7);
        int line=number-(row)*7;

        List<MonthCellDescriptor> list = currentMonthCells.get(row);
        MonthCellDescriptor cellDescriptor = list.get(line);
        
        return cellDescriptor;
    }

    //已不用  暂保留
//    private boolean doSingleSelectDate(final MonthCellDescriptor cell) {
//        boolean wasSelected = false;
//        Calendar newlySelCal = Calendar.getInstance(locale);
//        newlySelCal.setTime(cell.getDate());
//
//        if (CalendarUtil.getInstance().isSame(newlySelCal, monthCounter)) {
//            clearOldSelections();
//            cell.setSelected(false);
//            wasSelected = false;
//        } else {
//            clearOldSelections();
//
//            selectedCals.add(newlySelCal);
//            selectedCellDess.add(cell);
//            cell.setSelected(true);
//            wasSelected = true;
//        }
//        cell.setHasRead(true);
//        updateDataSetChanged();
//        return wasSelected;
//    }

 
    private static boolean sameDate(Calendar cal, Calendar selectedDate) {
        return cal.get(MONTH) == selectedDate.get(MONTH) && cal.get(YEAR) == selectedDate.get(YEAR)
                && cal.get(DAY_OF_MONTH) == selectedDate.get(DAY_OF_MONTH);
    }

    private boolean containCell(List<MonthCellDescriptor> selectedCells,
            final MonthCellDescriptor cell) {
        for (MonthCellDescriptor selectedCell : selectedCells) {
            if (selectedCell.equals(cell)) {
                return true;
            }
        }
        return false;
    }

    private void clearOldSelections() {
        for (MonthCellDescriptor selectedCell : selectedCellDess) {
            // De-select the currently-selected cell.
            selectedCell.setSelected(false);
        }
        selectedCellDess.clear();
        selectedCals.clear();
    }

    public interface CellClickListener {
        void handleClick(View v, MonthCellDescriptor cell);
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        dateListener = listener;
    }

 
    /**
     * Set a listener to react to user selection of a disabled date.
     * 
     * @param listener the listener to set, or null for no reaction
     */
    public void setOnInvalidDateSelectedListener(OnInvalidDateSelectedListener listener) {
        invalidDateListener = listener;
    }

    public void setOnDetailClickListener(OnDetailClickListener listener) {
        this.detailClickListener = listener;
    }

    /**
     * Interface to be notified when a new date is selected or unselected. This
     * will only be called when the user initiates the date selection. If you
     * call {@link #selectDate(Date)} this listener will not be notified.
     * 
     * @see #setOnDateSelectedListener(OnDateSelectedListener)
     */
    public interface OnDateSelectedListener {
        void onDateSelected(Calendar date,boolean isRead);

        void onDateUnselected(Calendar date);
    }

    public interface OnDetailClickListener {
        void onDetailClick();
    }

    /**
     * Interface to be notified when an invalid date is selected by the user.
     * This will only be called when the user initiates the date selection. If
     * you call {@link #selectDate(Date)} this listener will not be notified.
     * 
     * @see #setOnInvalidDateSelectedListener(OnInvalidDateSelectedListener)
     */
    public interface OnInvalidDateSelectedListener {
        void onInvalidDateSelected(Date date);

        void onPreMonthDateSelected(Date date);

        void onNextMonthDateSelected(Date date);
    }

    public interface OnMonthChangedListener {
        void onChangedToPreMonth(Date dateOfMonth);

        void onChangedToNextMonth(Date dateOfMonth);

        void onChangedToCurMonth(Date dateNow);
    }

    private class DefaultOnInvalidDateSelectedListener implements OnInvalidDateSelectedListener {
        @Override
        public void onInvalidDateSelected(Date date) {

        }

        @Override
        public void onPreMonthDateSelected(Date date) {
            previousMonth();
        }

        @Override
        public void onNextMonthDateSelected(Date date) {
            nextMonth();
        }
    }

    protected void showBirthdayDialog() {

        View view = View.inflate(context, R.layout.cx_fa_widget_accounting_date_dialog, null);
        final DatePicker dateDp = (DatePicker)view
                .findViewById(R.id.cx_fa_accounting_change_account_date_dialog_dp);
        TextView titleText = (TextView)view
                .findViewById(R.id.cx_fa_mate_family_info_user_birthday_tv);
        Button cancelBtn = (Button)view
                .findViewById(R.id.cx_fa_accounting_change_account_date_dialog_cancel);
        Button okBtn = (Button)view
                .findViewById(R.id.cx_fa_accounting_change_account_date_dialog_ok);
        titleText.setText("选择日期");
        final Calendar c1 = Calendar.getInstance();

        String[] split = title.getText().toString().split("\\.");
        int year = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int day = 1;

        if (monthCounter != null) {
            year = monthCounter.get(Calendar.YEAR);
            month = monthCounter.get(Calendar.MONTH);
            day = monthCounter.get(Calendar.DAY_OF_MONTH);
        }
        
        EditText monthE = (EditText)((ViewGroup)((ViewGroup) ((ViewGroup) dateDp.getChildAt(0)).getChildAt(0)).getChildAt(1)).getChildAt(1);	
		EditText yearE = (EditText)((ViewGroup)((ViewGroup) ((ViewGroup) dateDp.getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(1);	
		EditText dayE = (EditText)((ViewGroup)((ViewGroup) ((ViewGroup) dateDp.getChildAt(0)).getChildAt(0)).getChildAt(2)).getChildAt(1);	

		if(yearE != null) {
			yearE.setTextSize(16);
		}
		if(monthE != null) {
			monthE.setTextSize(16);
		}
		if(dayE != null) {
			dayE.setTextSize(16);
		}

        dateDp.init(year, month, day, new OnDateChangedListener() {

            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                c1.set(Calendar.YEAR, year);
                c1.set(Calendar.MONTH, monthOfYear);
                c1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        });


        final Dialog dialog = new Dialog(context, R.style.simple_dialog);
        dialog.setContentView(view);
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
                selectDate(c1,true);

            }
        });
        dialog.show();

    }
    
    private boolean isPreMonthDate(Date date) {
        boolean isPre = false;
        Calendar dateCal = Calendar.getInstance(locale);
        dateCal.setTime(date);
        if (dateCal.get(YEAR) < monthCounter.get(YEAR)) {
            isPre = true;
        } else if (dateCal.get(YEAR) == monthCounter.get(YEAR)
                && dateCal.get(MONTH) < monthCounter.get(MONTH)) {
            isPre = true;
        }
        return isPre;
    }

    private boolean isCurrentMonthDate(Date date) {
        boolean isCurrent = false;
        Calendar dateCal = Calendar.getInstance(locale);
        dateCal.setTime(date);
        if (dateCal.get(YEAR) == monthCounter.get(YEAR)
                && dateCal.get(MONTH) == monthCounter.get(MONTH)) {
            isCurrent = true;
        }
        return isCurrent;
    }

    private boolean isNextMonthDate(Date date) {
        boolean isNext = false;
        isNext = !isPreMonthDate(date) && !isCurrentMonthDate(date);
        return isNext;
    }
    
    private int mX;
	private int mY;
	private boolean moveFinish=false;
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	
    	boolean result = false;
    	 
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mX = (int) ev.getX();
			mY = (int) ev.getY();
			
			break;
		case MotionEvent.ACTION_MOVE:
			int curX = (int) ev.getX();
			int curY = (int) ev.getY();
			
			int disY = Math.abs(curY-mY);
			
			if(curX-mX>150 && is_landspace && !moveFinish && disY<150){				
				previousMonth();
				moveFinish=true;
			}else if(mX-curX>150 && is_landspace && !moveFinish && disY<150){				
				nextMonth();
				moveFinish=true;
			}
			
			break;
		case MotionEvent.ACTION_UP:
			result = false;
			moveFinish=false;
			break;

		default:
			break;
		}
		
		
		return result;
    	
    }
    
    
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        RkLog.i("RkCalendarDetailActivity_men", ">>>>>>>>>>>>0"); 
//    	return false;
//    }
    

}
