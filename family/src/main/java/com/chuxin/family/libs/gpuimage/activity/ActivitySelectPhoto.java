
package com.chuxin.family.libs.gpuimage.activity;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.gallery.CxGalleryActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.kids.CxKidAddFeed;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.libs.gpuimage.utils.PictureUtils;
import com.chuxin.family.neighbour.CxNeighbourAddInvitation;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.zone.CxZoneAddFeed;
import com.chuxin.family.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择图片
 * 
 * @author wangshichao
 */
public class ActivitySelectPhoto extends CxRootActivity implements OnClickListener {

    private static final String TAG = "ActivitySelectPhoto";
    public static Uri mCurrentPictureUri = null; // 当前获取的图片的uri

    private static String mCurrentFileName = "";

    public static Bitmap kCurrentBitmap = null; // 当前图片bitmap

    public static boolean kIsCallPhotoZoom = true; // 是否调用图片缩放处理

    public static boolean kIsCallFilter = true; // 是否调用滤镜处理

    public static boolean kIsCallSysCamera = true; // 是否调用系统camera

    public static boolean kCallCamera = true; // 直接调用滤镜carmera

    public static boolean kCallGallery = false; // 直接调用滤镜gallery 
    
    public static boolean kIsCallSysGallery=false; //是否调用系统相册   暂不用 用kChoseSingle代替选多张用非系统相册  单张用系统相册
    
    public static boolean kChoseSingle = true; // 从相册选择单张图片true, 多张图片false
    public static boolean kIsFromChat = true; // 从相册选择单张图片true, 多张图片false
    
    public static String kFrom="";//判断访问该类的是哪个类 wentont.men 12.11 

    private Button mBtnTakePhoto, mBtnGalley, mBtnCancel;

    private static final int NONE = 0;

    private LinearLayout mLayout;
    
   
    public static ArrayList<String> mImgsUri = new ArrayList<String>(); // 存取多张图片uri
    
    
    /**
     * RkGpuImageConstants.sStep1  系统相机
     * RkGpuImageConstants.sStep2  非系统相机
     * RkGpuImageConstants.sStep3  系统相册
     * RkGpuImageConstants.sStep4  非系统相机
     * 
     * RkGpuImageConstants.sStep5  裁剪
     * 
     * 系统相机或系统相册   可选择是否裁剪   不能选择是否要滤镜
     * 非系统相机或非系统相册   可选择是否要滤镜   可选择是否裁剪 
     * 
     * 非系统相册  可以选择是否单张或多张
     * 
     * 三星 调用系统相机有问题  调用系统相机的部分默认调用非系统相机
     * 
     */
    
    private PictureUtils mPictureUtils = null;
    private Uri mImageUri = null;
    private File mImageTempFile = null;
//    public static final int TAKEPICTURE = 1;// 拍照
//    public static final int PHOTORESOULT = 2;// 结果
    public static final String IMAGE_UNSPECIFIED = "image/*";
    private AlertDialog.Builder mAlertDialog;
//    private DialogInterface mDialog;
//    private String mImageName = "";
	private String imageName;
	
	public static ArrayList<String> multImages; //如果是调用相册时，结束应该
	
	private boolean isGallery = false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cx_fa_libgpu_activity_main_camera);

        //锁屏标志位
         CxGlobalParams.getInstance().setCallGpuimage(true);
         showSelectDialog();

    }

    @Override
    public void onClick(final View v) {
       
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public static Uri getCurrentPictureUri() {
        return mCurrentPictureUri;
    }

    public static void setCurrentPictureUri(Uri uri) {
        mCurrentPictureUri = uri;
    }

    public static void setCurrentFileName(String fileName) {
        mCurrentFileName = fileName;
    }

    public static String getCurrentFileName() {
        return mCurrentFileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == NONE){
            ActivitySelectPhoto.this.finish();
            return;
        }
        if(resultCode != Activity.RESULT_OK){
        	ActivitySelectPhoto.this.finish();
        	return ;
        }
        	
       	
        
        //系统相机
        if(requestCode==CxGpuImageConstants.sStep1){ 
        
            Matrix matrix = new Matrix();

            int orientation =-1;
	        if(android.os.Build.MANUFACTURER.equals("Motorola") || (android.os.Build.MANUFACTURER.equals("Meizu") && android.os.Build.MODEL.equals("M040"))){ 
	        	ExifInterface exif = null;         	  
	        	try {
	        		exif = new ExifInterface(mImageUri.getPath());
	        	} catch (IOException ex) {
	        		Log.e("test", "cannot read exif", ex);
	        	}
	        	if (exif != null) {
	        		orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
	        	}
	        }
	  
        	if((android.os.Build.MANUFACTURER.equals("Motorola") || 
        			(android.os.Build.MANUFACTURER.equals("Meizu") && android.os.Build.MODEL.equals("M040")))&&(orientation==6) ){
        		
        		PictureUtils mPu=new PictureUtils(getApplicationContext());  
        		Bitmap bitmap = mPu.getImageThumbnail(mImageUri.getPath(), 640, 480);
        		matrix.reset();
        		matrix.postRotate(90);
              
        		Bitmap bMapRotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                      bitmap.getHeight(), matrix, true);
        		if(null!=bitmap) {
        		  bitmap.recycle();
        		  bitmap=null;
        		}
        		try {
        			bMapRotate.compress(CompressFormat.JPEG, 100, new FileOutputStream(mImageTempFile));                
        			if(null!=bMapRotate) {
        				bMapRotate.recycle();
        				bMapRotate=null;
        			}               
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		}
        		mImageUri= Uri.fromFile(mImageTempFile);
        		
        		
        		if(kIsCallPhotoZoom){
        			startPhotoZoom(mImageUri);
        		}else{
        			saveImage(bMapRotate);
        		}
        	}else{
        		CxLog.i("ActivitySelectPhoto_men", mImageUri.toString())  ;
        		CxLog.i("ActivitySelectPhoto_men", android.os.Build.MANUFACTURER+":"+android.os.Build.MODEL)  ;
//        		System.out.println(android.os.Build.MANUFACTURER);
        		if(kIsCallPhotoZoom){
        			startPhotoZoom(mImageUri);
        		}else{
        			saveImage(decodeUriAsBitmap(mImageUri));
        		}
        	}

        	return ;
        }
    	
    
    	
      //第三方相机
    	if(CxGpuImageConstants.sStep2==requestCode){
//        		System.out.println(">>>>>>8");
    		if (mCurrentPictureUri != null) {
    			CxLog.i("ActivitySelectPhoto_men", mCurrentPictureUri.toString())  ;
    			if(kIsCallPhotoZoom){
//	        			System.out.println("zoom");
        			startPhotoZoom(mCurrentPictureUri);
        			return;
    			}
//    			else if(!kChoseSingle){
//    			 // TODO 选择多张需要返回图片数组uri
//    				
//                    Intent intent = new Intent();
//                    intent.putStringArrayListExtra(RkGpuImageConstants.KEY_PICTURE_URI, (ArrayList)mImgsUri);
//                    setResult(Activity.RESULT_OK, intent);
//                    mCurrentPictureUri=null;
//                    ActivitySelectPhoto.this.finish();
//                    return;
//    			}
    			else{
    				
    				if(skipByFrom()){
    					CxLog.i("ActivitySelectPhoto_men", ">>>>>>>>>>>>>>>>>>>1")  ;
    					return ;
    				}
    				
	                Intent intent = new Intent();
	                intent.putExtra(CxGpuImageConstants.KEY_PICTURE_URI,mCurrentPictureUri.toString());
	                setResult(Activity.RESULT_OK, intent);
	                mCurrentPictureUri=null;
	                ActivitySelectPhoto.this.finish();
	                return;
    			
    			}
    		}
    		
    		return ;
    	}
    	//系统相册
    	if(CxGpuImageConstants.sStep3==requestCode){
//        		System.out.println(">>>>>>8");
    		if (mCurrentPictureUri != null) {
    			CxLog.i("ActivitySelectPhoto_men", mCurrentPictureUri.toString())  ;
//    			if(kIsCallPhotoZoom){
////	        			System.out.println("zoom");
//    				startPhotoZoom(mCurrentPictureUri);
//    				return;
//    			}else{     				
    				Intent intent = new Intent();
    				intent.putExtra(CxGpuImageConstants.KEY_PICTURE_URI,mCurrentPictureUri.toString());
    				setResult(Activity.RESULT_OK, intent);
    				mCurrentPictureUri=null;
    				ActivitySelectPhoto.this.finish();
    				return;
//    			}
    		}
    		return ;
    	}
    	
    	//第三方相册
    	if(CxGpuImageConstants.sStep4==requestCode){
        		
    		if (mImgsUri != null ) {
    			
    			if(skipByFrom()){
					return ;
				}
 	
				Intent intent = new Intent();
				intent.putStringArrayListExtra(CxGpuImageConstants.KEY_PICTURE_URI,mImgsUri);
				setResult(Activity.RESULT_OK, intent);
				ActivitySelectPhoto.this.finish();
				return;
    				
    			
    		}
    		
    		return ;
    	}
  
    	
        Log.v("result", "mCurrentPictureUri=" + mCurrentPictureUri);
//            if (mCurrentPictureUri != null) { //条件要改一下了
        CxLog.i("AcitivitySelectPhoto_men", ">>>>>>>>>>>>>>");
    	
        	

        	
    	//裁剪 缩放
    	if(requestCode==CxGpuImageConstants.sStep5){
    		
    		Bitmap photo = null;

            Log.d(TAG, "data = " + data.getData());
            Log.d(TAG, "extras = " + data.getExtras());
            if (data.getData() != null) {
                photo = decodeUriAsBitmap(data.getData());
                Log.d(TAG, "photo=" + photo);
            } else if (data.getExtras() != null) {
                photo = data.getExtras().getParcelable("data");
                Log.d(TAG, "photo1=" + photo);
            }else{
            	ActivitySelectPhoto.this.finish();
            }
            // 对得到的图片进行处理
            saveImage(photo);// 100)压缩文件	
    		
    		return ;
    	}
    	
        return; 
    }
    
    
    //根据from 来源确定 跳转到哪个类
    private boolean skipByFrom(){
    	
    	if (!isGallery) {
			try {
    			mImgsUri.clear();
    			mImgsUri.add(mCurrentPictureUri.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    	
    	if(kFrom.equals("RkZoneAddFeed")){ //直接调用而不跳转
		
			Intent dierect = new Intent(ActivitySelectPhoto.this, CxZoneAddFeed.class);
			dierect.putStringArrayListExtra(CxGlobalConst.MULT_SELECT_IMAGE, mImgsUri);
			setResult(Activity.RESULT_OK, dierect);
	        mCurrentPictureUri=null;
	        ActivitySelectPhoto.this.finish();
			return true;
    	}
    	
    	if(kFrom.equals("RkNeighbourAddInvitation")){ //直接调用而不跳转
    		
    		Intent dierect = new Intent(ActivitySelectPhoto.this, CxNeighbourAddInvitation.class);
    		dierect.putStringArrayListExtra(CxGlobalConst.MULT_SELECT_IMAGE, mImgsUri);
    		setResult(Activity.RESULT_OK, dierect);
    		mCurrentPictureUri=null;
    		ActivitySelectPhoto.this.finish();
    		return true;
    	}
    	
    	if(kFrom.equals("CxKidAddFeed")){ //直接调用而不跳转	
    		Intent dierect = new Intent(ActivitySelectPhoto.this, CxKidAddFeed.class);
    		dierect.putStringArrayListExtra(CxGlobalConst.MULT_SELECT_IMAGE, mImgsUri);
    		setResult(Activity.RESULT_OK, dierect);
    		mCurrentPictureUri=null;
    		ActivitySelectPhoto.this.finish();
    		return true;
    	}
    	
    	
    	if(kFrom.equals("RkNeighbourFrament") || kFrom.equals("RkNbOurHome")){            	
    		Intent addInvitation = new Intent(ActivitySelectPhoto.this,CxNeighbourAddInvitation.class);
			addInvitation.putExtra(CxGlobalConst.S_NEIGHBOUR_SHARED_TYPE, 1);
			addInvitation.putStringArrayListExtra(CxGlobalConst.S_NEIGHBOUR_SHARED_IMAGE, mImgsUri);
            startActivity(addInvitation);
            overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
            mCurrentPictureUri=null;
            kFrom="";
            ActivitySelectPhoto.this.finish();
            return  true;
    	}// 判断来源  直接跳转到需要的类  不再会原来的类了  wentong.men 12.11
        	
    	if(kFrom.equals("RkUserPairZone")){            	
    		Intent toAddFeed = new Intent(ActivitySelectPhoto.this, CxZoneAddFeed.class);
    		toAddFeed.putExtra(CxGlobalConst.S_ZONE_SHARED_TYPE, 1);
    		toAddFeed.putStringArrayListExtra(CxGlobalConst.S_ZONE_SHARED_IMAGE, mImgsUri);
    		startActivity(toAddFeed);
    		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    		mCurrentPictureUri=null;
    		kFrom="";
    		ActivitySelectPhoto.this.finish();
    		return true;
    	}// 判断来源  直接跳转到需要的类  不再会原来的类了  wentong.men 12.11
    	
    	if(kFrom.equals("CxKidFragment")){            	
    		Intent toAddFeed = new Intent(ActivitySelectPhoto.this, CxKidAddFeed.class);
    		toAddFeed.putExtra(CxGlobalConst.S_KID_SHARED_TYPE, 1);
    		toAddFeed.putStringArrayListExtra(CxGlobalConst.S_KID_SHARED_IMAGE, mImgsUri);
    		startActivity(toAddFeed);
    		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    		mCurrentPictureUri=null;
    		kFrom="";
    		ActivitySelectPhoto.this.finish();
    		return true;
    	}// 判断来源  直接跳转到需要的类  不再会原来的类了  wentong.men 12.11
    	
    	return false;
    }
    
    

    @Override
    protected void onDestroy() {
    	isGallery = false;
        CxGlobalParams.getInstance().setCallGpuimage(false);
        CxLog.v(TAG, "on destory come in");
        if(null != mAlertDialog){
            mAlertDialog.create().dismiss();
        }
        if( null != mImageTempFile){
            mImageTempFile.delete();
            mImageTempFile = null;
            mImageUri = null;
//            System.out.println("delete");
        }
        
        super.onDestroy();
    }

    private void callSysCamera(){
        mPictureUtils = new PictureUtils(ActivitySelectPhoto.this);
        imageName = System.currentTimeMillis() + ".jpgbak";
        mImageTempFile = new File(Environment.getExternalStorageDirectory(), imageName);
        mImageUri = Uri.fromFile(mImageTempFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent,CxGpuImageConstants.sStep1 );
    }
    
    /**
     * 裁剪图片
     */
    public void startPhotoZoom(Uri uri) {
//    	System.out.println("startzoom");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true); // 保留比例
        intent.putExtra("return-data", true); // 将数据保留在Bitmap中返回
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, CxGpuImageConstants.sStep5);
    }
    
    /**
     * 图片uri 转换为 Bitmap
     * 
     * @param picture
     *            uri
     * @return bitmap
     */
    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    private void saveImage(final Bitmap image) {
        String folderName = "Android/data/"+this.getPackageName()+"/images";
        
        String fileName = System.currentTimeMillis() + ".jpgbak";
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path, folderName + "/" + fileName);
        FileOutputStream fileOutputStream = null;
        try {
            file.getParentFile().mkdirs();
            fileOutputStream = new FileOutputStream(file);
            if(null != image){
                if(image.compress(CompressFormat.JPEG, 30, fileOutputStream)){
                    fileOutputStream.flush();
                }
            }
            final Uri uri = Uri.fromFile(file);
//          Toast.makeText(this, "Saved: " + uri.toString(), Toast.LENGTH_SHORT)
//                  .show();
//            setResult(Activity.RESULT_OK);
            if( null != mImageTempFile){
                mImageTempFile.delete();
                mImageTempFile = null;
                mImageUri = null;
//                System.out.println("delete");
            }
            if (uri != null) {
//            	System.out.println("return uri");
                Intent intent = new Intent();
                intent.putExtra(CxGpuImageConstants.KEY_PICTURE_URI,uri.toString());
                setResult(Activity.RESULT_OK, intent);
            }
            ActivitySelectPhoto.this.finish();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            file.delete();
            e.printStackTrace();
        } catch (IOException e) {
            file.delete();
            e.printStackTrace();
        }
    }
    
    private void saveImage(Uri uri) {
        ActivitySelectPhoto.setCurrentPictureUri(uri);
        if (uri != null) {
            Intent intent = new Intent();
            intent.putExtra(CxGpuImageConstants.KEY_PICTURE_URI,
                   uri.toString());
            setResult(Activity.RESULT_OK, intent);
        }
        ActivitySelectPhoto.this.finish();
    }
    
    
    private void showSelectDialog(){
    	isGallery = false;
    	
        mAlertDialog = new AlertDialog.Builder(ActivitySelectPhoto.this);
        mAlertDialog.setTitle("");
        mAlertDialog.setItems(new String[] {
                getString(R.string.cx_fa_libgpu_take_photo), getString(R.string.cx_fa_libgpu_pick_photo), getString(R.string.cx_fa_libgpu_cancel)
        }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(!checkSdCardExist()){
                    return;
                }
                switch (which) {
                    case 0: //拍照
                        if (kIsCallSysCamera) {     //系统相机                	
                        	if(android.os.Build.MANUFACTURER.equals(ActivityCamera.SAMSUNG)){      //三星会调用第三方相机
                            	startActivityForResult(new Intent(ActivitySelectPhoto.this,ActivityCamera.class), CxGpuImageConstants.sStep2);                          	
                            }else{
                            	//调用系统相机
                            	callSysCamera();
                            }
                        } else {  //调用第三方相机
                            startActivityForResult(new Intent(ActivitySelectPhoto.this,
                                    ActivityCamera.class), CxGpuImageConstants.sStep2);
                        }
                        break;
                    case 1:        //相册        	
                    	if(!kChoseSingle){ //多选用非系统相册
                    		//  从相册选择多张图片
                        	isGallery = true;
                        	Intent target = new Intent(ActivitySelectPhoto.this,CxGalleryActivity.class);
                        	startActivityForResult(target, CxGpuImageConstants.sStep4);
                    	}else{ //系统相册
                    		
                    		if(kIsCallFilter){ //用滤镜   注：  如使用滤镜则不再判断是否缩放  是一个不足
                    			startActivityForResult(new Intent(ActivitySelectPhoto.this,ActivityGallery.class), 
                    					CxGpuImageConstants.sStep3);
                    		}else{
                    			if (kIsCallPhotoZoom) { //是否缩放
                                    startActivityForResult(new Intent(ActivitySelectPhoto.this,
                                            ActivityPhotoZoom.class), CxGpuImageConstants.sStep3);
                                                              
                                } else{
                                	startActivityForResult(new Intent(ActivitySelectPhoto.this,ActivitySysGallery.class), 
                              				 CxGpuImageConstants.sStep3);
                                }
                    			
                    		}
                    	}
                        break;
                    case 2:
                        ActivitySelectPhoto.this.finish();
                        break;
                }
                return;
            }

        });
        mAlertDialog.show();
    }
    @Override
    protected void onPause() {
        if(null != mAlertDialog && mAlertDialog.create().isShowing()){
            mAlertDialog.create().dismiss();
        }
        super.onPause();
    }
    // And to convert the image URI to the direct file system path of the image file
    public String getRealPathFromURI(Uri contentUri) {

            // can post image
            String [] proj={MediaStore.Images.Media.DATA};
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery( contentUri,
                            proj, // Which columns to return
                            null,       // WHERE clause; which rows to return (all rows)
                            null,       // WHERE clause selection arguments (none)
                            null); // Order-by clause (ascending by name)
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
    }
    private boolean checkSdCardExist() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            new AlertDialog.Builder(this)
            .setTitle(R.string.cx_fa_alert_dialog_tip)
            .setMessage(R.string.cx_fa_unuseable_extralstorage)
            .setPositiveButton(R.string.cx_fa_confirm_text,new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivitySelectPhoto.this.finish();
                }
            })
            .show();
            return false;
        } else {
            return true;
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        CxLog.i(TAG, "id>>>" + id);
        return super.onCreateDialog(id);
    }
}
