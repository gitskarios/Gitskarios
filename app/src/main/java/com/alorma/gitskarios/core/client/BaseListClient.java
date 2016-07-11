package com.alorma.gitskarios.core.client;

import com.alorma.gitskarios.core.ApiClient;
import com.alorma.gitskarios.core.Pair;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.converter.Converter;
import rx.Observable;
import rx.Subscriber;

public abstract class BaseListClient<K> implements RequestInterceptor, RestAdapter.Log {

  public URI last;
  public URI next;
  public int lastPage;
  public int nextPage;
  private ApiClient client;

  public BaseListClient(ApiClient client) {
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

  public Observable<? extends Pair<K, Integer>> observable() {
    return getApiObservable();
  }

  private Observable<? extends Pair<K, Integer>> getApiObservable() {
    return Observable.create(getApiObservable(getRestAdapter()));
  }

  protected abstract ApiSubscriber getApiObservable(RestAdapter restAdapter);

  protected Converter customConverter() {
    return null;
  }

  public ApiClient getClient() {
    return client;
  }

  protected String getToken() {
    if (TokenProvider.getInstance() != null) {
      return TokenProvider.getInstance().getToken();
    }
    return null;
  }

  public abstract class ApiSubscriber
      implements Observable.OnSubscribe<Pair<K, Integer>>, Callback<K> {

    Subscriber<? super Pair<K, Integer>> subscriber;

    public ApiSubscriber() {
    }

    @Override
    public void success(K k, Response r) {
      subscriber.onNext(new Pair<>(k, getLinkData(r)));
      subscriber.onCompleted();
    }

    @Override
    public void failure(RetrofitError error) {
      subscriber.onError(error);
    }

    private Integer getLinkData(Response r) {
      if (r != null) {
        List<Header> headers = r.getHeaders();
        Map<String, String> headersMap = new HashMap<String, String>(headers.size());
        for (Header header : headers) {
          headersMap.put(header.getName(), header.getValue());
        }

        String link = headersMap.get("Link");

        if (link != null) {
          String[] parts = link.split(",");
          try {
            PaginationLink paginationLink = new PaginationLink(parts[0]);
            return paginationLink.rel == RelType.next ? paginationLink.page : null;
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      return null;
    }

    @Override
    public void call(Subscriber<? super Pair<K, Integer>> subscriber) {
      this.subscriber = subscriber;
      call(getRestAdapter());
    }

    protected abstract void call(RestAdapter restAdapter);
  }
}
