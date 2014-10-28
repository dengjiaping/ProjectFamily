package com.chuxin.family.views.chat;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.models.Message;
import com.chuxin.family.models.SystemMessage;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxFamilyInfoData;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

public class SystemEntry extends ChatLogEntry {
    public SystemEntry(Message message, Context context, boolean isShowDate) {
		super(message, context, isShowDate);
	}

	private static final String TAG = "SystemEntry";

    private static final int VIEW_RES_ID = R.layout.cx_fa_view_chat_chatting_system_row;

    private static final int ICON_ID = R.id.cx_fa_view_chat_chatting_system_row_for_partner__headimage;

    private static final int CONTENT_ID = R.id.cx_fa_view_chat_chatting_system_text_for_partner;

    private static final int TIMESTAMP_ID = R.id.cx_fa_view_chat_chatting_system_row_for_partner__timestamp;

    private static final int DATESTAMP_ID = R.id.cx_fa_view_chat_chatting_system_row_for_partner__datestamp;

    private static final int CHAT_VIEW_LINEARLAYOUT_ID = R.id.cx_fa_view_chat_chatting_system_row_for_partner__content;

    private static final boolean OWNER_FLAG = false;
    
    private final int NO_BUTTON = 0; // 没有按钮
    private final int ONE_BUTTON = 1; // 有1个按钮
    private final int TWO_BUTTON =2; // 有两个按钮
    
    private final int PARTNER_ICON_TYPE = 0; // 伴侣头像
    private final int SYSTEM_ICON_TYPE = 1;// 系统头像
    private final int OTHER_ICON_TYPE = 2;// 其他头像
    
    private String mMsgText;

    @Override
    public int getType() {
		return ENTRY_TYPE_SYSTEM;
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
        
        TextView buttonLeft = (TextView)view.findViewById(R.id.cx_fa_view_chat_system_animation_reply_btn1);
        TextView buttonRight = (TextView)view.findViewById(R.id.cx_fa_view_chat_system_animation_reply_btn2);
        TextView buttonCenter = (TextView)view.findViewById(R.id.cx_fa_view_chat_system_animation_reply_btn3);
        TextView titleView = (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_system_title_for_partner);
        
        LinearLayout buttonLayout = (LinearLayout)view.findViewById(R.id.cx_fa_view_chat_system_animation_btn_wrap);        
        LinearLayout buttonLinearLayout = (LinearLayout)view.findViewById(R.id.cx_fa_view_chat_system_two_btn_linearlayout);
        LinearLayout buttonCenterLinearLayout = (LinearLayout)view.findViewById(R.id.cx_fa_view_chat_system_single_btn_linearlayout);
        ImageView bottomLine = (ImageView)view.findViewById(R.id.cx_fa_system_animation_bottom_line);


        SystemMessage msg = (SystemMessage)mMessage;

        int msgType = msg.getType();
        final int msgId = msg.getMsgId();
        int msgCreatTimeStamp = msg.getCreateTimestamp();
        mMsgText = msg.getText();
        String btn1Name = msg.getSystemBtn1Name();
        String btn2Name = msg.getSystemBtn2Name();
        String title = msg.getSystemTitle();
        final String method = msg.getSystemMethod();
        final String value1 = msg.getSystemValue1();
        final String value2 = msg.getSystemValue2();
        String btn3Name = msg.getSystemBtnName();
        int mode = msg.getSystemMode();
        final int redirect = msg.getSystemRedirect();
        int iconType = msg.getSystemIconType();
        String iconUrl = msg.getSystemIconUrl();
        final String btn1Confirm = msg.getSystemBtn1Confirm();
        final String btn2Confirm = msg.getSystemBtn2Confirm();
        
        titleView.setText(title);
        buttonLeft.setText(btn1Name);
        buttonRight.setText(btn2Name);
        buttonCenter.setText(btn3Name);
        
        if(PARTNER_ICON_TYPE == iconType){
            /*if( (null == RkGlobalParams.getInstance().getPartnerIconBig()) || //add by niechao
            		RkGlobalParams.getInstance().getPartnerIconBig().equals("")){
                icon.setImageResource(R.drawable.cx_fa_hb_icon_small);
            } else {
//                icon.setImage(RkGlobalParams.getInstance().getPartnerIconBig(), false, 44, mContext, "head", mContext);
            }*/
        	icon.displayImage(ImageLoader.getInstance(), 
        			CxGlobalParams.getInstance().getPartnerIconBig(), 
        			CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
        			CxGlobalParams.getInstance().getMateSmallImgConner());
        	
            chatLinearLayout.setBackgroundResource(R.drawable.chatview_bubble_someone);
        } else if(SYSTEM_ICON_TYPE == iconType){
            icon.setImageResource(R.drawable.chatview_message_icon);
        } else if(OTHER_ICON_TYPE == iconType){
            /*if(null != iconUrl){
                icon.setImage(iconUrl, false, 44, mContext, "head", mContext);
            }*/
            icon.displayImage(ImageLoader.getInstance(), iconUrl, 
            		CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
            		CxGlobalParams.getInstance().getSmallImgConner());
        }
        
        if(NO_BUTTON == mode){
            buttonLayout.setVisibility(View.GONE);
        } else if(ONE_BUTTON == mode){
            buttonLayout.setVisibility(View.VISIBLE);
            buttonLinearLayout.setVisibility(View.GONE);
            buttonCenterLinearLayout.setVisibility(View.VISIBLE);
            bottomLine.setVisibility(View.GONE);
        } else if(TWO_BUTTON == mode){
            buttonLayout.setVisibility(View.VISIBLE);
            buttonLinearLayout.setVisibility(View.VISIBLE);
            buttonCenterLinearLayout.setVisibility(View.GONE);
            bottomLine.setVisibility(View.VISIBLE);
        } else {
            buttonLayout.setVisibility(View.GONE);
        }
        
//        if(TextUtils.isEmpty(msg.getSystemBtnName())){
//        	buttonLinearLayout.setVisibility(View.VISIBLE);
//        	buttonCenterLinearLayout.setVisibility(View.GONE);
//        } else {
//        	buttonLinearLayout.setVisibility(View.GONE);
//        	buttonCenterLinearLayout.setVisibility(View.VISIBLE);
//        }
        if("user.update_family_bg".equals(method)){
        	UserApi.getInstance().requestFamilyInfo(mContext, familyBgCaller);
        }
        
        
        buttonLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ChatFragment.getInstance().processSystemMessage(msgId, method, "1", value1, value2, btn1Confirm);
			}
		});
        
        buttonRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ChatFragment.getInstance().processSystemMessage(msgId, method, "2", value1, value2, btn2Confirm);
			}
		});
        
        if (tag == null) {
            tag = new ChatLogAdapter();
        }
        tag.mChatType = msgType;
        tag.mMsgId = msgId;
        tag.mMsgText = mMsgText;

        text.setText(mMsgText);
        
        chatLinearLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ChatFragment.getInstance().redirectFragment(redirect);
            }
        });
        text.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        chatLinearLayout.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
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
    
    
    
    
    JSONCaller familyBgCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(result==null){				
				return -1;
			}
			
			CxFamilyInfoData data=(CxFamilyInfoData)result;
			if(data.getRc()!=0){				
				return -2;
			}
			
			if(data.getMeInfo()==null || data.getOppoInfo()==null){			
				return -3;
			}
			
			String familyImgPath=data.getFamily_icon();
		
			CxGlobalParams.getInstance().setFamily_big(familyImgPath);
			
			return 0;
		}
			
	};
    
    
    
    
    
    
    
    
    
    

}
