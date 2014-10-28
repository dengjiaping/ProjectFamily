package com.chuxin.family.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.net.ConnectionManager;
import com.chuxin.family.net.HttpApi;
import com.chuxin.family.parse.been.data.ChatBgData;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.ScreenUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

public class CxChatBgAdapter extends BaseAdapter {
	private Context mCtx;
	private String mCustomBackground; //第一张图片路径
	private int mSelectedItemNumber; //以前选中的聊天背景
	private ArrayList<String> mDefaultChatBgRes;
	private ArrayList<String> mChatBgUpperName;
	
	private char[] mBgDownloads;
	private ProgressBar tempProgress;
	private TextView tempText;
	
	private Map<Integer,ProgressBar> progressbarMap;
	private Map<Integer,TextView> textviewMap;
	
	private ChatBgData configData;
	
//	private int pos;
//	private ProgressBar tempProgress;
//	private RkImageView tempImageView;
//	private ImageView tempImageViewSelected;
//	private TextView tempText;
	
	public CxChatBgAdapter(Context ctx, String  customBackground, 
			int selectItemNumber, ArrayList<String> defaultChatBgRes,ArrayList<String> mChatBgUpperName){
		this.mCtx = ctx;
		this.mCustomBackground = customBackground;
		this.mSelectedItemNumber = selectItemNumber;
		this.mDefaultChatBgRes = defaultChatBgRes;
		this.mChatBgUpperName=mChatBgUpperName;
		
		progressbarMap=new HashMap<Integer, ProgressBar>();
		textviewMap=new HashMap<Integer, TextView>();
		configData=CxGlobalParams.getInstance().getChatbgData();
	}
	
	public void setClickItem(final int itemNumber,String customBg){
		if (itemNumber!=0 && mSelectedItemNumber == itemNumber) {
			return;
		}

		
		tempText=textviewMap.get(itemNumber);
		tempProgress=progressbarMap.get(itemNumber);
		
//		System.out.println(itemNumber);
		
		
		if(itemNumber>=2){			
			if(tempProgress.getVisibility()==View.VISIBLE){
				new AsyncTask<Void, Void, Void>(){
					
					private DefaultHttpClient mClient;
					private String fileName;
					private boolean completed=true;					
					private int downCount=0;					
					private String url;					
					private File file;
		
					@Override
					protected void onPreExecute() {				
						super.onPreExecute();	
						tempText.setVisibility(View.INVISIBLE);
						HttpApi mApi = ConnectionManager.getHttpApi();
						mClient = mApi.getmHttpClient();//获取httpclient	
					}
					@Override
					protected Void doInBackground(Void... params) {
						int type = ScreenUtil.getScreenType(mCtx);
						if(1==type){
							url=configData.getResourceUrl()+File.separator+"a_"+mChatBgUpperName.get(itemNumber-2)+"_xhd.jpg";
						}else{
							url=configData.getResourceUrl()+File.separator+"a_"+mChatBgUpperName.get(itemNumber-2)+"_md.jpg";
						}
						
//						System.out.println(url);
						HttpGet getChatBg=new HttpGet(url);
						try {
							HttpResponse response = mClient.execute(getChatBg);
							
							if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){	
								HttpEntity entity = response.getEntity();									
								InputStream in=null;							
								InputStream responseStream = null;
								try {
									long length = entity.getContentLength();
									tempProgress.setMax((int) length);
									responseStream = entity.getContent();
								} catch (Exception e1) {
								}
								if (responseStream != null) {
									in=responseStream;
								}
								Header header = entity.getContentEncoding();
								if (header != null) {
									String contentEncoding = header.getValue(); 
									if (contentEncoding != null) {
										if (contentEncoding.contains("gzip")) {
											in = new GZIPInputStream(responseStream);
										}
									}
								}						        
								try{									
									String folderName = "chuxin"+File.separator+"chatbg"+File.separator+"chatbgimage";
							        fileName = mChatBgUpperName.get(itemNumber-2).toLowerCase()+".jpg";		      
							        File path =mCtx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
							        file = new File(path, folderName + File.separator + fileName);
						        
							        if(!file.getParentFile().exists())
							        	file.getParentFile().mkdirs();						
							        FileOutputStream out=new FileOutputStream(file);
									
									int len1 = 0;
									int total=0;
									byte[] buffer = new byte[1024];
									while ((len1 = in.read(buffer)) != -1) {
										out.write(buffer, 0, len1);
										total+=len1;
										tempProgress.setProgress(total);
									}	
								}catch(Exception e){
									e.printStackTrace();
									if(downCount<3){
										downCount++;
										doInBackground();
									}else{
										completed=false;
										if(file.exists())
											file.delete();
									}
								}
							}
							
						} catch (Exception e) {
							e.printStackTrace();
							completed=false;
						}	
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {						
						super.onPostExecute(result);
						if(completed){
							tempProgress.setVisibility(View.INVISIBLE);
							tempProgress.setProgress(0);
							mSelectedItemNumber=itemNumber;
							CxChatBgAdapter.this.notifyDataSetChanged();
							listener.sendMessage(itemNumber);
						}else{
							tempText.setVisibility(View.VISIBLE);
							ToastUtil.getSimpleToast(mCtx, R.drawable.chatbg_update_error, "更新聊天背景失败\n检测一下网络吧",0).show();
						}
					}
					
				}.execute();
			}else{
				mSelectedItemNumber=itemNumber;
				CxChatBgAdapter.this.notifyDataSetChanged();
				listener.sendMessage(itemNumber);
			}
		}else if(1==itemNumber){		
			mSelectedItemNumber=itemNumber;		
			this.notifyDataSetChanged();	
			listener.sendMessage(itemNumber);
		}else if(0==itemNumber){
			if(customBg!=null){
				mCustomBackground=customBg;
			}
			mSelectedItemNumber=itemNumber;		
			this.notifyDataSetChanged();
		}
			
	}

	@Override
	public int getCount() {		
		
		
		return mDefaultChatBgRes.size()+2; 
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
		// 
		final int pos=position;

		ViewHolder holder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(mCtx).inflate(R.layout.cx_fa_activity_chatbg_select_adapter_item, null);
			holder = new ViewHolder();
			holder.itemSelectedView = (ImageView)convertView.findViewById(R.id.chat_bg_item_selected);
			holder.itemView = (CxImageView)convertView.findViewById(R.id.chat_bg_item_view);
			holder.itemProgress=(ProgressBar) convertView.findViewById(R.id.chat_bg_item_download_progressbar);
			holder.itemText=(TextView) convertView.findViewById(R.id.chat_bg_item_download_tv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		CxImageView tempImageView = holder.itemView;
		ImageView tempImageViewSelected = holder.itemSelectedView;
		final ProgressBar tempProgress = holder.itemProgress;
		final TextView tempText = holder.itemText;
		
		progressbarMap.put(pos, tempProgress);
		textviewMap.put(pos, tempText);

		int widthPixels = mCtx.getResources().getDisplayMetrics().widthPixels;
		int row_w = widthPixels- ScreenUtil.dip2px(mCtx, 15*2+10*2); // 三个图像减去父对象margin及padding后，可用的宽度
		int w = (Integer) (row_w / 3); // 自动算图的宽度(有一个风险，如果屏幕的宽大于高，这个就会特别难看)
		int h = w;
		
		FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(w, h);
		FrameLayout.LayoutParams lp2=new FrameLayout.LayoutParams(w, (int)(h/4+0.5));
		
		
		
		tempImageView.setLayoutParams(lp);
		tempImageViewSelected.setLayoutParams(lp);
		
		
		lp2.setMargins(0, (int)(h-(int)(h/4+0.5)+0.5), 0, 0);
		tempProgress.setLayoutParams(lp2);
		
		tempText.setTextSize(ScreenUtil.px2dip(mCtx, (int)(w/8+0.5)));

		switch (position) {
		case 0: //当曾经选中第一项，且那张自定义背景在本地存在			
			if(mSelectedItemNumber==0){
				tempImageViewSelected.setVisibility(View.VISIBLE);				
			}
			if (null == mCustomBackground) {
				tempImageView.setImageResource(R.drawable.chatbg_plus);
			}else{
				if(!mCustomBackground.contains("http")){
					try {
						Drawable drawable = Drawable.createFromPath(mCustomBackground);
						tempImageView.setImageDrawable(drawable);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					tempImageView.displayImage(ImageLoader.getInstance(), mCustomBackground, 
							R.drawable.chatbg_plus, true, CxGlobalParams.getInstance().getSmallImgConner());	
					
				}
				
					
			}
			break;
		case 1:	
			if(mSelectedItemNumber==1){
				tempImageViewSelected.setVisibility(View.VISIBLE);
			}
			tempImageView.setImageResource(CxResourceDarwable.getInstance().dr_chat_chatbg_thumbnail_default);
			break;
		default:		
			
			
			File file=getFile(mChatBgUpperName.get(pos-2).toLowerCase());				
			if(mSelectedItemNumber==pos){
				tempImageViewSelected.setVisibility(View.VISIBLE);
				tempProgress.setVisibility(View.GONE);
				tempText.setVisibility(View.GONE);
			}else{
				tempImageViewSelected.setVisibility(View.GONE);
				if(file.exists()){
					tempProgress.setVisibility(View.GONE);
					tempText.setVisibility(View.GONE);
				}else{
					tempProgress.setVisibility(View.VISIBLE);
					tempText.setVisibility(View.VISIBLE);
				}
			}
				
			String chatbgRes = mDefaultChatBgRes.get(position-2);
			if(chatbgRes.contains(configData.getResourceUrl())){
				tempImageView.displayImage(ImageLoader.getInstance(), chatbgRes, 
						R.drawable.chatview_imageloading, false, 0);
				
			}else{
				tempImageView.setImageResource(Integer.parseInt(chatbgRes));
			}
			break;
		}
		
		return convertView;
	}
	
	static class ViewHolder{
		public CxImageView itemView;
		public ImageView itemSelectedView;
		public ProgressBar itemProgress;
		public TextView itemText;
	}

	
	private File getFile(String name){
		String folderName = "chuxin"+File.separator+"chatbg"+File.separator+"chatbgimage";
        String fileName = name+".jpg";		      
        File path =mCtx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, folderName + File.separator + fileName);
        return file;
	}
	
	
	
	private DownloadChatbgFinishListener listener;
	
	public interface DownloadChatbgFinishListener{
		void sendMessage(int position);
	}
	
	public void setDownloadChatbgFinishListener(DownloadChatbgFinishListener listener){
		this.listener=listener;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
