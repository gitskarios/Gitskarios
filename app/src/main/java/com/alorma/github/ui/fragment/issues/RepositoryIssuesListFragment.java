package com.alorma.github.ui.fragment.issues;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.CompoundButton;
import android.widget.Spinner;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
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
import com.alorma.github.ui.utils.SimpleItemSelectedItemListener;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import core.search.IssuesSearchRequest;
import core.issues.Issue;
import core.repositories.Permissions;
import java.util.List;
import javax.inject.Inject;

public class RepositoryIssuesListFragment extends LoadingListFragment<IssuesAdapter>
    implements PermissionsManager, BackManager, com.alorma.github.presenter.View<List<Issue>>, RecyclerArrayAdapter.ItemCallback<Issue> {

  @Inject IssuesPresenter presenter;

  private static final String REPO_INFO = "REPO_INFO";

  private static final int ISSUE_REQUEST = 1234;

  private RepoInfo repoInfo;

  private Integer currentFilter = null;

  @BindView(R.id.showPullRequest) CompoundButton showPullRequest;
  @BindView(R.id.revealView) View revealView;
  @BindView(R.id.spinner) Spinner spinner;
  @BindArray(R.array.issues_filter) String[] issueFilters;

  public static RepositoryIssuesListFragment newInstance(RepoInfo repoInfo) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(REPO_INFO, repoInfo);

    RepositoryIssuesListFragment fragment = new RepositoryIssuesListFragment();
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

    ButterKnife.bind(this, view);

    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, issueFilters);
    spinner.setAdapter(adapter);

    spinner.setOnItemSelectedListener(new SimpleItemSelectedItemListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (currentFilter == null || currentFilter != position) {
          currentFilter = position;
          IssuesSearchRequest request = getIssueSearchRequest(position);
          presenter.execute(request);
        }
      }
    });

    showPullRequest.setOnCheckedChangeListener((compoundButton, b) -> {
      IssuesSearchRequest request = getIssueSearchRequest(currentFilter);
      presenter.execute(request);
      checkFAB();
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
    }
  }

  @Override
  protected void executeRequest() {
    if (currentFilter == 0 || currentFilter == 1) {
      IssuesSearchRequest builder = getIssueSearchRequest(currentFilter);
      presenter.execute(builder);
    }
  }

  @Override
  protected boolean autoStart() {
    return false;
  }

  @Override
  protected void executePaginatedRequest(int page) {
    if (currentFilter == 0 || currentFilter == 1) {
      IssuesSearchRequest builder = getIssueSearchRequest(currentFilter);
      presenter.executePaginated(builder);
    }
  }

  @NonNull
  private IssuesSearchRequest getIssueSearchRequest(Integer status) {
    IssuesSearchRequest builder = new IssuesSearchRequest();
    builder.setIsOpen(status == null || status == 0);
    builder.setAuthor(repoInfo.owner);
    builder.setRepo(repoInfo.name);
    builder.setIsPullRequest(showPullRequest.isChecked());
    return builder;
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
    if (showPullRequest == null) {
      return repoInfo.permissions != null && repoInfo.permissions.pull;
    } else {
      return !showPullRequest.isChecked() || repoInfo.permissions != null && repoInfo.permissions.pull;
    }
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
  public void setRefreshing() {
    super.setRefreshing();
    executeRequest();
  }

  @Override
  public void showLoading() {
    swipe.setRefreshing(true);
  }

  @Override
  public void hideLoading() {
    swipe.setRefreshing(false);
  }

  @Override
  public void onDataReceived(List<Issue> data, boolean isFromPaginated) {
    hideEmpty();
    if (!isFromPaginated) {
      clear();
    }
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
