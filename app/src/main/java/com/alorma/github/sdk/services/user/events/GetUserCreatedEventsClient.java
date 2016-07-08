package com.alorma.github.sdk.services.user.events;

import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

public class GetUserCreatedEventsClient extends GithubListClient<List<GithubEvent>> {

  private String username;
  private int page = 0;

  public GetUserCreatedEventsClient(String username) {
    super();
    this.username = username;
  }

  public GetUserCreatedEventsClient(String username, int page) {
    super();
    this.username = username;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        EventsService eventsService = restAdapter.create(EventsService.class);
        if (page == 0) {
          eventsService.createdEvents(username, this);
        } else {
          eventsService.createdEvents(username, page, this);
        }
      }
    };
  }
}
