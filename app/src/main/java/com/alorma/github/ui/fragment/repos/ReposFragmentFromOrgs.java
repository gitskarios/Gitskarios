package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.services.repos.GithubReposClient;
import com.alorma.github.sdk.services.repos.UserReposClient;
import com.alorma.github.sdk.services.repos.UserReposFromOrganizationClient;

public class ReposFragmentFromOrgs extends BaseReposListFragment {

    public static ReposFragmentFromOrgs newInstance() {
        return new ReposFragmentFromOrgs();
    }

    @Override
    protected void loadArguments() {

    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        UserReposFromOrganizationClient client = new UserReposFromOrganizationClient(getActivity());
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        UserReposFromOrganizationClient client = new UserReposFromOrganizationClient(getActivity(), page);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_repositories;
    }

}
