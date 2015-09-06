package com.alorma.github.ui.fragment.commit;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.alorma.github.R;
import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.sdk.bean.dto.response.GithubStatus;
import com.alorma.github.sdk.bean.dto.response.GithubStatusResponse;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.services.repo.GetShaCombinedStatus;
import com.alorma.github.ui.adapter.commit.GithubStatusAdapter;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by a557114 on 06/09/2015.
 */
public class CommitStatusFragment extends PaginatedListFragment<List<GithubStatus>, GithubStatusAdapter> {

    public static final String COMMIT_INFO = "COMMIT_INFO";
    private CommitInfo commitInfo;

    public static CommitStatusFragment newInstance(CommitInfo commitInfo) {
        CommitStatusFragment fragment = new CommitStatusFragment();

        Bundle args = new Bundle();

        args.putParcelable(COMMIT_INFO, commitInfo);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();
        GetShaCombinedStatus getShaCombinedStatus = new GetShaCombinedStatus(getActivity(), commitInfo.repoInfo, commitInfo.sha);
        getShaCombinedStatus.setOnResultCallback(new StatusCallback(this));
        getShaCombinedStatus.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        GetShaCombinedStatus getShaCombinedStatus = new GetShaCombinedStatus(getActivity(), commitInfo.repoInfo, commitInfo.sha, page);
        getShaCombinedStatus.setOnResultCallback(new StatusCallback(this));
        getShaCombinedStatus.execute();
    }

    @Override
    protected void onResponse(List<GithubStatus> githubStatuses, boolean refreshing) {
        if (githubStatuses.size() > 0) {
            hideEmpty();
            if (getAdapter() != null) {
                getAdapter().addAll(githubStatuses);
            } else {
                GithubStatusAdapter adapter = new GithubStatusAdapter(LayoutInflater.from(getActivity()));
                adapter.addAll(githubStatuses);
                setAdapter(adapter);
            }
        } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty(false);
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty(true);
        }
    }
    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            commitInfo = getArguments().getParcelable(COMMIT_INFO);
        }
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_git_commit;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_status;
    }

    private class StatusCallback implements BaseClient.OnResultCallback<GithubStatusResponse> {
        private BaseClient.OnResultCallback<List<GithubStatus>> callback;

        public StatusCallback(BaseClient.OnResultCallback<List<GithubStatus>> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponseOk(GithubStatusResponse githubStatusResponse, Response r) {
            if (callback != null) {
                callback.onResponseOk(githubStatusResponse.statuses, r);
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            if (callback != null) {
                callback.onFail(error);
            }
        }
    }
}
