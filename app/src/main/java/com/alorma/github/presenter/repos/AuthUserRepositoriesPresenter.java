package com.alorma.github.presenter.repos;

import android.support.annotation.NonNull;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.repositories.CloudUserRepositoriesDataSource;
import com.alorma.github.sdk.core.repositories.Repo;
import java.util.List;

public class AuthUserRepositoriesPresenter extends RepositoriesPresenter {

  public AuthUserRepositoriesPresenter(String sortOrder) {
    super(sortOrder);
  }

  @NonNull
  protected CacheDataSource<String, List<Repo>> getUserReposCache() {
    return new UserReposCache("auth_repos");
  }

  @NonNull
  protected CloudDataSource<String, List<Repo>> getCloudRepositoriesDataSource(
      RestWrapper reposRetrofit, String sortOrder) {
    return new CloudUserRepositoriesDataSource(reposRetrofit, sortOrder);
  }
}
