
package com.chuxin.family.net;

import com.chuxin.family.parse.CxKidParser;
import com.chuxin.family.parse.CxKidsInfoParser;
import com.chuxin.family.parse.been.CxKidFeedList;
import com.chuxin.family.parse.been.CxKidsInfoData;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.settings.CxFile;
import com.chuxin.family.utils.CxLog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CxKidApi extends ConnectionManager {

    private final String CHILD_INFO_UPDATE = HttpApi.HTTP_SERVER_PREFIX + "Child/update";

    private final String CHILD_INFO_DELETE = HttpApi.HTTP_SERVER_PREFIX + "Child/delete";

    private final String KID_LIST = HttpApi.HTTP_SERVER_PREFIX + "Child/feed/list"; // 孩子空间列表

    private final String KID_ADD_FEED = HttpApi.HTTP_SERVER_PREFIX + "Child/feed/post"; // 添加帖子

    private final String KID_DELETE_FEED = HttpApi.HTTP_SERVER_PREFIX + "Child/feed/delete"; // 删除帖子

    private final String KID_ADD_REPLY = HttpApi.HTTP_SERVER_PREFIX + "Child/reply/post"; // 添加回复

    private final String KID_DELETE_REPLY = HttpApi.HTTP_SERVER_PREFIX + "Child/reply/delete"; // 删除回复

    // private final String SHARE_TO_THIRD = HttpApi.HTTP_SERVER_PREFIX +
    // "Share/feed"; //分享到第三方

    private CxKidApi() {
    };

    private static CxKidApi api;

    public static CxKidApi getInstance() {
        if (null == api) {
            api = new CxKidApi();
        }
        return api;
    }

    /**
     * 更新备忘资料
     * 
     * @param id -孩子id（为空时为新增）
     * @param wholename - 全名
     * @param nickname - 昵称
     * @param gender - 性别
     * @param birth - 生日 (型如 19800731 string)
     * @param note - 备注
     * @param data - 其他数据
     * @param avata - 图片文件
     */
    public void updateKidInfo(String id, String wholename, String nickname, String gender,
            String birth, String note, String data, String avata, final Context ctx,
            final JSONCaller callback) throws Exception {
        CxLog.i("updateKidInfo", "wholename=" + wholename);
        CxLog.i("updateKidInfo", "nickname=" + nickname);
        CxLog.i("updateKidInfo", "gender=" + gender);
        CxLog.i("updateKidInfo", "birth=" + birth);
        CxLog.i("updateKidInfo", "note=" + note);
        CxLog.i("updateKidInfo", "data=" + data);
        CxLog.i("updateKidInfo", "avata=" + avata);
        CxLog.i("updateKidInfo", "id=" + id);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        // Map<String, String> params = new HashMap<String, String>();

        if (id != null) {
            params.add(new BasicNameValuePair("id", id));
        }

        if (wholename != null) {
            params.add(new BasicNameValuePair("name", wholename));
        }
        if (nickname != null) {
            params.add(new BasicNameValuePair("nickname", nickname));
        }

        if (gender != null) {
            params.add(new BasicNameValuePair("gender", gender));
        }

        if (!TextUtils.isEmpty(birth) && !birth.equalsIgnoreCase("0")) {
            params.add(new BasicNameValuePair("birth", birth));
        }

        if (note != null) {
            params.add(new BasicNameValuePair("note", note));
        }

        // 注意，此处不能用: "!TextUtils.isEmpty(data)". 原因: 后端会判断，如果为null，则不会更改此字段
        if (null != data) {
            params.add(new BasicNameValuePair("data", data));
        }

        if (0 == params.size()) {
            throw new Exception("all params can not be null");
        }

        final String url = CHILD_INFO_UPDATE;

        JSONCaller caller = new JSONCaller() {

            @Override
            public int call(Object result) {
                // 解析
                if (null == result) {
                    callback.call(null);
                    return -1;
                }
                CxLog.i("CxKidApi_men", result.toString());
                CxKidsInfoParser parser = new CxKidsInfoParser();
                CxKidsInfoData data = parser.parserForKidInfo(result, ctx, false);
                callback.call(data);
                return 0;
            }
        };

        this.doHttpPost(url, caller, params);
    }

    /**
     * 更新备忘资料
     * 
     * @param id -孩子id（为空时为新增）
     * @param wholename - 全名
     * @param nickname - 昵称
     * @param gender - 性别
     * @param birth - 生日 (型如 19800731 string)
     * @param note - 备注
     * @param data - 其他数据
     * @param avata - 图片文件
     */
    public void updateKidInfoAvata(String id, String wholename, String nickname, String gender,
            String birth, String note, String data, String avata, final Context ctx,
            final JSONCaller callback) throws Exception {
        CxLog.i("updateKidInfoAvata", "wholename=" + wholename);
        CxLog.i("updateKidInfoAvata", "nickname=" + nickname);
        CxLog.i("updateKidInfoAvata", "gender=" + gender);
        CxLog.i("updateKidInfoAvata", "birth=" + birth);
        CxLog.i("updateKidInfoAvata", "note=" + note);
        CxLog.i("updateKidInfoAvata", "data=" + data);
        CxLog.i("updateKidInfoAvata", "avata=" + avata);
        CxLog.i("updateKidInfoAvata", "id=" + id);
        // List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        Map<String, String> params = new HashMap<String, String>();

        if (TextUtils.isEmpty(avata)) {
            throw new Exception("avata image can not null");
        }
        if (!new File(avata).exists()) {
            throw new Exception("avata image can not reach or exitst");
        }

        if (id != null) {
            params.put("id", id);
        }

        final String url = CHILD_INFO_UPDATE;

        JSONCaller call = new JSONCaller() {

            @Override
            public int call(Object result) {
                // 解析
                if (null == result) {
                    callback.call(null);
                    return -1;
                }
                CxLog.i("CxKidApi_men", result.toString());
                CxKidsInfoParser parser = new CxKidsInfoParser();
                CxKidsInfoData data = parser.parserForKidInfo(result, ctx, false);
                callback.call(data);
                return 0;
            }
        };

        List<CxFile> images = new ArrayList<CxFile>();
        images.add(new CxFile(avata, "avata", "image/jpg"));
        try {
            CxSendImageApi.getInstance().sendMultTypeData(url, params, images, call);
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(null);
        }
    }

    /**
     * 删除孩子资料
     * 
     * @param id 孩子id
     * @param callback
     */
    public void doDeleteKid(final String id, final Context ctx,
            final ConnectionManager.JSONCaller callback) {

        NameValuePair[] params = {
            new BasicNameValuePair("id", id),
        };

        final String url = CHILD_INFO_DELETE;
        doHttpGet(url, new ConnectionManager.JSONCaller() {

            @Override
            public int call(Object result) {
                if (null == result) {
                    callback.call(null);
                    return -1;
                }
                CxLog.i("CxKidApi_men", result.toString());
//                CxKidsInfoParser parser = new CxKidsInfoParser();
//                CxKidsInfoData data = parser.parserForKidsInfo(result, ctx, false);
                callback.call(result);

                return 0;
            }
        }, params);
    }

    /**
     * 孩子空间列表
     * 
     * @param offset
     * @param limit
     * @param callback
     * @param ctx
     */
    public void requestFeedList(final int offset, final int limit, final JSONCaller callback,
            final Context ctx) {
        JSONCaller netCallback = new JSONCaller() {

            @Override
            public int call(Object result) {
                JSONObject jObj = null;
                try {
                    jObj = (JSONObject)result;
                } catch (Exception e) {
                }
                if (null == result) {
                    callback.call(null);
                    return -1;
                }
                CxLog.i("CxKidApi_men", jObj.toString());

                CxKidParser parser = new CxKidParser();
                CxKidFeedList feedList = null;
                try {
                    feedList = parser.getKidHomeList(offset, jObj, ctx);

                } catch (Exception e) {
                }
                callback.call(feedList);
                return 0;
            }
        };

        this.doHttpGet(KID_LIST, netCallback, new BasicNameValuePair("offset", "" + offset),
                new BasicNameValuePair("limit", "" + limit));

    }

    /**
     * 添加帖子
     * 
     * @param text
     * @param photos
     * @param type
     * @param group_id
     * @param open
     * @param callback
     * @throws Exception
     */
    public void requestAddFeed(String text, List<String> photos, int sync_space, int sync_group,
            int open, final JSONCaller callback) throws Exception {
        if ((null == text) && ((null == photos) || (photos.size() < 1))) {
            throw new Exception("nb parameters can not be both null");
        }

        List<CxFile> images = new ArrayList<CxFile>();
        if (photos != null && photos.size() > 0) {
            for (int i = 0; i < photos.size(); i++) {
                String fileStr = photos.get(i).replace("file://", "");
                CxFile tempFile = new CxFile(fileStr, "photo" + i, null);
                images.add(tempFile);
            }
        }
        JSONCaller call = new JSONCaller() {

            @Override
            public int call(Object result) {
                if (null == result) {
                    callback.call(-1);
                    return -1;
                }
                try {
                    JSONObject obj = (JSONObject)result;
                    CxLog.i("CxKidApi_men", obj.toString());
                    callback.call(obj);
                    CxLog.i("111", "ok");
                } catch (Exception e) {
                    callback.call(null);
                }

                return 0;
            }
        };

        // 经纬度现在暂时不传
        CxSendImageApi.getInstance().sendShareInKid(text, images, null, null, sync_space,
                sync_group, open, call);

    }

    /**
     * 删除帖子
     * 
     * @param id
     * @param callback
     */
    public void requestDeleteFeed(String id, final JSONCaller callback) {

        JSONCaller deleteCallback = new JSONCaller() {

            @Override
            public int call(Object result) {
                if (null == result) {
                    callback.call(null);
                    return -1;
                }
                CxLog.i("CxKidApi_men", result.toString());
                try {
                    JSONObject obj = (JSONObject)result;
                    int rc = -1;
                    rc = obj.getInt("rc");
                    CxParseBasic deleteResult = new CxParseBasic();
                    deleteResult.setRc(rc);
                    if(!obj.isNull("msg")){
                    	deleteResult.setMsg(obj.getString("msg"));
                    }
                    if(!obj.isNull("ts")){
                    	deleteResult.setTs(obj.getInt("ts"));
                    }
                    callback.call(deleteResult);
                } catch (Exception e) {
                    callback.call(null);
                }

                return 0;
            }
        };

        this.doHttpGet(KID_DELETE_FEED, deleteCallback, new BasicNameValuePair("id", id));
    }

    /**
     * 回复帖子
     * 
     * @param feed_id ，帖子ID
     * @param text ，文字信息
     * @param reply_to ，回复用户的ID
     * @param extra ，表情
     */
    public void requestAddReply(String feed_id, String type, String text, String audio,
            int audioLength, String reply_to, final JSONCaller callback) throws Exception {
        if (null == feed_id) {
            throw new Exception("feed id can not be null");
        }
        if ((null == text) && (null == audio)) {
            throw new Exception("text and extra can not be both null");
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("feed_id", feed_id);
        params.put("type", type);

        if (audioLength > 0) {
            params.put("audio_len", audioLength + "");
        }
        if (null != text) {
            params.put("text", text);
        }
        if (null != reply_to) {
            params.put("reply_to", reply_to);
        }

        JSONCaller call = new JSONCaller() {

            @Override
            public int call(Object data) {
                // 解析
            	if (null == data) {
                    callback.call(null);
                    return -1;
                }
            	CxLog.i("CxKidApi_men", data.toString());  
                try {
                	JSONObject result = (JSONObject)data;
                    int rc = result.getInt("rc");
                    if (rc != 0) {
                        callback.call(result);
                        return 0;
                    }
                    CxKidParser sendParser = new CxKidParser();
                    callback.call(sendParser.getAddReplyResult(result));
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };

        List<CxFile> files = new ArrayList<CxFile>();

        if (null != audio) {
            if (new File(audio).exists()) {
                files.add(new CxFile(audio, "audio", "audio/amr"));
            }
        }
        try {
            CxSendImageApi.getInstance().sendMultTypeData(KID_ADD_REPLY, params, files, call);
        } catch (Exception e) {
            e.printStackTrace();
            CxLog.i("CxKidApi_men", ">>>>>>>>>>>1");  
            callback.call(null);
        }

    }

    /**
     * 删除回复
     * 
     * @param id,回复的ID
     * @param callback
     * @throws Exception
     */
    public void requestDeleteReply(String id, final JSONCaller callback) throws Exception {
        if (TextUtils.isEmpty(id)) {
            throw new Exception("delete id can not be null");
        }
        JSONCaller deleteReplyBack = new JSONCaller() {

            @Override
            public int call(Object result) {
                if (null == result) {
                    callback.call(null);
                    return -1;
                }
                CxLog.i("CxKidApi_men", result.toString());
                try {
                    JSONObject obj = (JSONObject)result;
                    int rc = -1;
                    rc = obj.getInt("rc");
                    CxParseBasic deleteResult = new CxParseBasic();
                    deleteResult.setRc(rc);
                	if(!obj.isNull("msg")){
                		deleteResult.setMsg(obj.getString("msg"));
                	}
                	if(!obj.isNull("ts")){
                		 deleteResult.setTs(obj.getInt("ts"));
                	}             
                    callback.call(deleteResult);
                } catch (Exception e) {
                    callback.call(null);
                }

                return 0;
            }
        };

        this.doHttpGet(KID_DELETE_REPLY, deleteReplyBack, new BasicNameValuePair("id", id));
    }

    /**
     * 分享到第三方: 微信 qq 统一使用CxZoneApi中的该方法
     * 
     * @param type
     * @param feed_id
     * @param callback
     * @throws Exception
     */
    // public synchronized void sendShareRequest(String type,
    // String feed_id, final JSONCaller callback) throws Exception{
    // if ((null == type) || (null == feed_id) || (null == callback)) {
    // throw new Exception("any param can not be null");
    // }
    // List<NameValuePair> params = new ArrayList<NameValuePair>();
    // params.add(new BasicNameValuePair("type", type));
    // params.add(new BasicNameValuePair("feed_id", feed_id));
    // JSONCaller caller = new JSONCaller() {
    //
    // @Override
    // public int call(Object result) {
    // JSONObject obj = null;
    // obj = (JSONObject)result;
    // if (null == obj) {
    // callback.call(null);
    // }
    // try {
    // CxZoneParser parser = new CxZoneParser();
    // CxShareThdRes res = parser.parseForShare(obj);
    // callback.call(res);
    // } catch (Exception e) {
    // e.printStackTrace();
    // callback.call(null);
    // }
    // return 0;
    // }
    // };
    //
    // this.doHttpGet(SHARE_TO_THIRD, caller, params);
    // }

}
