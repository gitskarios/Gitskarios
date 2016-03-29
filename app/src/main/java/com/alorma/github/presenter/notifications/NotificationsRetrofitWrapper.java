package com.alorma.github.presenter.notifications;

import com.alorma.github.presenter.RetrofitWrapper;
import com.alorma.github.sdk.core.ApiClient;
import retrofit2.Retrofit;

public class NotificationsRetrofitWrapper extends RetrofitWrapper{
  public NotificationsRetrofitWrapper(ApiClient apiClient, String token) {
    super(apiClient, token);
  }

  @Override
  protected NotificationsService get(Retrofit retrofit) {
    return retrofit.create(NotificationsService.class);
  }
}
