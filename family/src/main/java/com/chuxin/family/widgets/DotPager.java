package com.chuxin.family.widgets;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DotPager extends LinearLayout {   
    
	private int mPageNum;   
    private int mPageIndex;
    private int mLastPageIndex;
    
    private int mPointResId;
    private int mSelectPointResId;
    private int mScrollDotMargin;

    public DotPager(Context context, int pageNum, int pageIndex, int pointResId, int selectPointResId, int margin) {   
        super(context);
        
        mLastPageIndex = -1;
        mPageNum = pageNum;
        mPageIndex = pageIndex;
        mPointResId = pointResId;
        mSelectPointResId = selectPointResId;
        mScrollDotMargin = margin;
        
        init();
    }

    private void init() {
    	this.setOrientation(LinearLayout.HORIZONTAL);
    	this.setGravity(Gravity.CENTER);
    	
    	ImageView imageView;
    	for (int i = 0; i < mPageNum; i++) {
    		imageView = new ImageView(getContext());
    		if (i == mPageIndex) {
    			imageView.setImageResource(mSelectPointResId);
    		} else {
    			imageView.setImageResource(mPointResId);
    		}
    		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    		layoutParams.setMargins(mScrollDotMargin, 0, mScrollDotMargin, 0);
    		imageView.setLayoutParams(layoutParams);
    		addView(imageView);
    	}
    }

       
    public int getPageNum() {   
        return mPageNum;   
    }   
       
    public void setPageIndex(int pageIndex) {   
        mPageIndex = pageIndex;
        refresh();
    }   
       
    public int getPageIndex() {   
        return mPageIndex;   
    }   

    public void refresh() {
    	if (mLastPageIndex != mPageIndex) {
        	ImageView imageView;
        	
        	if (mLastPageIndex >= 0) {
	    		imageView = (ImageView)getChildAt(mLastPageIndex);
	    		imageView.setImageResource(mPointResId);
        	}
    		
    		imageView = (ImageView)getChildAt(mPageIndex);
    		imageView.setImageResource(mSelectPointResId);
    	}
    	mLastPageIndex = mPageIndex;
    } 
}
