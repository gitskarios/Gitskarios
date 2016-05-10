package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
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
  public void onStart() {
    super.onStart();

    if (getArguments() != null) {
      username = getArguments().getString(USERNAME);
    }

    presenter.load(username, this);
  }

  @Override
  protected void initInjectors(ApiComponent apiComponent) {
    apiComponent.inject(this);
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
    presenter.loadMore(username, this);
  }
}
