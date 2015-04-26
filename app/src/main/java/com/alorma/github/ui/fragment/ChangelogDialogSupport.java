package com.alorma.github.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.view.WebViewUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ChangelogDialogSupport extends DialogFragment {

    public static ChangelogDialogSupport create(boolean darkTheme, int accentColor) {
        ChangelogDialogSupport dialog = new ChangelogDialogSupport();
        Bundle args = new Bundle();
        args.putBoolean("dark_theme", darkTheme);
        args.putInt("accent_color", accentColor);
        dialog.setArguments(args);
        return dialog;
    } 

    @NonNull
    @Override 
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View customView;
        try { 
            customView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_webview, null);
        } catch (InflateException e) {
            throw new IllegalStateException("This device does not support Web Views.");
        } 
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .theme(getArguments().getBoolean("dark_theme") ? Theme.DARK : Theme.LIGHT)
                .title(R.string.changelog)
                .customView(customView, false)
                .positiveText(android.R.string.ok)
                .build(); 
 
 
        final WebView webView = (WebView) customView.findViewById(R.id.webview);
        try { 
            // Load from changelog.html in the assets folder 
            StringBuilder buf = new StringBuilder();
            InputStream json = getActivity().getAssets().open("changelog.html");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null)
                buf.append(str);
            in.close();
 
 
            // Inject color values for WebView body background and links 
            final int accentColor = getArguments().getInt("accent_color");
            webView.loadData(buf.toString()
                    .replace("{style-placeholder}", getArguments().getBoolean("dark_theme") ?
                            "body { background-color: #444444; color: #fff; }" : 
                            "body { background-color: #EDEDED; color: #000; }") 
                    .replace("{link-color}", colorToHex(shiftColor(accentColor, true)))
                    .replace("{link-color-active}", colorToHex(accentColor))
                    , "text/html", "UTF-8"); 
        } catch (Throwable e) {
            webView.loadData("<h1>Unable to load</h1><p>" + e.getLocalizedMessage() + "</p>", "text/html", "UTF-8");
        }

        WebViewUtils.manageUrls(webView);

        return dialog;
    } 
 
 
    private String colorToHex(int color) {
        return Integer.toHexString(color).substring(2);
    } 

    private int shiftColor(int color, boolean up) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= (up ? 1.1f : 0.9f); // value component
        return Color.HSVToColor(hsv);
    } 
} 