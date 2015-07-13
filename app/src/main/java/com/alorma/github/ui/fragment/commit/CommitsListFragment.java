package com.alorma.github.ui.fragment.commit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.commit.ListCommitsClient;
import com.alorma.github.ui.adapter.commit.CommitsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.BranchManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.ui.listeners.TitleProvider;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by Bernat on 07/09/2014.
 */
public class CommitsListFragment extends PaginatedListFragment<List<Commit>, CommitsAdapter> implements TitleProvider, BranchManager, PermissionsManager
        , BackManager {

    private static final String REPO_INFO = "REPO_INFO";

    private List<Commit> commits;

    private RepoInfo repoInfo;
    private StickyRecyclerHeadersDecoration headersDecoration;

    public static CommitsListFragment newInstance(RepoInfo repoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);

        CommitsListFragment fragment = new CommitsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onResponse(final List<Commit> commits, boolean refreshing) {
        if (this.commits == null || refreshing) {
            this.commits = new ArrayList<>();
        }
        if (commits != null && commits.size() > 0) {

            orderCommits(commits);

            if (getAdapter() == null || refreshing) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommitsAdapter commitsAdapter = new CommitsAdapter(LayoutInflater.from(getActivity()), false);
                        commitsAdapter.addAll(CommitsListFragment.this.commits);
                        commitsAdapter.setRepoInfo(repoInfo);
                        setAdapter(commitsAdapter);

                        if (headersDecoration == null) {
                            headersDecoration = new StickyRecyclerHeadersDecoration(getAdapter());
                            addItemDecoration(headersDecoration);
                        }
                    }
                });
            }

            // TODO lazy
            /*if (commitsAdapter.isLazyLoading()) {
                if (commitsAdapter != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commitsAdapter.setLazyLoading(false);
                            commitsAdapter.addAll(commits);
                        }
                    });
                }
            }*/
        } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty();
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty();
        }
    }

    @Override
    public void setEmpty(int statusCode) {
        super.setEmpty(statusCode);
        if (fab != null) {
            fab.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void hideEmpty() {
        super.hideEmpty();
        if (fab != null) {
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setPermissions(boolean admin, boolean push, boolean pull) {

    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    private void orderCommits(List<Commit> commits) {

        for (Commit commit : commits) {
            if (commit.commit.author.date != null) {
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                DateTime dt = formatter.parseDateTime(commit.commit.committer.date);

                Days days = Days.daysBetween(dt.withTimeAtStartOfDay(), new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay());

                commit.days = days.getDays();

                this.commits.add(commit);
            }
        }
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();
        ListCommitsClient client = new ListCommitsClient(getActivity(), repoInfo, 0);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        ListCommitsClient client = new ListCommitsClient(getActivity(), repoInfo, page);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            repoInfo = getArguments().getParcelable(REPO_INFO);
        }
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_diff;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_commits;
    }

    @Override
    public int getTitle() {
        return R.string.commits_fragment_title;
    }

    @Override
    protected boolean useFAB() {
        return false;
    }

    @Override
    public void setCurrentBranch(String branch) {
        if (repoInfo != null && !repoInfo.branch.equalsIgnoreCase(branch)) {
            repoInfo.branch = branch;

            if (getAdapter() != null) {
                getAdapter().clear();
            }
            startRefresh();
            refreshing = true;
            executeRequest();
        }
    }
}
