package com.chuxin.family.pair;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.family.app.CxApplication;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxAuthenGenderSelectorActivity;
import com.chuxin.family.main.CxAuthenNew;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.net.CxNeighbourApi;
import com.chuxin.family.net.CxPairApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxPairParser;
import com.chuxin.family.parse.been.CxCall;
import com.chuxin.family.parse.been.CxPairApprove;
import com.chuxin.family.parse.been.data.CxPairApproveData;
import com.chuxin.family.parse.been.data.CxPairInitData;
import com.chuxin.family.resource.CxResource;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.tabloid.CxTabloidActivity;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.widgets.NiceEditText;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.tauth.Tencent;
/**
 * 新结对的发送邀请界面
 * @author shichao
 *
 */
public class CxPairRequest extends Fragment {
	private final String TENCENT_APP_ID = "100360393";
	private TextView mInviteNumTip;
	private Button /*showDimesionCode,*/ sendPairRequest;
	private EditText requestNumber; //
	private static CxPairRequest mSelf;
	
	private Button mMenuBtn;
	private Button mSendBtn1;
	private String mInviteNumStr = null;
	
	private AlertDialog mInviteDialog;
	
	private final String PROFILE_FIELD_PAIR = "pair";
	private final String PROFILE_FIELD_PAIR_ID = "pair_id";
	private final String PROFILE_FILE_NAME = "profile_name";
	private final String PROFILE_FIELD_MATE_ID = "mate_id";
	private final String PROFILE_FIELD_SINGLE_MODE = "single_mode";
	
//	private Tencent mTencent;
	
	public static CxPairRequest getInstance(){
		return mSelf;
	}
	
	@Override
	public void onAttach(Activity activity) {
//		mTencent = Tencent.createInstance(TENCENT_APP_ID, activity.getApplicationContext());
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View tempView = inflater.inflate(R.layout.cx_fa_fragment_ready_invit, null);
		
//		ImageView mImg = (ImageView) tempView.findViewById(R.id.cx_fa_pair_img);
//		mImg.setImageResource(RkResourceDarwable.getInstance().dr_pair_versiontip);
		
		mMenuBtn = (Button)tempView.findViewById(R.id.cx_fa_invite_menu);
		mSendBtn1 = (Button)tempView.findViewById(R.id.cx_fa_invite_send);
		mSendBtn2 = (Button)tempView.findViewById(R.id.cx_fa_invite_send2);
		mSendBtn2.setText(CxResourceString.getInstance().str_pair_invite_oppo_btn_text);
		
//		mSendBtn2.setText(getString(RkResourceString.getInstance().str_pair_invite_partner_text));
		
		mMenuBtn.setOnClickListener(clicker);
		mSendBtn1.setOnClickListener(clicker);
		mSendBtn2.setOnClickListener(clicker);
		
//		myInvitedNumberText = (TextView)tempView.findViewById(R.id.cx_fa_my_invite_number);
//		showDimesionCode = (Button)tempView.findViewById(R.id.cx_fa_dimessionbar_btn);
		
		TextView mTopText = (TextView)tempView.findViewById(R.id.cx_fa_pair_top_word_tv);
		mTopText.setText(CxResourceString.getInstance().str_pair_top_word);
		
		mInviteNumTip = (TextView)tempView.findViewById(R.id.cx_fa_invite_num_tip);
		
		sendPairRequest = (Button)tempView.findViewById(R.id.cx_fa_request_pair);
		requestNumber = (EditText)tempView.findViewById(R.id.cx_fa_request_pair_number_code);
		requestNumber.setHint(CxResourceString.getInstance().str_pair_edit_hint);
		
//		showDimesionCode.setOnClickListener(clicker);
		sendPairRequest.setOnClickListener(clicker);
		
		//先检测自己的邀请码本地是否存在，存在就不做任何动作，否则去网络获取邀请码
		SharedPreferences spf = getActivity().getSharedPreferences(
				CxGlobalConst.S_PAIR_CH_NAME, Context.MODE_PRIVATE);
		String inviteTextTip = getString(CxResourceString.getInstance().str_pair_congratulation);
		String inviteNumStr = spf.getString(CxGlobalConst.S_PAIR_CH_FIELD, null);
		if (null != inviteNumStr) { //本地有
			//此情况先显示后发请求
			mInviteNumTip.setText(String.format(inviteTextTip, inviteNumStr));
			mInviteNumStr = inviteNumStr;
		}else{ //本地无邀请码，网络请求获取邀请码
			mInviteNumTip.setText("验证码读取中...");
		}
		CxPairApi.getInstance().callPair(null, myInvitedNumber); //备注：解除结对时需要清除本地的自己的邀请码
		
		mSelf = CxPairRequest.this;
		
		((CxMain)getActivity()).closeMenu();
		
		return tempView;
	}
	
	public void onStart() {
		super.onStart();
		mSendBtn1.setText(getString(CxResourceString.getInstance().str_pair_invite_text));
	};
	
	OnClickListener clicker = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_request_pair: //发送结对邀请
				//
				if (TextUtils.isEmpty(requestNumber.getText().toString())) {
					Toast noneInviteNumber = Toast.makeText(getActivity(), 
							getActivity().getString(R.string.cx_fa_input_invite_number), Toast.LENGTH_LONG);
					noneInviteNumber.setGravity(Gravity.CENTER, 0, 0);
					noneInviteNumber.show();
					return;
				}
				if (requestNumber.getText().toString().equalsIgnoreCase(mInviteNumStr)
						/*TextUtils.equals(mInviteNumStr, requestNumber.getText().toString())*/) {
					Toast noneInviteNumber = Toast.makeText(getActivity(), 
							getActivity().getString(R.string.cx_fa_same_invitenum_with_me), Toast.LENGTH_LONG);
					noneInviteNumber.setGravity(Gravity.CENTER, 0, 0);
					noneInviteNumber.show();
					return;
				}
				
				v.setEnabled(false);
				try {
//					RkLoadingUtil.getInstance().showLoading(getActivity(), true);
					DialogUtil.getInstance().getLoadingDialogShow(getActivity(), -1);
					CxPairApi.getInstance().callPair(requestNumber.getText().toString(), sendInviteRequest);
				} catch (Exception e1) {
					DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
					e1.printStackTrace();
				}
				break;
			/*case R.id.cx_fa_dimessionbar_btn: //显示二维码
				AlertDialog dlg = new AlertDialog.Builder(getActivity()).create();
				dlg.show();
				ImageView img = new ImageView(getActivity());
				img.setImageResource(R.drawable.cx_fa_role_qrcode);
				img.setScaleType(ScaleType.CENTER_INSIDE);
				dlg.setContentView(img);
				break;*/
			case R.id.cx_fa_invite_menu:
				try {
					((CxMain)getActivity()).toggleMenu();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
//			case R.id.cx_fa_invite_type: 
//				invitePartner();
//				break;
			case R.id.cx_fa_invite_send: 
			case R.id.cx_fa_invite_send2: 
				invitePartner();
				break;
			case R.id.invite_mate_by_sms:
				if ((null != mInviteDialog) && (mInviteDialog.isShowing())) {
					mInviteDialog.dismiss();
				}
				mInviteDialog = null;
				if (TextUtils.isEmpty(mInviteNumStr)) {
					Toast.makeText(getActivity(), getString(
							R.string.cx_fa_no_invite_number), Toast.LENGTH_SHORT).show();
					return;
				}
                String smsContentStr = String.format(
                        getString(CxResourceString.getInstance().str_pair_invite_word), mInviteNumStr);
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.putExtra("sms_body", smsContentStr);
                i.setType("vnd.android-dir/mms-sms");
                startActivity(i);
				break;
			case R.id.invite_mate_by_wx:
				if ((null != mInviteDialog) && (mInviteDialog.isShowing())) {
					mInviteDialog.dismiss();
				}
				mInviteDialog = null;
				if ((null != mInviteDialog) && (mInviteDialog.isShowing())) {
					mInviteDialog.dismiss();
				}
				mInviteDialog = null;
				if (TextUtils.isEmpty(mInviteNumStr)) {
					Toast.makeText(getActivity(), getString(
							R.string.cx_fa_no_invite_number), Toast.LENGTH_SHORT).show();
					return;
				}
                String wxContentStr = String.format(
                        getString(CxResourceString.getInstance().str_pair_invite_word), mInviteNumStr);
				if(CxAuthenNew.api.isWXAppInstalled() && CxAuthenNew.api.isWXAppSupportAPI()){
			        WXTextObject textObj = new WXTextObject();
			        textObj.text = wxContentStr;
			        
			        WXMediaMessage msg = new WXMediaMessage();
			        
			        msg.mediaObject = textObj;
			        msg.description = wxContentStr;
			        SendMessageToWX.Req req = new SendMessageToWX.Req();
			        req.transaction = String.valueOf(System.currentTimeMillis());
			        req.message = msg;
			        req.scene = 0;
			        
			        CxAuthenNew.api.sendReq(req);
		    	} else {
		    		Toast.makeText(CxPairRequest.this.getActivity(), 
		    				getString(R.string.cx_fa_has_no_wx), Toast.LENGTH_LONG).show();
		    	}
				
				break;
			case R.id.invite_mate_by_qr:
				if ((null != mInviteDialog) && (mInviteDialog.isShowing())) {
					mInviteDialog.dismiss();
				}
				mInviteDialog = null;
				AlertDialog dlg = new AlertDialog.Builder(getActivity()).create();
				dlg.show();
				ImageView img = new ImageView(getActivity());
				img.setImageResource(R.drawable.cx_fa_invite_qr_code);
				img.setScaleType(ScaleType.CENTER_INSIDE);
				dlg.setContentView(img);
				break;
			case R.id.invite_mate_cancel:
				if ((null != mInviteDialog) && (mInviteDialog.isShowing())) {
					mInviteDialog.dismiss();
				}
				mInviteDialog = null;
				break;
				
			default:
				break;
			}
			
		}
	};
	
	/**
	 * 邀请对对方
	 */
	private void invitePartner(){
		AlertDialog.Builder adb = new AlertDialog.Builder(
				new ContextThemeWrapper(getActivity(), R.style.inviteDialogCustom)
			);  
		adb.setTitle("");
		adb.setItems(new String[]{							
				getString(CxResourceString.getInstance().str_pair_invite_by_dx),
				/*getString(R.string.cx_fa_role_invite_by_qq),*/
				getString(CxResourceString.getInstance().str_pair_invite_by_wx),
				getString(CxResourceString.getInstance().str_pair_invite_by_qr),
				getString(R.string.cx_fa_cancel_button_text)}, 
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				int type=-1;
				
				switch (arg1) {
				case 0: //短信邀请
					if (TextUtils.isEmpty(mInviteNumStr)) {
						Toast.makeText(getActivity(), getString(
								R.string.cx_fa_no_invite_number), Toast.LENGTH_SHORT).show();
						return;
					}
	                String smsContentStr = String.format(
	                        getString(CxResourceString.getInstance().str_pair_invite_word), mInviteNumStr);
	                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
	                i.putExtra("sms_body", smsContentStr);
	                i.setType("vnd.android-dir/mms-sms");
	                startActivity(i);
	                type=3;
					break;
				case 1: //微信邀请
					if (TextUtils.isEmpty(mInviteNumStr)) {
						Toast.makeText(getActivity(), getString(
								R.string.cx_fa_no_invite_number), Toast.LENGTH_SHORT).show();
						return;
					}
	                String wxContentStr = String.format(
	                        getString(CxResourceString.getInstance().str_pair_invite_word), mInviteNumStr);
					if(CxAuthenNew.api.isWXAppInstalled() && CxAuthenNew.api.isWXAppSupportAPI()){
				        WXTextObject textObj = new WXTextObject();
				        textObj.text = wxContentStr;
				        
				        WXMediaMessage msg = new WXMediaMessage();
				        
				        msg.mediaObject = textObj;
				        msg.description = wxContentStr;
				        SendMessageToWX.Req req = new SendMessageToWX.Req();
				        req.transaction = String.valueOf(System.currentTimeMillis());
				        req.message = msg;
				        req.scene = 0;
				        
				        CxAuthenNew.api.sendReq(req);
				        type=2;
			    	} else {
			    		Toast.makeText(CxPairRequest.this.getActivity(), 
			    				getString(R.string.cx_fa_has_no_wx), Toast.LENGTH_LONG).show();
			    	}
					break;
				case 2: //二维码邀请
					DialogUtil.getInstance().getCodeDialogShow(getActivity(), R.drawable.cx_fa_invite_qr_code, 
							getString(CxResourceString.getInstance().str_pair_invite_code_dialog_text), 
							getString(CxResourceString.getInstance().str_pair_invite_code_dialog_text2));
					type=4;
					break;
				/*case 1: //QQ邀请
					String contentStr = "";
			        String redirectUrl = "";
					if(0 == RkGlobalParams.getInstance().getVersion()){ //老婆版邀请老婆
						contentStr = String.format(getString(R.string.cx_fa_role_invite_word), mInviteNumStr);
			            redirectUrl = "http://m.family.chuxin.net/dl/wife";
			        } else { //老公版邀请老公
			        	contentStr = String.format(
		                        getString(R.string.cx_fa_role_invite_word), mInviteNumStr);
			            redirectUrl = "http://m.family.chuxin.net/dl/husband";
			        }
					Bundle params = new Bundle();
					params.putString(Constants.PARAM_TITLE,"");
				    params.putString(Constants.PARAM_IMAGE_URL,
				            "http://app100724043.chuxin.net/family/common/group/weixin_108.png");
				    params.putString(Constants.PARAM_TARGET_URL,
				            redirectUrl);
				    params.putString(Constants.PARAM_SUMMARY, contentStr);
				    mTencent.shareToQQ(getActivity(), params, new IUiListener() {
			            
			            @Override
			            public void onError(UiError arg0) {
			                Toast.makeText(getActivity(), "发送失败", Toast.LENGTH_LONG).show();
			            }
			            
			            @Override
			            public void onComplete(JSONObject arg0) {
			                Toast.makeText(getActivity(), "发送成功", Toast.LENGTH_LONG).show();
			            }
			            
			            @Override
			            public void onCancel() {
			                Toast.makeText(getActivity(), "发送取消", Toast.LENGTH_LONG).show();
			            }
			        });
				    type=1;
					break;*/
				default:
					break;
				}
				if(0<type && type<9){ //统计
		    		CxNeighbourApi.getInstance().requestInviteType("invite",type, null);
				}
			}
		});
		adb.show();
	}
	
	//自己的邀请码获取的回调
	JSONCaller myInvitedNumber = new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if (null == result) {//异常
				return -1;
			}
			CxCall callDetail = null;
			try {
				callDetail = (CxCall)result;
			} catch (Exception e) {
			}
			if (null == callDetail) {//异常
				return -2;
			}
			if(0 != callDetail.getRc()){ //服务器告知失败
				return 1;
			}
			if((null == callDetail.getData()) || 
					(null == callDetail.getData().getInvite_code()) ){ //
				return 3;
			}
			//以下是正常获取到自己的邀请码的情况
			Handler updateMyInviteNumber = new Handler(Looper.getMainLooper()){
				public void handleMessage(android.os.Message msg) {
					if ((null == msg) || (null == msg.obj)) {
						return;
					}
					String inviteTextTip = CxApplication.getInstance().getString(CxResourceString.getInstance().str_pair_congratulation);
					String inviteNumStr = String.format(inviteTextTip, msg.obj.toString());
					mInviteNumTip.setText(inviteNumStr);
				};
			};
			
			SharedPreferences sp = CxApplication.getInstance().getSharedPreferences(
					CxGlobalConst.S_PAIR_CH_NAME, Context.MODE_PRIVATE);
			Editor edt = sp.edit();
			edt.putString(CxGlobalConst.S_PAIR_CH_FIELD, callDetail.getData().getInvite_code());
			edt.commit();
			
			mInviteNumStr = callDetail.getData().getInvite_code();
			
			Message msg = new Message();
			msg.obj = callDetail.getData().getInvite_code();
			updateMyInviteNumber.sendMessage(msg);
			return 0;
		}
	};
	
	//发送邀请的回调
	JSONCaller sendInviteRequest = new JSONCaller() {
		
		@Override
		public int call(Object result) {
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
			new Handler(getActivity().getMainLooper()){
				public void handleMessage(Message msg) {
					sendPairRequest.setEnabled(true);
//					RkLoadingUtil.getInstance().dismissLoading();
				};
			}.sendEmptyMessageDelayed(1, 20);
			
			if (null == result) {//异常
				showMessageInToast(getActivity().getString(R.string.cx_fa_net_err));
				return -1;
			}
			CxCall callDetail = null;
			try {
				callDetail = (CxCall)result;
			} catch (Exception e) {
			}
			if (null == callDetail) {//异常
				showMessageInToast(getActivity().getString(R.string.cx_fa_net_err));
				return -2;
			}
			
			
			if(3005==callDetail.getRc()){
				new Handler(getActivity().getMainLooper()){
					public void handleMessage(Message msg) {
						showSameGenderDialog();
					}
				}.sendEmptyMessage(0);
				return 5;
			}
			
			
			
			if(0 != callDetail.getRc()){ //服务器告知失败
				showMessageInToast(callDetail.getMsg());
				return 1;
			}
			if(null == callDetail.getData()){ //应该也属于异常
				showMessageInToast(callDetail.getMsg());
				return 3;
			}
			if ( (null == callDetail.getData().getMatchs()) 
					|| (callDetail.getData().getMatchs().size() < 1) ) { //在服务器上没有与邀请码对应的人
				showMessageInToast(callDetail.getMsg());
				return 4;
			}
			
			final List<CxPairInitData> matchs = callDetail.getData().getMatchs();
			new Handler(getActivity().getMainLooper()){
				public void handleMessage(Message msg) {
					showApproveDialog(matchs);
				}
			}.sendEmptyMessage(0);
			
			
			//以下是有人匹配的情况，弹窗
//			RkGlobalParams.getInstance().setInviteMePair(callDetail.getData().getMatchs()); //先set数据
//			
//			/*Intent inviteMeAction = new Intent(getActivity(), RkInvitations.class);
//			startActivity(inviteMeAction);*/
//			Intent inviteMeAction = new Intent(getActivity(), RkApproveInvitation.class);
//			inviteMeAction.putExtra("from", 1);
//			startActivity(inviteMeAction);
			return 0;
		}
	};
	
	
	private void showApproveDialog(List<CxPairInitData> matchs ){
		View view = View.inflate(getActivity(), R.layout.cx_fa_widget_pair_approve_dialog,null);
		CxImageView iconImg = (CxImageView) view.findViewById(R.id.cx_fa_pair_approve_icon_img);
		TextView pairText = (TextView) view.findViewById(R.id.cx_fa_pair_approve_text_tv);
		ImageButton cancelBtn = (ImageButton) view.findViewById(R.id.cx_fa_pair_approve_cancel_btn);
		Button okBtn = (Button) view.findViewById(R.id.cx_fa_pair_approve_sure_btn);
		
		
		
		final Dialog dialog=new Dialog(getActivity(), R.style.simple_dialog);		
		dialog.setContentView(view);	
		
		final CxPairInitData data = matchs.get(0);
		if(data==null){
			dialog.dismiss();
			return;
		}
		iconImg.setImageResource(CxResourceDarwable.getInstance().dr_chat_icon_small_oppo);
		iconImg.displayImage(ImageLoader.getInstance(), data.getIcon(), 
				CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
				CxGlobalParams.getInstance().getSmallImgConner());
		String tipWord = getString(R.string.cx_fa_pair_tip_prefixtext);
		if ((null == data.getName()) || (data.getName().equalsIgnoreCase("null")) ) {
			pairText.setText(String.format(tipWord, getString(R.string.cx_fa_partner_text)));
		}else{
			pairText.setText(String.format(tipWord, data.getName()));
		}
		
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();				
			}
		});
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					dialog.dismiss();
					DialogUtil.getInstance().getLoadingDialogShow(getActivity(), -1);
					CxPairApi.getInstance().approveInvite(data.getUid(),approveCaller);
				} catch (Exception e) {	
					DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
					e.printStackTrace();
				}
									
			}
		});
		dialog.show();
	}
	
	
	
	/**
	 * 角色冲突的dialog
	 */
	private void showSameGenderDialog() {
		DialogUtil du = DialogUtil.getInstance();
		String str=CxApplication.getInstance().getString(CxResourceString.getInstance().str_pair_role_clash_text);
		du.getSimpleDialog(getActivity(), null, str, null, null).show();
		du.setOnSureClickListener(new OnSureClickListener() {
			
			@Override
			public void surePress() {
				Intent intent = new Intent(getActivity(), CxAuthenGenderSelectorActivity.class);	
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				getActivity().finish();
			}
		});
	};
	
	
	private void showMessageInToast(String message){
		Handler updateMyInviteNumber = new Handler(Looper.getMainLooper()){
			public void handleMessage(android.os.Message msg) {
				if ((null == msg) || (null == msg.obj)) {
					return;
				}
				Toast netErrorToast = Toast.makeText(getActivity(), 
						msg.obj.toString(), Toast.LENGTH_LONG);
				netErrorToast.setGravity(Gravity.CENTER, 0, 0);
				netErrorToast.show();
			};
		};
		Message msg = new Message();
		msg.obj = message;
		updateMyInviteNumber.sendMessage(msg);
	}
	
	public JSONCaller approveCaller = new JSONCaller() {
		
		@Override
		public int call(Object result) {
//			RkLoadingUtil.getInstance().dismissLoading();
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
			if (null == result) {
				showMessageInToast(getString(R.string.cx_fa_invite_faild));
				return -1;
			}
			CxLog.i("RkPairApi_men", result.toString());
			CxPairApprove approve = null;
			try {
				approve = new CxPairParser().parseApprove(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null == approve) {
				//不处理
				showMessageInToast(getString(R.string.cx_fa_invite_faild));
				return -2;
			}
			if (0 == approve.getRc()) { //结对请求成功（不是结对成功）
				//改变global中结对标志位
				CxPairApproveData approveData = approve.getData();
				if (null != approveData) {
					SharedPreferences sp = CxApplication.getInstance().getContext().getSharedPreferences(PROFILE_FILE_NAME, Context.MODE_PRIVATE);
					Editor edit = sp.edit();
					
					CxGlobalParams.getInstance().setPairId(approveData.getPair_id());
					CxGlobalParams.getInstance().setPartnerId(approveData.getPartner_id());
					
					edit.putString(PROFILE_FIELD_PAIR_ID, approveData.getPair_id());
					edit.putString(PROFILE_FIELD_MATE_ID, approveData.getPartner_id());
					
//					notifyPairSuccess(); //弹框确认结对成功，确定后修改RkGlobalParams中的kpair值
					//2013.9.25梦飞确定主动结对成功不需要弹出对话框提示，对应278号bug
					int pair = CxGlobalParams.getInstance().getPair();
					if(pair==0){ //未结对状态下的结对
						CxGlobalParams.getInstance().setPair(1); //进一步反馈给观察者
						edit.putInt(PROFILE_FIELD_PAIR, 1);
					}else{//单人模式状态下的结对
						CxGlobalParams.getInstance().setSingle_mode(0); //进一步反馈给观察者
						edit.putInt(PROFILE_FIELD_SINGLE_MODE, 0);
					}
					edit.commit();
				}else{
					showMessageInToast(getString(R.string.cx_fa_invite_faild));
				}
				
			}else{ //结对失败
				showMessageInToast(getString(R.string.cx_fa_invite_faild));
			}
			return 0;
		}
	};
	private Button mSendBtn2;

//	private void notifyPairSuccess(){
//		Handler updateMyInviteNumber = new Handler(Looper.getMainLooper()){
//			public void handleMessage(android.os.Message msg) {
//				AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
//				ad.setMessage(getString(R.string.cx_fa_pair_success_text));
//				ad.setButton(getString(R.string.cx_fa_confirm_text), 
//						new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						//改变global中结对标志位
//						CxGlobalParams.getInstance().setPair(1); //进一步反馈给观察者
//						
//					}
//				});
//				
//				ad.show();
//			};
//		};
//		Message msg = new Message();
//		msg.obj = "";
//		updateMyInviteNumber.sendMessage(msg);
//	}
	
}
