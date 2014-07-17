package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;

import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.repos.BaseReposClient;
import com.alorma.github.sdk.services.repos.StarredReposClient;
import com.alorma.github.sdk.services.repos.UserReposClient;
import com.alorma.github.sdk.services.repos.WatchedReposClient;
import com.alorma.github.ui.adapter.repos.ReposAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;

import java.util.ArrayList;

public class WatchedReposFragment extends PaginatedListFragment<ListRepos> {
    private static final String COLOR = "COLOR";

    private String username;
    private ReposAdapter reposAdapter;
    private int textColor = -1;

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
    public static WatchedReposFragment newInstance(String username, int color) {
        WatchedReposFragment reposFragment = new WatchedReposFragment();
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
        WatchedReposClient client;

        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
            textColor = getArguments().getInt(COLOR);
        }

        client = new WatchedReposClient(getActivity(), username);

        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
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

    private void setUpList() {
        if (textColor != -1) {
            reposAdapter = new ReposAdapter(getActivity(), new ArrayList<Repo>(), textColor);
        } else {
            reposAdapter = new ReposAdapter(getActivity(), new ArrayList<Repo>());
        }
        setListAdapter(reposAdapter);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;

        if (reposAdapter != null) {
            reposAdapter.setTextTitleColor(textColor);
        }
    }

}
