package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.repos.AuthOrgsRepositoriesPresenter;
import com.alorma.github.presenter.repos.AuthUserReposCache;

import core.repositories.CloudOrgsRepositoriesDataSource;
import core.repositories.RepositoriesRetrofitWrapper;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class AuthOrgsRepositoriesModule extends RepositoriesModule {

    @Provides
    @PerActivity
    AuthUserReposCache provideUserReposCacheDataSource() {
        return new AuthUserReposCache("auth_orgs_repos");
    }

    @Provides
    @PerActivity
    CloudOrgsRepositoriesDataSource provideCloudRepositoriesDataSource(
            RepositoriesRetrofitWrapper retrofitWrapper, @SortOrder String sortOrder) {
        return new CloudOrgsRepositoriesDataSource(retrofitWrapper, sortOrder);
    }

    @Provides
    @PerActivity
    AuthOrgsRepositoriesPresenter provideAuthOrgsRepositoriesPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
            AuthUserReposCache authUserReposCache,
            CloudOrgsRepositoriesDataSource dataSource) {
        return new AuthOrgsRepositoriesPresenter(
                mainScheduler, ioScheduler,
                new GenericRepository<>(authUserReposCache, dataSource));
    }
}
