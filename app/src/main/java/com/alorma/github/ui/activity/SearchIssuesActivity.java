package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.issues.IssuesListFragment;

/**
 * Created by Bernat on 31/01/2015.
 */
public class SearchIssuesActivity extends BackActivity
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, IssuesListFragment.SearchClientRequest {

    private static String REPO_INFO = "REPO_INFO";

    private SearchView searchView;
    private RepoInfo repoInfo;
    private String query;
    private IssuesListFragment issuesListFragment;

    public static Intent launchIntent(Context context, RepoInfo repoInfo) {
        Intent intent = new Intent(context, SearchIssuesActivity.class);

        intent.putExtra(REPO_INFO, repoInfo);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues_search);

        setTitle("");

        if (getIntent().getExtras() != null) {
            repoInfo = (RepoInfo) getIntent().getExtras().getParcelable(REPO_INFO);

            issuesListFragment = IssuesListFragment.newInstance(repoInfo, true);
            issuesListFragment.setSearchClientRequest(this);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, issuesListFragment);
            ft.commit();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (getToolbar() != null) {
            getToolbar().inflateMenu(R.menu.search_issues_menu);

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
        if (query == null) {
            query = "";
        }

        query += "+repoInfo:" + repoInfo.owner + "/" + repoInfo.name;

        issuesListFragment.setRefreshing();
        issuesListFragment.clear();

        this.query = query;

        issuesListFragment.executeSearch();
    }

    @Override
    public String request() {
        return this.query;
    }
}
