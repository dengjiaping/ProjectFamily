package com.chuxin.family.utils;

import java.io.File;

import com.chuxin.family.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {
	
	
	/**
	 * 获取自定义toast  该toast比较简单如果需要定制性比较高的可在下面再写
	 * @param context 上下文
	 * @param resId  资源id（如是sd卡的资源，可在下面再下一个方法）如-1则不显示图片 
	 * 			-2显示默认表示成功的图片 
	 * 			-3显示表示失败的图片
	 * @param text  显示的文字 
	 * @param duration 0 短的Toast.LENGTH_SHORT，1 长的
	 * @return 
	 */
	public static Toast getSimpleToast(Context context, int resId,String text,int duration) {
		Toast toast = new Toast(context);
		View view = View.inflate(context, R.layout.cx_fa_widget_chatbg_toast, null);
		TextView tv = (TextView) view.findViewById(R.id.cx_fa_widget_chatbg_toast_tv);
		ImageView iv = (ImageView) view.findViewById(R.id.cx_fa_widget_chatbg_toast_iv);
		
	
//			String tempBbStr = "drawable"+File.separator+imageName;
//			int resId = context.getResources().getIdentifier(tempBbStr, null, context.getPackageName());
		if(resId>0){
			iv.setImageResource(resId);
		}else if(resId==-1){
			iv.setVisibility(View.GONE);
		}else if(resId==-2){
			iv.setImageResource(R.drawable.chatbg_update_success);
		}else if(resId==-3){
			iv.setImageResource(R.drawable.chatbg_update_error);
		}

		if(!TextUtils.isEmpty(text)){
			tv.setText(text);
		}else{
			tv.setVisibility(View.GONE);
		}

		//如需自定义时长可参考http://www.apkbus.com/forum.php?mod=viewthread&tid=48443
		if(duration==0)
			toast.setDuration(Toast.LENGTH_SHORT);
		else
			toast.setDuration(Toast.LENGTH_LONG);
		
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(view);
		return toast;
	} 
	
	
}
