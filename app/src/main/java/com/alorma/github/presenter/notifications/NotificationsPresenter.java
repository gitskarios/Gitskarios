package com.alorma.github.presenter.notifications;

import com.alorma.github.bean.NotificationsParent;
import com.alorma.github.presenter.BaseRxPresenter;
import com.alorma.github.presenter.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.datasource.SdkItem;
import core.notifications.Notification;
import core.notifications.NotificationsRequest;
import core.repository.GenericRepository;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NotificationsPresenter
        extends BaseRxPresenter<NotificationsRequest, List<NotificationsParent>, View<List<NotificationsParent>>> {

  private GenericRepository<NotificationsRequest, List<Notification>> repository;

  public NotificationsPresenter(
          Scheduler mainScheduler, Scheduler ioScheduler,
          GenericRepository<NotificationsRequest, List<Notification>> repository) {
    super(mainScheduler, ioScheduler, null);
    this.repository = repository;
  }

  @Override
  public void execute(final NotificationsRequest request) {
    if(!isViewAttached()) return;

    repository.execute(new SdkItem<>(request)).map(SdkItem::getK).map(notifications -> {
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
    }).subscribeOn(ioScheduler).observeOn(mainScheduler).doOnSubscribe(() -> {
      if (isViewAttached()) {
        getView().showLoading();
      }
    }).subscribe(notifications -> {
      if (isViewAttached()) {
        getView().hideLoading();
        getView().onDataReceived(notifications, false);
      }
    }, throwable -> {
      if (isViewAttached()) {
        getView().showError(throwable);
      }
    });
  }
}
