package com.alorma.github.ui.fragment.repos;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.presenter.repos.RepositoriesPresenter;
import com.alorma.github.sdk.core.repositories.Repo;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.RepoUtils;
import com.mikepenz.iconics.typeface.IIcon;
import java.util.List;

public abstract class ReposFragment extends Fragment
    implements TitleProvider, Presenter.Callback<List<Repo>> {

  @Override
  public void onStart() {
    super.onStart();

    String sortOrder = RepoUtils.sortOrder(getActivity());
    getPresenter(sortOrder).load(null, this);
  }

  @NonNull
  protected abstract RepositoriesPresenter getPresenter(String sortOrder);

  @Override
  public abstract int getTitle();

  @Override
  public abstract IIcon getTitleIcon();

  @Override
  public void showLoading() {

  }

  @Override
  public void onResponse(List<Repo> repos) {

  }

  @Override
  public void onResponseEmpty() {

  }

  @Override
  public void hideLoading() {

  }
}
