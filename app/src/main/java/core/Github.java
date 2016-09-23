package core;

public class Github implements ApiClient {

  @Override
  public String getApiOauthUrlEndpoint() {
    return "https://github.com";
  }

  @Override
  public String getApiEndpoint() {
    return "https://api.github.com";
  }

  @Override
  public String getType() {
    return "github";
  }
}
