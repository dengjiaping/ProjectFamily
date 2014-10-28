
package com.chuxin.family.libs.gpuimage.activity;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 对图片的处理
 * 
 * @author wangshichao 2013-03-15
 */
public class ActivityPhotoZoom extends CxRootActivity {

    private static final String TAG = "ACTIVITYPHOTOZOOM";

    public static final int NONE = 0;

    public static final int PHOTORESOULT = 1;// 结果

    public static final String IMAGE_UNSPECIFIED = "image/*";

    public static Bitmap kCurrentBitmap = null;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!checkSdCardExist()){
            return;
        }
        startPhotoZoom();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode=" + requestCode);
        if (resultCode == CxGpuImageConstants.sStep5) {
            setResult(Activity.RESULT_OK);
            finish();
        } else if (resultCode == NONE) {
        	finish();
            return;
        } else {
            if (data == null)
                return;
            // 处理结果
            if (requestCode == PHOTORESOULT) {
                Bitmap photo = null;
                if (data.getData() != null) {
                    photo = decodeUriAsBitmap(data.getData());
                    Log.d(TAG, "photo=" + photo);
                } else if (data.getExtras() != null) {
                    photo = decodeUriAsBitmap(getImageUri());
                }
                
                kCurrentBitmap = photo;
                saveImage(kCurrentBitmap);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 图片uri 转换为 Bitmap
     * 
     * @param picture uri
     * @return bitmap
     */
    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 裁剪图片
     */
    public void startPhotoZoom() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null); // ACTION_PICK,ACTION_GET_CONTENT
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        // 裁剪比例
        intent.putExtra("aspectX", 1); // x方向比例
        intent.putExtra("aspectY", 1); // y方向比例
        // 裁剪图片
        intent.putExtra("outputX", 600); // 宽
        intent.putExtra("outputY", 600); // 高
        intent.putExtra("scale", true); // 保留比例
        intent.putExtra("return-data", false); // 将数据保留在Bitmap中返回
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("circleCrop", true);
        startActivityForResult(intent, PHOTORESOULT);
    }
    
    private Uri getImageUri(){
        return Uri.fromFile(getTempFile());
    }
    
    private File getTempFile(){
        try {
            String folderName = "Android/data/"+this.getPackageName()+"/images";
            String fileName = "temp.jpgbak";
            File path = Environment.getExternalStorageDirectory();
            File file = new File(path, folderName + "/" + fileName);
            if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
//            file.createNewFile();
            return file;
        } catch (IOException e) {
            CxLog.e(TAG, "" + e.getMessage());
        }
        return null;
    }

    private void saveImage(final Bitmap image) {
    	if (null == image) {
			return;
		}
        String folderName = "Android/data/"+this.getPackageName()+"/images";
        String fileName = System.currentTimeMillis() + ".jpgbak";
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path, folderName + "/" + fileName);
        try {
            file.getParentFile().mkdirs();
            image.compress(CompressFormat.JPEG, 100, new FileOutputStream(file)); // 感觉文件太大，可以再压缩
            final Uri uri = Uri.fromFile(file);
            ActivitySelectPhoto.setCurrentPictureUri(uri);

            setResult(Activity.RESULT_OK);
//            if( null != getTempFile() ){
//                getTempFile().delete(); // 删除临时文件
//            }
            ActivityPhotoZoom.this.finish();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private boolean checkSdCardExist() {
        // TODO Auto-generated method stub
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            new AlertDialog.Builder(this)
            .setTitle(R.string.cx_fa_alert_dialog_tip)
            .setMessage(R.string.cx_fa_unuseable_extralstorage)
            .setPositiveButton(R.string.cx_fa_confirm_text,new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    ActivityPhotoZoom.this.finish();
                }
            })
            .show();
            return false;
        } else {
            return true;
        }
    }

}
