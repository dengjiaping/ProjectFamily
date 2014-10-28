package com.chuxin.family.pair;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.net.CxPairApi;
import com.chuxin.family.parse.been.data.CxPairInitData;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxResourceManager;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.zone.CxUsersPairZone;
import com.chuxin.family.R;

public class CxApproveInvitation extends CxRootActivity {

	private CxImageView mHeadThumb;
	private Button mApproveButton;
	private ImageButton mIgnoreButton;
	private TextView mPairTip;
	private CxPairInitData init;
	public static boolean isVisible = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cx_fa_fragment_approve);
		
		Intent intent = getIntent();
		intExtra = intent.getIntExtra("from", 1);
		
		
		if (isVisible) {
			CxApproveInvitation.this.finish();
			return;
		}
		isVisible = true;
		
		Display display = getWindowManager().getDefaultDisplay();
		WindowManager.LayoutParams layoutParam = getWindow().getAttributes();
		layoutParam.width = (int)(display.getWidth() * 0.9);
//		layoutParam.height = (int)(display.getHeight() * 0.4);
		getWindow().setAttributes(layoutParam);
		
		mHeadThumb = (CxImageView)findViewById(R.id.invited_person_head);
		mApproveButton = (Button)findViewById(R.id.comfirm_pair);
		mIgnoreButton = (ImageButton)findViewById(R.id.cancel_pair);
		mPairTip = (TextView)findViewById(R.id.invited_person_name);
		
		String tipWord = getString(R.string.cx_fa_pair_tip_prefixtext);
		
		mApproveButton.setOnClickListener(buttonListener);
		mIgnoreButton.setOnClickListener(buttonListener);
		
		List<CxPairInitData> data = CxGlobalParams.getInstance().getInviteMePair();
		if ( (null == data) || (data.size() < 1) ) {
			this.finish();
			return;
		} //取出来的
		init = data.get(0);
		if (null == init) {
			this.finish();
			return;
		}
		/*mHeadThumb.setImage(init.getIcon(), true, 74, RkApproveInvitation.this, 
				"head", RkApproveInvitation.this);*/
		mHeadThumb.displayImage(imageLoader, init.getIcon(), 
				R.drawable.pair_logo, true, 
				CxGlobalParams.getInstance().getSmallImgConner());
		if ((null == init.getName()) || (init.getName().equalsIgnoreCase("null")) ) {
			mPairTip.setText(String.format(tipWord, getString(R.string.cx_fa_partner_text)));
		}else{
			mPairTip.setText(String.format(tipWord, init.getName()));
		}
		
	}
	
	OnClickListener buttonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			isVisible = false;
			if (R.id.comfirm_pair == v.getId()) {
				try {
					CxLog.i("RkPair_men", ">>>>>>>>>>1");
					if(intExtra==1){
						CxLog.i("RkPair_men", ">>>>>>>>>>2");
						CxPairApi.getInstance().approveInvite(init.getUid(), 
								CxPairRequest.getInstance().approveCaller);
						CxLog.i("RkPair_men", ">>>>>>>>>>22");
					}else{
						CxLog.i("RkPair_men", ">>>>>>>>>>3："+init.getUid());
						CxPairApi.getInstance().approveInvite(init.getUid(), 
								CxPairActivity.getInstance().approveCaller);
						CxLog.i("RkPair_men", ">>>>>>>>>>33");
					}
					
					CxApproveInvitation.this.finish();
//					RkLoadingUtil.getInstance().showLoading(
//							RkPairRequest.getInstance().getActivity(), false);
				} catch (Exception e) {
//					RkLoadingUtil.getInstance().dismissLoading();
				}
				
				return;
			}
			if (R.id.cancel_pair == v.getId()) {
				CxApproveInvitation.this.finish();
				return;
			}
		}
	};
	private int intExtra;
	
	@Override
	protected void onDestroy() {
		isVisible = false;
		
//		try {
//			CxResourceManager resourceManager = CxResourceManager.getInstance(
//					CxApproveInvitation.this, "head", CxApproveInvitation.this);
//			if (null != resourceManager) {
//				resourceManager.clearMemory();
//				resourceManager = null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		super.onDestroy();
	}
	
}
