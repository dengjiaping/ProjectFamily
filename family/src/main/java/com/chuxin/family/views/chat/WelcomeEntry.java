
package com.chuxin.family.views.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chuxin.family.models.Message;
import com.chuxin.family.models.WelcomeMessage;
import com.chuxin.family.R;

public class WelcomeEntry extends ChatLogEntry {
    private static final String TAG = "WelcomeEntry";

    private static final int VIEW_RES_ID = R.layout.cx_fa_view_chat_chatting_header;

    private static final int CONTENT_ID = R.id.cx_fa_chat_header_msg;

    private static final boolean OWNER_FLAG = true;

    public WelcomeEntry(Message message, Context context, boolean isShowDate) {
		super(message, context, isShowDate);
	}
    
    @Override
    public int getType() {
		return ENTRY_TYPE_WELCOME;
    }

    @Override
    public boolean isOwner() {
        return OWNER_FLAG;
    }

    public int getViewResourceId() {
        return VIEW_RES_ID;
    }

    public int getContentId() {
        return CONTENT_ID;
    }

    @Override
    public View build(View view, ViewGroup parent) {
        ChatLogAdapter tag = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(getViewResourceId(), null);
        }
        TextView text = (TextView)view.findViewById(getContentId());

        WelcomeMessage message = (WelcomeMessage)mMessage;
        final String msgText = message.getText();
        text.setText(msgText);
        return view;
    }

}
