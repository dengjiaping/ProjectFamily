
package com.chuxin.family.views.chat;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.libs.gpuimage.utils.PictureUtils;
import com.chuxin.family.models.Message;
import com.chuxin.family.models.TextMessage;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.InteractiveImageSpan;
import com.chuxin.family.utils.RelativeInteractiveImageSpan;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.widgets.CustomTextView;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEntry extends ChatLogEntry {
    private static final String TAG = "TextEntry";

    private static final int VIEW_RES_ID = R.layout.cx_fa_view_chat_chatting_text_row;

    private static final int ICON_ID = R.id.cx_fa_view_chat_chatting_text_row__icon;

    private static final int CONTENT_ID = R.id.cx_fa_view_chat_chatting_text_row__content;

    private static final int TIMESTAMP_ID = R.id.cx_fa_view_chat_chatting_text_row__timestamp;

    private static final int DATESTAMP_ID = R.id.cx_fa_view_chat_chatting_text_row__datestamp;

    private static final int SEND_SUCCESS_BUTTON_ID = R.id.cx_fa_view_chat_chatting_text_row__exclamation;

    private static final int CHAT_VIEW_LINEARLAYOUT_ID = R.id.cx_fa_view_chat_chatting_row__content;

    private static final boolean OWNER_FLAG = true;
    private String mMsgText;
    
    // add by shichao 经典表情图文混排
    private static String[] sFaceValues = null;

    private static String[] sFaceTexts = null;

    public static String[] getFaceValues(Resources res) {
        if (sFaceValues == null) {
            sFaceValues = res.getStringArray(R.array.face_static_images_ids);
        }
        return sFaceValues;
    }

    public static String[] getFaceTexts(Resources res) {
        if (sFaceTexts == null) {
            sFaceTexts = res.getStringArray(R.array.face_texts);
        }
        return sFaceTexts;
    }


    public TextEntry(Message message, Context context, boolean isShowDate) {
		super(message, context, isShowDate);
	}

    @Override
    public int getType() {
		return ENTRY_TYPE_TEXT;
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
    
    public int getChatViewLinearLayoutId(){
    	return CHAT_VIEW_LINEARLAYOUT_ID;
    }

    @Override
    public View build(View view, ViewGroup parent) {
        ChatLogAdapter tag = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(getViewResourceId(), null);
        }
        CxImageView icon = (CxImageView)view.findViewById(getIconId());
        LinearLayout chatLinearLayout = (LinearLayout)view.findViewById(getChatViewLinearLayoutId());
        TextView text = (TextView)view.findViewById(getContentId());
        TextView timeStamp = (TextView)view.findViewById(getTimeStampId());
        TextView dateStamp = (TextView)view.findViewById(getDateStampId());
 
        TextMessage message = (TextMessage)mMessage;

        int msgType = message.getType();
        final int msgId = message.getMsgId();
        int msgCreatTimeStamp = message.getCreateTimestamp();
        mMsgText = message.getText();

        if (tag == null) {
            tag = new ChatLogAdapter();
        }
        tag.mChatType = msgType;
        tag.mMsgId = msgId;
        tag.mMsgText = mMsgText;
        // RkLog.v(TAG, "text" + message.getText());

        if (isOwner()) {
            if(message.getText().equals(mContext.getString(CxResourceString.getInstance().str_chat_welcome_first_msg))){
                mIsShowDate = true;
            }
        	//icon.setImageResource(R.drawable.cx_fa_wf_icon_small);
        	/*icon.setImage(RkGlobalParams.getInstance().getIconBig(), 
            		false, 44, mContext, "head", mContext);*/
            icon.setImageResource(CxResourceDarwable.getInstance().dr_chat_icon_small_me);
            icon.displayImage(ImageLoader.getInstance(), 
            		CxGlobalParams.getInstance().getIconSmall(), 
            		CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
            		CxGlobalParams.getInstance().getSmallImgConner());
            icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO update owner headimage
                    CxLog.i(TAG, "click update owner headimage button");
//                    mChatFragment.updateHeadImage();
                    ChatFragment.getInstance().updateHeadImage();
                }
            });
                int msgSendState = message.getSendSuccess();
                final ImageButton reSendBtn = (ImageButton)view.findViewById(getSendSuccessButtonId());
                ProgressBar pb = (ProgressBar) view.findViewById(R.id.cx_fa_view_chat_chatting_text_row_circleProgressBar);
                if(msgSendState == 0){
                    reSendBtn.setVisibility(View.GONE);
                    pb.setVisibility(View.VISIBLE);
                } else if (msgSendState == 1) {
                    reSendBtn.setVisibility(View.GONE);
                    pb.setVisibility(View.GONE);
                } if ( msgSendState == 2 ) {
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
                                            ChatFragment.getInstance().reSendMessage(mMsgText, 0, msgId);
                                        }
                                    }).setNegativeButton(R.string.cx_fa_cancel_button_text, null).show();
                        }
                    });
                }
        } else {
        	//icon.setImageResource(R.drawable.cx_fa_hb_icon_small);
        	/*icon.setImage(RkGlobalParams.getInstance().getPartnerIconBig(), 
            		false, 44, mContext, "head", mContext);*/
        	icon.setImageResource(CxResourceDarwable.getInstance().dr_chat_icon_small_oppo);
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
        text.setText("");
        
        if(CxGlobalParams.getInstance().getSingle_mode()==1 && !isOwner()){
        	if(mMsgText.contains("小家看起来很不错，快邀请我进来玩吧")){
        		text.setText(CxResourceString.getInstance().str_chat_welcome_single_mode_default);
        	}
        }else{
        	String[] faceTexts = getFaceTexts(mContext.getResources());
            TypedArray faceImageIds = mContext.getResources().obtainTypedArray(R.array.cx_fa_ids_input_panel_face_images);


            boolean isFace=false;
            boolean hasright=false;
            SpannableStringBuilder spanStr = null;
            for(int i=0;i<mMsgText.length();i++){
                if('['==mMsgText.charAt(i)){
                    for(int j=i+1;j<mMsgText.length();j++){
                        if(']'==mMsgText.charAt(j)){
                            hasright=true;
                            
                            String substring = mMsgText.substring(i, j+1);
                            for(int m=0;m<faceTexts.length;m++){
                                if(faceTexts[m].equals(substring)){
                                
                                    isFace=true;
                                    int resourceId = faceImageIds.getResourceId(m, 0);
                                    spanStr = TextUtil.getImageSpanStr(substring, resourceId, 
                                            mContext.getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_size), 
                                            mContext.getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_size), mContext);
                                    text.append(spanStr);
                                    break;
                                }
                            }   
                            if(!isFace){
                                text.append(substring);                      
                            }
                            i=j;
                            break;                                          
                        }
                    }
                    if(!hasright){
                        text.append("[");                        
                    }
                }else{
                    text.append(mMsgText.charAt(i)+"");
                }
                
            }
        }
        
        
        
        
        
        
        
        
        // 图文混排 add by shichao
/*        try {
            SpannableStringBuilder builder = new SpannableStringBuilder(mMsgText);
            Pattern pattern = Pattern.compile("\\[(\\w+?)\\]");
            Matcher matcher = pattern.matcher(mMsgText);
            
            String[] faceValues = getFaceValues(view.getResources());
            String[] faceTexts = getFaceTexts(view.getResources());
            TypedArray faceImageIds = view.getResources().obtainTypedArray(R.array.face_static_images);
            PictureUtils pu = new PictureUtils(mContext);
            while(matcher.find()){
                for(int i = 0; i<matcher.groupCount(); i++){
                    System.out.println("Group 0:"+matcher.group(i));//得到第0组——整个匹配
                    for(int j = 0; j < faceTexts.length; j++){
                       if(matcher.group(i).equals(faceTexts[j])){
//                           Field field = R.drawable.class.getDeclaredField(faceValues[j]);
//                           int resourceId = Integer.parseInt(field.get(null).toString());
                           int resourceId = faceImageIds.getResourceId(j, 0);
                           Drawable drawable = mContext.getResources().getDrawable(resourceId);
//                           Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId);
//                           Bitmap imageBitmap = pu.zoomBitmap(bitmap, 45, 45);
//                           ImageSpan span = new ImageSpan(mContext, imageBitmap, ImageSpan.ALIGN_BASELINE);
                           InteractiveImageSpan span = new RelativeInteractiveImageSpan(drawable, 2.0f, 1);
//                           drawable.setBounds(0, 0, 12, 12);
                           builder.setSpan(span, matcher.start(),
                                   matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                           bitmap.recycle();
                           break;
                       }
                    }
                    
                }
            }
            
            text.setText(builder);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } */
//        catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
        
//        text.setText(mMsgText);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date((long)(msgCreatTimeStamp) * 1000));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String dateNow = dateFormat.format(new Date((long)(msgCreatTimeStamp) * 1000));
        
        if (mIsShowDate) {
            dateStamp.setVisibility(View.VISIBLE);
            dateStamp.setText(dateNow);
        } else {
            dateStamp.setVisibility(View.GONE);
        }

        String format = view.getResources().getString(
                R.string.cx_fa_nls_reminder_period_time_format);
        String time = String.format(format, calendar);
        timeStamp.setText(time);

        view.setTag(tag);
        return view;
    }

}
