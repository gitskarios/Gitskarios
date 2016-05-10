package com.alorma.github.presenter;

import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.repository.GenericRepository;
import com.alorma.github.sdk.core.usecase.GenericUseCase;
import com.alorma.gitskarios.core.client.TokenProvider;
import javax.inject.Inject;

public abstract class Presenter<Request, Response> {

  @Inject ApiClient apiClient;

  public abstract void load(Request request, Callback<Response> responseCallback);

  public abstract void loadMore(Request request, Callback<Response> responseCallback);

  protected GenericUseCase<Request, Response> config() {
    return new GenericUseCase<>(configRepository(getRest(apiClient, getToken())));
  }

  protected abstract GenericRepository<Request, Response> configRepository(RestWrapper restWrapper);

  protected abstract RestWrapper getRest(ApiClient apiClient, String token);

  protected String getToken() {
    return TokenProvider.getInstance().getToken();
  }

  public abstract void action(Response response, Callback<Response> responseCallback);

  public interface Callback<Response> {

    void showLoading();

    void onResponse(Response response);

    void hideLoading();

    void onResponseEmpty();
  }
}
