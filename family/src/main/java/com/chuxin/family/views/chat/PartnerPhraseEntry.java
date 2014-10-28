package com.chuxin.family.views.chat;

import android.content.Context;

import com.chuxin.family.models.Message;
import com.chuxin.family.R;

public class PartnerPhraseEntry extends PhraseEntry {
	private static final int VIEW_RES_ID = R.layout.cx_fa_view_chat_chatting_phrase_row_for_partner;
	private static final int ICON_ID = R.id.cx_fa_view_chat_chatting_phrase_row_for_partner__icon;
	private static final int PHRASE_TEXT_ID = R.id.cx_fa_view_chat_chatting_phrase_row_for_partner__content_text;
	private static final int PHRASE_IMAGE_ID = R.id.cx_fa_view_chat_chatting_phrase_row_for_partner__content_image;
	private static final int TIMESTAMP_ID = R.id.cx_fa_view_chat_chatting_phrase_row_for_partner__timestamp;
	private static final int DATESTAMP_ID = R.id.cx_fa_view_chat_chatting_phrase_row_for_partner__datestamp;
	private static final boolean OWNER_FLAG = false;

	public PartnerPhraseEntry(Message message, Context context, boolean isShowDate) {
		super(message, context, isShowDate);
	}

	@Override
	public int getType() {
		return ENTRY_TYPE_PEER_PHRASE;
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
	
	public int getPhraseTextId() {
		return PHRASE_TEXT_ID;
	}
	
	public int getPhraseImageId() {
		return PHRASE_IMAGE_ID;
	}
	
	public int getTimeStampId() {
		return TIMESTAMP_ID;
	}
	
	public int getDateStampId(){
		return DATESTAMP_ID;
	}
}
