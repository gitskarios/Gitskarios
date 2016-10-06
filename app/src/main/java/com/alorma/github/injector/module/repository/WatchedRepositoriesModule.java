package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.repos.AuthUserReposCache;
import com.alorma.github.presenter.repos.AuthWatchedRepositoriesPresenter;

import core.repositories.CloudWatchedRepositoriesDataSource;
import core.repositories.RepositoriesRetrofitWrapper;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class WatchedRepositoriesModule extends RepositoriesModule {

    @Provides
    @PerActivity
    AuthUserReposCache provideUserReposCacheDataSource() {
        return new AuthUserReposCache("auth_watched");
    }

    @Provides
    @PerActivity
    CloudWatchedRepositoriesDataSource provideCloudRepositoriesDataSource(
            RepositoriesRetrofitWrapper retrofitWrapper, @SortOrder String sortOrder) {
        return new CloudWatchedRepositoriesDataSource(retrofitWrapper, sortOrder);
    }

    @Provides
    @PerActivity
    AuthWatchedRepositoriesPresenter provideAuthWatchedRepositoriesPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
            AuthUserReposCache authUserReposCache,
            CloudWatchedRepositoriesDataSource dataSource) {
        return new AuthWatchedRepositoriesPresenter(
                mainScheduler, ioScheduler, new GenericRepository<>(authUserReposCache, dataSource));
    }
}
