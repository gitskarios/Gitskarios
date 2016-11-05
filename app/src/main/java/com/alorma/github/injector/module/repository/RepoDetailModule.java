package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.AbstractCacheDataSource;
import com.alorma.github.presenter.repo.GetRepositoryUseCase;
import com.alorma.github.presenter.repo.GetRepositoryUseCaseImpl;
import com.alorma.github.presenter.repo.RepositoryPresenter;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.commit.GetSingleCommitClient;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import core.datasource.CacheDataSource;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.repositories.Branch;
import core.repositories.Commit;
import core.repositories.Repo;
import core.repository.ChangeRepositoryStarUseCase;
import core.repository.ChangeRepositoryWatchUseCase;
import core.repository.GenericRepository;
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
  ChangeRepositoryStarUseCase provideChangeRepositoryStarRepository() {
    return new ChangeRepositoryStarUseCase();
  }

  @Provides
  @PerActivity
  ChangeRepositoryWatchUseCase provideChangeRepositoryWatchRepository() {
    return new ChangeRepositoryWatchUseCase();
  }

  @Provides
  @PerActivity
  GetRepositoryUseCase providesGetRepositoryUseCase(GenericRepository<RepoInfo, Repo> repoGenericRepository,
      GenericRepository<RepoInfo, List<Branch>> branchesGenericRepository, GenericRepository<CommitInfo, Commit> commitGenericRepository) {
    return new GetRepositoryUseCaseImpl(repoGenericRepository, branchesGenericRepository, commitGenericRepository);
  }

  @Provides
  @PerActivity
  RepositoryPresenter provideRepositoryPresenter(@MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
      ChangeRepositoryStarUseCase changeRepositoryStarUseCase, ChangeRepositoryWatchUseCase changeRepositoryWatchUseCase,
      GetRepositoryUseCase getRepositoryUseCase) {
    return new RepositoryPresenter(mainScheduler, ioScheduler, getRepositoryUseCase, changeRepositoryStarUseCase,
        changeRepositoryWatchUseCase);
  }
}
