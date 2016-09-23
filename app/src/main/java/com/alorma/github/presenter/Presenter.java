package com.alorma.github.presenter;

import com.alorma.gitskarios.core.client.TokenProvider;
import core.ApiClient;
import core.datasource.RestWrapper;
import core.repository.GenericRepository;
import core.usecase.GenericUseCase;
import javax.inject.Inject;

public abstract class Presenter<Request, Response> {

  @Inject protected ApiClient apiClient;

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

  public abstract void action(Response response, Callback<Response> responseCallback
          , boolean firstTime);

  public interface Callback<Response> {

    void showLoading();

    void onResponse(Response response, boolean firstTime);

    void hideLoading();

    void onResponseEmpty();
  }
}
