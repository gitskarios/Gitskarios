package com.alorma.github.ui.fragment.detail.repo;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alorma.github.BuildConfig;
import com.alorma.github.GistsApplication;
import com.alorma.github.R;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetReadmeContentsClient;
import com.alorma.github.ui.events.ColorEvent;
import com.alorma.github.ui.listeners.RefreshListener;
import com.bugsense.trace.BugSenseHandler;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 22/07/2014.
 */
public class MarkdownFragment extends Fragment implements BaseClient.OnResultCallback<String> {

    public static final String OWNER = "OWNER";
    public static final String REPO = "REPO";

    private WebView webview;
    private RefreshListener refreshListener;
    private Bus bus;

    public static MarkdownFragment newInstance(String owner, String repo, RefreshListener refreshListener) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);

        MarkdownFragment f = new MarkdownFragment();
        f.setRefreshListener(refreshListener);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get tracker.
        Tracker t = ((GistsApplication) getActivity().getApplication()).getTracker();

        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName(getClass().getSimpleName());

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
        bus = new Bus();
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
            String owner = getArguments().getString(OWNER);
            String repo = getArguments().getString(REPO);
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
            webview.setBackgroundColor(getResources().getColor(R.color.gray_github));
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
        Log.e(tag, "Error", error);
        if (BuildConfig.DEBUG) {
            Toast.makeText(getActivity(), "Error: " + tag, Toast.LENGTH_SHORT).show();
        }

        if (error != null && error.getMessage() != null) {
            BugSenseHandler.addCrashExtraData(tag, error.getMessage());
            BugSenseHandler.flush(getActivity());
        }

        if (refreshListener != null) {
            refreshListener.cancelRefresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Subscribe
    public void colorReceived(ColorEvent event) {

    }
}
