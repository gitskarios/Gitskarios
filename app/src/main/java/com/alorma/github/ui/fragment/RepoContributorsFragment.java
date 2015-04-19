package com.alorma.github.ui.fragment;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Contributor;
import com.alorma.github.sdk.bean.dto.response.ListContributors;
import com.alorma.github.sdk.bean.dto.response.ListUsers;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.gitskarios.basesdk.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetRepoContributorsClient;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.users.BaseUsersListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 11/04/2015.
 */
public class RepoContributorsFragment extends BaseUsersListFragment {

    private static final String REPO_INFO = "REPO_INFO";
    private static final String OWNER_USER = "OWNER_USER";
    private RepoInfo repoInfo;
    private User owner;

    public static RepoContributorsFragment newInstance(RepoInfo repoInfo, User owner) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);
        bundle.putParcelable(OWNER_USER, owner);

        RepoContributorsFragment fragment = new RepoContributorsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            repoInfo = getArguments().getParcelable(REPO_INFO);
            owner = getArguments().getParcelable(OWNER_USER);
        }
    }

    @Override
    protected UsersAdapter setUpList(ListUsers users) {
        UsersAdapter adapter = super.setUpList(users);
        adapter.setRepoOwner(owner.login);
        return adapter;
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        GetRepoContributorsClient contributorsClient = new GetRepoContributorsClient(getActivity(), repoInfo);
        contributorsClient.setOnResultCallback(new ContributorsCallback());
        contributorsClient.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        GetRepoContributorsClient contributorsClient = new GetRepoContributorsClient(getActivity(), repoInfo, page);
        contributorsClient.setOnResultCallback(new ContributorsCallback());
        contributorsClient.execute();
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_person;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_contributors;
    }

    private class ContributorsCallback implements BaseClient.OnResultCallback<ListContributors> {
        @Override
        public void onResponseOk(ListContributors contributors, Response r) {
            ListUsers users = new ListUsers();

            users.add(owner);
            for (Contributor contributor : contributors) {
                if (!contributor.author.login.equalsIgnoreCase(repoInfo.owner)) {
                    users.add(users.size(), contributor.author);
                }
            }
            RepoContributorsFragment.this.onResponseOk(users, r);
        }

        @Override
        public void onFail(RetrofitError error) {

        }
    }
}
