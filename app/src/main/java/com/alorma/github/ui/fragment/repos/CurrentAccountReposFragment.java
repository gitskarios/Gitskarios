package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;

import android.util.Pair;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.repos.GithubReposClient;
import com.alorma.github.sdk.services.repos.UserReposClient;
import java.util.List;
import retrofit.client.Response;
import rx.Observer;

public class CurrentAccountReposFragment extends BaseReposListFragment {

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

    public static CurrentAccountReposFragment newInstance() {
        return new CurrentAccountReposFragment();
    }

    @Override
    protected void onResponse(List<Repo> repos, boolean refreshing) {
        super.onResponse(repos, refreshing);

        if (getAdapter() != null) {
            getAdapter().showOwnerNameExtra(false);
        }
    }

    public static CurrentAccountReposFragment newInstance(String username) {
        CurrentAccountReposFragment currentAccountReposFragment = new CurrentAccountReposFragment();
        if (username != null) {
            Bundle bundle = new Bundle();
            bundle.putString(USERNAME, username);

            currentAccountReposFragment.setArguments(bundle);
        }
        return currentAccountReposFragment;
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

        GithubReposClient client = new UserReposClient(getActivity(), username);
        client.observable().subscribe(subscriber);
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        UserReposClient client = new UserReposClient(getActivity(), username, page);
        client.observable().subscribe(subscriber);
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_repositories;
    }

}
