package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;
import com.alorma.github.R;
import com.alorma.github.presenter.repos.RepositoriesPresenter;
import com.alorma.github.presenter.repos.StarredRepositoriesPresenter;
import com.alorma.github.sdk.core.repositories.Repo;
import com.alorma.github.utils.RepoUtils;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;

public class StarredReposFragment extends ReposFragment {

  private static final String USERNAME = "USERNAME";
  private String username;

  public static StarredReposFragment newInstance() {
    return new StarredReposFragment();
  }

  public static StarredReposFragment newInstance(String username) {
    StarredReposFragment reposFragment = new StarredReposFragment();
    if (username != null) {
      Bundle bundle = new Bundle();
      bundle.putString(USERNAME, username);

      reposFragment.setArguments(bundle);
    }
    return reposFragment;
  }

  private RepositoriesPresenter presenter;

  @Override
  public void onStart() {
    super.onStart();

    String sortOrder = RepoUtils.sortOrder(getActivity());
    presenter = new StarredRepositoriesPresenter(sortOrder);

    if (getArguments() != null) {
      username = getArguments().getString(USERNAME);
    }

    presenter.load(username, this);
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
