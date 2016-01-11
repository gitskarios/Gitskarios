package com.alorma.github.ui.fragment.commit;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubStatus;
import com.alorma.github.sdk.bean.dto.response.GithubStatusResponse;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.services.repo.GetShaCombinedStatus;
import com.alorma.github.ui.adapter.commit.GithubStatusAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by a557114 on 06/09/2015.
 */
public class CommitStatusFragment extends LoadingListFragment<GithubStatusAdapter> {

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
        GetShaCombinedStatus getShaCombinedStatus = new GetShaCombinedStatus(commitInfo.repoInfo, commitInfo.sha);
        getShaCombinedStatus.observable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Pair<GithubStatusResponse, Integer>, GithubStatusResponse>() {
                    @Override
                    public GithubStatusResponse call(Pair<GithubStatusResponse, Integer> githubStatusResponseIntegerPair) {
                        return githubStatusResponseIntegerPair.first;
                    }
                })
                .subscribe(new StatusCallback());
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        GetShaCombinedStatus getShaCombinedStatus = new GetShaCombinedStatus(commitInfo.repoInfo, commitInfo.sha, page);
        getShaCombinedStatus.observable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Pair<GithubStatusResponse, Integer>, GithubStatusResponse>() {
                    @Override
                    public GithubStatusResponse call(Pair<GithubStatusResponse, Integer> githubStatusResponseIntegerPair) {
                        return githubStatusResponseIntegerPair.first;
                    }
                })
                .subscribe(new StatusCallback());
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            commitInfo = (CommitInfo) getArguments().getParcelable(COMMIT_INFO);
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

    private class StatusCallback extends Subscriber<GithubStatusResponse> {

        public StatusCallback() {

        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                setEmpty();
            }
        }

        @Override
        public void onNext(GithubStatusResponse githubStatuses) {
            List<GithubStatus> statuses = githubStatuses.statuses;
            if (statuses != null && statuses.size() > 0) {
                hideEmpty();
                if (getAdapter() != null) {
                    getAdapter().addAll(statuses);
                } else {
                    GithubStatusAdapter adapter = new GithubStatusAdapter(LayoutInflater.from(getActivity()));
                    adapter.addAll(statuses);
                    setAdapter(adapter);
                }
            } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                setEmpty();
            }
        }
    }
}
