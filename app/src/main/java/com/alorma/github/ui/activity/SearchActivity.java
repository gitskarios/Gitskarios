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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.viewpager.NavigationPagerAdapter;
import com.alorma.github.ui.fragment.search.SearchReposFragment;
import com.alorma.github.ui.fragment.search.SearchUsersFragment;
import com.alorma.github.ui.listeners.TitleProvider;
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

        viewPager.setAdapter(new NavigationPagerAdapter(getSupportFragmentManager(), getResources(), listFragments));
        tabLayout.setupWithViewPager(viewPager);

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (textView.length() > 0) {
                    switch (actionId) {
                        case EditorInfo.IME_ACTION_DONE:
                        case EditorInfo.IME_ACTION_SEARCH:
                        case EditorInfo.IME_ACTION_SEND:
                        case EditorInfo.IME_ACTION_NEXT:
                        case EditorInfo.IME_ACTION_GO:
                            if (textView.getText() != null) {
                                search(textView.getText().toString());
                            }
                            break;
                    }
                }
                return false;
            }
        });
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
