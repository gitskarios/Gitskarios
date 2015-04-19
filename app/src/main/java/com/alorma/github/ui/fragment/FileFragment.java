package com.alorma.github.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.inapp.Base64;
import com.alorma.github.sdk.bean.dto.request.RequestMarkdownDTO;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.content.GetFileContentClient;
import com.alorma.github.sdk.services.content.GetMarkdownClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.utils.MarkdownUtils;
import com.alorma.github.utils.ImageUtils;
import com.alorma.github.utils.uris.RepoUri;
import com.alorma.gitskarios.basesdk.client.BaseClient;

import java.io.UnsupportedEncodingException;

import dmax.dialog.SpotsDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 20/07/2014.
 */
public class FileFragment extends BaseFragment implements BaseClient.OnResultCallback<Content> {

	private static final String REPO_INFO = "REPO_INFO";
	public static final String NAME = "NAME";
	private static final String PATH = "PATH";
	public static final String PATCH = "PATCH";

	private WebView webView;
	private ImageView imageView;
	private Content content;

	private String patch;
	private String name;
	private String path;
	private RepoInfo repoInfo;

	private FileFragmentListener fileFragmentListener;
	private SpotsDialog progressDialog;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_content, null, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

		if (fileFragmentListener != null) {
			if (fileFragmentListener.showUpIndicator()) {
				toolbar.setNavigationIcon(R.drawable.ic_ab_back_mtrl_am_alpha);
				toolbar.setNavigationOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getActivity().finish();
					}
				});
			}
		}

		webView = (WebView) view.findViewById(R.id.webview);
		imageView = (ImageView) view.findViewById(R.id.imageView);

		if (getArguments() != null) {
			repoInfo = getArguments().getParcelable(REPO_INFO);
			name = getArguments().getString(NAME);
			path = getArguments().getString(PATH);
			patch = getArguments().getString(PATCH);

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

			if (patch == null) {
				getContent();
			} else {
				webView.loadUrl("file:///android_asset/diff.html");
			}
			toolbar.setTitle(name);
		}
	}

	protected void getContent() {
		if (repoInfo != null) {
			showProgressDialog(R.style.SpotDialog_CommentIssue);
			GetFileContentClient fileContentClient = new GetFileContentClient(getActivity(), repoInfo, path);
			fileContentClient.setOnResultCallback(this);
			fileContentClient.execute();
		}
	}

	@Override
	public void onResponseOk(Content content, Response r) {
		this.content = content;

		hideProgressDialog();


		if (MarkdownUtils.isMarkdown(content.name)) {
			RequestMarkdownDTO request = new RequestMarkdownDTO();
			request.text = decodeContent();
			GetMarkdownClient markdownClient = new GetMarkdownClient(getActivity(), request, new Handler());
			markdownClient.setOnResultCallback(new BaseClient.OnResultCallback<String>() {
				@Override
				public void onResponseOk(final String s, Response r) {
					if (getActivity() != null && isAdded()) {
						webView.clearCache(true);
						webView.clearFormData();
						webView.clearHistory();
						webView.clearMatches();
						webView.clearSslPreferences();
						webView.getSettings().setUseWideViewPort(false);
						webView.setBackgroundColor(getResources().getColor(R.color.gray_github_light));
						webView.loadDataWithBaseURL("http://github.com", s, "text/html", "UTF-8", null);
					}
				}

				@Override
				public void onFail(RetrofitError error) {
					ErrorHandler.onRetrofitError(getActivity(), "FileActivity", error);
				}
			});
			markdownClient.execute();
		} else if (ImageUtils.isImage(content.name)) {
			if (getActivity() != null && isAdded()) {
				try {
					byte[] imageAsBytes = Base64.decode(content.content);
					Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
					webView.setVisibility(View.GONE);
					imageView.setVisibility(View.VISIBLE);
					imageView.setImageBitmap(bitmap);
					// TODO STOP loading
				} catch (Exception e) {
					Toast.makeText(getActivity(), R.string.error_loading_image, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		} else if (content.isSubmodule()){
			if (getActivity() != null && isAdded()) {
				RepoUri uri = new RepoUri().create(content.git_url);
				Intent intent = RepoDetailActivity.createLauncherIntent(getActivity(), uri.getOwner(), uri.getRepo());
				startActivity(intent);
				getActivity().finish();
			}
		} else {
			if (getActivity() != null && isAdded()) {
				webView.loadUrl("file:///android_asset/source.html");
			}
		}
	}

	private String decodeContent() {
		if (patch == null) {
			byte[] data = android.util.Base64.decode(content.content, android.util.Base64.DEFAULT);
			try {
				content.content = new String(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return content.content;
		} else {
			return patch;
		}
	}

	public void setFileFragmentListener(FileFragmentListener fileFragmentListener) {
		this.fileFragmentListener = fileFragmentListener;
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
		ErrorHandler.onRetrofitError(getActivity(), "FileActivity", error);
	}

	public interface FileFragmentListener {
		boolean showUpIndicator();
	}

	protected void showProgressDialog(@StyleRes int style) {
		if (progressDialog == null) {
			try {
				progressDialog = new SpotsDialog(getActivity(), style);
				progressDialog.setCancelable(false);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void hideProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}
