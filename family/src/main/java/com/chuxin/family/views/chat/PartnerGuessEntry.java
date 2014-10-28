package com.chuxin.family.views.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.models.AnimationMessage;
import com.chuxin.family.models.GuessRequestMessage;
import com.chuxin.family.models.GuessResponseMessage;
import com.chuxin.family.models.Message;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.widgets.CxInputPanel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PartnerGuessEntry extends ChatLogEntry{
	private String TAG = "GuessEntry";
	private GuessResponseMessage mResponseMsg ;
	private GuessRequestMessage mRequestMsg ;
	
	private String guess_id;
	private int value1;
	private int value2;
	private int result;
	private int[] fingerImg = new int[]{R.drawable.chat_btnguess_scissors, R.drawable.chat_btnguess_rock, R.drawable.chat_btnguess_paper};		// 猜拳的三种图片
	
	public PartnerGuessEntry(Message message, Context context, boolean isShowDate) {
		super(message, context, isShowDate);
	}

	@Override
	public boolean isOwner() {
		return false;
	}

	@Override
	public int getType() {
		return ENTRY_TYPE_PEER_GUESS_REPONSE;
	}

	@Override
	public View build(View view, ViewGroup parent) {
		ChatLogAdapter tag = null;
		
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.cx_fa_view_chat_chatting_guess_for_partner,	null);
		}
		
		LinearLayout layer1 	= (LinearLayout)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_for_partner__layer1);
		LinearLayout layer2 	= (LinearLayout)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_for_partner__layer2);
	    ImageView gif 			= (ImageView)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_for_partner_pic);						// 显示动画
	      
		// 判断消息类型
		boolean isRequest = true;
		if(mMessage instanceof GuessRequestMessage){
			mRequestMsg 	= (GuessRequestMessage)mMessage;
			isRequest 			= true;
		}else{
			mResponseMsg = (GuessResponseMessage)mMessage;
			isRequest 			= false;
		}

		/*  处理tag相关  */
		int msgType;
        final int msgId;
        int msgCreatTimeStamp;
        
        if(isRequest){
        	msgType 					= mRequestMsg.getType();
            msgId 							= mRequestMsg.getMsgId();
            msgCreatTimeStamp = mRequestMsg.getCreateTimestamp();
        }else{
        	msgType 					= mResponseMsg.getType();
            msgId 							= mResponseMsg.getMsgId();
            msgCreatTimeStamp = mResponseMsg.getCreateTimestamp();
        }

        if (tag == null) {
            tag = new ChatLogAdapter();
        }
        tag.mChatType 	= msgType;
        tag.mMsgId 		= msgId;
        
       headImgHandler(view);										// 处理头像
       setSendDate(view, msgCreatTimeStamp);			// 处理发送时间
       
        TextView title = (TextView) view.findViewById(R.id.cx_fa_view_chat_chatting_guess_for_partner_title);
        
        // 处理显示界面 
        if(isRequest){
	        	/*  对方发起的猜拳 (类型: guess_request） */
	        	layer1.setVisibility(View.VISIBLE);
				layer2.setVisibility(View.GONE);
				title.setText(R.string.cx_fa_guess_start_title);
				try {
					guess_id	= mRequestMsg.mData.getString("guess_id");
					value1 	= mRequestMsg.mData.getInt("value1");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
				TextView txt = (TextView) view.findViewById(R.id.cx_fa_view_chat_chatting_guess_for_partner_text);
				
				// 判断男女版 (1：老公  0:老婆 -1:未知    说明:除了1外,其它的都视为老公)
				if(CxGlobalParams.getInstance().getVersion() !=0 ){
					txt.setText(R.string.cx_fa_guess_start_info_wife);				//老公版
				}else{
					txt.setText(R.string.cx_fa_guess_start_info_husband);		//老婆版
				}
				
				// 是否已回复
				boolean is_reply = false;
				try {
						if(mRequestMsg.mData.has("is_reply")){
							is_reply = mRequestMsg.mData.getBoolean("is_reply");
						}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// 如果未回复，则增加点击回复
				if(!is_reply){
						// 显示动画
						gif.setImageResource(R.drawable.guess_gif);
				        AnimationDrawable anim = (AnimationDrawable) gif.getDrawable();
				        anim.stop();
				        anim.start();
					    
				        // 点击进行回拳
						layer1.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								try {
						        	guess_id = mRequestMsg.mData.getString("guess_id");
									value1		= mRequestMsg.mData.getInt("value1");
								} catch (JSONException e) {
									CxLog.e(TAG, "获取guess_id 和 value1 错误:" + e.getMessage());
								}
								ChatFragment.getInstance().showInputPanelGuessMode(msgId, guess_id, value1);		// 显示回拳面板
							}								
						});
				}else{
						//去掉可能存在的点击事件
						layer1.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
							}
						});
						// 如果已点击，则不再显示动画，显示已回拳的图片 
						gif.setImageResource(R.drawable.chat_guess_over);
				}
	        
        }else{
	        	/*  对方的回复 (类型: guess_response) */
	        	layer1.setVisibility(View.GONE);
				layer2.setVisibility(View.VISIBLE);
				title.setText(R.string.cx_fa_guess_result_title);
				
				//String sender = "";
	        	try {
		        	guess_id =  mResponseMsg.mData.getString("guess_id");
					value1		= mResponseMsg.mData.getInt("value1");
					value2		= mResponseMsg.mData.getInt("value2");
					result 		= mResponseMsg.mData.getInt("result");
					//sender		= mResponseMsg.mData.getString("sender");
				} catch (JSONException e) {
					CxLog.e(TAG, "获取json数据时出错:" + e.getMessage());
				}
	        	
	        	TextView winerInfo 		= (TextView) view.findViewById(R.id.cx_fa_view_chat_chatting_guess_for_partner_result);					// 显示谁赢了
	        	ImageView imgGuess1 	= (ImageView) view.findViewById(R.id.cx_fa_view_chat_chatting_guess_pic_for_partner_guess1);
	        	ImageView imgGuess2 	= (ImageView) view.findViewById(R.id.cx_fa_view_chat_chatting_guess_pic_for_partner_guess2);
	        	imgGuess1.setImageResource( fingerImg[value1-1] );
	        	imgGuess2.setImageResource( fingerImg[value2-1] );
	        	
	        	if(result==0){
						winerInfo.setText(R.string.cx_fa_guess_result_tie);		// 平局 
	        	}else{
		        		int wo = 0;			// 我
						int ta	= 0;			// 他
						
						// 判断男女版 (1：老公  0:老婆 -1:未知    说明:除了1外,其它的都视为老公)
						if(CxGlobalParams.getInstance().getVersion() !=0 ){
							wo 	= R.string.cx_fa_guess_result_win_wife;					// 软件是老公版，我是老婆
							ta		= R.string.cx_fa_guess_result_win_husband;			
						}else{
							wo 	= R.string.cx_fa_guess_result_win_husband;			// 软件是老婆版，我是老公
							ta		= R.string.cx_fa_guess_result_win_wife;
						}
						
		        		if(result==1){
		        			winerInfo.setText(ta);				// 他赢了!
		        		}else{
		        			winerInfo.setText(wo);			// 我赢了!
		        		}
	        	}
        }
        
        
       
        
        view.setTag(tag);
		return view;
	}
	
	/**
	 * 头像处理
	 * @param view
	 */
	private void headImgHandler(View view){
		 // 头像
        CxImageView icon = (CxImageView) view.findViewById(R.id.cx_fa_view_chat_chatting_guess_row_for_partner_icon);
        /*if(RkGlobalParams.getInstance().getPartnerIconBig()==null || RkGlobalParams.getInstance().getPartnerIconBig().equals("")){
        	icon.setImageResource(R.drawable.cx_fa_hb_icon_small);
        }else{
        	icon.setImage(RkGlobalParams.getInstance().getPartnerIconBig(), false, 44, mContext, "head", mContext);
        }*/
        icon.displayImage(ImageLoader.getInstance(), 
        		CxGlobalParams.getInstance().getPartnerIconBig(), 
        		CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
        		CxGlobalParams.getInstance().getMateSmallImgConner());

        icon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CxLog.i(TAG, "click update owner headimage button");
                //  跳转到个人资料页
				ChatFragment.getInstance().gotoOtherFragment("rkmate");
            }
        });
	}
	/**
	 * 处理发送时间
	 * @param view
	 * @param msgCreatTimeStamp
	 */
	private void setSendDate(View view, int msgCreatTimeStamp){
		// 设置消息时间
        TextView dateStamp = (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_for_partner_datestamp);
        TextView timeStamp = (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_for_partner_timestamp);
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
	}

}
