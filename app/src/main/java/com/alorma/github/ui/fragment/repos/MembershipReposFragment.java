package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.module.repository.MembershipRepositoriesModule;
import com.alorma.github.presenter.repos.AuthMembershipRepositoriesPresenter;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import javax.inject.Inject;

public class MembershipReposFragment extends ReposFragment {

  private static final String USERNAME = "USERNAME";
  @Inject AuthMembershipRepositoriesPresenter presenter;
  private String username;

  public static MembershipReposFragment newInstance() {
    return new MembershipReposFragment();
  }

  public static MembershipReposFragment newInstance(String username) {
    MembershipReposFragment reposFragment = new MembershipReposFragment();
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
  protected void onRefresh() {
    presenter.execute(username);
  }

  @Override
  protected void initInjectors(ApiComponent apiComponent) {
    apiComponent
            .plus(new MembershipRepositoriesModule())
            .inject(this);
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