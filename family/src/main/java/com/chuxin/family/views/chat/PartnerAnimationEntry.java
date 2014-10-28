package com.chuxin.family.views.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.ant.liao.chuxin.EnhancedGifView;
import com.ant.liao.chuxin.GifView.GifImageType;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.models.AnimationMessage;
import com.chuxin.family.models.Message;
import com.chuxin.family.net.ChatApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.whip.WhipActivity;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PartnerAnimationEntry extends ChatLogEntry{
	private static final String TAG = "PartnerAnimationEntry";
	private String mMsgText;
	private AnimationMessage mAnimationMsg ;
	private Context mContext;
	private int animation_id, effect;
	private PopupWindow mPopWin ;			// 播放动画时使用
	private LayoutInflater inflater; 
	
   private EnhancedGifView gifView;
   private static MediaPlayer sVoicePlayer = null; 
   private int soundResId, gifResId ;						// 要播放的声音资源ID、动画资源ID
   private View popView;										// 播放动画时的视图
   private  CxImageView shakeHeadImg;				// 要晃动的头像对像
   
   private static boolean mIsAnimate = false;
	    
	public PartnerAnimationEntry(Message message, Context context,	boolean isShowDate) {
		super(message, context, isShowDate);
		this.mContext = context;
	}

	@Override
	public boolean isOwner() {
		return false;
	}

	@Override
	public int getType() {
		return ENTRY_TYPE_PEER_ANIMATION;
	}

	@Override
	public View build(View view, ViewGroup parent) {
		ChatLogAdapter tag = null;
		
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.cx_fa_view_chat_chatting_animation_for_partner,	null);
		}
		
		mAnimationMsg = (AnimationMessage)mMessage;
		int msgType 		= mAnimationMsg.getType();
        final int msgId 	= mAnimationMsg.getMsgId();
        int msgCreatTimeStamp = mAnimationMsg.getCreateTimestamp();
//        mMsgText = message.getText();

        if (tag == null) {
            tag = new ChatLogAdapter();
        }
        tag.mChatType 	= msgType;
        tag.mMsgId 		= msgId;
//        tag.mMsgText 	= mMsgText;
        
        // 头像
        CxImageView icon = (CxImageView) view.findViewById(R.id.cx_fa_view_chat_chatting_animation_row_for_partner_icon);
        /*if(RkGlobalParams.getInstance().getPartnerIconBig()==null || RkGlobalParams.getInstance().getPartnerIconBig().equals("")){
        	icon.setImageResource(R.drawable.cx_fa_hb_icon_small);
        }else{
	        icon.setImage(RkGlobalParams.getInstance().getPartnerIconBig(), 
	        		false, 44, mContext, "head", mContext);
        }*/
        icon.displayImage(ImageLoader.getInstance(), 
        		CxGlobalParams.getInstance().getPartnerIconBig(), 
        		CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
        		CxGlobalParams.getInstance().getMateSmallImgConner());
        
        icon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CxLog.i(TAG, "click update owner headimage button");
                // 跳转到个人资料页
				ChatFragment.getInstance().gotoOtherFragment("rkmate");
            }
        });
        
        
        // 设置消息时间
        TextView dateStamp = (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_row_datestamp);
        TextView timeStamp = (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_row_for_partner_timestamp);
        String format = view.getResources().getString(R.string.cx_fa_nls_reminder_period_time_format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date((long)(msgCreatTimeStamp) * 1000));
        String time = String.format(format, calendar);
        timeStamp.setText(time);
        
        if (mIsShowDate) {
        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        	String dateNow = dateFormat.format(new Date((long)(msgCreatTimeStamp) * 1000));
            dateStamp.setVisibility(View.VISIBLE);
            dateStamp.setText(dateNow);
        } else {
            dateStamp.setVisibility(View.GONE);
        }
        
        try {
			animation_id = mAnimationMsg.mData.getInt("animation_id");
			effect				= mAnimationMsg.mData.getInt("effect");
		} catch (JSONException e) {
			CxLog.e(TAG, "获取animation_id 和 effect 错误:" + e.getMessage());
		}
        
        // 是否需要播放动画 (effect 跟 animation_id相等，表示对方弹了脑壳或抽了鞭子。这时侯才需要播放动画, 如果是对方的回复，则不需要播放动画)
        if(effect==animation_id){
        	// 是否已读
        	boolean is_read = false;
        	try {
        		if( mAnimationMsg.mData.has("is_read") ){
        			is_read = mAnimationMsg.mData.getBoolean("is_read");
        		}
			} catch (JSONException e) {
				CxLog.e(TAG, "得到is_read出错:"+e.getMessage());		
			}
        	
        	
        	/* 如果没读，则开始播放动画 */
        	if(!is_read && !mIsAnimate){
        		mIsAnimate = true;
            	// 先把此条消息置为已读 (防止低端手机一播放动画就闪退，再次进来还要播放，继续闪退的问题。先设为已读，即使闪退，下次进来就不会再播了)
    		    try {
    				mAnimationMsg.mData.put("is_read", true);
    				mAnimationMsg.update();
    				ChatFragment.getInstance().updateChatViewDB();		// 强行同步一下视图中的数据(要不然Message会有缓存，还会再次反复播放动画)
    			} catch (JSONException e) {
    				CxLog.e(TAG, e.getMessage());
    			}
    		    
	        	 inflater  				= LayoutInflater.from(mContext);
	        	 popView 			= inflater.inflate(R.layout.cx_fa_view_chat_whip_partner_animation, null);
	        	 gifView 				= (EnhancedGifView)popView.findViewById(R.id.cx_fa_view_chat_whip_gif_partner);
	        	 
	        	mPopWin = new PopupWindow(popView, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, true);
	        	if(animation_id==0){
	        		soundResId 	= R.raw.whip_pumping_whip;
	    			gifResId			= R.drawable.whip_gif_whip;
	        	}else if(animation_id==1){
	        		soundResId 	= R.raw.whip_bullet_head;
	    			gifResId			= R.drawable.whip_gif_head;
	        	}
	        	mPopWin.showAtLocation(view, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
	        	playGifAndSound();			// 播放声音和动画，播放完成后会把本条消息置为已读
	        }
        }
        
        // 根据不同情况，要改变的几个页面对象
        TextView title 		= (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_reply_title);		// 标题 
        ImageView pic 	= (ImageView)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_reply_pic);	// 配图
        TextView info 		= (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_reply_info);		// 提示信息
        
        /*
         *  判断是否需要用户应答   
         *         effect: 动画效果    [0-9] 0：抽鞭子 2，3，4，5 回复抽鞭子
         *                                               1：弹脑壳 6，7，8，9 回复弹脑壳
         */
        LinearLayout  btnWrap = (LinearLayout) view.findViewById(R.id.cx_fa_view_chat_chatting_animation_btn_wrap);
        if(effect==0 || effect==1){        	// 需要用户应答
        	//将按钮区域显示
        	btnWrap.setVisibility(View.VISIBLE);
        	
        	TextView btn1 = (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_reply_btn1);  // 回复按钮1 (求饶、撒娇)
        	TextView btn2 = (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_animation_reply_btn2);  // 回复按钮2 (耍赖、反击)
        	
        	if(animation_id==0){	
        		//抽鞭子
        		title.setText( mContext.getString(R.string.cx_fa_animation_0_name) );
        		pic.setImageResource(R.drawable.whip_imagewhip);
        		info.setText( mContext.getString(R.string.cx_fa_animation_0_hit) );
        		
        		btn1.setText(R.string.cx_fa_animation_0_reply_btn_1);
        		btn2.setText(R.string.cx_fa_animation_0_reply_btn_2);
        	}else{
        		// 弹脑壳	
        		title.setText( mContext.getString(R.string.cx_fa_animation_1_name) );
        		pic.setImageResource(R.drawable.whip_imagehead);
        		info.setText( mContext.getString(R.string.cx_fa_animation_1_hit) );
        		
        		btn1.setText(R.string.cx_fa_animation_1_reply_btn_1);
        		btn2.setText(R.string.cx_fa_animation_1_reply_btn_2);
        	}
        	
        	btn1.setOnClickListener(btnClickListener);
        	btn2.setOnClickListener(btnClickListener);
        }else{        	// 不需要用户应答，只显示对方回复结果
        	//将按钮区域隐藏
        	btnWrap.setVisibility(View.GONE);
        	
        	// 判断是抽鞭子还是弹脑壳，来显示对应的回复
        	if(animation_id==0){
        		// 抽鞭子
        		title.setText( mContext.getString(R.string.cx_fa_animation_0_reply_title) );
        		if(effect==2){				// 求饶
	        		pic.setImageResource(R.drawable.whip_replyqiurao);
	        		info.setText( mContext.getString(R.string.cx_fa_animation_0_reply_text_1) );
        		}else if(effect==3){		// 耍赖
	        		pic.setImageResource(R.drawable.whip_replyshualai);
	        		info.setText( mContext.getString(R.string.cx_fa_animation_0_reply_text_2) );
        		}
        	}else{
        		// 弹脑壳
        		title.setText( mContext.getString(R.string.cx_fa_animation_1_reply_title) );
        		if(effect==6){				// 撒娇
	        		pic.setImageResource(R.drawable.whip_replysajiao);
	        		info.setText( mContext.getString(R.string.cx_fa_animation_1_reply_text_1) );
        		}else if(effect==7){		// 反击
	        		pic.setImageResource(R.drawable.whip_replyfanji);
	        		info.setText( mContext.getString(R.string.cx_fa_animation_1_reply_text_2) );
        		}
        	}
        }
        
        
        view.setTag(tag);
		return view;
	}

	// 按钮点击
	OnClickListener btnClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
				case R.id.cx_fa_view_chat_chatting_animation_reply_btn1:
					if(animation_id==0){
						effect 				= 2;				// 求饶
					}else if(animation_id==1){
						effect 				= 6;				// 撒娇
					}
									
					break;
				case R.id.cx_fa_view_chat_chatting_animation_reply_btn2:
					if(animation_id==0){
						effect 				= 3;					// 耍赖
					}else if(animation_id==1){
						effect 				= 7;					// 反击
					}					
					
					break;
				default:
					break;
			}
			
			String msg2 = animation_id + "," + effect;
	        ChatFragment.getInstance().sendMessage(msg2, com.chuxin.family.models.Message.MESSAGE_TYPE_ANIMATION);
			
		}
	};
	
	/**
	 * 播放动画和声音
	 */
	private void playGifAndSound(){
		// 播放动画
		gifView.setGifImageType(GifImageType.COVER);
		gifView.setGifImage(gifResId);
		gifView.showCover();
		
		// 播放声音
		playSoundTimes = 0;
		playSound();
	}
	
	
	int playSoundTimes = 0;			// 播放计数器，记录播放了几次
	/**
	 * 播放声音
	 */
	private void playSound(){
		final int maxTimes = 3;					// 最多播放几次
		// 播放声音
 		if (sVoicePlayer == null) {
			sVoicePlayer = MediaPlayer.create(mContext.getApplicationContext(), soundResId);
			sVoicePlayer.setLooping(false);
 		
			sVoicePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
				 @Override  
	             public void onCompletion(MediaPlayer mp) {  
					 CxLog.w("chat animation", "------complete animation------");
					 
						 sVoicePlayer.release();
						 sVoicePlayer = null;
						 
						 if(playSoundTimes<maxTimes-1){
							 playSoundTimes = playSoundTimes +1;
							 playSound();				// 默认重复播放
				 		}else{
				 			/* 动画和声音播放完成后，要去做的事 */				 		
				 			gifView.destroy();
				 			mPopWin.dismiss();		// 关闭本窗口
				 			mIsAnimate = false;
				 		}
	             }
			});
			
 		}

		try { 
			if (!sVoicePlayer.isPlaying()){
				sVoicePlayer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			if (sVoicePlayer != null) {
				sVoicePlayer.release();
				sVoicePlayer = null;
			}
		}
	}
	
	
}
