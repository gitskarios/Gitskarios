package com.alorma.github.sdk.services.user.events;

import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

/**
 * Created by Bernat on 03/10/2014.
 */
public class GetUserEventsClient extends GithubListClient<List<GithubEvent>> {
  private String username;
  private int page = 0;

  public GetUserEventsClient(String username) {
    super();
    this.username = username;
  }

  public GetUserEventsClient(String username, int page) {
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
          eventsService.events(username, this);
        } else {
          eventsService.events(username, page, this);
        }
      }
    };
  }
}
