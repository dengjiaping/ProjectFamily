package com.chuxin.family.views.chat;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ant.liao.chuxin.EnhancedGifView;
import com.chuxin.family.neighbour.DialogListener;
import com.chuxin.family.widgets.CxInputPanel;
import com.chuxin.family.R;

import org.fmod.effects.RkSoundEffects;

public class ChatSoundEffectDialog extends Dialog{

	private static final String TAG = "ChatSoundEffectDialog";
	private EnhancedGifView mSoundEffectImageView;
	private int mEffect;
	private DialogListener mDialogListener;
	public ChatSoundEffectDialog(Context context, int effect, int theme, DialogListener dl) {
		super(context, theme);
		this.mEffect = effect;
		this.mDialogListener = dl;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_dialog_chat_sound_effect);
		mSoundEffectImageView = (EnhancedGifView)findViewById(R.id.cx_fa_chat_sound_effect_gift_imageview);
        switch(mEffect){
            case CxInputPanel.RKDSP_EFFECT_YUANSHENG:
                mSoundEffectImageView.setGifImage(R.drawable.sound_h_zhuangyou);
                break;
            case CxInputPanel.RKDSP_EFFECT_HANHAN:
                mSoundEffectImageView.setGifImage(R.drawable.sound_w_zhuanghan);
                break;
            case CxInputPanel.RKDSP_EFFECT_YOUYOU:
                mSoundEffectImageView.setGifImage(R.drawable.sound_h_zhuangyou);
                break;
            case CxInputPanel.RKDSP_EFFECT_HUAIHUAI:
                mSoundEffectImageView.setGifImage(R.drawable.sound_w_zhuanghuai);
                break;
            case CxInputPanel.RKDSP_EFFECT_SHASHA:
                mSoundEffectImageView.setGifImage(R.drawable.sound_h_zhuangsha);
                break;
    }
		
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
		mDialogListener.refreshUiAndData();
		dismiss();
//		android.os.Message message = RecordEntry.recordHandler.obtainMessage(0);
//        message.sendToTarget();
//		RkSoundEffects.cFmodStop();
		return false;
	}
	
}
