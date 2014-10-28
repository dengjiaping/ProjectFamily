package com.chuxin.family.accounting;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuxin.family.accounting.CxAccountFragment.CurrentObserver;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.net.CxAccountingApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxAccountingParser;
import com.chuxin.family.parse.been.CxAccountDetailDayList;
import com.chuxin.family.parse.been.CxAccountDetailMonthList;
import com.chuxin.family.parse.been.data.AccountDetailDayData;
import com.chuxin.family.parse.been.data.AccountDetailMonthData;
import com.chuxin.family.parse.been.data.AccountDetailMonthItem;
import com.chuxin.family.parse.been.data.AccountHomeItem;
import com.chuxin.family.parse.been.data.DateData;
import com.chuxin.family.resource.CxResourceColor;
import com.chuxin.family.utils.DateUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.R;

/**
 * 记账明细类  该类逻辑比较复杂 看不懂的可以问我
 * @author wentong.men
 *
 */
public class CxAccountDetailActivity extends CxRootActivity {
	
	protected static final int MONTH_REFRESH = 0;
	protected static final int DAY_REFRESH = 1;
	private TextView yearText;
	private TextView yearIn;
	private TextView yearOut;
	private TextView yearSurplus;
	
	private Handler detailHandler;
	
	private DateData mDate; //存放当前日期的javabean
	
	private AccountDetailMonthData mMonthData;
	
	private DetailAdapter adapter;
	private ExpandableListView elv;
	
	private int yearNumber; //当前页面是哪一年 会变化
	
	private int monthNubmer;//当前年有多少个月要展示（除本年外的其他年都是12，本年的值会变化）
	
	private String tempMonth; //当前要展示哪个月的数据
	
	private int tempGroupPosition=-1; //当前要展示月在adapter的第几个条目
	
	private String tempAccontId=null; //要访问的账目ID
	
	private boolean monthIsFirst=false; //是第一次进来 刷新数据吗  如果是第一次会先调用本地数据库缓存
	
	private boolean monthIsDelete=false;//当监听者发现有账目被删除后会值为true，重新刷新数据
	
	private boolean yearIsUp=false; //当年增加时置为true；
	
	private boolean yearIsDown=false;//当年减少时置为true；
	
	
	private int type=0;  // 新增加的参数 用来选择是否只看自己的记账内容。 0 全部； 1 自己； 2014.02.15 wentong.men
	
//	private int tempCachePosition;
	
	private Map<Integer, Integer> tempCacheData =new HashMap<Integer, Integer>();//用来存放每年的哪个月是打开的状态，回到改年会继续打开
	
	
	private Map<Integer, List<AccountDetailDayData>> infos = new HashMap<Integer, List<AccountDetailDayData>>();//elv每个条目positon对应的子条目数据（网络）
	private Map<Integer, List<AccountDetailDayData>> nativeInfos = new HashMap<Integer, List<AccountDetailDayData>>();//elv每个条目positon对应的子条目数据（本地缓存）

	@Override
	protected void onCreate(Bundle arg0) {
	
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_activity_account_details);
		
		detailHandler=new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch(msg.what){
				case MONTH_REFRESH:
					monthRefresh();
					break;
				case DAY_REFRESH:
					adapter.updateAdapter(mMonthData.getList(), infos);
					break;
				default:
					break;
				}
			}
		};
		
		mUpdateObserver = new CurrentObserver();
		List<String> changeTags=new ArrayList<String>();
		changeTags.add(CxAccountingParam.ADD_ACCOUNT);
		changeTags.add(CxAccountingParam.UPDATE_ACCOUNT); 
		changeTags.add(CxAccountingParam.DELETE_ACCOUNT);
		mUpdateObserver.setListenTag(changeTags);
		mUpdateObserver.setMainThread(true);
		CxAccountingParam.getInstance().registerObserver(mUpdateObserver);//注册监听者，当账目被修改时刷新数据
		
		
		
		initTitle();	
		init();	
		
		monthIsFirst=true;		
		tempMonth=monthNubmer<10?"0"+monthNubmer:""+monthNubmer;
		
		
		CxAccountDetailMonthList list = CxAccountingApi.getInstance().requestNativeYearDetails(this, yearNumber+"");
		if(list!=null){
			mMonthData = list.getData();			
			if(mMonthData!=null){				
				detailHandler.sendEmptyMessage(MONTH_REFRESH);
			}		
		}//加载本地缓存年度明细
		CxLog.i("men", monthNubmer+"");
		tempGroupPosition=monthNubmer-mDate.getMonth()-1;				
		elv.expandGroup(tempGroupPosition);
		
		CxAccountDetailDayList dayList = CxAccountingApi.getInstance().requestNativeMonthDetails(CxAccountDetailActivity.this, yearNumber+tempMonth);
		if(dayList!=null){
			ArrayList<AccountDetailDayData> data = dayList.getData();			
			nativeInfos.put(tempGroupPosition, data);
			adapter.updateAdapter(mMonthData.getList(), nativeInfos);
		}	//加载本地缓存月度明细

		CxAccountingApi.getInstance().requestAccountYearDetails(this,yearNumber+"",type, yearCaller);//第一次网络请求		

	}
	

	 protected void monthRefresh() {//刷新年度数据
		 
		 yearText.setText(yearNumber+"年");
		 
		 if(mMonthData!=null){
			 yearIn.setText(mMonthData.getIn()+"元");
			 yearOut.setText(mMonthData.getOut()+"元");
			 yearSurplus.setText(mMonthData.getSurplus());
//			 adapter.updateAdapter(mMonthData.getList(), infos);
		 }else{
			 yearIn.setText("0元");
			 yearOut.setText("0元");
			 yearSurplus.setText("0"); 
			
//			 adapter.updateAdapter(null, infos); 
		 }
	
		
	}


	private void init() {
		 
		 mDate = DateUtil.getNumberTime(-1);//当前日期时间数据
		 
		 yearNumber=mDate.getYear();
		 monthNubmer=mDate.getMonth()+1;
		 
		 LinearLayout leftImage = (LinearLayout) findViewById(R.id.cx_fa_accounting_detail_img_left);
		 yearText = (TextView) findViewById(R.id.cx_fa_accounting_detail_tv_middle);
		 LinearLayout rightImage = (LinearLayout) findViewById(R.id.cx_fa_accounting_detail_img_right);
		
		 yearIn = (TextView) findViewById(R.id.cx_fa_accounting_detail_tv_year_in);
		 yearOut = (TextView) findViewById(R.id.cx_fa_accounting_detail_tv_year_out);
		 yearSurplus = (TextView) findViewById(R.id.cx_fa_accounting_detail_tv_year_surplus);
		 
		 elv = (ExpandableListView) findViewById(R.id.cx_fa_accounting_detail_elv);	 
		 elv.setGroupIndicator(null);
		 adapter = new DetailAdapter(this);
		 elv.setAdapter(adapter);
		 
		 elv.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				
//				if(elv.isGroupExpanded(groupPosition)){
//					tempGroupPosition=-1;
//					tempMonth=null;
//					return false;
//				}
				
				
				tempGroupPosition=groupPosition;//被点击条目positon
				
				
				if(mMonthData!=null && mMonthData.getList()!=null && mMonthData.getList().size()>0){//
					AccountDetailMonthItem item = mMonthData.getList().get(groupPosition);
					tempMonth=item.getMonth();
				}else{
					tempMonth=(monthNubmer-groupPosition)<10?"0"+(monthNubmer-groupPosition):""+(monthNubmer-groupPosition);
				}
								
				
				if(!infos.containsKey(groupPosition)){//没有网络缓存
					if(!nativeInfos.containsKey(groupPosition)){//没有本地缓存
						CxAccountDetailDayList list = CxAccountingApi.getInstance().requestNativeMonthDetails(CxAccountDetailActivity.this, yearNumber+tempMonth);
						if(list!=null){
							ArrayList<AccountDetailDayData> data = list.getData();			
							nativeInfos.put(tempGroupPosition, data);
							adapter.updateAdapter(mMonthData.getList(), nativeInfos);
						}
					}
					CxAccountingApi.getInstance().requestAccountMonthDetails(CxAccountDetailActivity.this,yearNumber+tempMonth,type, monthCaller);		
				}
						
				return false;
			}
		});
		 
		 
		 yearText.setText(yearNumber+"年");
		
		 leftImage.setOnClickListener(imgListener);
		 rightImage.setOnClickListener(imgListener);
		 

		
	}


	private void initTitle() {
		Button backBtn = (Button) findViewById(R.id.cx_fa_activity_title_back);
		typeBtn = (Button) findViewById(R.id.cx_fa_activity_title_more);
		TextView titleText = (TextView) findViewById(R.id.cx_fa_activity_title_info);
		
		titleText.setText(getString(R.string.cx_fa_accounting_homepage_detail));
		backBtn.setText(getString(R.string.cx_fa_navi_back));
		typeBtn.setVisibility(View.VISIBLE);
		typeBtn.setText(R.string.cx_fa_accounting_detail_title_type_btn_only);
		
		backBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				finish();				
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			}
		});
		
		typeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				typeBtn.setEnabled(false);
				if(type==0){
					type=1;
					typeBtn.setText(R.string.cx_fa_accounting_detail_title_type_btn_all);
				}else{
					type=0;
					typeBtn.setText(R.string.cx_fa_accounting_detail_title_type_btn_only);
				}
				
				CxAccountingApi.getInstance().requestAccountYearDetails(CxAccountDetailActivity.this,yearNumber+"",type, yearCaller);//第一次网络请求	
				elv.expandGroup(tempGroupPosition);
				CxAccountingApi.getInstance().requestAccountMonthDetails(CxAccountDetailActivity.this,yearNumber+tempMonth,type, monthCaller);
			}
		});
		
	}
	
	
	OnClickListener imgListener =new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			CxAccountDetailMonthList list=null;
			
			switch(v.getId()){
			case R.id.cx_fa_accounting_detail_img_left:
				yearIsDown=true;
				
				if(elv.isGroupExpanded(tempGroupPosition)){
					tempCacheData.put(yearNumber, tempGroupPosition);
				}else{
					tempCacheData.put(yearNumber, -1);
				}//离开改年时判断是否有某月是展开的
//				if(yearNumber==mDate.getYear()){
//					RkLog.i("men", ">>>>>>>>>>>>>>>>>>>>>>111111>>>>>>"+tempGroupPosition);
//					tempCachePosition=tempGroupPosition;
//				}
//				
				yearNumber--;
				if(yearNumber==mDate.getYear()){					
					monthNubmer=mDate.getMonth()+1;
				}else{
					monthNubmer=12;
				}
				tempMonth=null;
				tempGroupPosition=-1;
				infos.clear();
				nativeInfos.clear();//情况改年临时数据
				list = CxAccountingApi.getInstance().requestNativeYearDetails(CxAccountDetailActivity.this, yearNumber+"");
				if(list!=null){
					AccountDetailMonthData monthData = list.getData();			
					if(monthData!=null){	
						mMonthData=monthData;
						detailHandler.sendEmptyMessage(MONTH_REFRESH);
					}	
				}else{
					mMonthData=null;
					detailHandler.sendEmptyMessage(MONTH_REFRESH);
				}//请求本地缓存数据
				
				
				for(int i=0;i<adapter.getGroupCount();i++){
					if(elv.isGroupExpanded(i)){						
						elv.collapseGroup(i);
					}
				}//关闭所有条目
				
				CxAccountingApi.getInstance().requestAccountYearDetails(CxAccountDetailActivity.this,yearNumber+"",type, yearCaller);
				elv.startAnimation(AnimationUtils.loadAnimation(CxAccountDetailActivity.this, R.anim.tran_next_in));
				break;
			case R.id.cx_fa_accounting_detail_img_right:
				yearIsUp=true;
				if(elv.isGroupExpanded(tempGroupPosition)){
					tempCacheData.put(yearNumber, tempGroupPosition);
				}else{
					tempCacheData.put(yearNumber, -1);
				}
				
//				if(yearNumber==mDate.getYear()){	
//					RkLog.i("men", ">>>>>>>>>>>>>>>>>>>>>>22222>>>>>>"+tempGroupPosition);
//					tempCachePosition=tempGroupPosition;
//				}

				yearNumber++;
				if(yearNumber==mDate.getYear()){				
					monthNubmer=mDate.getMonth()+1;
				}else{
					monthNubmer=12;
				}
				tempMonth=null;
				tempGroupPosition=-1;
				infos.clear();
				nativeInfos.clear();
				list = CxAccountingApi.getInstance().requestNativeYearDetails(CxAccountDetailActivity.this, yearNumber+"");
				if(list!=null){
					AccountDetailMonthData monthData = list.getData();				
					if(monthData!=null){	
						mMonthData=monthData;
						detailHandler.sendEmptyMessage(MONTH_REFRESH);
					}		
				}else{
					mMonthData=null;
					detailHandler.sendEmptyMessage(MONTH_REFRESH);
				}
				
				CxLog.i("men", yearNumber+">>>>>>"+monthNubmer);
				for(int i=0;i<adapter.getGroupCount();i++){
					if(elv.isGroupExpanded(i)){
						elv.collapseGroup(i);
					}
				}
				CxAccountingApi.getInstance().requestAccountYearDetails(CxAccountDetailActivity.this,yearNumber+"",type, yearCaller);				
				elv.startAnimation(AnimationUtils.loadAnimation(CxAccountDetailActivity.this, R.anim.tran_pre_in));
				break;
			default:
				
				break;
				
			}
			
		}
	};
	
	JSONCaller yearCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			new Handler(getMainLooper()){
				public void handleMessage(Message msg) {
					typeBtn.setEnabled(true);
				};
			}.sendEmptyMessage(1);
				
			if(result==null){
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			
			CxAccountDetailMonthList list=null;
			try {
				list = (CxAccountDetailMonthList) result;
			} catch (Exception e) {
			}
			if(list==null || list.getRc()==408){
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
			AccountDetailMonthData monthData = list.getData();
			
			if(monthData==null){
				showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				return -4;
			}
			
			mMonthData=monthData;
			
			if(monthIsFirst){//如果是第一次进来，关闭本地缓存的条目，展开对应的条目
				 
				 monthIsFirst=false;
				 if(mMonthData.getList()!=null && mMonthData.getList().size()>0){					 
					 
					new Handler(getMainLooper()){
						public void handleMessage(Message msg) {
							monthNubmer=mMonthData.getList().size();
							CxLog.i("men",monthNubmer+">>>>>>>>>>>>" );
							elv.collapseGroup(tempGroupPosition);
							tempGroupPosition=monthNubmer-mDate.getMonth()-1;				
							elv.expandGroup(tempGroupPosition);
							CxLog.i("RkAccountDetailActivity_men",">>>>>>>>>>>>"+yearNumber+tempMonth );
							CxAccountingApi.getInstance().requestAccountMonthDetails(CxAccountDetailActivity.this,yearNumber+tempMonth,type, monthCaller);
						};
					}.sendEmptyMessage(1);					 
				 }
				 
			 }
			
			if(monthIsDelete){ //删除监听，展开对应条目重新请求数据	
				new Handler(getMainLooper()){
					public void handleMessage(Message msg) {
						monthIsDelete=false;
						infos.clear();
						nativeInfos.clear();
						elv.expandGroup(tempGroupPosition);
						AccountDetailMonthItem item = mMonthData.getList().get(tempGroupPosition);
						tempMonth=item.getMonth();				
						CxAccountingApi.getInstance().requestAccountMonthDetails(CxAccountDetailActivity.this,yearNumber+tempMonth,type, monthCaller);
					};
				}.sendEmptyMessage(1);
				
			}
			
			if(yearIsUp || yearIsDown){	
				yearIsUp=false;
				yearIsDown=false;

				//当年变化时，如过该年有条目应该是展开的则展开该条目。
				Integer tempNumber=tempCacheData.get(yearNumber);	
				CxLog.i("men", ">>>>>>>>>>>>>>>>>>>>>>4444>>>>>>"+(tempNumber==null));
				if(tempNumber!=null){
					CxLog.i("men", ">>>>>>>>>>>>>>>>>>>>>>4444>>>>>>"+tempNumber);
				}				
				if(tempNumber!=null && tempNumber>=0){
					if(monthData.getList()!=null && monthData.getList().size()>0){
						tempGroupPosition=tempNumber;
						AccountDetailMonthItem item = monthData.getList().get(tempGroupPosition);
						tempMonth=item.getMonth();	
						CxLog.i("men", ">>>>>>>>>>>>>>>>>>>>>>33333>>>>>>"+tempMonth);
						CxAccountingApi.getInstance().requestAccountMonthDetails(CxAccountDetailActivity.this,yearNumber+tempMonth,type, monthCaller);
						new Handler(getMainLooper()){
							public void handleMessage(Message msg) {
								elv.expandGroup(tempGroupPosition);
							};
						}.sendEmptyMessage(1);
					}else{
						detailHandler.sendEmptyMessage(DAY_REFRESH);
					}
				}else{
					detailHandler.sendEmptyMessage(DAY_REFRESH);
				}				
			}

			detailHandler.sendEmptyMessage(MONTH_REFRESH);		
			return 0;
		}
	};
	
	JSONCaller monthCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(result==null){
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			
			CxAccountDetailDayList list=null;
			try {
				list = (CxAccountDetailDayList) result;
			} catch (Exception e) {
			}
			if(list==null || list.getRc()==408){
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
			
			 ArrayList<AccountDetailDayData> data = list.getData();
			 
//			if(data==null){
//				return -4;
//			}
			
			infos.put(tempGroupPosition, data);
			
			
			detailHandler.sendEmptyMessage(DAY_REFRESH);
			
			
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
				ToastUtil.getSimpleToast(CxAccountDetailActivity.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
	
	
	
	private CurrentObserver mUpdateObserver;
	private Button typeBtn;

	
	
	private class DetailAdapter extends BaseExpandableListAdapter {
		
		private ArrayList<AccountDetailMonthItem> mGroupNames= new ArrayList<AccountDetailMonthItem>();
		private Map<Integer, List<AccountDetailDayData>> mChildrenInfos = new HashMap<Integer, List<AccountDetailDayData>>();
		
		private Context mContext;
		
		public DetailAdapter(Context context) {
			this.mContext=context;		
			for(int i=monthNubmer;i>0;i--){//默认展示到本月为止的数据
				AccountDetailMonthItem item=new AccountDetailMonthItem();
				item.setIn("0");
				item.setOut("0");
				item.setSurplus("0");
				item.setMonth(i+"");
				mGroupNames.add(item);				
			}
		}
		
		private void updateAdapter(ArrayList<AccountDetailMonthItem> groupNames,Map<Integer, List<AccountDetailDayData>> childrenInfos) {
			
			if(groupNames!=null && groupNames.size()>0){	
				monthNubmer=groupNames.size();
				this.mGroupNames=groupNames;
			}else{ //切换年时如回去本地数据失败则显示0
				mGroupNames.clear();
				for(int i=monthNubmer;i>0;i--){
					AccountDetailMonthItem item=new AccountDetailMonthItem();
					item.setIn("0");
					item.setOut("0");
					item.setSurplus("0");
					item.setMonth(i+"");
					mGroupNames.add(item);				
				}
			}
					
			this.mChildrenInfos=childrenInfos;
			
			this.notifyDataSetChanged();
		}
		

		@Override
		public int getGroupCount() {
			
			if(mGroupNames==null){
				return 0;
			}
			
			return mGroupNames.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			
			if(!mChildrenInfos.containsKey(groupPosition) || mChildrenInfos.get(groupPosition)==null){
				return 1;
			}
			
			return mChildrenInfos.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			
			return mGroupNames.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			
			return false;
		}
		
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			
			return false;
		}
		
		@Override
		public void onGroupExpanded(int groupPosition) {
//			super.onGroupExpanded(groupPosition);		
			for (int i = 0; i < adapter.getGroupCount(); i++) {
			    // ensure only one expanded Group exists at every time
			    if (groupPosition != i && elv.isGroupExpanded(groupPosition)) {
			        	elv.collapseGroup(i);
			    }
			}
//			elv.setSelectedGroup(groupPosition);
		}
		

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ViewHolder holder=null;
			if(convertView==null){
				convertView= LayoutInflater.from(mContext).inflate(R.layout.cx_fa_activity_account_details_month, null);
				holder=new ViewHolder();
				holder.monthMonth=(TextView) convertView.findViewById(R.id.cx_fa_accounting_detail_elv_month_tv_month);
				holder.monthMonth2=(TextView) convertView.findViewById(R.id.cx_fa_accounting_detail_elv_month_tv_month_2);
				holder.monthIn=(TextView) convertView.findViewById(R.id.cx_fa_accounting_detail_elv_month_tv_in);
				holder.monthOut=(TextView) convertView.findViewById(R.id.cx_fa_accounting_detail_elv_month_tv_out);
				holder.monthSurplus=(TextView) convertView.findViewById(R.id.cx_fa_accounting_detail_elv_month_tv_surplus);	
				holder.monthUpDown=(ImageView) convertView.findViewById(R.id.cx_fa_accounting_detail_elv_month_iv_updown);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			
			AccountDetailMonthItem item = mGroupNames.get(groupPosition);
			
			TextView monthMonth=holder.monthMonth;
			TextView monthMonth2=holder.monthMonth2;
			ImageView monthUpDown=holder.monthUpDown;
			
			String monthNumber=Integer.parseInt(item.getMonth())+"";
			
//			if(isExpanded){//判断是否展开来跟新月的颜色
//				monthMonth.setText(TextUtil.getNewSpanStr(monthNumber, 20, Color.rgb(235, 161, 121)));
//				monthMonth2.setText(TextUtil.getNewSpanStr("月", 15, Color.rgb(235, 161, 121)));
//			}else{
//				monthMonth.setText(TextUtil.getNewSpanStr(monthNumber, 20, Color.rgb(190, 190, 190)));
//				monthMonth2.setText(TextUtil.getNewSpanStr("月", 15, Color.rgb(190, 190, 190)));
//			}
			
			monthMonth.setText(TextUtil.getNewSpanStr(monthNumber, 20, Color.rgb(235, 161, 121)));
			monthMonth2.setText(TextUtil.getNewSpanStr("月", 15, Color.rgb(235, 161, 121)));
			
			
			if(isExpanded){//判断是否展开来跟新月的颜色
				monthUpDown.setImageResource(R.drawable.accounting_arrow_up);
			}else{
				monthUpDown.setImageResource(R.drawable.accounting_arrow_down);
			}
			
			
			
			holder.monthIn.setText(item.getIn()+"元");
			holder.monthOut.setText(item.getOut()+"元");
			holder.monthSurplus.setText(item.getSurplus()+"元");		
			
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
	
			convertView= LayoutInflater.from(mContext).inflate(R.layout.cx_fa_activity_account_details_day, null);
			TextView dayDay=(TextView) convertView.findViewById(R.id.cx_fa_accounting_detail_elv_day_tv_day);
			TextView dayWeekday=(TextView) convertView.findViewById(R.id.cx_fa_accounting_detail_elv_day_tv_weekday);
			LinearLayout dayLayout=(LinearLayout) convertView.findViewById(R.id.cx_fa_accounting_detail_elv_day_items_layout);
				
			List<AccountDetailDayData> list = mChildrenInfos.get(groupPosition);
			if(list==null){//没有数据时
				View view = LayoutInflater.from(mContext).inflate(R.layout.cx_fa_activity_account_details_day_no_content, null);				
				return view;
			}
			AccountDetailDayData data = list.get(childPosition);
			String date = data.getDate();			
			DateData dateData = DateUtil.getNumberTime(yearNumber+tempMonth+date);
			
			dayDay.setText(dateData.getDay()+"");
			dayWeekday.setText(DateUtil.getCatipalNumber(dateData.getWeekday()));
			
			ArrayList<String> items = data.getList();	
			CxAccountingParser parser=new CxAccountingParser();
			dayLayout.removeAllViews();
			for(int i=0;i<items.size();i++){
				String str = items.get(i);
				final AccountHomeItem item = parser.getAccountItem(str);
				
				View view = LayoutInflater.from(mContext).inflate(R.layout.cx_fa_activity_account_details_day_item, null);
				TextView money = (TextView) view.findViewById(R.id.cx_fa_accounting_detail_elv_day_item_tv_money);
				ImageView mAuthor = (ImageView) view.findViewById(R.id.cx_fa_accounting_detail_elv_day_item_iv_author);
				TextView category = (TextView) view.findViewById(R.id.cx_fa_accounting_detail_elv_day_item_tv_category);
				View line =  view.findViewById(R.id.cx_fa_accounting_detail_elv_day_item_line);
//				LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.cx_fa_accounting_detail_elv_day_item_layout);
				
				String author = item.getAuthor();
				if(author.equals(CxGlobalParams.getInstance().getUserId())){
					mAuthor.setBackgroundResource(CxResourceColor.getInstance().co_accounting_account_item_pink);
				}else{
					mAuthor.setBackgroundResource(CxResourceColor.getInstance().co_accounting_account_item_blue);
				}
				
				if(i==items.size()-1){
					line.setVisibility(View.GONE);
				}
				money.setText((item.getType()==1?"支出":"收入")+item.getMoney()+"元");
				category.setText(TextUtil.numberToCategory(mContext, item.getCategory()));
				final String str2=str;
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						tempAccontId=item.getId();
						Intent intent=new Intent(mContext, CxChangeAccountActivity.class);
						intent.putExtra(CxAccountingParam.ACCOUNT_CONTENT,str2);
						startActivity(intent);
						CxAccountDetailActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
					}
				});
				
				dayLayout.addView(view);
			}
			

			return convertView;
		}

		
		
		
		
	}
	
	
	static class ViewHolder{
		TextView  monthMonth;
		TextView  monthMonth2;
		TextView  monthIn;
		TextView  monthOut;
		TextView  monthSurplus;
		ImageView monthUpDown;
		
		TextView  dayDay;
		TextView  dayWeekday;	
		LinearLayout dayLayout;	
	}
	
	class CurrentObserver extends CxObserverInterface {

		/* 主要事项有：1、新的分享 2、头像（中、小） */
		@Override
		public void receiveUpdate(String actionTag) {
			if (null == actionTag) {
				return;
			}
			CxAccountingParam param = CxAccountingParam.getInstance();
			
			//修改监听
			if(actionTag.equalsIgnoreCase(CxAccountingParam.UPDATE_ACCOUNT)){

				CxLog.i("men", ">>>>>>>>>>>>>>>>>>>>>>3");
				CxAccountingApi.getInstance().requestAccountYearDetails(CxAccountDetailActivity.this,yearNumber+"",type, yearCaller);
				elv.expandGroup(tempGroupPosition);
				CxAccountingApi.getInstance().requestAccountMonthDetails(CxAccountDetailActivity.this,yearNumber+tempMonth,type, monthCaller);
				return;
			}
			//删除监听
			if( actionTag.equalsIgnoreCase(CxAccountingParam.DELETE_ACCOUNT)){
				
				String account = param.getDelAccount(); 
				if(account.equals(tempAccontId)){
					monthIsDelete=true;
					CxLog.i("men", ">>>>>>>>>>>>>>>>>>>>>>1");
					CxAccountingApi.getInstance().requestAccountYearDetails(CxAccountDetailActivity.this,yearNumber+"",type, yearCaller);	
				}
				return;
			}
			
		}
	}
	
	@Override
	protected void onDestroy() {
		if(mUpdateObserver!=null){
			CxAccountingParam.getInstance().unRegisterObsercer(mUpdateObserver);
			mUpdateObserver=null;
		}
		if(infos!=null){
			infos.clear();
			infos=null;
		}
		if(nativeInfos!=null){
			nativeInfos.clear();
			nativeInfos=null;
		}
		if(mMonthData!=null){
			mMonthData=null;
		}
		super.onDestroy();
	}
	
	

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
			if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
				finish();				
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				return false;
			}
			return super.onKeyDown(keyCode, event);
		};

}
