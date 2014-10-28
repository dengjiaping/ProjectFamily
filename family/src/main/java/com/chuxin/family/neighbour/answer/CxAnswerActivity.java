package com.chuxin.family.neighbour.answer;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.neighbour.CxNbNeighboursHome;
import com.chuxin.family.neighbour.CxNbOurHome;
import com.chuxin.family.neighbour.CxNeighbourList;
import com.chuxin.family.neighbour.CxNeighbourParam;
import com.chuxin.family.net.CxAnswerApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxAnswerHomeList;
import com.chuxin.family.parse.been.data.AnswerHomeRateItem;
import com.chuxin.family.parse.been.data.AnswerHomeTotalItem;
import com.chuxin.family.parse.been.data.AnswerHomeUserInfo;
import com.chuxin.family.parse.been.data.AnswerHomeWeekItem;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.ScreenUtil;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.R;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 谁家最聪明
 * @author wentong.men
 *
 */
public class CxAnswerActivity extends CxRootActivity {
	
	protected static final int NET_REFRESH = 0;
	private TextView totalText;
	private TextView ratioText;
	private ImageView girlRatioImg;
	private ImageView boyRatioImg;
	private CxImageView girlIconImg;
	private CxImageView boyIconImg;
	private TextView leaveText;
	private LinearLayout weekRankLayout;
	private LinearLayout totalRankLayout;
	private LinearLayout ratioRankLayout;
	private TextView weekRankText;
	private TextView totalRankText;
	private TextView ratioRankText;
	private LinearLayout itemLayout;
	private TextView unfoldText;
	private ImageView unfoldImg;
	
	
	private boolean isFold=true; //最下面的条目状态：是否展开  默认折叠
	
	private boolean isOver=false; //今日题目是否答完
	
	private int rank=0;// 周排行 1；总排行 2；答对率排行 3；
	
	private CxAnswerHomeList homeList; //数据data
	
	private Handler homeHandler;
	
	private int myWeekRank=0; //记录自己家排行
	
	private boolean isUp=false; //排名是否上升
	
	private boolean isAlone=false; //是否有密邻
	
	private boolean isNetWork=true; //是否有网络
	
	private MediaPlayer mediaPlayer;
	
	@Override
	protected void onCreate(Bundle arg0) {		
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_activity_neighbour_answer_homepage);
		
		isFold=false;
		
		homeHandler=new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch(msg.what){
				case NET_REFRESH:
					
					netRefresh();
					if(isUp){ //排名上升则跳转
						isUp=false;
						CxNeighbourParam.getInstance().setWeekItems(homeList.getWeekItems());
						Intent intent=new Intent(CxAnswerActivity.this, CxAnswerUpActivity.class);
//						intent.putExtra(RkNeighbourParam.ANSWER_HOME_LIST, homeList.getWeekItems());
						intent.putExtra(CxNeighbourParam.ANSWER_WEEK_RANK, myWeekRank);
						startActivity(intent);
						overridePendingTransition(R.anim.scale_next_in, R.anim.scale_next_out);
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

	protected void netRefresh() { 
		int totalScore = homeList.getTotal_score();
		int rate = homeList.getRight_rate();
		int husbandScore = homeList.getHusband_score();
		int wifeScore = homeList.getWife_score();
		int todayRemain = homeList.getToday_remain();
		totalText.setText(totalScore+"");
		ratioText.setText(rate+"%");
		if(todayRemain>0){
			isOver=false;
			leaveText.setText("今日还剩"+todayRemain+"题");
		}else{
			startAnswer.setClickable(false);
			startAnswer.setBackgroundResource(R.drawable.talent_btn_start_gray);
			isOver=true;
			leaveText.setText(getString(R.string.cx_fa_answer_home_today_remain_over));
		}
		float wifePercent=0;
		float husbandPercent=0;
		int wifePixel=0;
		int husbandPixel=0;
		if(totalScore==0){
			
			wifePercent=0.5f;
			husbandPercent=0.5f;
		}else{
			wifePercent=(float)wifeScore/totalScore;
			husbandPercent=(float)husbandScore/totalScore;
		}
		CxLog.i("men", wifeScore+">>>>>>>>"+husbandScore);
		CxLog.i("men", wifePercent+">>>>>>>>"+husbandPercent);
		
		if(wifePercent==0){
			wifePixel=ScreenUtil.dip2px(CxAnswerActivity.this, 10);
			husbandPixel=LayoutParams.MATCH_PARENT;
		}else if(husbandPercent==0){
			husbandPixel=ScreenUtil.dip2px(CxAnswerActivity.this, 10);
			wifePixel=LayoutParams.MATCH_PARENT;
		}else{
			if(wifePercent<0.15){
				wifePercent=0.15f;
			}
			if(husbandPercent<0.15){
				husbandPercent=0.15f;
			}
			CxLog.i("men", wifePercent+">>>>dd>>>>"+husbandPercent);
			wifePixel=ScreenUtil.dip2px(CxAnswerActivity.this, 80*wifePercent);
			husbandPixel=ScreenUtil.dip2px(CxAnswerActivity.this, 80*husbandPercent);
		}
		
		int heightPixel=ScreenUtil.dip2px(CxAnswerActivity.this, 8);
		
		LayoutParams wifeLp=new LayoutParams(wifePixel, heightPixel);
		wifeLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		girlRatioImg.setLayoutParams(wifeLp);
		
		
		LayoutParams husbandLp=new LayoutParams(husbandPixel, heightPixel);		
		boyRatioImg.setLayoutParams(husbandLp);
		
		
		AnswerHomeUserInfo userInfo = homeList.getUserInfo();
		if(userInfo!=null){
			girlIconImg.displayImage(ImageLoader.getInstance(), userInfo.getWifeUrl(), 
					R.drawable.cx_fa_wf_icon, true, 
					CxGlobalParams.getInstance().getSmallImgConner());
			boyIconImg.displayImage(ImageLoader.getInstance(), userInfo.getHusbandUrl(), 
					R.drawable.cx_fa_hb_icon, true, 
					CxGlobalParams.getInstance().getSmallImgConner());
		}
		updateRank();
		
	}

	private void updateRank() {
		
		if(homeList==null){
			return;
		}
		
		itemLayout.removeAllViews();
		int itemCount=0;

		if(rank==1 && homeList.getWeekItems()!=null && (homeList.getWeekItems().size())>0){
			itemCount=homeList.getWeekItems().size();
			if(isFold && itemCount>3){
				itemCount=3;
			}
		}else if(rank==2 && homeList.getTotalItems()!=null && (homeList.getTotalItems().size())>0){
			itemCount=homeList.getTotalItems().size();
			if(isFold && itemCount>3){
				itemCount=3;
			}
		}else if(rank==3 && homeList.getRateItems()!=null && (homeList.getRateItems().size())>0){
			itemCount=homeList.getRateItems().size();
			if(isFold && itemCount>3){
				itemCount=3;
			}
		}
		
		if(itemCount==1){
			isAlone=true;
		}
		
		for(int i=0;i<itemCount;i++){
			
			View view = View.inflate(CxAnswerActivity.this, R.layout.cx_fa_activity_neighbour_answer_homepage_item, null);
			
			LinearLayout rankLayout = (LinearLayout) view.findViewById(R.id.cx_fa_answer_home_item_layout);
			ImageView firstImg = (ImageView) view.findViewById(R.id.cx_fa_answer_home_item_rank_first_iv); 
			ImageView secondImg = (ImageView) view.findViewById(R.id.cx_fa_answer_home_item_rank_second_or_third_iv); 
			TextView forthText = (TextView) view.findViewById(R.id.cx_fa_answer_home_item_rank_forth_tv); 
			CxImageView girlImg = (CxImageView) view.findViewById(R.id.cx_fa_answer_home_item_girl_icon_iv); 
			CxImageView boyImg = (CxImageView) view.findViewById(R.id.cx_fa_answer_home_item_boy_icon_iv); 
			
			TextView familyText = (TextView) view.findViewById(R.id.cx_fa_answer_home_item_family_name_tv);
			TextView scoreText = (TextView) view.findViewById(R.id.cx_fa_answer_home_item_score_tv);

			if(i==0){
				firstImg.setVisibility(View.VISIBLE);
				secondImg.setVisibility(View.GONE);
				forthText.setVisibility(View.GONE);	
			}else if(i==1){
				firstImg.setVisibility(View.GONE);
				secondImg.setVisibility(View.VISIBLE);
				forthText.setVisibility(View.GONE);	
				secondImg.setImageResource(R.drawable.talent_rank_second);
			}else if(i==2){
				firstImg.setVisibility(View.GONE);
				secondImg.setVisibility(View.VISIBLE);
				forthText.setVisibility(View.GONE);	
				secondImg.setImageResource(R.drawable.talent_rank_third);
			}else if(i>2){
				firstImg.setVisibility(View.GONE);
				secondImg.setVisibility(View.GONE);
				forthText.setVisibility(View.VISIBLE);	
				forthText.setText(i+1+"");
			}
			
			
			if(rank==1){
				final AnswerHomeWeekItem weekItem = homeList.getWeekItems().get(i);
				if(weekItem!=null){
					String groupId = weekItem.getGroupId();					
					girlImg.displayImage(ImageLoader.getInstance(), weekItem.getWifeUrl(), 
							R.drawable.cx_fa_wf_icon, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					boyImg.displayImage(ImageLoader.getInstance(), weekItem.getHusbandUrl(), 
							R.drawable.cx_fa_hb_icon, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					if(CxGlobalParams.getInstance().getPairId().equals(groupId)){
						rankLayout.setBackgroundResource(R.color.cx_fa_co_neighbour_answer_home_item_ourhome_bg);
						familyText.setText(getString(R.string.cx_fa_neighbour_family_name));	
					}else{
						familyText.setText(weekItem.getWifeName()+"和"+weekItem.getHusbandName()+"一家");
					}
					scoreText.setText(weekItem.getWeekScore()+"分");					
				}
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String groupId = weekItem.getGroupId();
						if(CxGlobalParams.getInstance().getPairId().equalsIgnoreCase(groupId)){
							Intent intent=new Intent(CxAnswerActivity.this,CxNbOurHome.class);
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_URL,weekItem.getWifeUrl());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_URL,weekItem.getHusbandUrl());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_PAIR_ID,weekItem.getGroupId());
							startActivity(intent);	
							CxAnswerActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
						}else{
							Intent intent=new Intent(CxAnswerActivity.this,CxNbNeighboursHome.class);
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_ID,weekItem.getGroupId());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_NAME,weekItem.getWifeName());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_NAME,weekItem.getHusbandName());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_URL,weekItem.getWifeUrl());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_URL,weekItem.getHusbandUrl());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_PAIR_ID,weekItem.getGroupId());
							startActivity(intent);	
							CxAnswerActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
						}
						
						
					}
				});
				
			}else if(rank==2){
				final AnswerHomeTotalItem totalItem = homeList.getTotalItems().get(i);
				if(totalItem!=null){
					String groupId = totalItem.getGroupId();					
					girlImg.displayImage(ImageLoader.getInstance(), totalItem.getWifeUrl(), 
							R.drawable.cx_fa_wf_icon, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					boyImg.displayImage(ImageLoader.getInstance(), totalItem.getHusbandUrl(), 
							R.drawable.cx_fa_hb_icon, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					if(CxGlobalParams.getInstance().getPairId().equals(groupId)){
						rankLayout.setBackgroundResource(R.color.cx_fa_co_neighbour_answer_home_item_ourhome_bg);
						familyText.setText(getString(R.string.cx_fa_neighbour_family_name));	
					}else{
						familyText.setText(totalItem.getWifeName()+"和"+totalItem.getHusbandName()+"一家");
					}
					scoreText.setText(totalItem.getTotalScore()+"分");		
				}
				
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String groupId = totalItem.getGroupId();
						if(CxGlobalParams.getInstance().getPairId().equalsIgnoreCase(groupId)){
							Intent intent=new Intent(CxAnswerActivity.this,CxNbOurHome.class);
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_URL,totalItem.getWifeUrl());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_URL,totalItem.getHusbandUrl());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_PAIR_ID,totalItem.getGroupId());
							startActivity(intent);	
							CxAnswerActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
						}else{
							Intent intent=new Intent(CxAnswerActivity.this,CxNbNeighboursHome.class);
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_ID,totalItem.getGroupId());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_NAME,totalItem.getWifeName());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_NAME,totalItem.getHusbandName());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_URL,totalItem.getWifeUrl());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_URL,totalItem.getHusbandUrl());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_PAIR_ID,totalItem.getGroupId());
							startActivity(intent);	
							CxAnswerActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
						}
						
						
					}
				});
				
				
			}else if(rank==3){
				final AnswerHomeRateItem rateItem = homeList.getRateItems().get(i);
				if(rateItem!=null){
					String groupId = rateItem.getGroupId();					
					girlImg.displayImage(ImageLoader.getInstance(), rateItem.getWifeUrl(), 
							R.drawable.cx_fa_wf_icon, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					boyImg.displayImage(ImageLoader.getInstance(), rateItem.getHusbandUrl(), 
							R.drawable.cx_fa_hb_icon, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					if(CxGlobalParams.getInstance().getPairId().equals(groupId)){
						rankLayout.setBackgroundResource(R.color.cx_fa_co_neighbour_answer_home_item_ourhome_bg);
						familyText.setText(getString(R.string.cx_fa_neighbour_family_name));	
					}else{
						familyText.setText(rateItem.getWifeName()+"和"+rateItem.getHusbandName()+"一家");
					}
					scoreText.setText(rateItem.getRate()+"%");					
				}
				
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String groupId = rateItem.getGroupId();
						if(CxGlobalParams.getInstance().getPairId().equalsIgnoreCase(groupId)){
							Intent intent=new Intent(CxAnswerActivity.this,CxNbOurHome.class);
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_URL,rateItem.getWifeUrl());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_URL,rateItem.getHusbandUrl());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_PAIR_ID,rateItem.getGroupId());
							startActivity(intent);	
							CxAnswerActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
						}else{
							Intent intent=new Intent(CxAnswerActivity.this,CxNbNeighboursHome.class);
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_ID,rateItem.getGroupId());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_NAME,rateItem.getWifeName());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_NAME,rateItem.getHusbandName());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_URL,rateItem.getWifeUrl());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_URL,rateItem.getHusbandUrl());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_PAIR_ID,rateItem.getGroupId());
							startActivity(intent);	
							CxAnswerActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
						}
						
						
					}
				});
				
				
			}	
			
			itemLayout.addView(view);
		}
	}

	private void initTitle() {
		ImageView backImg = (ImageView)findViewById(R.id.cx_fa_answer_home_title_back_iv);
		totalText = (TextView) findViewById(R.id.cx_fa_answer_home_title_total_tv);
		ratioText = (TextView) findViewById(R.id.cx_fa_answer_home_title_ratio_tv);
		girlRatioImg = (ImageView) findViewById(R.id.cx_fa_answer_home_title_girl_ratio_iv);
		boyRatioImg = (ImageView) findViewById(R.id.cx_fa_answer_home_title_boy_ratio_iv);
		girlIconImg = (CxImageView)findViewById(R.id.cx_fa_answer_home_title_girl_icon_iv);
		boyIconImg = (CxImageView) findViewById(R.id.cx_fa_answer_home_title_boy_icon_iv);
				
		backImg.setOnClickListener(clickListener);
		
	}

	private void init() {
		
		startAnswer = (Button) findViewById(R.id.cx_fa_answer_home_start_btn);
		leaveText = (TextView) findViewById(R.id.cx_fa_answer_home_leave_tv);
		
		weekRankLayout = (LinearLayout) findViewById(R.id.cx_fa_answer_home_week_rank_layout);
		totalRankLayout = (LinearLayout) findViewById(R.id.cx_fa_answer_home_total_rank_layout);
		ratioRankLayout = (LinearLayout)findViewById(R.id.cx_fa_answer_home_ratio_rank_layout);
		weekRankText = (TextView) findViewById(R.id.cx_fa_answer_home_week_rank_tv);
		totalRankText = (TextView) findViewById(R.id.cx_fa_answer_home_total_rank_tv);
		ratioRankText = (TextView) findViewById(R.id.cx_fa_answer_home_ratio_rank_tv);
		
		itemLayout = (LinearLayout) findViewById(R.id.cx_fa_answer_home_neighbour_items_layout);
		
//		LinearLayout unfoldLayout = (LinearLayout) findViewById(R.id.cx_fa_answer_home_fold_or_unfold_layout);
//		unfoldText = (TextView) findViewById(R.id.cx_fa_answer_home_fold_or_unfold_tv);
//		unfoldImg = (ImageView) findViewById(R.id.cx_fa_answer_home_fold_or_unfold_iv);
		
		
		startAnswer.setOnClickListener(clickListener);
		weekRankLayout.setOnClickListener(clickListener);
		totalRankLayout.setOnClickListener(clickListener);
		ratioRankLayout.setOnClickListener(clickListener);
//		unfoldLayout.setOnClickListener(clickListener);
		
		weekRank();
		
//		RkAnswerApi.getInstance().requestAnswerHome(0, 100, homeCaller, this);

	}
	
	
	
OnClickListener clickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_answer_home_title_back_iv:
				back();
				break;
			case R.id.cx_fa_answer_home_start_btn:
				startAnswer();
				break;
			case R.id.cx_fa_answer_home_week_rank_layout:
				weekRank();
				break;
			case R.id.cx_fa_answer_home_total_rank_layout:
				totalRank();
				break;
			case R.id.cx_fa_answer_home_ratio_rank_layout:
				ratioRank();
				break;
//			case R.id.cx_fa_answer_home_fold_or_unfold_layout:
//				unfold();
//				break;
			
			default:
				break;
			}
			
		}
	};
	
	
	
	@Override
	public void onResume() {		
		super.onResume();
		CxAnswerApi.getInstance().requestAnswerHome(0, 100, homeCaller, CxAnswerActivity.this);
	}


	protected void back() {
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	
	
	protected void ratioRank() {
		if(rank==3){
			return;
		}
		rank=3;
		weekRankLayout.setBackgroundColor(Color.rgb(169, 220, 221));
		totalRankLayout.setBackgroundColor(Color.rgb(169, 220, 221));
		ratioRankLayout.setBackgroundColor(Color.rgb(206, 233, 234));		
		weekRankText.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_answer_home_tab_week_rank), 12, Color.WHITE));
		totalRankText.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_answer_home_tab_total_rank), 12, Color.WHITE));
		ratioRankText.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_answer_home_tab_ratio_rank), 15, Color.rgb(55, 50, 47)));
		
		updateRank();
		
	}

	protected void totalRank() {
		
		if(rank==2){
			return;
		}
		rank=2;
		weekRankLayout.setBackgroundColor(Color.rgb(169, 220, 221));
		totalRankLayout.setBackgroundColor(Color.rgb(206, 233, 234));
		ratioRankLayout.setBackgroundColor(Color.rgb(169, 220, 221));		
		weekRankText.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_answer_home_tab_week_rank), 12, Color.WHITE));
		totalRankText.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_answer_home_tab_total_rank), 15, Color.rgb(55, 50, 47)));
		ratioRankText.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_answer_home_tab_ratio_rank), 12, Color.WHITE));
		
		updateRank();
	}

	protected void weekRank() {
		if(rank==1){
			return;
		}
		rank=1;
		weekRankLayout.setBackgroundColor(Color.rgb(206, 233, 234));
		totalRankLayout.setBackgroundColor(Color.rgb(169, 220, 221));
		ratioRankLayout.setBackgroundColor(Color.rgb(169, 220, 221));		
		weekRankText.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_answer_home_tab_week_rank), 15, Color.rgb(55, 50, 47)));
		totalRankText.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_answer_home_tab_total_rank), 12, Color.WHITE));
		ratioRankText.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_answer_home_tab_ratio_rank), 12, Color.WHITE));
		
		updateRank();	
	}

	protected void startAnswer() {
		
		if(!isNetWork){
			ToastUtil.getSimpleToast(CxAnswerActivity.this, -3, getString(R.string.cx_fa_answer_home_net_fail_2), 1).show();
			return ;
		}
		
		if(isAlone){
			DialogUtil du = DialogUtil.getInstance();
			du.setOnSureClickListener(new OnSureClickListener() {
				
				@Override
				public void surePress() {
					Intent neighbours = new Intent(CxAnswerActivity.this,CxNeighbourList.class);
					startActivity(neighbours);
					CxAnswerActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
					
				}
			});
			du.getSimpleDialog(CxAnswerActivity.this, null, getString(R.string.cx_fa_answer_home_alone_add_neighbour_text), 
					getString(R.string.cx_fa_answer_home_alone_add_neighbour), null).show();;
			return;
		}
		
		if(isOver){
			ToastUtil.getSimpleToast(CxAnswerActivity.this, -3, getString(R.string.cx_fa_answer_home_today_remain_over_toast), 1).show();
			return ;
		}
		
		Intent intent=new Intent(CxAnswerActivity.this, CxAnswerQuestionActivity.class);
		startActivity(intent);
		CxAnswerActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
			
//			ScaleAnimation sa=new ScaleAnimation(0.5f, 1.2f, 0.5f, 1.2f,
//					Animation.RELATIVE_TO_SELF , 0.5f, 
//					Animation.RELATIVE_TO_SELF, 0.5f);
//			sa.setDuration(500);			
//			contentstartAnimation(sa);
		
		
	}
	
	
	JSONCaller homeCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(null==result){
				isNetWork=false;
				showResponseToast(CxAnswerActivity.this.getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxAnswerHomeList list=null;
			try {
				list = (CxAnswerHomeList) result;
			} catch (Exception e) {
			}
			if (null == list || list.getRc()==408) {
				isNetWork=false;
				showResponseToast(CxAnswerActivity.this.getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			
			int rc = list.getRc();
			if (0 != rc) {
				isNetWork=false;
				if(TextUtils.isEmpty(list.getMsg())){
					showResponseToast(CxAnswerActivity.this.getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(list.getMsg(),0);
				}
				return -3;
			}

			homeList=list;
			
			int weekRank = list.getWeekRank();
			if(weekRank<myWeekRank){
				isUp=true;
			}
			myWeekRank=weekRank;
			CxLog.i("men", ">>>>>>>>>>>>>>>>myWeekRank:"+myWeekRank);
			homeHandler.sendEmptyMessage(NET_REFRESH);
			
			
			return 0;
		}
	};
	private Button startAnswer;
	
	public void onDestroy() {
		super.onDestroy();
		
		if(homeList!=null){
			homeList=null;
		}
		
		/*if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}*/
		
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
		new Handler(CxAnswerActivity.this.getMainLooper()) {
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
				ToastUtil.getSimpleToast(CxAnswerActivity.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	};
		
	@Override
	public void onStop() {
		try {
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onStop();
	}

	@Override
	public void onStart() {
		super.onStart();
		
		try {
			if (null == mediaPlayer) {
				mediaPlayer = MediaPlayer.create(CxAnswerActivity.this,
						R.raw.answer_background_music);
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setLooping(true);
			}
			
			try {
				mediaPlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mediaPlayer.start();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
