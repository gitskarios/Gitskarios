package com.alorma.github.ui.fragment.repos;

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
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.AttributesUtils;

import java.util.List;

import core.repositories.Repo;

public abstract class ReposFragment extends BaseFragment
    implements TitleProvider, com.alorma.github.presenter.View<List<Repo>>,
        RecyclerArrayAdapter.RecyclerAdapterContentListener {

  private ReposAdapter adapter;
  private SwipeRefreshLayout refreshLayout;
  private RecyclerView recyclerView;

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    super.injectComponents(applicationComponent);

    ApiComponent apiComponent = DaggerApiComponent.builder().applicationComponent(applicationComponent).apiModule(new ApiModule()).build();

    initInjectors(apiComponent);
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
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
    adapter = new ReposAdapter(LayoutInflater.from(getContext()));
    adapter.setRecyclerAdapterContentListener(this);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
  }

  protected abstract void onRefresh();

  @Override
  public void showLoading() {
    refreshLayout.post(() -> refreshLayout.setRefreshing(true));
  }

  @Override
  public void onDataReceived(List<Repo> repos, boolean isFromPaginated) {
    if (!isFromPaginated) {
      adapter.clear();
    }
    adapter.addAll(repos);
  }

  @Override
  public void showError(Throwable throwable) {
    if (isResumed()) {
      if (recyclerView != null) {
        Snackbar.make(recyclerView, R.string.empty_repos, Snackbar.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  public void hideLoading() {
    refreshLayout.post(() -> refreshLayout.setRefreshing(false));
  }

  @Override
  protected boolean showTitle() {
    return false;
  }
}
