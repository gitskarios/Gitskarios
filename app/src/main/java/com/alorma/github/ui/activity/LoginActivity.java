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
import com.alorma.github.sdk.client.BaseClient;
import com.alorma.github.sdk.client.RequestTokenClient;
import com.alorma.github.sdk.security.StoreCredentials;

import retrofit.RetrofitError;

public class LoginActivity extends Activity {

    public static String OAUTH_URL = "https://github.com/login/oauth/authorize";
    public static String OAUTH_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";

    public static String CLIENT_ID = "5dbfca95aa4de59f4a1c";
    public static String CLIENT_SECRET = "b3688437884ec6978884abafdce798d8781314d1";
    private StoreCredentials credentials;
    private ProgressDialog progress;

    public static void startActivity(Activity context, int result) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivityForResult(intent, result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        credentials = new StoreCredentials(this);
        if (credentials.token() != null) {
             MainActivity.startActivity(this);
        } else {
            String url = OAUTH_URL + "?client_id=" + CLIENT_ID;

            url = url + "&scope=gist,user,repo,notifications";

            WebView webview = (WebView) findViewById(R.id.webview);
            webview.setWebChromeClient(new CustomChromeClient());
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
            String accessTokenFragment = "access_token=";
            String accessCodeFragment = "code=";

            if (progress != null) {
                progress.dismiss();
                progress = null;
            }

            progress = new ProgressDialog(view.getContext());
            progress.setMax(100);
            progress.show();

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
            if (progress != null) {
                progress.dismiss();
                progress = null;
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @Override
        public void onResponseOk(Token token) {
            if (token.access_token != null) {
                Toast.makeText(LoginActivity.this, token.access_token, Toast.LENGTH_LONG).show();
                endAcces(token.access_token);
            } else if (token.error != null) {
                Toast.makeText(LoginActivity.this, token.error, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            if (error.getResponse() != null) {
                Log.e("RETROFIT", "Response error body: " + error.getResponse().getBody());
            }
        }
    }

    private class CustomChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (progress != null) {
                progress.setProgress(newProgress);
            }
        }
    }
}
