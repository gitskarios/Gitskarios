package com.alorma.github.presenter.repos;

import android.support.annotation.NonNull;
import com.alorma.github.injector.PerActivity;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.repositories.CloudWatchedRepositoriesDataSource;
import com.alorma.github.sdk.core.repositories.Repo;
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
