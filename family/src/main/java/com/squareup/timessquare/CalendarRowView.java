// Copyright 2012 Square, Inc.
package com.squareup.timessquare;

import com.chuxin.family.R;
import com.chuxin.family.utils.Globals;
import com.chuxin.family.utils.CxLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/** TableRow that draws a divider between each cell. To be used with {@link CalendarGridView}. */
public class CalendarRowView extends ViewGroup implements View.OnClickListener {
  private boolean isHeaderRow;
  private CalendarView.CellClickListener listener;
  private int cellSizeW;
  private int cellSizeH;

  public CalendarRowView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.setBackgroundResource(R.color.calendar_bg);
  }

  @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
    child.setOnClickListener(this);
    super.addView(child, index, params);
    this.setBackgroundResource(R.color.calendar_bg);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    long start = System.currentTimeMillis();
    final int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
    cellSizeW = totalWidth / 7;
    final int totalHeight = MeasureSpec.getSize(heightMeasureSpec);
    cellSizeH = totalHeight / 7;
    int cellWidthSpec = makeMeasureSpec(cellSizeW, EXACTLY);
    int cellHeightSpec = 0;
    if(Globals.getInstance().getCalendarIsLandSpace()){
        if(getIsHeaderRow()){
            cellHeightSpec = makeMeasureSpec(cellSizeW, AT_MOST);
        } else {
            cellHeightSpec = makeMeasureSpec((int)(cellSizeW*0.7), EXACTLY);
        }
    } else {
        cellHeightSpec = makeMeasureSpec(cellSizeW, AT_MOST);
    }
    int rowHeight = 0;
    for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
      final View child = getChildAt(c);
      child.measure(cellWidthSpec, cellHeightSpec);
      // The row height is the height of the tallest cell.
      if (child.getMeasuredHeight() > rowHeight) {
        rowHeight = child.getMeasuredHeight();
      }
    }
    final int widthWithPadding = totalWidth + getPaddingLeft() + getPaddingRight();
    final int heightWithPadding = rowHeight + getPaddingTop() + getPaddingBottom();
//    final int widthWithPadding = totalWidth;
//    final int heightWithPadding = rowHeight;
    setMeasuredDimension(widthWithPadding, heightWithPadding);
    //Logr.d("Row.onMeasure %d ms", System.currentTimeMillis() - start);
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    long start = System.currentTimeMillis();
    int cellHeight = bottom - top;
    for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
      final View child = getChildAt(c);
      child.layout(c * cellSizeW, 0, (c + 1) * cellSizeW, cellHeight);
    }
    //Logr.d("Row.onLayout %d ms", System.currentTimeMillis() - start);
  }

  public void setIsHeaderRow(boolean isHeaderRow) {
    this.isHeaderRow = isHeaderRow;
  }
  public boolean getIsHeaderRow(){
      return isHeaderRow;
  }

  @Override 
  public void onClick(View v) {
    // Header rows don't have a click listener
    if (listener != null) {
      listener.handleClick(v, (MonthCellDescriptor) v.getTag());
    }
  }

  public void setCellClickListener(CalendarView.CellClickListener listener) {
    this.listener = listener;
  }
}
