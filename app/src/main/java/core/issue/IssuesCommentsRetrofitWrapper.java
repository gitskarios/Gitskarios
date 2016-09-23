package core.issue;

import core.ApiClient;
import core.datasource.RetrofitWrapper;
import retrofit2.Retrofit;

public class IssuesCommentsRetrofitWrapper extends RetrofitWrapper {
  public IssuesCommentsRetrofitWrapper(ApiClient apiClient, String token) {
    super(apiClient, token);
  }

  @Override
  protected IssueCommentsRetrofit get(Retrofit retrofit) {
    return retrofit.create(IssueCommentsRetrofit.class);
  }
}
