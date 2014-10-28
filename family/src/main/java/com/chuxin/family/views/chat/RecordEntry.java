package com.chuxin.family.views.chat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import org.fmod.effects.RkSoundEffects;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.models.Message;
import com.chuxin.family.models.VoiceMessage;
import com.chuxin.family.neighbour.DialogListener;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CxAudioFileResourceManager;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.widgets.CxInputPanel;
import com.chuxin.family.widgets.VoiceTip;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

public class RecordEntry extends ChatLogEntry {
	private static final String TAG = "RecordEntry";
	private static final int VIEW_RES_ID = R.layout.cx_fa_view_chat_chatting_record_row;
	private static final int ICON_ID = R.id.cx_fa_view_chat_chatting_record_row__icon;
	private static final int CONTENT_ID = R.id.cx_fa_view_chat_chatting_record_audio_length;
	private static final int TIMESTAMP_ID = R.id.cx_fa_view_chat_chatting_record_row__timestamp;
	private static final int DATESTAMP_ID = R.id.cx_fa_view_chat_chatting_record_row__datestamp;
	private static final int SEND_SUCCESS_BUTTON_ID = R.id.cx_fa_view_chat_chatting_record_row__exclamation;
	private static final int VOICE_IMAGE_VIEW_ID = R.id.cx_fa_view_chat_chatting_record_imageview;
	private static final int VOICE_LINEARLAYOUT_ID = R.id.cx_fa_view_chat_chatting_record_row__content;
	
	private static final int SOUND_EFFECT_TEXT_ID = R.id.cx_fa_view_chat_chatting_record_soundeffect_text;
	private static final int SOUND_EFFECT_IMAGEVIEW_ID = R.id.cx_fa_view_chat_chatting_record_soundimage;

	private static final boolean OWNER_FLAG = true;
	private static String dateTime = null;
	private ImageView mVoiceImageView;
	private AnimationDrawable mVoiceAd;
	private LinearLayout mVoiceLinearLayout;
	private boolean mVoicePlayFlag = true; // play voice flag
	private Timer mTimer = null;
	private TimerTask mTask = null;

	private ImageView newVoiceIcon = null; // 标示是否读过的小红点
	// VoiceMessage message = null;
	// private int mCurrentMsgId;
	private ProgressBar mPartnerProgressBar = null;
	private int mRetryCount = 3; // 重试3次下载
	private ChatSoundEffectDialog mEffectDialog = null;
	private int mEffect = 0;// 语音变声效果
	public Handler recordHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				// stopRecordAnimation();
				stopVoice();

				break;
			}
		};
	};

	public RecordEntry(Message message, Context context, boolean isShowDate) {
		super(message, context, isShowDate);
	}

	@Override
	public int getType() {
		return ENTRY_TYPE_RECORD;
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

	public int getTimeStampId() {
		return TIMESTAMP_ID;
	}

	public int getDateStampId() {
		return DATESTAMP_ID;
	}

	public int getSendSuccessButtonId() {
		return SEND_SUCCESS_BUTTON_ID;
	}

	public int getVoiceImageViewId() {
		return VOICE_IMAGE_VIEW_ID;
	}

	public int getVoiceLinearLayoutId() {
		return VOICE_LINEARLAYOUT_ID;
	}
	
	public int getSoundEffectText(){
	    return SOUND_EFFECT_TEXT_ID;
	}
	
	public int getSoundEffectImageView(){
	    return SOUND_EFFECT_IMAGEVIEW_ID;
	}

	@Override
	public View build(View view, ViewGroup parent) {
		ChatLogAdapter tag = null;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(getViewResourceId(),
					null);
		}

		final VoiceMessage message = (VoiceMessage) mMessage;
		int msgType = message.getType();
		final int msgId = message.getMsgId();
		// mCurrentMsgId = msgId;

		int msgCreatTimeStamp = message.getCreateTimestamp();
		final String msgVoiceUrl = message.getVoiceUrl();
		final int msgVoiceLen = message.getVoiceLen();
		final int audioType = message.getAudioType();
		mEffect = audioType;
		try {
			tag = (ChatLogAdapter) view.getTag();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (tag == null) {
			tag = new ChatLogAdapter();
		}

		tag.mChatType = msgType;
		tag.mMsgId = msgId;
		tag.mVoiceUrl = msgVoiceUrl;
		CxImageView icon = (CxImageView) view.findViewById(getIconId());
		TextView text = (TextView) view.findViewById(getContentId());
		TextView timeStamp = (TextView) view.findViewById(getTimeStampId());
		TextView dateStamp = (TextView) view.findViewById(getDateStampId());
		
		mVoiceImageView = (ImageView) view.findViewById(getVoiceImageViewId());
		mVoiceLinearLayout = (LinearLayout) view
				.findViewById(getVoiceLinearLayoutId());
		int minWidth = 110 + msgVoiceLen * (230 - 110) / 30 + 20; // ios 机制
		mVoiceLinearLayout.setMinimumWidth(minWidth); // 215
		// mVoiceLinearLayout.setMinimumHeight(85);
		
        TextView soundEffectText = (TextView)view.findViewById(getSoundEffectText());
        ImageView soundEffectImageview = (ImageView)view.findViewById(getSoundEffectImageView());
        
        switch(audioType){
                case CxInputPanel.RKDSP_EFFECT_YUANSHENG:
                    soundEffectText.setVisibility(View.GONE);
                    soundEffectImageview.setVisibility(View.GONE);
                    break;
                case CxInputPanel.RKDSP_EFFECT_HANHAN:
                    soundEffectText.setVisibility(View.VISIBLE);
                    soundEffectImageview.setVisibility(View.VISIBLE);
                    soundEffectText.setText(R.string.cx_fa_chatview_soundeffect_text_zhuanghan);
                    soundEffectImageview.setImageResource(R.drawable.cx_fa_sound_zhuanghan);
                    break;
                case CxInputPanel.RKDSP_EFFECT_YOUYOU:
                    soundEffectText.setVisibility(View.VISIBLE);
                    soundEffectImageview.setVisibility(View.VISIBLE);
                    soundEffectText.setText(R.string.cx_fa_chatview_soundeffect_text_zhuangyou);
                    soundEffectImageview.setImageResource(R.drawable.cx_fa_sound_zhuangyou);
                    break;
                case CxInputPanel.RKDSP_EFFECT_HUAIHUAI:
                    soundEffectText.setVisibility(View.VISIBLE);
                    soundEffectImageview.setVisibility(View.VISIBLE);
                    soundEffectText.setText(R.string.cx_fa_chatview_soundeffect_text_zhuanghuai);
                    soundEffectImageview.setImageResource(R.drawable.cx_fa_sound_zhuanghuai);
                    break;
                case CxInputPanel.RKDSP_EFFECT_SHASHA:
                    soundEffectText.setVisibility(View.VISIBLE);
                    soundEffectImageview.setVisibility(View.VISIBLE);
                    soundEffectText.setText(R.string.cx_fa_chatview_soundeffect_text_zhuangsha);
                    soundEffectImageview.setImageResource(R.drawable.cx_fa_sound_zhuangsha);
                    break;
        }
		
		if (isOwner()) {
			int msgSendState = message.getSendSuccess();
			mVoiceImageView.setImageResource(R.drawable.chat_voice6);
			/*icon.setImageResource(R.drawable.cx_fa_wf_icon_small);
			icon.setImage(RkGlobalParams.getInstance().getIconBig(), false, 44,
					mContext, "head", mContext);*/
			icon.displayImage(ImageLoader.getInstance(), 
            		CxGlobalParams.getInstance().getIconSmall(), 
            		CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
            		CxGlobalParams.getInstance().getSmallImgConner());
			// mVoiceImageView.setRotation(180);
			icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO update owner headimage
					CxLog.i(TAG, "click update owner headimage button");
					ChatFragment.getInstance().updateHeadImage();
				}
			});
			final ImageButton reSendBtn = (ImageButton) view
					.findViewById(getSendSuccessButtonId());
			ProgressBar pb = (ProgressBar) view
					.findViewById(R.id.cx_fa_view_chat_chatting_record_row_circleProgressBar);
			if (msgSendState == 0) { // 发送中
				pb.setVisibility(View.VISIBLE);
				reSendBtn.setVisibility(View.GONE);
			}
			if (msgSendState == 1) { // 发送成功
				pb.setVisibility(View.GONE);
				reSendBtn.setVisibility(View.GONE);
			}
			if (msgSendState == 2) { // 发送失败
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
														.reSendMessage(
																msgVoiceUrl, 4,
																msgId,
																msgVoiceLen, audioType);
											}
										})
								.setNegativeButton(
										R.string.cx_fa_cancel_button_text, null)
								.show();

					}
				});
			}
		} else {
			mPartnerProgressBar = (ProgressBar) view
					.findViewById(R.id.cx_fa_view_chat_chatting_record_row_partner_circleProgressBar);
			mVoiceImageView.setImageResource(R.drawable.chat_voice3);

			newVoiceIcon = (ImageView) view
					.findViewById(R.id.cx_fa_view_chat_chatting_record_newvoice_for_partner); // 得到是否读过的小红点对象
			// 如果该条信息已读过，则将小红点隐藏
			if (message.getIsRead()) {
				newVoiceIcon.setVisibility(View.GONE);
			} else {
				newVoiceIcon.setVisibility(View.VISIBLE);
			}

			// RkLog.v(TAG, "partner:" +
			// RkMateParams.getInstance().getMateIcon());
			// mVoiceImageView.setRotation(0);
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
		
		mVoiceLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isOwner()) { // 自己发的语音
					if (mVoicePlayFlag) {

						playVoice(msgVoiceUrl, msgVoiceLen);

						if (!isOwner() && mMessage != null
								&& !message.getIsRead()) {

							newVoiceIcon.setVisibility(View.GONE); // 将标示信息未读的小红点隐藏

							// 将该条信息设置为已读, 并刷新聊天页面
							try {
								mMessage.mData.put("is_read", true);
								mMessage.update();

								ChatFragment.getInstance().updateChatViewDB(); // 强制更新一下视图中的数据，使之与数据库保持一致
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					} else {
						stopVoice();
					}
				} else { // 对方发过来的语音
					getAudioFile(msgVoiceUrl, msgVoiceLen, message);
				}
			}
		});

		text.setText(msgVoiceLen + "''");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date((long) (msgCreatTimeStamp) * 1000));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
		String dateNow = dateFormat.format(new Date(
				(long) (msgCreatTimeStamp) * 1000));

		if (mIsShowDate) {
			dateStamp.setVisibility(View.VISIBLE);
			dateStamp.setText(dateNow);
		} else {
			dateStamp.setVisibility(View.GONE);
		}
		String format = view.getResources().getString(
				R.string.cx_fa_nls_reminder_period_time_format);
		String time = String.format(format, calendar);
		timeStamp.setText(time);
		view.setTag(tag);
		return view;
	}

	public void playVoice(String path, int msgVoiceLen) {
		CxLog.v(TAG, "voice uri=" + path);
		if (null != ChatFragment.getInstance().mPlayRecordEntry
				&& ChatFragment.getInstance().mPlayRecordEntry != RecordEntry.this) {
			ChatFragment.getInstance().mPlayRecordEntry.stopRecordAnimation();
		}
		if (RkSoundEffects.cFmodGetIsPlay()) {
			// RkSoundEffects.cFmodStop();
			// stopRecordAnimation();
			stopVoice();
		}
		try {
			RkSoundEffects.soundPlay(path, mEffect);
		} catch (Exception e) {
			e.printStackTrace();
			new Handler(mContext.getMainLooper()) {
				public void handleMessage(android.os.Message msg) {
					Toast.makeText(
							mContext,
							mContext.getString(R.string.cx_fa_destroy_audio_file),
							Toast.LENGTH_SHORT).show();
				};
			}.sendEmptyMessage(1);
			return;
		}

		try {
			startTimer(msgVoiceLen);
			startRecordAnimation();
			if(mEffect > 0){
    			if(null == mEffectDialog){
    			    mEffectDialog= new ChatSoundEffectDialog(mContext, mEffect, R.style.bg_transparent_dialog, new DialogListener() {
                        
                        @Override
                        public void refreshUiAndData() {
                            stopVoice();
                        }
                    });
    			}
    			mEffectDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ChatFragment.getInstance().mPlayRecordEntry = RecordEntry.this;
	}

	public void stopVoice() {
		CxLog.d("playVoice", "isplay1=" + RkSoundEffects.cFmodGetIsPlay());
		CxLog.d("playVoice", "ispause1=" + RkSoundEffects.cFmodGetIsPause());
		if (RkSoundEffects.cFmodGetIsPlay() || RkSoundEffects.cFmodGetIsPause()) {
			RkSoundEffects.cFmodStop();
		}
		if (null != mTimer) {
			mTimer.cancel();
			mTimer = null;
		}
		if (null != mTask) {
			mTask.cancel();
			mTask = null;
		}

		stopRecordAnimation();
		if(mEffect > 0){
            if(null != mEffectDialog){
                mEffectDialog.dismiss();
            }
        }
	}

	private void startRecordAnimation() {
		if (isOwner()) {
			mVoiceImageView.setImageResource(R.anim.cx_fa_anim_chat_voice);
		} else {
			mVoiceImageView
					.setImageResource(R.anim.cx_fa_anim_chat_voice_for_partner);
		}
		mVoiceAd = (AnimationDrawable) mVoiceImageView.getDrawable();
		mVoiceAd.start();
		mVoicePlayFlag = false;
	}

	private void stopRecordAnimation() {
		if (null != mVoiceAd && mVoiceAd.isRunning()) {
			mVoiceAd.stop();
			mVoicePlayFlag = true;
			if (isOwner()) {
				mVoiceImageView.setImageResource(R.drawable.chat_voice6);
			} else {
				mVoiceImageView.setImageResource(R.drawable.chat_voice3);
			}
		}
	}

	private void startTimer(int msgVoiceLen) {
		if (null == mTimer) {
			mTimer = new Timer();
		}
		if (null == mTask) {
			mTask = new TimerTask() {

				@Override
				public void run() {
					android.os.Message message = recordHandler.obtainMessage(0);
					message.sendToTarget();
				}
			};
		}
		mTimer.schedule(mTask, (msgVoiceLen + 1) * 1000);
	}

	public String getAudioFile(final String url, final int audioLength,
			final VoiceMessage message) {
		if ((null == url) || (url.equals("null"))) { // 避免服务器返回"null"
			CxLog.i(TAG, " param url is null");
			return "";
		}

		final CxAudioFileResourceManager resourceManager = CxAudioFileResourceManager
				.getAudioFileResourceManager(ChatFragment.getInstance()
						.getActivity());
		// File file = resourceManager.getFile(Uri.parse(url));
		if (resourceManager.exists(Uri.parse(url))) {
			CxLog.i(TAG, "file path local=");
			final File file = resourceManager.getFile(Uri.parse(url));
			CxLog.i("getAudioFile", "filepath0=" + file.getAbsolutePath());
			new Handler(mContext.getMainLooper()) {
				public void handleMessage(android.os.Message msg) {
					preparePlayVoice(file.getAbsolutePath(), audioLength,
							message);
				}
			}.sendEmptyMessage(1);
			return file.getAbsolutePath();
		} else {
			// checkSdCardExist();
			new Handler(mContext.getMainLooper()) {
				public void handleMessage(android.os.Message msg) {
					mVoiceImageView.setVisibility(View.GONE);
					mPartnerProgressBar.setVisibility(View.VISIBLE);
				}
			}.sendEmptyMessage(1);

			CxLog.i(TAG, "ready to net download");
			resourceManager
					.addObserver(new CxAudioFileResourceManager.ResourceRequestObserver(
							Uri.parse(url)) {
						@Override
						public void requestReceived(Observable observable,
								Uri uri, long len) {
							observable.deleteObserver(this);
							try {
								mRetryCount--;
								CxLog.i("getAudioFile", "uri="+uri.toString());
								File file = resourceManager.getFile(uri);
								CxLog.i("getAudioFile", "contentlength>>>"
										+ len);
								CxLog.i("getAudioFile",
										"file.length>>>" + file.length());
								CxLog.i("getAudioFile", "audiolength>>>"
										+ audioLength);

								if (file.length() < len || (file.length() == 0 && len == -1)) { //考虑到目前腾讯服务器的问题
									CxLog.i("getAudioFile",
											"not download file complete, need to reload");
									file.delete();
									// 重试3次，失败了给出提示
									if (mRetryCount > 0) { //如果重试机会还有就重试下载
										getAudioFile(url, audioLength, message);
									} else { //重试次数没有就给出失败提示
										new Handler(mContext.getMainLooper()) {
                                        public void handleMessage(
                                                android.os.Message msg) {
                                            mPartnerProgressBar
                                                    .setVisibility(View.GONE);
                                            mVoiceImageView
                                                    .setVisibility(View.VISIBLE);
                                            ToastUtil.getSimpleToast(mContext, -3, mContext
                                                    .getString(R.string.cx_fa_not_download_file_complete), 1).show();
                                        }
                                    }.sendEmptyMessage(1);

                                }
								} else {
									CxLog.i("getAudioFile",
											"filepath1="
													+ file.getAbsolutePath());
									final String audioFilePath = file
											.getAbsolutePath();
									CxLog.i("getAudioFile", "audioFilePath="
											+ audioFilePath);
									new Handler(mContext.getMainLooper()) {
										public void handleMessage(
												android.os.Message msg) {
											mPartnerProgressBar
													.setVisibility(View.GONE);
											mVoiceImageView
													.setVisibility(View.VISIBLE);
											preparePlayVoice(audioFilePath,
													audioLength, message);
										}
									}.sendEmptyMessage(1);
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			resourceManager.request(Uri.parse(url));
			return "";
		}
	}

	private void preparePlayVoice(final String msgVoiceUrl,
			final int msgVoiceLen, VoiceMessage message) {
		if (mVoicePlayFlag) {
			playVoice(msgVoiceUrl, msgVoiceLen);
			if (!isOwner() && mMessage != null && !message.getIsRead()) {

				newVoiceIcon.setVisibility(View.GONE); // 将标示信息未读的小红点隐藏

				// 将该条信息设置为已读, 并刷新聊天页面
				try {
					mMessage.mData.put("is_read", true);
					mMessage.update();

					ChatFragment.getInstance().updateChatViewDB(); // 强制更新一下视图中的数据，使之与数据库保持一致
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			stopVoice();
		}
	}
}
