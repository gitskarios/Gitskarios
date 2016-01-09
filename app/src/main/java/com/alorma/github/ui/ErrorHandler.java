package com.alorma.github.ui;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alorma.github.BuildConfig;

/**
 * Created by Bernat on 27/07/2014.
 */
public class ErrorHandler {

    public static void onError(Context context, String tag, Throwable error) {
        Log.e(tag, "Error", error);
        if (BuildConfig.DEBUG) {
            if (context != null) {
                Toast.makeText(context, "Error: " + tag + "\n" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
