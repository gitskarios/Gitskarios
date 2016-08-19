package com.alorma.github.ui.fragment.issues.users;

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
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.core.issues.Issue;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.adapter.issues.IssuesAdapter;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.AttributesUtils;
import java.util.List;

public abstract class UserIssuesListFragment extends BaseFragment
    implements TitleProvider, Presenter.Callback<List<Issue>>, RecyclerArrayAdapter.RecyclerAdapterContentListener {

  private SwipeRefreshLayout refreshLayout;
  private RecyclerView recyclerView;
  private IssuesAdapter adapter;

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    super.injectComponents(applicationComponent);

    ApiComponent apiComponent = DaggerApiComponent.builder().applicationComponent(applicationComponent).apiModule(new ApiModule()).build();

    initInjectors(apiComponent);
  }

  protected abstract void initInjectors(ApiComponent apiComponent);

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
    refreshLayout.setOnRefreshListener(this::onRefresh);
    refreshLayout.setColorSchemeColors(AttributesUtils.getAccentColor(getContext()));

    recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
    adapter = new IssuesAdapter(LayoutInflater.from(getContext()));
    adapter.setRecyclerAdapterContentListener(this);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
  }

  @Override
  public void onStart() {
    super.onStart();
    showLoading();
    onRefresh();
  }

  protected abstract void onRefresh();

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
  public void onResponse(List<Issue> issues, boolean firstTime) {
    if (firstTime) {
      adapter.clear();
    }
    // TODO adapter.addAll(issues);
  }

  @Override
  public void hideLoading() {

  }

  @Override
  public void onResponseEmpty() {
    if (isResumed()) {
      if (recyclerView != null) {
        Snackbar.make(recyclerView, R.string.no_issues_found, Snackbar.LENGTH_SHORT).show();
      }
    }
  }
}
