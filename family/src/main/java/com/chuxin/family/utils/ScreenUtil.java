package com.chuxin.family.utils;

import com.chuxin.family.app.CxApplication;

import android.content.Context;

public class ScreenUtil {
	
	// dp转换成pix
	public static int dip2px(Context context, float dipValue){   
        final float scale = CxApplication.getInstance().getResources().getDisplayMetrics().density;   
        return (int)(dipValue * scale + 0.5f);   
	} 
	
	public static int px2dip(Context context, float dipValue){   
        final float scale = CxApplication.getInstance().getResources().getDisplayMetrics().density;   
        return (int)(dipValue / scale + 0.5f);   
	} 
	
	
	public static int getScreenType(Context context){   
        float height = CxApplication.getInstance().getResources().getDisplayMetrics().heightPixels;   
        if(height>=800){
        	return 1;
        }else{
        	return 0;
        }
       
	} 
	
	
}
