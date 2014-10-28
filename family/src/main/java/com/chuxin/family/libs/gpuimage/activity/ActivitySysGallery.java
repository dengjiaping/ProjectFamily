package com.chuxin.family.libs.gpuimage.activity;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.libs.gpuimage.utils.PictureUtils;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * 调用出系统默认camera
 * 
 * @author wangshichao
 * 
 */
public class ActivitySysGallery extends CxRootActivity {

	private static final String TAG = "SysGallery";
	public static final int NONE = 0;
	public static final int SELECT_FROM_GALLERY = 1;// GALLLERY
	private PictureUtils mPictureUtils = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!checkSdCardExist()){
			return;
		}
		Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, SELECT_FROM_GALLERY);
		
		mPictureUtils = new PictureUtils(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		CxLog.d(TAG, "data>>>" + data);
		if (resultCode == NONE) {
			ActivitySysGallery.this.finish();
			return;
		}
		Bitmap photo = null;
		Bitmap newbitmap = null;
		Uri uri = null;
		switch(requestCode){
			case SELECT_FROM_GALLERY:
				if (null != data.getData()) {
					uri = data.getData();
					CxLog.d(TAG, "uri>>>" + uri);
				} else if (null != data.getExtras()) {
					photo = data.getExtras().getParcelable("data");
					CxLog.v(TAG, "photo1=" + photo);
				}
				if(null != photo){
					uri = Uri.parse(MediaStore.Images.Media.insertImage(
						getContentResolver(), photo, null, null));
					CxLog.v(TAG, "uritemp>>>" + uri);
				}
					String imagePath = mPictureUtils.getRealPathFromURI(ActivitySysGallery.this, uri);
					CxLog.d(TAG, "imagePath>>>" + imagePath);
					photo = mPictureUtils.getImage(imagePath, 640, 480);
					CxLog.d(TAG, "photo=" + photo);
					// 对得到的图片进行处理
					saveImage(Uri.parse(imagePath));
					photo.recycle();
				break;
		}
	}
	private void saveImage(Uri uri) {
			ActivitySelectPhoto.setCurrentPictureUri(uri);
			setResult(Activity.RESULT_OK);
			ActivitySysGallery.this.finish();
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
                    ActivitySysGallery.this.finish();
                }
            })
            .show();
			return false;
		} else {
			return true;
		}
	}
}
