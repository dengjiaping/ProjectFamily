package com.chuxin.family.neighbour;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.net.CxNeighbourApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

public class CxAddNeighbourDialog extends Dialog implements OnClickListener{

	private Context mContext;
	private String mNeighbourNumber;
	private CxImageView mHbIcon;
	private CxImageView mWfIcon;
	private TextView mFamilyName;
	private Button mAddBtn;
	private Button mCancelBtn;
	private String mWifeName, mWifeAvatar, mHusbandName, mHusbandAvatar;
	private static final int UPDATE_VIEW = 0;
	private static final int POP_AGREE_TOAST = 1;// 发送成功提示
	private static final int POP_ERROR_TOAST = 2;// 发送失败提示
	private DialogListener mDialogListener;
	private String mErrorMsg = "";
	
	private Handler mAddNeighbourHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
				case UPDATE_VIEW:
					updateView();
					break;
				case POP_AGREE_TOAST:
					popAgreeToast();
					break;
				case POP_ERROR_TOAST:
				    popErrorToast();
				    break;
			}
		}
	};
	public CxAddNeighbourDialog(Context context) {
		super(context);
		mContext = context;
	}
	public CxAddNeighbourDialog(Context context, String num, String wifeName, String wifeAvatar, String husbandName, String husbandAvatar, int style, DialogListener di) {
		super(context, style);
		mContext = context;
		mNeighbourNumber = num;
		mDialogListener = di;
		mWifeName = wifeName;
		mWifeAvatar = wifeAvatar;
		mHusbandName = husbandName;
		mHusbandAvatar = husbandAvatar;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_dialog_add_neighbor);
		init();
	}
	
	private void init(){
		mHbIcon = (CxImageView)findViewById(R.id.cx_fa_dialog_add_neighbour_hb_icon);
		mWfIcon = (CxImageView)findViewById(R.id.cx_fa_dialog_add_neighbour_wf_icon);
		mFamilyName = (TextView)findViewById(R.id.cx_fa_add_neighbour_title_info);
		mAddBtn = (Button)findViewById(R.id.cx_fa_dialog_add_btn);
		mAddBtn.setOnClickListener(this);
		mCancelBtn = (Button)findViewById(R.id.cx_fa_dialog_cancel_btn);
		mCancelBtn.setOnClickListener(this);
		//requestNetData();
		updateView();
	}
	
	private void requestNetData(){
		try {
			CxNeighbourApi.getInstance().requestNeighbourQuery(mNeighbourNumber, new JSONCaller(){

				@Override
				public int call(Object result) {
					 if( null == result){
						 return -1;
					 }
					JSONObject data = (JSONObject)result;
					try {
					JSONObject jObj = data.getJSONObject("data");
					String wifeName = jObj.getString("wife_name");
					String wifeAvatar = jObj.getString("wife_avatar");
					String husbandName = jObj.getString("husband_name");
					String husbandAvatar = jObj.getString("husband_avatar");
					mWifeName = wifeName;
					mWifeAvatar = wifeAvatar;
					mHusbandName = husbandName;
					mHusbandAvatar = husbandAvatar;
					Message msg = Message.obtain(mAddNeighbourHandler, UPDATE_VIEW);
					msg.sendToTarget();
//					new Handler(mContext.getMainLooper()){
//						public void handleMessage(Message msg) {
//							mWfIcon.setImage(mWifeAvatar, false, 44, "RkAddNeighbourDialog", "head", mContext);
//							mHbIcon.setImage(mHusbandAvatar, false, 44, "RkAddNeighbourDialog", "head", mContext);
//							String familyNameFormat = mContext.getResources().getString(R.string.cx_fa_neighbour_family_name_formatted);
//							mFamilyName.setText(String.format(familyNameFormat, mWifeName, mHusbandName));
//						};
//					}.sendEmptyMessage(1);
					
					} catch (JSONException e) {
						CxLog.e("RkAddNeighbourDialog/requestNetData", "" + e.getMessage());
					}
					return 0;
				}
				
			});
		} catch (Exception e) {
			CxLog.e("RkAddNeighbourDialog/requestNetData", "" + e.getMessage());
		}
	}
	
	private void updateView(){
//		mWfIcon.setImage(mWifeAvatar, false, 44, "RkAddNeighbourDialog", "head", mContext);
//		mHbIcon.setImage(mHusbandAvatar, false, 44, "RkAddNeighbourDialog", "head", mContext);
		
		mWfIcon.displayImage(ImageLoader.getInstance(), mWifeAvatar, 
				R.drawable.cx_fa_wf_icon_small, true, 
				CxGlobalParams.getInstance().getSmallImgConner());
		
		mHbIcon.displayImage(ImageLoader.getInstance(), mHusbandAvatar, 
				R.drawable.cx_fa_hb_icon_small, true, 
				CxGlobalParams.getInstance().getSmallImgConner());
		
        String version = mContext.getString(CxResourceString.getInstance().str_pair);
        String familyNameFormat = mContext.getResources().getString(R.string.cx_fa_neighbour_family_name_formatted);
        if("老公".equals(version)){
//            mWfIcon.setImage(mWifeAvatar, false, 44, "RkAddNeighbourDialog", "head", mContext);
//            mHbIcon.setImage(mHusbandAvatar, false, 44, "RkAddNeighbourDialog", "head", mContext);
            
            mWfIcon.displayImage(ImageLoader.getInstance(), mWifeAvatar, 
    				CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
    				CxGlobalParams.getInstance().getSmallImgConner());
    		
    		mHbIcon.displayImage(ImageLoader.getInstance(), mHusbandAvatar, 
    				CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
    				CxGlobalParams.getInstance().getSmallImgConner());
            
            mFamilyName.setText(String.format(familyNameFormat, mWifeName, mHusbandName));
        }else{
//            mWfIcon.setImage(mHusbandAvatar, false, 44, "RkAddNeighbourDialog", "head", mContext);
//            mHbIcon.setImage(mWifeAvatar, false, 44, "RkAddNeighbourDialog", "head", mContext);
            
            mHbIcon.displayImage(ImageLoader.getInstance(), mWifeAvatar, 
            		CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
    				CxGlobalParams.getInstance().getSmallImgConner());
    		
            mWfIcon.displayImage(ImageLoader.getInstance(), mHusbandAvatar, 
            		CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
    				CxGlobalParams.getInstance().getSmallImgConner());
            
            
            mFamilyName.setText(String.format(familyNameFormat, mHusbandName, mWifeName));
        }
		
		
	}
	
	@SuppressWarnings("static-access")
	private void popAgreeToast(){
		mDialogListener.refreshUiAndData();
		CxAddNeighbourDialog.this.dismiss();
//		Toast toast = Toast.makeText(mContext, mContext.getResources().getString(R.string.cx_fa_add_neighbour_already_send_tip), Toast.LENGTH_LONG);
//		toast.setGravity(Gravity.BOTTOM, 0, 0);
//		
////		LinearLayout toastView = (LinearLayout) toast.getView();
////		ImageView imageCodeProject = new ImageView(mContext);
////		imageCodeProject.setImageResource(R.drawable.send_button);
////		toastView.addView(imageCodeProject, 0);
//		toast.show();
//		Message message = Message.obtain(RkNeighbourList.getInstance().neighbourHandler, RkNeighbourList.UPDATE_NEIGHBOUR_LIST_DATA);
//		message.sendToTarget();
	}
	
	private void popErrorToast(){
	  CxAddNeighbourDialog.this.dismiss();
//	  Toast toast = Toast.makeText(mContext, mErrorMsg, Toast.LENGTH_LONG);
//      toast.setGravity(Gravity.BOTTOM, 0, 0);
//      toast.show();
	  
	  ToastUtil.getSimpleToast(getContext(), -1, mErrorMsg, 1).show();
	}
	
	private void addNeighbour(){
		try {
			CxNeighbourApi.getInstance().requestNeighborInvite(mNeighbourNumber, new JSONCaller() {
				
				@Override
				public int call(Object result) {
					if( null == result){
						return -1;
					}
					JSONObject jObj = (JSONObject)result;
					try {
						int rc = jObj.getInt("rc");
						if( 0 == rc){
						    Message message = Message.obtain(mAddNeighbourHandler, POP_AGREE_TOAST);
	                        message.sendToTarget();
						} else {
						      mErrorMsg = jObj.getString("msg");
	                          Message message = Message.obtain(mAddNeighbourHandler, POP_ERROR_TOAST);
	                          message.sendToTarget();
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return 0;
				}
			});
		} catch (Exception e) {
			CxLog.e("RkAddNeighbourDialog/addNeighbour", "" + e.getMessage());
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.cx_fa_dialog_add_btn:
				addNeighbour();
				break;
			case R.id.cx_fa_dialog_cancel_btn:
				this.dismiss();
				break;
		}
	}

}
