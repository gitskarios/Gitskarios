package com.alorma.github.presenter.repos;

import android.support.annotation.NonNull;
import com.alorma.github.injector.scope.PerActivity;
import core.datasource.CacheDataSource;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.repositories.CloudMembershipRepositoriesDataSource;
import core.repositories.Repo;
import java.util.List;
import javax.inject.Inject;

@PerActivity public class AuthMembershipRepositoriesPresenter extends RepositoriesPresenter {

  @Inject
  public AuthMembershipRepositoriesPresenter() {
    super();
  }

  @NonNull
  protected CacheDataSource<String, List<Repo>> getUserReposCacheDataSource() {
    return new AuthUserReposCache("auth_membership");
  }

  @NonNull
  protected CloudDataSource<String, List<Repo>> getCloudRepositoriesDataSource(
      RestWrapper reposRetrofit, String sortOrder) {
    return new CloudMembershipRepositoriesDataSource(reposRetrofit, sortOrder);
  }
}
