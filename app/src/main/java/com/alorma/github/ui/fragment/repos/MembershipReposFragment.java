package com.alorma.github.ui.fragment.repos;

import android.support.annotation.NonNull;
import com.alorma.github.R;
import com.alorma.github.presenter.repos.RepositoriesPresenter;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.repositories.Repo;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;

public class MembershipReposFragment extends ReposFragment {

  public static MembershipReposFragment newInstance() {
    return new MembershipReposFragment();
  }

  @NonNull
  @Override
  protected RepositoriesPresenter getPresenter(String sortOrder) {
    return new RepositoriesPresenter(sortOrder) {
      @Override
      protected CacheDataSource<String, List<Repo>> getUserReposCache() {
        return null;
      }

      @Override
      protected CloudDataSource<String, List<Repo>> getCloudRepositoriesDataSource(
          RestWrapper reposRetrofit, String sortOrder) {
        return null;
      }
    };
  }

  @Override
  public int getTitle() {
    return R.string.navigation_orgs_members;
  }

  @Override
  public IIcon getTitleIcon() {
    return Octicons.Icon.oct_repo;
  }
}
