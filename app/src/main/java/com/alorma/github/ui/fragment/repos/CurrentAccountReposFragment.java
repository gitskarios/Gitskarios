package com.alorma.github.ui.fragment.repos;

import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.module.repository.CurrentAccountRepositoriesModule;
import com.alorma.github.presenter.repos.AuthUserRepositoriesPresenter;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import javax.inject.Inject;

public class CurrentAccountReposFragment extends ReposFragment {

  @Inject AuthUserRepositoriesPresenter presenter;

  public static CurrentAccountReposFragment newInstance() {
    return new CurrentAccountReposFragment();
  }

  @Override
  protected void initInjectors(ApiComponent apiComponent) {
    apiComponent
            .plus(new CurrentAccountRepositoriesModule())
            .inject(this);
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
    presenter.execute(null);
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
    presenter.executePaginated(null);
  }
}
