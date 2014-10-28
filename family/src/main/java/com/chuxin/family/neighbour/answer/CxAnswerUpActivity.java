package com.chuxin.family.neighbour.answer;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.neighbour.CxNeighbourParam;
import com.chuxin.family.net.CxAnswerApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.data.AnswerHomeWeekItem;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.R;

/**
 * 排名上升页面 
 * @author wentong.men
 *
 */
public class CxAnswerUpActivity extends CxRootActivity {

	
	private String oppoGroupId;//对方一家的密邻id
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_activity_neighbour_answer_up);
		
		init();
		
	}

	private void init() {
		
		Intent intent = getIntent();
		ArrayList<AnswerHomeWeekItem> weekItems = CxNeighbourParam.getInstance().getWeekItems();
		int weekRank = intent.getIntExtra(CxNeighbourParam.ANSWER_WEEK_RANK, -1);
		
		if(weekRank<0 || weekItems==null){
//			ToastUtil.getSimpleToast(this, -3, "获取参数错误", 1).show();
			finish();
		}
		
		
		CxImageView myGrilImg = (CxImageView) findViewById(R.id.cx_fa_answer_up_my_gril_icon_iv);
		CxImageView myBoyImg = (CxImageView) findViewById(R.id.cx_fa_answer_up_my_boy_icon_iv);
		TextView myFamilyText = (TextView) findViewById(R.id.cx_fa_answer_up_my_family_name_tv);
		TextView myScoreText = (TextView) findViewById(R.id.cx_fa_answer_up_my_score_tv);
		CxImageView oppoGrilImg = (CxImageView) findViewById(R.id.cx_fa_answer_up_other_gril_icon_iv);
		CxImageView oppoBoyImg = (CxImageView) findViewById(R.id.cx_fa_answer_up_other_boy_icon_iv);
		TextView oppoFamilyText = (TextView) findViewById(R.id.cx_fa_answer_up_other_family_name_tv);
		TextView oppoScoreText = (TextView) findViewById(R.id.cx_fa_answer_up_other_score_tv);
		ImageView backImg = (ImageView) findViewById(R.id.cx_fa_answer_up_back_iv);
		ImageView shareImg = (ImageView) findViewById(R.id.cx_fa_answer_up_share_iv);
		ImageView shineImg = (ImageView) findViewById(R.id.cx_fa_answer_up_shine_iv);
		
		AnswerHomeWeekItem myItem = weekItems.get(weekRank);
		myGrilImg.displayImage(imageLoader, myItem.getWifeUrl(), 
				R.drawable.cx_fa_wf_icon, true, 
				CxGlobalParams.getInstance().getSmallImgConner());
		myBoyImg.displayImage(imageLoader, myItem.getHusbandUrl(), 
				R.drawable.cx_fa_hb_icon, true, 
				CxGlobalParams.getInstance().getSmallImgConner());
		myFamilyText.setText(getString(R.string.cx_fa_neighbour_family_name));
		myScoreText.setText(myItem.getWeekScore());
		
		AnswerHomeWeekItem otherItem = weekItems.get(weekRank+1);
		oppoGroupId=otherItem.getGroupId();
		
		oppoGrilImg.displayImage(imageLoader, otherItem.getWifeUrl(),
				R.drawable.cx_fa_wf_icon, true, 
				CxGlobalParams.getInstance().getSmallImgConner());
		oppoBoyImg.displayImage(imageLoader, otherItem.getHusbandUrl(), 
				R.drawable.cx_fa_hb_icon, true, 
				CxGlobalParams.getInstance().getSmallImgConner());
		oppoFamilyText.setText(otherItem.getWifeName()+"和"+otherItem.getHusbandName()+"一家");
		oppoScoreText.setText(otherItem.getWeekScore());
		
		backImg.setOnClickListener(clickListener);
		shareImg.setOnClickListener(clickListener);
		
		shineImg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_anim));
	}
	
	
	OnClickListener clickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_answer_up_back_iv:
				finish();
				break;
			case R.id.cx_fa_answer_up_share_iv:
				CxAnswerApi.getInstance().requestNeighbourShare(oppoGroupId, shareCaller);
				break;

			default:
				break;
			}
			
		}
	};
	
	
	JSONCaller shareCaller=new JSONCaller() {
		
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

			showResponseToast(getString(R.string.cx_fa_answer_up_share_text), 1);
			
			CxAnswerUpActivity.this.finish();
			return 0;
		}
	};
	
	
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
				ToastUtil.getSimpleToast(CxAnswerUpActivity.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
//				back();
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};
	
	
}
