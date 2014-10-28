package com.chuxin.family.tabloid;


import java.util.List;

import com.chuxin.family.app.CxApplication;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.neighbour.answer.CxAnswerQuestionActivity;
import com.chuxin.family.net.CxTabloidApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.TabloidParse;
import com.chuxin.family.parse.been.CxTabloid;
import com.chuxin.family.parse.been.CxTabloidCateConf;
import com.chuxin.family.parse.been.data.TabloidCateConfData;
import com.chuxin.family.parse.been.data.TabloidCateConfObj;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 我家小报
 * @author dujy
 *
 */
public class CxTabloidActivity extends CxRootActivity {
	private String TAG = "RkTabloidActivity";
	
	private Button mBackBtn;
	private TextView mTitleInfo;
	
	private ListView cateListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_activity_tabloid);
		
		mBackBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
		mTitleInfo = (TextView)findViewById(R.id.cx_fa_activity_title_info);
		
		mBackBtn.setText(getString(R.string.cx_fa_navi_back));
		mBackBtn.setBackgroundResource(R.drawable.cx_fa_back_btn);
		mBackBtn.setOnClickListener(buttonListener);

		mTitleInfo.setText(getString(R.string.cx_fa_tabloid_activity_title));
		
		cateListView = (ListView)findViewById(R.id.tabloid_cate_listView); 
		
		Handler handler = new Handler(){   
	        public void handleMessage(Message msg) {  
	            init();
	            super.handleMessage(msg);  
	        }  
	          
	    };
		handler.sendEmptyMessage(0);		
	}

	private void init(){
		// 获取小报类型列表(先从本地获取，然后再从网络中获取。从网络中获取后，更新本地的数据，下次进入时生效)
		CxTabloidApi tabloidApi = new CxTabloidApi();
		
		// 获取本地缓存的数据
		TabloidDao dao 	= new TabloidDao(CxTabloidActivity.this);
		String version 	= dao.getVersion();
		TabloidCateConfData pubConf	= dao.getPubConf();					// 得到全局的配置，没有分类配置数据
		List<TabloidCateConfObj> confList = dao.getCateConfList(0);			// 分类的配置数据
		
		if(pubConf!=null  && confList.size()>0){
			pubConf.setConfig(confList);																	// 将全局配置和分类配置合并
			buildView(pubConf);
		}else{
			tabloidApi.getCategoryConfig(getCateConfCaller, version);
		}
	}
	
	private void buildView(TabloidCateConfData confData){
		// 得到在哪个小时提醒(主要是为了在页面上显示"上午"还是"下午")
		String notifyTime 	= confData.getNotification_time().replaceAll("\\[|\\]", "");		// 去掉左右中括号
		String[] arr 				= notifyTime.split(",");
		int notifyHour 		= Integer.valueOf(arr[0]);
		
		TabloidAdapter ap = new TabloidAdapter(confData.getConfig(), notifyHour,  CxTabloidActivity.this);
		cateListView.setAdapter(ap);
	}
	
	public JSONCaller getCateConfCaller = new JSONCaller(){
		@Override
		public int call(Object result) {
			CxTabloidCateConf cateConf = null;
			CxLog.e(TAG, result.toString());
			try{
				cateConf = new TabloidParse().parseCateConf(result);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(cateConf==null || cateConf.getRc()==408){
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			
			// 请求成功
			if(cateConf.getRc()==0){
				
				// 更新DB (跟库中的版本进行对比，只有版本更新了，才需要更新里面的配置数据)
				TabloidDao dao = new TabloidDao(CxTabloidActivity.this);
				String v2  			=  dao.getVersion();
				CxLog.e(TAG, "v2" + v2);
				if(!cateConf.getData().getVersion().equals( v2) ){
					// 更新全局配置(先删除，再更新)
					dao.delPubConfig();
					dao.insertPubConfig(cateConf.getData());
					
					// 先删除所有的配置，然后再添加新获取的数据
					dao.delAllCateConf();		
					dao.insertCateConfBatch( cateConf.getData().getConfig());
				}
				
				if(v2.equals("0")){
					// 如果缓存中没数据，需要把网络中返回的数据重绘到UI上
					Message msg = new Message();
					msg.obj = cateConf.getData();
					
					new Handler(Looper.getMainLooper()){
						public void handleMessage(Message msg) {		
							
							buildView( (TabloidCateConfData) msg.obj);			// 重绘显示页面
							
						};
					}.sendMessage(msg);
					
					// 库中没有小报数据，则需要从服务器取一次(用户第一次进入会有这种情况)
					getTabloidDataFromServer();
				}
				
			}else{
				if(TextUtils.isEmpty(cateConf.getMsg())){
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(cateConf.getMsg(),0);
				}
			}
			return 0;
		}
	};

	
	
	/**
	 * 从服务端获取小报数据
	 */
	public void getTabloidDataFromServer(){
		CxTabloidApi tabloidApi = new CxTabloidApi();
		TabloidDao dao = new TabloidDao(CxTabloidActivity.this);
		String[] arr 			= dao.getCategorieIdsAndTabloidIds();
		String categorie_ids 	= arr[0];
		String tabloid_ids 		= arr[1];
		
		CxLog.e(TAG, "categorie_ids:" + categorie_ids + "    tabloid_ids:" + tabloid_ids);
		tabloidApi.getCategoryList(getTabloidDataCaller, categorie_ids, tabloid_ids);
	}
	
	public JSONCaller getTabloidDataCaller = new JSONCaller(){
		@Override
		public int call(Object result) {
			CxTabloid tabloidData = null;
			CxLog.e(TAG, result.toString());
			try{
				tabloidData = new TabloidParse().parseTabloid(result);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(tabloidData==null || tabloidData.getRc()==408){
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			
			// 请求成功
			if(tabloidData.getRc()==0){
				
				// 将数据插入到库中
				TabloidDao dao = new TabloidDao(CxTabloidActivity.this);
				dao.insertTabloidBatch( tabloidData.getDataList() );
			}else{
				if(TextUtils.isEmpty(tabloidData.getMsg())){
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(tabloidData.getMsg(),0);
				}
			}
			return 0;
		}
	};
	
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
	};

  OnClickListener buttonListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.cx_fa_activity_title_back:
					CxTabloidActivity.this.finish();
					break;
				default:
					break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rk_tabloid, menu);
		return true;
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
				ToastUtil.getSimpleToast(CxTabloidActivity.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}

}
