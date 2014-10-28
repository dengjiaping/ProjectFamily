package com.chuxin.family.views.chat;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.models.EmotionMessage;
import com.chuxin.family.models.Message;
import com.chuxin.family.models.PictureMessage;
import com.chuxin.family.models.TextMessage;

abstract public class ChatLogEntry {

	protected final static int ENTRY_TYPE_TEXT = 1;
	protected final static int ENTRY_TYPE_FACE = 2;
	protected final static int ENTRY_TYPE_PHOTO = 3;
	protected final static int ENTRY_TYPE_FEED = 4;
	protected final static int ENTRY_TYPE_RECORD = 5;
	protected final static int ENTRY_TYPE_WELCOME = 6;
	protected final static int ENTRY_TYPE_PHRASE = 7;
	protected final static int ENTRY_TYPE_LOCATION = 8;
	protected final static int ENTRY_TYPE_EMOTION = 9;
	protected final static int ENTRY_TYPE_TABLOID = 10;
	protected final static int ENTRY_TYPE_ANIMATION = 11;
	protected final static int ENTRY_TYPE_PEER_TEXT = 12;
	protected final static int ENTRY_TYPE_PEER_FACE = 13;
	protected final static int ENTRY_TYPE_PEER_PHOTO = 14;
	protected final static int ENTRY_TYPE_PEER_FEED = 15;
	protected final static int ENTRY_TYPE_PEER_RECORD = 16;
	protected final static int ENTRY_TYPE_PEER_WELCOME = 17;
	protected final static int ENTRY_TYPE_PEER_PHRASE = 18;
	protected final static int ENTRY_TYPE_PEER_LOCATION = 19;
	protected final static int ENTRY_TYPE_PEER_EMOTION = 20;
	protected final static int ENTRY_TYPE_PEER_TABLOID = 21;
	protected final static int ENTRY_TYPE_PEER_ANIMATION = 22;
	protected final static int ENTRY_TYPE_SYSTEM = 23;
	protected final static int ENTRY_TYPE_GUESS_REQUEST = 24;
	protected final static int ENTRY_TYPE_GUESS_RESPONSE = 25;
	protected final static int ENTRY_TYPE_PEER_GUESS_REQUEST  = 26;
	protected final static int ENTRY_TYPE_PEER_GUESS_REPONSE  = 27;
	
	public final static int ENTRY_TYPE_MAX = ENTRY_TYPE_PEER_GUESS_REPONSE +1;

	protected Message mMessage;
	protected Context mContext;
	protected boolean mIsShowDate;

	public ChatLogEntry(Message message, Context context, boolean isShowDate) {
		mMessage = message;
		mContext = context;
		mIsShowDate = isShowDate;
	}

	public abstract boolean isOwner();

	public abstract int getType();

	public abstract View build(View view, ViewGroup parent);
	
	public static boolean isFaceEntry(Resources resources, TextMessage message) {
		if (message == null)
			return false;
		
		String content = message.getText();
		assert(content != null);
		if (content.length() < 3)
			return false;
		
		String begin = content.substring(0, 1);
		String end   = content.substring(content.length() - 1, content.length());
		if (!begin.equals("[") || !end.equals("]"))
			return false;
		
		String[] faceTexts = FaceEntry.getFaceTexts(resources);
		for (int i = 0; i < faceTexts.length; i++) {
			if (faceTexts[i].equals(content))
				return true;
		}
		
		return false;
	}
	
	//发送和接收时判断是不是经典表情
	public static boolean isEmotionEntry(Resources resources, TextMessage message) {
		if (message == null)
			return false;
		
		String content = message.getText();
		if (null == content) {
			return false;
		}
//		assert(content != null);
		if (content.length() < 3)
			return false;
		
		String begin = content.substring(0, 1);
		String end   = content.substring(content.length() - 1, content.length());
		if (!begin.equals("[") || !end.equals("]"))
			return false;
		
		String[] faceTexts = EmotionEntry.getFaceTexts(resources);
		for (int i = 0; i < faceTexts.length; i++) {
			if (faceTexts[i].equals(content))
				return true;
		}
		 
		return false;
	}
	
	//接收时判断是不是非经典表情
	public static boolean isEmotionEntry2(Resources resources, PictureMessage message) {
        if (message == null) {

            return false;
        }

        int isEmotion = message.getIsEmotion();
        if (isEmotion == 1) {
            return true;
        }
        return false;
	}
		

	public static ChatLogEntry buildLogEntry(Message message, Context context,
			boolean isShowDate) {
//		mIsShowDate = isShowDate;
//		mContext = context;
//		mChatFragment = chatFragmet;
		if (message.getSender()
				.equals(CxGlobalParams.getInstance().getUserId())) {

			switch (message.getType()) {
				case Message.MESSAGE_TYPE_WELCOME:
					return new WelcomeEntry(message, context, isShowDate);
				case Message.MESSAGE_TYPE_VOICE:
					return new RecordEntry(message, context, isShowDate);
				case Message.MESSAGE_TYPE_PHRASE:
					return new PhraseEntry(message, context, isShowDate);
				case Message.MESSAGE_TYPE_FACE:
					return new FaceEntry(message, context, isShowDate);
				case Message.MESSAGE_TYPE_EMOTION:
					return new EmotionEntry(message, context, isShowDate);
				case Message.MESSAGE_TYPE_PICTURE:
					return new PhotoEntry(message, context, isShowDate);
	            case Message.MESSAGE_TYPE_LOCATION:
	                return new LocationEntry(message, context, isShowDate);
	            case Message.MESSAGE_TYPE_TABLOID:
	            	return new PartnerTabloidEntry(message, context, isShowDate);
	            case Message.MESSAGE_TYPE_ANIMATION:
	            	return new AnimationEntry(message, context, isShowDate);
	            case Message.MESSAGE_TYPE_GUESS_REQUEST:
	            	return new GuessEntry(message, context, isShowDate);
	            case Message.MESSAGE_TYPE_GUESS_RESPONSE:
	            	return new GuessEntry(message, context, isShowDate);
				case Message.MESSAGE_TYPE_TEXT:
					//经典表情发送和接收走的是text，但显示还是走EmotionEntry
					if (isEmotionEntry(context.getResources(), (TextMessage)(message))) {
						return new EmotionEntry(message, context, isShowDate);
					}
				default:
					return new TextEntry(message, context, isShowDate);
			}
		} else {
			switch (message.getType()) {
				case Message.MESSAGE_TYPE_WELCOME:
					return new WelcomeEntry(message, context, isShowDate);
				case Message.MESSAGE_TYPE_VOICE:
					return new PartnerRecordEntry(message, context, isShowDate);
				case Message.MESSAGE_TYPE_PHRASE:
					return new PartnerPhraseEntry(message, context, isShowDate);
				case Message.MESSAGE_TYPE_FACE:
					return new PartnerFaceEntry(message, context, isShowDate);
				case Message.MESSAGE_TYPE_EMOTION:
					return new PartnerEmotionEntry(message, context, isShowDate);
				case Message.MESSAGE_TYPE_PICTURE:
					//经典表情发送走的是emotion，接收走的是photo，显示还是要走PartnerEmotionEntry
					if (isEmotionEntry2(context.getResources(), (PictureMessage)(message))){
						
						return new PartnerEmotionEntry(message, context, isShowDate);
					}else{

						return new PartnerPhotoEntry(message, context, isShowDate);
					}
	            case Message.MESSAGE_TYPE_LOCATION:
	                return new PartnerLocationEntry(message, context, isShowDate);
	            case Message.MESSAGE_TYPE_FEED:
	                return new FeedEntry(message, context, isShowDate);
	            case Message.MESSAGE_TYPE_TABLOID:
	            	return new PartnerTabloidEntry(message, context, isShowDate);
	            case Message.MESSAGE_TYPE_ANIMATION:
	            	return new PartnerAnimationEntry(message, context, isShowDate);
	            case Message.MESSAGE_TYPE_GUESS_REQUEST:
	            	return new PartnerGuessEntry(message, context, isShowDate);
	            case Message.MESSAGE_TYPE_GUESS_RESPONSE:
	            	return new PartnerGuessEntry(message, context, isShowDate);
	            case Message.MESSAGE_TYPE_SYSTEM:
	            	return new SystemEntry(message, context, isShowDate);
				case Message.MESSAGE_TYPE_TEXT:
					if (isEmotionEntry(context.getResources(), (TextMessage)(message))) {
						return new PartnerEmotionEntry(message, context, isShowDate);
					}
				default:
					return new PartnerTextEntry(message, context, isShowDate);
			}

		}
	}

}
