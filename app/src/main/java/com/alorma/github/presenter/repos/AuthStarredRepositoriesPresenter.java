package com.alorma.github.presenter.repos;

import android.support.annotation.NonNull;
import com.alorma.github.injector.PerActivity;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.repositories.CloudStarredRepositoriesDataSource;
import com.alorma.github.sdk.core.repositories.Repo;
import java.util.List;
import javax.inject.Inject;

@PerActivity public class AuthStarredRepositoriesPresenter extends RepositoriesPresenter {

  @Inject
  public AuthStarredRepositoriesPresenter() {
    super();
  }

  @NonNull
  protected CacheDataSource<String, List<Repo>> getUserReposCacheDataSource() {
    return new AuthUserReposCache("auth_starred");
  }

  @NonNull
  protected CloudDataSource<String, List<Repo>> getCloudRepositoriesDataSource(
      RestWrapper reposRetrofit, String sortOrder) {
    return new CloudStarredRepositoriesDataSource(reposRetrofit, sortOrder);
  }
}
