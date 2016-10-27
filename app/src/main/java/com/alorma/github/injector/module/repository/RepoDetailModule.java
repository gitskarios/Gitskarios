package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.AbstractCacheDataSource;
import com.alorma.github.presenter.RepositoryPresenter;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import core.datasource.CacheDataSource;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.repositories.Repo;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.Scheduler;

@Module public class RepoDetailModule {

  @Provides
  @PerActivity
  CacheDataSource<RepoInfo, Repo> providesCache() {
    return new AbstractCacheDataSource<RepoInfo, Repo>() {
      @Override
      protected String getCacheKey(RepoInfo k, Integer page) {
        return k.toString();
      }
    };
  }

  @Provides
  @PerActivity
  CloudDataSource<RepoInfo, Repo> providesApi() {
    return new CloudDataSource<RepoInfo, Repo>(null) {
      @Override
      protected Observable<SdkItem<Repo>> execute(SdkItem<RepoInfo> request, RestWrapper service) {
        return new GetRepoClient(request.getK()).observable().map(SdkItem::new);
      }
    };
  }

  @Provides
  @PerActivity
  GenericRepository<RepoInfo, Repo> providesRepository(CacheDataSource<RepoInfo, Repo> cache, CloudDataSource<RepoInfo, Repo> api) {
    return new GenericRepository<>(cache, api);
  }

  @Provides
  @PerActivity
  RepositoryPresenter provideRepositoryPresenter(@MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
      GenericRepository<RepoInfo, Repo> repoGenericRepository) {
    return new RepositoryPresenter(mainScheduler, ioScheduler, repoGenericRepository);
  }
}
