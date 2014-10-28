package com.chuxin.family.widgets;

import java.util.Calendar;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.R;

public class VoiceTip {

	public static final int VOICE_TIP_MODE_VIBRATE = 1;
	public static final int VOICE_TIP_MODE_VOICE = 2;
	
	private static MediaPlayer sVoicePlayer = null; 
	private static long sTipBegin = 0;

	private static final int TIP_STYLE_1_DURATION = 182;
	private static final int TIP_STYLE_1_VOICE_ID = R.raw.tropfen;
	
	private static void voiceTip(Context context) {
		if (sVoicePlayer == null) {
			sVoicePlayer = MediaPlayer.create(context, TIP_STYLE_1_VOICE_ID);
			sVoicePlayer.setLooping(false);
		}

		try { 
			if (!sVoicePlayer.isPlaying())
				sVoicePlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
			
			if (sVoicePlayer != null) {
				sVoicePlayer.release();
				sVoicePlayer = null;
			}
		}
	}
	
	private static void vibrateTip(Context context) {
		try { 
			Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);  
	        vibrator.vibrate(TIP_STYLE_1_DURATION);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void tip(Context context, int flags) {
		long now = Calendar.getInstance().getTimeInMillis();
		if ((now - sTipBegin) < TIP_STYLE_1_DURATION)
			return;
		
		boolean allowVoice = CxGlobalParams.getInstance().isChatSound();
		boolean allowVibrate = CxGlobalParams.getInstance().isChatShock();
		
		sTipBegin = now;
		context = context.getApplicationContext();
		if ((flags & VOICE_TIP_MODE_VIBRATE) == VOICE_TIP_MODE_VIBRATE) {
			if (allowVibrate) 
				vibrateTip(context);
		}
		if ((flags & VOICE_TIP_MODE_VOICE) == VOICE_TIP_MODE_VOICE) {
			if (allowVoice)
			voiceTip(context);
		}
	}
}
