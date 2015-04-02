package com.alorma.github;

import android.app.Application;
import android.content.Context;

import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.ui.UiModule;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by Bernat on 08/07/2014.
 */
public class GitskariosApplication extends Application {

	private ObjectGraph graph;

	@Override
	public void onCreate() {
		super.onCreate();
		if (!BuildConfig.DEBUG) {
			Fabric.with(this, new Crashlytics());
		}

		JodaTimeAndroid.init(this);

		ApiConstants.CLIENT_ID = BuildConfig.CLIENT_ID;
		ApiConstants.CLIENT_SECRET = BuildConfig.CLIENT_SECRET;
		ApiConstants.CLIENT_CALLBACK = BuildConfig.CLIENT_CALLBACK;

		graph = ObjectGraph.create(getModules().toArray());
	}

	protected List<Object> getModules() {
		List<Object> objects = new ArrayList<>();
		objects.add(new UiModule());
		return objects;
	}

	public void inject(Object object) {
		graph.inject(object);
	}

	public static GitskariosApplication get(Context context) {
		return (GitskariosApplication) context.getApplicationContext();
	}

}
