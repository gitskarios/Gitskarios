package com.alorma.github.ui.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.inapp.IabConstants;
import com.alorma.github.inapp.IabHelper;
import com.alorma.github.inapp.IabResult;
import com.alorma.github.inapp.Inventory;
import com.alorma.github.inapp.Purchase;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.animations.HeightEvaluator;
import com.alorma.github.ui.animations.WidthEvaluator;
import com.alorma.github.ui.fragment.orgs.OrganzationsFragment;
import com.alorma.github.ui.fragment.users.FollowersFragment;
import com.alorma.github.ui.fragment.users.FollowingFragment;
import com.alorma.github.ui.fragment.menu.MenuFragment;
import com.alorma.github.ui.fragment.menu.MenuItem;
import com.alorma.github.ui.fragment.repos.ReposFragment;
import com.alorma.github.ui.fragment.repos.StarredReposFragment;
import com.alorma.github.ui.fragment.repos.WatchedReposFragment;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.UUID;

public class MainActivity extends BaseActivity implements View.OnClickListener, MenuFragment.OnMenuItemSelectedListener, IabHelper.OnIabSetupFinishedListener, IabHelper.OnIabPurchaseFinishedListener, IabHelper.QueryInventoryFinishedListener {

	private static final long MENU_ANIMATION_TIME = 200;
	private TextView currentState;
	private ImageView chevron;
	private View chevronLy;
	private boolean isMenuOpen = false;
	private MenuFragment menuFragment;
	private View menuFragmentLy;
	private View searchIcon;
	private int menuHeight = -1;

	private ReposFragment reposFragment;
	private StarredReposFragment starredFragment;
	private WatchedReposFragment watchedFragment;
	private FollowersFragment followersFragment;
	private FollowingFragment followingFragment;
	private IabHelper iabHelper;
	private boolean iabEnabled;
	private OrganzationsFragment organizationsFragmet;

	public static void startActivity(Activity context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		currentState = (TextView) findViewById(R.id.currentState);

		IconDrawable chevronDown = new IconDrawable(this, Iconify.IconValue.fa_chevron_down);
		chevronDown.colorRes(R.color.gray_github_dark);

		chevron = (ImageView) findViewById(R.id.chevron);
		chevron.setImageDrawable(chevronDown);

		chevronLy = findViewById(R.id.chevronLy);
		chevronLy.setOnClickListener(this);

		menuFragmentLy = findViewById(R.id.menuContent);

		searchIcon = findViewById(R.id.searchIcon);
		searchIcon.setOnClickListener(this);

		checkIab();

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content, ReposFragment.newInstance());
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

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean iabDonatePurchased = preferences.getBoolean(IabConstants.SKU_DONATE, false);

		android.view.MenuItem donateItem = menu.findItem(R.id.action_donate);

		if (iabEnabled) {
			if (donateItem == null) {
				if (!iabDonatePurchased) {
					menu.add(0, R.id.action_donate, 0, R.string.action_donate);
				}
			} else if (iabDonatePurchased) {
				menu.removeItem(R.id.action_donate);
			}
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {

		if (item.getItemId() == R.id.action_donate) {
			try {
				iabHelper.launchPurchaseFlow(this, IabConstants.SKU_DONATE, 10001,
						this, UUID.randomUUID().toString());
				item.setEnabled(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.chevronLy:
				if (isMenuOpen) {
					hideMenu();
				} else {
					showMenu();
				}
				break;
			case R.id.searchIcon:
				Intent search = SearchReposActivity.createLauncherIntent(this);
				startActivity(search);
		}
	}

	private void showMenu() {

		menuFragmentLy.setVisibility(View.VISIBLE);

		IconDrawable chevronUp = new IconDrawable(this, Iconify.IconValue.fa_chevron_up);
		chevronUp.colorRes(R.color.gray_github_dark);
		chevron.setImageDrawable(chevronUp);

		isMenuOpen = true;

		Float dimension = getResources().getDimension(R.dimen.menuSize);
		ValueAnimator searchIconAnimator = ValueAnimator.ofObject(new WidthEvaluator(searchIcon), dimension.intValue(), 0);
		searchIconAnimator.setDuration(MENU_ANIMATION_TIME);
		searchIconAnimator.start();

		if (menuHeight == -1) {
			menuHeight = menuFragmentLy.getHeight();
		}

		ValueAnimator valueAnimator = ValueAnimator.ofObject(new HeightEvaluator(menuFragmentLy, true), 0, menuHeight);
		valueAnimator.setDuration(MENU_ANIMATION_TIME);
		valueAnimator.setInterpolator(new LinearInterpolator());
		valueAnimator.start();
	}

	private void hideMenu() {
		if (menuFragment != null) {
			IconDrawable chevronDown = new IconDrawable(this, Iconify.IconValue.fa_chevron_down);
			chevronDown.colorRes(R.color.gray_github_dark);
			chevron.setImageDrawable(chevronDown);

			isMenuOpen = false;

			Float dimension = getResources().getDimension(R.dimen.menuSize);
			ValueAnimator searchIconAnimator = ValueAnimator.ofObject(new WidthEvaluator(searchIcon), 0, dimension.intValue());
			searchIconAnimator.setDuration(MENU_ANIMATION_TIME);
			searchIconAnimator.start();

			ValueAnimator valueAnimator = ValueAnimator.ofObject(new HeightEvaluator(menuFragmentLy, false), menuHeight, 0);
			valueAnimator.setDuration(MENU_ANIMATION_TIME);
			valueAnimator.setInterpolator(new LinearInterpolator());
			valueAnimator.start();
		}
	}

	private void setFragment(Fragment fragment) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content, fragment);
		ft.commit();

		hideMenu();
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
		currentState.setText(item.text);
	}

	@Override
	public void closeMenu() {
		hideMenu();
	}

	@Override
	public void onOrganizationsSelected() {
		if (organizationsFragmet == null) {
			organizationsFragmet = OrganzationsFragment.newInstance();
		}
		setFragment(organizationsFragmet);
	}

	@Override
	public void onBackPressed() {
		if (isMenuOpen) {
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
}
