package com.chuxin.family.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.net.CxSendImageApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxChangeChatBackground;
import com.chuxin.family.parse.been.data.ChatBgData;
import com.chuxin.family.parse.been.data.ChatBgItem;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.settings.CxChatBgAdapter.DownloadChatbgFinishListener;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxResourceManager;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
/**
 * 聊天背景设置
 * @author shichao.wang
 *
 */
public class CxChatBackgroundSelecter extends CxRootActivity {
	private GridView mGridView;
	private CxChatBgAdapter adapter;
	
	private ArrayList<String> mSystemChatBgRes;
	private ArrayList<String> mSystemChatBgName; 
	private ArrayList<String> mSystemChatBgUpperName;
	private ArrayList<String> mSystemChatBgSmallName;
	private ArrayList<String> mSystemChatBgSmallUpperName;
	private Button mBackBtn, mCustomBgBtn;
	
	private int selectItem = 1; //set chat background with first image of system by default
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_activity_chatbg_select);
	
		configData = CxGlobalParams.getInstance().getChatbgData();
		
		getResourceIntArr();
		
		mGridView = (GridView)findViewById(R.id.chat_background_gridview);
		
		mBackBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
		mCustomBgBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
		mBackBtn.setText(getString(R.string.cx_fa_navi_back));
		mCustomBgBtn.setBackgroundResource(R.drawable.cx_fa_selector_top_buttonplus);
		mCustomBgBtn.setVisibility(View.VISIBLE);
		mBackBtn.setOnClickListener(btnListener);
		mCustomBgBtn.setOnClickListener(btnListener);
		
		String bgStr = CxGlobalParams.getInstance().getChatBackgroundSmall();
		CxLog.i("RkCHatBackgroundSelecter_men", bgStr);
		CxLog.i("RkCHatBackgroundSelecter_men", CxGlobalParams.getInstance().getChatBackgroundBig());
		String customBg = null;
		if (null == bgStr) { //no background
			selectItem = 1; //the first image of system by default
		}else{
			if (bgStr.contains("@@")) { //system background
				customBg = null;
				bgStr = bgStr.replace("@@", "");
				String bgStr2 = bgStr.toLowerCase();
//				System.out.println(bgStr2);RkResourceString.getInstance().getStringByFlag(str, flag)"cx_fa_role_chatbg_thumbnail_default"
				if(bgStr2.equals(CxResourceString.getInstance().getStringByFlag("cx_fa_role_chatbg_thumbnail_default", 
						CxGlobalParams.getInstance().getVersion())) || bgStr.startsWith("chatbg")){
					selectItem=1;
				}else{				
					int i = 0;
					while(i < mSystemChatBgSmallName.size()){
						if (bgStr2.equalsIgnoreCase(mSystemChatBgSmallName.get(i))) { 
							selectItem = i+2; 
							break;
						}
						i++;
					}
				}
				
			}else{ //custom background
				selectItem = 0;
				customBg = bgStr;
			}
		}
		
		
		mGridView.setOnItemClickListener(itemClick);
		adapter = new CxChatBgAdapter(CxChatBackgroundSelecter.this,customBg, selectItem, mSystemChatBgRes,mSystemChatBgUpperName); 
		mGridView.setAdapter(adapter);
		
	}
	
	
	private void getResourceIntArr(){
		
		
		mSystemChatBgRes=new ArrayList<String>();
		mSystemChatBgName=new ArrayList<String>();
		mSystemChatBgUpperName=new ArrayList<String>();
		mSystemChatBgSmallName=new ArrayList<String>();
		mSystemChatBgSmallUpperName=new ArrayList<String>();
		
		ArrayList<ChatBgItem> items = configData.getItems();
		String url = configData.getResourceUrl();
		
		for(int i=0;i<items.size();i++){
			try {
				ChatBgItem item = items.get(i);
				String thumbnailUpper = item.getThumbnail();
				String bigImageUpper = item.getBigImage();
				String thumbnail = thumbnailUpper.toLowerCase();
				String bigImage = bigImageUpper.toLowerCase();
				
				String tempBbStr = "drawable"+File.separator+thumbnail;
				int resId = getResources().getIdentifier(tempBbStr, null, getPackageName());
				if(resId<=0){
		        	mSystemChatBgSmallUpperName.add(thumbnailUpper);
		        	mSystemChatBgSmallName.add(thumbnail);
		        	mSystemChatBgUpperName.add(bigImageUpper);
		        	mSystemChatBgName.add(bigImage);
		        	mSystemChatBgRes.add(url+File.separator+"a_"+thumbnailUpper+"_xhd.png");			        
				}else{
					mSystemChatBgSmallUpperName.add(thumbnailUpper);
					mSystemChatBgSmallName.add(thumbnail);
					mSystemChatBgUpperName.add(bigImageUpper);
					mSystemChatBgName.add(bigImage);
					mSystemChatBgRes.add(resId+"");
				}
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}
	

		
		
	}
	
	View.OnClickListener btnListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_activity_title_back:
				back();
				break;
			case R.id.cx_fa_activity_title_more: //调用gpuimage模块
				//启动gpuimage模块
				Intent selectImageForHead = new Intent(CxChatBackgroundSelecter.this, ActivitySelectPhoto.class);
				ActivitySelectPhoto.kIsCallPhotoZoom =false;
				ActivitySelectPhoto.kIsCallFilter = true;
				ActivitySelectPhoto.kIsCallSysCamera = true;
				ActivitySelectPhoto.kChoseSingle = true;
				startActivityForResult(selectImageForHead, CxSettingActivity.MODIFY_HEAD_REQUEST);
				break;

			default:
				break;
			}
		}
	};
	
	OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
		/*第一个item被点中就去调用世超接口获取聊天背景图片，如果有照片返回就要把那张图片
		 * 写到rk-chat-bg的目录下，并写好rk-chat-bg目录下此图片的路径；除此以外的item被点中，
		 * 要把rk-chat-bg目录清空，相应的第一张图片的路径要清空*/
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			//先刷新选中的图标，再发起网络请求

//			SystemClock.sleep(5000);
		
			if (0 == arg2) { //自定义聊天背景
				
				//启动gpuimage模块
				Intent selectImageForHead = new Intent(CxChatBackgroundSelecter.this, ActivitySelectPhoto.class);
				ActivitySelectPhoto.kIsCallPhotoZoom =false;
				ActivitySelectPhoto.kIsCallFilter = true;
				ActivitySelectPhoto.kIsCallSysCamera = true;
				ActivitySelectPhoto.kChoseSingle = true;
				startActivityForResult(selectImageForHead, CxSettingActivity.MODIFY_HEAD_REQUEST);				
			}
			
			
		
			adapter.setDownloadChatbgFinishListener(new DownloadChatbgFinishListener() {
				
				@Override
				public void sendMessage(int position) {
//					RkLog.i("", "arg2:"+position+",arg3:"+id);
//					if (0 == position) { //自定义聊天背景
//							
//						//启动gpuimage模块
//						Intent selectImageForHead = new Intent(RkChatBackgroundSelecter.this, ActivitySelectPhoto.class);
//						ActivitySelectPhoto.kIsCallPhotoZoom =false;
//						ActivitySelectPhoto.kIsCallFilter = false;
//						ActivitySelectPhoto.kIsCallSysCamera = true;
//						startActivityForResult(selectImageForHead, RkSettingFragment.MODIFY_HEAD_REQUEST);
//						
//					}else 
					if(1==position){
						try {
							CxSendImageApi.getInstance().modifyChatBackground(false, null, modifyCallback, 
									new BasicNameValuePair("chat_big", "@@"+CxResourceString.getInstance().getStringByFlag("cx_fa_role_chatbg_default", 
											CxGlobalParams.getInstance().getVersion())), 
									new BasicNameValuePair("chat_small", "@@"+CxResourceString.getInstance().getStringByFlag("cx_fa_role_chatbg_thumbnail_default", 
											CxGlobalParams.getInstance().getVersion())));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{ //使用系统背景
						try {	
//							String small="";
//							if(mSystemChatBgRes.get(position-2).contains("http")){
//								small=mSystemChatBgRes.get(position-2);
//							}else{
//								small="@@"+mSystemChatBgSmallUpperName.get(position-2);
//							}
//							RkLog.i("men", small);
							CxSendImageApi.getInstance().modifyChatBackground(false, null, modifyCallback, 
									new BasicNameValuePair("chat_big", "@@"+mSystemChatBgUpperName.get(position-2)), 
									new BasicNameValuePair("chat_small", "@@"+mSystemChatBgSmallUpperName.get(position-2)));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
			});
			
			if(0 != arg2)
				adapter.setClickItem(arg2,null);
		}
	};
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return ;
		}
	
		if (null == data) {
			return ;
		}
		String path = data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI);
		if (TextUtils.isEmpty(path)) {
			return ; 
		}
		
		path = path.replace("file://", "");
		
		adapter.setClickItem(0,path);
		
		List<CxFile> files = new ArrayList<CxFile>();
		CxFile file = new CxFile(path, "background", "image/jpg");
		files.add(file);
		try {
			CxSendImageApi.getInstance().modifyChatBackground(true,  files, modifyCallback);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	JSONCaller modifyCallback = new JSONCaller() {
		
		@Override
		public int call(Object result) {
			
			
			if (null == result) {
				displayModifyHead(getString(R.string.cx_fa_modify_chatbg_fail),0);
				return -1;
			}
			
			CxChangeChatBackground changeChatBgResult = null;
			try {
				changeChatBgResult = (CxChangeChatBackground)result;
			} catch (Exception e) {
			}
			if (null == changeChatBgResult) {
				displayModifyHead(getString(R.string.cx_fa_modify_chatbg_fail),0);
				return -2;
			}
			CxLog.i("RkChatBackgroundSeleter_men",changeChatBgResult.getRc()+"" );
			if(0 != changeChatBgResult.getRc()){
				displayModifyHead(changeChatBgResult.getMsg(),0);
				return changeChatBgResult.getRc();
			}
			
			displayModifyHead(getString(R.string.cx_fa_modify_chatbg_success),1);
			
			//修改聊天背景成功
			CxGlobalParams.getInstance().setChatBackgroundBig(changeChatBgResult.getChat_big());
			CxGlobalParams.getInstance().setChatBackgroundSmall(changeChatBgResult.getChat_small());
			
			CxChatBackgroundSelecter.this.finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			
			return 0;
		}
	};
	private ChatBgData configData;
	
	
	
	/**
	 * 
	 * @param info
	 * @param number 0 失败；1 成功；2 不要图。
	 */
	private void displayModifyHead(String info,int number) {
		Message msg = new Message();
		msg.obj = info;
		msg.arg1=number;
		new Handler(CxChatBackgroundSelecter.this.getMainLooper()) {
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
				ToastUtil.getSimpleToast(CxChatBackgroundSelecter.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}

	protected void back() {
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			back();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};
	
	
	@Override
	protected void onDestroy() {
//		try {
//			CxResourceManager resourceManager = CxResourceManager.getInstance(
//					CxChatBackgroundSelecter.this, "thunb", CxChatBackgroundSelecter.this);
//			if (null != resourceManager) {
//				resourceManager.clearMemory();
//				resourceManager = null;
//			}
//		} catch (Exception e) {
//		}
		super.onDestroy();
	}
	
	
	
}
