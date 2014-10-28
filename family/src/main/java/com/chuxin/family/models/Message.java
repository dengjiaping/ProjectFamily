package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.utils.CxLog;

public class Message extends Model {

	private static final String TAG = "Message";
	private static final String TAG_SENDER = "sender";
	private static final String TAG_MSG_ID = "id";
	private static final String TAG_CREATE_TIMESTAMP = "create_time";
	private static final String TAG_TYPE = "type";
	private static final String TAG_SEND_SUCCESS = "send_success";

	public static final int MESSAGE_TYPE_TEXT = 0;
	public static final int MESSAGE_TYPE_PICTURE = 1;
	public static final int MESSAGE_TYPE_FACE = 2;
	public static final int MESSAGE_TYPE_PHRASE = 3;
	public static final int MESSAGE_TYPE_VOICE = 4;
	public static final int MESSAGE_TYPE_LOCATION = 5;
	public static final int MESSAGE_TYPE_FEED = 6;
	public static final int MESSAGE_TYPE_WELCOME = 7;
	public static final int MESSAGE_TYPE_NUMBER = 8;
	public static final int MESSAGE_TYPE_EMOTION = 11;
	public static final int MESSAGE_TYPE_TABLOID = 12;
	public static final int MESSAGE_TYPE_SYSTEM = 13;
	public static final int MESSAGE_TYPE_ANIMATION = 14;
	public static final int MESSAGE_TYPE_GUESS_REQUEST = 15;
	public static final int MESSAGE_TYPE_GUESS_RESPONSE = 16;

    public Message() {
        super();
        mTable = "messages";
    }
	public Message(JSONObject data, Context context) {
		super();
		mTable = "messages";
		if (data != null) {
			mData = data;
			mId = String.valueOf(getMsgId());
			setFlag((int) (System.currentTimeMillis() / 1000)); // 按时间顺序作标记，以便顺序查取
		}
		mContext = context;
	}

	public String getSender() {
		try {
			return mData.getString(TAG_SENDER);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return null;
	}

	public int getMsgId() {
		try {
			return mData.getInt(TAG_MSG_ID);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return -1;
	}

	public int getSendSuccess() {
		try {
			return mData.getInt(TAG_SEND_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return 0;
	}

	public int getType() {
		return -1;
	}

	public int getCreateTimestamp() {
		try {
			return mData.getInt(TAG_CREATE_TIMESTAMP);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return 0;
	}

	/**
	 * 构建消息对象
	 * @param msg
	 * @param type
	 * @param sendState 发送状态 0，发送中 1.发送成功 2，发送失败
	 * @param id
	 * @return
	 */
	public static Message buildMessageObject(String msg, String type,
			int sendState, long id) {
		String data = null;
		if (type.equals("audio")) {
			data = "{\"sender\":\"" + CxGlobalParams.getInstance().getUserId()
					+ "\", \"id\": " + id
					+ ",\"type\": \"audio\", \"audio\": " + msg
					+ ",\"create_time\":"
					+ (int) (System.currentTimeMillis() / 1000)
					+ ",\"send_success\": " + sendState + "}";
		} else if (type.equals("photo")) {
			data = "{\"sender\":\"" + CxGlobalParams.getInstance().getUserId()
					+ "\", \"id\": " + id
					+ ",\"type\": \"photo\", \"photo\": " + msg
					+ ",\"create_time\":"
					+ (int) (System.currentTimeMillis() / 1000)
					+ ",\"send_success\": " + sendState + "}";
		} else if (type.equals("phrase")) {
			data = "{\"sender\":\"" + CxGlobalParams.getInstance().getUserId()
					+ "\",\"id\": " + id
					+ ",\"type\": \"phrase\", \"phrase\": \"" + msg
					+ "\",\"create_time\":"
					+ (int) (System.currentTimeMillis() / 1000)
					+ ",\"send_success\": " + sendState + "}";
		} else if (type.equals("face")) {
			data = "{\"sender\":\"" + CxGlobalParams.getInstance().getUserId()
					+ "\",\"id\": " + id
					+ ",\"type\": \"face\", \"face\": \"" + msg
					+ "\",\"create_time\":"
					+ (int) (System.currentTimeMillis() / 1000)
					+ ",\"send_success\": " + sendState + "}";
		} else if (type.equals("feed")) {
			return null;
		} else if (type.equals("default")) {
			data = "{\"sender\":\"" + CxGlobalParams.getInstance().getPairId()
					+ "\",\"id\": " + id
					+ ",\"type\": \"text\", \"text\": \"" + msg
					+ "\",\"create_time\":"
					+ (int) (System.currentTimeMillis() / 1000)
					+ ",\"send_success\": " + sendState + "}";
		} else if (type.equals("welcome")) {
			data = "{\"sender\":\"" + CxGlobalParams.getInstance().getUserId()
					+ "\",\"id\": " + id
					+ ",\"type\": \"welcome\", \"text\": \"" + msg
					+ "\",\"create_time\":"
					+ (int) (System.currentTimeMillis() / 1000)
					+ ",\"send_success\": " + sendState + "}";
		} else if (type.equals("emotion")) {
			
			String[] split = msg.split("\\.");//把字符串分割
//			System.out.println(msg);
			
			data = "{\"sender\":\"" + CxGlobalParams.getInstance().getUserId()
			+ "\",\"id\": " + id
			+ ",\"type\": \"emotion\", \"emotion\": \"" + split[0]+"."+split[3]
			+"\",\"category_id\":"+split[1]
			+",\"image_id\":"+split[2]
			+ ",\"create_time\":"
			+ (int) (System.currentTimeMillis() / 1000)
			+ ",\"send_success\": " + sendState + "}";
			
//			System.out.println(data);
		}else if(type.equals("tabloid")){
			data = "{\"type\":\"tabloid\""
					+ ",\"id\":" + id
					+ ",\"text\":" + msg
					+ ",\"create_time\":" + (int)(System.currentTimeMillis()/1000)
					+ "}";
		}else if(type.equals("animation")){
			String[] split = msg.split("\\,");		//把字符串分割 (长度可能是2，也可能是3。如果为3，则表示对方版本不支持)
			
			data = "{\"sender\":\"" + CxGlobalParams.getInstance().getUserId()
					+ "\",\"id\":" + id
					+ ",\"type\":\"animation\""
					+ ",\"animation_id\":" + split[0]
					+ ",\"effect\":" + split[1]
					+ ",\"shake_weight\":0" 					// 摇晃力度(暂时没用到，先写死)
					+ ",\"create_time\":" + (int)(System.currentTimeMillis()/1000)
					+ ",\"send_success\": " + sendState ;
				
				// 判断对方版本是否支持抽鞭子
				if(split.length==3){
					data = data + ",\"is_support\":false";
				}
			
			data = data		+ "}";
		}else if(type.equals("guess_request")){
			String[] split = msg.split("\\,");	
			
			data = "{\"type\":\"guess_request\""
					+",\"sender\":\"" + CxGlobalParams.getInstance().getUserId()
					+ "\",\"id\":" + id
					+ ",\"guess_id\":" + split[0]
					+ ",\"value1\":" + split[1]
					+ ",\"create_time\":" + (int)(System.currentTimeMillis()/1000)
					+ ",\"send_success\":" + sendState ;
			
					// 判断对方版本是否支持抽鞭子
					if(split.length==3){
						data = data + ",\"is_support\":false";
					}
			
					data = data + "}";			
		}else if(type.equals("guess_response")){
			String[] split = msg.split("\\,");	
			
			data = "{\"type\":\"guess_response\""
					+ ",\"sender\":\"" + CxGlobalParams.getInstance().getUserId()
					+ "\",\"id\":" + id
					+ ",\"guess_id\":" + split[1]
					+ ",\"value1\":" + split[2]
					+ ",\"value2\":" + split[3]
					+ ",\"result\":" + split[4]
					+ ",\"create_time\":" + (int)(System.currentTimeMillis()/1000)
					+ ",\"send_success\":" + sendState ;
			
					// 判断对方版本是否支持抽鞭子
					if(split.length==6){
						data = data + ",\"is_support\":false";
					}
			
					data = data + "}";	
		}else {
			data = "{\"sender\":\"" + CxGlobalParams.getInstance().getUserId()
					+ "\",\"id\": " + id
					+ ",\"type\": \"text\", \"text\": \"" + msg
					+ "\",\"create_time\":"
					+ (int) (System.currentTimeMillis() / 1000)
					+ ",\"send_success\": " + sendState + "}";
		}

		CxLog.d(TAG, "buildMessageObject() with data=" + data);
		JSONObject obj = null;
		try {
			obj = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return buildMessageObject(obj);
	}
	/**
	 * 构建消息对象
	 * @param msg
	 * @param type
	 * @param lat
	 * @param lon
	 * @param sendState  发送状态 0，发送中 1.发送成功 2，发送失败
	 * @param id
	 * @return
	 */
	public static Message buildMessageObject(String msg, String type, float lat, float lon,
			int sendState, long id) {
		String data = null;
		if (type.equals("geo")) {
			data = "{\"sender\":\"" + CxGlobalParams.getInstance().getUserId()
					+ "\",\"id\": " + id
					+ ",\"type\": \"geo\", \"text\": \"" + msg
					+ "\",\"create_time\":"
					+ (int) (System.currentTimeMillis() / 1000)
					+ ",\"lat\": " + lat
					+ ",\"lon\": " + lon
					+ ",\"send_success\": " + sendState + "}";
		} else {
			data = "{\"sender\":\"" + CxGlobalParams.getInstance().getUserId()
					+ "\",\"id\": " + id
					+ ",\"type\": \"text\", \"text\": \"" + msg
					+ "\",\"create_time\":"
					+ (int) (System.currentTimeMillis() / 1000)
					+ ",\"send_success\": " + sendState + "}";
		}
		
		CxLog.d(TAG, "buildMessageObject() with data=" + data);
		JSONObject obj = null;
		try {
			obj = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return buildMessageObject(obj);
	}

	public static Message buildMessageObject(JSONObject data) {
		if (null == data) { //add by shichao
			return null;
		}
		
		String type = null;
		try {
			type = data.getString(TAG_TYPE);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		if (type.equals("audio")) {
			return new VoiceMessage(data, mContext);
		} else if (type.equals("photo")) {
			return new PictureMessage(data, mContext);
		} else if (type.equals("phrase")) {
			return new FastPhraseMessage(data, mContext);
		} else if (type.equals("face")) {
			return new FaceMessage(data, mContext);
		} else if (type.equals("geo")) {
			return new LocationMessage(data, mContext);
		} else if (type.equals("feed")) {
			return new FeedMessage(data, mContext);
		} else if(type.equals("welcome")){
			return new WelcomeMessage(data, mContext);
		} else if(type.equals("emotion")){
			return new EmotionMessage(data, mContext);//封装data为message
		} else if(type.equals("tabloid")){
			return new TabloidMessage(data, mContext);
		} else if(type.equals("animation")){
			return new AnimationMessage(data, mContext);
		} else if(type.equals("guess_request")){
			return new GuessRequestMessage(data, mContext);
		}else if(type.equals("guess_response")){
			return new GuessResponseMessage(data, mContext);
		}else if(type.equals("system")){
			return new SystemMessage(data, mContext);
		} else {
			return new TextMessage(data, mContext);
		}
	}
}
