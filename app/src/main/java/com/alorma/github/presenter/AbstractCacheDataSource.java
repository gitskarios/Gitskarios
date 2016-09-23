package com.alorma.github.presenter;

import core.datasource.CacheDataSource;
import core.datasource.SdkItem;
import rx.Observable;

/**
 * Abstract class for caching SdkItem<Data> objects by some key.
 * This class also provides method for receiving SdkItem<Data> object as observable.
 *
 * @param <Request> uses it for
 */
public abstract class AbstractCacheDataSource<Request, Data> implements CacheDataSource<Request, Data> {

  @Override
  public void saveData(SdkItem<Request> request, SdkItem<Data> data) {
    CacheWrapper.cache().set(getCacheKey(request.getK(), request.getPage()), data);
  }

  @Override
  public Observable<SdkItem<Data>> getData(SdkItem<Request> request) {
    SdkItem<Data> sdkItem = CacheWrapper.cache().get(getCacheKey(request.getK(), request.getPage()));
    if (checkItemIsEmpty(sdkItem)) {
      return Observable.empty();
    } else {
      return Observable.just(sdkItem);
    }
  }

  protected boolean checkItemIsEmpty(SdkItem<Data> sdkItem) {
    return sdkItem == null || sdkItem.getK() == null;
  }

  protected abstract String getCacheKey(Request k, Integer page);
}
