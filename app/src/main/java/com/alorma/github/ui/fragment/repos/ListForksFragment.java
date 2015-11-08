package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetForksClient;
import java.util.List;

/**
 * Created by a557114 on 05/09/2015.
 */
public class ListForksFragment extends BaseReposListFragment {

    private static final String REPO_INFO = "REPO_INFO";
    private RepoInfo repoInfo;

    public static ListForksFragment newInstance(RepoInfo repoInfo) {
        ListForksFragment reposFragment = new ListForksFragment();
        if (repoInfo != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(REPO_INFO, repoInfo);

            reposFragment.setArguments(bundle);
        }
        return reposFragment;
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();
        GetForksClient client;

        if (getArguments() != null) {
            repoInfo = getArguments().getParcelable(REPO_INFO);
        }

        if (repoInfo != null) {
            client = new GetForksClient(getActivity(), repoInfo);
            client.setSort(GetForksClient.STARGAZERS);
            client.setOnResultCallback(this);
            client.execute();
        }
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        GetForksClient client = new GetForksClient(getActivity(), repoInfo, page);
        client.setSort(GetForksClient.STARGAZERS);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void onResponse(List<Repo> repos, boolean refreshing) {
        super.onResponse(repos, refreshing);

        if (getAdapter() != null) {
            getAdapter().showOwnerNameExtra(false);
        }
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_forked_repositories;
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            repoInfo = getArguments().getParcelable(USERNAME);
        }
    }

    @Override
    protected boolean showAdapterOwnerName() {
        return true;
    }
}
