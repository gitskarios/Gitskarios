package com.alorma.github.presenter.repos;

import android.support.annotation.NonNull;
import com.alorma.github.injector.scope.PerActivity;
import core.datasource.CacheDataSource;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.repositories.CloudWatchedRepositoriesDataSource;
import core.repositories.Repo;
import java.util.List;
import javax.inject.Inject;

@PerActivity public class AuthWatchedRepositoriesPresenter extends RepositoriesPresenter {

  @Inject
  public AuthWatchedRepositoriesPresenter() {
    super();
  }

  @NonNull
  protected CacheDataSource<String, List<Repo>> getUserReposCacheDataSource() {
    return new AuthUserReposCache("auth_watched");
  }

  @NonNull
  protected CloudDataSource<String, List<Repo>> getCloudRepositoriesDataSource(
      RestWrapper reposRetrofit, String sortOrder) {
    return new CloudWatchedRepositoriesDataSource(reposRetrofit, sortOrder);
  }
}
