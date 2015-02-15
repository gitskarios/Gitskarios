package com.alorma.github.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.orgs.OrganzationsFragment;
import com.alorma.github.ui.fragment.search.SearchUsersFragment;
import com.alorma.github.ui.fragment.users.FollowersFragment;
import com.alorma.github.ui.fragment.users.FollowingFragment;
import com.alorma.github.ui.view.SlidingTabLayout;
import com.alorma.github.utils.AttributesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 31/01/2015.
 */
public class PeopleActivity extends BackActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

	private static final String USERNAME = "USERNAME";
	private MenuItem searchItem;
	private SearchView searchView;
	private SearchUsersFragment searchUsersFragment;

	public static Intent launchIntent(Context context) {
		return new Intent(context, PeopleActivity.class);
	}

	public static Intent launchIntent(Context context, String username) {
		Intent intent = launchIntent(context);

		Bundle extras = new Bundle();
		extras.putString(USERNAME, username);

		intent.putExtras(extras);

		return intent;
	}

	private ViewPager viewPager;
	private List<Fragment> listFragments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_activity);

		setTitle(R.string.navigation_people);

		SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabStrip);

		slidingTabLayout.setSelectedIndicatorColors(AttributesUtils.getAccentColor(this, R.style.AppTheme_People));
		slidingTabLayout.setDividerColors(Color.TRANSPARENT);

		viewPager = (ViewPager) findViewById(R.id.pager);

		String username = null;
		if (getIntent().getExtras() != null) {
			username = getIntent().getExtras().getString(USERNAME);
		}

		FollowersFragment followersFragment = FollowersFragment.newInstance(username);
		FollowingFragment followingFragment = FollowingFragment.newInstance(username);
		OrganzationsFragment organzationsFragment = OrganzationsFragment.newInstance(username);

		listFragments = new ArrayList<>();
		listFragments.add(followersFragment);
		listFragments.add(followingFragment);
		listFragments.add(organzationsFragment);

		viewPager.setAdapter(new NavigationPagerAdapter(getFragmentManager(), listFragments));
		slidingTabLayout.setViewPager(viewPager);
	}

	private class NavigationPagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> listFragments;

		public NavigationPagerAdapter(FragmentManager fm, List<Fragment> listFragments) {
			super(fm);
			this.listFragments = listFragments;
		}

		@Override
		public Fragment getItem(int position) {
			return listFragments.get(position);
		}

		@Override
		public int getCount() {
			return listFragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return getString(R.string.navigation_following);
				case 1:
					return getString(R.string.navigation_followers);
				case 2:
					return getString(R.string.menu_organizations);
			}
			return "";
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		if (getToolbar() != null) {
			getToolbar().inflateMenu(R.menu.people_menu);

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
		}

		return true;
	}

	private void clearSearch() {
		if (searchUsersFragment != null) {
			getFragmentManager().popBackStack();
			searchUsersFragment = null;
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
		if (searchUsersFragment != null) {
			searchUsersFragment.setQuery(query);
		} else {
			searchUsersFragment = SearchUsersFragment.newInstance(query);
			setFragment(searchUsersFragment, true);
		}
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
	public void onBackPressed() {
		if (!searchView.isIconified()) {
			searchView.setIconified(true);
		} else {
			super.onBackPressed();
		}
	}
}
