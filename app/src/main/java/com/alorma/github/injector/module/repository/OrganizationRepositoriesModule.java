package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.repos.OrganizationRepositoriesPresenter;
import com.alorma.github.presenter.repos.UserReposCache;

import core.repositories.CloudOrganizationRepositoriesDataSource;
import core.repositories.RepositoriesRetrofitWrapper;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class OrganizationRepositoriesModule extends RepositoriesModule {

    @Provides
    @PerActivity
    UserReposCache provideUserReposCache(){
        return new UserReposCache("org_repos");
    }

    @Provides
    @PerActivity
    CloudOrganizationRepositoriesDataSource provideCloudOrganizationRepositoriesDataSource(
            RepositoriesRetrofitWrapper retrofitWrapper,
            @SortOrder String sortOrder) {
        return new CloudOrganizationRepositoriesDataSource(retrofitWrapper, sortOrder);
    }

    @Provides
    @PerActivity
    OrganizationRepositoriesPresenter provideOrganizationRepositoriesPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
            UserReposCache userReposCache,
            CloudOrganizationRepositoriesDataSource dataSource) {

        return new OrganizationRepositoriesPresenter(
                mainScheduler, ioScheduler, new GenericRepository<>(userReposCache, dataSource));
    }
}
