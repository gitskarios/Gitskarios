package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.Token;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.RepositoryReadmePresenter;
import com.alorma.github.presenter.repos.ReadmeCacheDataSource;
import com.alorma.github.sdk.bean.ReadmeInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import core.ApiClient;
import core.repositories.markdown.MarkdownCloudDataSource;
import core.repositories.markdown.MarkdownRetrofitWrapper;
import core.repositories.readme.ReadmeCloudDataSource;
import core.repositories.readme.ReadmeRetrofitWrapper;
import core.repositories.readme.RepoReadmeRepository;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module public class RepositoryReadmeModule {

  @Provides
  @PerActivity
  ReadmeCacheDataSource provideCacheDatasource() {
    return new ReadmeCacheDataSource();
  }

  @Provides
  @PerActivity
  ReadmeRetrofitWrapper provideReadmeRetrofitWrapper(ApiClient apiClient, @Token String token) {
    return new ReadmeRetrofitWrapper(apiClient, token);
  }

  @Provides
  @PerActivity
  ReadmeCloudDataSource provideReadmeCloudDataSource(ReadmeRetrofitWrapper tagsRetrofitWrapper) {
    return new ReadmeCloudDataSource(tagsRetrofitWrapper);
  }

  @Provides
  @PerActivity
  MarkdownRetrofitWrapper provideMarkdownRetrofitWrapper(ApiClient apiClient, @Token String token) {
    return new MarkdownRetrofitWrapper(apiClient, token);
  }

  @Provides
  @PerActivity
  MarkdownCloudDataSource providesMarkdownDatasource(MarkdownRetrofitWrapper restWrapper) {
    return new MarkdownCloudDataSource(restWrapper);
  }

  @Provides
  @PerActivity
  GenericRepository<ReadmeInfo, String> providesRepository(ReadmeCacheDataSource readmeCacheDataSource,
      ReadmeCloudDataSource readmeCloudDataSource, MarkdownCloudDataSource markdownCloudDataSource) {
    return new RepoReadmeRepository(readmeCacheDataSource, readmeCloudDataSource, markdownCloudDataSource);
  }

  @Provides
  @PerActivity
  RepositoryReadmePresenter provideRepositoryTagsPresenter(@MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
      GenericRepository<ReadmeInfo, String> repository) {
    return new RepositoryReadmePresenter(mainScheduler, ioScheduler, repository);
  }
}
