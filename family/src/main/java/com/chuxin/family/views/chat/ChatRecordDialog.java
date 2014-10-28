package com.chuxin.family.views.chat;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuxin.family.R;

public class ChatRecordDialog extends Dialog{

	private static final String TAG = "ChatRecordDialog";
	private ImageView mRecordImageView;
	private TextView mRecordRemainTimeTextView;
	private String mRemainingTime;
	
	public ChatRecordDialog(Context context, String time) {
		super(context);
		this.mRemainingTime = time;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_dialog_chat_record);
		mRecordImageView = (ImageView)findViewById(R.id.cx_fa_dialog_chat_record_imageview);
		mRecordRemainTimeTextView = (TextView) findViewById(R.id.cx_fa_dialog_chat_record_recordtime_textview);
		mRecordRemainTimeTextView.setText(mRemainingTime);
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_UP:
			dismiss();
			break;
		}
		dismiss();
		return false;
	}
	
}
