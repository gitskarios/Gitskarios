package core;

public class GithubEnterprise implements ApiClient {

  private String url;

  public GithubEnterprise(String url) {
    if (url == null) {
      throw new NullPointerException();
    }
    if (url.isEmpty()) {
      throw new IllegalArgumentException();
    }
    if (url.startsWith("http://")) {
      url = url.replace("http://", "https://");
    }
    if (!url.startsWith("https://")) {
      url = "https://" + url;
    }
    this.url = url;
  }

  @Override
  public String getApiOauthUrlEndpoint() {
    return url;
  }

  @Override
  public String getApiEndpoint() {
    if (url.endsWith("/api/v3/")) {
      return url.substring(0, url.length());
    } else if (url.endsWith("/api/")) {
      return url + "v3/";
    } else if (url.endsWith("/api")) {
      return url + "/v3/";
    } else if (url.endsWith("/api/v3")) {
      return url + "/";
    } else if (!url.endsWith("/")) {
      return url + "/api/v3/";
    } else if (url.endsWith("/")) {
      return url + "api/v3/";
    }
    return url;
  }

  @Override
  public String getType() {
    return "github-enterprise";
  }
}
