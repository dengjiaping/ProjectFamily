package com.chuxin.family.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chuxin.family.R;
/**
 * 
 * @author shichao.wang
 *
 */
public class NiceEditText extends RelativeLayout {
    
    private EditText mEditText;
    private ImageView mImageView;
    private String mTip;
    private int mMaxLength;
    private int mColor;

    public NiceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.cx_fa_decl_nice_edit_text_attrs);
        mTip = typedArray.getString(R.styleable.cx_fa_decl_nice_edit_text_attrs_tip);
        mMaxLength = typedArray.getInt(R.styleable.cx_fa_decl_nice_edit_text_attrs_maxLength, -1);
        mColor = typedArray.getInt(R.styleable.cx_fa_decl_nice_edit_text_attrs_textColor, Color.BLACK);
        typedArray.recycle();
        
        init();
    }

    protected void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.cx_fa_widget_nice_edit_text, this);

        mEditText = (EditText)findViewById(R.id.cx_fa_widget_nice_edit_text__edit);
        mImageView = (ImageView)findViewById(R.id.cx_fa_widget_nice_edit_text__erase);

        if (mMaxLength > 0) {
        	mEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(mMaxLength) });
        }
        mEditText.setTextColor(mColor);
        mEditText.setHint(mTip);
        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = mEditText.getText().toString();
                if (text.length() == 0) {
                    disableQuickRemoveButton();
                } else {
                    enableQuickRemoveButton();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            
        });
        
        mImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setText("");
            }
            
        });
        
        if (getText().length() > 0) {
        	enableQuickRemoveButton();
        } else {
        	disableQuickRemoveButton();
        }
    }

    public void setText(String text) {
    	mEditText.setText(text);
    	if(text != null){
    		// 设置光标移动到最后。
    		mEditText.setSelection(text.length());
    	}
    }

    public Editable getText() {
        return mEditText.getText();
    }
    
    private void disableQuickRemoveButton() {
        mImageView.setVisibility(INVISIBLE);
    }
    
    private void enableQuickRemoveButton() {
        mImageView.setVisibility(VISIBLE);
    }

    /**
     * 设置InputType
     * @param inputType
     * add by niechao
     */
    public void setInputType(int inputType){
    	mEditText.setInputType(inputType);
    }
    
    public void setOnFocusChangeListener(OnFocusChangeListener phoneListener){
    	mEditText.setOnFocusChangeListener(phoneListener);
    }
    
    public void addTextChangedListener(TextWatcher watcher){
    	mEditText.addTextChangedListener(watcher);
    }
    
    public void setTextColor(int color){
    	mEditText.setTextColor(color);
    }
    
}
