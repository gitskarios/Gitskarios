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
    private Observer<? super Pair<List<Repo>, Response>> subscriber = new Observer<Pair<List<Repo>, Response>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Pair<List<Repo>, Response> listResponsePair) {
            onResponseOk(listResponsePair.first, listResponsePair.second);
        }
    };

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
        client.observable().subscribe(subscriber);
    }


    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        StarredReposClient client = new StarredReposClient(getActivity(), username, page);
        client.observable().subscribe(subscriber);
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_starred_repositories;
    }

    @Override
    protected void loadArguments() {

    }
}
