package com.alorma.github.presenter.repos;

import android.support.annotation.NonNull;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.repositories.CloudStarredRepositoriesDataSource;
import com.alorma.github.sdk.core.repositories.CloudWatchedRepositoriesDataSource;
import com.alorma.github.sdk.core.repositories.Repo;
import java.util.List;

public class AuthWatchedRepositoriesPresenter extends RepositoriesPresenter {

  public AuthWatchedRepositoriesPresenter(String sortOrder) {
    super(sortOrder);
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
