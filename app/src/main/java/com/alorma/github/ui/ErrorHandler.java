package com.alorma.github.ui;

import com.alorma.github.BuildConfig;
import com.bugsense.trace.BugSenseHandler;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import retrofit.RetrofitError;

public class ErrorHandler {

    public static void onRetrofitError(Context context, String tag, RetrofitError error) {
        Log.e(tag, "Error", error);
        if (BuildConfig.DEBUG) {
            if (context != null) {
                Toast.makeText(context, "Error: " + tag + "\n" + error, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (error != null && error.getMessage() != null) {
                BugSenseHandler.addCrashExtraData(tag, error.getMessage());
                BugSenseHandler.flush(context);
            }
        }
    }
}