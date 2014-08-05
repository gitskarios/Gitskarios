package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;

import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.services.repos.BaseReposClient;
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
        BaseReposClient client;

        if (progressBar != null) {
            progressBar.show();
        }

        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }

        client = new StarredReposClient(getActivity(), username);

        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {

        if (progressBar != null) {
            progressBar.show();
        }

        StarredReposClient client = new StarredReposClient(getActivity(), username, page);
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
}
