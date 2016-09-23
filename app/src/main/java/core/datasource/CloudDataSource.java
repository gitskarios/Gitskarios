package core.datasource;

import rx.Observable;

public abstract class CloudDataSource<Request, Data> {

  private RestWrapper restWrapper;

  public CloudDataSource(RestWrapper restWrapper) {
    this.restWrapper = restWrapper;
  }

  public Observable<SdkItem<Data>> execute(SdkItem<Request> e) {
    return execute(e, restWrapper);
  }

  protected abstract Observable<SdkItem<Data>> execute(SdkItem<Request> request, RestWrapper service);
}
