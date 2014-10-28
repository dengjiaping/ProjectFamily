
package com.chuxin.family.views.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.models.FastPhraseMessage;
import com.chuxin.family.models.Message;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

public class PhraseEntry extends ChatLogEntry {
    private static final String TAG = "PhraseEntry";

    private static final int VIEW_RES_ID = R.layout.cx_fa_view_chat_chatting_phrase_row;

    private static final int ICON_ID = R.id.cx_fa_view_chat_chatting_phrase_row__icon;

    private static final int PHRASE_TEXT_ID = R.id.cx_fa_view_chat_chatting_phrase_row__content_text;

    private static final int PHRASE_IMAGE_ID = R.id.cx_fa_view_chat_chatting_phrase_row__content_image;

    private static final int TIMESTAMP_ID = R.id.cx_fa_view_chat_chatting_phrase_row__timestamp;

    private static final int DATESTAMP_ID = R.id.cx_fa_view_chat_chatting_phrase_row__datestamp;

    private static final int SEND_SUCCESS_BUTTON_ID = R.id.cx_fa_view_chat_chatting_phrase_row__exclamation;

    private static final boolean OWNER_FLAG = true;

    public PhraseEntry(Message message, Context context, boolean isShowDate) {
        super(message, context, isShowDate);
    }

    @Override
    public int getType() {
        return ENTRY_TYPE_PHRASE;
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

    public int getDateStampId() {
        return DATESTAMP_ID;
    }

    public int getSendSuccessButtonId() {
        return SEND_SUCCESS_BUTTON_ID;
    }

    @Override
    public View build(View view, ViewGroup parent) {
        ChatLogAdapter tag = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(getViewResourceId(), null);
        }

        FastPhraseMessage message = (FastPhraseMessage)mMessage;
        int msgType = message.getType();
        final int msgId = message.getMsgId();
        int msgCreatTimeStamp = message.getCreateTimestamp();
        final String msgPhrase = message.getPhrase();

        if (tag == null) {
            tag = new ChatLogAdapter();
        }
        tag.mChatType = msgType;
        tag.mMsgId = msgId;

        CxImageView icon = (CxImageView)view.findViewById(getIconId());
        TextView phraseText = (TextView)view.findViewById(getPhraseTextId());
        ImageView phraseImage = (ImageView)view.findViewById(getPhraseImageId());
        TextView timeStamp = (TextView)view.findViewById(getTimeStampId());
        TextView dateStamp = (TextView)view.findViewById(getDateStampId());

        if (isOwner()) {
            // RkLog.v(TAG, "Chat" +
            // RkGlobalParams.getInstance().getIconSmall());
            int msgSendState = message.getSendSuccess();
            /*icon.setImageResource(R.drawable.cx_fa_wf_icon_small);
            icon.setImage(RkGlobalParams.getInstance().getIconBig(), false, 44, mContext, "head",
                    mContext);*/
            icon.displayImage(ImageLoader.getInstance(), 
            		CxGlobalParams.getInstance().getIconSmall(), 
            		CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
            		CxGlobalParams.getInstance().getSmallImgConner());
            icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO update owner headimage
                    CxLog.i(TAG, "click update owner headimage button");
                    ChatFragment.getInstance().updateHeadImage();
                }
            });
            final ImageButton reSendBtn = (ImageButton)view.findViewById(getSendSuccessButtonId());
            ProgressBar pb = (ProgressBar)view
                    .findViewById(R.id.cx_fa_view_chat_chatting_phrase_row_circleProgressBar);
            if (msgSendState == 0) {
                pb.setVisibility(View.VISIBLE);
                reSendBtn.setVisibility(View.GONE);
            }
            if (msgSendState == 1) {
                pb.setVisibility(View.GONE);
                reSendBtn.setVisibility(View.GONE);
            }
            if (msgSendState == 2) {
                pb.setVisibility(View.GONE);
                reSendBtn.setVisibility(View.VISIBLE);
                reSendBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // resend message
                        new AlertDialog.Builder(ChatFragment.getInstance().getActivity())
                        .setTitle(R.string.cx_fa_alert_dialog_tip)
                        .setMessage(R.string.cx_fa_chat_resend_msg)
                        .setPositiveButton(R.string.cx_fa_chat_button_resend_text,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        reSendBtn.setVisibility(View.GONE);
                                        ChatFragment.getInstance().reSendMessage(msgPhrase, 1, msgId);
                                    }
                                }).setNegativeButton(R.string.cx_fa_cancel_button_text, null).show();

                    }
                });
            }
        } else {
            // RkLog.v(TAG, "partner:" +
            // RkMateParams.getInstance().getMateIcon());
            /*icon.setImageResource(R.drawable.cx_fa_hb_icon_small);
            icon.setImage(RkGlobalParams.getInstance().getPartnerIconBig(), false, 44, mContext, "head",
                    mContext);*/
        	icon.displayImage(ImageLoader.getInstance(), 
            		CxGlobalParams.getInstance().getPartnerIconBig(), 
            		CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
            		CxGlobalParams.getInstance().getMateSmallImgConner());
            icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO 跳转到个人资料页
                    ChatFragment.getInstance().gotoOtherFragment("rkmate");
                }
            });
        }
        TypedArray phraseImageResIds = view.getResources().obtainTypedArray(R.array.phrase_images);
        String[] phraseDescs = view.getResources().getStringArray(R.array.phrase_desc);
        String[] phraseValues = view.getResources().getStringArray(R.array.phrase_ids);
        for (int i = 0; i < phraseValues.length; i++) {
            if (phraseValues[i].equals(msgPhrase)) {
                phraseText.setText(phraseDescs[i]);
                phraseImage.setImageResource(phraseImageResIds.getResourceId(i, 0));
                break;
            }
        }
        // phraseText.setText("测试短语");
        // phraseImage.setImageResource(R.drawable.doing_duche);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String dateNow = dateFormat.format(new Date((long)(msgCreatTimeStamp) * 1000));

        if (mIsShowDate) {
            dateStamp.setVisibility(View.VISIBLE);
            dateStamp.setText(dateNow);
        } else {
            dateStamp.setVisibility(View.GONE);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date((long)(msgCreatTimeStamp) * 1000));
        String format = view.getResources().getString(
                R.string.cx_fa_nls_reminder_period_time_format);
        String time = String.format(format, calendar);
        timeStamp.setText(time);
        view.setTag(tag);
        return view;
    }

}
