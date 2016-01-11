package com.alorma.github.ui.fragment.issues;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.issues.GetIssuesClient;
import com.alorma.github.sdk.services.search.IssuesSearchClient;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.activity.NewIssueActivity;
import com.alorma.github.ui.activity.SearchIssuesActivity;
import com.alorma.github.ui.adapter.issues.IssuesAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 22/08/2014.
 */
public class IssuesListFragment extends LoadingListFragment<IssuesAdapter>
        implements View.OnClickListener, TitleProvider, PermissionsManager, BackManager, IssuesAdapter.IssuesAdapterListener {

    private static final String REPO_INFO = "REPO_INFO";
    private static final String FROM_SEARCH = "FROM_SEARCH";

    private static final int ISSUE_REQUEST = 1234;

    private RepoInfo repoInfo;

    private boolean fromSearch = false;
    private SearchClientRequest searchClientRequest;

    private int currentFilter = 0;
    private View revealView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.issues_list_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        revealView = view.findViewById(R.id.revealView);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        String[] items = getResources().getStringArray(R.array.issues_filter);
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
    public void onStart() {
        super.onStart();

        if (revealView != null) {
            revealView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.issue_list_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = SearchIssuesActivity.launchIntent(getActivity(), repoInfo);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            repoInfo = (RepoInfo) getArguments().getParcelable(REPO_INFO);
            fromSearch = getArguments().getBoolean(FROM_SEARCH, false);
        }
    }

    protected void executeRequest() {
        super.executeRequest();
        if (repoInfo != null) {
            if (fromSearch && searchClientRequest != null && searchClientRequest.request() != null) {
                if (currentFilter == 0 || currentFilter == 1) {
                    IssueInfo issueInfo = new IssueInfo();
                    issueInfo.repoInfo = repoInfo;
                    if (currentFilter == 0) {
                        issueInfo.state = IssueState.open;
                    } else if (currentFilter == 1) {
                        issueInfo.state = IssueState.closed;
                    }

                    setAction(new IssuesSearchClient(searchClientRequest.request()));
                }
            } else {
                if (currentFilter == 0 || currentFilter == 1) {
                    IssueInfo issueInfo = new IssueInfo();
                    issueInfo.repoInfo = repoInfo;
                    if (currentFilter == 0) {
                        issueInfo.state = IssueState.open;
                    } else if (currentFilter == 1) {
                        issueInfo.state = IssueState.closed;
                    }
                    HashMap<String, String> map = new HashMap<>();
                    map.put("state", issueInfo.state.name());

                    setAction(new GetIssuesClient(issueInfo, map));
                }
            }
        }
    }

    private void setAction(GithubListClient<List<Issue>> client) {
        client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Action1<Pair<List<Issue>, Integer>>() {
            @Override
            public void call(Pair<List<Issue>, Integer> listIntegerPair) {
                setPage(listIntegerPair.second);
            }
        }).flatMap(new Func1<Pair<List<Issue>, Integer>, Observable<Issue>>() {
            @Override
            public Observable<Issue> call(Pair<List<Issue>, Integer> listIntegerPair) {
                return Observable.from(listIntegerPair.first);
            }
        }).filter(new Func1<Issue, Boolean>() {
            @Override
            public Boolean call(Issue issue) {
                return issue.pullRequest == null;
            }
        }).toList().subscribe(new Subscriber<List<Issue>>() {
            @Override
            public void onCompleted() {
                stopRefresh();
            }

            @Override
            public void onError(Throwable e) {
                if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                    setEmpty();
                }
            }

            @Override
            public void onNext(List<Issue> issues) {
                onResponse(issues);
            }
        });
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);

        if (repoInfo != null) {
            if (fromSearch && searchClientRequest != null && searchClientRequest.request() != null) {
                if (currentFilter == 0 || currentFilter == 1) {
                    IssueInfo issueInfo = new IssueInfo();
                    issueInfo.repoInfo = repoInfo;
                    if (currentFilter == 0) {
                        issueInfo.state = IssueState.open;
                    } else if (currentFilter == 1) {
                        issueInfo.state = IssueState.closed;
                    }

                    setAction(new IssuesSearchClient(searchClientRequest.request(), page));
                }
            } else {
                if (currentFilter == 0 || currentFilter == 1) {
                    IssueInfo issueInfo = new IssueInfo();
                    issueInfo.repoInfo = repoInfo;
                    if (currentFilter == 0) {
                        issueInfo.state = IssueState.open;
                    } else if (currentFilter == 1) {
                        issueInfo.state = IssueState.closed;
                    }
                    HashMap<String, String> map = new HashMap<>();
                    map.put("state", issueInfo.state.name());

                    setAction(new GetIssuesClient(issueInfo, map, page));
                }
            }
        }
    }

    protected void onResponse(List<Issue> issues) {
        if (issues.size() > 0) {
            hideEmpty();
            if (refreshing || getAdapter() == null) {
                IssuesAdapter issuesAdapter = new IssuesAdapter(LayoutInflater.from(getActivity()));
                issuesAdapter.setIssuesAdapterListener(this);
                issuesAdapter.addAll(issues);
                setAdapter(issuesAdapter);
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
    protected void fabClick() {
        super.fabClick();
        if (repoInfo.permissions != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && fab != null) {
                animateRevealFab();
            } else {
                Intent intent = NewIssueActivity.createLauncherIntent(getActivity(), repoInfo);
                startActivityForResult(intent, ISSUE_REQUEST);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateRevealFab() {
        float x = fab.getX() + (fab.getWidth() / 2);
        float y = fab.getY() + (fab.getHeight() / 2);

        int finalRadius = Math.max(revealView.getWidth(), revealView.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(revealView, (int) x, (int) y, fab.getWidth() / 2, finalRadius);
        revealView.setVisibility(View.VISIBLE);
        anim.setDuration(600);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Intent intent = NewIssueActivity.createLauncherIntent(getActivity(), repoInfo);
                startActivityForResult(intent, ISSUE_REQUEST);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        anim.start();
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
    public void onIssueOpenRequest(Issue item) {
        if (item != null) {
            IssueInfo info = new IssueInfo();
            info.repoInfo = repoInfo;
            info.num = item.number;

            if (item.pullRequest == null) {
                Intent intent = IssueDetailActivity.createLauncherIntent(getActivity(), info);
                startActivityForResult(intent, ISSUE_REQUEST);
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
    public int getTitle() {
        return R.string.issues_fragment_title;
    }

    @Override
    public IIcon getTitleIcon() {
        return Octicons.Icon.oct_issue_opened;
    }

    public void setSearchClientRequest(SearchClientRequest searchClientRequest) {
        this.searchClientRequest = searchClientRequest;
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

    public void executeSearch() {
        onRefresh();
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

    public interface SearchClientRequest {
        String request();
    }
}
