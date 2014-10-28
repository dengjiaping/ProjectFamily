package com.chuxin.family.parse.been.data;

import com.chuxin.family.global.CxGlobalParams;

/**
 * 这是LONG-POLLING形式检查消息状态的data字段的内容
 * @author shichao.wang
 *
 */
public class CxPollingDataField {

	// 默认设为-1. 目的: 防止在setPair()失败时，getPair()会默认为0，视为未结对，造成很不好的用户体验. modify by dujianyin  2014.2.21
	private int pair = -1;		
	private int match;
	private int space_ts;
	private int chat_ts;
	private int edit_ts;
	private int rts;
	private int group;
	private int space_tips;		// 二人空间未读的消息数
	private int single_mode;
	private int calendar_ts;
	private int kid_tips;
	
	
	public int getKid_tips() {
		return kid_tips;
	}

	public void setKid_tips(int kid_tips) {
		this.kid_tips = kid_tips;
	}

	public int getCalendar_ts() {
        return calendar_ts;
    }

    public void setCalendar_ts(int calendar_ts) {
        this.calendar_ts = calendar_ts;
    }

    public int getSingle_mode() {
		return single_mode;
	}

	public void setSingle_mode(int single_mode) {
		this.single_mode = single_mode;
	}

	public int getSpace_tips() {
		return space_tips;
	}

	public void setSpace_tips(int space_tips) {
		this.space_tips = space_tips;
	}
	
	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getPair() {
		// 如果setPair()未成功，则弄成系统中现有的状态    说明：setPair()时，只能是0或1, 所以此处用-1判断
		if(pair==-1){
			pair =  CxGlobalParams.getInstance().getPair();
		}
		return pair;
	}
	
	public void setPair(int pair) {
		this.pair = pair;
	}
	
	public int getMatch() {
		return match;
	}
	
	public void setMatch(int match) {
		this.match = match;
	}
	
	public int getSpace_ts() {
		return space_ts;
	}
	
	public void setSpace_ts(int space_ts) {
		this.space_ts = space_ts;
	}
	
	public int getChat_ts() {
		return chat_ts;
	}
	
	public void setChat_ts(int chat_ts) {
		this.chat_ts = chat_ts;
	}
	
	public int getEdit_ts() {
		return edit_ts;
	}
	
	public void setEdit_ts(int edit_ts) {
		this.edit_ts = edit_ts;
	}
	
	public int getRts() {
		return rts;
	}
	
	public void setRts(int rts) {
		this.rts = rts;
	}
	
}
