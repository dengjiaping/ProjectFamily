package com.chuxin.family.parse.been.data;

import com.chuxin.family.models.CalendarDataObj;

public class CalendarData {

    private String mId; // 当前记录的id

    private String mPairId;

    private int mSetTs;// 设置的时间

    private int mType;// 分类0:事项 1:纪念日

    private String mContent;// 日历内容

    private int mAdance;// 提前量

    private int mTarget;// 显示对象0:自己 1:对方 2:双方

    private String mAuthor;// 创建者

    private int mCycle;// 循环周期类型，0：不循环，1：按天，2：按周，3：按月，4：按年

    private boolean mIsRemind;// 是否提醒，0:不提醒 1:提醒

    private int mStatus;// 日历状态，0：有效的，1：无效的，2：过期的

    private boolean mIsRead;// 是否已读 0：未读，1：已读

    private int mDayType;// 纪念日类别，0：无 1：生日 2：其它
    
    public CalendarDataObj getCalendarDataObj() {
        return calendarDataObj;
    }

    public void setCalendarDataObj(CalendarDataObj calendarDataObj) {
        this.calendarDataObj = calendarDataObj;
    }

    private CalendarDataObj calendarDataObj;

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmPairId() {
        return mPairId;
    }

    public void setmPairId(String mPairId) {
        this.mPairId = mPairId;
    }

    public int getmSetTs() {
        return mSetTs;
    }

    public void setmSetTs(int mSetTs) {
        this.mSetTs = mSetTs;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public int getmAdance() {
        return mAdance;
    }

    public void setmAdance(int mAdance) {
        this.mAdance = mAdance;
    }

    public int getmTarget() {
        return mTarget;
    }

    public void setmTarget(int mTarget) {
        this.mTarget = mTarget;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public int getmCycle() {
        return mCycle;
    }

    public void setmCycle(int mCycle) {
        this.mCycle = mCycle;
    }

    public boolean getmIsRemind() {
        return mIsRemind;
    }

    public void setmIsRemind(boolean mIsRemind) {
        this.mIsRemind = mIsRemind;
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public boolean getmIsRead() {
        return mIsRead;
    }

    public void setmIsRead(boolean mIsRead) {
        this.mIsRead = mIsRead;
    }

    public int getmDayType() {
        return mDayType;
    }

    public void setmDayType(int mDayType) {
        this.mDayType = mDayType;
    }



}
