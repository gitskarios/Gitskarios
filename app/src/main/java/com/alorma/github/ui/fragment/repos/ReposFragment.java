package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;
import android.widget.Toast;

import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.repos.BaseReposClient;
import com.alorma.github.sdk.services.repos.UserReposClient;
import com.alorma.github.ui.adapter.repos.ReposAdapter;
import com.alorma.github.ui.fragment.base.PaginatedFabListFragment;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;

import java.util.ArrayList;

public class ReposFragment extends PaginatedFabListFragment<ListRepos> {
    private static final String COLOR = "COLOR";

    private String username;
    private ReposAdapter reposAdapter;
    private int textColor = -1;

    public static ReposFragment newInstance() {
        return new ReposFragment();
    }

    public static ReposFragment newInstance(String username) {
        ReposFragment reposFragment = new ReposFragment();
        if (username != null) {
            Bundle bundle = new Bundle();
            bundle.putString(USERNAME, username);

            reposFragment.setArguments(bundle);
        }
        return reposFragment;
    }

    public static ReposFragment newInstance(String username, int color) {
        ReposFragment reposFragment = new ReposFragment();
        if (username != null) {
            Bundle bundle = new Bundle();
            bundle.putString(USERNAME, username);
            bundle.putInt(COLOR, color);

            reposFragment.setArguments(bundle);
        }
        return reposFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
            textColor = getArguments().getInt(COLOR);
        }
        setUseFab(username == null);
    }

    @Override
    protected void executeRequest() {
        BaseReposClient client;

        client = new UserReposClient(getActivity(), username);

        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        UserReposClient client = new UserReposClient(getActivity(), username, page);
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

    @Override
    protected void onFabClick() {
        Toast.makeText(getActivity(), "Hello FAB", Toast.LENGTH_SHORT).show();
    }
}
