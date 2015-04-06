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
import com.alorma.github.ui.fragment.orgs.OrganizationsFragment;
import com.alorma.github.ui.fragment.orgs.OrgsMembersFragment;
import com.alorma.github.ui.fragment.orgs.OrgsReposFragment;
import com.alorma.github.ui.fragment.users.FollowersFragment;
import com.alorma.github.ui.fragment.users.FollowingFragment;
import com.alorma.github.ui.view.SlidingTabLayout;
import com.alorma.github.utils.AttributesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 22/02/2015.
 */
public class OrganizationActivity extends BackActivity {

	private static final String ORG = "ORG";
	private ViewPager viewPager;

	public static Intent launchIntent(Context context, String orgName) {
		Intent intent = new Intent(context, OrganizationActivity.class);

		Bundle extras = new Bundle();
		extras.putString(ORG, orgName);

		intent.putExtras(extras);

		return intent;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.organization_activity);

		setTitle(R.string.navigation_people);

		SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabStrip);

		slidingTabLayout.setSelectedIndicatorColors(AttributesUtils.getAccentColor(this, R.style.AppTheme_Repos));
		slidingTabLayout.setDividerColors(Color.TRANSPARENT);

		viewPager = (ViewPager) findViewById(R.id.pager);

		String orgName = null;
		if (getIntent().getExtras() != null) {
			orgName = getIntent().getExtras().getString(ORG);
		}

		OrgsReposFragment orgReposFragment = OrgsReposFragment.newInstance(orgName);
		OrgsMembersFragment orgMembersFragment = OrgsMembersFragment.newInstance(orgName);

		ArrayList<Fragment> listFragments = new ArrayList<>();
		listFragments.add(orgReposFragment);
		listFragments.add(orgMembersFragment);

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
					return getString(R.string.navigation_orgs_repos);
				case 1:
					return getString(R.string.navigation_orgs_members);
			}
			return "";
		}
	}
}
