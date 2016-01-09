package com.alorma.github.ui.fragment.repos;

import com.alorma.github.R;
import com.alorma.github.sdk.services.repos.UserReposFromOrganizationClient;
import com.alorma.github.utils.RepoUtils;

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

        setAction(new UserReposFromOrganizationClient());
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        setAction(new UserReposFromOrganizationClient(null, RepoUtils.sortOrder(getActivity()), page));
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_repositories;
    }
}
