package com.chuxin.family.widgets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;

import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxResourceManager;
import com.chuxin.family.widgets.zoomview.CxZoomView;
/**
 * 带下载功能的imageview，支持res下的drawable图片资源、SD卡的图片、远程图片
 * @author shichao.wang
 * 备注：使用前要用setImageResource或在xml里面有src属性设置默认图片
 */
public class CxZoomImage extends CxZoomView {
	
	private Uri mImageUrl;
	private boolean mIsBackground; //true表示设置为背景，false视为是src
	private CxResourceManager resourceManager;
	private LoadComplete loadCallBack;
	
	private BitmapDrawable bitmapDrawable=null;
	private String oldUrl;

	public CxZoomImage(Context context){
		super(context);
	}
	
	// 为圆角图片类而加  add by dujianyin 2013.9.6
	 public CxZoomImage(Context context, AttributeSet attrs, int defStyle) { 
	        super(context, attrs, defStyle); 
	  } 
	 
	public CxZoomImage(Context context, int imageResId) {
		super(context);
		setImageResource(imageResId);
	}
	
	public CxZoomImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
//	public void setChatbgImage(String imageUrlUpper, boolean isBackground, 
//			int width, final Object requester, 
//    		String category, final Context ctx) {
//		if ( (null == imageUrlUpper) || (imageUrlUpper.equals("null")) || ctx==null) { //避免服务器返回"null"
//			this.setImageDrawable(getResources().getDrawable(R.drawable.cx_fa_role_chatbg_default));
//			return;
//		}	
//		//专门为聊天背景处理
//
//		if(imageUrlUpper.contains("@@")){
//			
//			if(imageUrlUpper.startsWith("@@chatbg")){
//				imageUrlUpper="@@cx_fa_role_chatbg_default";
//			}
//		
//			String imageUrl=imageUrlUpper.toLowerCase();
//			String tempBbStr = "drawable"+File.separator+(imageUrl.replace("@@", ""));
//			int resId = getResources().getIdentifier(tempBbStr, null, getContext().getPackageName());
//			if (0 != resId) {
//				setImageResource(resId);
//			}else{
//				if("ChatFragment".equals(requester.getClass().getName())){
//					if(oldUrl!=null && oldUrl.equals(imageUrlUpper)){
//						if(bitmapDrawable!=null){
//							setImageDrawable(bitmapDrawable); 
//							return;
//						}
//					}	
//					oldUrl=imageUrlUpper; 
//				}
//							
//				File file = getFile(ctx,imageUrl.replace("@@", ""));
//				if (null == file) {
//					return;
//				}
//
//				if(file.exists()){
//					try {
//						BitmapDrawable drawable = (BitmapDrawable) BitmapDrawable.createFromPath(file.getAbsolutePath());
//						if("ChatFragment".equals(requester.getClass().getName())){
//							bitmapDrawable=drawable;
//						}
//						if(drawable!=null)
//							setImageDrawable(drawable); 
////						bitmap.recycle();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}else{
//					ChatBgData configData = RkGlobalParams.getInstance().getChatbgData();
//					if(configData==null){
//						this.setImageDrawable(getResources().getDrawable(R.drawable.cx_fa_role_chatbg_default));
//					}else{
//						int type = ScreenUtil.getScreenType(ctx);
//						String url="";
//						if(1==type){
//							url=configData.getResourceUrl()+File.separator+"a_"+imageUrlUpper+"_xhd.jpg";
//						}else{
//							url=configData.getResourceUrl()+File.separator+"a_"+imageUrlUpper+"_md.jpg";
//						}
////						this.displayImage(ImageLoader.getInstance(), url, R.drawable.cx_fa_role_chatbg_default, false, 0);
//					}
//				}
//			}
//			
//			return;
//		}
//		
//		
//		
////		displayImage(ImageLoader.getInstance(), imageUrlUpper, R.drawable.cx_fa_role_chatbg_default, false, 0);
//		
//	}
//	
//	
//	private File getFile(Context context,String name){
//		if (null == context) {
//			return null;
//		}
//		String folderName = "chuxin"+File.separator+"chatbg"+File.separator+"chatbgimage";
//        String fileName = null;
//        if(name.contains("thumbnail")){
//        	fileName=name+".png";
//        }else{
//        	fileName=name+".jpg";
//        }
//        File path =context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//        File file = new File(path, folderName + File.separator + fileName);
//        return file;
//	}

	public void setImage(String imageUrl, boolean isBackground, 
			int width, Object requester, 
    		String category, Context ctx) {
		if ( (null == imageUrl) || (imageUrl.equals("null")) ) { //避免服务器返回"null"
//			RkLog.i("setImage", " param imageUrl is null");
			notifyCallbacker();
			return;
		}
//		RkLog.i("setImage", " imageUrl is "+imageUrl);
		mIsBackground = isBackground;
		
		//专门为聊天背景处理
		String tempBbStr = "drawable"+File.separator+(imageUrl.replace("@@", ""));
		int resId = getResources().getIdentifier(tempBbStr, null, getContext().getPackageName());
		if (0 != resId) {
			if (isBackground) {
				setBackgroundResource(resId);
			}else{
				setImageResource(resId);
			}
			return;
		}
		
		mImageUrl = Uri.parse(imageUrl);
		updateView(width, width, requester, category, ctx);
		
	}
	
	   public void setImage(Bitmap bitmap) {
	        if ( (null == bitmap) ) { //避免服务器返回"null"
	            return;
	        }
	        setImageBitmap(bitmap);
	    }
	
	private void updateView(int height, int width, Object requester, 
    		String category, Context ctx) {
		resourceManager = CxResourceManager.getInstance(requester, category, ctx);
		if (null == resourceManager) {
			return;
		}
		if (resourceManager.existAtMemory(mImageUrl)) {
			BitmapDrawable bd = resourceManager.getMemoryCacheImage(mImageUrl);
			if (null != bd) {
				if (mIsBackground) {
					setBackgroundDrawable(bd);					
					notifyCallbacker();
					return;
				}else{
					setImageDrawable(bd);
					notifyCallbacker();
					return;
				}
			}
			
		}
		
		CxLog.i("cache", "memory does not exist,ready obtain from local");
		boolean addResult =  false;
		if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState())) {
			
			addResult = drawImage();

		}
		if (addResult) { //图片成功加载
			return;
		}
		
		//----------
		CxLog.i("updateView", "ready to net download");
		resourceManager.addObserver(new CxResourceManager.ResourceRequestObserver(mImageUrl) {
			@Override
			public void requestReceived(Observable observable, Uri uri) {
				observable.deleteObserver(this);
				updateBackground();
			}
		});
		resourceManager.request(mImageUrl, height, width, CxZoomImage.this);
		
	}
	
	private void updateBackground(){
		//网络结束都应该要callback
		notifyCallbacker();
		
		Handler updateHandler = new Handler(Looper.getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
//				RkLog.i("updateHandler", " ready to drawImage() method");
				drawImage();
				super.handleMessage(msg);
			}
		};
		updateHandler.sendEmptyMessage(1);
	}
	
	private boolean drawImage() {
//		RkLog.i("-----------------", mImageUrl.toString());
//		RkResourceManager resourceManager = RkResourceManager.getImageResourceManager();
		if (resourceManager.existAtMemory(mImageUrl)) {
			BitmapDrawable bd = resourceManager.getMemoryCacheImage(mImageUrl);
			if (null != bd) {
				if (mIsBackground) {
					setBackgroundDrawable(bd);
//					notifyCallbacker();
					return true;
				}else{
					
					setImageDrawable(bd);
					
//					notifyCallbacker();
					return true;
				}
			}
			
		}
		if (!Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState())) {
			return false;
		}
//		RkLog.i("+++++++++:", mImageUrl.toString());
		Bitmap bitmap = null;
		
//		resourceManager.getCacheImage(mImageUrl);
		
		BitmapDrawable bitd = resourceManager.getExtralBitmapDrawable(mImageUrl.toString(),this);
		if (null != bitd) {
			if (mIsBackground) {
				setBackgroundDrawable(bitd);
//				notifyCallbacker();
				return true;
			}else{
				setImageDrawable(bitd);
//				notifyCallbacker();
				return true;
			}
		}
		if (!resourceManager.exists(mImageUrl)) {
			//--------------
			String str = mImageUrl.toString();
			if(str.contains("http://")){
				return false;
			}
//			RkLog.i("#######", mImageUrl.toString());
			try {
				File tempFile = new File(mImageUrl.toString());
				
				
//				RkLog.i("**********", mImageUrl.toString());
				if (tempFile.exists()) {
//					InputStream is = new FileInputStream(
//							new File(mImageUrl.toString()));
					InputStream is = new FileInputStream(tempFile);
					if (null != is) {
						if (mIsBackground) {
							BitmapDrawable bd = new BitmapDrawable(getResources(), is);
							if (null != bd) {
								setBackgroundDrawable(bd);
//								notifyCallbacker();
//								setBackground(bd);
								return true;
							}
						}else{
							bitmap = BitmapFactory.decodeStream(is);
							is.close();
							if (null != bitmap) {
								setImageBitmap(bitmap);
//								notifyCallbacker();
								return true;
							}
							
						}
						
//						RkLog.i("drawImage", " exist local, but image null");
					}
				}else{
//					RkLog.i("unchuxin", "unchuxin file not exist ---->"+mImageUrl.toString());
				}
			} catch (IOException e) {
				CxLog.i("error", e.toString());
//				e.printStackTrace();
			}
			
//			RkLog.e("RkImageView drawImage method ", "is not existed");
		}
		
		return false;
	}
	
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
	
	public String getImageLocalPath(){
		if (null == mImageUrl) {
			return null;
		}
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return null;
		}
		if (resourceManager.exists(mImageUrl)) {
		    CxLog.d("RkImageView", "mImageUrl>>>" + mImageUrl);
			return resourceManager.getStringFile(mImageUrl).getAbsolutePath();
		}else{
			return null;
		}
	}
	
	private void notifyCallbacker(){
		if (null == loadCallBack) {
			return;
		}
		loadCallBack.completeImgLoad();
		
	}
	
	public void setCompleteCallback(LoadComplete completer){
		this.loadCallBack = completer;
	}
	
	public interface LoadComplete{
		public abstract void completeImgLoad();
	}
	
}
