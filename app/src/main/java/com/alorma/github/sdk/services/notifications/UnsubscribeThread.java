package com.alorma.github.sdk.services.notifications;

import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

public class UnsubscribeThread extends GithubClient<Boolean> {

  private long notification;

  public UnsubscribeThread(long id) {
    super();
    this.notification = id;
  }

  @Override
  protected Observable<Boolean> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(NotificationsService.class)
        .unsubscribeThread(String.valueOf(notification))
        .map(response -> response != null && response.getStatus() == 204);
  }
}
