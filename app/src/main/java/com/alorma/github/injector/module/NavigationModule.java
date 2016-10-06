package com.alorma.github.injector.module;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.Token;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.NavigationProfilesPresenter;

import java.util.List;

import core.ApiClient;
import core.User;
import core.datasource.EmptyCacheDataSource;
import core.orgs.OrganizationsDataSource;
import core.orgs.OrganizationsRetrofitWrapper;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class NavigationModule {

    @Provides
    @PerActivity
    OrganizationsRetrofitWrapper provideRetrofitWrapper(ApiClient apiClient, @Token String token) {
        return new OrganizationsRetrofitWrapper(apiClient, token);
    }

    @Provides
    @PerActivity
    OrganizationsDataSource provideCloudOrgsDataSource(
            OrganizationsRetrofitWrapper retrofitWrapper) {
        return new OrganizationsDataSource(retrofitWrapper);
    }

    @Provides
    @PerActivity
    NavigationProfilesPresenter provideNavigationProfilesPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
            OrganizationsDataSource dataSource) {
        GenericRepository<String, List<User>> repository =
                new GenericRepository<>(new EmptyCacheDataSource<>(), dataSource);
        return new NavigationProfilesPresenter(mainScheduler, ioScheduler, repository);
    }
}
