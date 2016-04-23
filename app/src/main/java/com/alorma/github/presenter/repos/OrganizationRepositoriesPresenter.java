package com.alorma.github.presenter.repos;

import android.support.annotation.NonNull;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.repositories.CloudMembershipRepositoriesDataSource;
import com.alorma.github.sdk.core.repositories.CloudOrganizationRepositoriesDataSource;
import com.alorma.github.sdk.core.repositories.Repo;
import java.util.List;

public class OrganizationRepositoriesPresenter extends RepositoriesPresenter {

  public OrganizationRepositoriesPresenter(String sortOrder) {
    super(sortOrder);
  }

  @NonNull
  protected CacheDataSource<String, List<Repo>> getUserReposCacheDataSource() {
    return new AuthUserReposCache("org_repos");
  }

  @NonNull
  protected CloudDataSource<String, List<Repo>> getCloudRepositoriesDataSource(
      RestWrapper reposRetrofit, String sortOrder) {
    return new CloudOrganizationRepositoriesDataSource(reposRetrofit, sortOrder);
  }
}
