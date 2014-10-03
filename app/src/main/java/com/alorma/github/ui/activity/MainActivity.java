package com.alorma.github.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.alorma.github.R;
import com.alorma.github.inapp.IabConstants;
import com.alorma.github.inapp.IabHelper;
import com.alorma.github.inapp.IabResult;
import com.alorma.github.inapp.Inventory;
import com.alorma.github.inapp.Purchase;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.fragment.events.EventsListFragment;
import com.alorma.github.ui.fragment.menu.MenuFragment;
import com.alorma.github.ui.fragment.menu.MenuItem;
import com.alorma.github.ui.fragment.orgs.OrganzationsFragment;
import com.alorma.github.ui.fragment.repos.ReposFragment;
import com.alorma.github.ui.fragment.repos.StarredReposFragment;
import com.alorma.github.ui.fragment.repos.WatchedReposFragment;
import com.alorma.github.ui.fragment.users.FollowersFragment;
import com.alorma.github.ui.fragment.users.FollowingFragment;

import java.util.UUID;

public class MainActivity extends BaseActivity implements View.OnClickListener, MenuFragment.OnMenuItemSelectedListener, IabHelper.OnIabSetupFinishedListener,
		IabHelper.OnIabPurchaseFinishedListener, IabHelper.QueryInventoryFinishedListener {

	private MenuFragment menuFragment;

	private ReposFragment reposFragment;
	private StarredReposFragment starredFragment;
	private WatchedReposFragment watchedFragment;
	private FollowersFragment followersFragment;
	private FollowingFragment followingFragment;
	private EventsListFragment eventsFragment;
	private IabHelper iabHelper;
	private boolean iabEnabled;
	private OrganzationsFragment organizationsFragmet;
	private DrawerLayout mDrawerLayout;
	private String username;

	public static void startActivity(Activity context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		username = new GitskariosSettings(this).getAuthUser(null);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		checkIab();

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content, ReposFragment.newInstance());
		menuFragment = MenuFragment.newInstance();
		menuFragment.setOnMenuItemSelectedListener(this);
		ft.replace(R.id.menuContent, menuFragment);
		ft.commit();
	}

	@Override
	protected boolean useLogo() {
		return true;
	}

	@Override
	protected int getActionBarLogo() {
		return R.drawable.ic_ab_drawer_mask;
	}

	private void checkIab() {
		iabHelper = new IabHelper(this, IabConstants.KEY);
		iabHelper.startSetup(this);
	}

	@Override
	public void onIabSetupFinished(IabResult result) {
		iabEnabled = result.isSuccess();
		if (iabEnabled) {
			invalidateOptionsMenu();
			try {
				iabHelper.queryInventoryAsync(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.main, menu);

		final View actionView = menu.findItem(R.id.action_menu).getActionView().findViewById(R.id.menuPlaceHolder);

		menu.findItem(R.id.action_menu).getActionView().findViewById(R.id.menuItem)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupMenu menu = new MainMenu(MainActivity.this, actionView);
						if (iabEnabled) {
							SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
							boolean iabDonatePurchased = preferences.getBoolean(IabConstants.SKU_DONATE, false);
							if (!iabDonatePurchased) {
								menu = new DonateMenu(MainActivity.this, actionView);
							}
						}
						menu.show();
					}
				});

		return true;
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
		} else if (item.getItemId() == R.id.action_donate) {
			try {
				iabHelper.launchPurchaseFlow(this, IabConstants.SKU_DONATE, 10001,
						this, UUID.randomUUID().toString());
				item.setEnabled(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (item.getItemId() == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.searchIcon:
				Intent search = SearchReposActivity.createLauncherIntent(this);
				startActivity(search);
				break;
		}
	}

	private void setFragment(Fragment fragment) {
		if (fragment != null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.content, fragment);
			ft.commit();
		}
	}

	@Override
	public void onReposSelected() {
		if (reposFragment == null) {
			reposFragment = ReposFragment.newInstance();
		}

		setFragment(reposFragment);
	}

	@Override
	public void onStarredSelected() {
		if (starredFragment == null) {
			starredFragment = StarredReposFragment.newInstance();
		}

		setFragment(starredFragment);
	}

	@Override
	public void onWatchedSelected() {
		if (watchedFragment == null) {
			watchedFragment = WatchedReposFragment.newInstance();
		}

		setFragment(watchedFragment);
	}

	@Override
	public void onFollowersSelected() {
		if (followersFragment == null) {
			followersFragment = FollowersFragment.newInstance();
		}

		setFragment(followersFragment);
	}

	@Override
	public void onFollowingSelected() {
		if (followingFragment == null) {
			followingFragment = FollowingFragment.newInstance();
		}

		setFragment(followingFragment);
	}

	@Override
	public void onMenuItemSelected(@NonNull MenuItem item) {
		setTitle(item.text);
		closeMenu();
	}

	@Override
	public void closeMenu() {
		if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START)) {
			mDrawerLayout.closeDrawer(Gravity.START);
		}
	}

	@Override
	public void onOrganizationsSelected() {
		if (organizationsFragmet == null) {
			organizationsFragmet = OrganzationsFragment.newInstance();
		}
		setFragment(organizationsFragmet);
	}

	@Override
	public void onUserEventsSelected() {
		if (eventsFragment == null && username != null) {
			eventsFragment = EventsListFragment.newInstance(username);
		}
		setFragment(eventsFragment);
	}

	@Override
	public void onBackPressed() {
		if ((mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START))) {
			closeMenu();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (iabHelper != null) {
			iabHelper.dispose();
		}
		iabHelper = null;
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info) {
		if (result.isFailure()) {
			invalidateOptionsMenu();
		} else if (info.getSku().equals(IabConstants.SKU_DONATE)) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(IabConstants.SKU_DONATE, true);
			editor.apply();
			invalidateOptionsMenu();
		}
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inv) {
		if (result.isFailure()) {
			if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean(IabConstants.SKU_DONATE, true);
				editor.apply();
			}
			invalidateOptionsMenu();
		} else {
			boolean iabDonatePurchased = inv.hasPurchase(IabConstants.SKU_DONATE);
			if (iabDonatePurchased) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean(IabConstants.SKU_DONATE, true);
				editor.apply();
			}
			invalidateOptionsMenu();
		}
	}

	private class DonateMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener {

		public DonateMenu(Context context, View anchor) {
			super(context, anchor);
			inflate(R.menu.main_menu_donate);
			setOnMenuItemClickListener(this);
		}

		@Override
		public boolean onMenuItemClick(android.view.MenuItem item) {
			return onOptionsItemSelected(item);
		}
	}

	private class MainMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener {
		public MainMenu(Context context, View anchor) {
			super(context, anchor);
			inflate(R.menu.main_menu);
			setOnMenuItemClickListener(this);
		}

		@Override
		public boolean onMenuItemClick(android.view.MenuItem item) {
			return onOptionsItemSelected(item);
		}
	}
}
