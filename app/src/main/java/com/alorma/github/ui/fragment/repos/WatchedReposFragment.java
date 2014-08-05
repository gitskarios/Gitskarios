package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.repos.BaseReposClient;
import com.alorma.github.sdk.services.repos.StarredReposClient;
import com.alorma.github.sdk.services.repos.UserReposClient;
import com.alorma.github.sdk.services.repos.WatchedReposClient;
import com.alorma.github.ui.adapter.repos.ReposAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;

import java.util.ArrayList;

public class WatchedReposFragment extends BaseReposListFragment {

    private String username;

    public static WatchedReposFragment newInstance() {
        return new WatchedReposFragment();
    }

    public static WatchedReposFragment newInstance(String username) {
        WatchedReposFragment reposFragment = new WatchedReposFragment();
        if (username != null) {
            Bundle bundle = new Bundle();
            bundle.putString(USERNAME, username);

            reposFragment.setArguments(bundle);
        }
        return reposFragment;
    }

    @Override
    protected void executeRequest() {
        WatchedReposClient client;

        if (reposAdapter != null) {
            reposAdapter.clear();
        }

        if (swipe != null) {
            swipe.setRefreshing(true);
        }

        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }

        client = new WatchedReposClient(getActivity(), username);

        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {

        if (swipe != null) {
            swipe.setRefreshing(true);
        }

        WatchedReposClient client = new WatchedReposClient(getActivity(), username, page);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void onResponse(ListRepos repos) {
        if (reposAdapter == null) {
            setUpList();
        }
        reposAdapter.addAll(repos);
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_watched_repositories;
    }
}
