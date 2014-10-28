package com.chuxin.family.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class ValidEditText extends EditText {
	
	private String mOldText = "";
	private Validation mValidation;
	
	public interface Validation {
		public boolean valid(String s);
	};
	
	public ValidEditText(Context context) {
		super(context);
		init();
	}
	
	public ValidEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public void setValidation(Validation validation) {
		mValidation = validation;
	}
	
	public void setValue(String value) {
		setText(value);
		checkValidation();
	}
	
	public boolean checkValidation() {
		String currText = getText().toString();
//		Log.d("ValidEditText", "currText:" + currText);
//		Log.d("ValidEditText", "oldText :" + mOldText);
		if (currText.equals(mOldText)) {
			return true;
		}
		
		if (mValidation != null) {
			if (!mValidation.valid(currText)) {
				// restore text;
				setText(mOldText);
				return false;
			}
		}
		
		mOldText = currText;
		return true;
	}

	private void init() {
		mValidation = null;
		
		setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					((ValidEditText)v).checkValidation();
				}
			}
			
		});

	}
}
