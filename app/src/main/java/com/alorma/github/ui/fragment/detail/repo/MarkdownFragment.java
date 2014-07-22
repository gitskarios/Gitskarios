package com.alorma.github.ui.fragment.detail.repo;

import android.app.Fragment;
import android.content.Intent;
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
import com.alorma.github.R;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetReadmeContentsClient;
import com.bugsense.trace.BugSenseHandler;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 22/07/2014.
 */
public class MarkdownFragment extends Fragment implements BaseClient.OnResultCallback<String> {

    public static final String OWNER = "OWNER";
    public static final String REPO = "REPO";

    private SmoothProgressBar bar;
    private WebView webview;

    public static MarkdownFragment newInstance(String owner, String repo) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);

        MarkdownFragment f = new MarkdownFragment();
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_login, null, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String owner = getArguments().getString(OWNER);
            String repo = getArguments().getString(REPO);
            bar = (SmoothProgressBar) view.findViewById(R.id.smoothBar);
            webview = (WebView) view.findViewById(R.id.webview);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewCustomClient());

            webview.clearCache(true);
            webview.clearFormData();
            webview.clearHistory();
            webview.clearMatches();
            webview.clearSslPreferences();
            webview.getSettings().setUseWideViewPort(false);
            webview.setBackgroundColor(getResources().getColor(R.color.gray_github));

            bar.progressiveStart();

            GetReadmeContentsClient repoMarkdownClient = new GetReadmeContentsClient(getActivity(), owner, repo, this);
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

    private class WebViewCustomClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            bar.progressiveStop();
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
    }
}
