package com.alorma.github.presenter.repo;

import com.alorma.github.presenter.BaseRxPresenter;
import com.alorma.github.presenter.View;
import com.alorma.github.sdk.bean.info.RepoInfo;
import core.datasource.SdkItem;
import core.repositories.Repo;
import core.repository.ChangeRepositoryStarUseCase;
import core.repository.ChangeRepositoryWatchUseCase;
import rx.Observable;
import rx.Scheduler;

public class RepositoryPresenter extends BaseRxPresenter<RepoInfo, Repo, View<Repo>> {

  private final GetRepositoryUseCase getRepositoryUseCase;
  private final ChangeRepositoryStarUseCase changeRepositoryStarUseCase;
  private final ChangeRepositoryWatchUseCase changeRepositoryWatchUseCase;
  private Repo currentRepo;

  public RepositoryPresenter(Scheduler mainScheduler, Scheduler ioScheduler, GetRepositoryUseCase getRepositoryUseCase,
      ChangeRepositoryStarUseCase changeRepositoryStarUseCase, ChangeRepositoryWatchUseCase changeRepositoryWatchUseCase) {
    super(mainScheduler, ioScheduler, null);
    this.getRepositoryUseCase = getRepositoryUseCase;
    this.changeRepositoryStarUseCase = changeRepositoryStarUseCase;
    this.changeRepositoryWatchUseCase = changeRepositoryWatchUseCase;
  }

  @Override
  public void execute(final RepoInfo repoInfo) {
    if (isViewAttached() && repoInfo != null) {

      getView().showLoading();

      subscribe(getRepositoryUseCase.getRepository(repoInfo), false);
    }
  }

  public void toggleStar() {
    Observable<SdkItem<Repo>> resultObservable = Observable.defer(() -> {
      if (currentRepo != null) {
        Boolean isStarred = currentRepo.isStarred();

        String owner = currentRepo.getOwner().getLogin();
        String name = currentRepo.getName();

        if (isStarred != null && isStarred) {
          return changeRepositoryStarUseCase.unstar(owner, name);
        } else {
          return changeRepositoryStarUseCase.star(owner, name);
        }
      }
      return Observable.empty();
    }).flatMap(aBoolean -> {
      RepoInfo repoInfo = new RepoInfo();
      repoInfo.owner = currentRepo.getOwner().getLogin();
      repoInfo.name = currentRepo.getName();
      return getRepositoryUseCase.getRepository(repoInfo, true);
    });
    subscribe(resultObservable, false);
  }

  public void toggleWatch() {
    Observable<SdkItem<Repo>> resultObservable = Observable.defer(() -> {
      if (currentRepo != null) {
        Boolean isWatched = currentRepo.isWatched();

        String owner = currentRepo.getOwner().getLogin();
        String name = currentRepo.getName();

        if (isWatched != null && isWatched) {
          return changeRepositoryWatchUseCase.unwatch(owner, name);
        } else {
          return changeRepositoryWatchUseCase.watch(owner, name);
        }
      }
      return Observable.empty();
    }).flatMap(aBoolean -> {
      RepoInfo repoInfo = new RepoInfo();
      repoInfo.owner = currentRepo.getOwner().getLogin();
      repoInfo.name = currentRepo.getName();
      return getRepositoryUseCase.getRepository(repoInfo, true);
    });

    subscribe(resultObservable, false);
  }

  @Override
  protected void subscribe(Observable<SdkItem<Repo>> observable, boolean isFromPaginated) {
    observable = observable.map(SdkItem::getK).doOnNext(repo -> this.currentRepo = repo).map(SdkItem::new);
    super.subscribe(observable, isFromPaginated);
  }
}
