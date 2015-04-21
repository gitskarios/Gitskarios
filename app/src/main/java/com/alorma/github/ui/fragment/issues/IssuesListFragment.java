package com.alorma.github.ui.fragment.issues;

import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.ListIssues;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.issues.GetIssuesClient;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.activity.NewIssueActivity;
import com.alorma.github.ui.activity.SearchIssuesActivity;
import com.alorma.github.ui.adapter.issues.IssuesAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.mikepenz.octicons_typeface_library.Octicons;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 22/08/2014.
 */
public class IssuesListFragment extends PaginatedListFragment<ListIssues> implements View.OnClickListener, TitleProvider {

    private static final String REPO_INFO = "REPO_INFO";
    private static final String FROM_SEARCH = "FROM_SEARCH";

    private static final int ISSUE_REQUEST = 1234;

    private RepoInfo repoInfo;

    private float fabNewY;
    private float fabOldY;
    private IssuesAdapter issuesAdapter;
    private IssueState currentState = IssueState.open;
    private boolean fromSearch = false;
    private SearchClientRequest searchClientRequest;

    public static IssuesListFragment newInstance(RepoInfo repoInfo, boolean fromSearch) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);
        bundle.putBoolean(FROM_SEARCH, fromSearch);

        IssuesListFragment fragment = new IssuesListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(!fromSearch);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!fromSearch) {
            inflater.inflate(R.menu.issue_list_filter, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (!fromSearch) {
            if (getActivity() != null) {

                MenuItem itemFilter = menu.findItem(R.id.issue_list_filter);
                if (itemFilter != null) {
                    if (itemFilter.getSubMenu() != null) {
                        MenuItem openIssues = itemFilter.getSubMenu().findItem(R.id.issue_list_filter_open);
                        MenuItem closedIssues = itemFilter.getSubMenu().findItem(R.id.issue_list_filter_closed);

                        if (currentState == IssueState.open && openIssues != null) {
                            openIssues.setVisible(false);
                            closedIssues.setVisible(true);
                        } else if (currentState == IssueState.closed && closedIssues != null) {
                            openIssues.setVisible(true);
                            closedIssues.setVisible(false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!fromSearch) {
            switch (item.getItemId()) {
                case R.id.issue_list_filter_open:
                    currentState = IssueState.open;
                    if (issuesAdapter != null) {
                        issuesAdapter.clear();
                    }
                    onRefresh();
                    break;
                case R.id.issue_list_filter_closed:
                    currentState = IssueState.closed;
                    if (issuesAdapter != null) {
                        issuesAdapter.clear();
                    }
                    onRefresh();
                    break;
                case R.id.issue_list_filter_search:
                    Intent intent = SearchIssuesActivity.launchIntent(getActivity(), repoInfo);
                    startActivity(intent);
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        issuesAdapter = null;
        if (autoStart()) {
            executeRequest();
        }
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            repoInfo = getArguments().getParcelable(REPO_INFO);
            fromSearch = getArguments().getBoolean(FROM_SEARCH, false);
        }
    }

    protected void executeRequest() {
        super.executeRequest();
        if (repoInfo != null) {
            if (fromSearch) {
                if (searchClientRequest != null) {
                    searchClientRequest.request();
                }
            } else {
                IssueInfo issueInfo = new IssueInfo(repoInfo);
                issueInfo.state = currentState;
                GetIssuesClient issuesClient = new GetIssuesClient(getActivity(), issueInfo);
                issuesClient.setOnResultCallback(this);
                issuesClient.execute();
            }
        }
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);

        if (issuesAdapter != null) {
            issuesAdapter.setLazyLoading(true);
        }

        if (repoInfo != null) {
            if (fromSearch) {
                if (searchClientRequest != null) {
                    searchClientRequest.requestPaginated(page);
                }
            } else {
                IssueInfo issueInfo = new IssueInfo(repoInfo);
                issueInfo.state = currentState;
                GetIssuesClient issuesClient = new GetIssuesClient(getActivity(), issueInfo, page);
                issuesClient.setOnResultCallback(this);
                issuesClient.execute();
            }
        }
    }

    @Override
    public void onResponseOk(ListIssues issues, Response r) {
        super.onResponseOk(issues, r);

        if (refreshing) {
            if (issuesAdapter != null) {
                issuesAdapter.clear();
            }
        }
        if (issues == null || issues.size() == 0 && (issuesAdapter == null || issuesAdapter.getCount() == 0)) {
            setEmpty();
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
        } else if (issuesAdapter == null || issuesAdapter.getCount() == 0) {
            setEmpty();
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (issuesAdapter == null || issuesAdapter.getCount() == 0) {
            setEmpty();
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
        return !fromSearch && (repoInfo.permissions == null || repoInfo.permissions.pull);
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
        if (repoInfo.permissions != null) {
            Intent intent = NewIssueActivity.createLauncherIntent(getActivity(), repoInfo);
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

                Intent intent = IssueDetailActivity.createLauncherIntent(getActivity(), info);
                startActivity(intent);
            }
        }
    }

    public void setPermissions(Permissions permissions) {
        if (this.repoInfo != null) {
            this.repoInfo.permissions = permissions;
            checkFAB();
        }

    }

    @Override
    protected Octicons.Icon getFABGithubIcon() {
        return Octicons.Icon.oct_bug;
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.issues_fragment_title);
    }

    public void setSearchClientRequest(SearchClientRequest searchClientRequest) {
        this.searchClientRequest = searchClientRequest;
    }

    public void clear() {
        if (issuesAdapter != null) {
            issuesAdapter.clear();
        }
    }

    public interface SearchClientRequest {
        void request();

        void requestPaginated(int page);
    }

    @Override
    protected boolean autoStart() {
        return !fromSearch;
    }

    @Override
    public void setRefreshing() {
        super.setRefreshing();
        startRefresh();
    }
}
