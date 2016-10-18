package com.alorma.github.ui.fragment.issues.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.R;
import com.alorma.github.account.AccountNameProvider;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.issues.UserIssuesModule;
import com.alorma.github.presenter.issue.UserIssuesBaseRxPresenter;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.activity.PullRequestDetailActivity;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.AttributesUtils;
import com.mikepenz.iconics.typeface.IIcon;
import core.issue.IssuesSearchRequest;
import core.issues.Issue;
import java.util.List;
import javax.inject.Inject;

public abstract class UserIssuesListFragment extends BaseFragment
    implements TitleProvider, com.alorma.github.presenter.View<List<Issue>>, RecyclerArrayAdapter.RecyclerAdapterContentListener,
    RecyclerArrayAdapter.ItemCallback<Issue> {

  @Inject UserIssuesBaseRxPresenter presenter;
  @Inject AccountNameProvider accountNameProvider;

  private SwipeRefreshLayout refreshLayout;
  private RecyclerView recyclerView;
  private IssuesAdapter adapter;

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    super.injectComponents(applicationComponent);

    ApiComponent apiComponent = DaggerApiComponent.builder().applicationComponent(applicationComponent).apiModule(new ApiModule()).build();

    apiComponent.plus(new UserIssuesModule()).inject(this);
    presenter.attachView(this);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return getLayoutInflater(savedInstanceState).inflate(R.layout.recyclerview, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
    refreshLayout.setOnRefreshListener(this::loadItems);
    refreshLayout.setColorSchemeColors(AttributesUtils.getAccentColor(getContext()));

    recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
    adapter = new IssuesAdapter(LayoutInflater.from(getContext()));
    adapter.setRecyclerAdapterContentListener(this);
    adapter.setCallback(this);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
  }

  @Override
  public void onStart() {
    super.onStart();
    showLoading();
    loadItems();
  }

  @Override
  public void onPause() {
    super.onPause();
    presenter.detachView();
  }

  private void loadItems() {
    presenter.execute(buildIssueSearchRequest());
  }

  private IssuesSearchRequest buildIssueSearchRequest() {
    return new IssuesSearchRequest.Builder().setAction(getAction()).setAuthor(accountNameProvider.getName()).setIsOpen(true).build();
  }

  @Override
  public void loadMoreItems() {
    presenter.executePaginated(null);
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
  public void showLoading() {
    refreshLayout.post(() -> refreshLayout.setRefreshing(true));
  }

  @Override
  public void onDataReceived(List<Issue> issues, boolean isFromPaginated) {
    if (!isFromPaginated) {
      adapter.clear();
    }
    adapter.addAll(issues);
  }

  @Override
  public void hideLoading() {
    refreshLayout.post(() -> refreshLayout.setRefreshing(false));
  }

  @Override
  public void showError(Throwable throwable) {
    if (isResumed()) {
      if (recyclerView != null) {
        Snackbar.make(recyclerView, R.string.no_issues_found, Snackbar.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  public void onItemSelected(Issue item) {
    IssueInfo info = new IssueInfo();
    info.num = item.getNumber();
    info.repoInfo = new RepoInfo();
    info.repoInfo.owner = item.getRepository().getOwner().getLogin();
    info.repoInfo.name = item.getRepository().getName();

    if (item.getPullRequest() != null) {
      Intent intent = PullRequestDetailActivity.createLauncherIntent(getActivity(), info);
      startActivity(intent);
    } else {
      Intent intent = IssueDetailActivity.createLauncherIntent(getActivity(), info);
      startActivity(intent);
    }
  }

  @Override
  protected boolean showTitle() {
    return false;
  }

  protected abstract String getAction();

  @Override
  public IIcon getTitleIcon() {
    return null;
  }
}
