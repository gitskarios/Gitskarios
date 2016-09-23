package core.orgs;

import core.ApiClient;
import core.datasource.RetrofitWrapper;
import retrofit2.Retrofit;

public class OrganizationsRetrofitWrapper extends RetrofitWrapper {
  public OrganizationsRetrofitWrapper(ApiClient apiClient, String token) {
    super(apiClient, token);
  }

  @Override
  protected OrganizationsService get(Retrofit retrofit) {
    return retrofit.create(OrganizationsService.class);
  }
}
