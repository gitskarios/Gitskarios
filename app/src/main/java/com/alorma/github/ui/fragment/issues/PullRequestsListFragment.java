package com.alorma.github.ui.fragment.issues;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.pullrequest.GetPullsClient;
import com.alorma.github.ui.activity.NewIssueActivity;
import com.alorma.github.ui.activity.PullRequestDetailActivity;
import com.alorma.github.ui.adapter.issues.IssuesAdapter;
import com.alorma.github.ui.adapter.issues.PullRequestsAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 22/08/2014.
 */
public class PullRequestsListFragment extends LoadingListFragment<PullRequestsAdapter>
        implements View.OnClickListener, TitleProvider, PermissionsManager, BackManager, IssuesAdapter.IssuesAdapterListener,
        Observer<List<PullRequest>> {

    private static final String REPO_INFO = "REPO_INFO";
    private static final String FROM_SEARCH = "FROM_SEARCH";

    private static final int ISSUE_REQUEST = 1234;

    private RepoInfo repoInfo;

    private int currentFilter = 0;

    public static PullRequestsListFragment newInstance(RepoInfo repoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);

        PullRequestsListFragment fragment = new PullRequestsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.issues_list_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        String[] items = getResources().getStringArray(R.array.pullrequest_filter);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (currentFilter != position) {
                    currentFilter = position;

                    clear();
                    onRefresh();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            repoInfo = (RepoInfo) getArguments().getParcelable(REPO_INFO);
        }
    }

    protected void executeRequest() {
        super.executeRequest();
        if (repoInfo != null) {
            if (currentFilter == 0 || currentFilter == 1) {
                IssueInfo issueInfo = new IssueInfo();
                issueInfo.repoInfo = repoInfo;
                if (currentFilter == 0) {
                    issueInfo.state = IssueState.open;
                } else if (currentFilter == 1) {
                    issueInfo.state = IssueState.closed;
                }
                setAction(new GetPullsClient(issueInfo));
            }
        }
    }

    private void setAction(GithubListClient<List<PullRequest>> client) {
        client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Func1<Pair<List<PullRequest>, Integer>, List<PullRequest>>() {
            @Override
            public List<PullRequest> call(Pair<List<PullRequest>, Integer> listIntegerPair) {
                setPage(listIntegerPair.second);
                return listIntegerPair.first;
            }
        }).subscribe(this);
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);

        if (repoInfo != null) {
            if (currentFilter == 0 || currentFilter == 1) {
                IssueInfo issueInfo = new IssueInfo(repoInfo);
                if (currentFilter == 0) {
                    issueInfo.state = IssueState.open;
                } else if (currentFilter == 1) {
                    issueInfo.state = IssueState.closed;
                }
                setAction(new GetPullsClient(issueInfo, page));
            }
        }
    }

    @Override
    public void onNext(List<PullRequest> issues) {
        if (issues.size() > 0) {
            hideEmpty();
            if (refreshing || getAdapter() == null) {
                PullRequestsAdapter pullRequestsAdapter = new PullRequestsAdapter(LayoutInflater.from(getActivity()));
                pullRequestsAdapter.setIssuesAdapterListener(this);
                pullRequestsAdapter.addAll(issues);
                setAdapter(pullRequestsAdapter);
            } else {
                getAdapter().addAll(issues);
            }
        } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty();
        } else {
            getAdapter().clear();
            setEmpty();
        }
    }

    @Override
    public void onError(Throwable error) {
        stopRefresh();
        if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty();
        }
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_issue_opened;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_pullrequest_ound;
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

    public void setPermissions(Permissions permissions) {
        if (this.repoInfo != null) {
            this.repoInfo.permissions = permissions;
        }
    }

    @Override
    public int getTitle() {
        return R.string.pulls_fragment_title;
    }

    @Override
    public IIcon getTitleIcon() {
        return Octicons.Icon.oct_git_pull_request;
    }

    public void clear() {
        if (getAdapter() != null) {
            getAdapter().clear();
        }
    }

    @Override
    public void setPermissions(boolean admin, boolean push, boolean pull) {
        if (this.repoInfo != null) {
            Permissions permissions = new Permissions();
            permissions.admin = admin;
            permissions.push = push;
            permissions.pull = pull;

            setPermissions(permissions);
        }
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onIssueOpenRequest(Issue item) {
        if (item != null) {
            IssueInfo info = new IssueInfo();
            info.repoInfo = repoInfo;
            info.num = item.number;

            if (item.pullRequest == null) {
                Intent intent = PullRequestDetailActivity.createLauncherIntent(getActivity(), info);
                startActivity(intent);
            }
        }
    }

    @Override
    protected boolean autoStart() {
        return true;
    }

    @Override
    public void setRefreshing() {
        super.setRefreshing();
        startRefresh();
    }

    @Override
    public void onCompleted() {
        stopRefresh();
    }
}
