package com.chuxin.family.views.chat;

import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.libs.gpuimage.utils.PictureUtils;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.models.Message;
import com.chuxin.family.models.Model;
import com.chuxin.family.models.PictureMessage;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.CxImagePager;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.widgets.CxImageView.LoadComplete;
import com.chuxin.family.zone.CxZoneParam;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.chuxin.family.R;
import com.weibo.sdk.android.api.WeiboAPI.SRC_FILTER;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PhotoEntry extends ChatLogEntry {
	private static final String TAG = "PhotoEntry";

	private static final int VIEW_RES_ID = R.layout.cx_fa_view_chat_chatting_photo_row;

	private static final int ICON_ID = R.id.cx_fa_view_chat_chatting_photo_row__icon;

	private static final int CONTENT_ID = R.id.cx_fa_view_chat_chatting_photo_row__content;

	private static final int TIMESTAMP_ID = R.id.cx_fa_view_chat_chatting_photo_row__timestamp;

	private static final int DATESTAMP_ID = R.id.cx_fa_view_chat_chatting_photo_row__datestamp;

	private static final int SEND_SUCCESS_BUTTON_ID = R.id.cx_fa_view_chat_chatting_photo_row__exclamation;

	private static final boolean OWNER_FLAG = true;
	private String mTempImagePath;

	public PhotoEntry(Message message, Context context, boolean isShowDate) {
		super(message, context, isShowDate);
	}

	@Override
	public int getType() {
		return ENTRY_TYPE_PHOTO;
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

	public int getTimestampId() {
		return TIMESTAMP_ID;
	}

	public int getDateStampId() {
		return DATESTAMP_ID;
	}

	public int getSendSuccessButtonId() {
		return SEND_SUCCESS_BUTTON_ID;
	}

	@Override
	public View build(View view, ViewGroup parent) {
		ChatLogAdapter tag = null;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(getViewResourceId(),
					null);
		}
		PictureMessage message = (PictureMessage) mMessage;
		int msgType = message.getType();
		final int msgId = message.getMsgId();
		int msgCreatTimeStamp = message.getCreateTimestamp();
		final String msgUrl = message.getUrl();

		if (tag == null) {
			tag = new ChatLogAdapter();
		}
		tag.mChatType = msgType;
		tag.mMsgId = msgId;
		tag.isPicture=true;
		CxLog.v("PhotoEntry", "view>>>" + view);
		CxImageView icon = (CxImageView) view.findViewById(getIconId());
		final CxImageView image = (CxImageView) view
				.findViewById(getContentId());
		image.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return false;
			}
		});
		TextView timeStamp = (TextView) view.findViewById(getTimestampId());
		TextView dateStamp = (TextView) view.findViewById(getDateStampId());

		if (isOwner()) {
			CxLog.v(TAG, "icon>>>>" + icon);
			/*icon.setImageResource(R.drawable.cx_fa_wf_icon_small);
			icon.setImage(RkGlobalParams.getInstance().getIconBig(), false, 44,
					mContext, "head", mContext);*/
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
			int msgSendState = message.getSendSuccess();
			final ImageButton reSendBtn = (ImageButton) view
					.findViewById(getSendSuccessButtonId());
			ProgressBar pb = (ProgressBar) view
					.findViewById(R.id.cx_fa_view_chat_chatting_phote_row_circleProgressBar);
			if (msgSendState == 0) {
				reSendBtn.setVisibility(View.GONE);
				pb.setVisibility(View.VISIBLE);
			} else if (msgSendState == 1) {
				reSendBtn.setVisibility(View.GONE);
				pb.setVisibility(View.GONE);
			}
			if (msgSendState == 2) {
				pb.setVisibility(View.GONE);
				reSendBtn.setVisibility(View.VISIBLE);
				reSendBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// resend message
						new AlertDialog.Builder(ChatFragment.getInstance()
								.getActivity())
								.setTitle(R.string.cx_fa_alert_dialog_tip)
								.setMessage(R.string.cx_fa_chat_resend_msg)
								.setPositiveButton(
										R.string.cx_fa_chat_button_resend_text,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												reSendBtn
														.setVisibility(View.GONE);
												ChatFragment.getInstance()
														.reSendMessage(msgUrl,
																3, msgId);
											}
										})
								.setNegativeButton(
										R.string.cx_fa_cancel_button_text, null)
								.show();
					}
				});
			}
			// image.setImageURI(Uri.parse(message.getUrl()));
		} else {
			// RkLog.v(TAG, "partner:" +
			// RkMateParams.getInstance().getMateIcon());
			/*icon.setImageResource(R.drawable.cx_fa_hb_icon_small);
			icon.setImage(RkGlobalParams.getInstance().getPartnerIconBig(),
					false, 44, mContext, "head", mContext);*/
			icon.displayImage(ImageLoader.getInstance(), 
            		CxGlobalParams.getInstance().getPartnerIconBig(), 
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
		PictureUtils pu = new PictureUtils(mContext);
		DisplayMetrics metrics = parent.getResources().getDisplayMetrics();
		String thumbUrl = null;
		switch (metrics.densityDpi) {
		case DisplayMetrics.DENSITY_HIGH:
		case DisplayMetrics.DENSITY_MEDIUM:
		case DisplayMetrics.DENSITY_LOW:
		default:
			thumbUrl = message.getUrl();
			if (isOwner()) {
				Bitmap bm = pu.getImageThumbnail(
						thumbUrl.replace("file://", ""), 320, 320);
				// 矫正图片
				// Matrix matrix = new Matrix();
				// matrix.reset();
				// matrix.postRotate(90);
				// Bitmap bMapRotate = Bitmap.createBitmap(bm, 0, 0,
				// bm.getWidth(),
				// bm.getHeight(), matrix, true);
				// bm = bMapRotate;
				Drawable d = mContext.getResources().getDrawable(
						R.drawable.chatview_imageloading);
				image.setImageDrawable(d);
				image.setImage(bm);
				image.setScaleType(ScaleType.CENTER_CROP);
				mTempImagePath = msgUrl;
				// bMapRotate.recycle();
				image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						imageClick(msgUrl);
					}
				});
				tag.mImagePath = mTempImagePath;
			} else {
				CxLog.i(TAG, "thumbUrl>>>" + thumbUrl);
//				image.setImage(thumbUrl.replace("file://", ""), false, 44,
//						mContext, "head", mContext);
//				image.displayImage(ImageLoader.getInstance(), 
//						thumbUrl.replace("file://", ""), 
//						R.drawable.chatview_imageloading, false, 0);
//				image.setScaleType(ScaleType.CENTER_CROP);
				
				
				DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
				builder.showImageForEmptyUri(R.drawable.chatview_imageloading);
				builder.showImageOnFail(R.drawable.chatview_imageloading);
				builder.bitmapConfig(Bitmap.Config.RGB_565);
				builder.cacheInMemory(true);
				builder.cacheOnDisc(true);
				builder.imageScaleType(ImageScaleType.EXACTLY);
				builder.displayer(new FadeInBitmapDisplayer(0));
				
				final ChatLogAdapter tempTag = tag;
				image.setmUrlStr(thumbUrl.replace("file://", ""));
				ImageLoader.getInstance().displayImage(thumbUrl.replace("file://", ""), 
						image, builder.build(), new ImageLoadingListener() {
							
							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								mTempImagePath = null;
							}
							
							@Override
							public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
								mTempImagePath = null;
							}
							
							@Override
							public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
								try {
									
									mTempImagePath = ImageLoader.getInstance().getFilePath(arg0);
									CxLog.i("999", " mTempImagePath = "+mTempImagePath);
									tempTag.mImagePath = mTempImagePath;
									
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
							@Override
							public void onLoadingCancelled(String arg0, View arg1) {
								mTempImagePath = null;
							}
						});
//				mTempImagePath = image.getImageLocalPath();
//				mTempImagePath = ImageLoader.getInstance().getFilePath(
//						thumbUrl.replace("file://", ""));
//				final String tempImageStr = mTempImagePath;
				
				image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
//						imageClick(imagePath);
//						String imagePath = image.getImageLocalPath().toString();
						String imagePath = image.getImagePath();
						if (null == imagePath) {
							Toast.makeText(mContext, "本地图片加载失败", Toast.LENGTH_SHORT).show();
						}else{
							imageClick(imagePath);
						}
					}
				});
				
				image.setCompleteCallback(new LoadComplete() {

					@Override
					public void completeImgLoad() {
						/*final String imagePath = image.getImageLocalPath()
								.toString();
						if (null == imagePath) {
							RkLog.w("chat completeImgLoad is null", "!!!!!!!!!");
							return;
						}
						RkLog.d(TAG, "imagePath>>>>" + imagePath);*/
						/*image.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
//								imageClick(imagePath);
								String imagePath = image.getImageLocalPath().toString();
								if (null == imagePath) {
									Toast.makeText(mContext, "本地图片加载失败", Toast.LENGTH_SHORT).show();
								}else{
									imageClick(imagePath);
								}
							}
						});*/
					}
				});
			}
			break;
		}
//		tag.mImagePath = mTempImagePath;
//		RkLog.i(TAG, "tag.mImagePath>>>" + tag.mImagePath);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
		String dateNow = dateFormat.format(new Date(
				(long) (msgCreatTimeStamp) * 1000));

		if (mIsShowDate) {
			dateStamp.setVisibility(View.VISIBLE);
			dateStamp.setText(dateNow);
		} else {
			dateStamp.setVisibility(View.GONE);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date((long) (msgCreatTimeStamp) * 1000));
		String format = view.getResources().getString(
				R.string.cx_fa_nls_reminder_period_time_format);
		String time = String.format(format, calendar);
		timeStamp.setText(time);
		view.setTag(tag);
		return view;
	}

	private void imageClick(String imagepath) {
		if (null == imagepath) {
			if (null == mContext) {
				return;
			}
			Toast toast = Toast.makeText(mContext, mContext.getString(
					R.string.cx_fa_file_exists), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
	    getImagePaths();
	    for(int i = 0; i < imagepaths.size(); i++ ){
	        if(imagepath.equals(imagepaths.get(i))){
	            Intent imageDetail = new Intent(ChatFragment.getInstance().getActivity(), CxImagePager.class);
                imageDetail.putExtra(CxGlobalConst.S_ZONE_TITLE_MORE_BUTTTON, true);
                imageDetail.putExtra(CxGlobalConst.S_ZONE_SELECTED_ORDER, i);
                imageDetail.putExtra(CxGlobalConst.S_STATE, CxImagePager.STATE_CHAT);
                imageDetail.putStringArrayListExtra("imagespath", (ArrayList<String>)imagepaths);
                mContext.startActivity(imageDetail);
                ChatFragment.getInstance().getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	        }
	    }
	    
		// 检测图片是否存在
//		if (null == imagepath) { // null检查---add by niechao
//			Toast toast = Toast.makeText(ChatFragment.getInstance()
//					.getActivity(), ChatFragment.getInstance().getActivity()
//					.getString(R.string.cx_fa_file_exists), Toast.LENGTH_LONG);
//			toast.setGravity(Gravity.CENTER, 0, 0);
//			toast.show();
//			return;
//		}
//		File imageFile = new File(imagepath.replace("file://", ""));
//		if (imageFile.exists()) {
//			// TODO save picture
//			Intent intent = new Intent(
//					ChatFragment.getInstance().getActivity(),
//					SavePictureActiviy.class);
//			intent.putExtra("big_picture_url", imagepath);
//			ChatFragment.getInstance().getActivity().startActivity(intent);
//			ChatFragment
//					.getInstance()
//					.getActivity()
//					.overridePendingTransition(
//							R.anim.cx_fa_anim_activity_enter_left_in,
//							R.anim.cx_fa_anim_activity_enter_left_out);
//		} else {
//			Toast toast = Toast.makeText(ChatFragment.getInstance()
//					.getActivity(), ChatFragment.getInstance().getActivity()
//					.getString(R.string.cx_fa_file_exists), Toast.LENGTH_LONG);
//			toast.setGravity(Gravity.CENTER, 0, 0);
//			toast.show();
//		}
	}
	private List<String> imagepaths = null;;
   private void getImagePaths(){
       imagepaths = new ArrayList<String>();
        List<Model> messages = new Message(null, mContext).gets("1=1", new String[] {}, null, 0, 0);
        if(null == messages || messages.size() <= 0){
            return;
        }
        CxLog.i(TAG, "test==" + messages.toString());
        Iterator<Model> i = messages.iterator();
        try {
            while(i.hasNext()){
                Message msg = (Message)i.next();
                CxLog.i(TAG, "test==msg" + msg.mData.toString());
                JSONObject obj = msg.mData;
                if(obj.get("type").equals("photo")){
//                   PictureMessage pm = (PictureMessage) msg;
               final PictureMessage pm = new PictureMessage(msg.mData, mContext);
                    CxLog.i(TAG, "test==vm" + pm.mData.toString());
                    if(pm.getUrl().startsWith("http:")){
                        imagepaths.add(ImageLoader.getInstance().getFilePath(pm.getUrl()));
                    } else {
                        imagepaths.add(pm.getUrl());
                    }
                }
            }
        } catch (JSONException e) {
            CxLog.e(TAG, ""+e.getMessage());
        }
        CxLog.i(TAG, "test==vm" + imagepaths);
    }

}
