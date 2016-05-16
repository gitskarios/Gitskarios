package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.presenter.repos.AuthOrgsRepositoriesPresenter;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import javax.inject.Inject;

public class ReposFragmentFromOrgs extends ReposFragment {

  private static final String USERNAME = "USERNAME";
  @Inject AuthOrgsRepositoriesPresenter presenter;
  private String username;

  public static ReposFragmentFromOrgs newInstance() {
    return new ReposFragmentFromOrgs();
  }

  public static ReposFragmentFromOrgs newInstance(String username) {
    ReposFragmentFromOrgs reposFragment = new ReposFragmentFromOrgs();
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
  protected void onRefresh() {
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