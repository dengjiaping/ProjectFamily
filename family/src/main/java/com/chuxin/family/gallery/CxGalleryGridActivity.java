package com.chuxin.family.gallery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.neighbour.CxNeighbourAddInvitation;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.zone.CxZoneAddFeed;
import com.chuxin.family.R;
/**
 * 系统相册点选多图界面：此界面只负责点选多图，这个界面返回到ActivitySelectPhoto界面
 * @author shichao.wang
 *
 */
public class CxGalleryGridActivity extends CxRootActivity 
implements OnItemClickListener, OnClickListener{
	
	GridView mFileGridView;
	ImageSpecialFolder mFolderImageAdapter;
	List<String> mImagePath;
	String mTargetPath;
	ArrayList<String> mSelected = new ArrayList<String>();
	Button mBackBtn, mSubmitBtn;
	HashMap<String, String> mSelectedImage = new HashMap<String, String>();
	int mSelectedNums = 0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_activity_gallery_images);
		
		mFileGridView = (GridView)findViewById(R.id.cx_fa_gallery_image_view);
		
		mBackBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
		mSubmitBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
		mBackBtn.setText(getString(R.string.cx_fa_back_text));
		mSubmitBtn.setText(getString(R.string.cx_fa_finish_text));
		mSubmitBtn.setVisibility(View.VISIBLE);
		mBackBtn.setOnClickListener(CxGalleryGridActivity.this);
		mSubmitBtn.setOnClickListener(CxGalleryGridActivity.this);
		
		mTargetPath = getIntent().getStringExtra("dir");
		if (TextUtils.isEmpty(mTargetPath)) {
			CxGalleryGridActivity.this.finish();
			return;
		}
		
//		mTargetPath = "file://"+ mTargetPath;
		
		mFolderImageAdapter = new ImageSpecialFolder();
		mFileGridView.setAdapter(mFolderImageAdapter);
		
		new LoadSpeFolderImg().execute();
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}
	
	class LoadSpeFolderImg extends AsyncTask<Object, Integer, Integer>{

		@Override
		protected void onPostExecute(Integer result) {
			mFolderImageAdapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Object... params) {
//			Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
			String[] projection = {  
        	        MediaStore.Images.ImageColumns.DATA,  
        	};
			String selectStr = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "='"+mTargetPath+"'";
        	Cursor cursor = MediaStore.Images.Media.query(
        			CxGalleryGridActivity.this.getContentResolver(), 
        			MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, 
        			selectStr, null, MediaStore.Images.ImageColumns._ID);  
        	if (null == cursor) {
				return null;
			}
        	
        	mImagePath = new ArrayList<String>();
        	while(cursor.moveToNext()) { 
        		mImagePath.add("file://"+cursor.getString(cursor.getColumnIndex(
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
	
	class ImageSpecialFolder extends BaseAdapter{

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
				convertView = CxGalleryGridActivity.this.getLayoutInflater()
				.inflate(R.layout.cx_fa_activity_gallery_image_item, null);
				holder = new FolderImageHolder();
				holder.folderImage = (CxImageView)convertView.findViewById(R.id.image_src);
				holder.selectView = (ImageView)convertView.findViewById(R.id.image_selected);
				convertView.setTag(holder);
			}else{
				holder = (FolderImageHolder)convertView.getTag();
			}
			
			final ImageView selectView = holder.selectView;
			if (mSelected.contains(""+position)) {
				selectView.setVisibility(View.VISIBLE);
			}else{
				selectView.setVisibility(View.INVISIBLE);
			}
			
			final String positionStr = ""+position;
			final String positionImag = mImagePath.get(position);
			CxImageView folderImage = holder.folderImage;
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
					}else{
						if (mSelectedNums < 9) { //没有超过9张是正常的
							mSelectedNums++;
							try {
								mSelected.add(positionStr);
								mSelectedImage.put(positionStr, positionImag);
							} catch (Exception e) {
								e.printStackTrace();
							}
							selectView.setVisibility(View.VISIBLE);
						}else{ //超过9张就无反应了
							//
						}
					}
				}
			});
			
			
			return convertView;
		}
		
	}
	
	class FolderImageHolder{
		ImageView selectView;
		CxImageView folderImage;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cx_fa_activity_title_back:
			Intent backIntent = new Intent(CxGalleryGridActivity.this, CxGalleryActivity.class);
			startActivity(backIntent);
			CxGalleryGridActivity.this.finish();
			break;
			
		case R.id.cx_fa_activity_title_more:
			//
			if ((null == mSelected) || (mSelected.size() < 1)){
				ToastUtil.getSimpleToast(CxGalleryGridActivity.this, -1, 
						getString(R.string.cx_fa_no_image), Toast.LENGTH_LONG);
				return;
			}
//			ActivitySelectPhoto.multImages = mSelected;
			//直接跳到目标界面
			if (TextUtils.isEmpty(ActivitySelectPhoto.kFrom)) { //程序出问题
				CxLog.i("gallery ", " has no target to call");
				CxGalleryGridActivity.this.finish();
				return;
			}
			
			//需要分添加feed界面与非添加feed界面调用，如果是添加feed界面就以forresult
//			RkGalleryGridActivity.this.setResult(resultCode, data)
			
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
			String kFrom = ActivitySelectPhoto.kFrom;
			if (kFrom.equals("RkNeighbourFrament") || kFrom.equals("RkNbOurHome")) {
				Intent addInvitation = new Intent(CxGalleryGridActivity.this,
						CxNeighbourAddInvitation.class);
    			addInvitation.putExtra(CxGlobalConst.S_NEIGHBOUR_SHARED_TYPE, 1);
    			addInvitation.putStringArrayListExtra(CxGlobalConst.S_NEIGHBOUR_SHARED_IMAGE, selectedImgs);
                startActivity(addInvitation);
                overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
                ActivitySelectPhoto.kFrom="";
                CxGalleryGridActivity.this.finish();
                return;
			}
			
			if(kFrom.equals("RkUserPairZone")){
				Intent toAddFeed = new Intent(CxGalleryGridActivity.this, CxZoneAddFeed.class);
        		toAddFeed.putExtra(CxGlobalConst.S_ZONE_SHARED_TYPE, 1);
        		toAddFeed.putStringArrayListExtra(CxGlobalConst.S_ZONE_SHARED_IMAGE, selectedImgs);
        		startActivity(toAddFeed);
        		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
        		ActivitySelectPhoto.kFrom="";
        		CxGalleryGridActivity.this.finish();
        		return;
			}
			
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*if (KeyEvent.KEYCODE_BACK == keyCode) {
			Intent backIntent = new Intent(RkGalleryGridActivity.this, CxGalleryActivity.class);
			startActivity(backIntent);
			return true;
		}*/
		return super.onKeyDown(keyCode, event);
	}
	
}
