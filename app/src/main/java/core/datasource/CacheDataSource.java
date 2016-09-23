package core.datasource;

import rx.Observable;

public interface CacheDataSource<Request, Data> {

  void saveData(SdkItem<Request> request, SdkItem<Data> data);

  Observable<SdkItem<Data>> getData(SdkItem<Request> request);
}
