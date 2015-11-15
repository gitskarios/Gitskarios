package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;

import android.util.Pair;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.repos.GithubReposClient;
import com.alorma.github.sdk.services.repos.StarredReposClient;
import java.util.List;
import retrofit.client.Response;
import rx.Observer;
import rx.Subscriber;

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

        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }

        setAction(new StarredReposClient(getActivity(), username));
    }


    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        setAction(new StarredReposClient(getActivity(), username, page));
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_starred_repositories;
    }

    @Override
    protected void loadArguments() {

    }
}
