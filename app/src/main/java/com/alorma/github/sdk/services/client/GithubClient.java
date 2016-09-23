package com.alorma.github.sdk.services.client;

import com.alorma.gitskarios.core.client.BaseClient;
import com.alorma.gitskarios.core.client.LogProvider;
import com.alorma.gitskarios.core.client.UrlProvider;
import core.ApiClient;
import core.Github;
import core.GithubEnterprise;

public abstract class GithubClient<K> extends BaseClient<K> {

  public GithubClient() {
    super(getApiClient());
  }

  private static ApiClient getApiClient() {
    if (UrlProvider.getInstance() == null) {
      return new Github();
    } else {
      String url = UrlProvider.getInstance().getUrl();
      return new GithubEnterprise(url);
    }
  }

  @Override
  public void intercept(RequestFacade request) {
    request.addHeader("Accept", getAcceptHeader());
    request.addHeader("User-Agent", "Gitskarios");
    if (getToken() != null && getToken().length() > 0) {
      request.addHeader("Authorization", "token " + getToken());
    }
  }

  @Override
  public void log(String message) {
    if (LogProvider.getInstance() != null) {
      LogProvider.getInstance().log(message);
    }
  }

  public String getAcceptHeader() {
    return "application/vnd.github.v3.json";
  }
}
