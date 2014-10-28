package com.chuxin.family.gallery;

import com.chuxin.family.R;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.CxImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 图片最多选9张
 *
 * @author shichao.wang
 */
public class CxGalleryActivity extends CxRootActivity
        implements AdapterView.OnItemClickListener, OnClickListener {

    //文件夹部分
    private GridView mGalleryFileView;
    private GalleryFileAdapter mFileAdapter;
    int mItemSize;
    List<Map<String, String>> mGalleryData;

    boolean isSpecFolder = false;

    //特定文件夹中的图片
    GridView mFileGridView;
    ImageSpecialFolder mFolderImageAdapter;
    List<String> mImagePath; //
    String mTargetPath;
    ArrayList<String> mSelected = new ArrayList<String>();
    Button mBackBtn, mSubmitBtn;
    TextView mTitle;
    HashMap<String, String> mSelectedImage = new HashMap<String, String>();
    int mSelectedNums = 0;

    @Override
    protected void onDestroy() {
        try {
            mSelected.clear();
            mSelectedImage.clear();
            mSelectedNums = 0;
            mImagePath.clear();
            mGalleryData.clear();
            mImagePath = null;
            mSelected = null;
            mSelectedImage = null;
            mGalleryData = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.cx_fa_activity_gallery_files);

        // 4.4以上版本需要使用新的方法使其能够扫描sd卡。但必须有一个具体的扫描路径
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + File.pathSeparator}, null, null);
        } else {

            CxGalleryActivity.this.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }

        mItemSize = getResources().getDimensionPixelSize(R.dimen.contentview_size);

        mGalleryFileView = (GridView) findViewById(R.id.cx_fa_gallery_file_view);
        mGalleryFileView.setVisibility(View.VISIBLE);
        mFileAdapter = new GalleryFileAdapter();
        mGalleryFileView.setAdapter(mFileAdapter);
        mGalleryFileView.setOnItemClickListener(CxGalleryActivity.this);

        mTitle = (TextView) findViewById(R.id.cx_fa_activity_title_info);
        mTitle.setText(getString(R.string.cx_fa_select_gallery_text));
        mBackBtn = (Button) findViewById(R.id.cx_fa_activity_title_back);
        mSubmitBtn = (Button) findViewById(R.id.cx_fa_activity_title_more);
        mBackBtn.setText(getString(R.string.cx_fa_back_text));
        String finishText = String.format(getString(R.string.cx_fa_finish_text), "0");
        mSubmitBtn.setText(finishText);
        mSubmitBtn.setVisibility(View.INVISIBLE);
        mBackBtn.setOnClickListener(CxGalleryActivity.this);
        mSubmitBtn.setOnClickListener(CxGalleryActivity.this);

        mFileGridView = (GridView) findViewById(R.id.cx_fa_gallery_image_view);
        mFileGridView.setVisibility(View.GONE);
        mFolderImageAdapter = new ImageSpecialFolder();
        mFileGridView.setAdapter(mFolderImageAdapter);

        new LoadGalleryFiles().execute();

    }

    class GalleryFileAdapter extends BaseAdapter {

        List<Map<String, String>> mData;
        GridView.LayoutParams mLayoutParam;

        public GalleryFileAdapter() {
            mLayoutParam = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, mItemSize);
        }

        public void setSourceData(List<Map<String, String>> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (null == mData) {
                return 0;
            }

            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GalleryFileHolder holder = null;
            if (null == convertView) {
                convertView = CxGalleryActivity.this.getLayoutInflater()
                        .inflate(R.layout.cx_fa_activity_gallery_files_item, null);
                holder = new GalleryFileHolder();
                holder.firstImage = (CxImageView) convertView.findViewById(R.id.folder_first_image);
                holder.folderName = (TextView) convertView.findViewById(R.id.folder_name);
                holder.imageNums = (TextView) convertView.findViewById(R.id.folder_image_number);
                convertView.setTag(holder);
            } else {
                holder = (GalleryFileHolder) convertView.getTag();
            }

            Map<String, String> singleData = mData.get(position);
            CxImageView folderImage = holder.firstImage;
            folderImage.displayImage(imageLoader, "file://" + singleData.get(
                    MediaStore.Images.ImageColumns.DATA), R.drawable.cx_fa_gallery_file_bg, false, 0);
            TextView folderName = holder.folderName;
            folderName.setText(singleData.get(
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
            TextView folderNums = holder.imageNums;
            folderNums.setText(singleData.get("photos"));
            convertView.setLayoutParams(mLayoutParam);
            return convertView;
        }

    }

    static class GalleryFileHolder {
        public CxImageView firstImage;
        public TextView folderName;
        public TextView imageNums;
    }

    class LoadGalleryFiles extends AsyncTask<Object, Integer, Integer> {

        @Override
        protected void onPostExecute(Integer result) {
            mFileAdapter.setSourceData(mGalleryData);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Object... params) {
            mGalleryData = getGalleryData(CxGalleryActivity.this);
            return null;
        }

    }

    private List<Map<String, String>> getGalleryData(Context context) {
        try {
            String[] projection = {
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    "COUNT(*) AS photos "
            };
            String selection = "1) GROUP BY (" + MediaStore.Images.Media.BUCKET_DISPLAY_NAME;

            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(),
                    uri, projection, selection, null, MediaStore.Images.ImageColumns._ID);
            if (null == cursor) {
                return null;
            }
            List<Map<String, String>> targetData = new ArrayList<Map<String, String>>();
            while (cursor.moveToNext()) {
                CxLog.i("111", "" + cursor.getString(cursor.getColumnIndexOrThrow(
                                MediaStore.Images.ImageColumns.DATA))
                        /*+ "|" + c.getString(c.getColumnIndexOrThrow("thumbnail")) */
                                + "|" + cursor.getString(cursor.getColumnIndexOrThrow(
                                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
                                + "|" + cursor.getString(cursor.getColumnIndexOrThrow("photos"))
                );

                String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                CxLog.i("", "root dir=" + rootPath);

                Map<String, String> item = new HashMap<String, String>();
                item.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                        cursor.getString(cursor.getColumnIndexOrThrow(
                                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
                item.put(MediaStore.Images.ImageColumns.DATA,
                        cursor.getString(cursor.getColumnIndexOrThrow(
                                MediaStore.Images.ImageColumns.DATA)));
                item.put("photos", "(" + cursor.getString(cursor.getColumnIndexOrThrow("photos")) + "张)");
                targetData.add(item);
            }
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return targetData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        //arg2是position
        CxLog.i("--------", "arg2=" + arg2 + ",arg3=" + arg3);
        Map<String, String> tempItem = null;
        try {
            tempItem = mGalleryData.get(arg2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == tempItem) {
            return;
        }
        String path = tempItem.get(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
        int index = path.lastIndexOf(File.separator);
        if (-1 != index) {
            path = path.substring(0, index);
        }
        CxLog.i("***************=", path);
        /*Intent selecteImage = new Intent(RkGalleryActivity.this, RkGalleryGridActivity.class);
        selecteImage.putExtra("dir", path);
		startActivity(selecteImage);
		RkGalleryActivity.this.finish();*/

        mGalleryFileView.setVisibility(View.GONE);
        mFileGridView.setVisibility(View.VISIBLE);
        mSubmitBtn.setVisibility(View.INVISIBLE);
        mTargetPath = path;
        isSpecFolder = true;
        mTitle.setText(getString(R.string.cx_fa_select_photo_text));
        new LoadSpeFolderImg().execute();

    }

    class ImageSpecialFolder extends BaseAdapter {

        @Override
        public int getCount() {
            if (null == mImagePath) {
                return 0;
            }
            return mImagePath.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FolderImageHolder holder = null;
            if (null == convertView) {
                convertView = CxGalleryActivity.this.getLayoutInflater()
                        .inflate(R.layout.cx_fa_activity_gallery_image_item, null);
                holder = new FolderImageHolder();
                holder.folderImage = (CxImageView) convertView.findViewById(R.id.image_src);
                holder.selectView = (ImageView) convertView.findViewById(R.id.image_selected);
                convertView.setTag(holder);
            } else {
                holder = (FolderImageHolder) convertView.getTag();
            }

            if (position >= mImagePath.size()) {
                return convertView;
            }


            final ImageView selectView = holder.selectView;
            if (mSelected.contains("" + position)) {
                selectView.setVisibility(View.VISIBLE);
            } else {
                selectView.setVisibility(View.INVISIBLE);
            }


            final String positionStr = "" + position;
            final String positionImag = mImagePath.get(position);
            CxImageView folderImage = holder.folderImage;
            folderImage.setImageResource(R.drawable.chatview_imageloading);
            folderImage.displayImage(imageLoader, mImagePath.get(position),
                    R.drawable.chatview_imageloading, false, 0);
            folderImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mSelected.contains(positionStr)) {
                        try {
                            mSelected.remove(positionStr);
                            mSelectedImage.remove(positionStr);
                            mSelectedNums--;
                            if (mSelectedNums < 0) {
                                mSelectedNums = 0;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        selectView.setVisibility(View.INVISIBLE);
                    } else {
                        if (mSelectedNums < 9) { //没有超过9张是正常的
                            mSelectedNums++;
                            try {
                                mSelected.add(positionStr);
                                mSelectedImage.put(positionStr, positionImag);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            selectView.setVisibility(View.VISIBLE);
                        } else { //超过9张就无反应了
                            //
                        }
                    }

                    if (mSelectedNums > 0) {
                        mSubmitBtn.setVisibility(View.VISIBLE);
                    } else {
                        mSubmitBtn.setVisibility(View.INVISIBLE);
                    }

                    String finishText = String.format(getString(
                            R.string.cx_fa_finish_text), "" + mSelectedNums);
                    mSubmitBtn.setText(finishText);

                }
            });


            return convertView;
        }

    }

    class FolderImageHolder {
        ImageView selectView;
        CxImageView folderImage;
    }

    class LoadSpeFolderImg extends AsyncTask<Object, Integer, Integer> {

        @Override
        protected void onPostExecute(Integer result) {
            mFolderImageAdapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            //
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Object... params) {
//			Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
            String[] projection = {
                    MediaStore.Images.ImageColumns.DATA,
            };
            String selectStr = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "='" + mTargetPath + "'";
            Cursor cursor = MediaStore.Images.Media.query(
                    CxGalleryActivity.this.getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    selectStr, null, MediaStore.Images.ImageColumns._ID);
            if (null == cursor) {
                return null;
            }

            mImagePath = new ArrayList<String>();
            while (cursor.moveToNext()) {
                mImagePath.add("file://" + cursor.getString(cursor.getColumnIndex(
                        MediaStore.Images.ImageColumns.DATA)));
            }
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //暂时不考虑加载大图的问题了，后续是个优化点

            return null;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cx_fa_activity_title_back:
                if (isSpecFolder) { //选图片时
                    mFileGridView.setVisibility(View.GONE);
                    mGalleryFileView.setVisibility(View.VISIBLE);
                    isSpecFolder = false;
                    mSelectedNums = 0;
                    mTitle.setText(getString(R.string.cx_fa_select_gallery_text));
                    String finishText = String.format(getString(R.string.cx_fa_finish_text), "0");
                    mSubmitBtn.setText(finishText);
                    mSubmitBtn.setVisibility(View.INVISIBLE);
                    try {
                        mSelected.clear();
                        mSelectedImage.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else { //选相册时
                    isSpecFolder = false;
                    CxGalleryActivity.this.finish();
                }
                break;

            case R.id.cx_fa_activity_title_more:
                if (isSpecFolder) { //选图时提交选择

                    //取出已经选中的图片路径
                    ArrayList<String> selectedImgs = new ArrayList<String>();
                    try {
                        Iterator<Entry<String, String>> iter = mSelectedImage.entrySet().iterator();
                        while (iter.hasNext()) {
                            selectedImgs.add(iter.next().getValue());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if ((null == selectedImgs) || (selectedImgs.size() < 1)) {
                        Toast.makeText(CxGalleryActivity.this, getString(
                                R.string.cx_fa_no_select_img), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ActivitySelectPhoto.mImgsUri = selectedImgs;
                    Intent intent = getIntent();
                    intent.putStringArrayListExtra("data", selectedImgs);
                    setResult(Activity.RESULT_OK, intent);
                    CxGalleryActivity.this.finish();

                } else { //视为bug
                    mSubmitBtn.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (isSpecFolder) {
                mFileGridView.setVisibility(View.GONE);
                mGalleryFileView.setVisibility(View.VISIBLE);
                isSpecFolder = false;
                mSelectedNums = 0;
                mTitle.setText(getString(R.string.cx_fa_select_gallery_text));
                String finishText = String.format(getString(R.string.cx_fa_finish_text), "0");
                mSubmitBtn.setText(finishText);
                mSubmitBtn.setVisibility(View.INVISIBLE);
                try {
                    mSelected.clear();
                    mSelectedImage.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
