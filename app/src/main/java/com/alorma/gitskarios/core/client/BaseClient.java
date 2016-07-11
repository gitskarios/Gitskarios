package com.alorma.gitskarios.core.client;

import com.alorma.gitskarios.core.ApiClient;
import java.util.concurrent.TimeUnit;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.Response;
import retrofit.converter.Converter;
import rx.Observable;
import rx.functions.Func2;

public abstract class BaseClient<K> implements RequestInterceptor, RestAdapter.Log {

  private ApiClient client;

  public BaseClient(ApiClient client) {
    this.client = client;
  }

  protected RestAdapter getRestAdapter() {
    RestAdapter.Builder restAdapterBuilder =
        new RestAdapter.Builder().setEndpoint(client.getApiEndpoint())
            .setRequestInterceptor(this)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setLog(this);

    if (customConverter() != null) {
      restAdapterBuilder.setConverter(customConverter());
    }

    if (getInterceptor() != null) {
      restAdapterBuilder.setClient(getInterceptor());
    }

    return restAdapterBuilder.build();
  }

  protected Client getInterceptor() {
    return null;
  }

  public Observable<K> observable() {
    return getApiObservable(getRestAdapter()).retry(this::retry).debounce(100, TimeUnit.MILLISECONDS);
  }

  protected Boolean retry(Integer integer, Throwable throwable) {
      if (throwable instanceof RetrofitError) {
        Response response = ((RetrofitError) throwable).getResponse();
        if (response != null) {
          return response.getStatus() == 202 && integer < 3;
        }
      }
      return integer < 3;
  }

  protected abstract Observable<K> getApiObservable(RestAdapter restAdapter);

  protected Converter customConverter() {
    return null;
  }

  protected String getToken() {
    if (TokenProvider.getInstance() != null) {
      return TokenProvider.getInstance().getToken();
    }
    return "";
  }

  public ApiClient getClient() {
    return client;
  }
}
