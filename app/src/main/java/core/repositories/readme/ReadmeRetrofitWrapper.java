package core.repositories.readme;

import core.ApiClient;
import core.datasource.RetrofitWrapper;
import retrofit2.Retrofit;

public class ReadmeRetrofitWrapper extends RetrofitWrapper {

  public ReadmeRetrofitWrapper(ApiClient apiClient, String token) {
    super(apiClient, token);
  }

  @Override
  protected RepositoryReadmeService get(Retrofit retrofit) {
    return retrofit.create(RepositoryReadmeService.class);
  }
}
