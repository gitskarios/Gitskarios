package com.alorma.github.ui.fragment.orgs;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.module.repository.OrganizationRepositoriesModule;
import com.alorma.github.presenter.repos.OrganizationRepositoriesPresenter;
import com.alorma.github.ui.fragment.repos.ReposFragment;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import javax.inject.Inject;

public class OrgsReposFragment extends ReposFragment {

  private static final String ORGS = "ORGS";
  @Inject OrganizationRepositoriesPresenter presenter;
  private String orgName;

  public static OrgsReposFragment newInstance() {
    return new OrgsReposFragment();
  }

  public static OrgsReposFragment newInstance(String username) {
    OrgsReposFragment reposFragment = new OrgsReposFragment();
    if (username != null) {
      Bundle bundle = new Bundle();
      bundle.putString(ORGS, username);

      reposFragment.setArguments(bundle);
    }
    return reposFragment;
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_People;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_People;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      orgName = getArguments().getString(ORGS);
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
            .plus(new OrganizationRepositoriesModule())
            .inject(this);
  }

  @Override
  protected void onRefresh() {
    presenter.execute(orgName);
  }

  @Override
  public int getTitle() {
    return R.string.navigation_general_repositories;
  }

  @Override
  public IIcon getTitleIcon() {
    return Octicons.Icon.oct_repo;
  }

  @Override
  public void loadMoreItems() {
    presenter.executePaginated(orgName);
  }
}