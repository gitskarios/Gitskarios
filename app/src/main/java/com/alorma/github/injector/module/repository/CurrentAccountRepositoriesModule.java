package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.repos.AuthUserReposCache;
import com.alorma.github.presenter.repos.AuthUserRepositoriesPresenter;

import core.repositories.CloudUserRepositoriesDataSource;
import core.repositories.RepositoriesRetrofitWrapper;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class CurrentAccountRepositoriesModule extends RepositoriesModule {

    @Provides
    @PerActivity
    AuthUserReposCache provideUserReposCacheDataSource() {
        return new AuthUserReposCache("auth_repos");
    }

    @Provides
    @PerActivity
    CloudUserRepositoriesDataSource provideCloudRepositoriesDataSource(
            RepositoriesRetrofitWrapper retrofitWrapper, @SortOrder String sortOrder) {
        return new CloudUserRepositoriesDataSource(retrofitWrapper, sortOrder);
    }

    @Provides
    @PerActivity
    AuthUserRepositoriesPresenter provideAuthUserRepositoriesPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
            AuthUserReposCache authUserReposCache,
            CloudUserRepositoriesDataSource dataSource) {
        return new AuthUserRepositoriesPresenter(
                mainScheduler, ioScheduler, new GenericRepository<>(authUserReposCache, dataSource));
    }
}
