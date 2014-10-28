package com.chuxin.family.net;

import com.chuxin.family.app.CxApplication;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.parse.CxUserInfoParse;
import com.chuxin.family.parse.been.CxUserProfile;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxUserProfileKeeper;
import com.chuxin.family.views.login.CxThirdAccessToken;
import com.chuxin.family.views.login.CxThirdAccessTokenKeeper;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLHandshakeException;

public class HttpApi {
    protected static final String TAG = "HttpApi";


//	public static final String HTTP_SERVER_PREFIX = "http://api.family.rekoo.net/";// 外网官方

    public static String HTTP_SERVER_PREFIX;//本地测试

    static {
        if(CxGlobalConst.DEVELOPER_MODE){
            HTTP_SERVER_PREFIX = "http://192.168.3.30:441/";
        }else{
            HTTP_SERVER_PREFIX = "http://apifamily.hmammon.cn/";
            //http://api.family.rekoo.net/
        }
    }

//	 public static final String HTTP_SERVER_PREFIX = "http://192.168.2.193:441/"; //内网测试服务器

//	 public static final String HTTP_SERVER_PREFIX = "http://59.108.111.79:441/"; //内网映射测试服务器

//	 public static final String HTTP_SERVER_PREFIX = "http://192.168.0.128:441/"; //志超模拟数据测试服务器

//	 public static final String HTTP_SERVER_PREFIX = "http://203.195.184.92/"; //沙盒数据测试服务器

    private static final String DEFAULT_CLIENT_VERSION = "com.chuxin.family";
    private static final String CLIENT_VERSION_HEADER = "Owner-Agent";
    private static final int TIMEOUT = 15; // (2013.09.03建银建议long
    // polling修改这两个参数,连接超时时间是20秒，读取超时是3分钟）
    private static final int READ_TIME_OUT = 10; // 将read time out值改大

    private static DefaultHttpClient mHttpClient;

    public static DefaultHttpClient getmHttpClient() {
        return mHttpClient;
    }

    private final String mClientVersion;


    public HttpApi(DefaultHttpClient httpClient, String clientVersion) {
        mHttpClient = httpClient;
        mHttpClient.setHttpRequestRetryHandler(sRequestRetryHandler);
        if (clientVersion != null) {
            mClientVersion = clientVersion;
        } else {
            mClientVersion = DEFAULT_CLIENT_VERSION;
        }
    }

    public JSONObject executeHttpRequest(HttpRequestBase httpRequest)
            throws Exception {
        CxLog.i(TAG, "do HttpRequest: " + httpRequest.getURI());

        HttpResponse response = null;
        try {
            response = doExecuteHttpRequest(httpRequest);
        } catch (ConnectTimeoutException cte) {
            CxLog.i("http request error ", "" + cte.getMessage());
            return new JSONObject("{\"rc\":409}");
        } catch (SocketTimeoutException ste) {
            CxLog.i("http request error ", "" + ste.getMessage());
            return new JSONObject("{\"rc\":409}");
        } catch (IOException e) {
            CxLog.i("http request error ", "" + e.getMessage());
            return new JSONObject("{\"rc\":408}");
        }
        CxLog.i(TAG, "executed HttpRequest for: "
                + httpRequest.getURI().toString());

        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case 200:
                String content = EntityUtils.toString(response.getEntity());
                JSONObject jObj = new JSONObject(content);
                int rc = jObj.getInt("rc");
                if (CxNetworkResult.NET_FAIL_BY_UNLOGIN == rc) { //为脱网进入应用而设置
                    //这个状态下需要登录
                    final HttpRequestBase reSend = httpRequest;
                    if (networkAutoLogin(CxApplication.getInstance())) { //登录成功
                        return executeHttpRequest(reSend);
                    } else { //登录失败
                        return new JSONObject("{\"rc\":" + CxNetworkResult.NET_FAIL_BY_OTHER + "}");
                    }
                }

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

    public CxNetworkInputstream requestFile(HttpRequestBase request) {
        CxNetworkInputstream rkInput = new CxNetworkInputstream();
        HttpResponse response = null;
        try {
            response = doExecuteHttpRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ((null == response) || (null == response.getEntity())) {
            return null;
        }
        HttpEntity entity = response.getEntity();
        long len = entity.getContentLength();
        InputStream responseStream = null;
        try {
            responseStream = entity.getContent();
        } catch (Exception e1) {
        }
        if (responseStream == null) {
            return null;
        }
        Header header = entity.getContentEncoding();
        if (header == null) {
            rkInput.contentLength = len;
            rkInput.netIs = responseStream;

            return rkInput;
        }
        String contentEncoding = header.getValue();
        if (contentEncoding == null) {
            rkInput.contentLength = len;
            rkInput.netIs = responseStream;
            return rkInput;
        }
        // shichao 腾讯cos返回的头里
        // Content-Encoding是utf-8有问题，应该是压缩方式gzip，default等，没办法暂时给它过掉，腾讯的bug
        if (contentEncoding.contains("utf-8")) {
            rkInput.contentLength = len;
            rkInput.netIs = responseStream;
            return rkInput;
        }

        if (contentEncoding.contains("gzip")) {
            try {
                InputStream unZipStream = new GZIPInputStream(responseStream);
                rkInput.contentLength = len;
                rkInput.netIs = unZipStream;
                return rkInput;
            } catch (IOException e) {
                rkInput.contentLength = len;
                rkInput.netIs = responseStream;
                return rkInput;
            }
        }
        return rkInput;
    }

    public JSONObject doHttpPost(String url, NameValuePair... nameValuePairs)
            throws Exception {
        Log.d(TAG, "doHttpPost: " + url);
        final HttpPost httpPost = createHttpPost(url, nameValuePairs);

        HttpResponse response = null;
        try {
            response = doExecuteHttpRequest(httpPost);
        } catch (ConnectTimeoutException cte) {
            CxLog.i("http request error ", "" + cte.getMessage());
            return new JSONObject("{\"rc\":409}");
        } catch (SocketTimeoutException ste) {
            CxLog.i("http request error ", "" + ste.getMessage());
            return new JSONObject("{\"rc\":409}");
        } catch (IOException e) {
            CxLog.i("http request error ", "" + e.getMessage());
            return new JSONObject("{\"rc\":408}");
        }
        Log.d(TAG, "executed HttpRequest for: " + httpPost.getURI().toString());

        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case 200:
                String content = EntityUtils.toString(response.getEntity());
                JSONObject jObj = new JSONObject(content);
                int rc = jObj.getInt("rc");
                if (CxNetworkResult.NET_FAIL_BY_UNLOGIN == rc) { //为脱网进入应用而设置
                    //这个状态下需要登录
                    if (networkAutoLogin(CxApplication.getInstance())) { //登录成功
                        return executeHttpRequest(httpPost);
                    } else { //登录失败
                        return new JSONObject("{\"rc\":" + CxNetworkResult.NET_FAIL_BY_OTHER + "}");
                    }
                }

                return new JSONObject(content);

            case 401:
                response.getEntity().consumeContent();
                return new JSONObject("{\"rc\":401}");

            case 404:
                response.getEntity().consumeContent();
                return new JSONObject("{\"rc\":404}");

            default:
                response.getEntity().consumeContent();
                return new JSONObject("{\"rc\":\"" + statusCode + "\"}");
        }
    }

    /**
     * execute() an httpRequest catching exceptions and returning null instead.
     *
     * @param httpRequest
     * @return
     * @throws IOException
     */
    public HttpResponse doExecuteHttpRequest(HttpRequestBase httpRequest)
            throws IOException {
        try {
            mHttpClient.getConnectionManager().closeExpiredConnections();
            HttpResponse response = mHttpClient.execute(httpRequest);
            return response;
        } catch (IOException e) {
            CxLog.i("url", "" + httpRequest.getURI());
            CxLog.e("request fail reason", "" + e.getMessage());
            httpRequest.abort();
            throw e;
        }
    }

    public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs) {
        String query = URLEncodedUtils.format(stripNulls(nameValuePairs),
                HTTP.UTF_8);
        HttpGet httpGet = new HttpGet(url + "?" + query);
        httpGet.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
        return httpGet;
    }

    public HttpGet createHttpGet(String url, List<NameValuePair> nameValuePairs) {
        String query = URLEncodedUtils.format(nameValuePairs, HTTP.UTF_8);
        HttpGet httpGet = new HttpGet(url + "?" + query);
        httpGet.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
        return httpGet;
    }

    public HttpPost createHttpPost(String url, NameValuePair... nameValuePairs) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
        try {
            if (null != nameValuePairs) { // add by
                // shichao:因为有接口允许参数为空，要避免发"null"字符串的情况
                httpPost.setEntity(new UrlEncodedFormEntity(
                        stripNulls(nameValuePairs), HTTP.UTF_8));
            }
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException(
                    "Unable to encode http parameters.");
        }
        return httpPost;
    }

    public HttpPost createHttpPost(String url, List<NameValuePair> nameValuePairs) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
        try {
            if (null != nameValuePairs) { // add by shichao:因为有接口允许参数为空，要避免发"null"字符串的情况
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            }
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException(
                    "Unable to encode http parameters.");
        }
        return httpPost;
    }

    private List<NameValuePair> stripNulls(NameValuePair... nameValuePairs) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (int i = 0; i < nameValuePairs.length; i++) {
            NameValuePair param = nameValuePairs[i];
            if (param.getValue() != null) {
                params.add(param);
            }
        }
        return params;
    }

    /**
     * Create a thread-safe client. This client does not do redirecting, to
     * allow us to capture correct "error" codes.
     *
     * @return HttpClient
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     */
    public static final DefaultHttpClient createHttpClient() {
        // Sets up the http part of the service.
        final SchemeRegistry supportedSchemes = new SchemeRegistry();

        // Register the "http" protocol scheme, it is required
        // by the default operator to look up socket factories.
        final SocketFactory socketFactory = PlainSocketFactory
                .getSocketFactory();

        // Set some client http client parameter defaults.
        final HttpParams httpParams = createHttpParams();
        HttpClientParams.setRedirecting(httpParams, false);
        HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
        StringBuffer sb = new StringBuffer();
        sb.append(CxGlobalParams.getInstance().getVersionName());
        sb.append(CxGlobalParams.getInstance().getClientVersion());
        sb.append(" (android; android ");
        sb.append(Build.VERSION.RELEASE);
        sb.append("; ");
        sb.append("Scale/");
        sb.append(CxGlobalParams.getInstance().getScale());
        sb.append("; cid=");
        sb.append(CxGlobalParams.getInstance().getCid());
        sb.append(") ");
        sb.append(Locale.getDefault().toString());
        String agentStr = sb.toString();

        HttpProtocolParams.setUserAgent(httpParams, agentStr); // add by shichao

        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SSLSocketFactory sslSocketFactory = null;
        try {
            sslSocketFactory = new IgnoreTrustSSLSocketFactory(trustStore);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sslSocketFactory
                .setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        supportedSchemes.register(new Scheme("http", socketFactory, 80));
        supportedSchemes.register(new Scheme("https", sslSocketFactory, 443));
        final ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                httpParams, supportedSchemes);
        return new DefaultHttpClient(ccm, httpParams);
    }

    /**
     * Create the default HTTP protocol parameters.
     */
    private static final HttpParams createHttpParams() {
        final HttpParams params = new BasicHttpParams();

        // Turn off stale checking. Our connections break all the time anyway,
        // and it's not worth it to pay the penalty of checking every time.
        HttpConnectionParams.setStaleCheckingEnabled(params, false);

        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSoTimeout(params, READ_TIME_OUT * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        return params;
    }

    private static HttpRequestRetryHandler sRequestRetryHandler = new HttpRequestRetryHandler() {

        @Override
        public boolean retryRequest(IOException exception, int executionCount,
                                    HttpContext context) {
            if (executionCount >= 3) {
                CxLog.e("network faild", "beyong 3 times, cancel it");
                return false;
            } else {
                CxLog.e("network faild", "ready to redo request");
            }

            if (exception instanceof NoHttpResponseException) {
                return true;
            }

            if (exception instanceof SSLHandshakeException) {
                return false;
            }

            HttpRequest request = (HttpRequest) context
                    .getAttribute(ExecutionContext.HTTP_REQUEST);
            boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
            if (!idempotent) {
                return true;
            }

            return false;
        }

    };

    private boolean networkAutoLogin(Context ctx) {
        CxLog.w("login fail,", "there is a request, so login proccess-----------------");
        if (null == ctx) {
            return false;
        }
        CxThirdAccessToken localToken =
                CxThirdAccessTokenKeeper.readAccessToken(ctx);
        String plantName = localToken.getPlatName();
        String plantUid = localToken.getUid();
        String plantToken = localToken.getToken();
        if (TextUtils.isEmpty(plantToken)
                || TextUtils.isEmpty(plantToken)
                || TextUtils.isEmpty(plantToken)) {
            return false;
        }

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("via", plantName));
            params.add(new BasicNameValuePair("account", plantUid));
            params.add(new BasicNameValuePair("token", plantToken));
            params.add(new BasicNameValuePair("gender", "" + CxGlobalParams.getInstance().getVersion()));
            HttpPost post = createHttpPost(HTTP_SERVER_PREFIX + "Account/login", params);
            HttpResponse loginResponse = mHttpClient.execute(post);

            int stateCode = loginResponse.getStatusLine().getStatusCode();
            if (200 != stateCode) {
                return false;
            }
            String loginContent = EntityUtils.toString(loginResponse.getEntity());
            JSONObject loginObj = new JSONObject(loginContent);
            int rc = -1;
            try {
                rc = loginObj.getInt("rc");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (0 != rc) {
                return false;
            }
            CxLog.w("net auto login success", "###################");
            /*//开启long polling
            try {
		    	RkGlobalParams.getInstance().setLoginNetSuccess(true);
				Intent rkService = new Intent(ctx, RkBackgroundService.class);
				rkService.putExtra("source", 2);
				ctx.startService(rkService);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
            //登录成功，接下去获取个人资料
            JSONObject dataObj = loginObj.getJSONObject("data");
            String uid = dataObj.getString("uid");
            if (null == uid) {
                return false;
            }
            HttpGet get = createHttpGet(HTTP_SERVER_PREFIX + "User/get", new BasicNameValuePair("uid", uid));
            HttpResponse profileResponse = mHttpClient.execute(get);
            int profileState = -1;
            profileState = profileResponse.getStatusLine().getStatusCode();
            if (0 != profileState) {
                return false;
            }
            String profileString = EntityUtils.toString(profileResponse.getEntity());
            JSONObject profileObj = new JSONObject(profileString);
            CxUserProfile userProfile = CxUserInfoParse.parseForUserInfo(profileObj);
            if ((null == userProfile) || (null == userProfile.getData())) {
                return false;
            }

            //个人数据加入内存
            CxUserProfileKeeper profileKeeper = new CxUserProfileKeeper();
            profileKeeper.saveProfile(userProfile.getData(), ctx);

            return true;
        } catch (Exception e) {
            CxLog.w("", "" + e.toString());
        }

        return false;
    }

}
