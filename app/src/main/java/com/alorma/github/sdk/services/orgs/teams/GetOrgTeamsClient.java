package com.alorma.github.sdk.services.orgs.teams;

import com.alorma.github.sdk.bean.dto.response.Team;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

/**
 * Created by Bernat on 04/09/2014.
 */
public class GetOrgTeamsClient extends GithubListClient<List<Team>> {
  private String org;
  private int page = -1;

  public GetOrgTeamsClient(String org) {
    super();
    this.org = org;
  }

  public GetOrgTeamsClient(String org, int page) {
    super();
    this.org = org;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        TeamsService orgsService = restAdapter.create(TeamsService.class);
        if (page == -1) {
          orgsService.teams(org, this);
        } else {
          orgsService.teams(org, page, this);
        }
      }
    };
  }
}
