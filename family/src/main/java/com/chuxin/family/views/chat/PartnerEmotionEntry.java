package com.chuxin.family.views.chat;

import android.content.Context;

import com.chuxin.family.models.Message;
import com.chuxin.family.R;

public class PartnerEmotionEntry extends EmotionEntry {
	private static final int VIEW_RES_ID = R.layout.cx_fa_view_chat_chatting_face_row_for_partner;
	private static final int ICON_ID = R.id.cx_fa_view_chat_chatting_face_row_for_partner__icon;
	private static final int CONTENT_ID = R.id.cx_fa_view_chat_chatting_face_row_for_partner__content;
	private static final int CONTENT2_ID = R.id.cx_fa_view_chat_chatting_face_row_for_partner__content2;
	private static final int TIMESTAMP_ID = R.id.cx_fa_view_chat_chatting_face_row_for_partner__timestamp;
	private static final int DATESTAMP_ID = R.id.cx_fa_view_chat_chatting_face_row_for_partner__datestamp;
	private static final boolean OWNER_FLAG = false;

	public PartnerEmotionEntry(Message message, Context context, boolean isShowDate) {
		super(message, context, isShowDate);
	}
	
	@Override
	public int getType() {
		return ENTRY_TYPE_PEER_EMOTION;
	}
	
	@Override
	public boolean isOwner() {
		return OWNER_FLAG;
	}
	
	public int getViewResourceId() {
		return VIEW_RES_ID;
	}
	
	public int getIconId() {
		return ICON_ID;
	}
	
	public int getContentId() {
		return CONTENT_ID;
	}
	
	public int getContent2Id() {
		return CONTENT2_ID;
	}
	
	public int getTimeStampId() {
		return TIMESTAMP_ID;
	}
	public int getDateStampId(){
		return DATESTAMP_ID;
	}
}
