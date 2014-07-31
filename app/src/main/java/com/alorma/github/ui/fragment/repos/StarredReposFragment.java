package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;

import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.services.repos.BaseReposClient;
import com.alorma.github.sdk.services.repos.StarredReposClient;

public class StarredReposFragment extends BaseReposListFragment {
    private static final String COLOR = "COLOR";

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
    public static StarredReposFragment newInstance(String username, int color) {
        StarredReposFragment reposFragment = new StarredReposFragment();
        if (username != null) {
            Bundle bundle = new Bundle();
            bundle.putString(USERNAME, username);
            bundle.putInt(COLOR, color);

            reposFragment.setArguments(bundle);
        }
        return reposFragment;
    }

    @Override
    protected void executeRequest() {
        BaseReposClient client;

        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
            textColor = getArguments().getInt(COLOR);
        }

        client = new StarredReposClient(getActivity(), username);

        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
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

    public void setTextColor(int textColor) {
        this.textColor = textColor;

        if (reposAdapter != null) {
            reposAdapter.setTextTitleColor(textColor);
        }
    }

    @Override
    protected boolean useFab() {
        return false;
    }
}
