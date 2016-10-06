package core.datasource;

import rx.Observable;

public class EmptyCacheDataSource<Request, Data> implements CacheDataSource<Request, Data> {
    @Override
    public void saveData(SdkItem<Request> request, SdkItem<Data> data) {

    }

    @Override
    public Observable<SdkItem<Data>> getData(SdkItem<Request> request) {
        return null;
    }
}
