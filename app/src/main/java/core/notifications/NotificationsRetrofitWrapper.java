package core.notifications;

import core.ApiClient;
import core.datasource.RetrofitWrapper;
import retrofit2.Retrofit;

public class NotificationsRetrofitWrapper extends RetrofitWrapper {
  public NotificationsRetrofitWrapper(ApiClient apiClient, String token) {
    super(apiClient, token);
  }

  @Override
  protected NotificationsService get(Retrofit retrofit) {
    return retrofit.create(NotificationsService.class);
  }
}
