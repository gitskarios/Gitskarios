package com.alorma.github.ui;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alorma.github.BuildConfig;
import com.crashlytics.android.Crashlytics;

import retrofit.RetrofitError;

/**
 * Created by Bernat on 27/07/2014.
 */
public class ErrorHandler {

	public static void onRetrofitError(Context context, String tag, RetrofitError error) {
		Log.e(tag, "Error", error);
		if (BuildConfig.DEBUG) {
			if (context != null) {
				Toast.makeText(context, "Error: " + tag + "\n" + error, Toast.LENGTH_SHORT).show();
			}
		} else {
			if (error != null && error.getMessage() != null) {
				//Crashlytics.logException(error);
			}
		}
	}

}
