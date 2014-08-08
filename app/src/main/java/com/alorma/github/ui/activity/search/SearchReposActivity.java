package com.alorma.github.ui.activity.search;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.search.SearchReposFragment;

/**
 * Created by Bernat on 08/08/2014.
 */
public class SearchReposActivity extends BackActivity implements SearchReposFragment.OnSearchReposListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String query = getIntent().getStringExtra(SearchManager.QUERY);

        setTitle(query);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        SearchReposFragment fragment = SearchReposFragment.newInstance();
        fragment.setQuery(query);
        fragment.setOnSearchReposListener(this);
        ft.replace(android.R.id.content, fragment);
        ft.commit();
    }

    @Override
    public void onRepoItemSelected(Repo repo) {
        Intent intent = RepoDetailActivity.createLauncherActivity(this, repo.owner.login, repo.name, repo.description);
        startActivity(intent);
    }
}