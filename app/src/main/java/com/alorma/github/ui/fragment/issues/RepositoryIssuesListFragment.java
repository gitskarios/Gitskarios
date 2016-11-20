package com.alorma.github.ui.fragment.issues;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.issues.IssuesModule;
import com.alorma.github.presenter.issue.IssuesPresenter;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.activity.NewIssueActivity;
import com.alorma.github.ui.activity.PullRequestDetailActivity;
import com.alorma.github.ui.activity.SearchIssuesActivity;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.ui.fragment.issues.user.IssuesAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import core.issue.IssuesRequest;
import core.issues.Issue;
import core.issues.IssueState;
import core.repositories.Permissions;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;

public class RepositoryIssuesListFragment extends LoadingListFragment<IssuesAdapter>
    implements PermissionsManager, BackManager, com.alorma.github.presenter.View<List<Issue>>, RecyclerArrayAdapter.ItemCallback<Issue> {

  @Inject IssuesPresenter presenter;

  private static final String REPO_INFO = "REPO_INFO";
  private static final String FROM_SEARCH = "FROM_SEARCH";

  private static final int ISSUE_REQUEST = 1234;

  private RepoInfo repoInfo;

  private boolean fromSearch = false;

  private int currentFilter = 0;
  private View revealView;

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

          IssueState issueInfo;
          if (position == 0) {
            issueInfo = IssueState.open;
          } else {
            issueInfo = IssueState.closed;
          }
          HashMap<String, String> map = new HashMap<>();
          map.put("state", issueInfo.name());

          IssuesRequest request = new IssuesRequest(repoInfo, map);
          presenter.execute(request);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    super.injectComponents(applicationComponent);

    ApiComponent apiComponent = DaggerApiComponent.builder().applicationComponent(applicationComponent).apiModule(new ApiModule()).build();

    apiComponent.plus(new IssuesModule()).inject(this);
    presenter.attachView(this);
  }

  @Override
  public void onStop() {
    presenter.detachView();
    super.onStop();
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
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
      repoInfo = getArguments().getParcelable(REPO_INFO);
      fromSearch = getArguments().getBoolean(FROM_SEARCH, false);
    }
  }

  protected void executeRequest() {
    if (currentFilter == 0 || currentFilter == 1) {
      IssueState issueInfo;
      if (currentFilter == 0) {
        issueInfo = IssueState.open;
      } else {
        issueInfo = IssueState.closed;
      }
      HashMap<String, String> map = new HashMap<>();
      map.put("state", issueInfo.name());

      IssuesRequest request = new IssuesRequest(repoInfo, map);
      presenter.execute(request);
    }
  }

  @Override
  protected void executePaginatedRequest(int page) {
    super.executePaginatedRequest(page);
    if (currentFilter == 0 || currentFilter == 1) {
      IssueState issueInfo;
      if (currentFilter == 0) {
        issueInfo = IssueState.open;
      } else {
        issueInfo = IssueState.closed;
      }
      HashMap<String, String> map = new HashMap<>();
      map.put("state", issueInfo.name());

      IssuesRequest request = new IssuesRequest(repoInfo, map);
      presenter.executePaginated(request);
    }
  }

  protected void onResponse(List<Issue> issues) {
    if (issues.size() > 0) {
      hideEmpty();
      if (refreshing || getAdapter() == null) {
        IssuesAdapter issuesAdapter = new IssuesAdapter(LayoutInflater.from(getActivity()));
        issuesAdapter.addAll(issues);
        issuesAdapter.setCallback(this);
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
        openNewIssueActivity();
      }
    }
  }

  private void openNewIssueActivity() {
    Intent intent = NewIssueActivity.createLauncherIntent(getActivity(), repoInfo);
    startActivityForResult(intent, ISSUE_REQUEST);
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
        openNewIssueActivity();
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
      executeRequest();
    } else if (requestCode == ISSUE_REQUEST && resultCode == Activity.RESULT_OK) {
      executeRequest();
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
  protected boolean autoStart() {
    return !fromSearch;
  }

  @Override
  public void setRefreshing() {
    super.setRefreshing();
    executeRequest();
  }

  @Override
  public void showLoading() {
    loadingView.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideLoading() {
    loadingView.setVisibility(View.GONE);
  }

  @Override
  public void onDataReceived(List<Issue> data, boolean isFromPaginated) {
    hideEmpty();
    onResponse(data);
  }

  @Override
  public void showError(Throwable throwable) {
    setEmpty();
  }

  @Override
  public void onItemSelected(Issue item) {
    if (item != null) {
      IssueInfo info = new IssueInfo();
      info.repoInfo = repoInfo;
      info.num = item.getNumber();

      if (item.getPullRequest() == null) {
        Intent intent = IssueDetailActivity.createLauncherIntent(getActivity(), info);
        startActivityForResult(intent, ISSUE_REQUEST);
      } else {
        Intent intent = PullRequestDetailActivity.createLauncherIntent(getActivity(), info);
        startActivityForResult(intent, ISSUE_REQUEST);
      }
    }
  }
}
