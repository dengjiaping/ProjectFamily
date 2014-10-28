package com.chuxin.family.mate;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.widgets.NiceEditText;
import com.chuxin.family.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
/**
 * 
 * @author wentong.men
 *
 */
public class CxMateAddCustom extends CxRootActivity implements OnClickListener{
	
	private NiceEditText etValue;
	private String mCustomTitle;
    private Button mReturnButton;
    private Button mSaveButton;	
    private LinearLayout naviLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_activity_mateprofile_addcustom);

		naviLayout = (LinearLayout)findViewById(R.id.cx_fa_activity_title_layout);
		naviLayout.setVisibility(View.VISIBLE);		
		
		etValue = (NiceEditText)findViewById(R.id.mateCustomerStr);	
		etValue.addTextChangedListener(new CustomTextWatcher());
		
    	mReturnButton = (Button)findViewById(R.id.cx_fa_activity_title_back);
		mSaveButton = (Button)findViewById(R.id.cx_fa_activity_title_more);
		mSaveButton.setVisibility(View.VISIBLE);
		
		mReturnButton.setText(getString(R.string.cx_fa_navi_back));
		mSaveButton.setText(getString(R.string.cx_fa_navi_save));		
		
		mReturnButton.setOnClickListener(this);
		mSaveButton.setOnClickListener(this);         
	}
	
	private class CustomTextWatcher implements TextWatcher {
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	    }

	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	    }

	    public void afterTextChanged(Editable s) {
	    	mCustomTitle = s.toString();
	    }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	@Override
	public void onClick(View v) {
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etValue.getWindowToken(),0) ;
		
		switch(v.getId()){
			case R.id.cx_fa_activity_title_back:
				
				break;
			case R.id.cx_fa_activity_title_more:
				if (mCustomTitle != null && mCustomTitle.length() > 0) {
					Intent mIntent = new Intent();  
					mIntent.putExtra("customTitle", mCustomTitle);  
					this.setResult(0, mIntent);  	
				}
				break;
			default:
				break;
		}
		
		finish();
	}
}
