package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.module.repository.WatchedRepositoriesModule;
import com.alorma.github.presenter.repos.AuthWatchedRepositoriesPresenter;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import javax.inject.Inject;

public class WatchedReposFragment extends ReposFragment {

  private static final String USERNAME = "USERNAME";
  @Inject AuthWatchedRepositoriesPresenter presenter;
  private String username;

  public static WatchedReposFragment newInstance() {
    return new WatchedReposFragment();
  }

  public static WatchedReposFragment newInstance(String username) {
    WatchedReposFragment reposFragment = new WatchedReposFragment();
    if (username != null) {
      Bundle bundle = new Bundle();
      bundle.putString(USERNAME, username);

      reposFragment.setArguments(bundle);
    }
    return reposFragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      username = getArguments().getString(USERNAME);
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
  protected void initInjectors(ApiComponent apiComponent) {
    apiComponent
            .plus(new WatchedRepositoriesModule())
            .inject(this);
  }

  @Override
  protected void onRefresh() {
    presenter.execute(username);
  }

  @Override
  public int getTitle() {
    return R.string.navigation_repos;
  }

  @Override
  public IIcon getTitleIcon() {
    return Octicons.Icon.oct_repo;
  }

  @Override
  public void loadMoreItems() {
    presenter.executePaginated(username);
  }
}
