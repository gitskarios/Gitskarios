package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.repos.UserReposCache;
import com.alorma.github.presenter.repos.UserRepositoriesPresenter;

import core.repositories.CloudUserRepositoriesDataSource;
import core.repositories.RepositoriesRetrofitWrapper;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class UsernameRepositoriesModule extends RepositoriesModule {

    @Provides
    @PerActivity
    UserReposCache provideUserReposCacheDataSource() {
        return new UserReposCache("user_repos");
    }

    @Provides
    @PerActivity
    CloudUserRepositoriesDataSource provideCloudRepositoriesDataSource(
            RepositoriesRetrofitWrapper retrofitWrapper, @SortOrder String sortOrder) {
        return new CloudUserRepositoriesDataSource(retrofitWrapper, sortOrder);
    }

    @Provides
    @PerActivity
    UserRepositoriesPresenter provideUserRepositoriesPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
            UserReposCache userReposCache, CloudUserRepositoriesDataSource dataSource) {

        return new UserRepositoriesPresenter(
                mainScheduler, ioScheduler, new GenericRepository<>(userReposCache, dataSource));
    }
}
