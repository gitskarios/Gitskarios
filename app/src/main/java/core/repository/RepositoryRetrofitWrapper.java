package core.repository;

import core.ApiClient;
import core.datasource.RetrofitWrapper;
import core.issue.IssueCommentsRetrofit;
import core.repositories.ReposService;
import retrofit2.Retrofit;

public class RepositoryRetrofitWrapper extends RetrofitWrapper {
  public RepositoryRetrofitWrapper(ApiClient apiClient, String token) {
    super(apiClient, token);
  }

  @Override
  protected RepoActionsService get(Retrofit retrofit) {
    return retrofit.create(RepoActionsService.class);
  }
}
