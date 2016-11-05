package core.datasource;

import core.ApiClient;
import retrofit2.Response;

public abstract class RestWrapper {

  private ApiClient apiClient;

  public RestWrapper(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public <T> T get() {
    return get(apiClient);
  }

  protected abstract <T> T get(ApiClient apiClient);

  public abstract boolean isPaginated(Response listResponse);

  public abstract Integer getPage(Response listResponse);

  public abstract Integer getLastPage(Response listResponse);
}
