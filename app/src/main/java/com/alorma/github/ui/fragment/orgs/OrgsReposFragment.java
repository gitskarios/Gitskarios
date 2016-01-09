package com.alorma.github.ui.fragment.orgs;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.services.orgs.OrgsReposClient;
import com.alorma.github.ui.fragment.repos.BaseReposListFragment;
import com.alorma.github.utils.RepoUtils;

public class OrgsReposFragment extends BaseReposListFragment {

    private static final String ORGANIZATION = "ORG";
    private String org;

    public static OrgsReposFragment newInstance() {
        return new OrgsReposFragment();
    }

    public static OrgsReposFragment newInstance(String orgName) {
        OrgsReposFragment reposFragment = new OrgsReposFragment();
        if (orgName != null) {
            Bundle bundle = new Bundle();
            bundle.putString(ORGANIZATION, orgName);

            reposFragment.setArguments(bundle);
        }
        return reposFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            org = getArguments().getString(ORGANIZATION);
        }
    }

    @Override
    protected void loadArguments() {

    }

    @Override
    protected void executeRequest() {
        super.executeRequest();
        setAction(new OrgsReposClient(org, RepoUtils.sortOrder(getActivity())));
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        setAction(new OrgsReposClient(org, RepoUtils.sortOrder(getActivity()), page));
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_repositories;
    }
}
