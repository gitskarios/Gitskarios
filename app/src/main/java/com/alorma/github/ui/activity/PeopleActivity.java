package com.alorma.github.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.orgs.OrganzationsFragment;
import com.alorma.github.ui.fragment.users.FollowersFragment;
import com.alorma.github.ui.fragment.users.FollowingFragment;
import com.alorma.github.ui.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 31/01/2015.
 */
public class PeopleActivity extends BackActivity {

	private static final String USERNAME = "USERNAME";
	
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

		SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabStrip);

		slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);
		slidingTabLayout.setDividerColors(Color.TRANSPARENT);

		viewPager = (ViewPager) findViewById(R.id.pager);

		String username = null;
		if (getIntent().getExtras() != null) {
			username = getIntent().getExtras().getString(USERNAME);
		}
		
		FollowersFragment followersFragment = FollowersFragment.newInstance(username);
		FollowingFragment followingFragment =  FollowingFragment.newInstance(username);
		OrganzationsFragment organzationsFragment = OrganzationsFragment.newInstance(username);
		
		listFragments = new ArrayList<>();
		listFragments.add(followersFragment);
		listFragments.add(followingFragment);
		//listFragments.add(organzationsFragment);

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
				/*case 2:
					return getString(R.string.menu_organizations);*/
			}
			return "";
		}
	}
}
