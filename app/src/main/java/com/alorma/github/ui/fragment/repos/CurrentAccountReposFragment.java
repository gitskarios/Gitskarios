package com.alorma.github.ui.fragment.repos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.repos.UserReposClient;
import com.alorma.github.ui.activity.CreateRepositoryActivity;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;

public class CurrentAccountReposFragment extends BaseReposListFragment {

  private static final int CREATE_REPOS = 131;

  private String username;

  public static CurrentAccountReposFragment newInstance() {
    return new CurrentAccountReposFragment();
  }

  public static CurrentAccountReposFragment newInstance(String username) {
    CurrentAccountReposFragment currentAccountReposFragment = new CurrentAccountReposFragment();
    if (username != null) {
      Bundle bundle = new Bundle();
      bundle.putString(USERNAME, username);

      currentAccountReposFragment.setArguments(bundle);
    }
    return currentAccountReposFragment;
  }

  @Override
  public void onNext(Pair<List<Repo>, Integer> listIntegerPair) {
    super.onNext(listIntegerPair);

    if (getAdapter() != null) {
      getAdapter().showOwnerNameExtra(false);
    }
  }

  @Override
  protected void loadArguments() {
    if (getArguments() != null) {
      username = getArguments().getString(USERNAME);
    }
  }

  @Override
  protected void executeRequest() {
    super.executeRequest();

    setAction(new UserReposClient(getActivity(), username));
  }

  @Override
  protected void executePaginatedRequest(int page) {
    super.executePaginatedRequest(page);
    setAction(new UserReposClient(getActivity(), username, page));
  }

  @Override
  protected int getNoDataText() {
    return R.string.no_repositories;
  }

  @Override
  protected boolean useFAB() {
    return true;
  }

  @Override
  protected Octicons.Icon getFABGithubIcon() {
    return Octicons.Icon.oct_repo_create;
  }

  @Override
  protected void fabClick() {
    Intent intent = new Intent(getActivity(), CreateRepositoryActivity.class);
    startActivityForResult(intent, CREATE_REPOS);
  }
}
