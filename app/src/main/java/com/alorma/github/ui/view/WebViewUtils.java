package com.alorma.github.ui.view;

import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.ui.activity.IssueDetailActivity;

/**
 * Created by Bernat on 27/04/2015.
 */
public class WebViewUtils {

    public static void manageUrls(final WebView webView) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);

                if (url.contains("issues")) {

                    IssueInfo issueInfo = new IssueInfo(url);

                    Intent intent = IssueDetailActivity.createLauncherIntent(webView.getContext(), issueInfo);
                    webView.getContext().startActivity(intent);

                    return true;
                }

                return false;
            }
        });
    }

}
