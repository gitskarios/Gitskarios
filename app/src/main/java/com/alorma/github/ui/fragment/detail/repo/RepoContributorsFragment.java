package com.alorma.github.ui.fragment.detail.repo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Contributor;
import com.alorma.github.sdk.bean.dto.response.ListContributors;
import com.alorma.github.sdk.bean.dto.response.ListUsers;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoContributorsClient;
import com.alorma.github.ui.adapter.users.UsersAdapterSquare;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.basesdk.client.BaseClient;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 11/04/2015.
 */
public class RepoContributorsFragment extends BaseFragment implements TitleProvider, PermissionsManager, BackManager {

    private static final String REPO_INFO = "REPO_INFO";
    private static final String OWNER_USER = "OWNER_USER";
    private RepoInfo repoInfo;
    private User owner;
    private UsersAdapterSquare adapterSquare;

    public static RepoContributorsFragment newInstance(RepoInfo repoInfo, User owner) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);
        bundle.putParcelable(OWNER_USER, owner);

        RepoContributorsFragment fragment = new RepoContributorsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        adapterSquare = new UsersAdapterSquare(inflater);
        return inflater.inflate(R.layout.contributors_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterSquare);

        loadArguments();
        executeRequest();
    }

    protected void executeRequest() {
        GetRepoContributorsClient contributorsClient = new GetRepoContributorsClient(getActivity(), repoInfo);
        contributorsClient.setOnResultCallback(new ContributorsCallback());
        contributorsClient.execute();
    }

    @Override
    public int getTitle() {
        return R.string.contributors_fragment_title;
    }

    @Override
    public void setPermissions(boolean admin, boolean push, boolean pull) {

    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    protected void loadArguments() {
        if (getArguments() != null) {
            repoInfo = getArguments().getParcelable(REPO_INFO);
            owner = getArguments().getParcelable(OWNER_USER);
        }
    }

    private class ContributorsCallback implements BaseClient.OnResultCallback<ListContributors> {
        @Override
        public void onResponseOk(ListContributors contributors, Response r) {

            if (contributors != null) {
                ListUsers users = new ListUsers();

                users.add(owner);
                for (Contributor contributor : contributors) {
                    if (contributor != null
                            && contributor.author != null
                            && contributor.author.login != null
                            && !contributor.author.login.equalsIgnoreCase(repoInfo.owner)) {
                        users.add(users.size(), contributor.author);
                    }
                }
                adapterSquare.addAll(users);
            }

        }

        @Override
        public void onFail(RetrofitError error) {

        }

        protected void executePaginatedRequest(int page) {
            GetRepoContributorsClient contributorsClient = new GetRepoContributorsClient(getActivity(), repoInfo, page);
            contributorsClient.setOnResultCallback(new ContributorsCallback());
            contributorsClient.execute();
        }
    }
}
