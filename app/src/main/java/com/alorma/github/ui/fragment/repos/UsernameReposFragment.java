package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.presenter.repos.UserRepositoriesPresenter;
import com.alorma.github.sdk.core.repositories.Repo;
import com.alorma.github.ui.listeners.TitleProvider;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import javax.inject.Inject;

public class UsernameReposFragment extends ReposFragment implements TitleProvider,
    Presenter.Callback<List<Repo>> {

  @Inject UserRepositoriesPresenter userRepositoriesPresenter;
  private String username;

  public static UsernameReposFragment newInstance(String username) {
    UsernameReposFragment currentAccountReposFragment = new UsernameReposFragment();
    if (username != null) {
      Bundle bundle = new Bundle();
      bundle.putString("USERNAME", username);

      currentAccountReposFragment.setArguments(bundle);
    }
    return currentAccountReposFragment;
  }

  @Override
  protected void initInjectors(ApiComponent apiComponent) {
    apiComponent.inject(this);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      username = getArguments().getString("USERNAME");
    }
  }

  @Override
  public void onStart() {
    super.onStart();

  }

  @Override
  protected void onRefresh() {
    userRepositoriesPresenter.load(username, this);
  }

  @Override
  public int getTitle() {
    return R.string.repositories;
  }

  @Override
  public IIcon getTitleIcon() {
    return Octicons.Icon.oct_repo;
  }

  @Override
  public void loadMoreItems() {
    userRepositoriesPresenter.loadMore(username, this);
  }
}
