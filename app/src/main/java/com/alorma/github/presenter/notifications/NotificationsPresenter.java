package com.alorma.github.presenter.notifications;

import com.alorma.github.bean.NotificationsParent;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NotificationsPresenter extends Presenter<NotificationsRequest, List<NotificationsParent>> {
  GenericUseCase<NotificationsRequest, List<Notification>> useCase;

  public NotificationsPresenter() {

  }

  @Override
  public void load(final NotificationsRequest request,
      final Callback<List<NotificationsParent>> listCallback) {

    RestWrapper wrapper = new NotificationsRetrofitWrapper(new Github(), request.getToken());

    CloudDataSource<NotificationsRequest, List<Notification>> cloud =
        new CloudNotificationsDataSource(wrapper);
    GenericRepository<NotificationsRequest, List<Notification>> repository =
        new GenericRepository<>(null, cloud);
    useCase = new GenericUseCase<>(repository);

    Observable.fromCallable(() -> useCase.execute(request))
        .map((Func1<List<Notification>, List<NotificationsParent>>) notifications -> {
          Map<Long, NotificationsParent> parents = new HashMap<>();

          for (Notification notification : notifications) {
            if (parents.get(notification.repository.getId()) == null) {
              NotificationsParent notificationsParent = new NotificationsParent();
              parents.put(notification.repository.getId(), notificationsParent);
              notificationsParent.repo = notification.repository;
              notificationsParent.notifications = new ArrayList<>();
            }
            parents.get(notification.repository.getId()).notifications.add(notification);
          }

          Collection<NotificationsParent> values = parents.values();
          ArrayList<NotificationsParent> notificationsParents = new ArrayList<>(values);
          Collections.reverse(notificationsParents);


          return notificationsParents;
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
        .subscribe(new Action1<List<NotificationsParent>>() {
          @Override
          public void call(List<NotificationsParent> notifications) {
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
