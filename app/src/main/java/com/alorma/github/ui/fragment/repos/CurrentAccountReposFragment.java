package com.alorma.github.ui.fragment.repos;

import android.support.v4.app.Fragment;
import com.alorma.github.R;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.presenter.repos.RepositoriesPresenter;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.RepoUtils;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;

public class CurrentAccountReposFragment extends Fragment implements TitleProvider,
    Presenter.Callback<List<com.alorma.github.sdk.core.repositories.Repo>> {


  public static CurrentAccountReposFragment newInstance() {
    return new CurrentAccountReposFragment();
  }

  @Override
  public void onStart() {
    super.onStart();

    RepositoriesPresenter repositoriesPresenter =
        new RepositoriesPresenter(RepoUtils.sortOrder(getActivity()));

    repositoriesPresenter.load(null, this);
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
  public void showLoading() {

  }

  @Override
  public void onResponse(List<com.alorma.github.sdk.core.repositories.Repo> repos) {

  }

  @Override
  public void hideLoading() {

  }
}
