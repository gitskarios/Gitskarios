package com.alorma.github.presenter.notifications;

import com.alorma.github.bean.NotificationsParent;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.Presenter;
import core.ApiClient;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.notifications.CloudNotificationsDataSource;
import core.notifications.Notification;
import core.notifications.NotificationsRequest;
import core.notifications.NotificationsRetrofitWrapper;
import core.repository.GenericRepository;
import core.usecase.GenericUseCase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@PerActivity public class NotificationsPresenter extends Presenter<NotificationsRequest, List<NotificationsParent>> {
  GenericUseCase<NotificationsRequest, List<Notification>> useCase;

  @Inject
  public NotificationsPresenter() {

  }

  @Override
  public void load(final NotificationsRequest request, final Callback<List<NotificationsParent>> listCallback) {

    RestWrapper wrapper = new NotificationsRetrofitWrapper(apiClient, request.getToken());

    CloudDataSource<NotificationsRequest, List<Notification>> cloud = new CloudNotificationsDataSource(wrapper);
    GenericRepository<NotificationsRequest, List<Notification>> repository = new GenericRepository<>(null, cloud);
    useCase = new GenericUseCase<>(repository);

    useCase.execute(new SdkItem<>(request)).map(SdkItem::getK).map(notifications -> {
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
    }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe(() -> {
      if (listCallback != null) {
        listCallback.showLoading();
      }
    }).subscribe(notifications -> {
      if (listCallback != null) {
        listCallback.hideLoading();
        listCallback.onResponse(notifications, true);
      }
    }, throwable -> {

    });
  }

  @Override
  public void loadMore(NotificationsRequest notificationsRequest, Callback<List<NotificationsParent>> listCallback) {

  }

  @Override
  protected GenericRepository<NotificationsRequest, List<NotificationsParent>> configRepository(RestWrapper restWrapper) {
    return null;
  }

  @Override
  protected RestWrapper getRest(ApiClient apiClient, String token) {
    return null;
  }

  @Override
  public void action(List<NotificationsParent> notificationsParents, Callback<List<NotificationsParent>> listCallback, boolean firstTime) {

  }
}
