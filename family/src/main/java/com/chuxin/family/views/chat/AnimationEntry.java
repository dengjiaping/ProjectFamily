package com.chuxin.family.views.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.models.AnimationMessage;
import com.chuxin.family.models.Message;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 抽了鞭子和弹了脑壳 (后端命名为animation，所以android也就跟着在此用该名称)
 * @author dujy
 *
 */
public class AnimationEntry extends ChatLogEntry{
	private static final String TAG = "AnimationEntry";
	private Context mContext;
	private int animation_id, effect;
	
	public AnimationEntry(Message message, Context context,	boolean isShowDate) {
		super(message, context, isShowDate);
		this.mContext = context;
	}

	@Override
	public boolean isOwner() {
		return true;
	}

	@Override
	public int getType() {
		return ENTRY_TYPE_ANIMATION;
	}

	@Override
	public View build(View view, ViewGroup parent) {
		ChatLogAdapter tag = null;
		
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.cx_fa_view_chat_chatting_animation_row,	null);
		}
		
		AnimationMessage msg = (AnimationMessage)mMessage;
		int msgType 		= msg.getType();
        final int msgId 	= msg.getMsgId();
        int msgCreatTimeStamp = msg.getCreateTimestamp();
       //mMsgText = msg.getText();

        if (tag == null) {
            tag = new ChatLogAdapter();
        }
        tag.mChatType 	= msgType;
        tag.mMsgId 		= msgId;
        //tag.mMsgText 	= mMsgText;
        
        view.setTag(tag);
        
        // 头像
        CxImageView icon = (CxImageView) view.findViewById(R.id.cx_fa_view_chat_chatting_animation_row_icon);
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
                CxLog.i(TAG, "click update owner headimage button");
                ChatFragment.getInstance().updateHeadImage();
            }
        });
        
        // 是否发送成功（如果发送成功，就把小红点隐藏）
        final ImageButton reSendBtn 	= (ImageButton)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_row__exclamation);
        ProgressBar pb 							= (ProgressBar) view.findViewById(R.id.cx_fa_view_chat_chatting_animation_row_circleProgressBar);		// 发送中的图标
        if(msg.getSendSuccess()==0){			// 发送中
        	 pb.setVisibility(View.VISIBLE);
        	 reSendBtn.setVisibility(View.GONE);
        }else{									// 发送完成
        	pb.setVisibility(View.GONE);
        	
        	if(msg.getSendSuccess()==1){		// 发送成功
            	reSendBtn.setVisibility(View.GONE);
            }else{												// 发送失败
            	reSendBtn.setVisibility(View.VISIBLE);	
            	
            	// 重复发送
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
                                        String msg = animation_id + "," + effect;
                                        ChatFragment.getInstance().reSendMessage( msg,  Message.MESSAGE_TYPE_ANIMATION, msgId);
                                    }
                                }).setNegativeButton(R.string.cx_fa_cancel_button_text, null).show();
                    }
                });
            }
        }
        
        	
        	
        
        // 设置消息时间
        TextView dateStamp = (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_row_datestamp);
        TextView timeStamp = (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_row_timestamp);
        String format = view.getResources().getString(R.string.cx_fa_nls_reminder_period_time_format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date((long)(msgCreatTimeStamp) * 1000));
        String time = String.format(format, calendar);
        timeStamp.setText(time);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String dateNow = dateFormat.format(new Date((long)(msgCreatTimeStamp) * 1000));
        if (mIsShowDate) {
            dateStamp.setVisibility(View.VISIBLE);
            dateStamp.setText(dateNow);
        } else {
            dateStamp.setVisibility(View.GONE);
        }
        
        try {
			animation_id = msg.mData.getInt("animation_id");
			effect				= msg.mData.getInt("effect");
		} catch (JSONException e) {
			CxLog.e(TAG, "获取animation_id 和 effect 时发生错误:" + e.getMessage());
		}
        
        // 根据不同情况，要改变的几个页面对象
        TextView title 		= (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_row_title);		// 标题 
        ImageView pic 	= (ImageView)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_row_pic);		// 配图
        TextView info 		= (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_row_text);		// 提示信息
        pic.setVisibility(View.VISIBLE);			// 防止缓存，此处在设置一次
        
        // 判断对方版本是否支持抽鞭子和弹脑壳 (animation_id与effect相等，代表是抽了鞭子或弹了脑壳)
        if(animation_id==effect){
        	boolean is_support = true;		// 对方版本是否支持
        	try {
        		if(msg.mData.has("is_support")){
        			is_support = msg.mData.getBoolean("is_support");
        		}
        	} catch (JSONException e) {
        		CxLog.e(TAG, "获取is_support时发生错误:" + e.getMessage());
        	}
        	
        	// 如果不支持
        	if( !is_support){
	        	 if(animation_id==0){				// 抽鞭子
	 	        		title.setText(R.string.cx_fa_animation_0_name);
	        	 }else{											// 弹脑壳
	        		 	title.setText(R.string.cx_fa_animation_1_name);
	        	 }
	        	pic.setVisibility(View.GONE);;
	        	info.setText(R.string.cx_fa_animation_nonsupport);
	        	
	        	return view;
        	}
        }
        
        
        /*
         *  判断是否需要用户应答   
         *  		animation_id: 动画ID     [1-2]  0：抽鞭子，1：弹脑壳
         *         effect: 动画效果               [0-9] 0：抽鞭子 2，3，4，5 回复抽鞭子
         *          								                 1：弹脑壳 6，7，8，9 回复弹脑壳
         */
        if(animation_id==0){			// 抽鞭子
	        	if(effect==0){				// 抽了鞭子
	        		title.setText(R.string.cx_fa_animation_0_name);
	        		info.setText(R.string.cx_fa_animation_0_hit);
	        		pic.setImageResource(R.drawable.whip_imagewhip);
	        	}else if(effect==2){		// 回复了 "求饶" 
	        		title.setText(R.string.cx_fa_animation_0_reply_title);
	        		info.setText(R.string.cx_fa_animation_0_reply_text_1);
	        		pic.setImageResource(R.drawable.whip_replyqiurao);	        		
	        	}else if(effect==3){		// 回复了 "耍赖"
	        		title.setText(R.string.cx_fa_animation_0_reply_title);
	        		info.setText(R.string.cx_fa_animation_0_reply_text_2);
	        		pic.setImageResource(R.drawable.whip_replyshualai);
	        	}        	
        }else if(animation_id==1){	// 弹脑壳
        		if(effect==1){					// 弹了脑壳
        			title.setText(R.string.cx_fa_animation_1_name);
	        		info.setText(R.string.cx_fa_animation_1_hit);
	        		pic.setImageResource(R.drawable.whip_imagehead);
	        	}else if(effect==6){		// 回复了 "撒娇" 
	        		title.setText(R.string.cx_fa_animation_1_reply_title);
	        		info.setText(R.string.cx_fa_animation_1_reply_text_1);
	        		pic.setImageResource(R.drawable.whip_replysajiao);	        	
	        	}else if(effect==7){		// 回复了 "反击"
	        		title.setText(R.string.cx_fa_animation_1_reply_title);
	        		info.setText(R.string.cx_fa_animation_1_reply_text_2);
	        		pic.setImageResource(R.drawable.whip_replyfanji);
	        	}
        }
        
        
		return view;
	}
	
	
}
