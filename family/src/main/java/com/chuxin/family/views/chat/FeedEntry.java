
package com.chuxin.family.views.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxAuthenNew;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.models.FeedMessage;
import com.chuxin.family.models.LocationMessage;
import com.chuxin.family.models.Message;
import com.chuxin.family.models.TextMessage;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxMateProfile;
import com.chuxin.family.parse.been.CxUserProfile;
import com.chuxin.family.parse.been.data.CxMateProfileDataField;
import com.chuxin.family.parse.been.data.CxUserProfileDataField;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxUserProfileKeeper;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

public class FeedEntry extends ChatLogEntry {
    private static final String TAG = "FeedEntry";

    private static final int VIEW_RES_ID = R.layout.cx_fa_view_chat_chatting_feed_row_for_partner;

    private static final int ICON_ID = R.id.cx_fa_view_chat_chatting_feed_row_for_partner__headimage;

    private static final int CONTENT_ID = R.id.cx_fa_view_chat_chatting_feed_text_for_partner;

    private static final int TIMESTAMP_ID = R.id.cx_fa_view_chat_chatting_feed_row_for_partner__timestamp;

    private static final int DATESTAMP_ID = R.id.cx_fa_view_chat_chatting_feed_row_for_partner__datestamp;

    private static final int CHAT_VIEW_LINEARLAYOUT_ID = R.id.cx_fa_view_chat_chatting_feed_row_for_partner__content;

    private static final boolean OWNER_FLAG = false;
    private String mMsgText;

    public FeedEntry(Message message, Context context, boolean isShowDate) {
		super(message, context, isShowDate);
	}

    @Override
    public int getType() {
		return ENTRY_TYPE_FEED;
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

        FeedMessage msgfeed = (FeedMessage)mMessage;

        int msgType = msgfeed.getType();
        final int msgId = msgfeed.getMsgId();
        int msgCreatTimeStamp = msgfeed.getCreateTimestamp();
        mMsgText = msgfeed.getText();
        final String feedCategory = msgfeed.getFeedCategory();
        
        if("rkmate".equals(feedCategory)){
        	UserApi.getInstance().getUserPartnerProfile(oppoCaller);
        }else if("settings".equals(feedCategory)){
        	UserApi.getInstance().getUserProfile(CxGlobalParams.getInstance().getUserId(),meCaller);
        }
        
        chatLinearLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ChatFragment.getInstance().gotoOtherFragment(feedCategory);
            }
        });
        text.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ChatFragment.getInstance().gotoOtherFragment(feedCategory);
            }
        });
        text.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        if (tag == null) {
            tag = new ChatLogAdapter();
        }
        tag.mChatType = msgType;
        tag.mMsgId = msgId;
        tag.mMsgText = mMsgText;
        // RkLog.v(TAG, "text" + message.getText());

        if (isOwner()) {
        	/*icon.setImageResource(R.drawable.cx_fa_wf_icon_small);
            icon.setImage(RkGlobalParams.getInstance().getIconBig(), 
            		false, 44, mContext, "head", mContext);*/
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
        } else {
        	/*icon.setImageResource(R.drawable.cx_fa_hb_icon_small);
            icon.setImage(RkGlobalParams.getInstance().getPartnerIconBig(), 
            		false, 44, mContext, "head", mContext);*/
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

        text.setText(mMsgText);
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
    
    
    
    JSONCaller meCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			
			if (null == result) {
				
				return -1;
			}
			
			CxUserProfile userInitInfo = null;
			try {
				userInitInfo = (CxUserProfile)result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null == userInitInfo) {		
				return -1;
			}
			if (0 != userInitInfo.getRc()) {			
				return -2;
			}
		
			CxUserProfileDataField profile = userInitInfo.getData();
			if (null == profile) {			
				return -3;
			}
			
			CxGlobalParams.getInstance().setIconBig(profile.getIcon_big());
			CxGlobalParams.getInstance().setIconMid(profile.getIcon_mid());
			CxGlobalParams.getInstance().setIconSmall(profile.getIcon_small());
			
			return 0;
		}
	};
    
	
	JSONCaller oppoCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if (null == result) {
				return -1; //不做其他处理
			}
			try {
				CxMateProfile mateProfile = (CxMateProfile)result;
				if (0 != mateProfile.getRc() || (null == mateProfile.getData()) ) {
					return -1; //不做其他处理
				}
				//正常获取成功就要设置伴侣资料到RkMateParams
				CxMateProfileDataField profileDataField = mateProfile.getData();
				CxGlobalParams.getInstance().setPartnerIconBig(profileDataField.getIcon());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return 0;
		}
	};
	
    
    
    
    
    
    
    
    
    
    
    

}
