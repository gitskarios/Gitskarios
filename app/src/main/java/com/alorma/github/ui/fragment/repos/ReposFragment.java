package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.core.repositories.Repo;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.AttributesUtils;
import com.mikepenz.iconics.typeface.IIcon;
import java.util.List;

public abstract class ReposFragment extends BaseFragment
    implements TitleProvider, Presenter.Callback<List<Repo>>,
    RecyclerArrayAdapter.RecyclerAdapterContentListener {

  private ReposAdapter adapter;
  private SwipeRefreshLayout refreshLayout;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    GitskariosApplication application = (GitskariosApplication) getActivity().getApplication();
    ApplicationComponent component = application.getComponent();

    ApiComponent apiComponent = DaggerApiComponent.builder()
        .applicationComponent(component)
        .apiModule(new ApiModule())
        .build();

    initInjectors(apiComponent);
  }

  protected abstract void initInjectors(ApiComponent apiComponent);

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return getLayoutInflater(savedInstanceState).inflate(R.layout.recyclerview, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
    refreshLayout.setOnRefreshListener(this::onRefresh);
    refreshLayout.setColorSchemeColors(AttributesUtils.getAccentColor(getContext()));

    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
    adapter = new ReposAdapter(LayoutInflater.from(getContext()));
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
  public abstract int getTitle();

  @Override
  public abstract IIcon getTitleIcon();

  @Override
  public void showLoading() {
    refreshLayout.post(() -> refreshLayout.setRefreshing(true));
  }

  @Override
  public void onResponse(List<Repo> repos, boolean firstTime) {
    if (firstTime) {
      adapter.clear();
    }
    adapter.addAll(repos);
  }

  @Override
  public void onResponseEmpty() {
    Toast.makeText(getContext(), R.string.empty_repos, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void hideLoading() {
    refreshLayout.post(() -> refreshLayout.setRefreshing(false));
  }
}
