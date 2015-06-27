package com.alorma.github.ui.fragment.pullrequest;

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
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.pullrequest.GetPullRequestCommits;
import com.alorma.github.ui.activity.CommitDetailActivity;
import com.alorma.github.ui.adapter.commit.CommitsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
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
public class PullRequestCommitsListFragment extends PaginatedListFragment<List<Commit>> implements PermissionsManager
        , BackManager {

    private static final String ISSUE_INFO = "ISSUE_INFO";

    private CommitsAdapter commitsAdapter;
    private List<Commit> commitsMap;
    private StickyListHeadersListView listView;

    private IssueInfo issueInfo;
    private TimeTickBroadcastReceiver timeTickBroadcastReceiver;

    public static PullRequestCommitsListFragment newInstance(IssueInfo issueInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ISSUE_INFO, issueInfo);

        PullRequestCommitsListFragment fragment = new PullRequestCommitsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment_headers, null, false);
    }

    @Override
    protected void setupListView(View view) {
        listView = (StickyListHeadersListView) view.findViewById(android.R.id.list);
        if (listView != null) {
            listView.setDivider(getResources().getDrawable(R.drawable.divider_main));
            listView.setOnScrollListener(this);
            listView.setOnItemClickListener(this);
            listView.setAreHeadersSticky(false);
        }
    }

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
    public void setEmpty() {
        super.setEmpty();
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
        GetPullRequestCommits getPullRequestCommits = new GetPullRequestCommits(getActivity(), issueInfo);
        getPullRequestCommits.setOnResultCallback(this);
        getPullRequestCommits.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        commitsAdapter.setLazyLoading(true);
        GetPullRequestCommits getPullRequestCommits = new GetPullRequestCommits(getActivity(), issueInfo);
        getPullRequestCommits.setOnResultCallback(this);
        getPullRequestCommits.execute();
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            issueInfo = getArguments().getParcelable(ISSUE_INFO);
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
    protected boolean useFAB() {
        return false;
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        Commit item = commitsAdapter.getItem(position);

        CommitInfo info = new CommitInfo();
        info.repoInfo = issueInfo.repoInfo;
        info.sha = item.sha;

        Intent intent = CommitDetailActivity.launchIntent(getActivity(), info);
        startActivity(intent);
    }
}
