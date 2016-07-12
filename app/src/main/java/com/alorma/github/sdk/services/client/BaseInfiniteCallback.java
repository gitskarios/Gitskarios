package com.alorma.github.sdk.services.client;

import com.alorma.gitskarios.core.client.PaginationLink;
import com.alorma.gitskarios.core.client.RelType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscriber;

public abstract class BaseInfiniteCallback<K> implements Observable.OnSubscribe<K>, Callback<K> {

  protected Subscriber<? super K> subscriber;
  private List<K> ks;

  public BaseInfiniteCallback() {

  }

  @Override
  public void call(Subscriber<? super K> subscriber) {
    this.subscriber = subscriber;
    ks = new ArrayList<>();
    execute();
  }

  @Override
  public void success(K k, Response response) {
    int nextPage = getLinkData(response);
    subscriber.onNext(k);
    if (nextPage != -1) {
      executePaginated(nextPage);
    } else {
      subscriber.onCompleted();
    }
  }

  public abstract void execute();

  protected abstract void executePaginated(int nextPage);

  @Override
  public void failure(RetrofitError error) {
    subscriber.onError(error);
  }

  public int getLinkData(Response r) {
    List<Header> headers = r.getHeaders();
    Map<String, String> headersMap = new HashMap<String, String>(headers.size());
    for (Header header : headers) {
      headersMap.put(header.getName(), header.getValue());
    }

    String link = headersMap.get("Link");

    if (link != null) {
      String[] parts = link.split(",");
      try {
        PaginationLink bottomPaginationLink = new PaginationLink(parts[0]);
        if (bottomPaginationLink.rel == RelType.next) {
          return bottomPaginationLink.page;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return -1;
  }
}