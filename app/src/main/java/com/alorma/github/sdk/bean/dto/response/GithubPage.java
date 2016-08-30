package com.alorma.github.sdk.bean.dto.response;

import com.google.gson.annotations.SerializedName;

public class GithubPage {
  @SerializedName("page_name") public String pageName;
  public String title;
  public String summary;
  public String action;
  public String sha;
  @SerializedName("html_url") public String htmlUrl;

  @Override
  public String toString() {
    return action + ": " + title;
  }
}
