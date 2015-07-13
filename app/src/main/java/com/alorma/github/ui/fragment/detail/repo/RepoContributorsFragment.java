package com.alorma.github.ui.fragment.detail.repo;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.alorma.github.R;
import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.sdk.bean.dto.response.Contributor;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoContributorsClient;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 11/04/2015.
 */
public class RepoContributorsFragment extends PaginatedListFragment<List<Contributor>, UsersAdapter> implements TitleProvider, PermissionsManager, BackManager {

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
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(getActivity(), 2);
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return null;
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

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_person;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_contributors;
    }

    @Override
    protected void onResponse(List<Contributor> contributors, boolean refreshing) {

    }

    private class ContributorsCallback implements BaseClient.OnResultCallback<List<Contributor>> {
        @Override
        public void onResponseOk(List<Contributor> contributors, Response r) {
            hideEmpty();
            if (contributors != null) {
                List<User> users = new ArrayList<>();

                users.add(owner);
                for (Contributor contributor : contributors) {
                    if (contributor != null
                            && contributor.author != null
                            && contributor.author.login != null
                            && !contributor.author.login.equalsIgnoreCase(repoInfo.owner)) {
                        users.add(users.size(), contributor.author);
                    }
                }
                if (getAdapter() == null) {
                    UsersAdapter adapter = new UsersAdapter(LayoutInflater.from(getActivity()));
                    adapter.addAll(users);
                    setAdapter(adapter);
                } else {
                    getAdapter().addAll(users);
                }
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
