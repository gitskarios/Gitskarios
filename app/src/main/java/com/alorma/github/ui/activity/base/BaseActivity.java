package com.alorma.github.ui.activity.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.alorma.github.BuildConfig;
import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.sdk.security.UnAuthIntent;
import com.alorma.github.ui.activity.LoginActivity;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.bugsense.trace.BugSenseHandler;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 19/07/2014.
 */
public class BaseActivity extends Activity {

	private AuthReceiver authReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!BuildConfig.DEBUG) {
			// Get tracker.
			Tracker t = ((GitskariosApplication) getApplication()).getTracker();

			// Set screen name.
			// Where path is a String representing the screen name.
			t.setScreenName(this.getClass().getSimpleName());

			// Send a screen view.
			t.send(new HitBuilders.AppViewBuilder().build());

			BugSenseHandler.initAndStartSession(BaseActivity.this, "77b1f1f6");
		}
		ImageLoader.getInstance().init(UniversalImageLoaderUtils.getImageLoaderConfiguration(this));

		if (getActionBar() != null) {
			getActionBar().setDisplayShowHomeEnabled(useLogo());
			getActionBar().setHomeButtonEnabled(useLogo());
			getActionBar().setDisplayShowTitleEnabled(true);
			if (useLogo() && getActionBarLogo() != 0) {
				getActionBar().setIcon(getActionBarLogo());
			}
		}
	}

	protected boolean useLogo() {
		return false;
	}

	protected int getActionBarLogo() {
		return 0;
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
		authReceiver = new AuthReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UnAuthIntent.ACTION);
		manager.registerReceiver(authReceiver, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
		manager.unregisterReceiver(authReceiver);
	}

	private class AuthReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, R.string.unauthorized, Toast.LENGTH_SHORT).show();
			Intent loginIntent = new Intent(context, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(loginIntent);
			finish();
		}
	}
}
