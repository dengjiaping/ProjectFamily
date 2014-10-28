package com.chuxin.family.accounting;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import com.chuxin.family.app.CxApplication;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.neighbour.CxNeighbourParam;
import com.chuxin.family.net.CxAccountingApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxAccountHomeList;
import com.chuxin.family.parse.been.data.AccountHomeData;
import com.chuxin.family.parse.been.data.AccountHomeItem;
import com.chuxin.family.resource.CxResourceColor;
import com.chuxin.family.utils.DateUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.R;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


/**
 * 记账首页
 * @author wentong.men
 *
 */
public class CxAccountFragment extends Fragment {
	
	protected static final int NET_REFRESH = 0;
	private TextView monthIn;
	private TextView monthOut;
	private TextView monthSurplus;
	private TextView yearSurplus;
	
	private AccountHomeData homeData;
	
	private Handler homeHandler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		
		((CxMain)getActivity()).closeMenu();
		
		homeHandler=new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch(msg.what){
				case NET_REFRESH:
					
					netRefresh();
					
					break;
				default:
					break;
				}
			}
		};
		
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View inflate = inflater.inflate(R.layout.cx_fa_activity_account_homepage, null);
		
		Button backBtn = (Button) inflate.findViewById(R.id.cx_fa_activity_title_back);
		TextView titleText = (TextView) inflate.findViewById(R.id.cx_fa_activity_title_info);
		titleText.setText(getResources().getString(R.string.cx_fa_accounting_homepage_title));
		backBtn.setBackgroundResource(R.drawable.cx_fa_menu_btn);
		backBtn.setOnClickListener(titleListener);
		
		initDate(inflate);

		init(inflate);
	
		CxAccountHomeList list = CxAccountingApi.getInstance().requestNativeHomeList(getActivity());
		if(list!=null){
			homeData= list.getData();		
			homeHandler.sendEmptyMessage(NET_REFRESH);
		}//本地缓存
		
		
		CxAccountingApi.getInstance().requestAccountList(dataCaller, getActivity());//第一次网络请求
		
		return inflate;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	private void init(View inflate) {
		
		
		mUpdateObserver = new CurrentObserver();
		List<String> changeTags=new ArrayList<String>();
		changeTags.add(CxAccountingParam.ADD_ACCOUNT);
		changeTags.add(CxAccountingParam.UPDATE_ACCOUNT);
		changeTags.add(CxAccountingParam.DELETE_ACCOUNT);
		mUpdateObserver.setListenTag(changeTags);
		mUpdateObserver.setMainThread(true);
		CxAccountingParam.getInstance().registerObserver(mUpdateObserver);//注册观察者，账目的增删改
	
		
		monthIn = (TextView) inflate.findViewById(R.id.cx_fa_accounting_homepage_tv_month_in);
		monthOut = (TextView) inflate.findViewById(R.id.cx_fa_accounting_homepage_tv_month_out);
		monthSurplus = (TextView) inflate.findViewById(R.id.cx_fa_accounting_homepage_tv_month_surplus);
		yearSurplus = (TextView) inflate.findViewById(R.id.cx_fa_accounting_homepage_tv_year_surplus);
		
		
		
		TextView pie = (TextView) inflate.findViewById(R.id.cx_fa_accounting_homepage_tv_pie);
		Button addAccount = (Button) inflate.findViewById(R.id.cx_fa_accounting_homepage_btn_add_account);
		TextView detailMore = (TextView) inflate.findViewById(R.id.cx_fa_accounting_homepage_detail_more);
		TextView detail = (TextView) inflate.findViewById(R.id.cx_fa_accounting_homepage_tv_detail);
		
		layoutDetail = (LinearLayout) inflate.findViewById(R.id.cx_fa_accounting_homepage_detail_items_layout);		
		LinearLayout layoutPie = (LinearLayout) inflate.findViewById(R.id.cx_fa_accounting_homepage_pie_layout);
		LinearLayout layoutDet = (LinearLayout) inflate.findViewById(R.id.cx_fa_accounting_homepage_detail_layout);
		
//		pie.setOnClickListener(btnLintener);
//		detail.setOnClickListener(btnLintener);	
		addAccount.setOnClickListener(btnLintener);
		detailMore.setOnClickListener(btnLintener);
		layoutPie.setOnClickListener(btnLintener);
		layoutDet.setOnClickListener(btnLintener);
		
	}



	//初始化日期
	private void initDate(View inflate) {
		
		TextView monthText = (TextView) inflate.findViewById(R.id.cx_fa_accounting_homepage_title_tv_year_and_month);
		TextView dayText = (TextView) inflate.findViewById(R.id.cx_fa_accounting_homepage_title_tv_day);
		TextView weekText = (TextView) inflate.findViewById(R.id.cx_fa_accounting_homepage_title_tv_week);
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH)+1;
		day = c.get(Calendar.DAY_OF_MONTH);
		int week = c.get(Calendar.DAY_OF_WEEK);
		
		monthText.setText(year+"年"+month+"月");
		dayText.setText(day+"");
		weekText.setText(DateUtil.getCatipalNumber(week));
		
	}
	
	OnClickListener btnLintener =new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.cx_fa_accounting_homepage_pie_layout:
				Intent intent4=new Intent(getActivity(), CxAccountChatPie.class);
				startActivity(intent4);
				break;
			case R.id.cx_fa_accounting_homepage_btn_add_account:
				Intent intent=new Intent(getActivity(), CxChangeAccountActivity.class);
				startActivity(intent);
				break;
			case R.id.cx_fa_accounting_homepage_detail_more:
				Intent intent2=new Intent(getActivity(), CxAccountDetailActivity.class);
				startActivity(intent2);				
				break;
			case R.id.cx_fa_accounting_homepage_detail_layout:
				Intent intent3=new Intent(getActivity(), CxAccountDetailActivity.class);
				startActivity(intent3);
				break;
			default:
				break;
			}
			
			getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
			
		}
	};
	
	

	OnClickListener titleListener =new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.cx_fa_activity_title_back:
				try {
					((CxMain) getActivity()).toggleMenu();
				} catch (Exception e) {
				}
				break;
			default:
				break;
			}
			
		}
	};
	
	JSONCaller dataCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(null==result){
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null), 0);
				return -1;
			}
			CxAccountHomeList list=null;
			try {
				list = (CxAccountHomeList) result;
			} catch (Exception e) {
			}
			if (null == list || list.getRc()==408) {
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null), 0);
				return -2;
			}
			
			int rc = list.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(list.getMsg())){
					showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(list.getMsg(),0);
				}
				return -3;
			}

			homeData= list.getData();
			
			homeHandler.sendEmptyMessage(NET_REFRESH);
			
			
			return 0;
		}
	};
	private int month;
	private int day;
//	private AccountAdapter adapter;
	private CurrentObserver mUpdateObserver;
	private LinearLayout layoutDetail;
	private int year;
	
	
	private void netRefresh() {
		if(homeData==null){
			return;
		}
		if(!TextUtils.isEmpty(homeData.getMonthIn())){
			monthIn.setText(TextUtil.transforToMoney(homeData.getMonthIn())+"元");
		}else{
			monthIn.setText("0元");
		}
		
		if(!TextUtils.isEmpty(homeData.getMonthOut())){
			monthOut.setText(TextUtil.transforToMoney(homeData.getMonthOut())+"元");
		}else{
			monthOut.setText("0元");
		}
		
		if(!TextUtils.isEmpty(homeData.getMonthSurplus())){
			monthSurplus.setText(TextUtil.transforToMoney(homeData.getMonthSurplus())+"元");
		}else{
			monthSurplus.setText("0元");
		}
		
		if(!TextUtils.isEmpty(homeData.getYearSurplus())){
			yearSurplus.setText(TextUtil.transforToMoney(homeData.getYearSurplus())+"元");
		}else{
			yearSurplus.setText("0元");
		}

		
		ArrayList<AccountHomeItem> list = homeData.getList();
		
		if(list==null || list.size()<1){
			return;
		}else{
//			System.out.println(list.size());
			updateDetail(list);
		}	
		
	};
	
	//刷新明细条目
	private void updateDetail(ArrayList<AccountHomeItem> list) {
		layoutDetail.removeAllViews();
		for(int i=0;i<list.size();i++){
			
			final AccountHomeItem item = list.get(i);
			if(getActivity()==null){
				break;
			}
			View view = View.inflate(getActivity(), R.layout.cx_fa_activity_account_homepage_item, null);

			TextView mDate = (TextView) view.findViewById(R.id.cx_fa_accounting_homepage_detail_tv_date);
			TextView mContent = (TextView) view.findViewById(R.id.cx_fa_accounting_homepage_detail_tv_content);
			TextView mCategory = (TextView) view.findViewById(R.id.cx_fa_accounting_homepage_detail_tv_type);
			ImageView mAuthor = (ImageView) view.findViewById(R.id.cx_fa_accounting_homepage_detail_iv_author);
			
			String dateStr="";
			String date = item.getDate();
			int mYear=Integer.parseInt(date.substring(0, 4));
			int mMonth =Integer.parseInt(date.substring(4, 6));
			int mDay =Integer.parseInt(date.substring(6));
			if(mYear==year && mMonth==month){
				if(mDay==day){
					dateStr="今天";
				}else if((day-mDay)==1){
					dateStr="昨天";
				}else{
					dateStr=mMonth+"月"+mDay+"日";
				}
			}else{
				dateStr=mMonth+"月"+mDay+"日";
			}
			
			String author = item.getAuthor();
			if(author.equals(CxGlobalParams.getInstance().getUserId())){
//				System.out.println(getResources().getColor(R.color.cx_fa_role_co_accounting_account_item_pink));
				mAuthor.setBackgroundResource(CxResourceColor.getInstance().co_accounting_account_item_pink);
			}else{
				mAuthor.setBackgroundResource(CxResourceColor.getInstance().co_accounting_account_item_blue);
			}
			
	
			mDate.setText(dateStr);
			String contentStr="";
			String categoryStr="";
			int type = item.getType();
			int category = item.getCategory();
			if(type==1){
				contentStr+="支出"+item.getMoney()+"元";
				categoryStr=TextUtil.numberToCategory(getActivity(), category);
				
			}else if(type==2){
				contentStr+="收入"+item.getMoney()+"元";
				categoryStr=TextUtil.numberToCategory(getActivity(), category);
			}
			mContent.setText(contentStr);
			mCategory.setText(categoryStr);
			
			view.setBackgroundResource(R.drawable.cx_fa_neighbour_reply_background);
			
			view.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(getActivity(), CxChangeAccountActivity.class);
					intent.putExtra(CxAccountingParam.ACCOUNT_CONTENT,item.getDataStr());
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);				
				}
			});
			
			layoutDetail.addView(view);
			
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
		new Handler(CxApplication.getInstance().getMainLooper()) {
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
				ToastUtil.getSimpleToast(getActivity(), id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
	

	
	@Override
	public void onDestroy() {
		if(mUpdateObserver!=null){
			CxAccountingParam.getInstance().unRegisterObsercer(mUpdateObserver);
			mUpdateObserver=null;
		}
		super.onDestroy();
	}
	
	class CurrentObserver extends CxObserverInterface {

		/* 主要事项有：1、新的分享 2、头像（中、小） */
		@Override
		public void receiveUpdate(String actionTag) {
			if (null == actionTag) {
				return;
			}
//			CxAccountingParam param = CxAccountingParam.getInstance();

//			if (actionTag.equalsIgnoreCase(RkServiceParams.GROUP)) { // 对方发帖子成功时long polling告诉更新
//				int group = RkServiceParams.getInstance().getGroup();
//				if(group>0){
//					if (isFirstComplete && isDownComplete) {
//						isDownComplete = false;
//						RkNeighbourApi.getInstance().requestInvitationList(0, 15,
//								new InvitationResponse(true, false), getActivity());
//					}
//				}
//				return;
//			}
			if(actionTag.equalsIgnoreCase(CxAccountingParam.ADD_ACCOUNT) || actionTag.equalsIgnoreCase(CxAccountingParam.UPDATE_ACCOUNT) 
					|| actionTag.equalsIgnoreCase(CxAccountingParam.DELETE_ACCOUNT)){
				CxAccountingApi.getInstance().requestAccountList(dataCaller, getActivity());
            }
			
		}
	}
	
	
	
	
	
	

}
