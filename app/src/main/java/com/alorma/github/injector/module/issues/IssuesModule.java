package com.alorma.github.injector.module.issues;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.Token;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.issue.IssuesPresenter;
import core.ApiClient;
import core.datasource.EmptyCacheDataSource;
import core.issues.CloudIssuesDataSource;
import core.issues.IssuesRetrofitWrapper;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module public class IssuesModule {

  @Provides
  @PerActivity
  IssuesRetrofitWrapper provideIssuesRetrofitWrapper(ApiClient apiClient, @Token String token) {
    return new IssuesRetrofitWrapper(apiClient, token);
  }

  @Provides
  @PerActivity
  CloudIssuesDataSource provideCloudUsersIssuesDataSource(IssuesRetrofitWrapper retrofitWrapper) {
    return new CloudIssuesDataSource(retrofitWrapper);
  }

  @Provides
  @PerActivity
  IssuesPresenter provideIssuesPresenter(@MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
      CloudIssuesDataSource cloudUsersIssuesDataSource) {

    return new IssuesPresenter(mainScheduler, ioScheduler,
        new GenericRepository<>(new EmptyCacheDataSource<>(), cloudUsersIssuesDataSource));
  }
}
