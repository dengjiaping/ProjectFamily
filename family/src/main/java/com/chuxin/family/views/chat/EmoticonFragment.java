package com.chuxin.family.views.chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.parse.been.data.EmotionItem;
import com.chuxin.family.parse.been.data.EmotionSet;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.R;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class EmoticonFragment extends Fragment {

	private int barPosition;
	private int index;	
	private int numColumns;	

	private GridView mFaceGridView1;
	private GridView mFaceGridView2;
	
//	private ArrayList<String>  emoMenus;
	
	private ArrayList<EmotionSet>  emotions;
	
	private ArrayList<String>  imageList;
	private ArrayList<String>  imageNameList;
	
	
	/**
	 * 实例化方法
	 * @param emoId 表情名
	 * @param index 哪一页
	 * @return
	 */
	public static EmoticonFragment newInstance(int barPosition,int index,int numColumns){
		
		
		EmoticonFragment fragment=new EmoticonFragment();
		
		Bundle bundle = new Bundle();//封装到bundle
		bundle.putInt("barPosition", barPosition);
		bundle.putInt("index", index);
		bundle.putInt("numColumns", numColumns);
        fragment.setArguments(bundle);
		return fragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		barPosition=args.getInt("barPosition");
		index=args.getInt("index");	//获取参数
		numColumns=args.getInt("numColumns");	//获取参数
		
		emotions=EmotionParam.getInstance().getEmotions();
		
		imageList=new ArrayList<String>();//发送消息时的参数集合
		imageNameList=new ArrayList<String>();//每页显示的表情的名称集合	
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View inflate ;
		CxLog.i("men", barPosition+"");
		
		if(barPosition==0){//如果是经典表情
			inflate = inflater.inflate(R.layout.cx_fa_widget_faceitem01,container, false);

			mFaceGridView1 = (GridView) inflate.findViewById(R.id.cx_fa_widget_input_panel__layout4_grid1);
			switch(index){
			case 1://第一页
				TypedArray faceImageResIds1 = getResources().obtainTypedArray(
						R.array.cx_fa_ids_input_panel_face_images1);
				String[] faceValues1 = getResources().getStringArray(
						R.array.cx_fa_strs_input_panel_face_values11);
				
				mFaceGridView1.setAdapter(new FaceAdapter(faceImageResIds1, null,faceValues1));
				break;
			case 2://第二页
				TypedArray faceImageResIds2 = getResources().obtainTypedArray(
						R.array.cx_fa_ids_input_panel_face_images2);
				String[] faceValues2 = getResources().getStringArray(
						R.array.cx_fa_strs_input_panel_face_values21);
				mFaceGridView1.setAdapter(new FaceAdapter(faceImageResIds2, null,faceValues2));
				break;
			}
			
			//每个表情的点击事件
			mFaceGridView1.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					CxLog.d("inputpanel", "position=" + position);
					String msg = (String) mFaceGridView1.getAdapter().getItem(
							position);
					CxLog.d("inputpanel", "msg=" + msg);
//					onMessage(msg, 2);
					listener.click(msg);
				}

			});
		
		}else{	
			
			imageList.clear();
			imageNameList.clear();
			
			inflate = inflater.inflate(R.layout.cx_fa_widget_faceitem02,container, false);
			mFaceGridView2 = (GridView) inflate.findViewById(R.id.cx_fa_widget_input_panel__layout4_grid2);
//			System.out.println(">>>>>>>>>>>>>>");
			
//			mFaceGridView2.setColumnWidth(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_column_width));
//			RkLog.i("men", numColumns+"");
			mFaceGridView2.setNumColumns(numColumns);
			try {
				
				if(emotions!=null){
					EmotionSet set = emotions.get(barPosition-1);
					
					EmotionItem emotionItem = set.getItems().get(0);
					CxLog.i("men", emotionItem.getImage());
					
					int perNumber = set.getCountPerPage();
					int categoryId = set.getCategoryId();
					ArrayList<EmotionItem> items = set.getItems();
					EmotionItem item =null;
					for(int i=(index-1)*perNumber;i<index*perNumber;i++){
						if(i<items.size()){
							item = items.get(i);
							if("".equals(item.getTextTip().trim())){//如果表情名称为空则拼上"null"
								imageList.add(item.getImage()+"."+categoryId+"."+item.getImageId()+".null");
							}else{
								imageList.add(item.getImage()+"."+categoryId+"."+item.getImageId()+"."+item.getTextTip());
							}
//							RkLog.i("men", item.getImageName());
							imageNameList.add(item.getImageName());
						}
						item=null;
					}
					
				}
				
				mFaceGridView2.setAdapter(new FaceOtherAdapter(imageNameList, null,imageList));
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			//每个表情的点击事件
			mFaceGridView2.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					CxLog.d("inputpanel", "position=" + position);
					String msg = (String) mFaceGridView2.getAdapter().getItem(
							position);
					CxLog.d("inputpanel", "msg=" + msg);
//					onMessage(msg, 2);
					listener.click(msg);
				}

			});
			
		}
		
		return inflate;
	}
	
	class FaceOtherAdapter extends BaseAdapter {

		private int mCount = 0;
		private ArrayList<String> mImages = null;
		private String[] mTexts = null;
		private ArrayList<String> mValues = null;

		public FaceOtherAdapter(ArrayList images, String[] text, ArrayList<String> values) {
			super();

			mCount = images.size(); 

			if (text != null)
				assert (mCount == text.length);

			assert (mCount == values.size());

			mImages = images;
			mTexts = text;
			mValues = values;
		}  

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public Object getItem(int position) {
			return mValues.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.cx_fa_widget_face_emotion_cell2, null);
			
			} else {
				view = convertView;
			}
			if (mImages != null) {
				ImageView image = (ImageView) view.findViewById(R.id.cx_fa_widget_face_cell__image2);
				
//				LayoutParams lp=new LayoutParams(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_image_w), 
//						getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_image_h));
//				
//				image.setLayoutParams(lp);
				
				File tempPath=getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//				File tempFile=new File(tempPath,RkGlobalConst.S_CHAT_EMOTION+File.separator+mImages.get(position)+"@2x.png");
				
				String path=tempPath.getAbsolutePath()+File.separator+CxGlobalConst.S_CHAT_EMOTION+File.separator+mImages.get(position)+"@2x.png";
//				Uri uri=Uri.parse(path);

				Bitmap bitmap;
				try {
					bitmap = BitmapFactory.decodeStream(new FileInputStream(path));
					image.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			return view;
		}

	}
	
	
	
	class FaceAdapter extends BaseAdapter {

		private int mCount = 0;
		private TypedArray mImages = null;
		private String[] mTexts = null;
		private String[] mValues = null;

		public FaceAdapter(TypedArray images, String[] text, String[] values) {
			super();

			mCount = images.length(); 
			if (text != null)
				assert (mCount == text.length);
			assert (mCount == values.length);

			mImages = images;
			mTexts = text;
			mValues = values;
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public Object getItem(int position) {
			return mValues[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.cx_fa_widget_face_emotion_cell1, null);
			} else {
				view = convertView;
			}

			if (mImages != null) {
				ImageView image = (ImageView) view.findViewById(R.id.cx_fa_widget_face_cell__image1);
				int imageResId = mImages.getResourceId(position, 0);
				image.setImageResource(imageResId);
			}

			return view;
		}

	}
	
	private GridViewClickListener listener;
	
	public void setGridViewClickListener(GridViewClickListener listener ){
		this.listener=listener;
	}
	
	public interface GridViewClickListener{
		void click(String msg);
	}

	
}
















