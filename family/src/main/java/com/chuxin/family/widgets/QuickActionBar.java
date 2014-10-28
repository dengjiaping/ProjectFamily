package com.chuxin.family.widgets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.chuxin.family.R;

public class QuickActionBar {
	
	private final static String TAG = "QuickActionBar";
	
	public static final int DISPLAY_X_LEFT_OF_ANCHOR   = 0x01;
	public static final int DISPLAY_X_RIGHT_OF_ANCHOR   = 0x01;
	public static final int DISPLAY_X_MIDDLE_OF_ANCHOR   = 0x04;
	public static final int DISPLAY_X_EXACT_MIDDLE_OF_ANCHOR   = 0x08;
	
	public static final int DISPLAY_Y_ABOVE_OF_ANCHOR = 0x01;
	public static final int DISPLAY_Y_BELOW_OF_ANCHOR = 0x02;
	public static final int DISPLAY_Y_MIDDLE_OF_ANCHOR   = 0x04;
	public static final int DISPLAY_Y_EXACT_MIDDLE_OF_ANCHOR   = 0x08;

	private int mPopupWindowResouceId;
	private PopupWindow mPopupWindow;
	private int mXDisplayStyle;
	private int mYDisplayStyle;
	private int mAnimationStyle;
	private View mAnchorView;
	
	private HashMap<Integer, OnClickListener> mClickMap = new HashMap<Integer, OnClickListener>();

	public QuickActionBar(int resourceId) {
		mPopupWindowResouceId = resourceId;
		mXDisplayStyle = DISPLAY_X_RIGHT_OF_ANCHOR;
		mYDisplayStyle = DISPLAY_Y_BELOW_OF_ANCHOR;
		mAnimationStyle = R.style.cx_fa_style_quick_action_bar_animation_default;
	}

	public QuickActionBar(int resourceId, int xDisplayStyle, int yDisplayStyle) {
		mPopupWindowResouceId = resourceId;
		mXDisplayStyle = xDisplayStyle;
		mYDisplayStyle = yDisplayStyle;
		mAnimationStyle = R.style.cx_fa_style_quick_action_bar_animation_default;
	}

	public QuickActionBar(int resourceId, int xDisplayStyle, int yDisplayStyle, int animationStyle) {
		mPopupWindowResouceId = resourceId;
		mXDisplayStyle = xDisplayStyle;
		mYDisplayStyle = yDisplayStyle;
		mAnimationStyle = animationStyle;
	}
	
	public void setOnClickListener(int resourceId, OnClickListener listener) {
		mClickMap.put(resourceId, listener);
	}

	public void installListener() {
		class OnClickListenerWrapper implements OnClickListener {
			private OnClickListener mListener;
			public OnClickListenerWrapper(OnClickListener listener) {
				mListener = listener;
			}

			@Override
			public void onClick(View obj) {
				mListener.onClick(mAnchorView);
			}
			
		}
		
		Iterator<Entry<Integer, OnClickListener>> iterator = mClickMap.entrySet().iterator();
		View view = null;
		while (iterator.hasNext()) {
			Entry<Integer, OnClickListener> entry = iterator.next();
			view = mPopupWindow.getContentView().findViewById(entry.getKey());
			if (view != null)
				view.setOnClickListener(new OnClickListenerWrapper(entry.getValue()));
		}
	}

	
	public void dismiss() {
		show(null, null);
	}
	
	private void measure(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)); 		
	}
	
	public void show(View anchor, View contentview) {
		if (anchor == null) {
			if ((mPopupWindow != null) && (mPopupWindow.isShowing())) {
				mPopupWindow.dismiss();
			}
			mAnchorView = null;
			return;
		}
		
		mAnchorView = anchor;

		View contentView = null;
		if (mPopupWindow == null) {
			//contentView = LayoutInflater.from(anchor.getContext()).inflate(mPopupWindowResouceId, null);
			contentView = contentview;
			mPopupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, 
		            LayoutParams.WRAP_CONTENT, true);
			mPopupWindow.setTouchable(true);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			installListener();
		} else {
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
				mAnchorView = null;
				return;
			}
			contentView = mPopupWindow.getContentView();
		}

		
		measure(contentView);
		int contentViewHeight = contentView.getMeasuredHeight();
		int contentViewWidth = contentView.getMeasuredWidth();
		
		int xOffset = 0;
		int yOffset = 0;
		
		if ((mXDisplayStyle & DISPLAY_X_LEFT_OF_ANCHOR) == DISPLAY_X_LEFT_OF_ANCHOR) {
			xOffset -= contentViewWidth;
		} else if ((mXDisplayStyle & DISPLAY_X_RIGHT_OF_ANCHOR) == DISPLAY_X_RIGHT_OF_ANCHOR) {
			xOffset = 0;
		} else if ((mXDisplayStyle & DISPLAY_X_MIDDLE_OF_ANCHOR) == DISPLAY_X_MIDDLE_OF_ANCHOR) {
			xOffset = (int)(anchor.getMeasuredWidth() / 2);
		} else if ((mXDisplayStyle & DISPLAY_X_EXACT_MIDDLE_OF_ANCHOR) == DISPLAY_X_EXACT_MIDDLE_OF_ANCHOR) {
			xOffset = (int)((anchor.getMeasuredWidth() - contentViewWidth) / 2);
		} 

		if ((mYDisplayStyle & DISPLAY_Y_ABOVE_OF_ANCHOR) == DISPLAY_Y_ABOVE_OF_ANCHOR) {
			yOffset -= (contentViewHeight + anchor.getMeasuredHeight());
		} else if ((mYDisplayStyle & DISPLAY_Y_BELOW_OF_ANCHOR) == DISPLAY_Y_BELOW_OF_ANCHOR) {
			yOffset = 0;
		} else if ((mYDisplayStyle & DISPLAY_Y_MIDDLE_OF_ANCHOR) == DISPLAY_Y_MIDDLE_OF_ANCHOR) {
			yOffset = -1 * (int)(anchor.getMeasuredHeight() / 2);
		} else if ((mYDisplayStyle & DISPLAY_Y_EXACT_MIDDLE_OF_ANCHOR) == DISPLAY_Y_EXACT_MIDDLE_OF_ANCHOR) {
			yOffset = -1 * (int)((anchor.getMeasuredHeight() - contentViewHeight) / 2);
		}
		
		mPopupWindow.showAsDropDown(anchor, xOffset, yOffset);
		mPopupWindow.setAnimationStyle(mAnimationStyle);
	}

}
