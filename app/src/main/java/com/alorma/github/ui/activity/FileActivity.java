package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.content.GetFileContentClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;

import java.io.UnsupportedEncodingException;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 20/07/2014.
 */
public class FileActivity extends BackActivity implements BaseClient.OnResultCallback<Content> {

	private static final String OWNER = "OWNER";
	private static final String REPO = "REPO";
	private static final String HEAD = "HEAD";
	private static final String NAME = "NAME";
	private static final String PATH = "PATH";
	private WebView webView;
	private Content content;

	public static Intent createLauncherIntent(Context context, String owner, String repo, String head, String name, String path) {
		Bundle bundle = new Bundle();
		bundle.putString(OWNER, owner);
		bundle.putString(REPO, repo);
		bundle.putString(HEAD, head);
		bundle.putString(NAME, name);
		bundle.putString(PATH, path);

		Intent intent = new Intent(context, FileActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		webView = (WebView) findViewById(R.id.webview);

		String owner = getIntent().getExtras().getString(OWNER);
		String repo = getIntent().getExtras().getString(REPO);
		String head = getIntent().getExtras().getString(HEAD);
		String name = getIntent().getExtras().getString(NAME);
		String path = getIntent().getExtras().getString(PATH);

		setTitle(name);

		GetFileContentClient fileContentClient = new GetFileContentClient(this, owner, repo, path, head);
		fileContentClient.setOnResultCallback(this);
		fileContentClient.execute();

		webView.setVisibility(View.VISIBLE);
		WebSettings settings = webView.getSettings();
		settings.setBuiltInZoomControls(true);
		settings.setUseWideViewPort(true);
		settings.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new JavaScriptInterface(), "bitbeaker");
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.loadUrl("file:///android_asset/source.html");

	}

	@Override
	public void onResponseOk(Content content, Response r) {
		byte[] data = android.util.Base64.decode(content.content, android.util.Base64.DEFAULT);
		try {
			content.content = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		this.content = content;

		webView.loadUrl("file:///android_asset/source.html");
	}

	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int progress) {

		}
	}

	protected class JavaScriptInterface {
		@JavascriptInterface
		public String getCode() {
			return TextUtils.htmlEncode(content.content.replace("\t", "    "));
		}

		@JavascriptInterface
		public String getRawCode() {
			return content.content;
		}

		@JavascriptInterface
		public String getFilename() {
			return content.name;
		}
	}

	@Override
	public void onFail(RetrofitError error) {
		ErrorHandler.onRetrofitError(this, "FileActivity", error);
	}
}
