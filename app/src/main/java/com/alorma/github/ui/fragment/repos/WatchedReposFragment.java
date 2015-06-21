package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.services.repos.WatchedReposClient;

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
        super.executeRequest();
        WatchedReposClient client;

        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }

        client = new WatchedReposClient(getActivity(), username);

        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        WatchedReposClient client = new WatchedReposClient(getActivity(), username, page);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_watched_repositories;
    }

    @Override
    protected void loadArguments() {

    }
}
