package com.alorma.github.ui.fragment.repos;

import android.support.annotation.NonNull;
import com.alorma.github.R;
import com.alorma.github.presenter.repos.AuthUserRepositoriesPresenter;
import com.alorma.github.presenter.repos.RepositoriesPresenter;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

public class CurrentAccountReposFragment extends ReposFragment {

  public static CurrentAccountReposFragment newInstance() {
    return new CurrentAccountReposFragment();
  }

  @NonNull
  @Override
  protected RepositoriesPresenter getPresenter(String sortOrder) {
    return new AuthUserRepositoriesPresenter(sortOrder);
  }

  @Override
  public int getTitle() {
    return R.string.navigation_repos;
  }

  @Override
  public IIcon getTitleIcon() {
    return Octicons.Icon.oct_repo;
  }

}
