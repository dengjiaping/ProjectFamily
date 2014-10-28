package com.chuxin.family.accounting;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.models.AccountChart;
import com.chuxin.family.models.Model;
import com.chuxin.family.net.CxAccountingApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxAccountingParser;
import com.chuxin.family.parse.been.CxAccountPieList;
import com.chuxin.family.parse.been.data.AccountPieItem;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.views.chat.ChatFragment;
import com.chuxin.family.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
/**
 * 
 * @author shichao.wang
 *
 */
public class CxAccountChatPie extends CxRootActivity{

    private Button mBackButton;
    private TextView mTitle;
    private static int[] COLORS = new int[] {R.color.cx_fa_co_accounting_green , R.color.cx_fa_co_accounting_cyan, R.color.cx_fa_co_accounting_yellow,
        R.color.cx_fa_co_accounting_pink, R.color.cx_fa_co_accounting_orange,  R.color.cx_fa_co_accounting_blue, R.color.cx_fa_co_accounting_purple, 
        R.color.cx_fa_co_accounting_red, R.color.cx_fa_co_accounting_red,
        R.color.cx_fa_co_accounting_yellow, R.color.cx_fa_co_accounting_purple, R.color.cx_fa_co_accounting_orange
        };
//    private static String[] CATEGORYS = new String[] { "餐饮零食", "休闲娱乐", "人情往来", "商品购物", "金融保险", "医疗教育", "交通通讯" ,"居家物业" };
    /** The main series that will include all the data. */
    private CategorySeries mSeries = new CategorySeries("");
    /** The main renderer for the main dataset. */
    private DefaultRenderer mRenderer = new DefaultRenderer();
    
    /** The chart view that displays the data. */
    private GraphicalView mChartView;
    
    private static int[] sCategoryValues = null;

    private static String[] sCategoryTexts = null;

    private TextView mDateTextView;
    private String mCurrentDateString;
    private LinearLayout mLeftButton, mRightButton; // 日期加减
    private Date mCurrentDate;
    private SimpleDateFormat mSdf;
    private TextView mSpendButton, mEarnButton; // 支出收入按钮
    
    private final int UPDATE_SPEND_DATA = 1; // 更新支出数据
    private final int UPDATE_EARN_DATA = 2; // 更新收入数据
    
    private Button mSendChartButton; // 发送饼图
    
    private LinearLayout mChartWholeView;
    private LinearLayout mChartLinearLayout;
    
    private final int mSpendType = 1; // 支出
    private final int mEarnType = 2;// 收入
    private int mCurrentType = 1;
    private String mCurrentDateText;
    
    private int type=0; // 新增加的参数 用来选择是否只看自己的记账内容。 0 全部； 1 自己；  2014.02.15 wentong.men
    
//    private int tempCurrentType=0;
    
    private Handler mAccountChartHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case UPDATE_SPEND_DATA:
                    requestNetData();
                    break;
                case UPDATE_EARN_DATA:
                    requestNetData();
                    break;
            }
        }
        
    };
    
    public static int[] getCategoryValues(Resources res) {
        if (sCategoryValues == null) {
            sCategoryValues = res.getIntArray(R.array.cx_fa_ints_accounting_category);
        }
        return sCategoryValues;
    }

    public static String[] getCategoryTexts(Resources res) {
        if (sCategoryTexts == null) {
            sCategoryTexts = res.getStringArray(R.array.cx_fa_strs_accounting_category);
        }
        return sCategoryTexts;
    }
    
    @SuppressLint("SimpleDateFormat")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle outState) {
        super.onCreate(outState);
        setContentView(R.layout.cx_fa_activity_account_pie);
        mTitle = (TextView)findViewById(R.id.cx_fa_activity_title_info);
        mTitle.setText(R.string.cx_fa_accounting_homepage_pie);
        mBackButton = (Button)findViewById(R.id.cx_fa_activity_title_back);
        mBackButton.setText(getString(R.string.cx_fa_navi_back));
        mBackButton.setOnClickListener(mBtnListener);
        typeBtn = (Button) findViewById(R.id.cx_fa_activity_title_more);
        typeBtn.setVisibility(View.VISIBLE);
        typeBtn.setText(R.string.cx_fa_accounting_detail_title_type_btn_only);
        typeBtn.setOnClickListener(mBtnListener);
        
        mLeftButton = (LinearLayout)findViewById(R.id.cx_fa_accounting_pie_title_iv_left);
        mRightButton = (LinearLayout)findViewById(R.id.cx_fa_accounting_pie_title_iv_right);
        mLeftButton.setOnClickListener(mBtnListener);
        mRightButton.setOnClickListener(mBtnListener);
        
        
        outLayout = (LinearLayout) findViewById(R.id.cx_fa_accounting_pie_out_layout);
        inLayout = (LinearLayout) findViewById(R.id.cx_fa_accounting_pie_in_layout);
        mSpendButton = (TextView)findViewById(R.id.cx_fa_accounting_pie_tv_out);
        mEarnButton = (TextView)findViewById(R.id.cx_fa_accounting_pie_tv_in);
        outLayout.setOnClickListener(mBtnListener);
        inLayout.setOnClickListener(mBtnListener);
        
        mSendChartButton = (Button)findViewById(R.id.cx_fa_accounting_pie_btn_send);
        mSendChartButton.setText(CxResourceString.getInstance().str_accounting_pie_send_chart_to_partner);
        mSendChartButton.setOnClickListener(mBtnListener);
        
        mChartWholeView = (LinearLayout)findViewById(R.id.cx_fa_accounting_chart_image_layout);
        
        mDateTextView = (TextView)findViewById(R.id.cx_fa_accounting_pie_title_tv);
        
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        
        String monthStr=month>9?month+"":"0"+month;
        
        String date = "" + year + monthStr;
        mCurrentDateString = date;
        mSdf = new  SimpleDateFormat( "yyyyMM" );
        try {
            mCurrentDate = mSdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String format = getString(R.string.cx_fa_account_chart_date_formatted);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCurrentDate);
        String dateText = String.format(format, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1);
        mCurrentDateText = dateText;
        mDateTextView.setText(dateText);
        
        mChartLinearLayout = (LinearLayout) findViewById(R.id.chart);
        
        
        mRenderer.setZoomButtonsVisible(false); // 设置是否显示放大缩小饼图功能
        mRenderer.setStartAngle(180); // 设置旋转角度
        mRenderer.setDisplayValues(false); //设置是否在饼图里显示值
        mRenderer.setShowLabels(false); // 设置显示标签
        mRenderer.setShowLegend(true); // 设置是否显示图列
        mRenderer.setPanEnabled(false); // 是否可以拖动
        mRenderer.setFitLegend(true);
        mRenderer.setScale(1.2f);
        mRenderer.setClickEnabled(false);
        mRenderer.setLegendHeight(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_accounting_account_pie_legend_height));
        mRenderer.setLegendShapeWidth(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_accounting_account_pie_legend_shape_width));
        mRenderer.setLegendTextSize(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_accounting_account_pie_legend_tv_size));
        mRenderer.setLabelsColor(getResources().getColor(R.color.cx_fa_co_accounting_chartpie_lable_text_color)); // 设置显示标签的字体颜色
        
//        mRenderer.setLabelsTextSize(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_accounting_account_pie_tv_size)); // 设置显示标签的文本字体大小
        getCategoryValues(getResources());
        getCategoryTexts(getResources());
        if (mChartView == null) {
            mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
            mRenderer.setClickEnabled(true);
            mChartLinearLayout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
	        mChartView.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		          SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
		          if (seriesSelection == null) {              
		            for (int i = 0; i < mSeries.getItemCount(); i++) {
		                  mRenderer.getSeriesRendererAt(i).setHighlighted(false);
		            }
		            mChartView.repaint();
		          }else {
		             for (int i = 0; i < mSeries.getItemCount(); i++) {
		                mRenderer.getSeriesRendererAt(i).setHighlighted(i == seriesSelection.getPointIndex());
		             }
		             mChartView.repaint();
		
		          }
		        }
	        });
        } else {
            mChartView.repaint();
        }
        //clearAllData();
        loadData();
        
    }
    
    private void loadData(){
        requestNetData();
    }
    
    /**
     * 加载本地数据库支出饼图数据
     */
    private void loadSpendLocalData(Object obj){
        mSeries.clear();
        mChartView.repaint();
        
        if(type==1){
        	CxLog.i("RkAccountChatPie_men", ">>>>>>>>>>>>>>>>>>>>>>>out>>>"+type);
        	CxAccountPieList accounts = new CxAccountingParser().getAccountPieList((JSONObject)obj);        	
            if(accounts==null || null == accounts.getItems() || accounts.getItems().size() == 0){
                greyChartPie();
                return;
            }
//            mRenderer.setShowLabels(false);
//            mRenderer.setShowLegend(true); // 设置是否显示图列
//            mRenderer.setLegendHeight(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_accounting_account_pie_legend_height));
            mRenderer.removeAllRenderers();
            resetRenders();
            boolean hasData=false;
            for(int i=0;i<accounts.getItems().size();i++){
            	AccountPieItem item = accounts.getItems().get(i);
            	int type = item.getType();
                int category = item.getCategory();              
                String money = item.getMoney();
                if(1 == type){
                	hasData=true;
                    addData(category, money);
                }
                
            } 
            if(!hasData){
            	greyChartPie();
            }
            return ;
        }
        
        List<Model> accounts = new AccountChart(null, CxAccountChatPie.this).gets("flag=1", new String[] {}, null, 0, 0);
        if(null == accounts || accounts.size() == 0){
            greyChartPie();
            return;
        }
//        mRenderer.setShowLabels(false);
//        mRenderer.setShowLegend(true); // 设置是否显示图列
//        mRenderer.setLegendHeight(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_accounting_account_pie_legend_height));
        mRenderer.removeAllRenderers();
        resetRenders();
        Iterator<Model> i = accounts.iterator();
        while(i.hasNext()){
            AccountChart account = (AccountChart)i.next();
            int type = account.getAccountType();
            int category = account.getAccountCategory();
            String money = account.getAccountMoney();
            if(1 == type){
                addData(category, money);
            }
        }
    }
    
    /**
     * 加载本地数据库收入饼图数据
     */
    private void loadEarnLocalData(Object obj){
        mSeries.clear();
        mChartView.repaint();
        
        if(type==1){
        	CxLog.i("RkAccountChatPie_men", ">>>>>>>>>>>>>>>>>>>>>>>in>>>"+type);
        	CxAccountPieList accounts = new CxAccountingParser().getAccountPieList((JSONObject)obj);
            if(accounts==null || null == accounts.getItems() || accounts.getItems().size() == 0){
                greyChartPie();
                return;
            }
//            mRenderer.setShowLabels(false);
//            mRenderer.setShowLegend(true); // 设置是否显示图列
//            mRenderer.setLegendHeight(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_accounting_account_pie_legend_height));
            mRenderer.removeAllRenderers();
            resetRenders();
            boolean hasData=false;
            for(int i=0;i<accounts.getItems().size();i++){
            	AccountPieItem item = accounts.getItems().get(i);
            	int type = item.getType();
                int category = item.getCategory();
                String money = item.getMoney();
                if(2 == type){
                	hasData=true;
                    addData(category, money);
                }
            }
            if(!hasData){
            	greyChartPie();
            }
            
            return ;
        }
        
        
        List<Model> accounts = new AccountChart(null, CxAccountChatPie.this).gets("flag=2", new String[] {}, null, 0, 0);
        if(null == accounts || accounts.size() == 0){
            greyChartPie();
            return;
        }
//        mRenderer.setShowLabels(true);
//        mRenderer.setShowLegend(true); // 设置是否显示图列
//        mRenderer.setLegendHeight(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_accounting_account_pie_legend_height));
        mRenderer.removeAllRenderers();
        resetRenders();
        Iterator<Model> i = accounts.iterator();
        while(i.hasNext()){
            AccountChart account = (AccountChart)i.next();
            int type = account.getAccountType();
            int category = account.getAccountCategory();
            String money = account.getAccountMoney();
            if(2 == type){
                addData(category, money);
            }
        }
    }
    
    /**
     * 网络请求饼图数据
     */
    private void requestNetData(){
        CxAccountingApi.getInstance().requestAccountChart(mCurrentDateString,type, new JSONCaller(){
                    
                    @Override
                    public int call(Object result) {
                        updateAccounts(result);
                        if(mCurrentType == mSpendType){
                            loadSpendLocalData(result);
                        } else if(mCurrentType == mEarnType) {
                            loadEarnLocalData(result);
                        }
                        return 0;
                    }
                });
    }
    
    /**
     * 更新饼图数据库
     * @param result
     */
    public void updateAccounts(Object result){
    	if(type==1){
    		return;
    	}
        clearAllData();
        if(null == result){
        	showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
            return;
        }
        
        JSONArray accounts;
        try {
            accounts = (JSONArray)((JSONObject)result).getJSONArray("data");
            List<Model> accountLoacl = new AccountChart(null, CxAccountChatPie.this).gets("1=1", new String[] {}, null, 0, 0);
            if(null == accounts || accounts.length() == 0){
            	showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
                return;
            }
            if(null != accountLoacl){
                if(accounts.toString().equals(accountLoacl.toString())){ // 网络数据没有更新，不再存储
                    return;
                }
            }
           
            for(int i = 0; i < accounts.length(); i++){
                try {
                    JSONObject accountObj = accounts.getJSONObject(i);
                    AccountChart account = new AccountChart(accountObj, CxAccountChatPie.this);
                    account.mFlag = accountObj.getInt(AccountChart.TAG_TYPE);
                    account.put();
                } catch (JSONException e) {
                    CxLog.e("updateAccounts", "" + e.getMessage());
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
        }
        
    }
    
    private void clearAllData(){
        AccountChart accountcharts = new AccountChart(null, CxAccountChatPie.this);
        accountcharts.dropAll();
    }
    
    /**
     * 往饼图中添加数据并重绘
     * @param category 类型
     * @param value  值
     */
    public void addData(int category, String v){
//        System.out.println("mSeries.getItemCount()==" + mSeries.getItemCount());
        double value = 0;
        try {
          value = Double.parseDouble(v);
        } catch (NumberFormatException e) {
          return;
        }
        for(int i = 0; i < sCategoryValues.length; i++){
            if(category == sCategoryValues[i]){
//              mSeries.add(CATEGORYS[mSeries.getItemCount()], value);
//                System.out.println("sCategoryTexts[i]" + sCategoryTexts[i]);
                mSeries.add(sCategoryTexts[i], value);
                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
//                renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
//                System.out.println("mSeries.getItemCount()1==" + mSeries.getItemCount());
//                System.out.println("COLORS[i]" + COLORS[i]);
                renderer.setLegendTextColor(getResources().getColor(R.color.cx_fa_co_accounting_chartpie_lable_text_color)); // 设置显示标签的字体颜色
                renderer.setColor(getResources().getColor(COLORS[i]));
                renderer.setShowLegendItem(true);
                mRenderer.addSeriesRenderer(renderer);
                mChartView.repaint();
                break;
            }
        }
        
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
      super.onRestoreInstanceState(savedState);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onResume() {
      super.onResume();
    }
    
    protected void onPause() {
    	super.onPause();
    	overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    };
    
    
    OnClickListener mBtnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_activity_title_back: // 返回
                    CxAccountChatPie.this.finish();
                    break;
                case R.id.cx_fa_activity_title_more: // 全家和个人模式切换
                	if(type==0){
    					type=1;
    					typeBtn.setText(R.string.cx_fa_accounting_detail_title_type_btn_all);
    				}else{
    					type=0;
    					typeBtn.setText(R.string.cx_fa_accounting_detail_title_type_btn_only);
    				}
                	requestNetData();
                	break;
                case R.id.cx_fa_accounting_pie_title_iv_left: // 日期减
                    updateDate(-1);
                    break;
                case R.id.cx_fa_accounting_pie_title_iv_right: // 日期加
                    updateDate(1);
                    break;
                case R.id.cx_fa_accounting_pie_out_layout: // 支出
                	if(mCurrentType==mSpendType){
                		return;
                	}               	
                    mCurrentType = mSpendType;
                    outOnClick();
                   Message msgSpend = Message.obtain(mAccountChartHandler, UPDATE_SPEND_DATA);
                   msgSpend.sendToTarget();
                    break;
                case R.id.cx_fa_accounting_pie_in_layout: // 收入
                	if(mCurrentType==mEarnType){
                		return;
                	}
                    mCurrentType = mEarnType;
                    inOnClick();
                    Message msgEarn = Message.obtain(mAccountChartHandler, UPDATE_EARN_DATA);
                    msgEarn.sendToTarget();
                    break;
                case R.id.cx_fa_accounting_pie_btn_send:
                    try {
                        String folderName = "chuxin/sysimage";
                        String fileName = System.currentTimeMillis() + ".jpgbak";
                        File path = Environment.getExternalStorageDirectory();
                        File file = new File(path, folderName + "/" + fileName);
                        if (!file.exists()) {
                            file.getParentFile().mkdirs();
                        }
                        FileOutputStream fos = new FileOutputStream(file);
                        FileOutputStream fileOutputStream = null;
                            file.getParentFile().mkdirs();
                            fileOutputStream = new FileOutputStream(file);
                            if(null != getBitmapFromView(mChartWholeView)){
                                if(getBitmapFromView(mChartWholeView).compress(CompressFormat.JPEG, 100, fileOutputStream)){
                                    fileOutputStream.flush();
                                }
                            }
                            final Uri uri = Uri.fromFile(file);
                        // 发送饼图到聊天界面
                            String format = getString(CxResourceString.getInstance().str_accounting_account_tip);
                            String type = "";
                            if(mCurrentType == mSpendType){
                                type = getString(R.string.cx_fa_accounting_out);
                            } else {
                                type = getString(R.string.cx_fa_accounting_in);
                            }
                            String msg = String.format(format, mCurrentDateText, type);
                            ChatFragment.getInstance().sendMessage(msg, 0);
                            ChatFragment.getInstance().sendMessage(uri.toString(), 3);
                            ToastUtil.getSimpleToast(CxAccountChatPie.this, -2, "转发成功", 1).show();
                        fos.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }

        }
    };
	private LinearLayout outLayout;
	private LinearLayout inLayout;
	private Button typeBtn;
    
    /**
     * 日期改变更新饼图数据
     * @param day
     */
    private void updateDate(int day){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(mCurrentDate);
        rightNow.add(Calendar.MONTH, day);
        Date date = rightNow.getTime();
        mCurrentDate = date;
        mCurrentDateString = mSdf.format(date);
        String format = getString(R.string.cx_fa_account_chart_date_formatted);
        String dateText = String.format(format, rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH)+1);
        mDateTextView.setText(dateText);
        if(mCurrentType == mSpendType){
            Message msgSpend = Message.obtain(mAccountChartHandler, UPDATE_SPEND_DATA);
            msgSpend.sendToTarget();
        } else if(mCurrentType == mEarnType){
            Message msgEarn = Message.obtain(mAccountChartHandler, UPDATE_EARN_DATA);
            msgEarn.sendToTarget();
        }
    }
    
    /**
     * 转换生成饼图图片
     * @param view
     * @return
     */
    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) 
            bgDrawable.draw(canvas);
        else 
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }
    
    protected void outOnClick() {
    	
    	outLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
    	mSpendButton.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_out), 18, Color.rgb(235, 161, 121)));
		inLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
		mEarnButton.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_in), 16, Color.rgb(55, 50, 47)));
    	
  
        
    }

    protected void inOnClick() {
    	
    	outLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
    	mSpendButton.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_out), 16, Color.rgb(55, 50, 47)));
		inLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
		mEarnButton.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_accounting_in), 18, Color.rgb(235, 161, 121)));
    	
    }
    
    /**
     * 没有数据时生成一个浅灰色的圆形
     */
    
    private void greyChartPie(){
        mSeries.clear();
        mRenderer.removeAllRenderers();
        mRenderer.setShowLabels(false); // 设置显示标签
        mRenderer.setShowLegend(false); // 设置是否显示图列
        mRenderer.setFitLegend(false);
        mRenderer.setLegendHeight(0);
        mSeries.add("", 100);
        SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
        renderer.setColor(getResources().getColor(R.color.cx_fa_co_grey));
        mRenderer.addSeriesRenderer(renderer);
        mChartView.repaint();
    }
    
    private void resetRenders(){
        mRenderer.setZoomButtonsVisible(false); // 设置是否显示放大缩小饼图功能
        mRenderer.setStartAngle(180); // 设置旋转角度
        mRenderer.setDisplayValues(false); //设置是否在饼图里显示值
        mRenderer.setShowLabels(false); // 设置显示标签
        mRenderer.setShowLegend(true); // 设置是否显示图列
        mRenderer.setPanEnabled(false); // 是否可以拖动
        mRenderer.setFitLegend(true);
        mRenderer.setScale(1.2f);
        mRenderer.setClickEnabled(false);
        mRenderer.setLegendHeight(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_accounting_account_pie_legend_height));
        mRenderer.setLegendShapeWidth(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_accounting_account_pie_legend_shape_width));
        mRenderer.setLegendTextSize(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_accounting_account_pie_legend_tv_size));
        mRenderer.setLabelsColor(getResources().getColor(R.color.cx_fa_co_accounting_chartpie_lable_text_color)); // 设置显示标签的字体颜色
        
//        mRenderer.setLabelsTextSize(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_accounting_account_pie_tv_size)); // 设置显示标签的文本字体大小
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
		new Handler(CxAccountChatPie.this.getMainLooper()) {
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
				ToastUtil.getSimpleToast(CxAccountChatPie.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
    
    
}
