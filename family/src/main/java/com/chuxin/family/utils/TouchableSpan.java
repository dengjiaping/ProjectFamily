package com.chuxin.family.utils;

import android.widget.TextView;
import android.view.MotionEvent;

/**
 * @author Oleg Green
 * @since 14.07.13
 */
public interface TouchableSpan {

    boolean onTouchEvent(MotionEvent event, TextView widget);
}
