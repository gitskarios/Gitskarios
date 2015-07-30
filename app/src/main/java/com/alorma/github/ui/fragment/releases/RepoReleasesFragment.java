package com.alorma.github.ui.fragment.releases;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.Release;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.RepoReleaseClient;
import com.alorma.github.ui.adapter.ReleasesAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.ui.listeners.TitleProvider;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by a557114 on 29/07/2015.
 */
public class RepoReleasesFragment extends PaginatedListFragment<List<Release>, ReleasesAdapter>
        implements TitleProvider, PermissionsManager {

    private static final String REPO_INFO = "REPO_INFO";
    private static final String REPO_PERMISSIONS = "REPO_PERMISSIONS";

    private RepoInfo repoInfo;
    private Permissions permissions;

    public static RepoReleasesFragment newInstance(RepoInfo info, Permissions permissions) {
        RepoReleasesFragment repoReleasesFragment = new RepoReleasesFragment();

        Bundle args = new Bundle();

        args.putParcelable(REPO_INFO, info);
        args.putParcelable(REPO_PERMISSIONS, permissions);

        repoReleasesFragment.setArguments(args);

        return repoReleasesFragment;
    }

    @Override
    protected void loadArguments() {
        repoInfo = getArguments().getParcelable(REPO_INFO);
        permissions = getArguments().getParcelable(REPO_PERMISSIONS);
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        RepoReleaseClient client = new RepoReleaseClient(getActivity(), repoInfo, 0);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);

        RepoReleaseClient client = new RepoReleaseClient(getActivity(), repoInfo, 0);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void onResponse(List<Release> releases, boolean refreshing) {
        if (releases.size() > 0) {
            hideEmpty();
            if (getAdapter() != null) {
                getAdapter().addAll(releases);
            } else {
                ReleasesAdapter adapter = new ReleasesAdapter(LayoutInflater.from(getActivity()), repoInfo);
                adapter.addAll(releases);
                setAdapter(adapter);
            }
        } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty();
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            if (error != null && error.getResponse() != null) {
                setEmpty(error.getResponse().getStatus());
            }
        }
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return null;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_releases;
    }

    @Override
    public void setPermissions(boolean admin, boolean push, boolean pull) {
        this.permissions = new Permissions();
        this.permissions.admin = admin;
        this.permissions.push = push;
        this.permissions.pull = pull;
    }

    @Override
    public int getTitle() {
        return R.string.releases;
    }
}
