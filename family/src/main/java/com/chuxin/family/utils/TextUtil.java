package com.chuxin.family.utils;

import com.chuxin.family.R;
import com.chuxin.family.resource.CxResourceString;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

public class TextUtil {

	/**
	 * 得到某个文本的SpannableString对象
	 * @param str				： 源文本
	 * @param fontSize	：字体大小(如 1.3f)
	 * @param color			 :  在资源中定义的颜色   -1:表示不设置
	 * @return
	 */
	public static SpannableString getSpanStr(String str, float fontSize,  int color ){
		SpannableString spanStr = new SpannableString(str);
		spanStr.setSpan(new RelativeSizeSpan(fontSize), 0, 
				str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);		// 设置字体大小
		
		if(color!=-1){
			spanStr.setSpan(new ForegroundColorSpan(color), 0, 
					str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);		// 设置颜色
		}
		return spanStr;
	}
	
	
	
	public static SpannableString getNewSpanStr(String str, int fontSize,int color ){
		SpannableString spanStr = new SpannableString(str);
		spanStr.setSpan(new AbsoluteSizeSpan(fontSize,true), 0, 
				str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);		// 设置字体大小
		
		if(color!=-1){
			spanStr.setSpan(new ForegroundColorSpan(color), 0, 
					str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);		// 设置颜色
		}
		return spanStr;
	}
	
	public static SpannableStringBuilder getImageSpanStr(String str,int resId, final int right,final int bottom,Context context ){
		
		SpannableStringBuilder builder = new SpannableStringBuilder(str);
		
        final Drawable drawable = context.getResources().getDrawable(resId);
		
        DynamicDrawableSpan span=new DynamicDrawableSpan(DynamicDrawableSpan.ALIGN_BOTTOM) {
			
			@Override
			public Drawable getDrawable() {
				drawable.setBounds(0, 0, right, bottom);
				return drawable;
			}
		};
		
		builder.setSpan(span, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return builder;
	}
	
	/**
	 * 该方法用于把数字金额变成加入千位分隔符的样式，如 123455 到 123,455
	 * @param money 
	 * @return
	 */
	public static String transforToMoney(String oldMoney){
		
		String type="";
		String money="";
		if(oldMoney.startsWith("-") || oldMoney.startsWith("+")){
			type=oldMoney.substring(0,1);
			money=oldMoney.substring(1);
		}else{
			money=oldMoney;
		}
		
		int len = money.length();
		String newMoney="";
		if(len<=3){
			newMoney=money;
		}else if(len<=6){
			newMoney=money.substring(0,len-3)+","+money.substring(len-3);
		}else if(len<=9){
			newMoney=money.substring(0,len-6)+","+money.substring(len-6,len-3)+","+money.substring(len-3);
		}else{
			newMoney="";
		}
		
		newMoney=type+newMoney;
		
		return newMoney;
	}
	
	public static int categoryToNumber(Context context,String category){
		
		int number=0;
		
		String[] strings = context.getResources().getStringArray(R.array.cx_fa_strs_accounting_category);				
		int[] array = context.getResources().getIntArray(R.array.cx_fa_ints_accounting_category);
		for(int i=0;i<array.length;i++){
			if(category.equals(strings[i])){
				number=array[i];
				break;
			}
		}
		
		return number;
	}
	
	public static String numberToCategory(Context context,int number){
		
		String categoryStr="";
		String[] strings = context.getResources().getStringArray(R.array.cx_fa_strs_accounting_category);				
		int[] array = context.getResources().getIntArray(R.array.cx_fa_ints_accounting_category);
		for(int i=0;i<array.length;i++){
			if(number==array[i]){
				categoryStr=strings[i];
				break;
			}
		}
		return categoryStr;
	}
	
	
	
	public static int fromToNumber(Context context,String from){
		
		int number=0;	
		if(from.equals(context.getString(R.string.cx_fa_accounting_account_me))){
			number=1;
		}else if(from.equals(context.getString(CxResourceString.getInstance().str_pair))){
			number=2;
		}else if(from.equals(context.getString(R.string.cx_fa_accounting_account_both))){
			number=3;
		}
		return number;
	}
	
	public static String numberToFrom(Context context,int number){
		
		String from="";	
		if(number==1){
			from=context.getString(R.string.cx_fa_accounting_account_me);
		}else if(number==2){
			from=context.getString(CxResourceString.getInstance().str_pair);
		}else if(number==3){
			from=context.getString(R.string.cx_fa_accounting_account_both);
		}
		return from;
	}
	
	
	
	public static String getLunarMonth(int i){
		switch (i) {
		case 1:
			return "正月";
		case 2:
			return "二月";
		case 3:
			return "三月";
		case 4:
			return "四月";
		case 5:
			return "五月";
		case 6:
			return "六月";
		case 7:
			return "七月";
		case 8:
			return "八月";
		case 9:
			return "九月";
		case 10:
			return "十月";
		case 11:
			return "寒月";
		case 12:
			return "腊月";

		default:
			return "";
		}
		
	}
	
	public static String getLunarDay(int i){
		if(i<=0 || i>31){
			return "";
		}
		
		if(0<i && i<11){
			return "初"+getLunarDay2(i);
		}
		
		if(i>=11 && i<20){
			return "十"+getLunarDay2(i-10);
		}
		
		if(i>=20 && i<30){
			return "二十"+getLunarDay2(i-20);
		}
		if(i>=30 && i<=31){
			return "三十"+getLunarDay2(i-30);
		}
		return "";
	}
	
	
	
	private static  String getLunarDay2(int i){
		switch (i) {
		case 0:
			return "";
		case 1:
			return "一";
		case 2:
			return "二";
		case 3:
			return "三";
		case 4:
			return "四";
		case 5:
			return "五";
		case 6:
			return "六";
		case 7:
			return "七";
		case 8:
			return "八";
		case 9:
			return "九";
		case 10:
			return "十";
			
		default:
			return "";
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
