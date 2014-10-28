package com.chuxin.family.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.family.utils.CxLog;
import com.chuxin.family.R;
/**
 * 
 * @author shichao.wang 自定义封装上下滑动刷新list控件
 *
 */
public class ScrollableListView extends ListView
	implements OnScrollListener {

    private static final String TAG = "ScrollableListView";  
    
    private final static int RELEASE_TO_REFRESH = 0;  
    private final static int PULL_TO_REFRESH	= 1;  
    private final static int REFRESHING			= 2;  
    private final static int DONE				= 3;  
    private final static int LOADING			= 4;  
  
    // 实际的padding的距离与界面上偏移距离的比例  
    private final static int RATIO = 3;  
  
    private LayoutInflater inflater;  
  
    private LinearLayout mHeaderView;
    private LinearLayout mFooterView;
  
    private TextView mHeaderTipsTextview;
    private TextView mHeaderLastUpdatedTextView;
    private ImageView mHeaderArrowImageView;  
    private ProgressBar mHeaderProgressBar;  
  
    private TextView mFooterTipsTextview;  
    private TextView mFooterLastUpdatedTextView;  
    private ImageView mFooterArrowImageView;  
    private ProgressBar mFooterProgressBar;  
  
    private RotateAnimation animation;  
    private RotateAnimation reverseAnimation;  
  
    // 用于保证mStartY的值在一个完整的touch事件中只被记录一次  
    private boolean mIsRecordingHeaderEvents = false;
    private boolean mIsRecordingFooterEvents = false;
  
    protected int mHeaderContentWidth;  
    protected int mHeaderContentHeight;  
    protected int mFooterContentWidth;  
    protected int mFooterContentHeight;  
  
    private int mStartY;  
    private int mFirstVisibleItemIndex;  
    private int mLastVisibleItemIndex;
  
    private int state;  
  
    private boolean isBack;  
    private boolean mLoadingMoreMode = false;
  
    private OnRefreshListener mHeaderRefreshListener;  
    private OnRefreshListener mFooterRefreshListener;  
    private OnRefreshListener mRefreshListener;  
  
    private boolean mIsHeaderRefreshable;
    private boolean mIsFooterRefreshable;
    
    private boolean mIsInFooterRefresh = false;
    private boolean mIsInHeaderRefresh = false;
    private boolean mIsRefresh = false;
    private Context mContext;
  
    public ScrollableListView(Context context) {  
        super(context);  
        init(context);
        mContext = context;
    }  
  
    public ScrollableListView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init(context);
        mContext = context;
    }  
  
    private void init(Context context) {
        //setCacheColorHint(context.getResources().getColor(R.color.transparent));  
    	
    	addScrollableHeaderView(context);
    	addScrollableFooterView(context);
        setOnScrollListener(this);  
  
        animation = new RotateAnimation(0, -180,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);  
        animation.setInterpolator(new LinearInterpolator());  
        animation.setDuration(250);  
        animation.setFillAfter(true);  
  
        reverseAnimation = new RotateAnimation(-180, 0,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);  
        reverseAnimation.setInterpolator(new LinearInterpolator());  
        reverseAnimation.setDuration(250);  
        reverseAnimation.setFillAfter(true);  
  
        state = DONE;  
        mIsHeaderRefreshable = false;
        mIsFooterRefreshable = false;
    }
    
    protected void addScrollableHeaderView(Context context) {
        inflater = LayoutInflater.from(context);  
        
        mHeaderView = (LinearLayout) inflater.inflate(R.layout.cx_fa_widget_scrollable_list_view_header, null);  
  
        mHeaderArrowImageView = (ImageView) mHeaderView  
                .findViewById(R.id.cx_fa_widget_scrollable_list_view_header__arrow_image);  
        mHeaderArrowImageView.setMinimumWidth(70);  
        mHeaderArrowImageView.setMinimumHeight(50);  
        mHeaderProgressBar = (ProgressBar) mHeaderView  
                .findViewById(R.id.cx_fa_widget_scrollable_list_view_header__progressbar);  
        mHeaderTipsTextview = (TextView) mHeaderView.findViewById(R.id.cx_fa_widget_scrollable_list_view_header__tips);  
        mHeaderLastUpdatedTextView = (TextView) mHeaderView  
                .findViewById(R.id.cx_fa_widget_scrollable_list_view_header__last_updated);  
  
        measureView(mHeaderView);  
        mHeaderContentHeight = mHeaderView.getMeasuredHeight();  
        mHeaderContentWidth = mHeaderView.getMeasuredWidth();  
  
        mHeaderView.setPadding(0, -1 * mHeaderContentHeight, 0, 0);  
        mHeaderView.invalidate();  
  
//        RkLog.d(TAG, "size width:" + mHeaderContentWidth + " height:"  
//                + mHeaderContentHeight);  
  
        addHeaderView(mHeaderView, null, false);    	
    }
    
    protected void addScrollableFooterView(Context context) {
        inflater = LayoutInflater.from(context);  
        
        mFooterView = (LinearLayout) inflater.inflate(R.layout.cx_fa_widget_scrollable_list_view_footer, null);  
  
        mFooterArrowImageView = (ImageView) mFooterView  
                .findViewById(R.id.cx_fa_widget_scrollable_list_view_footer__arrow_image);  
        mFooterArrowImageView.setMinimumWidth(70);  
        mFooterArrowImageView.setMinimumHeight(50);  
        mFooterProgressBar = (ProgressBar) mFooterView  
                .findViewById(R.id.cx_fa_widget_scrollable_list_view_footer__progressbar);  
        mFooterTipsTextview = (TextView) mFooterView
        		.findViewById(R.id.cx_fa_widget_scrollable_list_view_footer__tips);  
        mFooterLastUpdatedTextView = (TextView) mFooterView  
                .findViewById(R.id.cx_fa_widget_scrollable_list_view_footer__last_updated);  
  
        measureView(mFooterView);  
        mFooterContentHeight = mFooterView.getMeasuredHeight();  
        mFooterContentWidth = mFooterView.getMeasuredWidth();  
  
        mFooterView.setPadding(0, 0, 0, 0);
        mFooterView.invalidate();  
  
//        RkLog.d("size", "width:" + mFooterContentWidth + " height:"  
//                + mFooterContentHeight);  

        if (!mIsFooterRefreshable)
        	mFooterView.setVisibility(GONE);
        else
        	mFooterView.setVisibility(VISIBLE);
        addFooterView(mFooterView, null, false);
    }
  
    public void onScroll(AbsListView arg0, int firstVisiableItem, int visiableItemCount,  
            int totalItemCount) {
        mFirstVisibleItemIndex = firstVisiableItem;
        mLastVisibleItemIndex = firstVisiableItem + visiableItemCount;
        mLoadingMoreMode = (mLastVisibleItemIndex  == totalItemCount);
//        RkLog.d(TAG, "firstVisibleItem=" + firstVisiableItem + ", visiableItemCount=" + visiableItemCount
//        		+ ",totalItemCount=" + totalItemCount);
    }
    private int oldCount=0;
    public void onScrollStateChanged(AbsListView arg0, int scrollState) {  
    	 int count = getAdapter().getCount();
  
//    	 if(count!=oldCount){
    	
           if(mLastVisibleItemIndex >= (count-6) && mIsRefresh){
               // 当滑动到底部数据还有6条时提前预加载剩余数据
        	   oldCount=count;
               startRefresh(); 
           } 
           
           if(mLastVisibleItemIndex >= count && mIsRefresh) {
        	   showLoadingMoreToast();
           }
           
//    	 } 
    }  

    private void onDragableHeaderTouchEvent(MotionEvent event) {
        switch (event.getAction()) { 
        case MotionEvent.ACTION_DOWN:  
            if (mFirstVisibleItemIndex == 0 && !mIsRecordingHeaderEvents) {  
                mIsRecordingHeaderEvents = true;  
                mStartY = (int) event.getY();  
                CxLog.v(TAG, "在down时候记录当前位置‘");  
            }  
            break;  

        case MotionEvent.ACTION_UP:  

            if (state != REFRESHING && state != LOADING) {  
                if (state == DONE) {  
                    // 什么都不做  
                }  
                if (state == PULL_TO_REFRESH) {  
                    state = DONE;  
                    changeHeaderViewByState();  

                    CxLog.v(TAG, "由下拉刷新状态，到done状态");  
                }  
                if (state == RELEASE_TO_REFRESH) {  
                    state = REFRESHING;  
                    changeHeaderViewByState();  
                    onRefresh();  

                    CxLog.v(TAG, "由松开刷新状态，到done状态");  
                }  
            }  

            mIsRecordingHeaderEvents = false;  
            isBack = false;  

            break;  

        case MotionEvent.ACTION_MOVE:  
            int tempY = (int) event.getY();  

            if (!mIsRecordingHeaderEvents && mFirstVisibleItemIndex == 0) {  
                CxLog.v(TAG, "在move时候记录下位置");  
                mIsRecordingHeaderEvents = true;  
                mStartY = tempY;  
            }  
            //RkLog.v(TAG, "state=" + state);
            if (state != REFRESHING && mIsRecordingHeaderEvents && state != LOADING) {  

                // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动  

                // 可以松手去刷新了  
                if (state == RELEASE_TO_REFRESH) {  

                    setSelection(0);  

                    // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步  
                    if (((tempY - mStartY) / RATIO < mHeaderContentHeight)  
                            && (tempY - mStartY) > 0) {  
                        state = PULL_TO_REFRESH;  
                        changeHeaderViewByState();  

                        CxLog.v(TAG, "由松开刷新状态转变到下拉刷新状态");  
                    }  
                    // 一下子推到顶了  
                    else if (tempY - mStartY <= 0) {  
                        state = DONE;  
                        changeHeaderViewByState();  

                        CxLog.v(TAG, "由松开刷新状态转变到done状态");  
                    }  
                    // 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步  
                    else {  
                        // 不用进行特别的操作，只用更新paddingTop的值就行了  
                    }  
                }  
                // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态  
                if (state == PULL_TO_REFRESH) {  

                    setSelection(0);  

                    // 下拉到可以进入RELEASE_TO_REFRESH的状态  
                    if ((tempY - mStartY) / RATIO >= mHeaderContentHeight) {  
                        state = RELEASE_TO_REFRESH;  
                        isBack = true;  
                        changeHeaderViewByState();  

                        CxLog.v(TAG, "由done或者下拉刷新状态转变到松开刷新");  
                    }  
                    // 上推到顶了  
                    else if (tempY - mStartY <= 0) {  
                        state = DONE;  
                        changeHeaderViewByState();  

                        CxLog.v(TAG, "由DOne或者下拉刷新状态转变到done状态");  
                    }  
                }  

                // done状态下  
                if (state == DONE) {  
                    if (tempY - mStartY > 0) {  
                        state = PULL_TO_REFRESH;  
                        changeHeaderViewByState();  
                    }  
                }  

                // 更新mHeaderView的size  
                if (state == PULL_TO_REFRESH) {  
                    mHeaderView.setPadding(0, -1 * mHeaderContentHeight  
                            + (tempY - mStartY) / RATIO, 0, 0);  

                }  

                // 更新mHeaderView的paddingTop  
                if (state == RELEASE_TO_REFRESH) {  
                    mHeaderView.setPadding(0, (tempY - mStartY) / RATIO  
                            - mHeaderContentHeight, 0, 0);  
                }  

            }  

            break;  
        }  
    }
    
    private void onDragableFooterTouchEvent(MotionEvent event) {
        switch (event.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
            if (mLoadingMoreMode && !mIsRecordingFooterEvents) {  
            	mIsRecordingFooterEvents = true;  
                mStartY = (int) event.getY();  
                CxLog.v(TAG, "在down时候记录当前位置‘");  
            }  
            break;  

        case MotionEvent.ACTION_UP:
            if (state != REFRESHING && state != LOADING) {
            	switch(state) {
            	case DONE:
            		break;

            	case PULL_TO_REFRESH:  
                    state = DONE;  
                    changeFooterViewByState();  

                    CxLog.v(TAG, "由下拉刷新状态，到done状态");  
                	break;

            	case RELEASE_TO_REFRESH:  
                    state = REFRESHING;  
                    changeFooterViewByState();  
                    onLoadMore();  

                    CxLog.v(TAG, "由松开刷新状态，到done状态");  
                    break;
                }  
            }  

            mIsRecordingFooterEvents = false;  
            isBack = false;  

            break;  

        case MotionEvent.ACTION_MOVE:  
            int tempY = (int) event.getY();  

            if (!mIsRecordingFooterEvents && mLoadingMoreMode) {  
                Log.v(TAG, "在move时候记录下位置");  
                mIsRecordingFooterEvents = true;  
                mStartY = tempY;  
            }

            if (state != REFRESHING && mIsRecordingFooterEvents && state != LOADING) {  

                // 保证在设置padding的过程中，当前的位置一直是在footer，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动  

                // 可以松手去刷新了  
                if (state == RELEASE_TO_REFRESH) {  

                    setSelection(getAdapter().getCount());  

                    // 往上推即可触发刷新
                    if (((mStartY - tempY) / RATIO < mFooterContentHeight)  
                            && (mStartY - tempY) > 0) {
                        state = PULL_TO_REFRESH;
                        changeFooterViewByState();

                        CxLog.v(TAG, "由松开刷新状态转变到下拉刷新状态");  
                    }  
                    // 又推回去了  
                    else if (mStartY - tempY <= 0) {  
                        state = DONE;  
                        changeFooterViewByState();  

                        CxLog.v(TAG, "由松开刷新状态转变到done状态");  
                    }  
                    else {  
                        // 不用进行特别的操作，只用更新paddingTop的值就行了  
                    }  
                }  
                // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态  
                if (state == PULL_TO_REFRESH) {  
                    setSelection(getAdapter().getCount());  

                    // 下拉到可以进入RELEASE_TO_REFRESH的状态  
                    if ((mStartY - tempY) / RATIO >= mFooterContentHeight) {
                        state = RELEASE_TO_REFRESH;  
                        isBack = true;  
                        changeFooterViewByState();  

                        CxLog.v(TAG, "由done或者下拉刷新状态转变到松开刷新");  
                    }  
                    // 上推到顶了  
                    else if ((mStartY - tempY) <= 0) {  
                        state = DONE;  
                        changeFooterViewByState();  

                        CxLog.v(TAG, "由DOne或者下拉刷新状态转变到done状态");  
                    }  
                }  

                // done状态下  
                if (state == DONE) {  
                    if (mStartY - tempY > 0) {  
                        state = PULL_TO_REFRESH;  
                        changeFooterViewByState();  
                    }  
                }  

                // 更新mFooterView的size  
                if (state == PULL_TO_REFRESH) {  
                    mFooterView.setPadding(0, 0, 0, (mStartY - tempY) / RATIO);  
                }  

                // 更新mHeaderView的paddingTop  
                if (state == RELEASE_TO_REFRESH) {  
                    mFooterView.setPadding(0, 0, 0, (mStartY - tempY) / RATIO);  
                }  

            }  

            break;  
        }  
    }

    public boolean onTouchEvent(MotionEvent event) {  
 
    	boolean processed = false;
    	if (!processed && mIsRecordingHeaderEvents) {
        	onDragableHeaderTouchEvent(event);
        	processed = true;
    	}
    	if (!processed && mIsRecordingFooterEvents) {
        	onDragableFooterTouchEvent(event);
        	processed = true;
    	}

        if (!processed && mIsHeaderRefreshable) {
        	onDragableHeaderTouchEvent(event);
        	processed = mIsRecordingHeaderEvents; 
        }
  
        if (!processed && mIsFooterRefreshable) {
        	onDragableFooterTouchEvent(event);
        	processed = mIsRecordingFooterEvents; 
        }

        return super.onTouchEvent(event);  
    }  
  
    // 当状态改变时候，调用该方法，以更新界面  
    protected void changeHeaderViewByState() {  
        switch (state) {  
        case RELEASE_TO_REFRESH:  
            mHeaderArrowImageView.setVisibility(View.VISIBLE);  
            mHeaderProgressBar.setVisibility(View.GONE);  
            mHeaderTipsTextview.setVisibility(View.VISIBLE);  
            mHeaderLastUpdatedTextView.setVisibility(View.VISIBLE);  
  
            mHeaderArrowImageView.clearAnimation();  
            mHeaderArrowImageView.startAnimation(animation);  
  
            mHeaderTipsTextview.setText(R.string.cx_fa_nls_list_release_to_refresh);
  
            CxLog.v(TAG, "当前状态，松开刷新");  
            break;  
        case PULL_TO_REFRESH:  
            mHeaderProgressBar.setVisibility(View.GONE);  
            mHeaderTipsTextview.setVisibility(View.VISIBLE);  
            mHeaderLastUpdatedTextView.setVisibility(View.VISIBLE);  
            mHeaderArrowImageView.clearAnimation();  
            mHeaderArrowImageView.setVisibility(View.VISIBLE);  
            // 是由RELEASE_To_REFRESH状态转变来的  
            if (isBack) {  
                isBack = false;  
                mHeaderArrowImageView.clearAnimation();  
                mHeaderArrowImageView.startAnimation(reverseAnimation);  
            }
            
            mHeaderTipsTextview.setText(R.string.cx_fa_nls_list_pull_down_to_refresh);
            CxLog.v(TAG, "当前状态，下拉刷新");  
            break;  
  
        case REFRESHING:  
  
            mHeaderView.setPadding(0, 0, 0, 0);  
  
            mHeaderProgressBar.setVisibility(View.VISIBLE);  
            mHeaderArrowImageView.clearAnimation();  
            mHeaderArrowImageView.setVisibility(View.GONE);  
            mHeaderTipsTextview.setText(R.string.cx_fa_nls_list_refreshing);
            mHeaderLastUpdatedTextView.setVisibility(View.VISIBLE);  
  
            CxLog.v(TAG, "当前状态,正在刷新...");  
            break;  
        case DONE:  
            mHeaderView.setPadding(0, -1 * mHeaderContentHeight, 0, 0);  
  
            mHeaderProgressBar.setVisibility(View.GONE);  
            mHeaderArrowImageView.clearAnimation();  
            mHeaderArrowImageView.setImageResource(R.drawable.scroll_list_arrow_down);  
            mHeaderTipsTextview.setText(R.string.cx_fa_nls_list_pull_down_to_refresh);
            mHeaderLastUpdatedTextView.setVisibility(View.VISIBLE);  
  
            CxLog.v(TAG, "当前状态，done");  
            break;  
        }  
    }  
    
    
    // 当状态改变时候，调用该方法，以更新界面  
    protected void changeFooterViewByState() {  
        switch (state) {  
        case RELEASE_TO_REFRESH:  
            mFooterArrowImageView.setVisibility(View.VISIBLE);  
            mFooterProgressBar.setVisibility(View.GONE);  
            mFooterTipsTextview.setVisibility(View.VISIBLE);  
            mFooterLastUpdatedTextView.setVisibility(View.VISIBLE);  
  
            mFooterArrowImageView.clearAnimation();  
            mFooterArrowImageView.startAnimation(animation);  
  
            mFooterTipsTextview.setText(R.string.cx_fa_nls_list_release_to_refresh);  
  
            CxLog.v(TAG, "当前状态，松开刷新");  
            break;  
        case PULL_TO_REFRESH:  
            mFooterProgressBar.setVisibility(View.GONE);  
            mFooterTipsTextview.setVisibility(View.VISIBLE);  
            mFooterLastUpdatedTextView.setVisibility(View.VISIBLE);  
            mFooterArrowImageView.clearAnimation();  
            mFooterArrowImageView.setVisibility(View.VISIBLE);  
            // 是由RELEASE_To_REFRESH状态转变来的  
            if (isBack) {  
                isBack = false;  
                mFooterArrowImageView.clearAnimation();  
                mFooterArrowImageView.startAnimation(reverseAnimation);  
            }
            mFooterTipsTextview.setText(R.string.cx_fa_nls_list_pull_up_to_refresh);  
            CxLog.v(TAG, "当前状态，下拉刷新");  
            break;  
  
        case REFRESHING:  
  
            mFooterView.setPadding(0, 0, 0, 0);  
  
            mFooterProgressBar.setVisibility(View.VISIBLE);  
            mFooterArrowImageView.clearAnimation();  
            mFooterArrowImageView.setVisibility(View.GONE);  
            mFooterTipsTextview.setText(R.string.cx_fa_nls_list_refreshing);  
            mFooterLastUpdatedTextView.setVisibility(View.VISIBLE);  
  
            CxLog.v(TAG, "当前状态,正在刷新...");  
            break;  
        case DONE:  
            mFooterView.setPadding(0, 0, 0, 0);  
  
            mFooterProgressBar.setVisibility(View.GONE);  
            mFooterArrowImageView.clearAnimation();  
            mFooterArrowImageView.setImageResource(R.drawable.scroll_list_arrow_up);  
            mFooterTipsTextview.setText(R.string.cx_fa_nls_list_pull_up_to_refresh);  
            mFooterLastUpdatedTextView.setVisibility(View.VISIBLE);  
  
            CxLog.v(TAG, "当前状态，done");  
            break;  
        }  
    }  

  
    public void setOnHeaderRefreshListener(OnRefreshListener refreshListener) {  
        mHeaderRefreshListener = refreshListener;  
        mIsHeaderRefreshable = true;  
    }  

    public void setOnFooterRefreshListener(OnRefreshListener refreshListener) {  
        mFooterRefreshListener = refreshListener;  
        mIsFooterRefreshable = true;  
    	mFooterView.setVisibility(VISIBLE);
    }  
    
    public void setOnRefreshListener(OnRefreshListener refreshListener) {  
        mRefreshListener = refreshListener;  
        mIsFooterRefreshable = false;
        mIsInHeaderRefresh = false;
        mIsRefresh = true;
//        mFooterView.setVisibility(VISIBLE);
    }  
  
    public interface OnRefreshListener {  
        public void onRefresh();
    }  
  
    public void onRefreshComplete() {
        state = DONE; 
        CxLog.i(TAG, "on refresh complete");
    	if (mIsInFooterRefresh) {
    		changeFooterViewByState();
    	} else {
            changeHeaderViewByState();  
    	}
        mIsInFooterRefresh = mIsInHeaderRefresh = false;
//        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日  HH:mm");  
//        String date=format.format(new Date());  
//        mHeaderLastUpdatedTextView.setText("最近更新:" + date);  
    }
    
    public void onLoadMoreComplete() {  
        state = DONE;  
//        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日  HH:mm");  
//        String date=format.format(new Date());  
//        mFooterLastUpdatedTextView.setText("最近更新:" + date);  
        changeFooterViewByState();  
        mIsInFooterRefresh = mIsInHeaderRefresh = false;
    }  
  
    private void onLoadMore() {  
        if (mFooterRefreshListener != null) {  
        	mIsInFooterRefresh = true;
            mFooterRefreshListener.onRefresh();  
        }
    }  

    private void startRefresh(){
        if(null != mRefreshListener){
            mIsRefresh = false;
            mRefreshListener.onRefresh();
        }
    }
    
    private void onRefresh() {  
        if (mHeaderRefreshListener != null) {  
        	mIsInHeaderRefresh = true;
            mHeaderRefreshListener.onRefresh();  
        }  
    }  
  
    public void refreshComplete(){
        mIsRefresh = true;
    }
    
    // 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”mHeaderView的width以及height  
    private void measureView(View child) {
         ViewGroup.LayoutParams p = child.getLayoutParams();  
            if (p == null) {  
                p = new ViewGroup.LayoutParams(  
                        ViewGroup.LayoutParams.MATCH_PARENT,  
                        ViewGroup.LayoutParams.WRAP_CONTENT);  
            }  
  
            int childWidthSpec = ViewGroup.getChildMeasureSpec(0,  
                    0 + 0, p.width);  
            int lpHeight = p.height;  
            int childHeightSpec;  
            if (lpHeight > 0) {  
                childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);  
            } else {  
                childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);  
            }  
            child.measure(childWidthSpec, childHeightSpec);  
    }
  
    /*public void setAdapter(BaseAdapter adapter) {
//        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日  HH:mm");  
//        String date=format.format(new Date());  
//        mHeaderLastUpdatedTextView.setText("最近更新:" + date);  
        super.setAdapter(adapter);  
    } */ 
    private void showLoadingMoreToast() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
      View layout = inflater.inflate(R.layout.cx_fa_custom_loading_more_toast, null);
      Toast toast = new Toast(mContext);
      //设置Toast的位置
      toast.setGravity(Gravity.BOTTOM, 0, 20);
      toast.setDuration(Toast.LENGTH_SHORT);
      toast.setView(layout);
      toast.show(); 
    }
}
