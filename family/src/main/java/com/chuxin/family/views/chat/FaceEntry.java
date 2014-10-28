
package com.chuxin.family.views.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ant.liao.chuxin.EnhancedGifView;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.models.FaceMessage;
import com.chuxin.family.models.Message;
import com.chuxin.family.models.TextMessage;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

public class FaceEntry extends ChatLogEntry {
    private static final String TAG = "FaceEntry";

    private static final int VIEW_RES_ID = R.layout.cx_fa_view_chat_chatting_face_row;

    private static final int ICON_ID = R.id.cx_fa_view_chat_chatting_face_row__icon;

    private static final int CONTENT_ID = R.id.cx_fa_view_chat_chatting_face_row__content;

    private static final int TIMESTAMP_ID = R.id.cx_fa_view_chat_chatting_face_row__timestamp;

    private static final int DATESTAMP_ID = R.id.cx_fa_view_chat_chatting_face_row__datestamp;

    private static final int SEND_SUCCESS_BUTTON_ID = R.id.cx_fa_view_chat_chatting_face_row__exclamation;

    private static final boolean OWNER_FLAG = true;

    private static String[] sFaceValues = null;

    private static String[] sFaceTexts = null;

    public static String[] getFaceValues(Resources res) {
        if (sFaceValues == null) {
            sFaceValues = res.getStringArray(R.array.face_ids);
        }
        return sFaceValues;
    }

    public static String[] getFaceTexts(Resources res) {
        if (sFaceTexts == null) {
            sFaceTexts = res.getStringArray(R.array.face_texts);
        }
        return sFaceTexts;
    }

    public FaceEntry(Message message, Context context, boolean isShowDate) {
        super(message, context, isShowDate);
    }

    @Override
    public int getType() {
        return ENTRY_TYPE_FACE;
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

    public int getSendSuccessButtonId() {
        return SEND_SUCCESS_BUTTON_ID;
    }

    private View buildFromTextMessage(View view, ViewGroup parent) {
        TextMessage message = (TextMessage)mMessage;

        ChatLogAdapter tag = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(getViewResourceId(), null);
        }

        int msgSendState = message.getSendSuccess();
        int msgType = message.getType();
        final int msgId = message.getMsgId();
        final String msgFace = message.getText();
        int msgCreatTimeStamp = message.getCreateTimestamp();

        if (tag == null) {
            tag = new ChatLogAdapter();
        }

        tag.mChatType = msgType;
        tag.mMsgId = msgId;
        // tag.mMsgText = msgFace;

        CxImageView icon = (CxImageView)view.findViewById(getIconId());
        // ImageView face = (ImageView)view.findViewById(getContentId());
        EnhancedGifView face = (EnhancedGifView)view.findViewById(getContentId());
        TextView timeStamp = (TextView)view.findViewById(getTimeStampId());
        TextView dateStamp = (TextView)view.findViewById(getDateStampId());

        if (isOwner()) {
            /*icon.setImageResource(R.drawable.cx_fa_wf_icon_small);
            icon.setImage(RkGlobalParams.getInstance().getIconBig(), false, 44, mContext, "head",
                    mContext);*/
        	icon.displayImage(ImageLoader.getInstance(), 
            		CxGlobalParams.getInstance().getIconSmall(), 
            		R.drawable.cx_fa_wf_icon_small, true, 
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
                    .findViewById(R.id.cx_fa_view_chat_chatting_face_row_circleProgressBar);
            if (msgSendState == 0) {
                reSendBtn.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
            }
            if (msgSendState == 1) {
                reSendBtn.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);
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
                                        ChatFragment.getInstance().reSendMessage(msgFace, 2, msgId);
                                    }
                                }).setNegativeButton(R.string.cx_fa_cancel_button_text, null).show();

                    }
                });
            }
        } else {
            /*icon.setImageResource(R.drawable.cx_fa_hb_icon_small);
            icon.setImage(RkGlobalParams.getInstance().getPartnerIconBig(), false, 44, mContext, "head",
                    mContext);*/
        	icon.displayImage(ImageLoader.getInstance(), 
            		CxGlobalParams.getInstance().getPartnerIconBig(), 
            		R.drawable.cx_fa_hb_icon_small, true, 
            		CxGlobalParams.getInstance().getMateSmallImgConner());
        	
            icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO 跳转到个人资料页
                    ChatFragment.getInstance().gotoOtherFragment("rkmate");
                }
            });
        }

        String[] faceValues = getFaceValues(view.getResources());
        String[] faceTexts = getFaceTexts(view.getResources());
        TypedArray faceImageIds = view.getResources().obtainTypedArray(R.array.face_images);

        CxLog.d(TAG, "messge face id=" + msgFace);
        for (int i = 0; i < faceTexts.length; i++) {
            if (faceTexts[i].equals(msgFace)) {
                CxLog.d(TAG, "face id=" + i);
                face.setGifImage(faceImageIds.getResourceId(i, 0));
                tag.mMsgText = faceTexts[i];
                break;
            }
        }
        faceImageIds.recycle();

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

    @Override
    public View build(View view, ViewGroup parent) {

        if (mMessage.getType() == Message.MESSAGE_TYPE_TEXT)
            return buildFromTextMessage(view, parent);

        ChatLogAdapter tag = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(getViewResourceId(), null);
        }

        FaceMessage message = (FaceMessage)mMessage;

        int msgSendState = message.getSendSuccess();
        int msgType = message.getType();
        final int msgId = message.getMsgId();
        final String msgFace = message.getFace();
        int msgCreatTimeStamp = message.getCreateTimestamp();

        if (tag == null) {
            tag = new ChatLogAdapter();
        }
        tag.mChatType = msgType;
        tag.mMsgId = msgId;
        // tag.mMsgText = msgFace;

        CxImageView icon = (CxImageView)view.findViewById(getIconId());
        // ImageView face = (ImageView)view.findViewById(getContentId());
        EnhancedGifView face = (EnhancedGifView)view.findViewById(getContentId());
        TextView timeStamp = (TextView)view.findViewById(getTimeStampId());
        TextView dateStamp = (TextView)view.findViewById(getDateStampId());

        if (isOwner()) {
            /*icon.setImageResource(R.drawable.cx_fa_wf_icon_small);
            icon.setImage(RkGlobalParams.getInstance().getIconSmall(), false, 44, mContext, "head",
                    mContext);*/
        	icon.displayImage(ImageLoader.getInstance(), 
            		CxGlobalParams.getInstance().getIconSmall(), 
            		R.drawable.cx_fa_wf_icon_small, true, 
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
                    .findViewById(R.id.cx_fa_view_chat_chatting_face_row_circleProgressBar);
            if (msgSendState == 0) {
                reSendBtn.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
            }
            if (msgSendState == 1) {
                reSendBtn.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);
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
                                        ChatFragment.getInstance().reSendMessage(msgFace, 2, msgId);
                                    }
                                }).setNegativeButton(R.string.cx_fa_cancel_button_text, null).show();

                    }
                });
            }
        } else {
            /*icon.setImageResource(R.drawable.cx_fa_hb_icon_small);
            icon.setImage(RkGlobalParams.getInstance().getPartnerIconBig(), 
            		false, 44, mContext, "head", mContext);*/
        	icon.displayImage(ImageLoader.getInstance(), 
            		CxGlobalParams.getInstance().getPartnerIconBig(), 
            		R.drawable.cx_fa_hb_icon_small, true, 
            		CxGlobalParams.getInstance().getMateSmallImgConner());
        	
            icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO 跳转到个人资料页
                    ChatFragment.getInstance().gotoOtherFragment("rkmate");
                }
            });
        }

        String[] faceValues = getFaceValues(view.getResources());
        String[] faceTexts = getFaceTexts(view.getResources());
        TypedArray faceImageIds = view.getResources().obtainTypedArray(R.array.face_images);

        CxLog.d(TAG, "messge face id=" + msgFace);
        for (int i = 0; i < faceValues.length; i++) {
            if (faceValues[i].equals(msgFace)) {
                CxLog.d(TAG, "face id=" + i);
//                face.setGifDecoderImage(Uri.parse("file:///storage/sdcard0/chuxin/face_huaxin.gif"));
                face.setGifImage(faceImageIds.getResourceId(i, 0));
                tag.mMsgText = faceTexts[i];
                break;
            }
        }
        faceImageIds.recycle();

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
