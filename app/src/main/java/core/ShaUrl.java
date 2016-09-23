package core;

import com.google.gson.annotations.SerializedName;

public class ShaUrl extends Sha {

  @SerializedName("html_url")
  public String htmlUrl;

  public ShaUrl() {
  }

  public String getHtmlUrl() {
    return htmlUrl;
  }

  public void setHtmlUrl(String htmlUrl) {
    this.htmlUrl = htmlUrl;
  }

}
