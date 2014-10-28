package com.chuxin.family.accounting;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.Toast;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.neighbour.CxNbNeighboursHome;
import com.chuxin.family.neighbour.CxNeighbourList;
import com.chuxin.family.net.CxAccountingApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxAccountingParser;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.data.AccountHomeItem;
import com.chuxin.family.parse.been.data.DateData;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.DateUtil;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.R;

/**
 * 记账的添加和修改删除类
 * @author wentong.men
 *
 */
public class CxChangeAccountActivity extends CxRootActivity {
	
	public enum AccountMode{
		ADD_MODE,UPDATE_MODE
	}

	private int type; //支出还是收入 1 支出，2收入
	
	private AccountMode mode;//是增加还是修改删除
	
	private String accountId; //账目id
	
	private String tempCategoryOut; //支出类别缓存
	private String tempCategoryIn; //收入类别缓存
	
	private String tempAuthor; //记账人
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_activity_account_change_account);
		
		initTitle();
		
		init();
		
	}

	private void init() {
		final InputMethodManager input = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		tempAuthor=CxGlobalParams.getInstance().getUserId();//默认自己
		
		type=1;	//默认1	
		date = DateUtil.getNumberTime(-1);//当前日期时间类
		
		AccountHomeItem item=null;
		
		Intent intent = getIntent();
		String extra = intent.getStringExtra(CxAccountingParam.ACCOUNT_CONTENT);
		
		if(TextUtils.isEmpty(extra)){//记账不传参数
			mode=AccountMode.ADD_MODE;
		}else{
			mode=AccountMode.UPDATE_MODE;
			CxAccountingParser parser=new CxAccountingParser();
			item = parser.getAccountItem(extra);
		}
		
		
		tvOut = (TextView) findViewById(R.id.cx_fa_accounting_account_tv_out);
		tvIn = (TextView) findViewById(R.id.cx_fa_accounting_account_tv_in);
		tvMoney = (EditText) findViewById(R.id.cx_fa_accounting_account_money_tv);
		tvCategory = (TextView) findViewById(R.id.cx_fa_accounting_account_category_tv);
		tvFrom = (TextView) findViewById(R.id.cx_fa_accounting_account_from_tv);
		tvDate = (TextView) findViewById(R.id.cx_fa_accounting_account_date_tv);
		tvRemark = (TextView) findViewById(R.id.cx_fa_accounting_account_remark_tv);
		
		
		LinearLayout layoutMoney = (LinearLayout) findViewById(R.id.cx_fa_accounting_account_money_layout);
		LinearLayout layoutCategory = (LinearLayout) findViewById(R.id.cx_fa_accounting_account_category_layout);
		LinearLayout layoutFrom = (LinearLayout) findViewById(R.id.cx_fa_accounting_account_from_layout);
		LinearLayout layoutDate = (LinearLayout) findViewById(R.id.cx_fa_accounting_account_date_layout);
		layoutOut = (LinearLayout) findViewById(R.id.cx_fa_accounting_account_out_layout);
		layoutIn = (LinearLayout) findViewById(R.id.cx_fa_accounting_account_in_layout);
		
		Button delBtn= (Button) findViewById(R.id.cx_fa_accounting_change_account_delete);
		
	
		if(mode==AccountMode.UPDATE_MODE){//删改模式
			
			accountId=item.getId();
			
			if(1==item.getType()){
				type=1;
				layoutOut.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
				tvOut.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_out), 18, Color.rgb(235, 161, 121)));
				layoutIn.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
				tvIn.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_in), 16, Color.rgb(55, 50, 47)));
			}else{
				type=2;
				layoutIn.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
				tvIn.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_in), 18, Color.rgb(235, 161, 121)));
				layoutOut.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
				tvOut.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_out), 16, Color.rgb(55, 50, 47)));
			}
			
			int from = item.getFrom();
			tempAuthor=item.getAuthor();
			if(item.getAuthor().equals(CxGlobalParams.getInstance().getPartnerId())){//如果记账人是对方，则修改  来自 
				if(from==1){
					from=2;
				}else if(from==2){
					from=1;
				}
				titleText.setText(CxResourceString.getInstance().str_accounting_account_title_opposite_account);
				delBtn.setVisibility(View.GONE);
			}else{
				titleText.setText(getString(R.string.cx_fa_accounting_account_title_edit_account));
				delBtn.setVisibility(View.VISIBLE);
			}
	
			tvFrom.setText(TextUtil.numberToFrom(this, from));
			tvMoney.setText(TextUtil.getNewSpanStr(TextUtil.transforToMoney(item.getMoney()), 20, Color.RED));
			tvCategory.setText(TextUtil.numberToCategory(this, item.getCategory()));	
			
			String tempdate=item.getDate();
			tvDate.setText(tempdate.substring(0,4)+"-"+tempdate.substring(4,6)+"-"+tempdate.substring(6,8));
			tvRemark.setText(item.getDesc());
//			tvDate.setText(item.getDate());
			tvMoney.setFocusable(false);
//			input.hideSoftInputFromWindow(tvMoney.getWindowToken(), 0);
			
		}else if(mode==AccountMode.ADD_MODE){//增加模式
			titleText.setText(getString(R.string.cx_fa_accounting_homepage_add_account));
			delBtn.setVisibility(View.GONE);
			type=1;
			layoutOut.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
			tvOut.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_out), 18, Color.rgb(235, 161, 121)));
			layoutIn.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
			tvIn.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_in), 16, Color.rgb(55, 50, 47)));
//			tvMoney.setText(TextUtil.getNewSpanStr("0", 16, Color.RED));
			tvCategory.setText(getString(R.string.cx_fa_accounting_category_out_athome_short));
			tvFrom.setText(getString(R.string.cx_fa_accounting_account_me));	
			tvDate.setText(date.getDateStr());
		
			input.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);//弹出键盘
			tvMoney.setFocusable(true);
			tvMoney.requestFocus();		
			tvMoney.requestFocusFromTouch();

		}
//		tvMoney.setInputType(InputType.TYPE_CLASS_PHONE );
		tvMoney.setSelection(tvMoney.getText().toString().length());
		tvMoney.setCursorVisible(false);	
		tvMoney.setHint(TextUtil.getNewSpanStr("0", 20, Color.RED).toString());
//		tvMoney.addTextChangedListener(textListener);
		
		
		tvMoney.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					input.hideSoftInputFromWindow(tvMoney.getWindowToken(), 0);
				}
			}
		});
		
//		tvMoney.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				tvMoney.setFocusable(true);
//				tvMoney.requestFocus();
//				tvMoney.requestFocusFromTouch();
//				tvMoney.setSelection(tvMoney.getText().toString().length());
//				tvMoney.setCursorVisible(false);
//				input.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//			}
//		});
		
		layoutMoney.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tvMoney.setFocusable(true);
				tvMoney.requestFocus();
				tvMoney.requestFocusFromTouch();
				tvMoney.setSelection(tvMoney.getText().toString().length());
				tvMoney.setCursorVisible(false);
				input.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				
			}
		});
		delBtn.setOnClickListener(contentListener);	
		layoutCategory.setOnClickListener(contentListener);
		layoutFrom.setOnClickListener(contentListener);
		layoutDate.setOnClickListener(contentListener);
		layoutOut.setOnClickListener(contentListener);
		layoutIn.setOnClickListener(contentListener);
		
	}

	//初始化标题栏
	private void initTitle() {
		Button backBtn = (Button) findViewById(R.id.cx_fa_activity_title_back);
		Button saveBtn = (Button) findViewById(R.id.cx_fa_activity_title_more);
		titleText = (TextView) findViewById(R.id.cx_fa_activity_title_info);
		
//		titleText.setText(getString(R.string.cx_fa_accounting_homepage_add_account));
		backBtn.setText(getString(R.string.cx_fa_navi_back));
		saveBtn.setText(getString(R.string.cx_fa_save_text));
		saveBtn.setVisibility(View.VISIBLE);
		
		backBtn.setOnClickListener(titleListener);
		saveBtn.setOnClickListener(titleListener);
		
	}
	
	OnClickListener contentListener =new OnClickListener() {
		@Override
		public void onClick(View v) {
			tvMoney.setFocusable(false);
			switch(v.getId()){
			case R.id.cx_fa_accounting_change_account_delete:
				
				DialogUtil du = DialogUtil.getInstance();
				du.getSimpleDialog(CxChangeAccountActivity.this, null,getString(R.string.cx_fa_accounting_account_sure_delete), null, null).show();
				du.setOnSureClickListener(new OnSureClickListener() {
					
					@Override
					public void surePress() {
						try {
							DialogUtil.getInstance().getLoadingDialogShow(CxChangeAccountActivity.this, -1);
							CxAccountingApi.getInstance().requestAccountDelete(accountId, delCaller);
						} catch (Exception e) {
							DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
							e.printStackTrace();
						}
					}
				});	
				
				break;
			case R.id.cx_fa_accounting_account_out_layout:
				outOnClick();
				break;
			case R.id.cx_fa_accounting_account_in_layout:
				inOnClick();
				break;
			case R.id.cx_fa_accounting_account_money_layout:
//				tvMoney.setFocusable(true);
//				tvMoney.requestFocus();
//				showMoneyDialog();
				break;
			case R.id.cx_fa_accounting_account_category_layout:
				showCategoryOrFromDialog(1);
				break;
			case R.id.cx_fa_accounting_account_from_layout:
				showCategoryOrFromDialog(2);
				break;
			case R.id.cx_fa_accounting_account_date_layout:
				showDateDialog();
				break;
			default:
				break;
			}
			
		}
	};
	
	
	OnClickListener titleListener =new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			tvMoney.setFocusable(false);
			switch(v.getId()){
			case R.id.cx_fa_activity_title_back:
				back();
				break;
			case R.id.cx_fa_activity_title_more:
				save();
				break;
			default:
				break;
			}
			
		}
	};
	
	protected void save() {
		
		if(mode==AccountMode.UPDATE_MODE && tempAuthor.equals(CxGlobalParams.getInstance().getPartnerId())){
			ToastUtil.getSimpleToast(this, -1, getString(R.string.cx_fa_accounting_account_save_opposite_fail), 1).show();
			return;
		}
		String money = tvMoney.getText().toString().trim().replace(",", "");
		if(money.equals("") || money.equals("0")){
			ToastUtil.getSimpleToast(this, -1, getString(R.string.cx_fa_accounting_account_save_nomoney), 1).show();
			return;
		}
		if(money.startsWith("-")){
			ToastUtil.getSimpleToast(CxChangeAccountActivity.this, -1, getString(R.string.cx_fa_accounting_account_save_fuzhi), 1).show();
			return ;
		}
		
//		if(money.contains(" ")){
//			ToastUtil.getSimpleToast(RkChangeAccountActivity.this, -1, "请不要输入负值", 1).show();
//			return ;
//		}
		
		if(!money.startsWith("+") && (money.contains("-") || money.contains("+"))){
			ToastUtil.getSimpleToast(CxChangeAccountActivity.this, -1, getString(R.string.cx_fa_accounting_account_save_zhengfuhao), 1).show();
			return;
		}
		
		if(money.contains(".")){
			ToastUtil.getSimpleToast(CxChangeAccountActivity.this, -1, getString(R.string.cx_fa_accounting_account_save_point), 1).show();
			return ;
		}	
		
		
		if((!money.startsWith("+") && money.length()>9) || money.length()>10){
			ToastUtil.getSimpleToast(CxChangeAccountActivity.this, -1, getString(R.string.cx_fa_accounting_account_save_big), 1).show();
			tvMoney.setText(TextUtil.getNewSpanStr("0", 20, Color.RED).toString());
			return;
		}
		
		
		String sendMoney = money.replace("+", "").replace(" ", "")+"00";
		int category = TextUtil.categoryToNumber(this, tvCategory.getText().toString().trim());
		int from = TextUtil.fromToNumber(this, tvFrom.getText().toString().trim());
		String date = tvDate.getText().toString().trim().replaceAll("-", "");
		String desc=tvRemark.getText().toString().trim();
		try {
			DialogUtil.getInstance().getLoadingDialogShow(CxChangeAccountActivity.this, -1);
			CxAccountingApi.getInstance().requestAccountAddOrUpdate(accountId, type, sendMoney, category, from, date, desc, updateCaller);
		} catch (Exception e) {
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
			e.printStackTrace();
		}
		
		
	}
	
	

	protected void back() {
		if(mode==AccountMode.ADD_MODE){
			if(!tvMoney.getText().toString().trim().equals("0") && !tvMoney.getText().toString().trim().equals("")){
				DialogUtil du = DialogUtil.getInstance();
				du.setOnSureClickListener(new OnSureClickListener() {
					
					@Override
					public void surePress() {
						finish();
						overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
					}
				});	
				du.getSimpleDialog(this, null, getString(R.string.cx_fa_accounting_account_sure_leave), null, null).show();
				return;
			}
		}
		finish();				
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		
	}

	private TextView tvOut;
	private TextView tvIn;
	private EditText tvMoney;
	private TextView tvCategory;
	private TextView tvFrom;
	private TextView tvDate;
	private TextView tvRemark;
	private DateData date;
	
	
	//已废弃不用
//	protected void showMoneyDialog() {
//		View view = View.inflate(this, R.layout.cx_fa_widget_accounting_money_dialog,null);
//		final EditText moneyEt = (EditText) view.findViewById(R.id.cx_fa_accounting_change_account_money_dialog_et);
//		Button cancelBtn = (Button) view.findViewById(R.id.cx_fa_accounting_change_account_money_dialog_cancel);
//		Button okBtn = (Button) view.findViewById(R.id.cx_fa_accounting_change_account_money_dialog_ok);
//		
//		final Dialog dialog=new Dialog(this, R.style.simple_dialog);		
//		dialog.setContentView(view);	
//		cancelBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();				
//			}
//		});
//		okBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if(TextUtils.isEmpty(moneyEt.getText().toString().trim())){
//					ToastUtil.getSimpleToast(RkChangeAccountActivity.this, -1, "你还没有输入金额！", 1).show();
////					moneyEt.setHint("你还没有输入金额！");
//				}else {
//					
//					String money= moneyEt.getText().toString().trim();
//					money=TextUtil.transforToMoney(money);
//					if(TextUtils.isEmpty(money)){
//						ToastUtil.getSimpleToast(RkChangeAccountActivity.this, -1, "输入金额过大,最大单位为亿。", 1).show();
//						tvMoney.setText(TextUtil.getNewSpanStr("0", 14, Color.RED).toString());
//					}else{
//						if(money.contains(".")){
//							ToastUtil.getSimpleToast(RkChangeAccountActivity.this, -1, "最小单位为元,请不要输入小数点。", 1).show();
//							return ;
//						}
//						dialog.dismiss();
//						tvMoney.setText(TextUtil.getNewSpanStr(money, 14, Color.RED).toString());
//					}					
//				}			
//			}
//		});
//		dialog.show();
//	}
	
	
	//日期dialog
	protected void showDateDialog() {
		View view = View.inflate(this, R.layout.cx_fa_widget_accounting_date_dialog,null);
		final DatePicker dateDp = (DatePicker) view.findViewById(R.id.cx_fa_accounting_change_account_date_dialog_dp);
		Button cancelBtn = (Button) view.findViewById(R.id.cx_fa_accounting_change_account_date_dialog_cancel);
		Button okBtn = (Button) view.findViewById(R.id.cx_fa_accounting_change_account_date_dialog_ok);
		
		final Calendar c1 = Calendar.getInstance();	
		
		String dateStr = tvDate.getText().toString().trim();
		String[] split = dateStr.split("-");
		
		dateDp.init(Integer.parseInt(split[0]),
				Integer.parseInt(split[1])-1,
				Integer.parseInt(split[2]), new OnDateChangedListener(){

            public void onDateChanged(DatePicker view, int year,
                    int monthOfYear, int dayOfMonth) {
            	
            	c1.set(Calendar.YEAR, year);
            	c1.set(Calendar.MONTH, monthOfYear);
            	c1.set(Calendar.DAY_OF_MONTH, dayOfMonth);                     	
            }
        });
		
		EditText monthE = (EditText)((ViewGroup)((ViewGroup) ((ViewGroup) dateDp.getChildAt(0)).getChildAt(0)).getChildAt(1)).getChildAt(1);	
		EditText yearE = (EditText)((ViewGroup)((ViewGroup) ((ViewGroup) dateDp.getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(1);	
		EditText dayE = (EditText)((ViewGroup)((ViewGroup) ((ViewGroup) dateDp.getChildAt(0)).getChildAt(0)).getChildAt(2)).getChildAt(1);	

		if(yearE != null) {
			yearE.setTextSize(16);
		}
		if(monthE != null) {
			monthE.setTextSize(16);
		}
		if(dayE != null) {
			dayE.setTextSize(16);
		}
		
		
		final Dialog dialog=new Dialog(this, R.style.simple_dialog);		
		dialog.setContentView(view);	
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();				
			}
		});
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final DateData newDate=DateUtil.getNumberTime(c1.getTimeInMillis());
			
//				if(newDate.getYear()>date.getYear() || (newDate.getYear()==date.getYear() && newDate.getMonth()>date.getMonth()) 
//						|| (newDate.getYear()==date.getYear() && newDate.getMonth()==date.getMonth() && newDate.getDay()>date.getDay())){				
//					
//					ToastUtil.getSimpleToast(RkChangeAccountActivity.this, -1, "今天"+date.getDay()+"号,日期不能超过今天", 1).show();
//					return ;
//				}
				
				dialog.dismiss();
				tvDate.setText(newDate.getDateStr());
						
			}
		});
		dialog.show();
	}


	private Dialog dialog;

	protected void showCategoryOrFromDialog(int cateOrFrom) {
		
		View view = View.inflate(this, R.layout.cx_fa_widget_accounting_categary_and_from_dialog,null);
		LinearLayout out = (LinearLayout) view.findViewById(R.id.cx_fa_accounting_change_account_categary_out);
		LinearLayout in = (LinearLayout) view.findViewById(R.id.cx_fa_accounting_change_account_categary_in);
		LinearLayout from = (LinearLayout) view.findViewById(R.id.cx_fa_accounting_change_account_from);
		
		Button cancelBtn = (Button) view.findViewById(R.id.cx_fa_accounting_change_account_category_dialog_cancel);
		
		if(cateOrFrom==1){//1则弹出类别
			from.setVisibility(View.GONE);
			if(type==1){
				in.setVisibility(View.GONE);
				out.setVisibility(View.VISIBLE);
			}else if(type==2){
				out.setVisibility(View.GONE);
				in.setVisibility(View.VISIBLE);
			}
		}else if(cateOrFrom==2){//2则弹出from
			out.setVisibility(View.GONE);
			in.setVisibility(View.GONE);
			from.setVisibility(View.VISIBLE);
		}
		
		
		
		TextView out1= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_categary_dialog_out1);
		TextView out2= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_categary_dialog_out2);
		TextView out3= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_categary_dialog_out3);
		TextView out4= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_categary_dialog_out4);
		TextView out5= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_categary_dialog_out5);
		TextView out6= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_categary_dialog_out6);
		TextView out7= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_categary_dialog_out7);
		TextView out8= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_categary_dialog_out8);
		
		TextView in1= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_categary_dialog_in1);
		TextView in2= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_categary_dialog_in2);
		TextView in3= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_categary_dialog_in3);
		TextView in4= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_categary_dialog_in4);
		
		TextView me= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_from_me);
		TextView opposite= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_from_opposite);
		TextView both= (TextView) view.findViewById(R.id.cx_fa_accounting_change_account_from_both);
		
			
		
		out1.setOnClickListener(categoryListener);
		out2.setOnClickListener(categoryListener);
		out3.setOnClickListener(categoryListener);
		out4.setOnClickListener(categoryListener);
		out5.setOnClickListener(categoryListener);
		out6.setOnClickListener(categoryListener);
		out7.setOnClickListener(categoryListener);
		out8.setOnClickListener(categoryListener);
		
		in1.setOnClickListener(categoryListener);
		in2.setOnClickListener(categoryListener);
		in3.setOnClickListener(categoryListener);
		in4.setOnClickListener(categoryListener);	
		
		me.setOnClickListener(fromListener);
		opposite.setOnClickListener(fromListener);		
		both.setOnClickListener(fromListener);
		
		cancelBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				dialog.dismiss();			
			}
		});
		
		dialog=new Dialog(this, R.style.simple_dialog);		
		dialog.setContentView(view);
		dialog.show();
		
		
	}
	//来自点击
	OnClickListener fromListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dialog.dismiss();
			String from="";
			
			switch (v.getId()) {
			case R.id.cx_fa_accounting_change_account_from_me:
				from=getString(R.string.cx_fa_accounting_account_me);
				break;
			case R.id.cx_fa_accounting_change_account_from_opposite:
				from=getString(CxResourceString.getInstance().str_pair);
				break;
			case R.id.cx_fa_accounting_change_account_from_both:
				from=getString(R.string.cx_fa_accounting_account_both);
				break;
			default:
				break;
			}
			tvFrom.setText(from);
		}
	};
	
	//类别点击
	OnClickListener categoryListener =new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			dialog.dismiss();
			String category="";
			
			switch(v.getId()){		
			case R.id.cx_fa_accounting_change_account_categary_dialog_out1:
				category=getString(R.string.cx_fa_accounting_category_out_athome_short);
				break;
			case R.id.cx_fa_accounting_change_account_categary_dialog_out2:
				category=getString(R.string.cx_fa_accounting_category_out_traffic_short);
				break;
			case R.id.cx_fa_accounting_change_account_categary_dialog_out3:
				category=getString(R.string.cx_fa_accounting_category_out_catering_short);
				break;
			case R.id.cx_fa_accounting_change_account_categary_dialog_out4:
				category=getString(R.string.cx_fa_accounting_category_out_entertainment_short);
				break;
			case R.id.cx_fa_accounting_change_account_categary_dialog_out5:
				category=getString(R.string.cx_fa_accounting_category_out_shopping_short);
				break;
			case R.id.cx_fa_accounting_change_account_categary_dialog_out6:
				category=getString(R.string.cx_fa_accounting_category_out_education_short);
				break;
			case R.id.cx_fa_accounting_change_account_categary_dialog_out7:
				category=getString(R.string.cx_fa_accounting_category_out_financial_short);
				break;
			case R.id.cx_fa_accounting_change_account_categary_dialog_out8:
				category=getString(R.string.cx_fa_accounting_category_out_gratitude_short);
				break;
			case R.id.cx_fa_accounting_change_account_categary_dialog_in1:
				category=getString(R.string.cx_fa_accounting_category_in_wages_short);
				break;
			case R.id.cx_fa_accounting_change_account_categary_dialog_in2:
				category=getString(R.string.cx_fa_accounting_category_in_award_short);
				break;
			case R.id.cx_fa_accounting_change_account_categary_dialog_in3:
				category=getString(R.string.cx_fa_accounting_category_in_investment_short);
				break;
			case R.id.cx_fa_accounting_change_account_categary_dialog_in4:
				category=getString(R.string.cx_fa_accounting_category_in_other_short);
				break;
			}			
			tvCategory.setText(category);

		}
	};
	
	//点击支出
	protected void outOnClick() {
		type=1;
		layoutOut.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
		tvOut.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_out), 18, Color.rgb(235, 161, 121)));
		layoutIn.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
		tvIn.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_in), 16, Color.rgb(55, 50, 47)));
		
		tempCategoryIn=tvCategory.getText().toString().trim();
		if(!TextUtils.isEmpty(tempCategoryOut)){
			tvCategory.setText(tempCategoryOut);
		}else{
			tvCategory.setText(getString(R.string.cx_fa_accounting_category_out_athome_short));
		}
		
		
	}
	//点击收入
	protected void inOnClick() {
		type=2;
		layoutIn.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
		tvIn.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_in), 18, Color.rgb(235, 161, 121)));
		layoutOut.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
		tvOut.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_out), 16, Color.rgb(55, 50, 47)));
		
		tempCategoryOut=tvCategory.getText().toString().trim();
		if(!TextUtils.isEmpty(tempCategoryIn)){
			tvCategory.setText(tempCategoryIn);
		}else{
			tvCategory.setText(getString(R.string.cx_fa_accounting_category_in_wages_short));
		}
		
	}
	
	
	JSONCaller delCaller = new JSONCaller() {
		
		@Override
		public int call(Object result) {
			
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
			
			if(result==null){
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			
			CxParseBasic del=null;
			try {
				del=(CxParseBasic) result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(del==null || del.getRc()==408){
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			int rc=del.getRc();
			if(0!=rc){
				if(TextUtils.isEmpty(del.getMsg())){
					showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(del.getMsg(),0);
				}
				return rc;
			}
			CxLog.i("men", ">>>>>>>>>>>>>>>>>>>2");
			CxAccountingParam.getInstance().setDelAccount(accountId);		
			finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			
			
			return 0;
		}
	};
	
	JSONCaller updateCaller = new JSONCaller() {
		
		@Override
		public int call(Object result) {
			
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
			
			if(result==null){
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			
			CxParseBasic del=null;
			try {
				del=(CxParseBasic) result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(del==null || del.getRc()==408){
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			int rc=del.getRc();
			if(0!=rc){
				if(TextUtils.isEmpty(del.getMsg())){
					showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(del.getMsg(),0);
				}
				return rc;
			}
			
			CxAccountingParam.getInstance().setUpdateAccount("");
			finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	
			return 0;
		}
	};

	private LinearLayout layoutOut;

	private LinearLayout layoutIn;

	private Dialog fromDialog;

	private TextView titleText;
	
	/**
	 * 
	 * @param info
	 * @param number 0 失败；1 成功；2 不要图。
	 */
	private void showResponseToast(String info,int number) {
		Message msg = new Message();
		msg.obj = info;
		msg.arg1=number;
		new Handler(CxChangeAccountActivity.this.getMainLooper()) {
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
				ToastUtil.getSimpleToast(CxChangeAccountActivity.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}

	
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		date=null;
	}
	
	
	 public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			back();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};
	
	
	
	
	TextWatcher textListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				if(s.length()>0){
					tvMoney.setHint("");
				}else{
					tvMoney.setHint(TextUtil.getNewSpanStr("0", 20, Color.RED).toString());
				}
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {	
		}
		@Override
		public void afterTextChanged(Editable s) {		
		}
	};
	
	
	
	
}
