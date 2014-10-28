package com.chuxin.family.net;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.parse.CxPollingMessageStatusParser;
import com.chuxin.family.parse.been.CxPollingMessageStatus;
import com.chuxin.family.service.CxServiceParams;
import com.chuxin.family.service.ServiceCallback;
import com.chuxin.family.utils.CxLog;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LongPollingApi {

    private static LongPollingApi longPolling;
    private final static String LONG_POLLING_URL = HttpApi.HTTP_SERVER_PREFIX + "check";
    private final static String CHAT_TS = "chat_ts";
    private final static String EDIT_TS = "edit_ts";
    private final static String RTS = "rts";
    private final static String PAIR = "pair";
    private final static String MATCH = "match";
    private final static String SPACE_TS = "space_ts";
    private final static String GROUP = "group";
    private final static String SPACE_TIPS = "space_tips";
    private final static String SINGLE_MODE = "single_mode";
    private final static String CALENDAR_TS = "calendar_ts";
    private final static String KID_TIPS = "child_tips";
    public final String DEFAULT_CLIENT_VERSION = "com.joelapenna.foursquare";
    public final String CLIENT_VERSION_HEADER = "Owner-Agent";

    private HttpGet mGetRequest;
    private DefaultHttpClient mClient;

    private LongPollingApi() {
        mClient = HttpApi.getmHttpClient();
        HttpParams params = mClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
        HttpConnectionParams.setSoTimeout(params, 180 * 1000);
        mClient.setParams(params);
    }

    public static LongPollingApi getInstance() {
        /*if (!RkGlobalParams.getInstance().isLoginNetSuccess()) {
			return null;
		}*/

        if (null == longPolling) {
            longPolling = new LongPollingApi();
        }

        return longPolling;
    }

    /**
     * 访问CHECK api，取回longpolling的各种状态： 目前有6参数（如下）： chat_ts:
     * 已接受的最大一条消息的消息ID（首次调用可输入0） edit_ts: 已知对方的编辑状态ID（首次调用可输入0） rts:
     * 提醒项的状态(删除、添加，修改）（时间戳） pair: 结对标记（已结对为0，未结对为1） match:
     * 未结对情况下，是否有匹配的可结对项（在结对的界面等待或输入对方手机号码时） space_ts: 二人空间更新的状态
     */
    public synchronized boolean pollingStatus(final ServiceCallback callback) {
        if (null == callback) {
            return false;
        }

        // 如果应用在后台
        if (!CxGlobalParams.getInstance().isAppStatus()) {
            callback.complete = true;
            callback.call(null);
            return false;
        }

        CxServiceParams param = CxServiceParams.getInstance();

        NameValuePair[] params = {
                new BasicNameValuePair(CHAT_TS, "" + param.getChatTs()),
                new BasicNameValuePair(EDIT_TS, "" + param.getEditTs()),
                new BasicNameValuePair(RTS, "" + param.getRts()),
                new BasicNameValuePair(PAIR, ""
                        + CxGlobalParams.getInstance().getPair()),
                new BasicNameValuePair(MATCH, "" + param.getMatch()),
                new BasicNameValuePair(SPACE_TS, "" + param.getSpaceTs()),
                new BasicNameValuePair(GROUP, "" + param.getGroup()),
                new BasicNameValuePair(SPACE_TIPS, "" + param.getSpace_tips()),
                new BasicNameValuePair(SINGLE_MODE, "" + CxGlobalParams.getInstance().getSingle_mode()),
                new BasicNameValuePair(CALENDAR_TS, "" + param.getCalendarTs()),
                new BasicNameValuePair(KID_TIPS, "" + CxGlobalParams.getInstance().getKid_tips()),

        };

        CxLog.i("longpolling send-----", "pair=" + CxGlobalParams.getInstance().getPair()
                + ",chat_ts=" + param.getChatTs() + ",space_ts="
                + param.getSpaceTs() + ",rts=" + param.getRts() + ",match=" + param.getMatch()
                + ", group=" + param.getGroup() + ",space_tips=" + param.getSpace_tips() + "calendar_ts=" + param.getCalendarTs());

        if (null != mGetRequest) {
            try {
                mGetRequest.abort();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mGetRequest = null;
        }

        try {
            mGetRequest = createHttpGet(LONG_POLLING_URL, params);
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        if (null == mGetRequest) {
            return false;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject obj = null;
                try {
                    obj = executeHttpRequest(mGetRequest);
                } catch (Exception e1) {
                    CxLog.e("long polling", "" + e1.getMessage());
                }

//				RkLog.e("long polling, server back data", "" +obj.toString());

                if (null == obj) {
                    callback.complete = true;
                    callback.call(null);
                    return;
                }
                CxLog.i("LongPollingApi_men", obj.toString());
                CxPollingMessageStatusParser parser = new CxPollingMessageStatusParser();
                CxPollingMessageStatus status = new CxPollingMessageStatus();
                status.setRc(1);
                try {
                    status = parser.parse(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callback.complete = true;
                callback.call(status);
            }
        }).start();

        return true;
    }

    public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs) {
        String query = URLEncodedUtils.format(stripNulls(nameValuePairs),
                HTTP.UTF_8);
        HttpGet httpGet = new HttpGet(url + "?" + query);
        httpGet.addHeader(CLIENT_VERSION_HEADER, DEFAULT_CLIENT_VERSION);
        return httpGet;
    }

    private List<NameValuePair> stripNulls(NameValuePair... nameValuePairs) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (NameValuePair param : nameValuePairs) {
            if (param.getValue() != null) {
                params.add(param);
            }
        }
        return params;
    }

    public JSONObject executeHttpRequest(HttpRequestBase httpRequest)
            throws Exception {

        HttpResponse response;
        try {
            //RkLog.i("long polling send ", "out");
//            if (httpRequest.isAborted()) return new JSONObject("{\"rc\":408}");
            response = mClient.execute(httpRequest);
            //RkLog.i("long polling send ", "out complete");
        } catch (IOException e) {
            CxLog.i("long polling send ", " happen error " + e.getMessage());
            e.printStackTrace();
            return new JSONObject("{\"rc\":408}");
        }

        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case 200:
                String content = EntityUtils.toString(response.getEntity());
                return new JSONObject(content);

            case 400:
                response.getEntity().consumeContent();
                return new JSONObject("{\"rc\":400}");

            case 401:
                response.getEntity().consumeContent();
                return new JSONObject("{\"rc\":401}");

            case 404:
                response.getEntity().consumeContent();
                return new JSONObject("{\"rc\":404}");

            case 500:
                response.getEntity().consumeContent();
                return new JSONObject("{\"rc\":500}");

            default:
                response.getEntity().consumeContent();
                return new JSONObject("{\"rc\":\"" + statusCode + "\"}");
        }
    }

    public void disposeRequest() {
        if (null != mGetRequest && (!mGetRequest.isAborted())) {
            try {
                mGetRequest.abort();
                CxLog.w("long polling", "abort request network");
            } catch (Exception e) {
                CxLog.e("long polling cancel", "" + e.getMessage());
            }
        } else {
            CxLog.w("long polling", "request is null or abort ");
        }
    }

}
