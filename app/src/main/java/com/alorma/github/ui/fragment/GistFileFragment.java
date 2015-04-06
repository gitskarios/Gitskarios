package com.alorma.github.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alorma.github.R;

/**
 * Created by Bernat on 20/07/2014.
 */
public class GistFileFragment extends Fragment {

    public static final String FILE_NAME = "FILE_NAME";
    public static final String CONTENT = "CONTENT";

    private WebView webView;

    private String content;
    private String fileName;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webView = (WebView) view.findViewById(R.id.webview);

        if (getArguments() != null) {
            fileName = getArguments().getString(FILE_NAME);
            content = getArguments().getString(CONTENT);

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

                webView.loadUrl("file:///android_asset/diff.html");

        }
    }

    protected class JavaScriptInterface {
        @JavascriptInterface
        public String getCode() {
            return TextUtils.htmlEncode(content.replace("\t", "    "));
        }

        @JavascriptInterface
        public String getRawCode() {
            return content;
        }

        @JavascriptInterface
        public String getFilename() {
            return fileName;
        }

    }
}
