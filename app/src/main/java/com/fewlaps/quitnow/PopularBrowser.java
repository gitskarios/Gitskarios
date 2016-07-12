package com.fewlaps.quitnow;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class PopularBrowser {

    private static final String CHROME_PCKG = "com.android.chrome"; //>500M downloads
    private static final String FIREFOX_PCKG = "org.mozilla.firefox"; //>100M downloads
    private static final String OPERA_PCKG = "com.opera.browser"; //>50M downloads
    private static final String UC_BROWSER_PCKG = "com.UCMobile.intl"; //>100M downloads, very geek

    private static List<String> browsers;

    static {
        browsers = new ArrayList<>();
        browsers.add(CHROME_PCKG);
        browsers.add(FIREFOX_PCKG);
        browsers.add(OPERA_PCKG);
        browsers.add(UC_BROWSER_PCKG);
    }

    private final Context context;

    public PopularBrowser(Context context) {
        this.context = context;
    }

    public void open(String url) {
        for (String browser : browsers) {
            boolean hasBeenOpened = openWithBrowser(url, browser);
            if (hasBeenOpened) {
                return;
            }
        }
        launchCommonAndroidChooser(url);
    }

    private boolean openWithBrowser(String url, String app) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(app);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException ex) {
            return false;
        }
    }

    private void launchCommonAndroidChooser(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
}