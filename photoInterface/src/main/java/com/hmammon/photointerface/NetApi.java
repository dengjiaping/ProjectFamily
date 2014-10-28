package com.hmammon.photointerface;

import android.content.Context;

import com.hmammon.photointerface.domain.Entity;
import com.hmammon.photointerface.domain.ErrorInfo;
import com.hmammon.photointerface.domain.UploadInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 网络交互工具类
 * Created by Xcfh on 2014/10/16.
 */
public class NetApi {
    private static NetApi instace = null;
    private ExecutorService threadPool;
    private NetStateApi netStateApi;

    private NetApi(Context context) {
        netStateApi = NetStateApi.getInstance(context);
        threadPool = Executors.newCachedThreadPool();
    }

    /**
     * 获取一个NET交互类实例
     *
     * @param context 上下文对象
     * @return NET交互类
     */
    public static NetApi getInstace(Context context) {
        if (instace == null) instace = new NetApi(context);
        return instace;
    }

    /**
     * 发送请求接口，会自动根据状态码和返回JSON来判断是否成功
     */
    public interface SendListener {
        public void onSuccess(JSONObject jsonObject);

        public void onFailed(ErrorInfo errorInfo);
    }

    /**
     * 发送文字信息
     *
     * @param jsonEntity 发送参数
     * @param uri        接口URL
     * @param listener   监听接口
     */
    public void send(Entity jsonEntity, URI uri, SendListener listener) {
        NetStateApi.NetStateInfo stateInfo = netStateApi.getNetStateInfo();
        if (!stateInfo.isNetAvailable()) {
            if (listener != null) {
                ErrorInfo errorInfo = new ErrorInfo();
                errorInfo.setState("No Connection");
                errorInfo.setMsg("No Connection");
                listener.onFailed(errorInfo);
            }
        }
        threadPool.submit(new SendThread(jsonEntity, uri, listener));
    }

    private class SendThread implements Callable<Boolean> {
        private SendListener listener;
        private Entity jsonEntity;
        private URI uri;

        private SendThread(Entity jsonEntity, URI uri, SendListener listener) {
            this.listener = listener;
            this.jsonEntity = jsonEntity;
            this.uri = uri;
        }

        @Override
        public Boolean call() throws Exception {
            DefaultHttpClient httpClient = new DefaultHttpClient();//使用手动合体的Apache的Httpmime 4.3.5的jar包
            HttpPost post = new HttpPost();
            post.setURI(uri);
            post.addHeader(HTTP.CHARSET_PARAM, HTTP.UTF_8);
            String str;
            try {
                str = jsonEntity.beJson().toString();
                StringEntity entity = new StringEntity(str, HTTP.UTF_8);
                post.setEntity(entity);
                HttpResponse response = httpClient.execute(post);
                if (response.getStatusLine().getStatusCode() == 200) {
                    InputStreamReader inputStreamReader = new InputStreamReader(response.getEntity().getContent(), HTTP.UTF_8);
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    JSONObject responseJson = new JSONObject(reader.readLine());
                    ZedLog.i(this, "JSON ; " + responseJson.toString());
                    if (listener != null) {
                        if (responseJson.has("state") && responseJson.getString("state").contains("error")) {//判定失败的条件是json里有个"state":"error"
                            listener.onFailed(new ErrorInfo().beObject(responseJson));
                        } else {
                            listener.onSuccess(responseJson);
                        }
                    }

                } else {
                    if (listener != null) {
                        ErrorInfo errorInfo = new ErrorInfo();
                        errorInfo.setState("Connect Failed");
                        errorInfo.setMsg("" + response.getStatusLine().getStatusCode());
                        listener.onFailed(errorInfo);
                    }
                }
            } catch (JSONException | IOException e) {
                if (listener != null) {
                    ErrorInfo errorInfo = new ErrorInfo();
                    errorInfo.setState("Exception");
                    errorInfo.setMsg(e.getLocalizedMessage());
                    listener.onFailed(errorInfo);
                }
            }
            return true;
        }
    }

    /**
     * 上传文件和发送参数信息
     *
     * @param entity   发送参数
     * @param listener 监听接口
     */
    public void upload(Entity entity, SendListener listener) {
        NetStateApi.NetStateInfo stateInfo = netStateApi.getNetStateInfo();
        if (!stateInfo.isNetAvailable()) {
            if (listener != null) {
                ErrorInfo errorInfo = new ErrorInfo();
                errorInfo.setState("No Connection");
                errorInfo.setMsg("No Connection");
                listener.onFailed(errorInfo);
            }
        }
        threadPool.submit(new UploadThread(entity, listener));
    }

    public class UploadThread implements Callable<Boolean> {

        private Entity uploadInfoEntity;

        private SendListener listener;

        public UploadThread(Entity uploadInfoEntity, SendListener listener) {
            this.uploadInfoEntity = uploadInfoEntity;
            this.listener = listener;
        }

        @Override
        public Boolean call() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost();

            post.setURI(URI.create(Constants.URI_UPLOAD));
            post.addHeader(HTTP.CHARSET_PARAM, HTTP.UTF_8);
            try {
                HttpEntity entity = MultipartEntityBuilder.create()
                        .addTextBody("data", uploadInfoEntity.beJson().toString())
                        .addPart("photo", new FileBody(((UploadInfo) uploadInfoEntity).getUploadFile()))
                        .build();// Httpmime下的多数据类型传输封装
                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                if (response.getStatusLine().getStatusCode() == 200) {
                    InputStreamReader inputStreamReader = new InputStreamReader(response.getEntity().getContent(), HTTP.UTF_8);
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    JSONObject responseJson = new JSONObject(reader.readLine());
                    if (listener != null) {
                        if (responseJson.has("state") && responseJson.getString("state").contains("error")) {
                            listener.onFailed(new ErrorInfo().beObject(responseJson));
                        } else {
                            listener.onSuccess(responseJson);
                        }
                    }
                    ZedLog.i(this, "JSON ; " + responseJson.toString());
                } else {
                    if (listener != null) {
                        ErrorInfo errorInfo = new ErrorInfo();
                        errorInfo.setState("Connect Failed");
                        errorInfo.setMsg("" + response.getStatusLine().getStatusCode());
                        listener.onFailed(errorInfo);
                    }
                }
            } catch (JSONException | IOException e) {
                if (listener != null) {
                    ErrorInfo errorInfo = new ErrorInfo();
                    errorInfo.setState("Exception");
                    errorInfo.setMsg(e.getLocalizedMessage());
                    listener.onFailed(errorInfo);
                }
            }
            return true;
        }
    }
}
