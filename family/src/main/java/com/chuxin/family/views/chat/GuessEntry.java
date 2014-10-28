package com.chuxin.family.views.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.models.GuessRequestMessage;
import com.chuxin.family.models.GuessResponseMessage;
import com.chuxin.family.models.Message;
import com.chuxin.family.models.Model;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GuessEntry extends ChatLogEntry{
	private String TAG = "GuessEntry";
	private GuessRequestMessage mRequestMsg ;
	private GuessResponseMessage mResponseMsg ;
	private int[] fingerImg = new int[]{R.drawable.chat_btnguess_scissors, R.drawable.chat_btnguess_rock, R.drawable.chat_btnguess_paper};		// 猜拳的三种图片
	
	int msgId;				// 信息ID (定义在这，是因为重发时要使用)
		
	String guess_id;		// 猜拳id
    int value1;				// 自己出的拳
    
    boolean isRequest;		// 是否是guesss_request类型的消息
	
	public GuessEntry(Message message, Context context, boolean isShowDate) {
		super(message, context, isShowDate);
	}

	@Override
	public boolean isOwner() {
		return true;
	}

	@Override
	public int getType() {
		return ENTRY_TYPE_GUESS_REQUEST;
	}

	@Override
	public View build(View view, ViewGroup parent) {
			ChatLogAdapter tag = null;
			
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(R.layout.cx_fa_view_chat_chatting_guess_row,	null);
			}
			
			LinearLayout layer1 = (LinearLayout)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_row__layer1);
			LinearLayout layer2 = (LinearLayout)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_row__layer2);
			
			// 判断消息类型
			if(mMessage instanceof GuessRequestMessage){
				mRequestMsg 	= (GuessRequestMessage)mMessage;
				isRequest 			= true;
			}else if(mMessage instanceof GuessResponseMessage){
				mResponseMsg = (GuessResponseMessage)mMessage;
				isRequest			= false;
			}
			
			// 处理Tag相关
			int msgType ;
	        int msgCreatTimeStamp ;
	
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
	        
	        
	        // 如果是自己发的，可能发送失败，需要重发。
	        if(isRequest){
	        	reSendHandler(view, mRequestMsg, Message.MESSAGE_TYPE_GUESS_REQUEST);		// 发起猜拳的消息重发
	        }else{
	        	reSendHandler(view, mResponseMsg, Message.MESSAGE_TYPE_GUESS_RESPONSE);		// 回拳的消息重发
	        }
	        
	        headImgHandler(view);										// 处理头像
	        setSendDate(view, msgCreatTimeStamp);		 // 处理显示时间
	       
	        /* 处理显示界面 */
	        TextView title = (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_row_title);
	        if(isRequest){	 
	        	// 自己发起的猜拳 (类型: guess_request）
	        	layer1.setVisibility(View.VISIBLE);
				layer2.setVisibility(View.GONE);
				
				title.setText(R.string.cx_fa_guess_start_title);				// 设置标题
				
				try {
		        	guess_id	= mRequestMsg.mData.getString("guess_id");
		        	value1		= mRequestMsg.mData.getInt("value1");
				} catch (JSONException e) {
					CxLog.e(TAG, "获取guess_id 和 value1 错误:" + e.getMessage());
				}
				
				// 设置提示文字
				TextView txt 		= (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_row_text);
				
				// 判断男女版 (1：老公  0:老婆 -1:未知    说明:除了1外,其它的都视为老公)
				if(CxGlobalParams.getInstance().getVersion() !=0 ){
					txt.setText(R.string.cx_fa_guess_start_info_husband);		//老公版
				}else{
					txt.setText(R.string.cx_fa_guess_start_info_wife);				//老婆版
				}
	        }else{
	        	// 自己的回复 (类型: guess_response)
				layer1.setVisibility(View.GONE);
				layer2.setVisibility(View.VISIBLE);
				title.setText(R.string.cx_fa_guess_result_title);		// 设置标题

				int result  		= 0;			// 比赛结果
				int value2 		= 0;	
				int send_success = 0;		// 发送结果
				//String sender 	= "";
				try {
		        	guess_id	= mResponseMsg.mData.getString("guess_id");
		        	value1		= mResponseMsg.mData.getInt("value1");
		        	value2		= mResponseMsg.mData.getInt("value2");
		        	result		= mResponseMsg.mData.getInt("result");
		        	send_success = mResponseMsg.mData.getInt("send_success");
		        	//sender		= mResponseMsg.mData.getString("sender");
				} catch (JSONException e) {
					CxLog.e(TAG, "获取guess_id 和 value1 错误:" + e.getMessage());
				}
				
				TextView winerInfo 								= (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_row_result);			// 显示谁赢了
				LinearLayout   guessResultPicLayout 	= (LinearLayout) view.findViewById(R.id.cx_fa_view_chat_chatting_guess_result_pic_layout);		// 显示对决图片的容器
				
				if(send_success==0){					// 发送中
					winerInfo.setText(R.string.cx_fa_guess_waiting_result);
					guessResultPicLayout.setVisibility(View.GONE);
				}else if(send_success==2){		// 发送失败
						winerInfo.setText(R.string.cx_fa_guess_reply_fail);
						guessResultPicLayout.setVisibility(View.GONE);
				}else{											// 发送成功
						guessResultPicLayout.setVisibility(View.VISIBLE);
						ImageView imgGuess1 	= (ImageView) view.findViewById(R.id.cx_fa_view_chat_chatting_guess_pic_guess1);
			        	ImageView imgGuess2 	= (ImageView) view.findViewById(R.id.cx_fa_view_chat_chatting_guess_pic_guess2);
			        	imgGuess1.setImageResource( fingerImg[value2-1] );
			        	imgGuess2.setImageResource( fingerImg[value1-1] );
						
						if(result==0){
							winerInfo.setText(R.string.cx_fa_guess_result_tie);		// 平局 
						}else {
							int wo = 0;			// 我
							int ta	= 0;			// 他
							
							// 判断男女版 (1:老公  0:老婆 -1:未知    说明:除了1外,其它的都视为老公)
							if(CxGlobalParams.getInstance().getVersion() !=0 ){
								wo 	= R.string.cx_fa_guess_result_win_wife;					// 软件是老公版，我是老婆
								ta		= R.string.cx_fa_guess_result_win_husband;
							}else{
								wo 	= R.string.cx_fa_guess_result_win_husband;			// 软件是老公婆，我是老公
								ta		= R.string.cx_fa_guess_result_win_wife;
							}
							
			        		if(result==1){
			        			winerInfo.setText(wo);					// 我赢了
			        		}else{
			        			winerInfo.setText(ta);					// 他赢了
			        		}
						}
				}
				
	        }
	        
	       // 显示动画
	       ImageView gif = (ImageView)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_row_pic);
	       AnimationDrawable anim = (AnimationDrawable) gif.getDrawable();
	       anim.stop();
	       anim.start();
	        
	        view.setTag(tag);
			return view;
	}
	
	/**
	 * 处理头像
	 * @param view
	 */
	private void headImgHandler(View view){
			 // 头像
	        CxImageView icon = (CxImageView) view.findViewById(R.id.cx_fa_view_chat_chatting_guess_row_icon);
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
	}
	private void reSendHandler( View view, final Model model, final int msgType){
		 // 是否发送成功（如果发送成功，就把小红点隐藏）
        final ImageButton reSendBtn = (ImageButton)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_row__exclamation);
        ProgressBar pb 							= (ProgressBar) view.findViewById(R.id.cx_fa_view_chat_chatting_guess_row_circleProgressBar);		// 发送中的图标
        if(mMessage.getSendSuccess()==0){			// 发送中
	        	 pb.setVisibility(View.VISIBLE);
	        	 reSendBtn.setVisibility(View.GONE);
        }else{									// 发送完成
	        	pb.setVisibility(View.GONE);
	        	
	        	if(mMessage.getSendSuccess()==1){		// 发送成功
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
			                                     if(isRequest){
					                                     String msg = guess_id + "," + value1;
					                                     ChatFragment.getInstance().reSendMessage( msg,  msgType, msgId);
			                                     }else{
			                                    	 	int value2 = 0;
			                                    	 	int result = 0;
			                                    	 	int oldMsgId = 0;
			                                    	 	try{
			                                    	 		value2 = model.mData.getInt("value2");
			                                    	 		result 	= model.mData.getInt("result");
			                                    	 		oldMsgId = model.mData.getInt("request_msg_id");			// 只要回拳失败时，才会有此属性
			                                    	 	}catch(Exception e){
			                                    	 		e.printStackTrace();
			                                    	 	}
				                                    	// 回拳时，
				                     					String msg2 = oldMsgId + "," + guess_id  + "," + value1 +  "," + value2 +  "," + result ;
				                     					ChatFragment.getInstance().reSendMessage( msg2,  msgType, msgId);
			                                     }
			                                 }
			                             }).setNegativeButton(R.string.cx_fa_cancel_button_text, null).show();
			                 }
			             });
	            	}
        	}
	}
	
	/**
	 * 处理发送时间
	 * @param view
	 * @param creatTimeStamp
	 */
	private void setSendDate(View view , long creatTimeStamp){
		// 设置消息时间
        TextView dateStamp 	= (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_row_datestamp);
        TextView timeStamp 	= (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_guess_row_timestamp);
        String format 				= view.getResources().getString(R.string.cx_fa_nls_reminder_period_time_format);
        Calendar calendar 		= Calendar.getInstance();
        calendar.setTime(new Date((long)(creatTimeStamp) * 1000));
        String time = String.format(format, calendar);
        timeStamp.setText(time);
        
        if (mIsShowDate) {
        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        	String dateNow = dateFormat.format(new Date((long)(creatTimeStamp) * 1000));
            dateStamp.setVisibility(View.VISIBLE);
            dateStamp.setText(dateNow);
        } else {
            dateStamp.setVisibility(View.GONE);
        }
	}

}
