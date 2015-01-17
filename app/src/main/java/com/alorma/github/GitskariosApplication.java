package com.alorma.github;

import android.app.Application;

import com.alorma.github.sdk.security.ApiConstants;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Bernat on 08/07/2014.
 */
public class GitskariosApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		JodaTimeAndroid.init(this);

		ApiConstants.CLIENT_ID = BuildConfig.CLIENT_ID;
		ApiConstants.CLIENT_SECRET = BuildConfig.CLIENT_SECRET;
		ApiConstants.CLIENT_CALLBACK = BuildConfig.CLIENT_CALLBACK;

	}
}
