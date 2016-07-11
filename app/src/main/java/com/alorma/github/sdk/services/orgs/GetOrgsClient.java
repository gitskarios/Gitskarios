package com.alorma.github.sdk.services.orgs;

import com.alorma.github.sdk.bean.dto.response.Organization;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.gitskarios.core.client.UsernameProvider;
import java.util.List;
import retrofit.RestAdapter;

/**
 * Created by Bernat on 04/09/2014.
 */
public class GetOrgsClient extends GithubListClient<List<Organization>> {
  private String username;
  private int page = -1;

  public GetOrgsClient(String username) {
    super();
    this.username = username;
  }

  public GetOrgsClient(String org, int page) {
    super();
    this.username = org;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        String apiUsername =
            UsernameProvider.getInstance() != null ? UsernameProvider.getInstance().getUsername()
                : "";
        if (username != null && username.equalsIgnoreCase(apiUsername)) {
          username = null;
        }
        OrgsService orgsService = restAdapter.create(OrgsService.class);
        if (page == -1) {
          if (username == null) {
            orgsService.orgs(this);
          } else {
            orgsService.orgsByUser(username, this);
          }
        } else {
          if (username == null) {
            orgsService.orgs(page, this);
          } else {
            orgsService.orgsByUser(username, page, this);
          }
        }
      }
    };
  }
}
