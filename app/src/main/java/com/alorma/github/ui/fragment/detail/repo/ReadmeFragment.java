package com.alorma.github.ui.fragment.detail.repo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetReadmeContentsClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 22/07/2014.
 */
public class ReadmeFragment extends BaseFragment implements BaseClient.OnResultCallback<String>, BranchManager, TitleProvider {

	public static final String OWNER = "OWNER";
	public static final String REPO = "REPO";

	private WebView webview;
	private String owner;
	private String repo;
	private UpdateReceiver updateReceiver;

	public static ReadmeFragment newInstance(String owner, String repo) {
		Bundle bundle = new Bundle();
		bundle.putString(OWNER, owner);
		bundle.putString(REPO, repo);

		ReadmeFragment f = new ReadmeFragment();
		f.setArguments(bundle);
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.readme_fragment, null);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (getArguments() != null) {
			owner = getArguments().getString(OWNER);
			repo = getArguments().getString(REPO);

			webview = (WebView) view.findViewById(R.id.webContainer);
			webview.setPadding(0, 24, 0, 0);
			webview.getSettings().setJavaScriptEnabled(true);
			webview.setWebViewClient(new WebViewCustomClient());

			webview.clearCache(true);
			webview.clearFormData();
			webview.clearHistory();
			webview.clearMatches();
			webview.clearSslPreferences();
			webview.getSettings().setUseWideViewPort(false);
			webview.setBackgroundColor(getResources().getColor(R.color.gray_github_light));
			getContent();
		}
	}

	private void getContent() {
		GetReadmeContentsClient repoMarkdownClient = new GetReadmeContentsClient(getActivity(), owner, repo);
		repoMarkdownClient.setCallback(this);
		repoMarkdownClient.execute();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.add(0, R.id.action_repo_watch, 1, R.string.menu_watch);

		int color = Color.WHITE;

		MenuItem itemWatch = menu.findItem(R.id.action_repo_watch);
		itemWatch.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		GithubIconDrawable drawableWatch = new GithubIconDrawable(getActivity(), GithubIconify.IconValue.octicon_eye);
		drawableWatch.setStyle(Paint.Style.FILL);
		drawableWatch.color(color);
		drawableWatch.actionBarSize();
		itemWatch.setIcon(drawableWatch);

		menu.add(0, R.id.action_repo_star, 0, R.string.menu_star);

		MenuItem itemStar = menu.findItem(R.id.action_repo_star);
		itemStar.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		GithubIconDrawable drawableStar = new GithubIconDrawable(getActivity(), GithubIconify.IconValue.octicon_star);
		drawableStar.setStyle(Paint.Style.FILL);
		drawableStar.color(color);
		drawableStar.actionBarSize();
		itemStar.setIcon(drawableStar);
	}

	@Override
	public void onResponseOk(final String s, Response r) {
		webview.loadDataWithBaseURL("http://github.com", s, "text/html", "UTF-8", null);
	}

	@Override
	public void onFail(RetrofitError error) {
		onError("README", error);
	}

	@Override
	public void setCurrentBranch(Branch branch) {
		if (getActivity() != null) {
			GetReadmeContentsClient repoMarkdownClient = new GetReadmeContentsClient(getActivity(), owner, repo);
			repoMarkdownClient.setCurrentBranch(branch);
			repoMarkdownClient.setCallback(this);
			repoMarkdownClient.execute();
		}
	}

	private void onError(String tag, RetrofitError error) {
		ErrorHandler.onRetrofitError(getActivity(), "MarkdownFragment", error);
	}

	@Override
	public CharSequence getTitle() {
		return getString(R.string.markdown_fragment_title);
	}

	private class WebViewCustomClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(i);
			return true;
		}
	}
	
	public void reload() {
		getContent();
	}

	@Override
	public void onStart() {
		super.onStart();
		updateReceiver = new UpdateReceiver();
		IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		getActivity().registerReceiver(updateReceiver, intentFilter);
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(updateReceiver);
	}

	public class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (isOnline(context)) {
				reload();
			}
		}

		public boolean isOnline(Context context) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfoMob = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			return (netInfoMob != null && netInfoMob.isConnectedOrConnecting()) || (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting());
		}
	}
	
}
