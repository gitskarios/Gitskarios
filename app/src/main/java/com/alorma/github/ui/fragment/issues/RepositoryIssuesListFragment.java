package com.alorma.github.ui.fragment.issues;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.MilestoneState;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.issues.GetIssuesClient;
import com.alorma.github.sdk.services.search.IssuesSearchClient;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.activity.MilestoneIssuesActivity;
import com.alorma.github.ui.activity.NewIssueActivity;
import com.alorma.github.ui.activity.SearchIssuesActivity;
import com.alorma.github.ui.activity.issue.RepositoryMilestonesActivity;
import com.alorma.github.ui.adapter.issues.IssuesAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
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

public class RepositoryIssuesListFragment extends LoadingListFragment<IssuesAdapter>
    implements View.OnClickListener, TitleProvider, PermissionsManager, BackManager, IssuesAdapter.IssuesAdapterListener {

  private static final String REPO_INFO = "REPO_INFO";
  private static final String FROM_SEARCH = "FROM_SEARCH";

  private static final int NEW_ISSUE_REQUEST = 1234;
  private static final int MILESTONES_REQUEST = 1212;

  private RepoInfo repoInfo;

  private boolean fromSearch = false;
  private SearchClientRequest searchClientRequest;

  private int currentFilter = 0;

  public static RepositoryIssuesListFragment newInstance(RepoInfo repoInfo, boolean fromSearch) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(REPO_INFO, repoInfo);
    bundle.putBoolean(FROM_SEARCH, fromSearch);

    RepositoryIssuesListFragment fragment = new RepositoryIssuesListFragment();
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
    return inflater.inflate(R.layout.repository_issues_list_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

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

    view.findViewById(R.id.milestones).setOnClickListener(v -> showMilestones());
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  private void showMilestones() {
    Intent intent = RepositoryMilestonesActivity.createLauncher(getActivity(), repoInfo, MilestoneState.all, true);
    startActivityForResult(intent, MILESTONES_REQUEST);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.issue_list_filter, menu);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    menu.findItem(R.id.action_search)
        .setIcon(
            new IconicsDrawable(this.getContext(), GoogleMaterial.Icon.gmd_search).color(Color.WHITE).sizeDp(24).respectFontBounds(true));
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
    client.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(new Action1<Pair<List<Issue>, Integer>>() {
          @Override
          public void call(Pair<List<Issue>, Integer> listIntegerPair) {
            setPage(listIntegerPair.second);
          }
        })
        .flatMap(new Func1<Pair<List<Issue>, Integer>, Observable<Issue>>() {
          @Override
          public Observable<Issue> call(Pair<List<Issue>, Integer> listIntegerPair) {
            return Observable.from(listIntegerPair.first);
          }
        })
        .filter(issue -> issue.pullRequest == null)
        .toList()
        .subscribe(new Subscriber<List<Issue>>() {
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
      openNewIssueActivity();
    }
  }

  private void openNewIssueActivity() {
    Intent intent = NewIssueActivity.createLauncherIntent(getActivity(), repoInfo);
    startActivityForResult(intent, NEW_ISSUE_REQUEST);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_FIRST_USER) {
      invalidate();
    } else if (resultCode == Activity.RESULT_OK) {
      if (requestCode == NEW_ISSUE_REQUEST) {
        invalidate();
      } else if (requestCode == MILESTONES_REQUEST) {
        Milestone milestone = data.getParcelableExtra(Milestone.class.getSimpleName());
        Intent intent = MilestoneIssuesActivity.launchIntent(getActivity(), repoInfo, milestone);
        startActivity(intent);
      }
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
  public int getTitle() {
    return R.string.issues_fragment_title;
  }

  @Override
  public IIcon getTitleIcon() {
    return Octicons.Icon.oct_issue_opened;
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
