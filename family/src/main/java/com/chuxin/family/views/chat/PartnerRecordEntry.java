
package com.chuxin.family.views.chat;

import android.content.Context;

import com.chuxin.family.models.Message;
import com.chuxin.family.R;

public class PartnerRecordEntry extends RecordEntry {

    private static final int VIEW_RES_ID = R.layout.cx_fa_view_chat_chatting_record_row_for_partner;

    private static final int ICON_ID = R.id.cx_fa_view_chat_chatting_record_row_for_partner__icon;

    private static final int CONTENT_ID = R.id.cx_fa_view_chat_chatting_record_audio_length_for_partner;

    private static final int TIMESTAMP_ID = R.id.cx_fa_view_chat_chatting_record_row_for_partner__timestamp;

    private static final int DATESTAMP_ID = R.id.cx_fa_view_chat_chatting_record_row_for_partner__datestamp;

    private static final int VOICE_IMAGE_VIEW_ID = R.id.cx_fa_view_chat_chatting_record_imageview_for_partner;

    private static final int VOICE_LINEARLAYOUT_ID = R.id.cx_fa_view_chat_chatting_record_row_for_partner__content_linearlayout;

    private static final int VOICE_PROGRESS_ID = R.id.cx_fa_view_chat_chatting_record_row_partner_circleProgressBar;

    private static final int SOUND_EFFECT_TEXT_ID = R.id.cx_fa_view_chat_chatting_record_soundeffect_text_for_partner;

    private static final int SOUND_EFFECT_IMAGEVIEW_ID = R.id.cx_fa_view_chat_chatting_record_soundimage_for_partner;

    private static final boolean OWNER_FLAG = false;

    public PartnerRecordEntry(Message message, Context context, boolean isShowDate) {
        super(message, context, isShowDate);
    }

    @Override
    public int getType() {
        return ENTRY_TYPE_PEER_RECORD;
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

    public int getTimeStampId() {
        return TIMESTAMP_ID;
    }

    public int getDateStampId() {
        return DATESTAMP_ID;
    }

    public int getVoiceImageViewId() {
        return VOICE_IMAGE_VIEW_ID;
    }

    public int getVoiceLinearLayoutId() {
        return VOICE_LINEARLAYOUT_ID;
    }

    public int getSoundEffectText() {
        return SOUND_EFFECT_TEXT_ID;
    }

    public int getSoundEffectImageView() {
        return SOUND_EFFECT_IMAGEVIEW_ID;
    }
}
