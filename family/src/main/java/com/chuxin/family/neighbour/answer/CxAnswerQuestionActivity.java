package com.chuxin.family.neighbour.answer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuxin.family.accounting.CxAccountDetailActivity;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.net.CxAnswerApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxAnswerQuestionData;
import com.chuxin.family.parse.been.CxAnswerResultData;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.data.AnswerQuestionItem;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.R;

/**
 * 谁家最聪明  答题
 * @author wentong.men
 *
 */
public class CxAnswerQuestionActivity extends CxRootActivity {

	
	protected static final int NET_REFRESH = 0;
	protected static final int SHOW_TIPS = 1;
	protected static final int COUNT_DOWN = 2;
	private static final int CONTINUE = 3;
	private LinearLayout aLayout1;
	private LinearLayout aLayout2;
	private LinearLayout bLayout1;
	private LinearLayout bLayout2;
	private LinearLayout cLayout1;
	private LinearLayout cLayout2;
	private LinearLayout dLayout1;
	private LinearLayout dLayout2;
	private TextView aText;
	private TextView bText;
	private TextView cText;
	private TextView dText;
	private ImageView aImg;
	private ImageView bImg;
	private ImageView cImg;
	private ImageView dImg;
	private TextView topicText;
	private TextView timeText;
	private TextView numberText;
	
	private Handler questionHandler;
	
	private CxAnswerQuestionData questionData; //数据
	
	private String rightResult; //正确的答案
	
	private String seletedResult="";//选中的答案
	
	private Timer timer;
	
	private TimerTask timerTask;
	
	private int countDown=0; //倒计时15秒
	
	private  boolean isClose=false; //判断提交答案时的状态是否还需要拉下一题
	
	private int remain; //剩余题数
	
	private boolean isSend=false; //答案是否已经发送
	
	private boolean isFirst=true;//是否第一次进来
	
	private boolean isBack=false;
	
	private  boolean isOver=false;

	@Override
	protected void onCreate(Bundle arg0) {		
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_activity_neighbour_answer_questions);
		
		
		
		questionHandler=new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch(msg.what){
				case NET_REFRESH:
					nextText.setVisibility(View.GONE);
					timeLayout.setVisibility(View.VISIBLE);
					rightLayout.setVisibility(View.GONE);
//					Animation shake = AnimationUtils.loadAnimation(RkAnswerQuestionActivity.this, R.anim.shake);
//					frame.startAnimation(shake);	
					netRefresh();				
					break;
				case SHOW_TIPS:
					if(!isBack){
						showHalfDialog();
					}
					break;
				case COUNT_DOWN:	
					
					countDown--;
					CxLog.i("men", countDown+"");
					if(countDown<0){
						return;
					}
					timeText.setText(countDown+"");
					
					if(countDown==0){
						if(remain==0){
							if(!isBack){
								showZoreDialog();
							}
						}else{
							commonOnClick();
						}						
					}					
					break;				
				case CONTINUE:
					
					if(remain==0){
						CxLog.i("men", remain+">>>1");
						if(!isBack){
							showZoreDialog();
						}
					}else{
						if(!isOver){
							CxLog.i("men", remain+">>>>2");
							next();
						}
					}
					break;
				default:
					break;
				}
			}
		};
		
		initTitle();
		
		init();
	}



	protected void showHalfDialog() {
		View inflate = View.inflate(this, R.layout.cx_fa_widget_neighbour_answer_question_dialog, null);
		TextView contentText = (TextView) inflate.findViewById(R.id.cx_fa_answer_question_dialog_text_tv);
		Button yesBtn = (Button) inflate.findViewById(R.id.cx_fa_answer_question_dialog_yes_btn);
		Button noBtn = (Button) inflate.findViewById(R.id.cx_fa_answer_question_dialog_no_btn);
		contentText.setText(CxResourceString.getInstance().str_answer_question_half_dialog_content);
		yesBtn.setText(getString(R.string.cx_fa_answer_question_half_dialog_cancel));
		noBtn.setText(getString(R.string.cx_fa_answer_question_half_dialog_sure));
		
		halfDialog = new Dialog(this, R.style.simple_dialog);		
		halfDialog.setContentView(inflate);	
		halfDialog.setCancelable(false);
		yesBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				halfDialog.dismiss();
				CxAnswerApi.getInstance().requestRemind(remindCaller);	
				finish();
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			}
		});
		noBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				halfDialog.dismiss();
				CxAnswerApi.getInstance().requestAnswerQuestion(questionCaller);
			}
		});
		halfDialog.show();
		
	}



	protected void showZoreDialog() {
		
		View inflate = View.inflate(this, R.layout.cx_fa_widget_neighbour_answer_question_dialog, null);
		TextView contentText = (TextView) inflate.findViewById(R.id.cx_fa_answer_question_dialog_text_tv);
		Button yesBtn = (Button) inflate.findViewById(R.id.cx_fa_answer_question_dialog_yes_btn);
		Button noBtn = (Button) inflate.findViewById(R.id.cx_fa_answer_question_dialog_no_btn);
		contentText.setText(getString(R.string.cx_fa_answer_home_today_remain_over));
		yesBtn.setText(getString(R.string.cx_fa_answer_question_zero_dialog_cancel));
		noBtn.setVisibility(View.GONE);
		
		zoreDialog = new Dialog(this, R.style.simple_dialog);		
		zoreDialog.setContentView(inflate);	
		zoreDialog.setCancelable(false);
		yesBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				zoreDialog.dismiss();
				back();			
			}
		});
		zoreDialog.show();
		
		
	}

	protected void netRefresh() {
			
//		Animation out = AnimationUtils.loadAnimation(RkAnswerQuestionActivity.this, R.anim.tran_next_out);
//		final Animation in = AnimationUtils.loadAnimation(RkAnswerQuestionActivity.this, R.anim.tran_next_in);

//		AnimationListener listener=new AnimationListener() {			
//			@Override
//			public void onAnimationStart(Animation animation) {	}			
//			@Override
//			public void onAnimationRepeat(Animation animation) {}		
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				frame.setAnimation(in);	
//			}
//		};
//		out.setAnimationListener(listener);
//		if(!isFirst){		
//			frame.setAnimation(out);
//		}else{
//			isFirst=false;
//		}
		
		
		
		
		remain = questionData.getToday_remain();		
		numberText.setText(remain+"");
		topicText.setText("    "+questionData.getQuestion());
		ArrayList<AnswerQuestionItem> items = questionData.getItems();
		int size = items.size();
		if(size==1){
			aLayout1.setVisibility(View.VISIBLE);
			bLayout1.setVisibility(View.GONE);
			cLayout1.setVisibility(View.GONE);
			dLayout1.setVisibility(View.GONE);
		}else if(size==2){
			aLayout1.setVisibility(View.VISIBLE);
			bLayout1.setVisibility(View.VISIBLE);
			cLayout1.setVisibility(View.GONE);
			dLayout1.setVisibility(View.GONE);
		}else if(size==3){
			aLayout1.setVisibility(View.VISIBLE);
			bLayout1.setVisibility(View.VISIBLE);
			cLayout1.setVisibility(View.VISIBLE);
			dLayout1.setVisibility(View.GONE);
		}else {
			aLayout1.setVisibility(View.VISIBLE);
			bLayout1.setVisibility(View.VISIBLE);
			cLayout1.setVisibility(View.VISIBLE);
			dLayout1.setVisibility(View.VISIBLE);
		}
		
		aImg.setVisibility(View.GONE);
		bImg.setVisibility(View.GONE);
		cImg.setVisibility(View.GONE);
		dImg.setVisibility(View.GONE);
		
		aLayout2.setBackgroundResource(R.drawable.talent_btn_answer);
		bLayout2.setBackgroundResource(R.drawable.talent_btn_answer);
		cLayout2.setBackgroundResource(R.drawable.talent_btn_answer);
		dLayout2.setBackgroundResource(R.drawable.talent_btn_answer);
		
		
		for(int i=0;i<size;i++){
			AnswerQuestionItem item = items.get(i);
			if(i==0){
				aText.setText(item.getText());
			}else if(i==1){
				bText.setText(item.getText());
			}else if(i==2){
				cText.setText(item.getText());
			}else if(i==3){
				dText.setText(item.getText());
			}
		}
		countDown=15;
		timeText.setText(countDown+"");
		
		setResultClickable(true);
		
	}



	private void initTitle() {
		
		frame = findViewById(R.id.cx_fa_answer_question_frame_layout);
		
		timeLayout = (LinearLayout) findViewById(R.id.cx_fa_answer_question_title_time_layout);
		nextText = (TextView) findViewById(R.id.cx_fa_answer_question_title_next_question_tv);
		
		timeText = (TextView) findViewById(R.id.cx_fa_answer_question_title_time_tv);
		numberText = (TextView) findViewById(R.id.cx_fa_answer_question_title_number_tv);
		ImageView closeImg = (ImageView) findViewById(R.id.cx_fa_answer_question_title_close_iv);
		nextText.setVisibility(View.GONE);
		timeLayout.setVisibility(View.VISIBLE);
		
		closeImg.setOnClickListener(clickListener);
	}
	
	private void init() {
		
		seletedResult="";
		
		topicText = (TextView) findViewById(R.id.cx_fa_answer_question_topic_tv);
		
		aLayout1 = (LinearLayout) findViewById(R.id.cx_fa_answer_question_result_a_layout_1);
		aLayout2 = (LinearLayout) findViewById(R.id.cx_fa_answer_question_result_a_layout_2);
		bLayout1 = (LinearLayout) findViewById(R.id.cx_fa_answer_question_result_b_layout_1);
		bLayout2 = (LinearLayout) findViewById(R.id.cx_fa_answer_question_result_b_layout_2);
		cLayout1 = (LinearLayout) findViewById(R.id.cx_fa_answer_question_result_c_layout_1);
		cLayout2 = (LinearLayout) findViewById(R.id.cx_fa_answer_question_result_c_layout_2);
		dLayout1 = (LinearLayout) findViewById(R.id.cx_fa_answer_question_result_d_layout_1);
		dLayout2 = (LinearLayout) findViewById(R.id.cx_fa_answer_question_result_d_layout_2);
		
		
		aText = (TextView) findViewById(R.id.cx_fa_answer_question_result_a_tv);
		bText = (TextView) findViewById(R.id.cx_fa_answer_question_result_b_tv);
		cText = (TextView) findViewById(R.id.cx_fa_answer_question_result_c_tv);
		dText = (TextView) findViewById(R.id.cx_fa_answer_question_result_d_tv);
		
		aImg = (ImageView) findViewById(R.id.cx_fa_answer_question_result_a_iv);
		bImg = (ImageView) findViewById(R.id.cx_fa_answer_question_result_b_iv);
		cImg = (ImageView) findViewById(R.id.cx_fa_answer_question_result_c_iv);
		dImg = (ImageView) findViewById(R.id.cx_fa_answer_question_result_d_iv);
		
		rightLayout = (FrameLayout) findViewById(R.id.cx_fa_answer_question_result_right_layout);
		shineImg = (ImageView) findViewById(R.id.cx_fa_answer_question_shine_iv);
		rightLayout.setVisibility(View.GONE);
		
		aLayout2.setOnClickListener(clickListener);
		bLayout2.setOnClickListener(clickListener);
		cLayout2.setOnClickListener(clickListener);
		dLayout2.setOnClickListener(clickListener);
		
		
//		mediaPlayer = MediaPlayer.create(this, R.raw.answer_background_music);
//		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);		
//		mediaPlayer.setLooping(true);
//		try {
//			mediaPlayer.prepare();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {		
//			@Override
//			public void onPrepared(MediaPlayer mp) {
//				mediaPlayer.start();
//			}
//		});		
//		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//			@Override
//			public void onCompletion(MediaPlayer mp) {
//			}
//		});
//		mediaPlayer.setOnErrorListener(new OnErrorListener() {
//			
//			@Override
//			public boolean onError(MediaPlayer mp, int what, int extra) {
//				return false;
//			}
//		});
		
		
		
		setResultClickable(false);
		
		startTimer();
		
		CxAnswerApi.getInstance().requestAnswerQuestion(questionCaller);
	}
	
	OnClickListener clickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_answer_question_title_close_iv:
				back();
				break;
			case R.id.cx_fa_answer_question_result_a_layout_2:
				aOnClick();
				break;
			case R.id.cx_fa_answer_question_result_b_layout_2:
				bOnClick();
				break;
			case R.id.cx_fa_answer_question_result_c_layout_2:
				cOnClick();
				break;
			case R.id.cx_fa_answer_question_result_d_layout_2:
				dOnClick();
				break;

			default:
				break;
			}
			
		}
	};
	
	
	JSONCaller questionCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			
			if(null==result){
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxAnswerQuestionData list=null;
			try {
				list = (CxAnswerQuestionData) result;
			} catch (Exception e) {
			}
			if (null == list || list.getRc()==408) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			
			int rc = list.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(list.getMsg())){
					showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(list.getMsg(),0);
				}
				return -3;
			}
			
			String question = list.getQuestion();
			if(TextUtils.isEmpty(question)){
				showResponseToast(getString(R.string.cx_fa_answer_home_today_remain_over_toast), 0);
				return -4;
			}
			
			isSend=false;
			questionData=list;
			
			rightResult=questionData.getResult();
			
			questionHandler.sendEmptyMessage(NET_REFRESH);
			
			return 0;
		}
	};
	
	
	JSONCaller resultCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			
			
			if(isClose){
				CxLog.i("men", isClose+"");
//				RkAnswerQuestionActivity.this.finish();
//				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
				return 2;
			}
			
			
			if(null==result){
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxAnswerResultData list=null;
			try {
				list = (CxAnswerResultData) result;
			} catch (Exception e) {
			}
			if (null == list || list.getRc()==408) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			
			int rc = list.getRc();
			CxLog.i("men", rc+"");
			if (0 != rc) {
				if(TextUtils.isEmpty(list.getMsg())){
					showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(list.getMsg(),0);
				}
				return -3;
			}
			
			int tips = list.getTips();
			if(tips==0){
				questionHandler.sendEmptyMessageDelayed(CONTINUE,3000);
//				RkAnswerApi.getInstance().requestAnswerQuestion(questionCaller);
			}else if(tips==1){
				questionHandler.sendEmptyMessageDelayed(SHOW_TIPS,3000);
			}
	
			return 0;
		}
	};
	
	
	
	
	JSONCaller remindCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {		
			if(null==result){
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxParseBasic list=null;
			try {
				list = (CxParseBasic) result;
			} catch (Exception e) {
			}
			if (null == list || list.getRc()==408) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			
			int rc = list.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(list.getMsg())){
					showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(list.getMsg(),0);
				}
				return -3;
			}
			
			return 0;
		}
	};
	
	
	
	private View frame;
	private LinearLayout timeLayout;
	private TextView nextText;
	private FrameLayout rightLayout;
	private ImageView shineImg;
	private MediaPlayer mediaPlayer;
	private Dialog halfDialog;
	private Dialog zoreDialog;
	
	
	private void setResultClickable(boolean b){
		aLayout2.setClickable(b);
		bLayout2.setClickable(b);
		cLayout2.setClickable(b);
		dLayout2.setClickable(b);
	}
	
	
	
	protected void aOnClick() {
		setResultClickable(false);
		seletedResult="A";
		aLayout2.setBackgroundResource(R.drawable.talent_btn_answer_h);
		if(!"A".equalsIgnoreCase(rightResult)){
			aImg.setVisibility(View.VISIBLE);
			aImg.setImageResource(R.drawable.talent_answer_wrong);
		}
		commonOnClick();
	}
	
	protected void bOnClick() {
		setResultClickable(false);
		seletedResult="B";
		bLayout2.setBackgroundResource(R.drawable.talent_btn_answer_h);
		if(!"B".equalsIgnoreCase(rightResult)){
			bImg.setVisibility(View.VISIBLE);
			bImg.setImageResource(R.drawable.talent_answer_wrong);
		}
		commonOnClick();
	}
	protected void cOnClick() {
		setResultClickable(false);
		seletedResult="C";
		cLayout2.setBackgroundResource(R.drawable.talent_btn_answer_h);
		if(!"C".equalsIgnoreCase(rightResult)){
			cImg.setVisibility(View.VISIBLE);
			cImg.setImageResource(R.drawable.talent_answer_wrong);
		}
		commonOnClick();
	}
	protected void dOnClick() {
		setResultClickable(false);
		seletedResult="D";
		dLayout2.setBackgroundResource(R.drawable.talent_btn_answer_h);
		if(!"D".equalsIgnoreCase(rightResult)){
			dImg.setVisibility(View.VISIBLE);
			dImg.setImageResource(R.drawable.talent_answer_wrong);
		}
		commonOnClick();
	}

	
	private void commonOnClick(){
		isSend=true;
//		stopTimer();
		if("A".equalsIgnoreCase(rightResult)){
			aImg.setVisibility(View.VISIBLE);
			aImg.setImageResource(R.drawable.talent_answer_right);
		}else if("B".equalsIgnoreCase(rightResult)){
			bImg.setVisibility(View.VISIBLE);
			bImg.setImageResource(R.drawable.talent_answer_right);
		}else if("C".equalsIgnoreCase(rightResult)){
			cImg.setVisibility(View.VISIBLE);
			cImg.setImageResource(R.drawable.talent_answer_right);
		}else if("D".equalsIgnoreCase(rightResult)){
			dImg.setVisibility(View.VISIBLE);
			dImg.setImageResource(R.drawable.talent_answer_right);
		}
		
		timeLayout.setVisibility(View.GONE);
		nextText.setVisibility(View.VISIBLE);
		
		if(seletedResult.equalsIgnoreCase(rightResult)){
			rightLayout.setVisibility(View.VISIBLE);
			shineImg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_anim));
		}
		
		if(questionData!=null){					
			CxAnswerApi.getInstance().requestAnswerResult(questionData.getId(), seletedResult, resultCaller);
		}
		

			
	}
	
	
	private void startTimer(){
		if (null == timer) {
			timer = new Timer(true);
		}
		if (null == timerTask) {
			timerTask = new TimerTask() {

				@Override
				public void run() {			
//					android.os.Message msg = android.os.Message.obtain(
//							mNbHandler, STOP_READ_RECORD);
//					msg.obj = image;
//					msg.sendToTarget();				
					questionHandler.sendEmptyMessage(COUNT_DOWN);
					
				}
			};
		}
		timer.schedule(timerTask,1000, 1000);
	}
	
	
	private void stopTimer(){
		if (null != timer) {
			timer.cancel();
			timer = null;
		}
		if (null != timerTask) {
			timerTask.cancel();
			timerTask = null;
		}
	}
	
	
	private void back(){
		
		isBack=true;
		if(questionData!=null){
			CxLog.i("men", ">>>>>>>>"+isSend);
			if(isSend){
				CxAnswerQuestionActivity.this.finish();
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
				return;
			}else{
				isClose=true;
				stopTimer();
				CxAnswerApi.getInstance().requestAnswerResult(questionData.getId(), seletedResult, resultCaller);
				CxAnswerQuestionActivity.this.finish();
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
			}
		
		}else{
			CxAnswerQuestionActivity.this.finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
		}
		
	}
	
	private void next(){
		
		CxAnswerApi.getInstance().requestAnswerQuestion(questionCaller);
		
	}
	
	
	@Override
	protected void onDestroy() {		
		super.onDestroy();
		
//		questionHandler.removeCallbacks(timerTask);
		isOver=true;
		
		stopTimer();
		if(questionData!=null){
			questionData=null;
		}
		
		if(halfDialog!=null){
			halfDialog.dismiss();
		}
		if(zoreDialog!=null){
			zoreDialog.dismiss();
		}
		
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		
		
	}


	/**
	 * 
	 * @param info
	 * @param number 0 失败；1 成功；2 不要图。
	 */
	private void showResponseToast(String info,int number) {
		Message msg = new Message();
		msg.obj = info;
		msg.arg1=number;
		new Handler(getMainLooper()) {
			public void handleMessage(Message msg) {
				if ((null == msg) || (null == msg.obj)) {
					return;
				}
				int id=-1;
				if(msg.arg1==0){
					id= R.drawable.chatbg_update_error;
				}else if(msg.arg1==1){
					id=R.drawable.chatbg_update_success;
				}
				ToastUtil.getSimpleToast(CxAnswerQuestionActivity.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			back();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};

}
