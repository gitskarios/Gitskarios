package com.alorma.github.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Token;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.login.RequestTokenClient;
import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.sdk.security.StoreCredentials;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends Activity {

    public static String OAUTH_URL = "https://github.com/login/oauth/authorize";

    private StoreCredentials credentials;
    private WebView webview;
    private SmoothProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bar = (SmoothProgressBar) findViewById(R.id.smoothBar);

        credentials = new StoreCredentials(this);
        if (credentials.token() != null) {
            MainActivity.startActivity(this);
            finish();
        } else {
            String url = OAUTH_URL + "?client_id=" + ApiConstants.CLIENT_ID;

            url = url + "&scope=gist,user,repo,notifications";

            webview = (WebView) findViewById(R.id.webview);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewCustomClient());

            webview.clearCache(true);
            webview.clearFormData();
            webview.clearHistory();
            webview.clearMatches();
            webview.clearSslPreferences();

            webview.loadUrl(url);
        }
    }

    private void endAcces(String accessToken) {
        credentials.storeToken(accessToken);

        MainActivity.startActivity(this);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private class WebViewCustomClient extends WebViewClient implements BaseClient.OnResultCallback<Token> {
        private RequestTokenClient requestTokenClient;

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            bar.progressiveStart();
            String accessTokenFragment = "access_token=";
            String accessCodeFragment = "code=";

            // We hijack the GET request to extract the OAuth parameters

            if (url.contains(accessTokenFragment)) {
                // the GET request contains directly the token
                String accessToken = url.substring(url.indexOf(accessTokenFragment));

                endAcces(accessToken);
            } else if (url.contains(accessCodeFragment)) {
                // the GET request contains an authorization code
                String accessCode = url.substring(url.indexOf(accessCodeFragment));

                accessCode = accessCode.split("=")[1];

                if (requestTokenClient == null) {
                    requestTokenClient = new RequestTokenClient(LoginActivity.this, accessCode);
                    requestTokenClient.setOnResultCallback(this);
                    requestTokenClient.execute();
                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            bar.progressiveStop();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @Override
        public void onResponseOk(Token token, Response r) {
            if (token.access_token != null) {
                endAcces(token.access_token);
            } else if (token.error != null) {
                Toast.makeText(LoginActivity.this, token.error, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            if (error.getResponse() != null) {
                Log.e("RETROFIT", "Response error body: " + error.getResponse().getBody());
            }
        }
    }
}
