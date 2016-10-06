package com.alorma.github.injector.module;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.Token;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.notifications.NotificationsPresenter;

import java.util.List;

import core.ApiClient;
import core.notifications.CloudNotificationsDataSource;
import core.notifications.Notification;
import core.notifications.NotificationsRequest;
import core.notifications.NotificationsRetrofitWrapper;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class UserNotificationsModule {

    @Provides
    @PerActivity
    NotificationsRetrofitWrapper provideNotificationsRetrofitWrapper(
            ApiClient apiClient, @Token String token) {
        return new NotificationsRetrofitWrapper(apiClient, token);
    }

    @Provides
    @PerActivity
    CloudNotificationsDataSource provideCloudNotificationsDataSource(
            NotificationsRetrofitWrapper retrofitWrapper) {
        return new CloudNotificationsDataSource(retrofitWrapper);
    }

    @Provides
    @PerActivity
    NotificationsPresenter provideNotificationsPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
            CloudNotificationsDataSource dataSource) {
        GenericRepository<NotificationsRequest, List<Notification>> repository =
                new GenericRepository<>(null, dataSource);
        return new NotificationsPresenter(mainScheduler, ioScheduler, repository);
    }
}
