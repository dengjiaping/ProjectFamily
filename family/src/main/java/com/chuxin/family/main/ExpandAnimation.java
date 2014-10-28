package com.chuxin.family.main;

import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
/**
 * 
 * @author shichao.wang
 *
 */
public class ExpandAnimation extends TranslateAnimation implements 
Animation.AnimationListener{

	private FrameLayout mSlidingLayout;
	int mMenuFramWidth;
	
	public ExpandAnimation(FrameLayout layout, int width, int fromXType, 
			float fromXValue, int toXType,float toXValue, int fromYType,
			float fromYValue, int toYType, float toYValue) {
		
		super(fromXType, fromXValue, toXType, toXValue, fromYType, 
				fromYValue, toYType, toYValue);
		
		mSlidingLayout = layout;
		mMenuFramWidth = width;
		setDuration(600);
  	    setFillAfter( false );
  	    setInterpolator(new AccelerateDecelerateInterpolator());
  	    setAnimationListener(this);
  	    mSlidingLayout.startAnimation(this);
	}


	public void onAnimationEnd(Animation arg0) {
		
		LayoutParams params = (LayoutParams) mSlidingLayout.getLayoutParams();
  	   	params.leftMargin = mMenuFramWidth;
  	   	params.gravity = Gravity.LEFT;	   
  	    mSlidingLayout.clearAnimation();
  	    mSlidingLayout.setLayoutParams(params);
  	    mSlidingLayout.requestLayout();
  	  			
	}

	public void onAnimationRepeat(Animation arg0) {
		
	}

	public void onAnimationStart(Animation arg0) {
		
	}

}
