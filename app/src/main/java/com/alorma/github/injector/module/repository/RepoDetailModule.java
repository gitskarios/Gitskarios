package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.Starred;
import com.alorma.github.injector.named.StarredNegative;
import com.alorma.github.injector.named.StarredPositive;
import com.alorma.github.injector.named.Token;
import com.alorma.github.injector.named.Watched;
import com.alorma.github.injector.named.WatchedNegative;
import com.alorma.github.injector.named.WatchedPositive;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.AbstractCacheDataSource;
import com.alorma.github.presenter.repo.GetRepositoryUseCase;
import com.alorma.github.presenter.repo.GetRepositoryUseCaseImpl;
import com.alorma.github.presenter.repo.RepositoryPresenter;
import com.alorma.github.presenter.repo.tags.GetTagsCountUseCase;
import com.alorma.github.presenter.repo.tags.GetTagsCountUseCaseImpl;
import com.alorma.github.presenter.repo.tags.TagsCountDatasource;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.commit.GetSingleCommitClient;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import core.ApiClient;
import core.datasource.CacheDataSource;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.repositories.Branch;
import core.repositories.Commit;
import core.repositories.Repo;
import core.repositories.releases.tags.TagsRetrofitWrapper;
import core.repository.ActionRepository;
import core.repository.CheckStarRepository;
import core.repository.GenericRepository;
import core.repository.GetRepositoryWatchRepository;
import core.repository.RepositoryRetrofitWrapper;
import dagger.Module;
import dagger.Provides;
import java.util.List;
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
  CloudDataSource<RepoInfo, List<Branch>> providesBranchesApi() {
    return new CloudDataSource<RepoInfo, List<Branch>>(null) {
      @Override
      protected Observable<SdkItem<List<Branch>>> execute(SdkItem<RepoInfo> request, RestWrapper service) {
        return new GetRepoBranchesClient(request.getK()).observable().map(SdkItem::new);
      }
    };
  }

  @Provides
  @PerActivity
  CloudDataSource<CommitInfo, core.repositories.Commit> providesCommitApi() {
    return new CloudDataSource<CommitInfo, Commit>(null) {
      @Override
      protected Observable<SdkItem<Commit>> execute(SdkItem<CommitInfo> request, RestWrapper service) {
        return new GetSingleCommitClient(request.getK()).observable().map(SdkItem::new);
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
  GenericRepository<RepoInfo, List<Branch>> providesBranchesRepository(CloudDataSource<RepoInfo, List<Branch>> api) {
    return new GenericRepository<>(null, api);
  }

  @Provides
  @PerActivity
  GenericRepository<CommitInfo, Commit> providesCommitRepository(CloudDataSource<CommitInfo, Commit> api) {
    return new GenericRepository<>(null, api);
  }

  @Provides
  @PerActivity
  @Starred
  CloudDataSource<RepoInfo, Boolean> getStarredDataSource(RepositoryRetrofitWrapper repositoryRetrofitWrapper) {
    return new RepoCheckStarDatasource(repositoryRetrofitWrapper);
  }

  @Provides
  @PerActivity
  @StarredPositive
  CloudDataSource<RepoInfo, Boolean> getStarDataSource(RepositoryRetrofitWrapper repositoryRetrofitWrapper) {
    return new RepoActionStarDatasource(repositoryRetrofitWrapper);
  }

  @Provides
  @PerActivity
  @StarredNegative
  CloudDataSource<RepoInfo, Boolean> getUnStarDataSource(RepositoryRetrofitWrapper repositoryRetrofitWrapper) {
    return new RepoActionUnStarDatasource(repositoryRetrofitWrapper);
  }

  @Provides
  @PerActivity
  CheckStarRepository getGetRepositoryStar(@Starred CloudDataSource<RepoInfo, Boolean> dataSource) {
    return new CheckStarRepository(dataSource);
  }

  @Provides
  @PerActivity
  RepositoryRetrofitWrapper getRepoWrapper(ApiClient apiClient, @Token String token) {
    return new RepositoryRetrofitWrapper(apiClient, token);
  }

  @Provides
  @PerActivity
  @Watched
  CloudDataSource<RepoInfo, Boolean> getWatchedDataSource(RepositoryRetrofitWrapper repositoryRetrofitWrapper) {
    return new RepoCheckWatchDatasource(repositoryRetrofitWrapper);
  }

  @Provides
  @PerActivity
  @WatchedPositive
  CloudDataSource<RepoInfo, Boolean> getWatchDataSource(RepositoryRetrofitWrapper repositoryRetrofitWrapper) {
    return new RepoActionWatchDatasource(repositoryRetrofitWrapper);
  }

  @Provides
  @PerActivity
  @WatchedNegative
  CloudDataSource<RepoInfo, Boolean> getUnWatchDataSource(RepositoryRetrofitWrapper repositoryRetrofitWrapper) {
    return new RepoActionUnWatchDatasource(repositoryRetrofitWrapper);
  }

  @Provides
  @PerActivity
  GetRepositoryWatchRepository getGetRepositoryWatch(@Watched CloudDataSource<RepoInfo, Boolean> dataSource) {
    return new GetRepositoryWatchRepository(dataSource);
  }

  @Provides
  @PerActivity
  @Starred
  ActionRepository provideChangeRepositoryStarRepository(@StarredPositive CloudDataSource<RepoInfo, Boolean> positive,
      @StarredNegative CloudDataSource<RepoInfo, Boolean> negative) {
    return new ActionRepository(positive, negative);
  }

  @Provides
  @PerActivity
  @Watched
  ActionRepository provideChangeRepositoryWatchRepository(@WatchedPositive CloudDataSource<RepoInfo, Boolean> positive,
      @WatchedNegative CloudDataSource<RepoInfo, Boolean> negative) {
    return new ActionRepository(positive, negative);
  }

  @Provides
  @PerActivity
  GetRepositoryUseCase providesGetRepositoryUseCase(GenericRepository<RepoInfo, Repo> repoGenericRepository,
      GenericRepository<RepoInfo, List<Branch>> branchesGenericRepository, GenericRepository<CommitInfo, Commit> commitGenericRepository,
      CheckStarRepository getStarUseCase, GetRepositoryWatchRepository getWatchUseCase) {
    return new GetRepositoryUseCaseImpl(repoGenericRepository, branchesGenericRepository, commitGenericRepository, getStarUseCase,
        getWatchUseCase);
  }

  @Provides
  @PerActivity
  TagsRetrofitWrapper provideTagsRetrofitWrapper(ApiClient apiClient, @Token String token) {
    return new TagsRetrofitWrapper(apiClient, token);
  }

  @Provides
  @PerActivity
  CloudDataSource<RepoInfo, Integer> providesTagsCountDatasource(TagsRetrofitWrapper tagsRetrofitWrapper) {
    return new TagsCountDatasource(tagsRetrofitWrapper);
  }

  @Provides
  @PerActivity
  GenericRepository<RepoInfo, Integer> providesTagsCountRepository(CloudDataSource<RepoInfo, Integer> api) {
    return new GenericRepository<>(null, api);
  }

  @Provides
  @PerActivity
  GetTagsCountUseCase providesGetTagsCount(GenericRepository<RepoInfo, Integer> genericRepository) {
    return new GetTagsCountUseCaseImpl(genericRepository);
  }

  @Provides
  @PerActivity
  RepositoryPresenter provideRepositoryPresenter(@MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
      @Starred ActionRepository starRepository, @Watched ActionRepository watchRepository,
      GetRepositoryUseCase getRepositoryUseCase, GetTagsCountUseCase getTagsCountUseCase) {
    return new RepositoryPresenter(mainScheduler, ioScheduler, getRepositoryUseCase, starRepository,
        watchRepository, getTagsCountUseCase);
  }
}
