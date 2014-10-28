package com.chuxin.family.views.chat;

import com.chuxin.family.models.Message;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.ScrollableListView;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatLogAdapter extends BaseAdapter {

	private static final String TAG = "ChatLogAdapter";
	private Cursor mCursor = null;
	public int mChatType;
	public int mMsgId;
	public boolean isPicture;
	
	public String mMsgText;
	private Context mContext;

	public String mImagePath;
	public String mVoiceUrl;

	public void updateDB(Cursor cursor){
		mCursor = cursor;
	}

	public void updateMessages(final Cursor cursor,
			final ScrollableListView listView, Context context,
			ChatFragment chatFragment, final int pos) {
		mContext = context;
		
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				int originalCount = 0;
				if (mCursor != null) {
					originalCount = mCursor.getCount();
//					mCursor.close();
				}
				
				mCursor = cursor;
				int offset = 0;
				if(null == mCursor){
				    return;
				}
				if (mCursor.getCount() > originalCount) {
					offset = (mCursor.getCount() - originalCount) + listView.getFirstVisiblePosition();
				} else {
					offset = -1;
				}
				
				final int originalPos = offset;
						
				CxLog.d(TAG, "cursor" + mCursor);
				notifyDataSetChanged();
				listView.onRefreshComplete();
				
				listView.post(new Runnable() {
					@Override
					public void run() {
						int scrollTo = pos;
						if (scrollTo >= mCursor.getCount())
							scrollTo = -1;
						
						if (scrollTo < 0) {
							listView.smoothScrollToPosition(mCursor.getCount());
						} else if (originalPos == -1) {
							;
						} else {
//							listView.smoothScrollToPosition(originalPos);
						}
					}
				});
			}
		}, 1);
	}

	@Override
	public int getItemViewType(int position) {
		mCursor.moveToPosition(position);
		String value = mCursor.getString(2);
		Message message = null;
		try {
			message = Message.buildMessageObject(new JSONObject(value));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		ChatLogEntry entry = ChatLogEntry.buildLogEntry(message, mContext, false);
		return entry.getType();
	}

	@Override
	public int getViewTypeCount() {
		return ChatLogEntry.ENTRY_TYPE_MAX;
	}

	@Override
	public int getCount() {
		if (mCursor == null)
			return 0;
		return mCursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		mCursor.moveToPosition(position);
		String value = mCursor.getString(2);
//		RkLog.i(TAG, "value=" + value);
		Message message = null;
		boolean isShowDate = false;
		try {
			message = Message.buildMessageObject(new JSONObject(value));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		CxLog.i(TAG, "message sender" + message.getSender() + " : id"
				+ message.mId + " type=" + message.getType());

	      if(position > 0){
	            mCursor.moveToPosition(position-1);
	            String  previousValue = mCursor.getString(2);
	            Message previousMessage = null;
	            try {
                    previousMessage = Message.buildMessageObject(new JSONObject(previousValue));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//	            RkLog.i(TAG, "message sender" + previousMessage.getSender() + " : id"
//	                    + previousMessage.mId + " type=" + previousMessage.getType());
	            
	            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
	            String dateNow = dateFormat.format(new Date((long) (message
	                    .getCreateTimestamp()) * 1000));
	            String datePrevious = dateFormat.format(new Date((long) (previousMessage
                        .getCreateTimestamp()) * 1000));
	            
	            if (!dateNow.equals(datePrevious)) {
	                isShowDate = true;
	            } else {
	                isShowDate = false;
	            }
	            
	        } else {
	            isShowDate = true;
	        }
		ChatLogEntry entry = ChatLogEntry.buildLogEntry(message, parent.getContext(), isShowDate);
		return entry.build(convertView, parent);
	}

}
