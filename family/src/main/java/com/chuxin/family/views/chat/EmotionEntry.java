
package com.chuxin.family.views.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ant.liao.chuxin.EnhancedGifView;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.models.EmotionMessage;
import com.chuxin.family.models.FaceMessage;
import com.chuxin.family.models.Message;
import com.chuxin.family.models.PictureMessage;
import com.chuxin.family.models.TextMessage;
import com.chuxin.family.net.ConnectionManager;
import com.chuxin.family.net.HttpApi;
import com.chuxin.family.parse.been.CxEmotionConfigList;
import com.chuxin.family.parse.been.data.EmotionSet;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxZipUtil;
import com.chuxin.family.utils.ScreenUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

public class EmotionEntry extends ChatLogEntry {
    private static final String TAG = "EmotionEntry";

    private static final int VIEW_RES_ID = R.layout.cx_fa_view_chat_chatting_face_row;

    private static final int ICON_ID = R.id.cx_fa_view_chat_chatting_face_row__icon;

    private static final int CONTENT_ID = R.id.cx_fa_view_chat_chatting_face_row__content;
    
    private static final int CONTENT2_ID = R.id.cx_fa_view_chat_chatting_face_row__content2;
    
    private static final int TIMESTAMP_ID = R.id.cx_fa_view_chat_chatting_face_row__timestamp;

    private static final int DATESTAMP_ID = R.id.cx_fa_view_chat_chatting_face_row__datestamp;

    private static final int SEND_SUCCESS_BUTTON_ID = R.id.cx_fa_view_chat_chatting_face_row__exclamation;

    private static final boolean OWNER_FLAG = true;

    private static String[] sFaceValues = null;

    private static String[] sFaceTexts = null;

	private EmoticonDao dao;

	private ArrayList<EmotionSet> emotions;

    public static String[] getFaceValues(Resources res) {
        if (sFaceValues == null) {
            sFaceValues = res.getStringArray(R.array.face_ids);
        }
        return sFaceValues;
    }

    public static String[] getFaceTexts(Resources res) {
        if (sFaceTexts == null) {
            sFaceTexts = res.getStringArray(R.array.face_texts);
        }
        return sFaceTexts;
    }

    public EmotionEntry(Message message, Context context, boolean isShowDate) {
        super(message, context, isShowDate);
        
        emotions = EmotionParam.getInstance().getEmotions();
    	if(emotions==null){
    		EmotionCacheData cacheData=new EmotionCacheData(mContext);
    		CxEmotionConfigList data = cacheData.queryCacheData(CxGlobalParams.getInstance().getUserId());
    		if(data!=null){
    			emotions=data.getList().getDatas();
    		}	
    	}
        
        
    }

    @Override
    public int getType() {
        return ENTRY_TYPE_EMOTION;
    }

    @Override
    public boolean isOwner() {
        return OWNER_FLAG;
    }

    public int getViewResourceId() {
        return VIEW_RES_ID;
    }

    public int getIconId() {
        return ICON_ID;
    }

    public int getContentId() {
        return CONTENT_ID;
    }
    
    public int getContent2Id() {
        return CONTENT2_ID;
    }

    public int getTimeStampId() {
        return TIMESTAMP_ID;
    }

    public int getDateStampId() {
        return DATESTAMP_ID;
    }

    public int getSendSuccessButtonId() {
        return SEND_SUCCESS_BUTTON_ID;
    }

    /**
     * 经典表情显示
     * @param view
     * @param parent
     * @return
     */
    private View buildFromTextMessage(View view, ViewGroup parent) {
        TextMessage message = (TextMessage)mMessage;

        ChatLogAdapter tag = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(getViewResourceId(), null);
        }

        int msgSendState = message.getSendSuccess();
        int msgType = message.getType();
        final int msgId = message.getMsgId();
        final String msgFace = message.getText();
        int msgCreatTimeStamp = message.getCreateTimestamp();

        if (tag == null) {
            tag = new ChatLogAdapter();
        }

        tag.mChatType = msgType;
        tag.mMsgId = msgId;
        // tag.mMsgText = msgFace;

        CxImageView icon = (CxImageView)view.findViewById(getIconId());
        CxImageView face2 = (CxImageView)view.findViewById(getContent2Id());
        // ImageView face = (ImageView)view.findViewById(getContentId());
        EnhancedGifView face = (EnhancedGifView)view.findViewById(getContentId());
        TextView timeStamp = (TextView)view.findViewById(getTimeStampId());
        TextView dateStamp = (TextView)view.findViewById(getDateStampId());
        
        face2.setVisibility(View.GONE);
        face.setVisibility(View.VISIBLE);
        
        int emoWidth_dp = ScreenUtil.dip2px(mContext, 55.0f);
        int emoheight_dp = ScreenUtil.dip2px(mContext, 52.0f);

        if (isOwner()) {
            /*icon.setImageResource(R.drawable.cx_fa_wf_icon_small);
            icon.setImage(RkGlobalParams.getInstance().getIconBig(), false, 44, mContext, "head",
                    mContext);*/
        	icon.displayImage(ImageLoader.getInstance(), 
            		CxGlobalParams.getInstance().getIconSmall(), 
            		CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
            		CxGlobalParams.getInstance().getSmallImgConner());
        	
            icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO update owner headimage
                    CxLog.i(TAG, "click update owner headimage button");
                    ChatFragment.getInstance().updateHeadImage();
                }
            });
            final ImageButton reSendBtn = (ImageButton)view.findViewById(getSendSuccessButtonId());
            ProgressBar pb = (ProgressBar)view
                    .findViewById(R.id.cx_fa_view_chat_chatting_face_row_circleProgressBar);
            if (msgSendState == 0) {
                reSendBtn.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
            }
            if (msgSendState == 1) {
                reSendBtn.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.INVISIBLE);
            }
            if (msgSendState == 2) {
                pb.setVisibility(View.GONE);
                reSendBtn.setVisibility(View.VISIBLE);
                reSendBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // resend message
                        reSendBtn.setVisibility(View.INVISIBLE);
                        ChatFragment.getInstance().reSendMessage(msgFace, 0, msgId);
                    }
                });
            }
        } else {
            /*icon.setImageResource(R.drawable.cx_fa_hb_icon_small);
            icon.setImage(RkMateParams.getInstance().getMateIcon(), false, 44, mContext, "head",
                    mContext);*/
        	icon.displayImage(ImageLoader.getInstance(), 
        			CxMateParams.getInstance().getMateIcon(), 
        			CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
            		CxGlobalParams.getInstance().getMateSmallImgConner());
        	
            icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO 跳转到个人资料页
                    ChatFragment.getInstance().gotoOtherFragment("rkmate");
                }
            });
        }

        String[] faceValues = getFaceValues(view.getResources());
        String[] faceTexts = getFaceTexts(view.getResources());
        TypedArray faceImageIds = view.getResources().obtainTypedArray(R.array.face_images);

        CxLog.d(TAG, "messge face id=" + msgFace);
        for (int i = 0; i < faceTexts.length; i++) {
            if (faceTexts[i].equals(msgFace)) {
                CxLog.d(TAG, "face id=" + i);
                face.setLayoutParams(new LayoutParams(emoWidth_dp,emoheight_dp));
                face.setGifImage(faceImageIds.getResourceId(i, 0));
                tag.mMsgText = faceTexts[i];
                break;
            }
        }
        faceImageIds.recycle();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String dateNow = dateFormat.format(new Date((long)(msgCreatTimeStamp) * 1000));

        if (mIsShowDate) {
            dateStamp.setVisibility(View.VISIBLE);
            dateStamp.setText(dateNow);
        } else {
            dateStamp.setVisibility(View.GONE);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date((long)(msgCreatTimeStamp) * 1000));
        String format = view.getResources().getString(
                R.string.cx_fa_nls_reminder_period_time_format);
        String time = String.format(format, calendar);
        timeStamp.setText(time);
        view.setTag(tag);
        return view;
    }

    /**
     * 非经典表情发送显示
     */
    @Override
    public View build(View view, ViewGroup parent) {
    	
        if (mMessage.getType() == Message.MESSAGE_TYPE_TEXT)
            return buildFromTextMessage(view, parent);
        
        if (mMessage.getType() == Message.MESSAGE_TYPE_PICTURE)
        	return buildFromPictureMessage(view, parent);

        ChatLogAdapter tag = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(getViewResourceId(), null);
        }
        
        EmotionMessage message = (EmotionMessage)mMessage;

        int msgSendState = message.getSendSuccess();
        int msgType = message.getType();
        final int msgId = message.getMsgId();
        final String msgFace = message.getEmotion();
        final int categoryId = message.getCategoryId();
        final int imageId = message.getImageId();
        int msgCreatTimeStamp = message.getCreateTimestamp();

        if (tag == null) {
            tag = new ChatLogAdapter();
        }
        tag.mChatType = msgType;
        tag.mMsgId = msgId;
        // tag.mMsgText = msgFace;

        CxImageView icon = (CxImageView)view.findViewById(getIconId());
        CxImageView face2 = (CxImageView)view.findViewById(getContent2Id());
        // ImageView face = (ImageView)view.findViewById(getContentId());
        EnhancedGifView face = (EnhancedGifView)view.findViewById(getContentId());
        TextView timeStamp = (TextView)view.findViewById(getTimeStampId());
        TextView dateStamp = (TextView)view.findViewById(getDateStampId());
        
        
//        System.out.println(emoWidth_dp); 

        if (isOwner()) {
            /*icon.setImageResource(R.drawable.cx_fa_wf_icon_small);
            icon.setImage(RkGlobalParams.getInstance().getIconSmall(), false, 44, mContext, "head",
                    mContext);*/
        	icon.displayImage(ImageLoader.getInstance(), 
            		CxGlobalParams.getInstance().getIconSmall(), 
            		CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
            		CxGlobalParams.getInstance().getSmallImgConner());
        	
            icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                   
                    CxLog.i(TAG, "click update owner headimage button");
                    ChatFragment.getInstance().updateHeadImage();
                }
            });
            ImageButton reSendBtn = (ImageButton)view.findViewById(getSendSuccessButtonId());
            ProgressBar pb = (ProgressBar)view
                    .findViewById(R.id.cx_fa_view_chat_chatting_face_row_circleProgressBar);
            if (msgSendState == 0) {
                reSendBtn.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
            }
            if (msgSendState == 1) {
                reSendBtn.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.INVISIBLE);
            }
            if (msgSendState == 2) {
                pb.setVisibility(View.GONE);
                reSendBtn.setVisibility(View.VISIBLE);
                reSendBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // resend message
                    	String[] split = msgFace.split("\\.");
                    	String newMsg=split[0]+"."+categoryId+"."+imageId+"."+split[1];
                        ChatFragment.getInstance().reSendMessage(newMsg, 11, msgId);
                    }
                });
            }
        } else {
            /*icon.setImageResource(R.drawable.cx_fa_hb_icon_small);
            icon.setImage(RkMateParams.getInstance().getMateIcon(), false, 44, mContext, "head",
                    mContext);*/
        	icon.displayImage(ImageLoader.getInstance(), 
        			CxMateParams.getInstance().getMateIcon(), 
        			CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
            		CxGlobalParams.getInstance().getMateSmallImgConner());
        	
            icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO 跳转到个人资料页
                    ChatFragment.getInstance().gotoOtherFragment("rkmate");
                }
            });
        }

        
        String path=null;
        
        File tempPath=mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        
        String[] split = msgFace.split("\\.");
        
//        RkLog.i("men", msgFace);
        
        path=tempPath.getAbsolutePath()+File.separator+CxGlobalConst.S_CHAT_EMOTION+File.separator+split[0]+"@2x";
        tag.mMsgText = split[1];
        
        EmotionSet set=null;
        String type="png";
        if(emotions!=null){
    		for(int i=0;i<emotions.size();i++){
    			set = emotions.get(i);
    			if(categoryId==set.getCategoryId()){
    				type = set.getItems().get(0).getType();
    				break;
    			}
    			set=null;
    		}
    	}

        if("gif".equals(type)){
        	face.setVisibility(View.VISIBLE);
        	face2.setVisibility(View.GONE);
//        	face.setGifDecoderImage(Uri.parse("file:///storage/sdcard0/chuxin/face_huaxin.gif"));
        	face.setLayoutParams(getLP(set));
//        	face.setMinimumWidth(minWidth)
//        	RkLog.i("men", "file:///"+path+".gif");
        	face.setGifDecoderImage(Uri.parse("file:///"+path+".gif"));
        	
        }else{
        	face.setVisibility(View.GONE);
        	face2.setVisibility(View.VISIBLE);
        	face2.setLayoutParams(getLP(set));
        	Bitmap bitmap;
			try {
				bitmap = BitmapFactory.decodeStream(new FileInputStream(path+".png"));
				face2.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String dateNow = dateFormat.format(new Date((long)(msgCreatTimeStamp) * 1000));

        if (mIsShowDate) {
            dateStamp.setVisibility(View.VISIBLE);
            dateStamp.setText(dateNow);
        } else {
            dateStamp.setVisibility(View.GONE);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date((long)(msgCreatTimeStamp) * 1000));
        String format = view.getResources().getString(
                R.string.cx_fa_nls_reminder_period_time_format);
        String time = String.format(format, calendar);
        timeStamp.setText(time);
        view.setTag(tag);
        return view;
    }
    
    
    
    /**
     * 非经典表情接收显示
     * @param view
     * @param parent
     * @return
     */
    private View buildFromPictureMessage(View view, ViewGroup parent) {
		
    	PictureMessage message = (PictureMessage)mMessage;
    	
    	ChatLogAdapter tag = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(getViewResourceId(), null);
        }


        int msgSendState = message.getSendSuccess();
        int msgType = message.getType();
        final int msgId = message.getMsgId();
        int categoryId = message.getCategoryId();
        int imageId = message.getImageId();
        int msgCreatTimeStamp = message.getCreateTimestamp();
        String imageName = message.getImageName();
        String imageType = message.getImageType();
        String url = message.getThumbnailUrl();

        if (tag == null) {
            tag = new ChatLogAdapter();
        }
        tag.mChatType = msgType;
        tag.mMsgId = msgId;
        tag.isPicture=false;
        // tag.mMsgText = msgFace;

        CxImageView icon = (CxImageView)view.findViewById(getIconId());
        CxImageView face2 = (CxImageView)view.findViewById(getContent2Id());
        // ImageView face = (ImageView)view.findViewById(getContentId());
        EnhancedGifView face = (EnhancedGifView)view.findViewById(getContentId());
        TextView timeStamp = (TextView)view.findViewById(getTimeStampId());
        TextView dateStamp = (TextView)view.findViewById(getDateStampId());
   
        
        if (isOwner()) {
            /*icon.setImageResource(R.drawable.cx_fa_wf_icon_small);
            icon.setImage(RkGlobalParams.getInstance().getIconSmall(), false, 44, mContext, "head",
                    mContext);*/
        	icon.displayImage(ImageLoader.getInstance(), 
            		CxGlobalParams.getInstance().getIconSmall(), 
            		CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
            		CxGlobalParams.getInstance().getSmallImgConner());
        	
            icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO update owner headimage
                    CxLog.i(TAG, "click update owner headimage button");
                    ChatFragment.getInstance().updateHeadImage();
                }
            });
            ImageButton reSendBtn = (ImageButton)view.findViewById(getSendSuccessButtonId());
            ProgressBar pb = (ProgressBar)view
                    .findViewById(R.id.cx_fa_view_chat_chatting_face_row_circleProgressBar);
            if (msgSendState == 0) {
                reSendBtn.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.VISIBLE);
            }
            if (msgSendState == 1) {
                reSendBtn.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.INVISIBLE);
            }
            if (msgSendState == 2) {
                pb.setVisibility(View.INVISIBLE);
                reSendBtn.setVisibility(View.VISIBLE);
//                reSendBtn.setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // resend message
//                        ChatFragment.getInstance().reSendMessage(msgFace, 2, msgId);
//                    }
//                });
            }
        } else {
            /*icon.setImageResource(R.drawable.cx_fa_hb_icon_small);
            icon.setImage(RkMateParams.getInstance().getMateIcon(), false, 44, mContext, "head",
                    mContext);*/
        	icon.displayImage(ImageLoader.getInstance(), 
        			CxMateParams.getInstance().getMateIcon(), 
        			CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
            		CxGlobalParams.getInstance().getMateSmallImgConner());
        	
            icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO 跳转到个人资料页
                    ChatFragment.getInstance().gotoOtherFragment("rkmate");
                }
            });
        }

        
        String path=null;
        
        File tempPath=mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        
        path=tempPath.getAbsolutePath()+File.separator+CxGlobalConst.S_CHAT_EMOTION+File.separator+imageName+"@2x";
        
        File imageFile=new File(path+"."+imageType);
        
        EmotionSet set=null;
        if(emotions!=null){
    		for(int i=0;i<emotions.size();i++){
    			set = emotions.get(i);
    			if(categoryId==set.getCategoryId()){
    				break;
    			}
    			set=null;
    		}
    	}
        
        if(!imageFile.exists()){
        	String[] split = url.split("/");
	        String fileName = split[split.length-1];
	        File imageFile2=new File(tempPath,CxGlobalConst.S_CHAT_EMOTION+File.separator+fileName);
	        if(!imageFile2.exists()){
	        	if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(
						Environment.getExternalStorageState()) ){
	        		downloadImage(url,imageType,face,face2,path,categoryId,set);
	        	}else{     	
//	        		Toast.makeText(mContext, "未检测到SD卡，图片下载失败喽！", Toast.LENGTH_SHORT).show();
	        		ToastUtil.getSimpleToast(mContext, -3,  "未检测到SD卡，图片下载失败喽！", 1).show();
	        		face.setVisibility(View.GONE);
		        	face2.setVisibility(View.VISIBLE);
		        	face2.setLayoutParams(getLP(set));	
					face2.setImageResource(R.drawable.chatview_imageloading);	
	        	}
	        }else{
	        	String[] split2 = fileName.split("\\.");
	        	imageType=split2[1];
	        	path=tempPath.getAbsolutePath()+File.separator+CxGlobalConst.S_CHAT_EMOTION+File.separator+split2[0];
	        	setFaceImage(imageType,face,face2,path,categoryId,true,set);
	        }
        }else {
        	setFaceImage(imageType,face,face2,path,categoryId,true,set);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String dateNow = dateFormat.format(new Date((long)(msgCreatTimeStamp) * 1000));

        if (mIsShowDate) {
            dateStamp.setVisibility(View.VISIBLE);
            dateStamp.setText(dateNow);
        } else {
            dateStamp.setVisibility(View.GONE);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date((long)(msgCreatTimeStamp) * 1000));
        String format = view.getResources().getString(
                R.string.cx_fa_nls_reminder_period_time_format);
        String time = String.format(format, calendar);
        timeStamp.setText(time);
        view.setTag(tag);
        return view;
	}

	/**
	 * 设置表情显示
	 * @param imageType  图片类型
	 * @param face	gif图的显示view
	 * @param face2 png图的显示view
	 * @param path	路径
	 * @param categoryId	类别
	 * @param completed		下载是否完成
	 */
	private void setFaceImage(String imageType,EnhancedGifView face,CxImageView face2,String path,int categoryId,boolean completed ,EmotionSet set){
	     if("gif".equals(imageType) && completed==true){
	    		
	        	face.setVisibility(View.VISIBLE);
	        	face2.setVisibility(View.GONE);
	        	face.setLayoutParams(getLP(set));
	//        	face.setGifDecoderImage(Uri.parse("file:///storage/sdcard0/chuxin/face_huaxin.gif"));
	        	face.setGifDecoderImage(Uri.parse("file:///"+path+".gif"));
	        	
	       }else{
	        	face.setVisibility(View.GONE);
	        	face2.setVisibility(View.VISIBLE);
	        	face2.setLayoutParams(getLP(set));
	        	Bitmap bitmap;
				try {
					if(completed){
						bitmap = BitmapFactory.decodeStream(new FileInputStream(path+".png"));
						face2.setImageBitmap(bitmap);
					}else {
//						Toast.makeText(mContext, "图片下载失败喽~~~", Toast.LENGTH_SHORT).show();
						ToastUtil.getSimpleToast(mContext, -3,  "图片下载失败喽~~~", 1).show();
						face2.setImageResource(R.drawable.chatview_imageloading);
					}
				} catch (FileNotFoundException e) {
					
					e.printStackTrace();
				}
	        }
	}
	
	
	/**
	 * 如果本地没有该图片则去下载
	 * @param url 地址
	 * @param imageType 图片类型
	 * @param face 
	 * @param face2
	 * @param path //本地存储路径
	 */
    public  void downloadImage(final String url,final String imageType,final EnhancedGifView face,
    		final CxImageView face2,final String path,final int categoryId,final EmotionSet set){
//    	System.out.println("downloadImage");
    	new AsyncTask<Void, Void, Void>(){

    		private DefaultHttpClient mClient;
			private String fileName;
			private boolean completed=true;
			
			private int downCount=0;

			@Override
    		protected void onPreExecute() {
    		
    			super.onPreExecute();
    			HttpApi mApi = ConnectionManager.getHttpApi();
				mClient = mApi.getmHttpClient();
				
    		}
			@Override
			protected Void doInBackground(Void... params) {
				
				HttpGet getImage=new HttpGet(url);
				try {
					HttpResponse response = mClient.execute(getImage);
					
					if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){

						HttpEntity entity = response.getEntity();	
						
						InputStream in=null;
						
						InputStream responseStream = null;
						try {
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
						
							String folderName = "chuxin"+File.separator+"emotion"+File.separator+"emotions";
							String[] split = url.split("/");
					        fileName = split[split.length-1];		      
					        File path =mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
					        File file = new File(path, folderName + File.separator + fileName);
				        
					        if(!file.getParentFile().exists())
					        	file.getParentFile().mkdirs();						
					        FileOutputStream out=new FileOutputStream(file);
							
							int len1 = 0;
							byte[] buffer = new byte[1024];
							while ((len1 = in.read(buffer)) != -1) {
								out.write(buffer, 0, len1);
							}	
						}catch(Exception e){
							e.printStackTrace();
							if(downCount<3){
								downCount++;
								doInBackground();
							}else{
								completed=false;
							}
						}
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}	
				return null;
			}
			
			@Override
    		protected void onPostExecute(Void result) {
    			super.onPostExecute(result);
    			setFaceImage(imageType,face,face2,path,categoryId,completed,set);    	
    		}
    		
    	}.execute();

    	
    	 
    }
    
    /**
     * 根据表情类别获取屏幕密度以得到相应的宽高
     * @param categoryId
     * @return
     */
    private LayoutParams getLP(EmotionSet set){
    	
    	
    	
    	int emoWidth_dp=dip2px(mContext, (float)100);
    	int emoheight_dp =dip2px(mContext, (float)100);
    	
    	if(set!=null){
    		emoWidth_dp=dip2px(mContext, set.getImageWidth());
    		emoheight_dp=dip2px(mContext, set.getImageHeight());
    	}
    	
    	return new LayoutParams(emoWidth_dp,emoheight_dp);
    }
    
    
    //dip转px
    private  int dip2px(Context context, float dpValue) {  
    	
    	int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
//    	int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
//    	System.out.println(widthPixels+">>>>>"+heightPixels);
    	
//        final float scale = context.getResources().getDisplayMetrics().density;  
    	float scale=((float)widthPixels/320)*1.2f;
//        System.out.println(scale);
        return (int) (dpValue * scale + 0.5f);  
    }  
    
    

}
