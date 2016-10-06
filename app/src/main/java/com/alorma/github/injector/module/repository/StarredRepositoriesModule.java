package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.repos.AuthStarredRepositoriesPresenter;
import com.alorma.github.presenter.repos.AuthUserReposCache;

import core.repositories.CloudStarredRepositoriesDataSource;
import core.repositories.RepositoriesRetrofitWrapper;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class StarredRepositoriesModule extends RepositoriesModule {

    @Provides
    @PerActivity
    AuthUserReposCache provideUserReposCacheDataSource() {
        return new AuthUserReposCache("auth_starred");
    }

    @Provides
    @PerActivity
    CloudStarredRepositoriesDataSource provideCloudRepositoriesDataSource(
            RepositoriesRetrofitWrapper retrofitWrapper, @SortOrder String sortOrder) {
        return new CloudStarredRepositoriesDataSource(retrofitWrapper, sortOrder);
    }

    @Provides
    @PerActivity
    AuthStarredRepositoriesPresenter provideAuthStarredRepositoriesPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
            AuthUserReposCache authUserReposCache,
            CloudStarredRepositoriesDataSource dataSource) {
        return new AuthStarredRepositoriesPresenter(
                mainScheduler, ioScheduler, new GenericRepository<>(authUserReposCache, dataSource));
    }
}
