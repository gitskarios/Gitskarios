package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.services.repos.StarredReposClient;
import com.alorma.github.utils.RepoUtils;

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
    protected void loadArguments() {
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();
        loadArguments();

        setAction(new StarredReposClient(username, RepoUtils.sortOrder(getActivity())));
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        setAction(new StarredReposClient(username, RepoUtils.sortOrder(getActivity()), page));
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_starred_repositories;
    }

}
