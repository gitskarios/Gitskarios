package core.issues;

import core.ApiClient;
import core.datasource.RetrofitWrapper;
import core.issue.IssuesSearchService;
import core.issue.IssuesService;
import retrofit2.Retrofit;

public class IssuesRetrofitWrapper extends RetrofitWrapper {
  public IssuesRetrofitWrapper(ApiClient apiClient, String token) {
    super(apiClient, token);
  }

  @Override
  protected IssuesService get(Retrofit retrofit) {
    return retrofit.create(IssuesService.class);
  }
}
