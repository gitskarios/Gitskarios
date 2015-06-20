package com.alorma.github.ui;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alorma.github.BuildConfig;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Bernat on 27/07/2014.
 */
public class ErrorHandler {

    public static void onError(Context context, String tag, Exception error) {
        Log.e(tag, "Error", error);
        if (BuildConfig.DEBUG) {
            if (context != null) {
                Toast.makeText(context, "Error: " + tag + "\n" + error, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (error != null && error.getMessage() != null && Fabric.isInitialized()) {
                Crashlytics.logException(error);
            }
        }
    }

}
