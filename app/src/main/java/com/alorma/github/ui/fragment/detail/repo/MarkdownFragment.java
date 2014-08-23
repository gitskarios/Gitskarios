package com.alorma.github.ui.fragment.detail.repo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
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
import com.alorma.github.ui.listeners.RefreshListener;
import com.bugsense.trace.BugSenseHandler;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 22/07/2014.
 */
public class MarkdownFragment extends BaseFragment implements BaseClient.OnResultCallback<String>, BranchManager {

    public static final String OWNER = "OWNER";
    public static final String REPO = "REPO";

    private WebView webview;
    private RefreshListener refreshListener;
    private String owner;
    private String repo;

    public static MarkdownFragment newInstance(String owner, String repo, RefreshListener refreshListener) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);

        MarkdownFragment f = new MarkdownFragment();
        f.setRefreshListener(refreshListener);
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new WebView(getActivity());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            owner = getArguments().getString(OWNER);
            repo = getArguments().getString(REPO);

            webview = (WebView) view;
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
            if (refreshListener != null) {
                refreshListener.showRefresh();
            }
            GetReadmeContentsClient repoMarkdownClient = new GetReadmeContentsClient(getActivity(), owner, repo);
            repoMarkdownClient.setCallback(this);
            repoMarkdownClient.execute();
        }
    }

    @Override
    public void onResponseOk(final String s, Response r) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webview.loadDataWithBaseURL("http://github.com", s, "text/html", "UTF-8", null);
            }
        });
    }

    @Override
    public void onFail(RetrofitError error) {
        onError("README", error);
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    @Override
    public void setCurrentBranch(Branch branch) {
        GetReadmeContentsClient repoMarkdownClient = new GetReadmeContentsClient(getActivity(), owner, repo);
        repoMarkdownClient.setCurrentBranch(branch);
        repoMarkdownClient.setCallback(this);
        repoMarkdownClient.execute();
    }

    private class WebViewCustomClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (refreshListener != null) {
                refreshListener.showRefresh();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (refreshListener != null) {
                refreshListener.cancelRefresh();
            }
        }
    }

    private void onError(String tag, RetrofitError error) {

        ErrorHandler.onRetrofitError(getActivity(), "MarkdownFragment", error);

        if (error != null && error.getMessage() != null) {
            BugSenseHandler.addCrashExtraData(tag, error.getMessage());
            BugSenseHandler.flush(getActivity());
        }

        if (refreshListener != null) {
            refreshListener.cancelRefresh();
        }
    }
}
