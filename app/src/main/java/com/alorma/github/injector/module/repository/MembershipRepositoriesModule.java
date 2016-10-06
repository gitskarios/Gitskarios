package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.injector.named.Token;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.repos.AuthMembershipRepositoriesPresenter;
import com.alorma.github.presenter.repos.AuthUserReposCache;

import core.repositories.CloudMembershipRepositoriesDataSource;
import core.repositories.RepositoriesRetrofitWrapper;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class MembershipRepositoriesModule extends RepositoriesModule {

    @Provides
    @PerActivity
    AuthUserReposCache provideAuthUserReposCache(){
        return new AuthUserReposCache("auth_membership");
    }

    @Provides
    @PerActivity
    CloudMembershipRepositoriesDataSource provideCloudMembershipRepositoriesDataSource(
            RepositoriesRetrofitWrapper repositoriesRetrofitWrapper,
            @SortOrder String sortOrder) {
        return new CloudMembershipRepositoriesDataSource(repositoriesRetrofitWrapper, sortOrder);
    }

    @Provides
    @PerActivity
    AuthMembershipRepositoriesPresenter provideAuthMembershipRepositoriesPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
            AuthUserReposCache authUserReposCache,
            CloudMembershipRepositoriesDataSource cloudMembershipRepositoriesDataSource) {

        return new AuthMembershipRepositoriesPresenter(
                mainScheduler, ioScheduler,
                new GenericRepository<>(authUserReposCache, cloudMembershipRepositoriesDataSource));
    }

}
