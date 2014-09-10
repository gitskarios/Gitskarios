package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.inapp.Base64;
import com.alorma.github.sdk.bean.dto.request.RequestMarkdownDTO;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.content.GetFileContentClient;
import com.alorma.github.sdk.services.content.GetMarkdownClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.utils.MarkdownUtils;
import com.alorma.github.utils.ImageUtils;

import java.io.UnsupportedEncodingException;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
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
	private ImageView imageView;
	private Content content;
	private SmoothProgressBar smoothBar;

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
		setContentView(R.layout.activity_content);

		webView = (WebView) findViewById(R.id.webview);
		imageView = (ImageView) findViewById(R.id.imageView);
		smoothBar = (SmoothProgressBar) findViewById(R.id.smoothBar);

		String owner = getIntent().getExtras().getString(OWNER);
		String repo = getIntent().getExtras().getString(REPO);
		String head = getIntent().getExtras().getString(HEAD);
		String name = getIntent().getExtras().getString(NAME);
		String path = getIntent().getExtras().getString(PATH);

		setTitle(name);

		smoothBar.progressiveStart();

		GetFileContentClient fileContentClient = new GetFileContentClient(this, owner, repo, path, head);
		fileContentClient.setOnResultCallback(this);
		fileContentClient.execute();

		webView.clearCache(true);
		webView.clearFormData();
		webView.clearHistory();
		webView.clearMatches();
		webView.clearSslPreferences();
		webView.getSettings().setUseWideViewPort(false);
		webView.setBackgroundColor(getResources().getColor(R.color.gray_github_light));
		webView.setVisibility(View.VISIBLE);
		WebSettings settings = webView.getSettings();
		settings.setBuiltInZoomControls(true);
		settings.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new JavaScriptInterface(), "bitbeaker");
		webView.setWebChromeClient(new MyWebChromeClient());

	}

	@Override
	public void onResponseOk(Content content, Response r) {
		this.content = content;

		if (MarkdownUtils.isMarkdown(content.name)) {
			RequestMarkdownDTO request = new RequestMarkdownDTO();
			request.text = decodeContent();
			GetMarkdownClient markdownClient = new GetMarkdownClient(this, request);
			markdownClient.setOnResultCallback(new BaseClient.OnResultCallback<String>() {
				@Override
				public void onResponseOk(final String s, Response r) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							webView.clearCache(true);
							webView.clearFormData();
							webView.clearHistory();
							webView.clearMatches();
							webView.clearSslPreferences();
							webView.getSettings().setUseWideViewPort(false);
							webView.setBackgroundColor(getResources().getColor(R.color.gray_github_light));
							webView.loadDataWithBaseURL("http://github.com", s, "text/html", "UTF-8", null);
						}
					});
				}

				@Override
				public void onFail(RetrofitError error) {
					ErrorHandler.onRetrofitError(FileActivity.this, "FileActivity", error);
				}
			});
			markdownClient.execute();
		} else if (ImageUtils.isImage(content.name)) {
			try {
				byte[] imageAsBytes = Base64.decode(content.content);
				Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
				webView.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageBitmap(bitmap);
				smoothBar.progressiveStop();
			} catch (Exception e) {
				Toast.makeText(this, R.string.error_loading_image, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		} else {
			webView.loadUrl("file:///android_asset/source.html");
		}
	}

	private String decodeContent() {
		byte[] data = android.util.Base64.decode(content.content, android.util.Base64.DEFAULT);
		try {
			content.content = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return content.content;
	}

	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int progress) {
			if (progress >= 100) {
				smoothBar.progressiveStop();
			}
		}
	}

	protected class JavaScriptInterface {
		@JavascriptInterface
		public String getCode() {
			return TextUtils.htmlEncode(decodeContent().replace("\t", "    "));
		}

		@JavascriptInterface
		public String getRawCode() {
			return decodeContent();
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
