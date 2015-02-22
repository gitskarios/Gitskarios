package com.alorma.github.ui.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.BuildConfig;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Token;
import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.sdk.security.StoreCredentials;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.login.RequestTokenClient;
import com.alorma.github.ui.ErrorHandler;
import com.crashlytics.android.Crashlytics;

import dmax.dialog.SpotsDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends Activity {

	public static String OAUTH_URL = "https://github.com/login/oauth/authorize";

	private StoreCredentials credentials;
	private SpotsDialog progressDialog;
	private WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!BuildConfig.DEBUG) {
			Crashlytics.start(this);
		}

		setContentView(R.layout.activity_login);

		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewCustomClient());

		webview.clearCache(true);
		webview.clearFormData();
		webview.clearHistory();
		webview.clearMatches();
		webview.clearSslPreferences();

		webview.getSettings().setUseWideViewPort(true);

		credentials = new StoreCredentials(this);
		if (credentials.token() != null) {
			if (credentials.scopeNoAsk()) {
				openMain();
			} else {
				updatingTokens();
			}
		} else {
			login();
		}
	}

	private void login() {
		String url = OAUTH_URL + "?client_id=" + ApiConstants.CLIENT_ID;

		url = url + "&scope=gist,user,name,notifications,repo";
		webview.loadUrl(url);
	}

	private void updatingTokens() {
		if (credentials.scopes() == null || (!credentials.scopes().contains("repo"))) {
			MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
			builder.title(R.string.repo_scope_title);
			builder.content(R.string.repo_scope_message);
			builder.positiveText(R.string.repo_scope_positive);
			builder.cancelable(false);
			builder.callback(new MaterialDialog.ButtonCallback() {
				@Override
				public void onPositive(MaterialDialog dialog) {
					super.onPositive(dialog);
					credentials.clear();
					login();
				}
			});
			builder.show();
		} else {
			MainActivity.startActivity(this);
			finish();
		}
	}


	private void openMain() {
		MainActivity.startActivity(LoginActivity.this);
		finish();
	}

	private void endAccess(String accessToken, String scope) {
		if (credentials != null) {
			credentials.storeToken(accessToken);
			credentials.storeScopes(scope);
			openMain();
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

	private void showDialog() {
		try {
			progressDialog = new SpotsDialog(this, R.style.SpotDialog_Login);
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
			Crashlytics.logException(e);
		}
	}

	private class WebViewCustomClient extends WebViewClient implements BaseClient.OnResultCallback<Token> {
		private RequestTokenClient requestTokenClient;

		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			String accessTokenFragment = "access_token=";
			String accessCodeFragment = "code";

			// We hijack the GET request to extract the OAuth parameters

			if (url != null) {
				if (url.contains(accessCodeFragment)) {
					// the GET request contains an authorization code

					Uri uri = Uri.parse(url);

					showDialog();

					if (requestTokenClient == null) {
						requestTokenClient = new RequestTokenClient(LoginActivity.this, uri.getQueryParameter(accessCodeFragment));
						requestTokenClient.setOnResultCallback(this);
						requestTokenClient.execute();
					}
				}
			}
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Uri uri = Uri.parse(url);
			Uri callback = Uri.parse(ApiConstants.CLIENT_CALLBACK);
			return (uri.getAuthority().equals(callback.getAuthority()));
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			// TODO STOP LOADING
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			super.onReceivedSslError(view, handler, error);
			handler.proceed();
		}

		@Override
		public void onResponseOk(Token token, Response r) {
			if (token.access_token != null) {
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				endAccess(token.access_token, token.scope);
			} else if (token.error != null) {
				Toast.makeText(LoginActivity.this, token.error, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onFail(RetrofitError error) {
			ErrorHandler.onRetrofitError(LoginActivity.this, "WebViewCustomClient", error);
		}
	}
}
