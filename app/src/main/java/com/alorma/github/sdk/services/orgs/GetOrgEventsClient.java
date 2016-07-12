package com.alorma.github.sdk.services.orgs;

import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

public class GetOrgEventsClient extends GithubListClient<List<GithubEvent>> {

  private String username;
  private String org;
  private int page;

  public GetOrgEventsClient(String username, String org) {
    super();
    this.username = username;
    this.org = org;
  }

  public GetOrgEventsClient(String username, String org, int page) {
    this(username, org);
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        OrgsService orgsService = restAdapter.create(OrgsService.class);
        if (page == 0) {
          orgsService.events(username, org, this);
        } else {
          orgsService.events(username, org, page, this);
        }
      }
    };
  }
}
