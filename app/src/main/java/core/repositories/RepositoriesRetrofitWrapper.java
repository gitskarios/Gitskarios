package core.repositories;

import core.ApiClient;
import core.datasource.RetrofitWrapper;
import retrofit2.Retrofit;

public class RepositoriesRetrofitWrapper extends RetrofitWrapper {
  public RepositoriesRetrofitWrapper(ApiClient apiClient, String token) {
    super(apiClient, token);
  }

  @Override
  protected ReposService get(Retrofit retrofit) {
    return retrofit.create(ReposService.class);
  }
}
