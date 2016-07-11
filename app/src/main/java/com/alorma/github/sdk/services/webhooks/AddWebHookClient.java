package com.alorma.github.sdk.services.webhooks;

import com.alorma.github.sdk.bean.dto.request.WebHookRequest;
import com.alorma.github.sdk.bean.dto.request.WebHookResponse;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

public class AddWebHookClient extends GithubClient<WebHookResponse> {

  private String owner;
  private String repo;
  private WebHookRequest webhook;

  public AddWebHookClient(String owner, String repo, WebHookRequest webhook) {
    this.owner = owner;
    this.repo = repo;
    this.webhook = webhook;
  }

  @Override
  protected Observable<WebHookResponse> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(WebHookService.class).addWebHook(owner, repo, webhook);
  }
}
