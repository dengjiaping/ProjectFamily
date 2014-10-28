package com.chuxin.family.libs.gpuimage.activity;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.libs.gpuimage.GPUImageFilterAdapter;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.libs.gpuimage.utils.HorizontalListView;
import com.chuxin.family.libs.gpuimage.utils.PictureUtils;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImage.OnPictureSavedListener;
import jp.co.cyberagent.android.gpuimage.GPUImage.OnSetImageFinishListener;
/**
 * 调用系统相册选择图片，选择对图片进行缩放或滤镜处理
 * @author wangshichao
 *
 */
public class ActivityGallery extends CxRootActivity implements OnClickListener,
		OnPictureSavedListener, OnSetImageFinishListener {

	private final static String TAG = "SHICHAO_ACTIVITY_GALLERY";
	private static final int REQUEST_PICK_IMAGE = 1;
	private GPUImage mGPUImage;
	private GLSurfaceView mPhotoImageView = null;
	private GPUImageFilterAdapter mGpuImageFilterAdapter;
	private ProgressDialog mProgressDialog;
	
	   private static final int SAVE_IMAGE = 0;


	    public Handler mGalleryHandler = new Handler(){
	        public void handleMessage(android.os.Message msg) {
	            super.handleMessage(msg);
	            switch(msg.what){
	                case SAVE_IMAGE:
	                    saveImage();
	                    break;
	            }
	        };
	    };

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	      if(!checkSdCardExist()){
	            return;
	        }
		setContentView(R.layout.cx_fa_libgpu_activity_gallery);
		findViewById(R.id.galleryConfirm).setOnClickListener(this);
		findViewById(R.id.galleryConfirm).setClickable(false);
		findViewById(R.id.galleryConfirm).setVisibility(View.INVISIBLE);
		findViewById(R.id.galleryBack).setOnClickListener(this);
		HorizontalListView listview = (HorizontalListView) findViewById(R.id.listview);
		listview.setBackgroundColor(Color.TRANSPARENT);
		Log.v(TAG, "list view height=" + listview.getHeight());
		mGPUImage = new GPUImage(this);
		mGpuImageFilterAdapter = new GPUImageFilterAdapter(this, mGPUImage);
		listview.setAdapter(mGpuImageFilterAdapter);
		mPhotoImageView = (GLSurfaceView) findViewById(R.id.surfaceView);
		mGPUImage.setGLSurfaceView(mPhotoImageView);
		// Bundle bundle = getIntent().getExtras();
		// Bitmap data=bundle.getParcelable("photoimage");//读出数据
		showProgressDialog();
		if(!ActivitySelectPhoto.kIsCallPhotoZoom && ActivitySelectPhoto.kIsCallFilter){
			 Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			 photoPickerIntent.setType("image/*");
			 startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE);
		} else {
			Log.d(TAG, "ActivityMain.mCurrentBitmap = "
					+ ActivityPhotoZoom.kCurrentBitmap);
			mGPUImage.setImage(ActivityPhotoZoom.kCurrentBitmap);
		}
//		dismissProgressDialog();
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		switch (requestCode) {
		case REQUEST_PICK_IMAGE:
			if (resultCode == RESULT_OK) {
				Log.d("data", "data = " + data.getData());
				
				handleImage(data.getData());
			} else {
				finish();
			}
			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	@Override
	public void onClick(final View v) {
		if (v.getId() == R.id.galleryBack) {
            ActivityGallery.this.finish();
        } else if (v.getId() == R.id.galleryConfirm) {
            CxLoadingUtil.getInstance().showLoading(ActivityGallery.this, true);
            Message message = Message.obtain(mGalleryHandler, SAVE_IMAGE);
            message.sendToTarget();
//            saveImage();
            //ActivityGallery.this.finish();
        }
	}

	@Override
	public void onPictureSaved(final Uri uri) {
//		Toast.makeText(this, "Saved: " + uri.toString(), Toast.LENGTH_SHORT)
//				.show();
		ActivitySelectPhoto.setCurrentPictureUri(uri);
		CxLoadingUtil.getInstance().dismissLoading();
		if(ActivitySelectPhoto.kIsCallPhotoZoom){
			setResult(CxGpuImageConstants.sStep5);
		} else {
		    Intent intent = new Intent();
            intent.putExtra(CxGpuImageConstants.KEY_PICTURE_URI,
                    uri.toString());
            setResult(Activity.RESULT_OK, intent);
//			setResult(Activity.RESULT_OK);
		}
		ActivityGallery.this.finish();
	}

	private void saveImage() {
		String fileName = "";
		if(!TextUtils.isEmpty(ActivitySelectPhoto.getCurrentFileName())){
			fileName = ActivitySelectPhoto.getCurrentFileName();
		} else {
			fileName = System.currentTimeMillis() + ".jpgbak";
		}
		mGPUImage.saveToPictures("Android/data/"+this.getPackageName()+"/images", fileName, this);	}

	private void handleImage(final Uri selectedImage) {
	    PictureUtils pu = new PictureUtils(ActivityGallery.this);
		Uri selectedImageuri = pu.changeContentUri(selectedImage);
		mGPUImage.setImage(selectedImageuri, this);
	}

    /**
     * 显示进度框
     * @return
     */
    private ProgressDialog showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setTitle(R.string.login_dialog_title);
//            mProgressDialog.setMessage(getString(R.string.login_dialog_message));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
        }
        mProgressDialog.show();
        return mProgressDialog;
    }

    private void dismissProgressDialog() {
        try {
            if (mProgressDialog !=null) {
                mProgressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            CxLog.e("dismissProgressDialog", "" + e.getMessage());
        }
    }
    @Override
    protected void onDestroy() {
//        if(null != GPUImage.mProgressDialog){
//            GPUImage.dismissProgressDialog();
//            GPUImage.mProgressDialog = null;
//        }
        dismissProgressDialog();
    	CxLoadingUtil.getInstance().dismissLoading();
        super.onDestroy();
    }

    @Override
    public void onSetImageFinish(boolean finish) {
        if(finish){
            findViewById(R.id.galleryConfirm).setVisibility(View.VISIBLE);
            findViewById(R.id.galleryConfirm).setClickable(true);
        }
        dismissProgressDialog();
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
                    ActivityGallery.this.finish();
                }
            })
            .show();
            return false;
        } else {
            return true;
        }
    }
}
