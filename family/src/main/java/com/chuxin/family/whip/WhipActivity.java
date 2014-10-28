package com.chuxin.family.whip;

import com.ant.liao.chuxin.EnhancedGifView;
import com.ant.liao.chuxin.GifView.GifImageType;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.views.chat.ChatFragment;
import com.chuxin.family.whip.ShakeListener.OnShakeListener;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.R;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 抽鞭子、弹脑壳
 * @author dujy
 *
 */
public class WhipActivity extends CxRootActivity{
	private String TAG  = "WhipActivity";
	private static final int  PUMPING_WHIP 	= 0;		// 抽鞭子 ,  老公版
	private static final int  BULLET_HEADS 		= 1;		// 弹脑壳，老婆版
	
    private Vibrator mVibrator = null;								  	// 震动
    private LinearLayout layer1,layer2;
    private EnhancedGifView gifView;
    
    private static MediaPlayer sVoicePlayer = null; 
    
    private int animation_id = 0, effect;
    private int soundResId, gifResId ;						// 要播放的声音资源ID、动画资源ID
    private boolean isPlaying = false;					// 是否在播放(声音和动画)，防止重复播放
    
    ImageView guideImg;		// 引导图片
    TextView guideText ;			// 引导文字
    
    ShakeListener mShakeListener = null;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cx_fa_view_chat_whip);

		// 判断男女 (0：老婆  1:老公 -1:未知    说明:除了0外,其它的都视为老公)
		if(0 == CxGlobalParams.getInstance().getVersion()){ //老婆
			animation_id 	= BULLET_HEADS;				// 弹脑壳 (老婆版)
		}else{ //老公
			animation_id 	= PUMPING_WHIP;			// 抽鞭子 (老公版)
		}
		
		mVibrator 				= (Vibrator)getSystemService(VIBRATOR_SERVICE);
		
		layer1 		= (LinearLayout)findViewById(R.id.cx_fa_view_chat_whip_layer1);
		layer2 		= (LinearLayout)findViewById(R.id.cx_fa_view_chat_whip_layer2);
		gifView 	= (EnhancedGifView)findViewById(R.id.cx_fa_view_chat_whip_gif);
	
		// 判断是抽鞭子还是弹脑壳，来显示不同的界面
		guideImg 	= (ImageView)layer1.findViewById(R.id.rk_ba_view_chat_whip_guide_pic);
		guideText	= (TextView)layer1.findViewById(R.id.rk_ba_view_chat_whip_guide_txt);
		
		if(animation_id==PUMPING_WHIP){					// 抽鞭子
			guideImg.setImageResource(R.drawable.whip_guide_whip);
			guideText.setText(R.string.cx_fa_animation_guide_text_0);
			
			soundResId 	= R.raw.whip_pumping_whip;
			gifResId			= R.drawable.whip_gif_whip;
		}else if(animation_id==BULLET_HEADS){		// 弹脑壳
			guideImg.setImageResource(R.drawable.whip_guide_head);
			guideText.setText(R.string.cx_fa_animation_guide_text_1);
			
			soundResId 	= R.raw.whip_bullet_head;
			gifResId			= R.drawable.whip_gif_head;
		}
		
	        
		Button cancelBtn = (Button)findViewById(R.id.cx_fa_view_chat_whip_cancel);
		cancelBtn.setOnClickListener(btnListener);
		
		// 弹脑壳时，允许用户点击图片解决
		if(animation_id==BULLET_HEADS){
			guideImg.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					 playGifAndSound();		// 播放动画和声音				
				}
			});
		}else{
			mShakeListener = new ShakeListener(this);
			mShakeListener.setOnShakeListener(new shakeLitener());
		}
		
		// 设置点击对话框外，不结束activity	（API Level>=11 有效）
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			  WhipActivity.this.setFinishOnTouchOutside(false);
		}
		
	}
	
	
	private class shakeLitener implements OnShakeListener{
		  @Override
		  public void onShake() {
			  mVibrator.vibrate(300);
			  playGifAndSound();		// 播放动画和声音	
			  mShakeListener.stop();
		  }		  
  }
	
	
View.OnClickListener btnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.cx_fa_view_chat_whip_cancel:
					WhipActivity.this.finish();
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 播放动画和声音
	 */
	private void playGifAndSound(){
		if(isPlaying)
			return;
		
		isPlaying = true;
		
		// 播放动画
		gifView.setGifImageType(GifImageType.COVER);
		gifView.setGifImage(gifResId);
		gifView.showCover();
		layer1.setVisibility(View.GONE);
		layer2.setVisibility(View.VISIBLE);
		
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
			sVoicePlayer = MediaPlayer.create(WhipActivity.this.getApplicationContext(),soundResId);
			sVoicePlayer.setLooping(false);
 		
			
			sVoicePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
				 @Override  
	             public void onCompletion(MediaPlayer mp) {  
						 sVoicePlayer.release();
						 sVoicePlayer = null;
						 if(playSoundTimes<maxTimes-1){
							 playSoundTimes = playSoundTimes +1;
							 playSound();				// 默认重复播放
				 		}else{
				 			/* 动画和声音播放完成后，要去做的事 */				 		
							Handler handler = new Handler(){   
				 		        public void handleMessage(Message msg) {  
				 		        	super.handleMessage(msg);  
				 		        	effect = animation_id;
				 		        	String msg2 = animation_id + "," + effect;
				 		        	ChatFragment.getInstance().sendMessage(msg2, com.chuxin.family.models.Message.MESSAGE_TYPE_ANIMATION);
				 		        }
				 		    };
				 			handler.sendEmptyMessage(0);		
				 			
							WhipActivity.this.finish();		// 关闭本窗口
							
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

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 释放资源
		if(sVoicePlayer!=null){
			sVoicePlayer.release();
		}
		sVoicePlayer = null;
		gifView.destroy();
	}
	
}
