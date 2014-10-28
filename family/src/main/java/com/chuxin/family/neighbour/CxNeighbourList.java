package com.chuxin.family.neighbour;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxAuthenNew;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.models.Model;
import com.chuxin.family.models.Neighbour;
import com.chuxin.family.neighbour.CxNeighbourFragment.CurrentObserver;
import com.chuxin.family.neighbour.CxNeighbourFragment.InvitationResponse;
import com.chuxin.family.net.CxNeighbourApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxNeighbourParser;
import com.chuxin.family.parse.been.CxNeighbourSendInvitation;
import com.chuxin.family.parse.been.data.InvitationData;
import com.chuxin.family.parse.been.data.InvitationPost;
import com.chuxin.family.parse.been.data.InvitationReply;
import com.chuxin.family.parse.been.data.InvitationUserInfo;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.service.CxServiceParams;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.NiceEditText;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.widgets.ScrollableListView;
import com.chuxin.family.widgets.ScrollableListView.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class CxNeighbourList extends CxRootActivity implements OnClickListener, IWXAPIEventHandler{

    private Button mReturnBtn;
    private TextView mTitle;
    private Button mInviteNeighbourBtn;
    private EditText mNeighbourNumberEditText;
    private TextView mNeighbourNumberTip;
    private Button mAddNeighbourBtn;
    private ScrollableListView mNeighbourList;
    private NeighbourListAdapter mNeighbourListAdapter;
    public static String mMyNeighbourNumber;
    public static String mMyNeighbourShowNumber;
    private static final int UPDATE_MY_NEIGHBOUR_NUMBER = 0; // 更新密邻列表列表号
    public static final int UPDATE_NEIGHBOUR_LIST_DATA = 1; // 更新密邻列表数据
    public static final int POP_ADD_DIALOG = 2; // 弹出添加密邻对话框
    private static final int SHOW_TOAST = 3; // 弹出提示
    private static final int POP_ERROR_TOAST = 4; // 弹出提示
    
    private static final int WXSceneSession = 0;
    
	private static final int MANAGE_VISIBLE = 5;
	private static final int MANAGE_GO = 6;
	
    
    
    private static CxNeighbourList mRkNeighbourList;
    private String mGroupId,mWifeName, mWifeAvatar, mHusbandName, mHusbandAvatar;
    private String mNeighbourNumber;
    
    private CurrentObserver mUpdateObserver;
    
    public Handler neighbourHandler  = new Handler(){
    	@SuppressWarnings("static-access")
		public void handleMessage(Message msg){
    		switch(msg.what){
	    		case UPDATE_MY_NEIGHBOUR_NUMBER:
	    			if(!TextUtils.isEmpty(mMyNeighbourShowNumber)){
		    			String numberFormat = CxNeighbourList.this.getResources().getString(R.string.cx_fa_my_neighbour_number_tip_formatted);
		    			mNeighbourNumberTip.setText(numberFormat.format(numberFormat, mMyNeighbourShowNumber));
	    			}
	    			break;
	    		case UPDATE_NEIGHBOUR_LIST_DATA:
	    			requestNetData();
	    			break;
	    		case POP_ADD_DIALOG:
	    		    popAddDialog();
	    		    break;
	    		case POP_ERROR_TOAST:
	    			ToastUtil.getSimpleToast(CxNeighbourList.this, -1, getString(R.string.cx_fa_add_neighbour_error_tip), 1).show();
	    			break;
	    		case SHOW_TOAST:
                    String message = msg.obj.toString();
//                    Toast toast = Toast.makeText(RkNeighbourList.this, message, Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.BOTTOM, 0, 0);
//                    toast.show();
                    ToastUtil.getSimpleToast(CxNeighbourList.this, -1, message, 1).show();
	    		    break;
	    		case MANAGE_VISIBLE:
	    			manageLayout.setVisibility(View.VISIBLE);
	    			break;
	    		case MANAGE_GO:
	    			manageLayout.setVisibility(View.GONE);
	    			break;
    		}
    		
    	}
    };
    
    public static CxNeighbourList getInstance(){
    	if(null == mRkNeighbourList){
    		mRkNeighbourList = new CxNeighbourList();
    	}
    	return mRkNeighbourList;
    }
    
    @Override
    protected void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        setContentView(R.layout.cx_fa_activity_neighbors_list);
        init();
    }
    public void init(){
        mReturnBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
        mReturnBtn.setText(R.string.cx_fa_navi_back);
        mReturnBtn.setOnClickListener(this);
        mTitle = (TextView)findViewById(R.id.cx_fa_activity_title_info);
        mTitle.setText(R.string.cx_fa_neighbour_list_title);
        mInviteNeighbourBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
        mInviteNeighbourBtn.setText(R.string.cx_fa_neighbour_invite_neighbour_btn);
        mInviteNeighbourBtn.setVisibility(View.VISIBLE);
        mInviteNeighbourBtn.setOnClickListener(this);
        mNeighbourNumberEditText = (EditText)findViewById(R.id.cx_fa_neighbour_number);
        mNeighbourNumberTip = (TextView)findViewById(R.id.cx_fa_neighbour_number_tip);
        mAddNeighbourBtn = (Button)findViewById(R.id.cx_fa_add_neighbour_btn);
        mAddNeighbourBtn.setOnClickListener(this);
        
        manageLayout = (LinearLayout) findViewById(R.id.cx_fa_neighbour_add_neighbour_manage_layout);
        manageLayout.setOnClickListener(this);
        
        mNeighbourList = (ScrollableListView)findViewById(R.id.cx_fa_neighbour_list);
        mNeighbourListAdapter = new NeighbourListAdapter();
        mNeighbourList.setAdapter(mNeighbourListAdapter);  
        mNeighbourList.setOnHeaderRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh() {
                Message message = Message.obtain(neighbourHandler, UPDATE_NEIGHBOUR_LIST_DATA);
                message.sendToTarget();
            }
        });
        
        mUpdateObserver =new CurrentObserver();
        List<String> changeTags=new ArrayList<String>();
		changeTags.add(CxNeighbourParam.NB_WIFE_ICON);
		changeTags.add(CxNeighbourParam.NB_HUSBAND_ICON);
		changeTags.add(CxNeighbourParam.NB_CHANGE_NAME);
		changeTags.add(CxNeighbourParam.NEIGHBOUR_REMOVE);
		
		mUpdateObserver.setListenTag(changeTags);
		mUpdateObserver.setMainThread(true);
		CxNeighbourParam.getInstance().registerObserver(mUpdateObserver);

        loadData();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if(mUpdateObserver!=null){
			CxNeighbourParam.getInstance().unRegisterObsercer(mUpdateObserver);
		}
    }
    
    public void loadData(){
    	loadLocalData();
    	requestNetData();
    }
    
    public void loadLocalData(){
    	List<Model> neighbours = new Neighbour(null, CxNeighbourList.this).gets("1=1", new String[] {}, null, 0, 0);
    	if(null == neighbours){
    		neighbourHandler.sendEmptyMessage(MANAGE_GO);
    		mNeighbourListAdapter.setNeighbourLists(null);
    		return;
    	}
    	ArrayList<Neighbour> newNeighbours = new ArrayList<Neighbour>();
    	Iterator<Model> i = neighbours.iterator();
    	boolean manageIsShow=false;
    	while(i.hasNext()){
    		Neighbour neighbour = (Neighbour)i.next();
    		newNeighbours.add(neighbour);
    		if(neighbour.getStatus()==2 && !neighbour.getNeighbourId().equals(CxGlobalParams.getInstance().getPairId())){
    			manageIsShow=true;
    		}
    	}
    	
    	if(manageIsShow){
    		neighbourHandler.sendEmptyMessage(MANAGE_VISIBLE);
    	}else{
    		neighbourHandler.sendEmptyMessage(MANAGE_GO);
    	}
    
    	mNeighbourListAdapter.setNeighbourLists(newNeighbours);
    	
    }
    
    public void requestNetData(){
    	CxLog.i("RkNeighbourList_men", "requestNetData");
    	CxNeighbourApi.getInstance().requestNeighborList(0, 100, new JSONCaller() {
			
			@Override
			public int call(Object result) {
				updateNeighbours(result);
				loadLocalData();
				return 0;
			}
		});
    }
    
    public void updateNeighbours(Object result){
    	if(null == result){
    		showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
    		return;
    	}
    	clearAllData();
    	JSONArray neighbours = (JSONArray)result;
    	for(int i = 0; i < neighbours.length(); i++){
    		try {
				JSONObject neighbourObj = neighbours.getJSONObject(i);
				Neighbour neighbour = new Neighbour(neighbourObj, CxNeighbourList.this);
				if(i == 0){
					mMyNeighbourNumber = neighbour.getNeighbourId();
					mMyNeighbourShowNumber=neighbour.getNeighbourShowId();
					Message message = Message.obtain(neighbourHandler, UPDATE_MY_NEIGHBOUR_NUMBER);
					message.sendToTarget();
				}
				neighbour.put();
			} catch (JSONException e) {
				CxLog.e("updateNeighbours", "" + e.getMessage());
			}
    	}
    }
    
    @Override
    public void onClick(View v) {
    	switch(v.getId()){
    		case R.id.cx_fa_activity_title_back:
    			//TODO 跳转回密邻首页
    			finish();
    			this.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    			break;
    		case R.id.cx_fa_activity_title_more:
    			//TODO 跳转到邀请密邻页
//    			Intent intent = new Intent(RkNeighbourList.this, RkInviteNeighbour.class);
//    			intent.putExtra("from", "neighbourList");
//    			startActivity(intent);
//    			finish();
//    			this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    			
    			//取消邀请亲友界面，改为弹dialog
    			
    			showInviteDialog();
    			
    			
    			break;
    		case R.id.cx_fa_add_neighbour_btn:
    			if(TextUtils.isEmpty(mNeighbourNumberEditText.getText().toString()) ){
//    				Toast toast = Toast.makeText(RkNeighbourList.this, getResources().getString(R.string.cx_fa_add_neighbour_null_tip), Toast.LENGTH_LONG);
//    				toast.setGravity(Gravity.BOTTOM, 0, 0);
//    				toast.show();
    				ToastUtil.getSimpleToast(CxNeighbourList.this, -1, getString(R.string.cx_fa_add_neighbour_null_tip), 1).show();
    				break;
    			}
    			if(null != mMyNeighbourShowNumber && mMyNeighbourShowNumber.equals(mNeighbourNumberEditText.getText().toString())){
//    				Toast toast = Toast.makeText(RkNeighbourList.this, getResources().getString(R.string.cx_fa_add_neighbour_error_tip), Toast.LENGTH_LONG);
//    				toast.setGravity(Gravity.BOTTOM, 0, 0);
//    				toast.show();
    				ToastUtil.getSimpleToast(CxNeighbourList.this, -1, getString(R.string.cx_fa_add_neighbour_error_tip), 1).show();
    				break;
    			}
    			CxLog.i("men",">>>>>>>>1");
    			neighbourQuery();
    			break;
    		case R.id.cx_fa_neighbour_add_neighbour_manage_layout:
    			Intent manageIntent=new Intent(CxNeighbourList.this, CxNeighbourManageActivity.class);
    			startActivity(manageIntent);
    			this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    			break;
    	}
        
    }
    
    private void showInviteDialog() {
		
    	View inflate = View.inflate(this, R.layout.cx_fa_widget_neighbour_invite_dialog, null);
    	Button bySms = (Button) inflate.findViewById(R.id.nb_list_invite_dialog_by_sms);
    	Button byWeixin = (Button) inflate.findViewById(R.id.nb_list_invite_dialog_by_weixin);
    	Button byCode = (Button) inflate.findViewById(R.id.nb_list_invite_dialog_by_code);
    	Button cancel = (Button) inflate.findViewById(R.id.nb_list_invite_dialog_cancel);
    	
    	bySms.setOnClickListener(inviteListener);
    	byWeixin.setOnClickListener(inviteListener);
    	byCode.setOnClickListener(inviteListener);
    	cancel.setOnClickListener(inviteListener);
    	
    	inviteDialog = new Dialog(this, R.style.simple_dialog);		
    	inviteDialog.setContentView(inflate);
    	inviteDialog.show();
    	
		
	}
    
    OnClickListener inviteListener =new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			inviteDialog.dismiss();
			String smsContentStr = String.format(getString(R.string.cx_fa_invite_neighbour_word), CxGlobalParams.getInstance().getGroup_show_id());
			switch (v.getId()) {
			case R.id.nb_list_invite_dialog_by_sms:
		        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
		        i.putExtra("sms_body", smsContentStr);
		        i.setType("vnd.android-dir/mms-sms");
		        startActivity(i);
				break;
			case R.id.nb_list_invite_dialog_by_weixin:
				if(CxAuthenNew.api.isWXAppInstalled() && CxAuthenNew.api.isWXAppSupportAPI()){
			        WXTextObject textObj = new WXTextObject();
			        textObj.text = smsContentStr;
			        
			        WXMediaMessage msg = new WXMediaMessage();
			        
			        msg.mediaObject = textObj;
			        msg.description = smsContentStr;
			        SendMessageToWX.Req req = new SendMessageToWX.Req();
			        req.transaction = String.valueOf(System.currentTimeMillis());
			        req.message = msg;
			        req.scene = WXSceneSession;
			        
			        CxAuthenNew.api.sendReq(req);
		    	} else {
//		    		Toast.makeText(RkNeighbourList.this, "请先安装微信", Toast.LENGTH_LONG).show();
		    		ToastUtil.getSimpleToast(CxNeighbourList.this, -1, "请先安装微信", 1).show();
		    		
		    	}
				break;
			case R.id.nb_list_invite_dialog_by_code:
				
				DialogUtil.getInstance().getCodeDialogShow(CxNeighbourList.this, 
						R.drawable.cx_fa_invite_qr_code, getString(R.string.cx_fa_neighbour_invite_code_dialog_text),
						getString(R.string.cx_fa_neighbour_invite_code_dialog_text2));
				
//				Intent intent = new Intent();        
//				intent.setAction("android.intent.action.VIEW");    
//				Uri content_url = Uri.parse(getString(R.string.cx_fa_invite_neighbour_word_url));   
//				intent.setData(content_url);  
//				startActivity(intent);			
				break;
			case R.id.nb_list_invite_dialog_cancel:				
				break;

			default:
				break;
			}
			
		}
	};
	private Dialog inviteDialog;
	private LinearLayout manageLayout;
    
    
    private void popAddDialog(){
     // 添加密邻
        CxAddNeighbourDialog addDialog = new CxAddNeighbourDialog(CxNeighbourList.this, mGroupId,
                mWifeName, mWifeAvatar, mHusbandName, mHusbandAvatar, R.style.add_neighbour_dialog, new DialogListener() {
            
            @Override
            public void refreshUiAndData() {
//              requestNetData();
//                Toast toast = Toast.makeText(RkNeighbourList.this, getResources().getString(R.string.cx_fa_add_neighbour_already_send_tip), Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.BOTTOM, 0, 0);
//                
//                LinearLayout toastView = (LinearLayout) toast.getView();
//                ImageView imageCodeProject = new ImageView(RkNeighbourList.this);
//                imageCodeProject.setImageResource(R.drawable.send_button);
//                toastView.addView(imageCodeProject, 0);
//                toast.show();
            	ToastUtil.getSimpleToast(CxNeighbourList.this, -2, getString(R.string.cx_fa_add_neighbour_already_send_tip), 1).show();
                Message message = Message.obtain(neighbourHandler, UPDATE_NEIGHBOUR_LIST_DATA);
                message.sendToTarget();
            }
        });
        addDialog.show();
    }
    
    /**
     * clear all neighbour list data
     */
    public void clearAllData() {
        Neighbour neighbours = new Neighbour(null, CxNeighbourList.this);
        neighbours.dropAll();
    }
    
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			finish();
			CxNeighbourList.this.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			return false;
		}
		
		
		return super.onKeyDown(keyCode, event);
	};
    
    class NeighbourListAdapter extends BaseAdapter{
    	
		class NeighbourViewHolder{
			public String mWifeName;
			public String mWifeAvatar;
			public String mHusbandName;
			public String mHusbandAvatar;
			public int mStatus;
		}
    	private List<Neighbour> mNeighbours = null;
    	
		public void setNeighbourLists(final List<Neighbour> neighbours) {
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

				@Override
				public void run() {
					mNeighbours = neighbours;
					mNeighbourListAdapter.notifyDataSetChanged();
					mNeighbourList.onRefreshComplete();
					mNeighbourList.setSelection(0);
					mNeighbourList.setSelectionAfterHeaderView();
				}

			}, 1);
		}
		@Override
		public int getCount() {
			if(null == mNeighbours){
				return 0;
			}
			return mNeighbours.size();
		}

		@Override
		public Object getItem(int position) {
			if(null != mNeighbours){
				return mNeighbours.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NeighbourViewHolder neighbourViewHolder = null;
			if(null == convertView){
				neighbourViewHolder = new NeighbourViewHolder();
				convertView = LayoutInflater.from(CxNeighbourList.this).inflate(R.layout.cx_fa_activity_neighbors_list_item, null);
			} else {
				neighbourViewHolder = (NeighbourViewHolder)convertView.getTag();
			}
			LinearLayout itemLayout = (LinearLayout)convertView.findViewById(R.id.cx_fa_neighbour_list_item);
			CxImageView wifeAvatar = (CxImageView)convertView.findViewById(R.id.cx_fa_neighbour_list_item_wife_avatar);
			CxImageView husbandAvatar = (CxImageView)convertView.findViewById(R.id.cx_fa_neighbour_list_item_husband_avatar);
			TextView familyName = (TextView)convertView.findViewById(R.id.cx_fa_neighbour_list_item_name);
			ImageButton arrowButton = (ImageButton)convertView.findViewById(R.id.cx_fa_neighbour_arrow_btn);
			TextView arrowText = (TextView)convertView.findViewById(R.id.cx_fa_neighbour_arrow_text);
			ImageView privateChat = (ImageView)convertView.findViewById(R.id.cx_fa_neighbour_chat_btn);
			
			final Neighbour neighbour = (Neighbour)mNeighbours.get(position);
			
			neighbourViewHolder.mHusbandAvatar = neighbour.getHusbandAvatar();
			neighbourViewHolder.mWifeAvatar = neighbour.getWifeAvatar();
			neighbourViewHolder.mHusbandName = neighbour.getHusbandName();
			neighbourViewHolder.mWifeName = neighbour.getWifeName();
			neighbourViewHolder.mStatus = neighbour.getStatus();
			
            String version = getString(CxResourceString.getInstance().str_pair);
            if("老公".equals(version)){
//                husbandAvatar.setImage(neighbourViewHolder.mHusbandAvatar, false, 44, RkNeighbourList.this, "head", RkNeighbourList.this);
                husbandAvatar.displayImage(imageLoader, 
                		neighbourViewHolder.mHusbandAvatar, 
                		CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
                
//                wifeAvatar.setImage(neighbourViewHolder.mWifeAvatar, false, 44, RkNeighbourList.this, "head", RkNeighbourList.this);
            
                wifeAvatar.displayImage(imageLoader, 
                		neighbourViewHolder.mWifeAvatar, 
                		CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
                
            }else{
//                husbandAvatar.setImage(neighbourViewHolder.mWifeAvatar, false, 44, RkNeighbourList.this, "head", RkNeighbourList.this);
                husbandAvatar.displayImage(imageLoader, 
                		neighbourViewHolder.mWifeAvatar, 
                		CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
//                wifeAvatar.setImage(neighbourViewHolder.mHusbandAvatar, false, 44, RkNeighbourList.this, "head", RkNeighbourList.this);
                wifeAvatar.displayImage(imageLoader, 
                		neighbourViewHolder.mHusbandAvatar, 
                		CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
            }
            
//			husbandAvatar.setImage(neighbourViewHolder.mHusbandAvatar, false, 44, RkNeighbourList.this, "head", RkNeighbourList.this);
//			wifeAvatar.setImage(neighbourViewHolder.mWifeAvatar, false, 44, RkNeighbourList.this, "head", RkNeighbourList.this);
			String familyNameFormat = getResources().getString(R.string.cx_fa_neighbour_family_name_formatted);
			if(position == 0){
				arrowButton.setVisibility(View.GONE);
				familyName.setText(getResources().getString(R.string.cx_fa_neighbour_family_name));
			} else {
				arrowButton.setVisibility(View.GONE);
			    if("老公".equals(version)){
			        familyName.setText(String.format(familyNameFormat, neighbourViewHolder.mWifeName, neighbourViewHolder.mHusbandName));
			    } else {
			        familyName.setText(String.format(familyNameFormat, neighbourViewHolder.mHusbandName, neighbourViewHolder.mWifeName));
			    }   		    
			}
			final int mPos=position;
			if(0 == neighbourViewHolder.mStatus){
				privateChat.setVisibility(View.GONE);
				arrowButton.setVisibility(View.GONE);
				arrowText.setVisibility(View.VISIBLE);
				arrowText.setText(getResources().getString(R.string.cx_fa_neighbour_wait_agree));
				itemLayout.setOnClickListener(null);
				
			} else if(1 == neighbourViewHolder.mStatus){
				privateChat.setVisibility(View.GONE);
				arrowButton.setVisibility(View.GONE);
				arrowText.setVisibility(View.VISIBLE);
				arrowText.setText(getResources().getString(R.string.cx_fa_neighbour_refuse));
			} else if(2 == neighbourViewHolder.mStatus){
				arrowButton.setVisibility(View.GONE);
				if(mPos == 0){
					privateChat.setVisibility(View.GONE);
				}else{
					privateChat.setVisibility(View.VISIBLE);	
				}
				
				privateChat.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent=new Intent(CxNeighbourList.this,CxNeighbourAddMessage.class);
						intent.putExtra(CxNeighbourParam.NB_ADD_MESSAGE_GROUP_ID,neighbour.getId());
						startActivity(intent);	
						CxNeighbourList.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
						
					}
				});
				
				
//				arrowButton.setVisibility(View.VISIBLE);
				arrowText.setVisibility(View.GONE);
				itemLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(mPos == 0){
							Intent intent=new Intent(CxNeighbourList.this,CxNbOurHome.class);
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_URL,neighbour.getWifeAvatar());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_URL,neighbour.getHusbandAvatar());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_PAIR_ID,neighbour.getId());
							startActivity(intent);		
							CxNeighbourList.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
						}else{
							Intent intent=new Intent(CxNeighbourList.this,CxNbNeighboursHome.class);
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_ID,neighbour.getNeighbourId());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_NAME,neighbour.getWifeName());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_NAME,neighbour.getHusbandName());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_URL,neighbour.getWifeAvatar());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_URL,neighbour.getHusbandAvatar());
							intent.putExtra(CxGlobalConst.S_NEIGHBOUR_PAIR_ID,neighbour.getNeighbourId());
							startActivity(intent);	
							CxNeighbourList.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
						}
					}
				});
			}
			
			convertView.setTag(neighbourViewHolder);
			
			return convertView;
		}
    }

    
    private void neighbourQuery(){
        try {
        	CxLog.i("men",">>>>>>>>2");
            CxNeighbourApi.getInstance().requestNeighbourQuery(mNeighbourNumberEditText.getText().toString(), new JSONCaller(){

                @Override
                public int call(Object result) {
                     if( null == result){                   	 
                    	 showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);                	 
                         return -1;
                     }
                    JSONObject data = (JSONObject)result;
                    
                    try {
                    int rc = data.getInt("rc");
                    if(rc != 0 ){
                    	if(rc==408){
                    		 showResponseToast(getString(R.string.cx_fa_net_response_code_null),0); 
                    		 return 408;
                    	}
                    	
                    	if(!data.isNull("msg") && !TextUtils.isEmpty(data.getString("msg"))){
                    		CxLog.i("men",">>>>>>>>3");
                            Message msg = Message.obtain(neighbourHandler, SHOW_TOAST);
                            msg.obj = data.get("msg");
                            msg.sendToTarget();
                            return 0;
                    	}else{
                    		showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
                    	}
                    	
                    	
                    } 
                        
                    JSONObject jObj = data.getJSONObject("data");
                    String groupId=jObj.getString("id");
                    String wifeName = jObj.getString("wife_name");
                    String wifeAvatar = jObj.getString("wife_avatar");
                    String husbandName = jObj.getString("husband_name");
                    String husbandAvatar = jObj.getString("husband_avatar");
                    mGroupId=groupId;
                    mWifeName = wifeName;
                    mWifeAvatar = wifeAvatar;
                    mHusbandName = husbandName;
                    mHusbandAvatar = husbandAvatar;
                    
                    if(mGroupId.equals(mMyNeighbourNumber)){
                    	Message msg = Message.obtain(neighbourHandler, POP_ERROR_TOAST );
                        msg.sendToTarget();
                    }else{
                    	Message msg = Message.obtain(neighbourHandler, POP_ADD_DIALOG);
                        msg.sendToTarget();
                    }
  
//                  new Handler(mContext.getMainLooper()){
//                      public void handleMessage(Message msg) {
//                          mWfIcon.setImage(mWifeAvatar, false, 44, "RkAddNeighbourDialog", "head", mContext);
//                          mHbIcon.setImage(mHusbandAvatar, false, 44, "RkAddNeighbourDialog", "head", mContext);
//                          String familyNameFormat = mContext.getResources().getString(R.string.cx_fa_neighbour_family_name_formatted);
//                          mFamilyName.setText(String.format(familyNameFormat, mWifeName, mHusbandName));
//                      };
//                  }.sendEmptyMessage(1);
                    
                    } catch (JSONException e) {
                        CxLog.e("RkAddNeighbourDialog/requestNetData", "" + e.getMessage());
                        showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
                    }
                    return 0;
                }
                
            });
        } catch (Exception e) {
            CxLog.e("RkAddNeighbourDialog/requestNetData", "" + e.getMessage());
        }
    }
    
    
    class CurrentObserver extends CxObserverInterface {

		/* 主要事项有：1、新的分享 2、头像（中、小） */
		@Override
		public void receiveUpdate(String actionTag) {
			if (null == actionTag) {
				return;
			}
			CxNeighbourParam param = CxNeighbourParam.getInstance();

			
			//TODO  jfdas
			if(CxNeighbourParam.NB_WIFE_ICON.equalsIgnoreCase(actionTag)){
				String nbWifeIcon = param.getNbWifeIcon();
				if(nbWifeIcon!=null){	
					requestNetData();
				}
				
				return;
			}
			if(CxNeighbourParam.NB_HUSBAND_ICON.equalsIgnoreCase(actionTag)){
				String nbHusbandIcon = param.getNbHusbandIcon();
				if(nbHusbandIcon!=null){	
					requestNetData();			
				}
				return;
			}
			
		
			if(CxNeighbourParam.NB_CHANGE_NAME.equalsIgnoreCase(actionTag)){
				 InvitationUserInfo nbChangeName = param.getNbChangeName();
				if(nbChangeName!=null){
					requestNetData();
				}	
				return;
			}
			
			if(CxNeighbourParam.NEIGHBOUR_REMOVE.equalsIgnoreCase(actionTag)){				
				requestNetData();					
				return;
			}

			
		}
	}


    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        int result = 0;

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }

        Toast.makeText(this, getResources().getString(result), Toast.LENGTH_LONG).show();
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
		new Handler(CxNeighbourList.this.getMainLooper()) {
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
				ToastUtil.getSimpleToast(CxNeighbourList.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
      
    
    
        
}
