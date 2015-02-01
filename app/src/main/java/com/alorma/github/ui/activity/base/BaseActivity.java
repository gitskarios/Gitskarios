package com.alorma.github.ui.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.security.UnAuthIntent;
import com.alorma.github.ui.activity.LoginActivity;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 19/07/2014.
 */
public class BaseActivity extends ActionBarActivity {

	private AuthReceiver authReceiver;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private UpdateReceiver updateReceiver;

	public Toolbar getToolbar() {
		return toolbar;
	}

	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageLoader.getInstance().init(UniversalImageLoaderUtils.getImageLoaderConfiguration(this));
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		if (isToolbarEnabled()) {
			toolbar = (Toolbar) findViewById(getToolbarId());

			if (toolbar != null) {
				toolbar.setTitle(R.string.app_name);
				setSupportActionBar(toolbar);

				mDrawerLayout = (DrawerLayout) findViewById(getDrawerLayout());

				if (mDrawerLayout != null) {
					actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, 0, 0);

					actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

					mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
				}
			}
		}
	}

	public boolean isToolbarEnabled() {
		return true;
	}

	public int getToolbarId() {
		return R.id.toolbar;
	}

	public int getDrawerLayout() {
		return R.id.drawer_layout;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (actionBarDrawerToggle != null) {
			actionBarDrawerToggle.syncState();
		}
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout != null) {
				if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
					mDrawerLayout.closeDrawer(Gravity.START);
				} else {
					mDrawerLayout.openDrawer(Gravity.START);
				}
			}
		}
		return false;
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
	public void setTitle(CharSequence title) {
		if (toolbar != null) {
			toolbar.setTitle(title);
		} else {
			super.setTitle(title);
		}
	}

	@Override
	public void setTitle(int titleId) {
		if (toolbar != null) {
			toolbar.setTitle(titleId);
		} else {
			super.setTitle(titleId);
		}
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

	@Override
	public void onBackPressed() {
		if ((mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START))) {
			closeMenu();
		} else {
			super.onBackPressed();
		}
	}

	public void closeMenu() {
		if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START)) {
			mDrawerLayout.closeDrawer(Gravity.START);
		}
	}

	public void reload() {
		getContent();
	}

	protected void getContent() {

	}

	@Override
	public void onStart() {
		super.onStart();
		updateReceiver = new UpdateReceiver();
		IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(updateReceiver, intentFilter);
	}

	@Override
	public void onStop() {
		super.onStop();
		unregisterReceiver(updateReceiver);
	}

	public class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (isOnline(context)) {
				reload();
			}
		}

		public boolean isOnline(Context context) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfoMob = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			return (netInfoMob != null && netInfoMob.isConnectedOrConnecting()) || (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting());
		}
	}
}
