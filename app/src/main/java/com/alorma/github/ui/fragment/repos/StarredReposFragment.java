package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.services.repos.GithubReposClient;
import com.alorma.github.sdk.services.repos.StarredReposClient;

public class StarredReposFragment extends BaseReposListFragment {

    private String username;

    public static StarredReposFragment newInstance() {
        return new StarredReposFragment();
    }

    public static StarredReposFragment newInstance(String username) {
        StarredReposFragment reposFragment = new StarredReposFragment();
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
        GithubReposClient client;

        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }

        client = new StarredReposClient(getActivity(), username);

        client.setOnResultCallback(this);
        client.execute();
    }


    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        StarredReposClient client = new StarredReposClient(getActivity(), username, page);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_starred_repositories;
    }

    @Override
    protected void loadArguments() {

    }
}
