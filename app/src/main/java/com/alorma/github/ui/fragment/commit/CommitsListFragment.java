package com.alorma.github.ui.fragment.commit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.commit.ListCommitsClient;
import com.alorma.github.ui.activity.CommitDetailActivity;
import com.alorma.github.ui.adapter.commit.CommitsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.BranchManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.ui.listeners.TitleProvider;
import com.mikepenz.octicons_typeface_library.Octicons;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Bernat on 07/09/2014.
 */
public class CommitsListFragment extends PaginatedListFragment<List<Commit>> implements TitleProvider, BranchManager, PermissionsManager
        , BackManager {

    private static final String REPO_INFO = "REPO_INFO";

    private CommitsAdapter commitsAdapter;
    private List<Commit> commitsMap;
    private StickyListHeadersListView listView;

    private RepoInfo repoInfo;
    private TimeTickBroadcastReceiver timeTickBroadcastReceiver;

    public static CommitsListFragment newInstance(RepoInfo repoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);

        CommitsListFragment fragment = new CommitsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment_headers, null, false);
    }

    // TODO
    /*@Override
    protected void setupListView(View view) {
        listView = (StickyListHeadersListView) view.findViewById(android.R.id.list);
        if (listView != null) {
            listView.setDivider(getResources().getDrawable(R.drawable.divider_main));
            listView.setOnScrollListener(this);
            listView.setOnItemClickListener(this);
            listView.setAreHeadersSticky(true);
        }
    }*/

    @Override
    protected void onResponse(final List<Commit> commits, boolean refreshing) {
        if (commitsMap == null || refreshing) {
            commitsMap = new ArrayList<>();
        }
        if (commits != null && commits.size() > 0) {

            orderCommits(commits);

            if (commitsAdapter == null || refreshing) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commitsAdapter = new CommitsAdapter(getActivity(), commitsMap, false);
                        listView.setAdapter(commitsAdapter);
                    }
                });
            }

            if (commitsAdapter.isLazyLoading()) {
                if (commitsAdapter != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commitsAdapter.setLazyLoading(false);
                            commitsAdapter.addAll(commits);
                        }
                    });
                }
            }
        } else if (commitsAdapter == null || commitsAdapter.getCount() == 0) {
            setEmpty();
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (commitsAdapter == null || commitsAdapter.getCount() == 0) {
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
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

        timeTickBroadcastReceiver = new TimeTickBroadcastReceiver();

        getActivity().registerReceiver(timeTickBroadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(timeTickBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void setPermissions(boolean admin, boolean push, boolean pull) {

    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    private class TimeTickBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (commitsAdapter != null) {
                        commitsAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void orderCommits(List<Commit> commits) {

        for (Commit commit : commits) {
            if (commit.commit.author.date != null) {
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                DateTime dt = formatter.parseDateTime(commit.commit.committer.date);

                Days days = Days.daysBetween(dt.withTimeAtStartOfDay(), new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay());

                commit.days = days.getDays();

                commitsMap.add(commit);
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
        commitsAdapter.setLazyLoading(true);
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

            if (commitsAdapter != null) {
                commitsAdapter.clear();
            }
            startRefresh();
            refreshing = true;
            executeRequest();
        }
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        Commit item = commitsAdapter.getItem(position);

        CommitInfo info = new CommitInfo();
        info.repoInfo = repoInfo;
        info.sha = item.sha;

        Intent intent = CommitDetailActivity.launchIntent(getActivity(), info);
        startActivity(intent);
    }
}
