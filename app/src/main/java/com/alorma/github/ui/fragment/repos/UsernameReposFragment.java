package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.module.repository.UsernameRepositoriesModule;
import com.alorma.github.presenter.repos.UserRepositoriesPresenter;
import com.alorma.github.ui.listeners.TitleProvider;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import javax.inject.Inject;

public class UsernameReposFragment extends ReposFragment implements TitleProvider {

  @Inject UserRepositoriesPresenter presenter;
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
    apiComponent
            .plus(new UsernameRepositoriesModule())
            .inject(this);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      username = getArguments().getString("USERNAME");
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    presenter.attachView(this);
    onRefresh();
  }

  @Override
  public void onPause() {
    super.onPause();
    presenter.detachView();
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  protected void onRefresh() {
    presenter.execute(username);
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
    presenter.executePaginated(username);
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Profile;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Profile;
  }
}
