package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.alorma.github.R;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.presenter.repos.AuthUserRepositoriesPresenter;
import com.alorma.github.sdk.core.repositories.Repo;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.RepoUtils;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;

public class UsernameReposFragment extends Fragment implements TitleProvider,
    Presenter.Callback<List<com.alorma.github.sdk.core.repositories.Repo>> {

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
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (getArguments() != null) {
      username = getArguments().getString("USERNAME");
    }

    AuthUserRepositoriesPresenter authUserRepositoriesPresenter =
        new AuthUserRepositoriesPresenter(RepoUtils.sortOrder(getActivity()));

    authUserRepositoriesPresenter.load(username, this);
  }

  protected void loadArguments() {

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
  public void showLoading() {

  }

  @Override
  public void onResponse(List<Repo> repos) {

  }

  @Override
  public void hideLoading() {

  }

  @Override
  public void onResponseEmpty() {

  }
}
