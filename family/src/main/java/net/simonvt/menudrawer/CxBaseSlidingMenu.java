package net.simonvt.menudrawer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.calendar.CxCalendarParam;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxAuthenNew;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.net.ConnectionManager;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.pair.CxPairActivity;
import com.chuxin.family.parse.been.CxMateProfile;
import com.chuxin.family.parse.been.CxUserProfile;
import com.chuxin.family.parse.been.data.CxMateProfileDataField;
import com.chuxin.family.parse.been.data.CxUserProfileDataField;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxResourceManager;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.R;

public abstract class CxBaseSlidingMenu extends CxRootActivity {

	protected MenuDrawer mMenuDrawer;

	protected ListView mList;

	protected CxImageView mPartnerImageView;
	protected TextView mMateName;
	protected LinearLayout mChatLayout, mZoneLayout, /*mSettingLayout,*/
			mRemindLayout, mInviteLayout, tempLayout, mMoreLayout,
			mNeighbourLayout, mFinanceRecorderLayout, mCalendarLayout,
			mKidLayout, mUnpairMoreLayout;
	
	protected LinearLayout mPairedLayout, mUnpairLayout;
	
	protected int mTraceViewId;
	protected TextView mTitle, mUnPairTextTip;
//	protected FrameLayout mMateLayer;

	public boolean setPairZero = false;

	private CurrentObserver mGlobalObserver;
	private static Button mUnReadBtn/* 蜜邻圈未读状态 */, mSpaceUnReadBtn/* 相册未读状态 */;

	public static final int UPDATE_UNREAD_MESSAGE = 0; // 密邻未读消息数
	public static final int UPDATE_SPACE_UNREAD_MESSAGE = 1; // 二人空间未读消息数
	public static final int UPDATE_KID_UNREAD_MESSAGE = 2; // 孩子空间未读消息数
	public static final int CHANGE_MENU_KID = 3; // 孩子空间未读消息数
	
//	private GridView mUnPairFuncViewt;
//	private MyUnPairFuncAdapter mAdapter;
//	private int mGridPadValue = 0;

	public static Handler mBaseSlidingMenuHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_UNREAD_MESSAGE:
				changeUnReadBtn();
				break;
			case UPDATE_SPACE_UNREAD_MESSAGE:
				changeSpaceUnReadBtn();
				break;
			case UPDATE_KID_UNREAD_MESSAGE:
				changeKidUnReadBtn();
				break;
			}
		}
	};
	

	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);

		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND,
				getDrawerPosition(), getDragMode());

		mMenuDrawer
				.setOnInterceptMoveEventListener(new MenuDrawer.OnInterceptMoveEventListener() {

					@Override
					public boolean isViewDraggable(View v, int dx, int x, int y) {
						if (CxGlobalParams.getInstance().isRecorderFlag()) {
							return true;
						}
						return false;
					}
				});

//		mGridPadValue = getResources().getDimensionPixelSize(R.dimen.cx_fa_grid_item_padding);
		
		View menuView = LayoutInflater.from(CxBaseSlidingMenu.this).inflate(
				R.layout.cx_fa_view_main_menu_new, null);

		// 菜单选项初始化
		mPairedLayout = (LinearLayout)menuView.findViewById(R.id.pair_layout);
		mUnpairLayout = (LinearLayout)menuView.findViewById(R.id.cx_fa_no_pair_layout); 
		
		mInviteOppoLayout = (LinearLayout) menuView.findViewById(R.id.invite_oppo_rl_layout);
		mPartnerImageView = (CxImageView) menuView.findViewById(R.id.menuPage_imageView1);
		
		mChatLayout = (LinearLayout) menuView.findViewById(R.id.chat_rl_layout);
		mKidLayout = (LinearLayout) menuView.findViewById(R.id.kids_rl_layout);
		mZoneLayout2 = (LinearLayout) menuView.findViewById(R.id.zone_rl_layout2);		
		mNeighbourLayout = (LinearLayout) menuView.findViewById(R.id.neighbour_rl_layout); // add by shichao 20131024		
		
		
		mCalendarAndZoneLayout = (LinearLayout) menuView.findViewById(R.id.calendar_and_zone_layout);
		mAccountingAndMoreLayout = (LinearLayout) menuView.findViewById(R.id.accounting_and_more_layout);
		mCalendarLayout = (LinearLayout) menuView.findViewById(R.id.calendar_rl_layout);
		mZoneLayout = (LinearLayout) menuView.findViewById(R.id.zone_rl_layout);
		mFinanceRecorderLayout = (LinearLayout) menuView.findViewById(R.id.finance_recorder_layout);
		mMoreLayout = (LinearLayout) menuView.findViewById(R.id.more_rl_layout);
		
		mCalendarAndAccountingLayout = (LinearLayout) menuView.findViewById(R.id.calendar_and_accounting_layout);
		mMoreAndNullLayout = (LinearLayout) menuView.findViewById(R.id.more_and_null_layout);
		mCalendarLayout2 = (LinearLayout) menuView.findViewById(R.id.calendar_rl_layout2);
		mFinanceRecorderLayout2 = (LinearLayout) menuView.findViewById(R.id.finance_recorder_layout2);
		mMoreLayout2 = (LinearLayout) menuView.findViewById(R.id.more_rl_layout2);
		
		
		mUnReadBtn = (Button) menuView.findViewById(R.id.menuPage_unreadview);	
		changeUnReadBtn();

		mKidUnReadBtn = (Button) menuView.findViewById(R.id.menuPage_kid_unreadview);	
		changeKidUnReadBtn();
		
		mSpaceUnReadBtn = (Button) menuView.findViewById(R.id.menuPage_space_unreadview);
		mSpaceUnReadBtn2 = (Button) menuView.findViewById(R.id.menuPage_space_unreadview2);
		changeSpaceUnReadBtn();
		
		mChatText = (TextView) menuView.findViewById(R.id.cx_fa_main_menu_chat_tv);
		mChatText.setText(CxResourceString.getInstance().str_main_menu_chat_with);
		
		mInviteOppoText = (TextView) menuView.findViewById(R.id.cx_fa_main_menu_invite_oppo_tv);
		mInviteOppoText.setText(CxResourceString.getInstance().str_main_menu_invite);
		
		
		mUnpairMoreLayout = (LinearLayout) menuView.findViewById(R.id.unpair_more_layout);
		mInviteLayout = (LinearLayout) menuView.findViewById(R.id.invite_rl_layout);
	

//		int headHeight = (int) ((mMenuDrawer.getMenuSize() - ((20 * getResources()
//				.getDisplayMetrics().density) + 0.5f)) * 0.8);
		int headHeight = (int) (mMenuDrawer.getMenuSize() * 0.67);
		ViewGroup.LayoutParams params = mPartnerImageView.getLayoutParams();
		params.height = headHeight;
		mPartnerImageView.setLayoutParams(params);		
		mPartnerImageView.setScaleType(ScaleType.CENTER_CROP);


		mUnPairTextTip = (TextView) menuView.findViewById(R.id.unpair_info_tip);
		mUnPairTextTip.setText(CxResourceString.getInstance().str_main_login_comingtext);
		
		mInviteOppoLayout.setOnClickListener(inviteClick);
		mPartnerImageView.setOnClickListener(menuItemClick);
		

		mChatLayout.setOnClickListener(menuItemClick);
		mKidLayout.setOnClickListener(menuItemClick);
		mZoneLayout.setOnClickListener(menuItemClick);
		mNeighbourLayout.setOnClickListener(menuItemClick);
		
		mCalendarLayout.setOnClickListener(menuItemClick);
		mZoneLayout2.setOnClickListener(menuItemClick);
		mFinanceRecorderLayout2.setOnClickListener(menuItemClick);
		mMoreLayout.setOnClickListener(menuItemClick);
		
		mCalendarLayout2.setOnClickListener(menuItemClick);
		mFinanceRecorderLayout.setOnClickListener(menuItemClick);
		mMoreLayout2.setOnClickListener(menuItemClick);
		
			
		mInviteLayout.setOnClickListener(menuItemClick);
		mUnpairMoreLayout.setOnClickListener(menuItemClick);
		
		
		mMenuDrawer.setMenuView(menuView);
		showMenu();

		// 设置观察者
		CxGlobalParams mGlobalParam = CxGlobalParams.getInstance(); // 获取model的subject实例
		mGlobalObserver = new CurrentObserver(); // 生成观察者实例
		// 设置观察目标
		List<String> tags = new ArrayList<String>();
		tags.add(CxGlobalParams.PAIR); // 结对状态
		tags.add(CxGlobalParams.SINGLE_MODE); //单人模式状态
		tags.add(CxGlobalParams.IS_LOGIN); // 登录状态
		tags.add(CxGlobalParams.VERSION_TYPE); // 版本
//		tags.add(RkGlobalParams.PARTNER_ICON_BIG); // 对方的大头像
		tags.add(CxGlobalParams.FAMILY_BIG);  //家庭头像
		tags.add(CxGlobalParams.PARTNER_NAME); // 对方的昵称
		tags.add(CxGlobalParams.GENDER); // 性别 版本
		tags.add(CxGlobalParams.CHANGE_MENU); // 切换fragment
		// 此界面只针对结对状态进行监听
		mGlobalObserver.setListenTag(tags); // 设置观察目标
		mGlobalObserver.setMainThread(true); // 设置在UI线程执行update
		mGlobalParam.registerObserver(mGlobalObserver); // 注册观察者

	}

	protected void showMenu() {
		CxGlobalParams mGlobalParam = CxGlobalParams.getInstance();
		if (1 != mGlobalParam.getPair()) { // 未结对
			mInviteLayout.setBackgroundResource(R.color.menu_item_fouced);
			tempLayout = mInviteLayout;
			mTraceViewId = R.id.invite_rl_layout;
			mPairedLayout.setVisibility(View.GONE);
			mUnpairLayout.setVisibility(View.VISIBLE);
		}else{
			mChatLayout.setBackgroundResource(R.color.menu_item_fouced);
			tempLayout = mChatLayout;
			mTraceViewId = R.id.chat_rl_layout;
			mPairedLayout.setVisibility(View.VISIBLE);
			mUnpairLayout.setVisibility(View.GONE);
			int single_mode = CxGlobalParams.getInstance().getSingle_mode();		
			if(single_mode==1){
				mInviteOppoLayout.setVisibility(View.VISIBLE);
			}else{
				mInviteOppoLayout.setVisibility(View.GONE);
			}
			
			int version_type = CxGlobalParams.getInstance().getVersion_type();	
			if(version_type==1){
				mPartnerImageView.setImageResource(R.drawable.memo_defaultimage);
				mPartnerImageView.displayImage(imageLoader, CxGlobalParams
						.getInstance().getFamily_big(), R.drawable.memo_defaultimage, false, 0);
				mKidLayout.setVisibility(View.GONE);
				mZoneLayout2.setVisibility(View.VISIBLE);
				
				mCalendarAndZoneLayout.setVisibility(View.GONE);
				mCalendarAndAccountingLayout.setVisibility(View.VISIBLE);
				mAccountingAndMoreLayout.setVisibility(View.GONE);
				mMoreAndNullLayout.setVisibility(View.VISIBLE);
			}else{
				mPartnerImageView.setImageResource(R.drawable.memo_defaultimage_family);
				mPartnerImageView.displayImage(imageLoader, CxGlobalParams
						.getInstance().getFamily_big(), R.drawable.memo_defaultimage_family, false, 0);
				mKidLayout.setVisibility(View.VISIBLE);
				mZoneLayout2.setVisibility(View.GONE);
				
				mCalendarAndZoneLayout.setVisibility(View.VISIBLE);
				mCalendarAndAccountingLayout.setVisibility(View.GONE);
				mAccountingAndMoreLayout.setVisibility(View.VISIBLE);
				mMoreAndNullLayout.setVisibility(View.GONE);
			}
			
		}
	}
	
	OnClickListener inviteClick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.invite_oppo_rl_layout:
				Intent inviteIntent=new Intent(CxBaseSlidingMenu.this,CxPairActivity.class);
				startActivity(inviteIntent);
				overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				break;

			default:
				break;
			}
			
		}
	};

	OnClickListener menuItemClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
//			int tempId = v.getId();
//			if ((tempId == R.id.calendar_rl_layout) ){
////				Toast.makeText(RkBaseSlidingMenu.this, "新版即将开启", Toast.LENGTH_SHORT).show();
//				ToastUtil.getSimpleToast(RkBaseSlidingMenu.this, -1, getString(R.string.cx_fa_role_main_menu_calendar_toast_text), 1).show();
//				return;
//			}
			
			if (mTraceViewId == v.getId()) {
				mMenuDrawer.closeMenu();
				return;
			}
			if(R.id.calendar_rl_layout==v.getId()){
				CxCalendarParam.getInstance().setFragment_type(1);
			}
			menuEvent(v.getId());

		}
	};

	public void menuEvent(int menuItem) {
		if (!((menuItem == R.id.menuPage_imageView1)
				|| (menuItem == R.id.chat_rl_layout)
				|| (menuItem == R.id.kids_rl_layout)
				|| (menuItem == R.id.zone_rl_layout)
				|| (menuItem == R.id.neighbour_rl_layout)
				|| (menuItem == R.id.calendar_rl_layout)
				|| (menuItem == R.id.zone_rl_layout2)
				|| (menuItem == R.id.finance_recorder_layout2)
				|| (menuItem == R.id.more_rl_layout)
				|| (menuItem == R.id.calendar_rl_layout2)
				|| (menuItem == R.id.finance_recorder_layout)
				|| (menuItem == R.id.more_rl_layout2) 
				|| (menuItem == R.id.invite_rl_layout)
				|| (menuItem == R.id.unpair_more_layout)
				|| (menuItem == R.id.invite_oppo_rl_layout)
		)) {
			CxLog.e("main activity", "no id match menu item");
			return;
		} // || (menuItem == R.id.remind_rl_layout) by shichao
		switch (menuItem) {
		case R.id.menuPage_imageView1: //头像
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_MATE_PROFILE);
			tempLayout = null;
			mTraceViewId = R.id.menuPage_imageView1;
			break;
			
		case R.id.chat_rl_layout: //聊天
			
			mChatLayout.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_CHAT);
			tempLayout = mChatLayout;
			mTraceViewId = R.id.chat_rl_layout;
			break;
		case R.id.kids_rl_layout://孩子空间
			
			mKidLayout.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_KID);
			tempLayout = mKidLayout;
			mTraceViewId = R.id.kids_rl_layout;
			break;
			
		case R.id.zone_rl_layout://相册
		
			mZoneLayout.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_ZONE);
			tempLayout = mZoneLayout;
			mTraceViewId = R.id.zone_rl_layout;
			break;
			
		case R.id.neighbour_rl_layout://密邻
			
			mNeighbourLayout.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_NEIGHBOUR);
			tempLayout = mNeighbourLayout;
			mTraceViewId = R.id.neighbour_rl_layout;
			break;
			
		case R.id.calendar_rl_layout:		//日历	
			mCalendarLayout.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_CALENDAR);
			tempLayout = mCalendarLayout;
			mTraceViewId = R.id.calendar_rl_layout;
			break;
			
		case R.id.zone_rl_layout2: //相册2
			
			mZoneLayout2.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_ZONE);
			tempLayout = mZoneLayout2;
			mTraceViewId = R.id.zone_rl_layout2;
			break;
			
		case R.id.finance_recorder_layout2: // 记账2
			
			mFinanceRecorderLayout2.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_ACCOUNT);
			tempLayout = mFinanceRecorderLayout2;
			mTraceViewId = R.id.finance_recorder_layout2;
			break;
	
		case R.id.more_rl_layout: //更多
			
			mMoreLayout.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_MORE);
			tempLayout = mMoreLayout;
			mTraceViewId = R.id.more_rl_layout;
			break;
		case R.id.calendar_rl_layout2:		 //日历2	
			mCalendarLayout2.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_CALENDAR);
			tempLayout = mCalendarLayout2;
			mTraceViewId = R.id.calendar_rl_layout2;
			break;
			
		case R.id.finance_recorder_layout: // 记账
			
			mFinanceRecorderLayout.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_ACCOUNT);
			tempLayout = mFinanceRecorderLayout;
			mTraceViewId = R.id.finance_recorder_layout;
			break;
			
		case R.id.more_rl_layout2: //更多2
	
			mMoreLayout2.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_MORE);
			tempLayout = mMoreLayout2;
			mTraceViewId = R.id.more_rl_layout2;
			break;

		case R.id.invite_rl_layout: //未结对邀请
			
			mInviteLayout.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_INVITE);
			tempLayout = mInviteLayout;
			mTraceViewId = R.id.invite_rl_layout;
			break;
		

		case R.id.unpair_more_layout: //未结对更多
			
			mUnpairMoreLayout.setBackgroundResource(R.color.menu_item_fouced);
			if (null != tempLayout) {
				tempLayout.setBackgroundResource(R.color.menu_item_normal);
			}
			switchFragment(FRAGMENT_MORE);
			tempLayout = mUnpairMoreLayout;
			mTraceViewId = R.id.unpair_more_layout;
			break;
		

		
		case R.id.invite_oppo_rl_layout: // 邀请		
			
//			if (null != tempLayout) {
//				tempLayout.setBackgroundResource(R.drawable.menu_btn);
//			}
//			switchFragment(FRAGMENT_INVITE_OPPO);
//			tempLayout = null;
//			mTraceViewId = R.id.invite_oppo_rl_layout;
			break;
		default:
			break;
		}
	}

	public static final int FRAGMENT_MATE_PROFILE = R.id.menuPage_imageView1;
	public static final int FRAGMENT_CHAT = R.id.chat_rl_layout;
	public static final int FRAGMENT_KID = R.id.kids_rl_layout;
	public static final int FRAGMENT_INVITE = R.id.zone_rl_layout;
	public static final int FRAGMENT_NEIGHBOUR = R.id.neighbour_rl_layout; 
	public static final int FRAGMENT_CALENDAR = R.id.calendar_rl_layout;

	public static final int FRAGMENT_MORE = R.id.more_rl_layout;

	public static final int FRAGMENT_ACCOUNT = R.id.finance_recorder_layout;
	
	public static final int FRAGMENT_ZONE = R.id.invite_rl_layout;
	public static final int FRAGMENT_INVITE_OPPO = R.id.invite_oppo_rl_layout;
	
//	public static final int FRAGMENT_SETTINGS = R.id.settings_rl_layout;
	
	
	
	


	protected abstract void switchFragment(int item);

	protected abstract int getDragMode();

	protected abstract Position getDrawerPosition();

	/*
	 * //对外提供content部分 public View getcontentView(){ return
	 * mMenuDrawer.getContentContainer(); }
	 */

	protected abstract void logout();

	protected abstract void pairStatusChange(int item);

	class CurrentObserver extends CxObserverInterface {

		/* 全局监听主要事项有：1、登录与否 2、前后台的监听 */
		@Override
		public void receiveUpdate(String actionTag) {
			if (null == actionTag) {
				return;
			}

			if (actionTag.equals(CxGlobalParams.IS_LOGIN)) {
				CxLog.i("RkMain_men", CxGlobalParams.getInstance().isLogin()+">>>>>>>>>>>2"+CxAuthenNew.isShown());
				if (!CxGlobalParams.getInstance().isLogin()) { // 登出
					logout();
				}
				return;
			}
			
			if (actionTag.equals(CxGlobalParams.VERSION_TYPE)) {
				CxLog.i("RkMain_men", CxGlobalParams.getInstance().getVersion_type()+">>>>>>>>>>>2"+CxAuthenNew.isShown());
				showMenu();
				int version_type = CxGlobalParams.getInstance().getVersion_type();
				if(version_type==1){
					menuEvent(R.id.more_rl_layout2);
				}else{
					menuEvent(R.id.more_rl_layout);
				}
				return;
			}
			
			if (actionTag.equals(CxGlobalParams.FAMILY_BIG)) { // 菜单中对方的大头像
			// mPartnerImageView.setImage(RkGlobalParams.getInstance()
			// .getPartnerIconBig(), false, mPartnerImageView
			// .getWidth(), BaseSlidingMenu.this, "head", BaseSlidingMenu.this);
//				mPartnerImageView.displayImage(imageLoader, RkGlobalParams
//						.getInstance().getPartnerIconBig(), RkResourceDarwable
//						.getInstance().dr_mate_memo_imagedefault, false, 0);
				
				int version_type = CxGlobalParams.getInstance().getVersion_type();
				if(version_type==1){
					mPartnerImageView.displayImage(imageLoader, CxGlobalParams
							.getInstance().getFamily_big(), R.drawable.memo_defaultimage, false, 0);
				}else{
					mPartnerImageView.displayImage(imageLoader, CxGlobalParams
							.getInstance().getFamily_big(), R.drawable.memo_defaultimage_family, false, 0);
				}
				
				
				
				return;
			}

			if (actionTag.equalsIgnoreCase(CxGlobalParams.PARTNER_NAME)) {
//				RkLog.i("RkBaseSlidingMenu_men", getString(RkResourceString.getInstance().str_mate_name));
//				mMateName.setText(String.format(
//								getString(RkResourceString.getInstance().str_mate_name),
//								RkGlobalParams.getInstance().getPartnerName()));
				return;
			}
			
			if (actionTag.equalsIgnoreCase(CxGlobalParams.GENDER)) {
				mChatText.setText(CxResourceString.getInstance().str_main_menu_chat_with);
				mInviteOppoText.setText(CxResourceString.getInstance().str_main_menu_invite);
				return;
			}
			if (actionTag.equalsIgnoreCase(CxGlobalParams.CHANGE_MENU)) {
				int menu = CxGlobalParams.getInstance().getChangeMenu();
				menuEvent(menu);
				return;
			}
			
			
			if (actionTag.equals(CxGlobalParams.SINGLE_MODE)) {
				int mode = CxGlobalParams.getInstance().getSingle_mode();
				if(mode==0){
					ToastUtil.getSimpleToast(CxBaseSlidingMenu.this,-2 , "双方帐号绑定成功", 1).show();;
					
					new Thread() {
						public void run() {
							fetchMyInfo(CxGlobalParams.getInstance()
									.getUserId());
						};
					}.start();
				}
				return;
			}
			

			if (this.getListenTag().contains(CxGlobalParams.PAIR)) { // 结对标志位变化
				CxGlobalParams mGlobalParam = CxGlobalParams.getInstance();
				if (1 == mGlobalParam.getPair()) { // 结对了
					/************
					 * 分主动结对和被动结对的情况，如果主动结对，已经拥有pair ID 和对方
					 * 的UID，此时只需要获取对方资料；如果是被动结对，此时对方资料、pair id、
					 * 和对方UID都不知道，需要先获取自己资料，成功后再获取对方的资料，成功后再切换视图。
					 * 现行做法：一旦发现有结对成功：不分主动还是被动，都访问自己的资料和对方资料 ×××××
					 */
					new Thread() {
						public void run() {
							fetchMyInfo(CxGlobalParams.getInstance()
									.getUserId());
						};
					}.start();

					// // 修改默认菜单选择项:结对成功装入聊天界面
					// pairStatusChange(FRAGMENT_CHAT);
				} else { // 未结对
					// 修改默认菜单选择项:解除结对就转到邀请界面
					/*
					 * if (setPairZero) {
					 * //获取自己的资料失败后，本界面自己调用RkGlobalParams.getInstance
					 * ().setPair(0); setPairZero = false; return; }
					 */
					// 以下是long polling改变的，需要立即响应切换视图到未结对状态
					/*
					 * if (null != tempLayout) {
					 * tempLayout.setBackgroundResource(R.drawable.menu_btn); }
					 * tempLayout = mInviteLayout; mTraceViewId =
					 * R.id.invite_rl_layout; pairStatusChange(FRAGMENT_INVITE);
					 */
					showMenu();
					menuEvent(R.id.invite_rl_layout);

				}

				return;
			}

		}

	}// end class

	/* 获取用户在chuxin上的资料 */
	public void fetchMyInfo(String uid) {
		UserApi userApi = UserApi.getInstance();

		userApi.getUserProfile(uid, new ConnectionManager.JSONCaller() {

			@Override
			public int call(Object data) {
				if (null == data) {
					setPairZero = true;
					CxGlobalParams.getInstance().setPair(0);
					return -1;
				}

				CxUserProfile userInitInfo = null;
				try {
					userInitInfo = (CxUserProfile) data;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (null == userInitInfo) {
					setPairZero = true;
					CxGlobalParams.getInstance().setPair(0);
					return -1;
				}
				if (0 != userInitInfo.getRc()) {
					setPairZero = true;
					CxGlobalParams.getInstance().setPair(0);
					return -2;
				}
				// 获取成功,初始化用户信息，之后就进入主界面
				CxUserProfileDataField profile = userInitInfo.getData();
				if (null == profile) {
					setPairZero = true;
					CxGlobalParams.getInstance().setPair(0);
					return -3;
				}

				String tempMateId = profile.getPartner_id();

				if (TextUtils.isEmpty(tempMateId)) { // 如果这种情况取到对方的UID为null，认为是未结对
					setPairZero = true;
					CxGlobalParams.getInstance().setPair(0);
					return 0;
				}

				CxGlobalParams global = CxGlobalParams.getInstance();
				global.setVersion(profile.getGender());
				global.setIconBig(profile.getIcon_big());
				global.setIconMid(profile.getIcon_mid());
				global.setIconSmall(profile.getIcon_small());
				// global.setPartnerName(profile.getName());
				global.setIsLogin(true);
				global.setPair(1); // 1表示结对(0表示未结对)
				global.setPartnerId(profile.getPartner_id()); // 对方UID
				global.setPairId(profile.getPair_id()); // 结对号
				global.setZoneBackground(profile.getBg_big()); // 二人空间的背景图
				global.setUserId(profile.getUid());
				global.setChatBackgroundBig(profile.getChat_big());
				global.setChatBackgroundSmall(profile.getChat_small());
				global.setAppNormal(profile.getUid());

				// 加载聊天的三种模式设置
				SharedPreferences chatSp = getSharedPreferences(
						CxGlobalConst.S_CHAT_NAME, Context.MODE_PRIVATE);
				boolean chatSound = chatSp.getBoolean(
						CxGlobalConst.S_CHAT_SOUND, false);
				boolean chatShock = chatSp.getBoolean(
						CxGlobalConst.S_CHAT_SHOCK, false);
				boolean chatEarphone = chatSp.getBoolean(
						CxGlobalConst.S_CHAT_EARPHONE, false);
				global.setChatSound(chatSound);
				global.setChatShock(chatShock);
				global.setChatEarphone(chatEarphone);

				// 成功后要切入聊天界面，同时换左侧的菜单界面
				new Handler(getMainLooper()) {
					public void handleMessage(Message msg) {
						showMenu();
						menuEvent(FRAGMENT_CHAT);
					};
				}.sendEmptyMessageDelayed(1, 20);

				// 如果结对且伴侣UID不为空，就要同时开启线程去获取伴侣资料（视为结对成功的情况）
				UserApi.getInstance().getUserPartnerProfile(
						userMateProfileCallback);

				return 0;
			}

		});
	}

	// 伴侣资料获取回调
	JSONCaller userMateProfileCallback = new JSONCaller() {

		@Override
		public int call(Object result) {
			if (null == result) {
				setPairZero = true;
				CxGlobalParams.getInstance().setPair(0);
				return -1; // 不做其他处理
			}
			try {
				CxMateProfile mateProfile = (CxMateProfile) result;
				if (0 != mateProfile.getRc() || (null == mateProfile.getData())) {
					setPairZero = true;
					CxGlobalParams.getInstance().setPair(0);
					return -1; // 不做其他处理
				}
				// 正常获取成功就要设置伴侣资料到RkMateParams
				CxMateProfileDataField profileDataField = mateProfile.getData();
				CxMateParams myMateProfile = CxMateParams.getInstance();
				myMateProfile.setMateData(profileDataField.getData());
				myMateProfile.setMateIcon(profileDataField.getIcon());
				myMateProfile.setmMateBirth(profileDataField.getBirth());
				myMateProfile.setmMateEmail(profileDataField.getEmail());
				myMateProfile.setmMateMobile(profileDataField.getMobile());
				myMateProfile.setmMateName(profileDataField.getName());
				myMateProfile.setmMateNote(profileDataField.getNote());
				myMateProfile.setmMateUid(profileDataField.getPartner_id());

				// 对方头像添加进全局
				CxGlobalParams.getInstance().setPartnerIconBig(
						profileDataField.getIcon());
				CxGlobalParams.getInstance().setPartnerName(
						profileDataField.getName());

				/*
				 * //成功后要切入聊天界面，同时换左侧的菜单界面 new Handler(getMainLooper()){ public
				 * void handleMessage(Message msg) {
				 * pairStatusChange(FRAGMENT_CHAT); showMenu(); };
				 * }.sendEmptyMessageDelayed(1, 20);
				 */

			} catch (Exception e) {
				setPairZero = true;
				CxGlobalParams.getInstance().setPair(0);
				e.printStackTrace();
			}

			return 0;
		}
	};


	private LinearLayout mInviteOppoLayout;

	private static Button mKidUnReadBtn;

	private LinearLayout mZoneLayout2;

	private LinearLayout mFinanceRecorderLayout2;

	private LinearLayout mCalendarLayout2;

	private LinearLayout mMoreLayout2;

	private static Button mSpaceUnReadBtn2;

	private LinearLayout mCalendarAndZoneLayout;

	private LinearLayout mAccountingAndMoreLayout;

	private LinearLayout mCalendarAndAccountingLayout;

	private LinearLayout mMoreAndNullLayout;

	private TextView mChatText;

	private TextView mInviteOppoText;

	@Override
	protected void onDestroy() {
//		try {
//			CxResourceManager resourceManager = CxResourceManager.getInstance(
//					CxBaseSlidingMenu.this, "head", CxBaseSlidingMenu.this);
//			if (null != resourceManager) {
//				resourceManager.clearMemory();
//				resourceManager = null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		if (null != mGlobalObserver) {
			CxGlobalParams.getInstance().unRegisterObsercer(mGlobalObserver);
		}

		super.onDestroy();
	}

	protected void changeSlideStatus(boolean status) {
		CxGlobalParams.getInstance().setRecorderFlag(status);
	}

	// 更新未读消息状态
	private static void changeUnReadBtn() {
		if (CxGlobalParams.getInstance().getGroup() > 0) {
			mUnReadBtn.setVisibility(View.VISIBLE);
			mUnReadBtn.setText("" + CxGlobalParams.getInstance().getGroup());
		} else {
			mUnReadBtn.setVisibility(View.GONE);
		}
	}

	// 更新二人空间未读消息状态
	private static void changeSpaceUnReadBtn() {
		if (CxGlobalParams.getInstance().getSpaceTips() > 0) {
			mSpaceUnReadBtn.setVisibility(View.VISIBLE);
			mSpaceUnReadBtn.setText(""+ CxGlobalParams.getInstance().getSpaceTips());
			mSpaceUnReadBtn2.setVisibility(View.VISIBLE);
			mSpaceUnReadBtn2.setText(""+ CxGlobalParams.getInstance().getSpaceTips());
			
		} else {
			mSpaceUnReadBtn.setVisibility(View.GONE);
			mSpaceUnReadBtn2.setVisibility(View.GONE);
		}
	}
	
	private static void changeKidUnReadBtn() {
		if (CxGlobalParams.getInstance().getKid_tips() > 0) {
			mKidUnReadBtn.setVisibility(View.VISIBLE);
			mKidUnReadBtn.setText(""
					+ CxGlobalParams.getInstance().getKid_tips());
		} else {
			mKidUnReadBtn.setVisibility(View.GONE);
		}
		
	}
	
	/*class MyUnPairFuncAdapter extends BaseAdapter{

		private int []names = {R.string.cx_fa_unpair_chat_text, R.string.cx_fa_unpair_gallery_text, 
				R.string.cx_fa_unpair_jz_text, R.string.cx_fa_unpair_ml_text, 
				R.string.cx_fa_unpair_game_text, R.string.cx_fa_unpair_calendar_text};
		private int []icons = {R.drawable.menu_icon_chat, R.drawable.menu_icon_album,
				R.drawable.menu_icon_accounting, R.drawable.menu_icon_neighbor, 
				R.drawable.menu_icon_game, R.drawable.menu_icon_calendar};
		private GridView.LayoutParams mImageViewLayoutParams = new GridView.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		private int mItemHeight = 0;
		
		public void setItemHeight(int height){
			if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new GridView.LayoutParams(
            		LayoutParams.MATCH_PARENT, mItemHeight);
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
//			return 9;
			return 6;
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
		public int getViewTypeCount() {
//			return 2;
			return super.getViewTypeCount();
		}
		
		@Override
		public int getItemViewType(int position) {
//			return (position < 3 ? 1 : 0);
			return super.getItemViewType(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (position < 3) {
				if (convertView == null) {
                    convertView = new View(RkBaseSlidingMenu.this);
                }
                // Set empty view 
                convertView.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0));
                return convertView;
			}
			UnPairFuncHolder holder;
			if (null == convertView) {
				convertView = RkBaseSlidingMenu.this.getLayoutInflater()
				.inflate(R.layout.cx_fa_view_unpair_func_item, null);
				holder = new UnPairFuncHolder();
				holder.mIcon = (ImageView)convertView.findViewById(R.id.item_funcs_item_icon);
				holder.mName = (TextView)convertView.findViewById(R.id.item_funcs_item_name);
//				holder.mViewLayout.setLayoutParams(mImageViewLayoutParams);
				convertView.setLayoutParams(mImageViewLayoutParams);
				convertView.setTag(holder);
			}else{
				holder = (UnPairFuncHolder)convertView.getTag();
			}
			
			ImageView icon = holder.mIcon;
			TextView name = holder.mName;
			icon.setBackgroundResource(icons[position]);
			name.setText(names[position]);
			
			if (convertView.getLayoutParams().height != mItemHeight) {
				convertView.setLayoutParams(mImageViewLayoutParams);
			}
			
			return convertView;
		}
		
	}
	
	static class UnPairFuncHolder{
		public ImageView mIcon;
		public TextView mName;
	}*/
	
}
