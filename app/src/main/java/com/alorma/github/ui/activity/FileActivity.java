package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alorma.github.ui.activity.base.BackActivity;

/**
 * Created by Bernat on 20/07/2014.
 */
public class FileActivity extends BackActivity {

	private static final String URL = "URL";

	public static Intent createLauncherIntent(Context context, String url) {
		Bundle bundle = new Bundle();
		bundle.putString(URL, url);

		Intent intent = new Intent(context, FileActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WebView webView = new WebView(this);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}
		});

		if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(URL)) {
			String url = getIntent().getExtras().getString(URL);
			webView.loadUrl(url);
		} else {
			finish();
		}

		setContentView(webView);
	}
}
