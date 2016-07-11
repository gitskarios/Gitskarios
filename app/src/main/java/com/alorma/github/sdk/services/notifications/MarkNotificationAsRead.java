package com.alorma.github.sdk.services.notifications;

import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

public class MarkNotificationAsRead extends GithubClient<Boolean> {

  private long notification;

  public MarkNotificationAsRead(long notification) {
    super();
    this.notification = notification;
  }

  @Override
  protected Observable<Boolean> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(NotificationsService.class)
        .markThreadAsRead(String.valueOf(notification), new Object())
        .map(response -> response != null && response.getStatus() == 205);
  }
}
