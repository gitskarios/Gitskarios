package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.alorma.github.BuildConfig;
import com.alorma.github.sdk.utils.GitskariosSettings;

/**
 * Created by bernat.borras on 4/1/16.
 */
public class CheckShowAd {
    public static void showEnterpriseAd(Context context) {
        GitskariosSettings settings = new GitskariosSettings(context);
        boolean showEnterprise = settings.getShowEnterprise();
        if (showEnterprise || BuildConfig.DEBUG) {
            settings.setShowEnterpriseVisited();
            Intent intent = new Intent(context, EnterpriseAdActivity.class);
            context.startActivity(intent);
        }
    }
}
