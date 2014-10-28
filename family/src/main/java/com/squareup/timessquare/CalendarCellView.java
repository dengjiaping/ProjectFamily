// Copyright 2013 Square, Inc.

package com.squareup.timessquare;

import com.chuxin.family.R;
import com.chuxin.family.calendar.CalendarDisplayUtility;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.models.CalendarDataObj;
import com.chuxin.family.parse.been.data.CalendarData;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.Globals;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CalendarCellView extends LinearLayout {

    private Context mContext;

    private TextView mDateTextView,mDateTextView2, mLunarDateTextView,mLunarDateTextView2;

    private ImageView mDotItemImageView, mDotMemorialImageView, mTriangelImageView;

    private FrameLayout mCellViewFrameLayout;

    private boolean mIsCurrentMonth;

    private boolean mIsLandspace = false;

    private int day = 0;

    public LinearLayout mItemsLayout;

    private View mView;

    private List<CalendarDataObj> mCurrentData;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private CalendarDisplayUtility mDisplayUtility = null;

    private List<CalendarData> mCellViewCalendarListData = new ArrayList<CalendarData>();

    public static final int UPDATE_LANDSPACE_ITEMS = 0;

//	private LinearLayout dotLayout1;
//
//	private LinearLayout dotLayout2;

	private ImageView mDotImg;
	
	private boolean isToday;

    public static Handler mCalendarCellViewHandler;

	private ImageView landDot1;

	private ImageView landDot2;

	private ImageView landDot3;

	private TextView landText1;

	private TextView landText2;

	private TextView landText3;

    public CalendarCellView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public CalendarCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mIsLandspace = Globals.getInstance().getCalendarIsLandSpace();
        // RkLog.i("CalendarCellView", "mIsLandspace=" + mIsLandspace);
        mCalendarCellViewHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_LANDSPACE_ITEMS:
                        //View itemview = add((CalendarData)msg.obj);
                        //mItemsLayout.addView(itemview);
                        List<CalendarDataObj> obj = (List<CalendarDataObj>)msg.obj;
                        showAllCalendarDialog(obj);
                        break;
                }
            }
        };
        init();
        // initData();
    }

    private void init() {
        mDisplayUtility = new CalendarDisplayUtility(getResources());
        if (!mIsLandspace) {
            mView = View.inflate(mContext, R.layout.cx_fa_widget_calendar_cell_vertical, null);
            mCellViewFrameLayout = (FrameLayout)mView.findViewById(R.id.cx_fa_widget_calendar_cell_fl);
            mDateTextView = (TextView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_date_tv_1);
            mDateTextView2 = (TextView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_date_tv_2);
            mLunarDateTextView = (TextView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_lunar_date_tv_1);
            mLunarDateTextView2 = (TextView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_lunar_date_tv_2);
            mDotItemImageView = (ImageView)mView
                    .findViewById(R.id.cx_fa_widget_calendar_cell_dot_item_iv);
            mDotMemorialImageView = (ImageView)mView
                    .findViewById(R.id.cx_fa_widget_calendar_cell_dot_memorial_iv);
            mTriangelImageView = (ImageView)mView
                    .findViewById(R.id.cx_fa_widget_calendar_cell_triangle_iv);
            
//            dotLayout1 = (LinearLayout)mView.findViewById(R.id.cx_fa_widget_calendar_cell_dot_layout1);
//            dotLayout2 = (LinearLayout)mView.findViewById(R.id.cx_fa_widget_calendar_cell_dot_layout2);
            mDotImg = (ImageView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_dot_item_or_memorial_iv);
        } else {
            mView = View.inflate(mContext, R.layout.cx_fa_widget_calendar_cell_horizontal, null);
            mCellViewFrameLayout = (FrameLayout)mView
                    .findViewById(R.id.cx_fa_widget_calendar_cell_landspace_fl);
            mDateTextView = (TextView)mView
                    .findViewById(R.id.cx_fa_widget_calendar_cell_landspace_date_tv);
            mDateTextView2 = (TextView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_landspace_date_tv2);
            mLunarDateTextView = (TextView)mView
                    .findViewById(R.id.cx_fa_widget_calendar_cell_lunar_landspace_date_tv);
            mLunarDateTextView2 = (TextView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_lunar_landspace_date_tv2);
            mTriangelImageView = (ImageView)mView
                    .findViewById(R.id.cx_fa_widget_calendar_cell_triangle_landsapce_iv);
            mItemsLayout = (LinearLayout)mView.findViewById(R.id.cx_fa_calendar_landspace_items_layout);
            
            landDot1 = (ImageView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_dot_landspace_iv1);
            landDot2 = (ImageView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_dot_landspace_iv2);
            landDot3 = (ImageView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_dot_landspace_iv3);
            landText1 = (TextView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_text_landspace_tv1);
            landText2 = (TextView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_text_landspace_tv2);
            landText3 = (TextView)mView.findViewById(R.id.cx_fa_widget_calendar_cell_text_landspace_tv3);
        }
        this.setBackgroundResource(R.color.calendar_bg);
        this.addView(mView);
    }

    private void showAllCalendarDialog( List<CalendarDataObj> cdolist) {
        mCurrentData = cdolist;
        View view = View.inflate(mContext, R.layout.cx_fa_widget_calendar_detail_dialog, null);
        LinearLayout calendarList = (LinearLayout)view
                .findViewById(R.id.cx_fa_widget_calendar_detail_dialog_layout);
        if (null != calendarList) {
            calendarList.removeAllViews();
        }
        if ((null != mCurrentData) && (mCurrentData.size() > 0)) {
            calendarList.setVisibility(View.VISIBLE);
            for (int i = 0; i < mCurrentData.size(); i++) {
                View itemview = addLandspaceItemView(mCurrentData.get(i));
                calendarList.addView(itemview);
            }
            Dialog dialog = new Dialog(mContext, R.style.simple_dialog);
            dialog.setContentView(view);
            dialog.show();
        }
        
    }

    private View addLandspaceItemView(CalendarDataObj calendar) {
//        final CalendarDataObj calendar = calendardata.getCalendarDataObj();
        View view = View.inflate(mContext, R.layout.cx_fa_fragment_calendar_item, null);
        LinearLayout itemWhole = (LinearLayout)view.findViewById(R.id.cx_fa_calendar_item_whole_lv);
        CxImageView imageIcon = (CxImageView)view.findViewById(R.id.cx_fa_calendar_item_icon_iv);
        TextView itemContent = (TextView)view.findViewById(R.id.cx_fa_calendar_item_content_tv);
        ImageView memorialImageView = (ImageView)view.findViewById(R.id.cx_fa_calendar_item_memorial_iv);
        ImageView clockImageView = (ImageView)view.findViewById(R.id.cx_fa_calendar_item_clock_iv);
        ImageView privateImageView = (ImageView)view.findViewById(R.id.cx_fa_calendar_item_private_iv);
        LinearLayout itemTimeAndTarget = (LinearLayout)view.findViewById(R.id.cx_fa_calendar_item_time_and_target_layout);
        TextView itemTime = (TextView)view.findViewById(R.id.cx_fa_calendar_item_time_tv);
        TextView itemTarget = (TextView)view.findViewById(R.id.cx_fa_calendar_item_target_tv);
        LinearLayout triangleImg = (LinearLayout)view.findViewById(R.id.cx_fa_calendar_item_triangle_img_layout);
        triangleImg.setVisibility(View.GONE);
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
                itemTime.setText(mContext.getString(R.string.cx_fa_calendar_status_remind_expire));
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
            imageIcon.displayImage(imageLoader, CxGlobalParams.getInstance().getIconSmall(),
                    CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, CxGlobalParams.getInstance()
                            .getSmallImgConner());
            clockImageView.setBackgroundResource(CxResourceDarwable.getInstance().dr_calendar_clock_img_me);
        } else { // 对方创建
        	imageIcon.displayImage(imageLoader, CxGlobalParams.getInstance().getPartnerIconBig(),
            		CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, CxGlobalParams.getInstance()
                            .getMateSmallImgConner());
            clockImageView.setBackgroundResource(CxResourceDarwable.getInstance().dr_calendar_clock_img_oppo);

        }
        itemContent.setText(calendar.getContent());
        return view;
    }

    public void updateData(List<CalendarDataObj> listcdo) {
//    	mItemsLayout.removeAllViews();
//        mCurrentData = listcdo;  
        CxLog.i("CalendarCellView_men", listcdo.size()+"");
        if(isToday){
        	landText1.setTextColor(Color.WHITE);
        	landText2.setTextColor(Color.WHITE);
        	landText3.setTextColor(Color.WHITE);
        }
        int size = listcdo.size();
        switch (size) {
		case 0:
			landDot1.setVisibility(View.INVISIBLE);
			landText1.setVisibility(View.INVISIBLE);
			landDot2.setVisibility(View.INVISIBLE);
			landText2.setVisibility(View.INVISIBLE);
			landDot3.setVisibility(View.INVISIBLE);
			landText3.setVisibility(View.INVISIBLE);
			break;
		case 1:
			landDot1.setVisibility(View.VISIBLE);
			landText1.setVisibility(View.VISIBLE);
			landDot2.setVisibility(View.INVISIBLE);
			landText2.setVisibility(View.INVISIBLE);
			landDot3.setVisibility(View.INVISIBLE);
			landText3.setVisibility(View.INVISIBLE);
			break;
		case 2:
			landDot1.setVisibility(View.VISIBLE);
			landText1.setVisibility(View.VISIBLE);
			landDot2.setVisibility(View.VISIBLE);
			landText2.setVisibility(View.VISIBLE);
			landDot3.setVisibility(View.INVISIBLE);
			landText3.setVisibility(View.INVISIBLE);
			break;

		default:
			landDot1.setVisibility(View.VISIBLE);
			landText1.setVisibility(View.VISIBLE);
			landDot2.setVisibility(View.VISIBLE);
			landText2.setVisibility(View.VISIBLE);
			landDot3.setVisibility(View.VISIBLE);
			landText3.setVisibility(View.VISIBLE);
			break;
		}
        for (int i = 0; i < listcdo.size(); i++) {
            CalendarDataObj obj = listcdo.get(i);
            if(i==0){
            	if (!obj.isShowMemorial()) {
            		landDot1.setImageResource(R.drawable.cx_fa_widget_calendar_cell_dot_item);
                } else {
                	landDot1.setImageResource(R.drawable.cx_fa_widget_calendar_cell_dot_memorial);
                }
            	landText1.setText(obj.getContent());            	
            }
            if(i==1){
            	if (!obj.isShowMemorial()) {
            		landDot2.setImageResource(R.drawable.cx_fa_widget_calendar_cell_dot_item);
            	} else {
            		landDot2.setImageResource(R.drawable.cx_fa_widget_calendar_cell_dot_memorial);
            	}
            	landText2.setText(obj.getContent());            	
            }
            if(i==2){
            	if (!obj.isShowMemorial()) {
            		landDot3.setImageResource(R.drawable.cx_fa_widget_calendar_cell_dot_item);
            	} else {
            		landDot3.setImageResource(R.drawable.cx_fa_widget_calendar_cell_dot_memorial);
            	}
            	landText3.setText(obj.getContent());            	
            }
        }
        
    }
//    public View addItemView(CalendarDataObj calendar) {
//        //RkLog.i("addItemView", "calendar=" + calendar.mData.toString());
//    	RkLog.i("CalendarCellView_men", calendar.getContent()+"");
//        View view = View.inflate(mContext, R.layout.cx_fa_widget_calendar_cell_horizontal_item,null);
//        ImageView imageIcon = (ImageView)view
//                .findViewById(R.id.cx_fa_widget_calendar_cell_dot_landspace_iv);
//        TextView itemContent = (TextView)view
//                .findViewById(R.id.cx_fa_widget_calendar_cell_content_landspace_tv);
//        itemContent.setText(calendar.getContent());
//        if (!calendar.isShowMemorial()) {
//            imageIcon.setBackgroundResource(R.drawable.cx_fa_widget_calendar_cell_dot_item);
//        } else {
//            imageIcon.setBackgroundResource(R.drawable.cx_fa_widget_calendar_cell_dot_memorial);
//        }
//        return view;
//    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setText(String t1, String t2) {
        
        CxLog.i("setText", "mIsCurrentMonth=" + mIsCurrentMonth +"t1:t2="+t1+":"+t2);
    	
   
    	if(t1.length()==1){
    		mDateTextView.setVisibility(View.VISIBLE);
    		mDateTextView2.setVisibility(View.INVISIBLE);
    		if(mIsCurrentMonth){
    			if(isToday){
    				mDateTextView.setTextColor(Color.WHITE);
    			}else{
    				mDateTextView.setTextColor(getResources().getColor(R.color.calendar_active_month_bg));
    			}
    			 
    		}else{
    			 mDateTextView.setTextColor(getResources().getColor(R.color.calendar_inactive_month_bg));
    		}
    		mDateTextView.setText(t1);	
    	}else{
    		mDateTextView.setVisibility(View.INVISIBLE);
    		mDateTextView2.setVisibility(View.VISIBLE);
    		if(mIsCurrentMonth){
    			if(isToday){
    				mDateTextView2.setTextColor(Color.WHITE);
    			}else{
    				mDateTextView2.setTextColor(getResources().getColor(R.color.calendar_active_month_bg));
    			}
    		}else{
    			mDateTextView2.setTextColor(getResources().getColor(R.color.calendar_inactive_month_bg));
    		}
    		mDateTextView2.setText(t1);	
    	}
    	
    	if(t2.length()==2){
    		mLunarDateTextView.setVisibility(View.VISIBLE);
    		mLunarDateTextView2.setVisibility(View.INVISIBLE);
    		if(mIsCurrentMonth){
    			if(isToday){
    				mLunarDateTextView.setTextColor(Color.WHITE);
    			}else{
    				mLunarDateTextView.setTextColor(getResources().getColor(R.color.calendar_active_month_lunar_bg));
    			}
    		}else{
    			mLunarDateTextView.setTextColor(getResources().getColor(R.color.calendar_inactive_month_bg));
    		}
    		mLunarDateTextView.setText(t2);	
    	}else{
    		mLunarDateTextView.setVisibility(View.INVISIBLE);
    		mLunarDateTextView2.setVisibility(View.VISIBLE);
    		if(mIsCurrentMonth){
    			if(isToday){
    				mLunarDateTextView2.setTextColor(Color.WHITE);
    			}else{
    				mLunarDateTextView2.setTextColor(getResources().getColor(R.color.calendar_active_month_lunar_bg));
    			}
    		}else{
    			mLunarDateTextView2.setTextColor(getResources().getColor(R.color.calendar_inactive_month_bg));
    		}
    		mLunarDateTextView2.setText(t2);	
    	}
    	
    	
//        if (mIsCurrentMonth) {
//            mDateTextView.setTextColor(getResources().getColor(R.color.calendar_active_month_bg));
//            mLunarDateTextView.setTextColor(getResources().getColor(R.color.calendar_active_month_bg));
//
//        } else {
//            mDateTextView.setTextColor(getResources().getColor(R.color.calendar_inactive_month_bg));
//            mLunarDateTextView.setTextColor(getResources().getColor(
//                    R.color.calendar_inactive_month_bg));
//        }
//
//        mDateTextView.setText(t1);
//        mLunarDateTextView.setText(t2);
    }
    public void setShowDot(boolean item,boolean memorial) {
    	if(item && memorial ){
    		mDotItemImageView.setVisibility(View.VISIBLE);
    		mDotMemorialImageView.setVisibility(View.VISIBLE);
    		mDotImg.setVisibility(View.INVISIBLE);
    		return ;
    	}
    	if(item && !memorial ){
    		mDotItemImageView.setVisibility(View.INVISIBLE);
    		mDotMemorialImageView.setVisibility(View.INVISIBLE);
    		mDotImg.setVisibility(View.VISIBLE);
    		mDotImg.setImageResource(R.drawable.cx_fa_widget_calendar_cell_dot_item);
    		return ;
    	}
    	if(!item && memorial ){
    		mDotItemImageView.setVisibility(View.INVISIBLE);
    		mDotMemorialImageView.setVisibility(View.INVISIBLE);
    		mDotImg.setVisibility(View.VISIBLE);
    		mDotImg.setImageResource(R.drawable.cx_fa_widget_calendar_cell_dot_memorial);
    		return ;
    	}
    	if(!item && !memorial ){
    		mDotItemImageView.setVisibility(View.INVISIBLE);
    		mDotMemorialImageView.setVisibility(View.INVISIBLE);
    		mDotImg.setVisibility(View.INVISIBLE);
    		return ;
    	}
    }

    public void setShowTriangle(boolean isshow) {
    	 if (isshow) {
    		 mTriangelImageView.setVisibility(View.INVISIBLE);
         } else {
        	 mTriangelImageView.setVisibility(View.VISIBLE);
         } 
    }

    public void setToday(boolean istoday) {
    	this.isToday=istoday;
        if (istoday) {
            mCellViewFrameLayout.setBackgroundResource(R.drawable.cx_fa_widget_calendar_cell_bg_today);
        }
    }

    public void setSelectable(boolean isselected) {
        //RkLog.i("setSelectable", "mDay=" + day + "isselected=" + isselected);
        //mCellViewFrameLayout.setBackgroundColor(getResources().getColor(R.color.calendar_bg));
        if (isselected) {
            mCellViewFrameLayout.setBackgroundResource(R.drawable.cx_fa_widget_calendar_cell_bg_focused);
        } else {
            mCellViewFrameLayout.setBackgroundColor(getResources().getColor(R.color.calendar_bg));
        }
    }

    public void setCurrentMonth(boolean iscurrentmonth) {
        this.mIsCurrentMonth = iscurrentmonth;
    }

}
