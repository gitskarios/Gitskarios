package com.alorma.github.presenter.notifications;

import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.core.Github;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.notifications.CloudNotificationsDataSource;
import com.alorma.github.sdk.core.notifications.Notification;
import com.alorma.github.sdk.core.notifications.NotificationsRequest;
import com.alorma.github.sdk.core.notifications.NotificationsRetrofitWrapper;
import com.alorma.github.sdk.core.repository.GenericRepository;
import com.alorma.github.sdk.core.usecase.GenericUseCase;
import java.util.List;
import java.util.concurrent.Callable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class NotificationsPresenter extends Presenter<NotificationsRequest, List<Notification>> {
  GenericUseCase<NotificationsRequest, List<Notification>> useCase;

  public NotificationsPresenter() {

  }

  @Override
  public void load(final NotificationsRequest request,
      final Callback<List<Notification>> listCallback) {

    RestWrapper wrapper = new NotificationsRetrofitWrapper(new Github(), request.getToken());

    CloudDataSource<NotificationsRequest, List<Notification>> cloud =
        new CloudNotificationsDataSource(wrapper);
    GenericRepository<NotificationsRequest, List<Notification>> repository =
        new GenericRepository<>(null, cloud);
    useCase = new GenericUseCase<>(repository);

    Observable.fromCallable(new Callable<List<Notification>>() {
      @Override
      public List<Notification> call() throws Exception {
        return useCase.execute(request);
      }
    })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(new Action0() {
          @Override
          public void call() {
            if (listCallback != null) {
              listCallback.showLoading();
            }
          }
        })
        .subscribe(new Action1<List<Notification>>() {
          @Override
          public void call(List<Notification> notifications) {
            if (listCallback != null) {
              listCallback.hideLoading();
              listCallback.onResponse(notifications);
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {

          }
        });
  }
}
