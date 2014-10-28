package com.chuxin.family.main;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;


public class CollapseAnimation extends TranslateAnimation implements 
TranslateAnimation.AnimationListener{
	
	private FrameLayout mSlidingLayout;
	int mMenuFramWidth;

	public CollapseAnimation(FrameLayout layout, int width, int fromXType, 
			float fromXValue, int toXType,float toXValue, int fromYType, 
			float fromYValue, int toYType, float toYValue) {
		
		super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, 
				toYType, toYValue);
		
		mSlidingLayout = layout;
		mMenuFramWidth = width;
		setDuration(600);
	    setFillAfter( false );
	    setInterpolator(new AccelerateDecelerateInterpolator());
	    setAnimationListener(this);
	    
	    LayoutParams params = (LayoutParams) mSlidingLayout.getLayoutParams();
  	   	params.rightMargin = 0;
  	   	params.leftMargin = 0;
  	    mSlidingLayout.setLayoutParams(params);
  	    mSlidingLayout.requestLayout();
  	    mSlidingLayout.startAnimation(this);
  	   	
	}
	public void onAnimationEnd(Animation animation) {
	
	}

	public void onAnimationRepeat(Animation animation) {
		
	}

	public void onAnimationStart(Animation animation) {
		
	}
	
}
