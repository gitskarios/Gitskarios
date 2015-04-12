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
import com.alorma.github.ui.fragment.orgs.OrganizationsFragment;
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
public class PeopleActivity extends BackActivity {

    private static final String USERNAME = "USERNAME";
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

        slidingTabLayout.setSelectedIndicatorColors(AttributesUtils.getAccentColor(this, R.style.AppTheme_Repos));
        slidingTabLayout.setDividerColors(Color.TRANSPARENT);

        viewPager = (ViewPager) findViewById(R.id.pager);

        String username = null;
        if (getIntent().getExtras() != null) {
            username = getIntent().getExtras().getString(USERNAME);
        }

        FollowingFragment followingFragment = FollowingFragment.newInstance(username);
        FollowersFragment followersFragment = FollowersFragment.newInstance(username);
        OrganizationsFragment organizationsFragment = OrganizationsFragment.newInstance(username);

        listFragments = new ArrayList<>();
        listFragments.add(followingFragment);
        listFragments.add(followersFragment);
        listFragments.add(organizationsFragment);

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
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            Intent intent = SearchActivity.launchIntent(this);
            startActivity(intent);
        }

        return true;
    }

    private void setFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

}
