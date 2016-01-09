package com.alorma.github.ui.fragment.repos;

import com.alorma.github.R;
import com.alorma.github.sdk.services.repos.MemberReposClient;

/**
 * Created by Bernat on 18/07/2015.
 */
public class MembershipReposFragment extends BaseReposListFragment {

    public static MembershipReposFragment newInstance() {
        return new MembershipReposFragment();
    }

    @Override
    protected void loadArguments() {

    }

    @Override
    protected void executeRequest() {
        super.executeRequest();
        setAction(new MemberReposClient());
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        setAction(new MemberReposClient(page));
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_member_repositories;
    }
}
