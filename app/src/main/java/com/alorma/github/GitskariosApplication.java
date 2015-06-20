package com.alorma.github;

import android.app.Application;
import android.content.Context;

import com.alorma.github.ui.UiModule;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;

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

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker tracker = analytics.newTracker(R.xml.global_tracker);
            tracker.enableAdvertisingIdCollection(true);
        }

        JodaTimeAndroid.init(this);

        graph = ObjectGraph.create(getModules().toArray());

        ImageLoader.getInstance().init(UniversalImageLoaderUtils.getImageLoaderConfiguration(this));

        Iconics.registerFont(new Octicons());
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
