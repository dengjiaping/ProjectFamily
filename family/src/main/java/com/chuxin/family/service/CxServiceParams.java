
package com.chuxin.family.service;

import com.chuxin.family.model.CxSubjectInterface;

public class CxServiceParams extends CxSubjectInterface {

    private static CxServiceParams serviceParam;

    /* 以下是long polling 关联的参数项 */
    public final static String CHAT_TS = "chat_ts";

    private long chatTs; // 已接受的最大一条消息的消息ID（首次调用可输入0）

    public final static String EDIT_TS = "edit_ts";

    private int editTs; // 已知对方的编辑状态ID（首次调用可输入0）

    public final static String R_TS = "r_ts";

    private int rts; // 提醒项的状态(删除、添加，修改）

    private int match; // 未结对情况下，是否有匹配的可结对项;结对情况下无此项

    /*
     * 对于match参数在未结对情况下：同一个long polling,本地需要动态记录一下match的变化， 当再次打开long
     * polling，match又从0开始
     */

    public final static String SPACE_TS = "space_ts";

    private long spaceTs; // 二人空间更新的状态

    // public final static String NEIGHBOUR_TS="neighbour_ts";
    // private long neighbourTs;

    public final static String GROUP = "group";

    private int group;

    public final static String SPACE_TIPS = "space_tips"; // 二人空间未读的消息数量

    private int space_tips;

    public final static String CALENDAR_TS = "calendar_ts";

    private int calendarTs;


    private CxServiceParams() {
    }

    public static CxServiceParams getInstance() {
        if (null == serviceParam) {
            serviceParam = new CxServiceParams();
        }

        return serviceParam;
    }

    public long getChatTs() {
        return chatTs;
    }

    public void setChatTs(long chat) {
        if (chat == this.chatTs) {
            return;
        }
        this.chatTs = chat;
        notifyObserver(CHAT_TS);
    }

    public int getEditTs() {
        return editTs;
    }

    public void setEditTs(int edit) {
        if (edit == this.editTs) {
            return;
        }
        this.editTs = edit;
        notifyObserver(EDIT_TS);
    }

    public int getRts() {
        return rts;
    }

    public void setRts(int rts) {
        if (rts == this.rts) {
            return;
        }
        this.rts = rts;
        notifyObserver(R_TS);
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match; // 同一个long polling, match需要动态改变
    }

    public long getSpaceTs() {
        return spaceTs;
    }

    public void setSpaceTs(long space) {
        if (space == this.spaceTs) {
            return;
        }
        this.spaceTs = space;
        notifyObserver(SPACE_TS);
    }

    public int getCalendarTs() {
        return calendarTs;
    }

    public void setCalendarTs(int calendar_ts) {
        if (calendar_ts == this.calendarTs) {
            return;
        }
        this.calendarTs = calendar_ts;
        notifyObserver(CALENDAR_TS);
    }

    // public long getNeighbourTs() {
    // return neighbourTs;
    // }
    //
    // public void setNeighbourTs(long neighbourTs) {
    // if(neighbourTs==this.neighbourTs)
    // return;
    //
    // this.neighbourTs = neighbourTs;
    // notifyObserver(NEIGHBOUR_TS);
    // }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
        notifyObserver(GROUP);
    }

    public int getSpace_tips() {
        return space_tips;
    }

    public void setSpace_tips(int space_tips) {
        this.space_tips = space_tips;
        notifyObserver(SPACE_TIPS);
    }

}
