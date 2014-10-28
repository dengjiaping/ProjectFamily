package com.chuxin.family.views.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.chuxin.family.models.Message;
import com.chuxin.family.models.TabloidMessage;
import com.chuxin.family.models.TextMessage;
import com.chuxin.family.net.CxTabloidApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.tabloid.CxTabloidActivity;
import com.chuxin.family.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 我家小报类型
 * @author dujy
 *
 */
public class PartnerTabloidEntry  extends ChatLogEntry{
	private static final String TAG = "TabloidEntry";

	private String mMsgText;
	
	public PartnerTabloidEntry(Message message, Context context, boolean isShowDate) {
		super(message, context, isShowDate);
	}

	@Override
	public boolean isOwner() {
		return false;
	}

	@Override
	public int getType() {
		return ENTRY_TYPE_PEER_TABLOID;
	}


	@Override
	public View build(View view, ViewGroup parent) {
		ChatLogAdapter tag = null;
		
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.cx_fa_view_chat_chatting_tabloid_row_for_partner,	null);
		}
//		else{
//			return view;
//		}
		
		
		
		TabloidMessage message = (TabloidMessage)mMessage;
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
        
        // 设置消息时间
        TextView timeStamp = (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_tabloid_row_timestamp);
        TextView dateStamp = (TextView)view.findViewById(R.id.cx_fa_view_chat_chatting_tabloid_row_datestamp);
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
		
		// 设置按钮
		ImageView imgBtn = (ImageView)view.findViewById(R.id.cx_fa_view_chat_tabloid_row_set);
		imgBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, CxTabloidActivity.class);
				ChatFragment.getInstance().getActivity().startActivity(intent);
			}
			
		});
				
		JSONArray arr = null;
		try {
			arr = new JSONArray(mMsgText);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 放多个笑话的容器
		LinearLayout tabloidList = (LinearLayout)view.findViewById(R.id.cx_fa_view_chat_chatting_tabloid_row_tabloidList);
		tabloidList.removeAllViews();
		// 取出所有笑话, 显示在页面上
		for(int i=0; i<arr.length(); i++){
			JSONObject jsonObj = null;
			String title 			= "";
			String text 			= "";
			int category_id 	= -1;
			int tabloid_id		= -1;
			try {
				jsonObj 			= (JSONObject)arr.get(i);
				title 					= jsonObj.getString("title");
				text 					= jsonObj.getString("text");
				category_id 	= jsonObj.getInt("category_id");
				tabloid_id 		= jsonObj.getInt("id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			View tmpView 		= LayoutInflater.from(mContext).inflate(R.layout.cx_fa_view_chat_chatting_tabloid_row_for_partner_list_item, null);
			tmpView.setSelected(false);
			TextView titleView	= (TextView) tmpView.findViewById(R.id.cx_fa_view_chat_tabloid_row_item_title);
			TextView textView	= (TextView) tmpView.findViewById(R.id.cx_fa_view_chat_tabloid_row_item_text);
			
			TextView forward 	= (TextView)tmpView.findViewById(R.id.cx_fa_view_chat_tabloid_row_item_forward);
			forward.setText(CxResourceString.getInstance().str_taboloid_forward);
			
			titleView.setText(title );
			textView.setText(text);

			// 如果是最后一条信息，则将灰线隐藏
			if(i==arr.length()-1){
				ImageView grayLine = (ImageView)tmpView.findViewById(R.id.cx_fa_view_chat_tabloid_row_item_grayline);
				grayLine.setVisibility(View.GONE);
			}
			
			final String tmpStr = text;
			final int cateId		= category_id;
			final int tid				= tabloid_id;
			// 转发小报
			forward.setOnClickListener(new OnClickListener(){
				
				@Override
				public void onClick(View v) {
					String forwardPre 	=  v.getResources().getString(CxResourceString.getInstance().str_taboloid_forward_pre);;
					String forwardStr 	=  forwardPre + tmpStr;
					
					// 给对方发一条文本消息
					ChatFragment chatFragment = ChatFragment.getInstance();
					chatFragment.sendMessage(forwardStr, 0);
					
					// 发一个统计请求
					CxTabloidApi tabloidApi = new CxTabloidApi();
					tabloidApi.forward(forwardCaller, cateId, tid);
					
				}
				
			});

			tabloidList.addView(tmpView);
		}
		
		view.setTag(tag);
		return view;
	}

	// 发送转发统计后的回调，暂时什么也不做
	public JSONCaller forwardCaller = new JSONCaller(){
		@Override
		public int call(Object result) {
			return 0;
		}
	};
	
}
