package com.alorma.github.presenter.repos;

import android.support.annotation.NonNull;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.repositories.CloudStarredRepositoriesDataSource;
import com.alorma.github.sdk.core.repositories.Repo;
import java.util.List;

public class AuthStarredRepositoriesPresenter extends RepositoriesPresenter {

  public AuthStarredRepositoriesPresenter(String sortOrder) {
    super(sortOrder);
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
