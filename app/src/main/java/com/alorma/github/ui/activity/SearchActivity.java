package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.search.SearchReposFragment;
import com.alorma.github.ui.fragment.search.SearchUsersFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 31/01/2015.
 */
public class SearchActivity extends BackActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private SearchView searchView;
    private SearchReposFragment searchReposFragment;
    private SearchUsersFragment searchUsersFragment;
    private ViewPager viewPager;
    private List<Fragment> listFragments;
    private String lastQuery;

    public static Intent launchIntent(Context context) {
        return new Intent(context, SearchActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle("");

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabStrip);

        viewPager = (ViewPager) findViewById(R.id.pager);

        searchReposFragment = SearchReposFragment.newInstance(null);
        searchUsersFragment = SearchUsersFragment.newInstance(null);

        listFragments = new ArrayList<>();
        listFragments.add(searchReposFragment);
        listFragments.add(searchUsersFragment);

        viewPager.setAdapter(new NavigationPagerAdapter(getSupportFragmentManager(), listFragments));
        if (ViewCompat.isLaidOut(tabLayout)) {
            tabLayout.setupWithViewPager(viewPager);
        } else {
            tabLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    tabLayout.setupWithViewPager(viewPager);

                    tabLayout.removeOnLayoutChangeListener(this);
                }
            });
        }
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
                    return getString(R.string.navigation_repos_search);
                case 1:
                    return getString(R.string.navigation_people);
            }
            return "";
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (getToolbar() != null) {
            getToolbar().inflateMenu(R.menu.people_menu);

            MenuItem searchItem = menu.findItem(R.id.action_search);

            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return false;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    clearSearch();
                    return false;
                }
            });


            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setIconifiedByDefault(false);
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(this);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItemCompat.expandActionView(searchItem);

        if (searchView != null) {
            searchView.requestFocus();
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void clearSearch() {
        if (searchUsersFragment != null) {
            getFragmentManager().popBackStack();
            searchUsersFragment = null;
        }

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        if (!s.equals(lastQuery)) {
            lastQuery = s;
            search(s);
        }
        return true;
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
        }
        if (searchUsersFragment != null) {
            searchUsersFragment.setQuery(query);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
