package com.chuxin.family.libs.gpuimage.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chuxin.family.R;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.libs.gpuimage.GPUImageFilterAdapter;
import com.chuxin.family.libs.gpuimage.utils.CameraHelper;
import com.chuxin.family.libs.gpuimage.utils.CameraHelper.CameraInfo2;
import com.chuxin.family.libs.gpuimage.utils.HorizontalListView;
import com.chuxin.family.libs.gpuimage.utils.PictureUtils;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImage.OnPictureSavedListener;
import jp.co.cyberagent.android.gpuimage.GPUImage.OnSetImageFinishListener;

/**
 * 使用gpuimage库带滤镜的camera
 *
 * @author wangshichao
 */
public class ActivityCamera extends CxRootActivity implements OnClickListener,
        OnPictureSavedListener, OnSetImageFinishListener {

    private final static String TAG = "ActivityCamera";
    protected static final String SAMSUNG = "samsung";

    private GPUImage mGPUImage;

    private CameraHelper mCameraHelper;

    private CameraLoader mCamera;

    private ImageButton mButtonCapture; // 拍照按钮

    private ImageButton mButtonConfirm; // 确认按钮

    private ImageButton mButtonTakenAgain; // 重拍按钮

    private GLSurfaceView mGlSurfaceView;

    private Bitmap mBitmap = null;

    private static final int SAVE_IMAGE = 0;
    private PictureUtils mPu;


    public Handler mCameraHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SAVE_IMAGE:
                    saveImage();
                    break;
            }
        }


    };

    public static int getResId(String variableName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.cx_fa_libgpu_activity_camera);
        mButtonCapture = (ImageButton) findViewById(R.id.cx_fa_libgpu_button_capture);
        mButtonCapture.setOnClickListener(this);

        ImageButton mButtonCameraBack = (ImageButton) findViewById(R.id.cameraBack);
        mButtonCameraBack.setOnClickListener(this);
        mButtonConfirm = (ImageButton) findViewById(R.id.button_confirm);
        mButtonConfirm.setOnClickListener(this);
        mButtonTakenAgain = (ImageButton) findViewById(R.id.button_taken_again);
        mButtonTakenAgain.setOnClickListener(this);
        HorizontalListView listview = (HorizontalListView) findViewById(R.id.listview);

        checkSdCardExist();

        mGPUImage = new GPUImage(this);
        GPUImageFilterAdapter mGpuImageFilterAdapter = new GPUImageFilterAdapter(this, mGPUImage);
        // listview.setAdapter(mAdapter);
        listview.setAdapter(mGpuImageFilterAdapter);

        mGlSurfaceView = (GLSurfaceView) findViewById(R.id.surfaceViewCamera);
        mGPUImage.setGLSurfaceView(mGlSurfaceView);

        mCameraHelper = new CameraHelper(this);
        mCamera = new CameraLoader();


        View cameraSwitchView = findViewById(R.id.img_switch_camera);
        cameraSwitchView.setOnClickListener(this);
        if (!mCameraHelper.hasFrontCamera() || !mCameraHelper.hasBackCamera()) {
            cameraSwitchView.setVisibility(View.GONE);
        }
        mCamera.onResume();
        mPu = new PictureUtils(ActivityCamera.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//		mCamera.onResume();
        if (mCamera.mCameraInstance == null) {
            ToastUtil.getSimpleToast(this, -3, "相机权限可能被拒绝了", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        mCamera.onPause();
        super.onPause();
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.cx_fa_libgpu_button_capture) {
            mButtonCapture.setClickable(false);
            if (mCamera.mCameraInstance.getParameters().getFocusMode()
                    .equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                takePicture();
            } else {
                mCamera.mCameraInstance
                        .autoFocus(new Camera.AutoFocusCallback() {

                            @Override
                            public void onAutoFocus(final boolean success,
                                                    final Camera camera) {
                                takePicture();
                            }
                        });
            }
        }

        if (v.getId() == R.id.img_switch_camera) {
            mCamera.switchCamera();
        }
        if (v.getId() == R.id.button_confirm) {
            Log.v("tag", "confirm image");
            mButtonTakenAgain.setVisibility(View.INVISIBLE);
            mButtonTakenAgain.setClickable(false);
            Message message = Message.obtain(mCameraHandler, SAVE_IMAGE);
            message.sendToTarget();
//			saveImage();
            // ActivityCamera.this.finish();
        }

        if (v.getId() == R.id.cameraBack) {
            ActivityCamera.this.finish();
        }
    }


    private void takePicture() {
        WindowManager windowManager = getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        final int w = dm.widthPixels;
        final int h = dm.heightPixels;
        Log.v(TAG, "w=" + w + " h=" + h);
        Camera.Parameters params = mCamera.mCameraInstance.getParameters();

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        switch (metrics.densityDpi) {
            case DisplayMetrics.DENSITY_XXHIGH:
                params.setPictureSize(1280, 960);
                break;
            case DisplayMetrics.DENSITY_XHIGH:
            case DisplayMetrics.DENSITY_HIGH:
                params.setPictureSize(640, 480);
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
            case DisplayMetrics.DENSITY_LOW:
                params.setPictureSize(320, 240);
                break;
            default:
                params.setPictureSize(dm.widthPixels, dm.heightPixels);
                break;
        }
//		
//		List<Camera.Size> psize = params.getSupportedPictureSizes();// 取得相机所支持多少图片大小的个数
//        if (null != psize && 0 < psize.size()) {
//            int sizewidths[] = new int[psize.size()];
//            int sizeheights[] = new int[psize.size()];
//            float densitys[] = new float[psize.size()];
//            for (int i = 0; i < psize.size(); i++) {
//                Camera.Size size = (Camera.Size) psize.get(i);
//                if (size.width > 1280) {
//                    params.setPictureSize(1280, 960);
//                } else {
//                    params.setPictureSize(640, 480);
//                }
//            }
//        } else {
//            params.setPictureSize(display.getWidth(), display.getHeight());
//        }


        params.setRotation(90);
        mCamera.mCameraInstance.setParameters(params);
//		mCamera.mCameraInstance.setDisplayOrientation(90);
        for (Camera.Size size2 : mCamera.mCameraInstance.getParameters()
                .getSupportedPictureSizes()) {
            Log.i(TAG, "Supported: " + size2.width + "x" + size2.height);
        }
        mCamera.mCameraInstance.takePicture(null, null,
                new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, final Camera camera) {
                        mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//					   final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
//                      if (pictureFile == null) {
//                          Log.d(TAG,
//                                  "Error creating media file, check storage permissions");
//                          return;
//                      }
//
//                      try {
//                          FileOutputStream fos = new FileOutputStream(
//                                  pictureFile);
//                          fos.write(data);
//                          fos.close();
//                      } catch (FileNotFoundException e) {
//                          Log.d(TAG, "File not found: " + e.getMessage());
//                      } catch (IOException e) {
//                          Log.d(TAG,
//                                  "Error accessing file: " + e.getMessage());
//                      }
//                      data = null;
//                      PictureUtils pu = new PictureUtils(ActivityCamera.this);
//                      bMap = pu.rotateImage(bMap, pictureFile);
//                       Bitmap bitmap = null;
//                      if(null != mBitmap){
//                          mBitmap.recycle();
//                          mBitmap = null;
//                      }
//                      try {
//                          mBitmap = BitmapFactory.decodeFile(pictureFile
//                                    .getAbsolutePath());
//                      } catch (OutOfMemoryError e) {
//                          mBitmap.recycle();
//                          e.printStackTrace();
//                      }
                        // TODO 需要机型适配
//					  if(getPhoneManufacturer().equals("samsung") && getPhoneModel().equals("GT-I9500")){
//						  //camera.stopPreview();
////						  mCamera.onPause();
////						  camera.release();
//					      mGPUImage.setImage(mBitmap);
//					  }
//                      mGPUImage.setImage(bitMap);
//                      Uri imageUri = pu.changeContentUri(Uri.fromFile(pictureFile));
//					  mGPUImage.setImage(imageUri, ActivityCamera.this);
                        mButtonCapture.setVisibility(View.GONE);
                        mButtonConfirm.setVisibility(View.VISIBLE);
                        mButtonTakenAgain.setVisibility(View.VISIBLE);

                        mButtonTakenAgain
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        if (getPhoneManufacturer().equals(SAMSUNG) && getPhoneModel().equals("GT-I9500")) {
                                            //camera.stopPreview();
//					                          mCamera.onResume();
                                            camera.startPreview();

                                        } else {
                                            camera.startPreview();
                                        }
                                        mGlSurfaceView
                                                .setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                                        mButtonCapture
                                                .setVisibility(View.VISIBLE);
                                        mButtonCapture.setClickable(true);
                                        mButtonConfirm.setVisibility(View.GONE);
                                        mButtonTakenAgain
                                                .setVisibility(View.GONE);
                                    }
                                });
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if(null != mBitmap){ //聂超 2013-12-9修复Bitmap为null的bug
          mBitmap.recycle();
		    System.gc();
		}*/
    }

    public static final int MEDIA_TYPE_IMAGE = 1;

    public static final int MEDIA_TYPE_VIDEO = 2;

    private class CameraLoader {
        private int mCurrentCameraId = 0;

        private Camera mCameraInstance;

        public void onResume() {
            setUpCamera(mCurrentCameraId);
        }

        public void onPause() {
            releaseCamera();
        }

        public void switchCamera() {
            releaseCamera();
            mCurrentCameraId = (mCurrentCameraId + 1)
                    % mCameraHelper.getNumberOfCameras();
            setUpCamera(mCurrentCameraId);
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) // 这个API已做判断处理
        private void setUpCamera(final int id) {
            mCameraInstance = getCameraInstance(id);
            if (null == mCameraInstance)
                return;
            Parameters parameters = mCameraInstance.getParameters();
//				parameters.setPreviewSize(cs.width, cs.height);
            // TODO 根据手机型号做相应适配
            parameters.setPreviewSize(640, 480);
            parameters.set("orientation", "portrait");
            parameters.set("rotation", 90);

            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCameraInstance.setParameters(parameters);

            int orientation = mCameraHelper.getCameraDisplayOrientation(
                    ActivityCamera.this, mCurrentCameraId);
//			mCameraHelper.setCameraDisplayOrientation(ActivityCamera.this, mCurrentCameraId, mCameraInstance);
            CameraInfo2 cameraInfo = new CameraInfo2();
            mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
            boolean flipHorizontal = cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT;
            mGPUImage.setUpCamera(mCameraInstance, orientation, flipHorizontal,
                    false);
        }

        /**
         * A safe way to get an instance of the Camera object.
         */
        private Camera getCameraInstance(final int id) {
            Camera c = null;
            try {
                c = mCameraHelper.openCamera(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }

        private void releaseCamera() {
            if (null != mCameraInstance) {
                mCameraInstance.setPreviewCallback(null);
                mCameraInstance.release();
                mCameraInstance = null;
            }
        }
    }

    private void saveImage() {
        CxLoadingUtil.getInstance().showLoading(ActivityCamera.this, true);
        String fileName = System.currentTimeMillis() + ".jpgbak";

        mGPUImage.setImage(mBitmap);
        mGPUImage.saveToPictures("Android/data/" + this.getPackageName() + "/images", fileName, this);
    }

    @Override
    public void onPictureSaved(Uri uri) {
        mGlSurfaceView.setVisibility(View.INVISIBLE);
//	    PictureUtils pu=new PictureUtils(getApplicationContext());
//	    try {
//			int i = pu.getImageOrientation(uri.getPath());
//			System.out.println(i);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}

        Uri imageUri = uri;
        //	    getPhoneManufacturer().equals("samsung") && getPhoneModel().equals("GT-N7000")
        if (getPhoneManufacturer().equals(SAMSUNG) || getPhoneManufacturer().equals("Motorola")) {
            Bitmap bitmap = mPu.getBitmapFromUri(uri.getPath());
            File newFile = new File(uri.getPath());
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.postRotate(90);
            Bitmap bMapRotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            bitmap = bMapRotate;
            File adjustFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            try {
                bMapRotate.compress(CompressFormat.JPEG, 30, new FileOutputStream(adjustFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageUri = Uri.fromFile(adjustFile);
            ActivitySelectPhoto.setCurrentPictureUri(imageUri);
            if (newFile.exists()) {
                newFile.delete();
            }
            if (null != bitmap) {
                bitmap.recycle();
            }
            if (null != bMapRotate) {
                bMapRotate.recycle();
            }
        } else {
            ActivitySelectPhoto.setCurrentPictureUri(imageUri);
        }
//        Toast.makeText(this, imageUri.toString(), Toast.LENGTH_LONG).show();
        CxLoadingUtil.getInstance().dismissLoading();
        mButtonTakenAgain.setClickable(true);
        if (ActivitySelectPhoto.kCallCamera) {
            Intent intent = new Intent();
            intent.putExtra(CxGpuImageConstants.KEY_PICTURE_URI, imageUri.toString());
            setResult(Activity.RESULT_OK, intent);
        } else {
            setResult(Activity.RESULT_OK);
        }
        ActivityCamera.this.finish();
    }

    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    public static String getPhoneManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    private File getOutputMediaFile(final int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        checkSdCardExist();
        File mediaStorageDir = new File(
                Environment
                        .getExternalStorageDirectory(),
                "Android/data/" + this.getPackageName() + "/images");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                CxLog.d("Android/data/" + this.getPackageName() + "/images", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
//                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + System.currentTimeMillis() + ".jpgbak");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * 检查sdcard是否存在
     */
    private boolean checkSdCardExist() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.cx_fa_alert_dialog_tip)
                    .setMessage(R.string.cx_fa_unuseable_extralstorage)
                    .setPositiveButton(R.string.cx_fa_confirm_text, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCamera.this.finish();
                        }
                    })
                    .show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onSetImageFinish(boolean finish) {

    }
    
/*    public void adjustImage(Uri imageUri){
        Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imageUri);
        Matrix matrix = new Matrix();
        // rotate the Bitmap (there a problem with exif so we'll query the mediaStore for orientation
        Cursor cursor = getApplicationContext().getContentResolver().query(uri,
              new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            int orientation =  cursor.getInt(0);
            matrix.preRotate(orientation);
        }
    }*/

}
