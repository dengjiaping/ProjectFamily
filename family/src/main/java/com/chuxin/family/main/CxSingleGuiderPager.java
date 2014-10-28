package com.chuxin.family.main;

import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * 
 * @author shichao.wang
 *
 */
public class CxSingleGuiderPager extends Fragment {

	private static final String sShowEntryBtn = "show_entry_btn";
	private static final String sImgPath = "img_path";
	
	private int mImagePath = R.drawable.cx_fa_role_login_introduction1;
	private boolean mShowEntry = false;
	
	private CxImageView imageview;
//	private ImageButton mEntryBtn;
	private CxImageView imageview2;
	
	public static CxSingleGuiderPager getInstance(int drawableId, boolean showEntry){
		CxSingleGuiderPager tempPage = new CxSingleGuiderPager();
		Bundle args = new Bundle();
		args.putBoolean(sShowEntryBtn, showEntry);
		args.putInt(sImgPath, drawableId);
		tempPage.setArguments(args);
		return tempPage;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = getArguments();
		if (null != data) {
			mImagePath = data.getInt(sImgPath);
			mShowEntry = data.getBoolean(sShowEntryBtn);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View tempView = inflater.inflate(R.layout.cx_fa_fragment_guider_page, null);
		imageview = (CxImageView)tempView.findViewById(R.id.cx_fa_guider_imageview);
//		mEntryBtn = (ImageButton)tempView.findViewById(R.id.cx_fa_guider_entry_btn);
		imageview2 = (CxImageView) tempView.findViewById(R.id.cx_fa_guider_imageview2);
		
//		RkLog.i("RkSingleGuidePager_men", mShowEntry+"");
		
		if (mShowEntry) {
			/*mEntryBtn.setVisibility(View.VISIBLE);
			mEntryBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					((RkAuthenNew)getActivity()).completeGuider();
				}
			});*/
			imageview.setVisibility(View.GONE);	
			imageview2.setVisibility(View.VISIBLE);	
			
		}else{
			imageview.setVisibility(View.VISIBLE);	
			imageview2.setVisibility(View.GONE);	
		}
		

		
		
		return tempView;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		imageview.setImageResource(mImagePath);
		super.onActivityCreated(savedInstanceState);
	}
	
}
