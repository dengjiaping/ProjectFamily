package com.chuxin.family.widgets;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
/**
 * 开关按钮
 * @author shichao.wang
 *
 */
public class CxSwitchButton extends View implements OnTouchListener {

	//开关开启时的背景，关闭时的背景，滑动按钮
	private Bitmap mSwitchOnBackground, mSwitchOffBackground, mSliper;
	private Rect mOnRect, mOffRect;
	
	//是否正在滑动
	private boolean mIsSlipping = false;
	//当前开关状态，true为开启，false为关闭
	private boolean mIsSwitchOn = true;
	
	//手指按下时的水平坐标X，当前的水平坐标X
	private float mPreviousX, mCurrentX;
	
	//开关监听器
	private OnSwitchListener mOnSwitchListener;
	//是否设置了开关监听器
	private boolean mIsSwitchListenerOn = false;
	
	public CxSwitchButton(Context context) {
		super(context);
		init();
	}
	
	
	public CxSwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		setOnTouchListener(this);
	}
	
	public void setImageResource(int switchOnImage, int switchOffImage, int slipBtn) {
		mSwitchOnBackground = BitmapFactory.decodeResource(getResources(), switchOnImage);
		mSwitchOffBackground = BitmapFactory.decodeResource(getResources(), switchOffImage);
		mSliper = BitmapFactory.decodeResource(getResources(), slipBtn);	
		
		//开关开启的Rect
		mOnRect = new Rect(mSwitchOffBackground.getWidth() - mSliper.getWidth(), 0, 
				mSwitchOffBackground.getWidth(), mSliper.getHeight());
		//开关关闭的Rect
		mOffRect = new Rect(0, 0, mSliper.getWidth(), mSliper.getHeight());
	}
	
	//最初设置的"开"与"关"状态
	public void setSwitchState(boolean switchState) {
		mIsSwitchOn = switchState;
	}
	
	//获取开关状态
	public boolean getSwitchState() {
		return mIsSwitchOn;
	}
	
	//当按钮已经显示后,外部需要根据某些情况改变按钮的开关状态,慎用.(有别于public void setSwitchState(boolean switchState)方法)
	public void updateSwitchState(boolean switchState) {
		mIsSwitchOn = switchState;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		//滑动按钮的左边坐标
		float left_SlipBtn;
		
		//手指滑动到左半边的时候表示开关为关闭状态，滑动到右半边的时候表示开关为开启状态
		if(mCurrentX < (mSwitchOnBackground.getWidth() / 2)) {
			canvas.drawBitmap(mSwitchOffBackground, matrix, paint);
		} else {
			canvas.drawBitmap(mSwitchOnBackground, matrix, paint);
		}
		
		//判断当前是否正在滑动
		if(mIsSlipping) {
			if(mCurrentX > mSwitchOnBackground.getWidth()) {
				left_SlipBtn = mSwitchOnBackground.getWidth() - mSliper.getWidth();
			} else {
				left_SlipBtn = mCurrentX - mSliper.getWidth() / 2;
			}
		} else {
			//根据当前的开关状态设置滑动按钮的位置
			if(mIsSwitchOn) {
				left_SlipBtn = mOnRect.left;
			} else {
				left_SlipBtn = mOffRect.left;
			}
		}
		
		//对滑动按钮的位置进行异常判断
		if(left_SlipBtn < 0) {
			left_SlipBtn = 0;
		} else if(left_SlipBtn > mSwitchOnBackground.getWidth() - mSliper.getWidth()) {
			left_SlipBtn = mSwitchOnBackground.getWidth() - mSliper.getWidth();
		}
		
		canvas.drawBitmap(mSliper, left_SlipBtn, 0, paint);
	}
	
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (null == mSwitchOnBackground) {
			return;
		}
		setMeasuredDimension(mSwitchOnBackground.getWidth(), mSwitchOnBackground.getHeight());
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()) {
		//滑动
		case MotionEvent.ACTION_MOVE:
			mCurrentX = event.getX();
			break;
			
		//按下
		case MotionEvent.ACTION_DOWN:
			if(event.getX() > mSwitchOnBackground.getWidth() 
					|| event.getY() > mSwitchOnBackground.getHeight()) {
				return false;
			}
			
			mIsSlipping = true;
			mPreviousX = event.getX();
			mCurrentX = mPreviousX;
			break;
		
		//松开
		case MotionEvent.ACTION_UP:
			mIsSlipping = false;
			//松开前开关的状态
			boolean previousSwitchState  = mIsSwitchOn;
			
			if(event.getX() >= (mSwitchOnBackground.getWidth() / 2)) {
				mIsSwitchOn = true;
			} else {
				mIsSwitchOn = false;
			}
			
			//如果设置了监听器，则调用此方法
			if(mIsSwitchListenerOn && (previousSwitchState != mIsSwitchOn)) {
				mOnSwitchListener.onSwitched(mIsSwitchOn);
			}
			break;
		
		default:
			break;
		}
		
		//重新绘制控件
		invalidate();
		return true;
	}

	public void setOnSwitchListener(OnSwitchListener listener) {
		if (null == listener) {
			return;
		}
		mOnSwitchListener = listener;
		mIsSwitchListenerOn = true;
	}
	
	//开关按钮的状态变化调用的接口
	public interface OnSwitchListener {
		abstract void onSwitched(boolean isSwitchOn);
	}
}
