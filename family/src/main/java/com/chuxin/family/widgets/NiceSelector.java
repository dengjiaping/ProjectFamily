package com.chuxin.family.widgets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chuxin.family.R;
/**
 * 
 * @author shichao.wang
 *
 */
public class NiceSelector extends ListView {
	
	private static final String TAG = "NiceSelector";
	private String[] mValues = null;
	private String[] mTexts = null;
	private boolean mMultiSelection = false;
	private boolean mMustSelectOne = true;
	protected List<Integer> mSelectedList = new ArrayList<Integer>();
	
	private int calculatePixelForDip(int dip) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
		return (int)px;
	}
	
	public String[] getSelection() {
		if (mSelectedList.size() == 0) {
			return null;
		}
		
		String[] results = new String[mSelectedList.size()];
		for(int i = 0; i < results.length; i++) {
			results[i] = mValues[mSelectedList.get(i)];
		}
		return results;
	}
	
	public void setSelection(String[] values) {
		if ((values == null) || (values.length == 0))
			return;
		
		mSelectedList.clear();
		for(int i = 0; i < values.length; i++) {
			for(int j = 0; j < mValues.length; j++) {
				if (values[i].equals(mValues[j])) {
					clickItem(Integer.valueOf(j));
				}
			}
		}
	}

    public NiceSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
                
        setFooterDividersEnabled(false);
        setBackgroundResource(R.drawable.cx_fa_bg_white_corner_rect_with_grey_border);
        setDivider(getResources().getDrawable(R.drawable.cx_fa_bg_grey_divider_line));
        setDividerHeight(calculatePixelForDip(1));
        setSelector(R.color.cx_fa_co_transparent);
        setCacheColorHint(0x0);
        
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.cx_fa_decl_niceselector_attrs);
        int textsResId = typedArray.getResourceId(R.styleable.cx_fa_decl_niceselector_attrs_texts, 0);
        Log.d(TAG, "textsResId = " + textsResId);
        if (textsResId == 0) {
        	Log.e(TAG, "Error: no valid texts resource set!");
        	assert(false);
        }
        mTexts = getResources().getStringArray(textsResId);
        
        int valuesResId = typedArray.getResourceId(R.styleable.cx_fa_decl_niceselector_attrs_values, 0);
        if (valuesResId == 0) {
        	Log.e(TAG, "Error: no valid values resource set!");
        	assert(false);
        }
        Log.d(TAG, "valuesResId = " + valuesResId);
        mValues = getResources().getStringArray(valuesResId);
        
        mMultiSelection = typedArray.getBoolean(R.styleable.cx_fa_decl_niceselector_attrs_multiSelection, false);
        mMustSelectOne = typedArray.getBoolean(R.styleable.cx_fa_decl_niceselector_attrs_mustSelectOne, true);
        typedArray.recycle();
        
        setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				return mValues.length;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup root) {
				Log.d(TAG, "redraw child view " + position);
				View view = null;
				if (convertView != null) {
					view = convertView;
				} else {
					view = LayoutInflater.from(root.getContext())
							.inflate(R.layout.cx_fa_widget_nice_selector_row, root, false);
				}
				
				ImageView image = (ImageView)view.findViewById(R.id.cx_fa_widget_niceselector_image);
				TextView text = (TextView)view.findViewById(R.id.cx_fa_widget_niceselector_text);

				Integer positionWrapper = Integer.valueOf(position);
				if (mSelectedList.contains(positionWrapper)) {
					image.setVisibility(VISIBLE);
				} else {
					image.setVisibility(GONE);
				}
				text.setText(mTexts[position]);
				
				return view;
			}
        	
        });
        
        setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				clickItem(Integer.valueOf(position));
			}
        	
        });
    }
    
    private void updateChildAt(Integer position) {
    	View view = getChildAt(position);
    	if (view == null) {
    		// this view may have not been seen.
    		return;
    	}
		ImageView image = (ImageView)view.findViewById(R.id.cx_fa_widget_niceselector_image);

		if (mSelectedList.contains(position)) {
			image.setVisibility(VISIBLE);
		} else {
			image.setVisibility(GONE);
		}
		//this.setVisibility(View.INVISIBLE);
    }
    
    void clickItem(Integer position) {
    	int deselected = -1;
    	
    	if (mSelectedList.contains(position)) {
    		// this item has already bee selected
    		boolean allowRemove = false;
    		if (mSelectedList.size() > 1) {
        		allowRemove = true;    			
    		} else if (!mMustSelectOne) {
				allowRemove = true;
    		} else {
    			// the item cannot be removed, so no change will happen.
    			return;
    		}
    		
    		if (allowRemove)
    			mSelectedList.remove(position);
    		
    	} else {
    		// this item has not been selected yet.
    		
    		if (!mMultiSelection) {
    			// check if allow to do multi-selection
    			if (mSelectedList.size() > 0) {
    				deselected = mSelectedList.get(0);
    				mSelectedList.clear();
    			}
    		}
    		mSelectedList.add(position);
    	}

    	if (deselected != -1)
        	updateChildAt(Integer.valueOf(deselected));
    	
    	updateChildAt(position);
    }

}
