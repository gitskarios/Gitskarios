package com.alorma.github.ui.fragment.issues;

import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.ListIssues;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.issues.GetIssuesClient;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.activity.NewIssueActivity;
import com.alorma.github.ui.adapter.issues.IssuesAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by Bernat on 22/08/2014.
 */
public class IssuesListFragment extends PaginatedListFragment<ListIssues> implements View.OnClickListener, TitleProvider {

    private static final String REPO_INFO = "REPO_INFO";

    private static final int ISSUE_REQUEST = 1234;

    private RepoInfo repoInfo;

    private float fabNewY;
    private float fabOldY;
    private IssuesAdapter issuesAdapter;
    private Permissions permissions;

    public static IssuesListFragment newInstance(RepoInfo repoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);

        IssuesListFragment fragment = new IssuesListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        issuesAdapter = null;
        executeRequest();
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            repoInfo = getArguments().getParcelable(REPO_INFO);
        }
    }

    protected void executeRequest() {
        super.executeRequest();
        if (repoInfo != null) {
            IssueInfo issueInfo = new IssueInfo(repoInfo);
            GetIssuesClient issuesClient = new GetIssuesClient(getActivity(), issueInfo);
            issuesClient.setOnResultCallback(this);
            issuesClient.execute();
        }
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);

        if (issuesAdapter != null) {
            issuesAdapter.setLazyLoading(true);
        }

        if (repoInfo != null) {
            IssueInfo issueInfo = new IssueInfo(repoInfo);
            GetIssuesClient issuesClient = new GetIssuesClient(getActivity(), issueInfo, page);
            issuesClient.setOnResultCallback(this);
            issuesClient.execute();
        }
    }

    @Override
    protected void onResponse(ListIssues issues, boolean refreshing) {
        if (issues != null && issues.size() > 0) {

            if (issuesAdapter == null || refreshing) {
                issuesAdapter = new IssuesAdapter(getActivity(), issues);
                setListAdapter(issuesAdapter);
            }

            if (issuesAdapter.isLazyLoading()) {
                if (issuesAdapter != null) {
                    issuesAdapter.setLazyLoading(false);
                    issuesAdapter.addAll(issues);
                }
            } else {
                setListAdapter(issuesAdapter);
            }
        }
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_issue_opened;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_issues_found;
    }

    @Override
    protected boolean useFAB() {
        return permissions == null || permissions.pull;
    }

    @Override
    protected PropertyValuesHolder showAnimator(View fab) {
        return PropertyValuesHolder.ofFloat(View.Y, fabNewY, fabOldY);
    }

    @Override
    protected PropertyValuesHolder hideAnimator(View fab) {
        fabOldY = fab.getY();
        fabNewY = fab.getY() + fab.getHeight() + (getResources().getDimension(R.dimen.gapLarge) * 2);
        return PropertyValuesHolder.ofFloat(View.Y, fab.getY(), fabNewY);
    }

    @Override
    protected void fabClick() {
        super.fabClick();

        if (permissions != null) {
            Intent intent = NewIssueActivity.createLauncherIntent(getActivity(), repoInfo, permissions);
            startActivityForResult(intent, ISSUE_REQUEST);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_FIRST_USER) {
            invalidate();
        } else if (requestCode == ISSUE_REQUEST && resultCode == Activity.RESULT_OK) {
            invalidate();
        }
    }

    public void invalidate() {
        onRefresh();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (issuesAdapter != null) {
            Issue item = issuesAdapter.getItem(position);
            if (item != null) {
                IssueInfo info = new IssueInfo();
                info.repo = repoInfo;
                info.num = item.number;

                Intent intent = IssueDetailActivity.createLauncherIntent(getActivity(), info, permissions);
                startActivity(intent);
            }
        }
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
        checkFAB();
    }

    @Override
    protected Octicons.Icon getFABGithubIcon() {
        return Octicons.Icon.oct_bug;
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.issues_fragment_title);
    }
}
