package com.alorma.github.sdk.security;

import com.alorma.gitskarios.core.ApiClient;

/**
 * Created by Bernat on 08/07/2014.
 */
public class GitHub implements ApiClient {

  private String hostname;

  public GitHub() {

  }

  public GitHub(String hostname) {
    if (hostname != null) {
      if (!hostname.startsWith("https://")) {
        hostname = "https://" + hostname;
      }
      this.hostname = hostname;
    }
  }

  @Override
  public String getApiOauthUrlEndpoint() {
    return hostname == null ? "https://github.com" : hostname;
  }

  @Override
  public String getApiEndpoint() {
    String hostname = "https://api.github.com";

    if (this.hostname != null) {
      hostname = this.hostname;
      if (!hostname.endsWith("/")) {
        hostname = hostname + "/";
      }

      hostname = hostname + "api/v3/";
    }

    return hostname;
  }

  @Override
  public String getType() {
    return "github";
  }
}
