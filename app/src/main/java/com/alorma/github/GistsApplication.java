package com.alorma.github;

import android.app.Application;

import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.sdk.security.StoreCredentials;

/**
 * Created by Bernat on 08/07/2014.
 */
public class GistsApplication extends Application{

    public static int AB_COLOR;

    @Override
    public void onCreate() {
        super.onCreate();

        AB_COLOR = getResources().getColor(R.color.accent);
        
        ApiConstants.CLIENT_ID = getString(R.string.client_id);
        ApiConstants.CLIENT_SECRET = getString(R.string.client_secret);
        ApiConstants.CLIENT_CALLBACK = getString(R.string.client_callback);
    }
}
