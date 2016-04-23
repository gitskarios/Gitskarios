package com.alorma.github.ui.fragment.orgs;

import android.os.Bundle;
import com.alorma.github.R;
import com.alorma.github.presenter.repos.OrganizationRepositoriesPresenter;
import com.alorma.github.presenter.repos.RepositoriesPresenter;
import com.alorma.github.ui.fragment.repos.ReposFragment;
import com.alorma.github.utils.RepoUtils;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

public class OrgsReposFragment extends ReposFragment {

  private static final String ORGS = "ORGS";
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

  private RepositoriesPresenter presenter;

  @Override
  public void onStart() {
    super.onStart();

    String sortOrder = RepoUtils.sortOrder(getActivity());
    presenter = new OrganizationRepositoriesPresenter(sortOrder);

    if (getArguments() != null) {
      orgName = getArguments().getString(ORGS);
    }

    presenter.load(orgName, this);
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
    presenter.loadMore(orgName, this);
  }
}