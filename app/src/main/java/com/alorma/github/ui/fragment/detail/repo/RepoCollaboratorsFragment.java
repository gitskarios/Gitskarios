package com.alorma.github.ui.fragment.detail.repo;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListUsers;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoCollaboratorsClient;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.users.BaseUsersListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by Bernat on 11/04/2015.
 */
public class RepoCollaboratorsFragment extends BaseUsersListFragment {

    private static final String REPO_INFO = "REPO_INFO";
    private RepoInfo repoInfo;

    public static RepoCollaboratorsFragment newInstance(RepoInfo repoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);

        RepoCollaboratorsFragment fragment = new RepoCollaboratorsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            repoInfo = getArguments().getParcelable(REPO_INFO);
        }
    }

    @Override
    protected UsersAdapter setUpList(ListUsers users) {
        UsersAdapter adapter = super.setUpList(users);
        adapter.setRepoOwner(repoInfo.owner);
        return adapter;
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        GetRepoCollaboratorsClient contributorsClient = new GetRepoCollaboratorsClient(getActivity(), repoInfo);
        contributorsClient.setOnResultCallback(this);
        contributorsClient.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        GetRepoCollaboratorsClient contributorsClient = new GetRepoCollaboratorsClient(getActivity(), repoInfo, page);
        contributorsClient.setOnResultCallback(this);
        contributorsClient.execute();
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_person;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_collaborators;
    }
}
