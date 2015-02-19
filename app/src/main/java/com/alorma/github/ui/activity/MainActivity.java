package com.alorma.github.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;

import com.alorma.github.R;
import com.alorma.github.inapp.IabConstants;
import com.alorma.github.inapp.IabHelper;
import com.alorma.github.inapp.IabResult;
import com.alorma.github.inapp.Inventory;
import com.alorma.github.inapp.Purchase;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.fragment.NotificationsFragment;
import com.alorma.github.ui.fragment.events.EventsListFragment;
import com.alorma.github.ui.fragment.menu.MenuFragment;
import com.alorma.github.ui.fragment.menu.MenuItem;
import com.alorma.github.ui.fragment.repos.ReposFragment;
import com.alorma.github.ui.fragment.repos.StarredReposFragment;
import com.alorma.github.ui.fragment.repos.WatchedReposFragment;
import com.alorma.github.ui.fragment.search.SearchReposFragment;
import com.alorma.github.ui.view.NotificationsActionProvider;

public class MainActivity extends BaseActivity implements MenuFragment.OnMenuItemSelectedListener, IabHelper.OnIabSetupFinishedListener,
		IabHelper.OnIabPurchaseFinishedListener, IabHelper.QueryInventoryFinishedListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, NotificationsActionProvider.OnNotificationListener {

	private MenuFragment menuFragment;

	private ReposFragment reposFragment;
	private StarredReposFragment starredFragment;
	private WatchedReposFragment watchedFragment;
	private IabHelper iabHelper;
	private boolean iabEnabled;
	private EventsListFragment eventsFragment;
	private SearchReposFragment searchReposFragment;
	private SearchView searchView;
	private android.view.MenuItem searchItem;

	public static void startActivity(Activity context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		GetAuthUserClient client = new GetAuthUserClient(this);
		client.execute();

		checkIab();

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		menuFragment = MenuFragment.newInstance();
		menuFragment.setOnMenuItemSelectedListener(this);
		ft.replace(R.id.menuContent, menuFragment);
		ft.commit();
	}

	private void checkIab() {
		iabHelper = new IabHelper(this, IabConstants.KEY);
		iabHelper.startSetup(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		if (getToolbar() != null) {
			getToolbar().inflateMenu(R.menu.main_menu);

			searchItem = menu.findItem(R.id.action_search);

			MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
				@Override
				public boolean onMenuItemActionExpand(android.view.MenuItem item) {
					return false;
				}

				@Override
				public boolean onMenuItemActionCollapse(android.view.MenuItem item) {
					clearSearch();
					return false;
				}
			});

			searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
			searchView.setSubmitButtonEnabled(true);
			searchView.setOnQueryTextListener(this);
			searchView.setOnCloseListener(this);

			android.view.MenuItem notificationsItem = menu.findItem(R.id.action_notifications);

			NotificationsActionProvider notificationProvider = (NotificationsActionProvider) MenuItemCompat.getActionProvider(notificationsItem);

			notificationProvider.setOnNotificationListener(this);

		}

		return true;
	}

	private void clearSearch() {
		if (searchReposFragment != null) {
			getFragmentManager().popBackStack();
			searchReposFragment = null;
		}

	}

	@Override
	public boolean onQueryTextSubmit(String s) {
		search(s);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String s) {
		return false;
	}

	@Override
	public boolean onClose() {
		clearSearch();
		return false;
	}

	private void search(String query) {
		if (searchReposFragment != null) {
			searchReposFragment.setQuery(query);
		} else {
			searchReposFragment = SearchReposFragment.newInstance(query);
			setFragment(searchReposFragment, true);
		}
	}

	@Override
	public void onIabSetupFinished(IabResult result) {
		iabEnabled = result.isSuccess();
		if (iabEnabled) {
			try {
				iabHelper.queryInventoryAsync(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void setFragment(Fragment fragment) {
		if (searchView != null) {
			if (!searchView.isIconified()) {
				searchView.setIconified(true);
			}
		}
		setFragment(fragment, false);
	}

	private void setFragment(Fragment fragment, boolean addToBackStack) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content, fragment);
		if (addToBackStack) {
			ft.addToBackStack(null);
		}
		ft.commit();
	}

	@Override
	public boolean onProfileSelected() {
		Intent launcherIntent = ProfileActivity.createLauncherIntent(this);
		startActivity(launcherIntent);
		return false;
	}

	@Override
	public boolean onReposSelected() {
		if (reposFragment == null) {
			reposFragment = ReposFragment.newInstance();
		}

		setFragment(reposFragment);
		return true;
	}

	@Override
	public boolean onStarredSelected() {
		if (starredFragment == null) {
			starredFragment = StarredReposFragment.newInstance();
		}

		setFragment(starredFragment);
		return true;
	}

	@Override
	public boolean onWatchedSelected() {
		if (watchedFragment == null) {
			watchedFragment = WatchedReposFragment.newInstance();
		}

		setFragment(watchedFragment);
		return true;
	}

	@Override
	public boolean onPeopleSelected() {
		Intent intent = PeopleActivity.launchIntent(this);
		startActivity(intent);
		return false;
	}

	@Override
	public void onMenuItemSelected(@NonNull MenuItem item, boolean changeTitle) {
		if (changeTitle) {
			setTitle(item.text);
		}
		closeMenu();
	}

	@Override
	public boolean onUserEventsSelected() {
		GitskariosSettings settings = new GitskariosSettings(this);
		String user = settings.getAuthUser(null);
		if (user != null) {
			if (eventsFragment == null) {
				eventsFragment = EventsListFragment.newInstance(user);
			}
			setFragment(eventsFragment);
		}
		return true;
	}

	@Override
	public boolean onSettingsSelected() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
		return false;
	}

	@Override
	public boolean onAboutSelected() {
		Intent intent = AboutActivity.launchIntent(this);
		startActivity(intent);
		return false;
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

	@Override
	public void onBackPressed() {
		if (!searchView.isIconified()) {
			searchView.setIconified(true);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onNotificationRequested() {
		setTitle(R.string.notifications);
		setFragment(NotificationsFragment.newInstance());
	}

	@Override
	public void onNotificationInfoReceived(int notifications) {

	}
}
