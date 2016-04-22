package com.alorma.github.ui.fragment.repos;

import com.alorma.github.R;
import com.alorma.github.presenter.repos.AuthUserRepositoriesPresenter;
import com.alorma.github.presenter.repos.RepositoriesPresenter;
import com.alorma.github.sdk.core.repositories.Repo;
import com.alorma.github.utils.RepoUtils;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;

public class CurrentAccountReposFragment extends ReposFragment {

  private RepositoriesPresenter presenter;

  public static CurrentAccountReposFragment newInstance() {
    return new CurrentAccountReposFragment();
  }

  @Override
  public void onStart() {
    super.onStart();

    String sortOrder = RepoUtils.sortOrder(getActivity());
    presenter = new AuthUserRepositoriesPresenter(sortOrder);
    presenter.load(null, this);
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
  public void onResponse(List<Repo> repos) {

  }
}
