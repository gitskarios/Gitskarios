package com.alorma.github.ui;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.alorma.github.BuildConfig;
import com.bugsense.trace.BugSenseHandler;

import retrofit.RetrofitError;

/**
 * Created by Bernat on 27/07/2014.
 */
public class ErrorHandler {

    public static void onRetrofitError(Context context, String tag, RetrofitError error) {
        Log.e(tag, "Error", error);
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, "Error: " + tag + "\n" + error, Toast.LENGTH_SHORT).show();
        } else {
            if (error != null && error.getMessage() != null) {
                BugSenseHandler.addCrashExtraData(tag, error.getMessage());
                BugSenseHandler.flush(context);
            }
        }
    }

}
