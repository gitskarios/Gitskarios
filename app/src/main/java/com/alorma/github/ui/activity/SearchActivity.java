package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.search.SearchReposFragment;
import com.alorma.github.ui.fragment.search.SearchUsersFragment;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 31/01/2015.
 */
public class SearchActivity extends BackActivity {

    private EditText searchView;
    private SearchReposFragment searchReposFragment;
    private SearchUsersFragment searchUsersFragment;
    private ViewPager viewPager;
    private String lastQuery;

    public static Intent launchIntent(Context context) {
        return new Intent(context, SearchActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle("");

        searchView = (EditText) findViewById(R.id.searchView);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabStrip);

        viewPager = (ViewPager) findViewById(R.id.pager);

        searchReposFragment = SearchReposFragment.newInstance(null);
        searchUsersFragment = SearchUsersFragment.newInstance(null);

        List<Fragment> listFragments = new ArrayList<>();
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
            getToolbar().inflateMenu(R.menu.search_activity_menu);

            MenuItem searchItem = menu.findItem(R.id.action_search);

            IconicsDrawable searchIcon = new IconicsDrawable(getApplicationContext(), Octicons.Icon.oct_search).actionBar().colorRes(R.color.gray_github_medium);

            searchItem.setIcon(searchIcon);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            if (searchView != null && searchView.getText() != null) {
                String searchText = searchView.getText().toString();
                if (!TextUtils.isEmpty(searchText)) {
                    if (!searchText.equals(lastQuery)) {
                        lastQuery = searchText;
                        search(searchText);
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
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
