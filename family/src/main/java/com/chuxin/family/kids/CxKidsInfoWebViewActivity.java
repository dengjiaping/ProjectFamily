
package com.chuxin.family.kids;

import com.chuxin.family.R;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.net.HttpApi;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.io.Console;
import java.util.List;

/**
 * webview 加载模板
 * 
 * @author shichao.wang
 */
public class CxKidsInfoWebViewActivity extends CxRootActivity {

    private WebView mWebView;

    private String mUrl = "";

    private final String BASEURL = HttpApi.HTTP_SERVER_PREFIX + "Page/child/grow";
//    private final String BASEURL = "http://192.168.2.193:441/Page/child/grow?age=0005&type=1";

    private int mPageFlag = 1; // 1 跳转到健康页 2跳转到教育页
    
    private Dialog mLoadingDialog;
    
    private String mCurrentAge="";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.cx_fa_activity_kids_info_webview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
        Intent intent = getIntent();
        mPageFlag = intent.getIntExtra("redirect_page_id", 1);
        mCurrentAge = intent.getStringExtra("redirect_page_age");
        initTitle();
        init();
    }

    // 初始化标题栏
    private void initTitle() {
        Button backBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
        TextView titleText = (TextView)findViewById(R.id.cx_fa_activity_title_info);
        if (mPageFlag == 1) {
            titleText.setText(getString(R.string.cx_fa_kids_home_health_title_text));
        } else if (mPageFlag == 2) {
            titleText.setText(getString(R.string.cx_fa_kids_home_teach_title_text));
        }
        backBtn.setText(getString(R.string.cx_fa_navi_back));

        backBtn.setOnClickListener(titleListener);
    }

    private void init() {
        mWebView = (WebView)findViewById(R.id.cx_fa_kids_info_webview);

        mWebView.getSettings().setRenderPriority(RenderPriority.HIGH); // improve
                                                                       // render
                                                                       // priority
        mWebView.getSettings().setBlockNetworkImage(false); // image load last
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // mWebView.getSettings().setPluginsEnabled(true); //deprecated
        mWebView.getSettings().setPluginState(PluginState.ON);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVerticalScrollBarEnabled(false); // 控制显示滚动条
        mWebView.setHorizontalScrollBarEnabled(false);

//        mWebView.setBackgroundResource(R.drawable.cx_fa_main_login_launchscreen);
        mWebView.setBackgroundColor(Color.argb(0, 0, 0, 0));

        mWebView.setLongClickable(true);

        mWebView.setWebViewClient(new GameWebViewClient());
        mWebView.setWebChromeClient(new GameWebChromeClient());
//        mWebView.clearCache(true);
        String age = mCurrentAge;
        String type = mPageFlag + "";
        String gender = CxGlobalParams.getInstance().getVersion() + "";
        mUrl = BASEURL + "?age=" + age + "&type=" + type + "&gender=" + gender;
        mWebView.requestFocus();

        CookieStore cs = new BasicCookieStore();
        cs = HttpApi.getmHttpClient().getCookieStore();
        List<Cookie> cookies = cs.getCookies();
        String newCookies = "";
        String domain = "";
        for (Cookie ck : cookies) {
            newCookies = ck.getName() + "=" + ck.getValue() + ";";
            domain = ck.getDomain();
            synCookies(CxKidsInfoWebViewActivity.this, ck);
        }
        mLoadingDialog = DialogUtil.getInstance().getLoadingDialog(CxKidsInfoWebViewActivity.this);
        mLoadingDialog.show();
//        mWebView.loadUrl("http://192.168.2.193:441/Page/child/grow?age=0005&type=1");
        CxLog.i("webview", "url="+mUrl);
         mWebView.loadUrl(mUrl);

    }
    
    private class GameWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            showError();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            CxLog.i("webview", "url="+url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            CookieManager cookieManager = CookieManager.getInstance();
            String CookieStr = cookieManager.getCookie(url);
            CxLog.i("webview", "Cookies = " + CookieStr);
            //mLoadingDialog.getLoadingDialog(CxKisInfoWebViewActivity.this).dismiss();
            super.onPageFinished(view, url);
        }
    }

    private class GameWebChromeClient extends WebChromeClient {
        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            CxLog.i("webview", "message=" + message + "lineNumber=" + lineNumber + "sourceID="
                    + sourceID);
        }
        
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            String message = consoleMessage.message();
            int lineNumber = consoleMessage.lineNumber();
            String sourceID = consoleMessage.sourceId();
            String messageLevel = consoleMessage.message();

            CxLog.i("webview", String.format("[%s] sourceID: %s lineNumber: %n message: %s",
                    messageLevel, sourceID, lineNumber, message));

            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress >= 100) {
                mLoadingDialog.dismiss();
            }
        }
    }

    private OnClickListener titleListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_activity_title_back:
                    back();
                    break;

                default:
                    break;
            }

        }
    };

    protected void back() {
        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    };

    private void showError() {
        DialogUtil clearChatCacheDialog = DialogUtil.getInstance();
        clearChatCacheDialog.setOnSureClickListener(new OnSureClickListener() {

            @Override
            public void surePress() {
                back();
            }
        });
        clearChatCacheDialog.getSimpleDialog(CxKidsInfoWebViewActivity.this, null,
                getString(R.string.cx_fa_net_response_code_err), null, null).show();
    }

    /**
     * 同步一下cookie
     */
    public void synCookies(Context context, Cookie ck) {
        CxLog.i("webview", "cookies="+ck.toString());
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
//         cookieManager.removeSessionCookie();
        cookieManager.setCookie(mUrl, ck.getName()+"="+ck.getValue());// cookies是在HttpClient中获得的cookie
        cookieManager.setCookie(mUrl, "expires="+ck.getExpiryDate());// cookies是在HttpClient中获得的cookie
        cookieManager.setCookie(mUrl, "domain=" + ck.getDomain());// cookies是在HttpClient中获得的cookie
        cookieManager.setCookie(mUrl, "path="+ck.getPath());// cookies是在HttpClient中获得的cookie
        cookieSyncManager.sync();
        System.out.println("webview get cookies="+cookieManager.getCookie(mUrl));
    }
}
