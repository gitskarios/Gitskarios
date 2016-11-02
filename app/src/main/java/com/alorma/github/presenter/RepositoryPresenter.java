package com.alorma.github.presenter;

import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.actions.CheckRepoStarredClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoWatchedClient;
import core.datasource.SdkItem;
import core.repositories.Branch;
import core.repositories.Commit;
import core.repositories.Repo;
import core.repository.ChangeRepositoryStarRepository;
import core.repository.ChangeRepositoryWatchRepository;
import core.repository.GenericRepository;
import java.util.List;
import rx.Observable;
import rx.Scheduler;

public class RepositoryPresenter extends BaseRxPresenter<RepoInfo, Repo, View<Repo>> {

  private final GenericRepository<RepoInfo, Repo> repoGenericRepository;
  private final GenericRepository<RepoInfo, List<Branch>> branchesGenericRepository;
  private final GenericRepository<CommitInfo, Commit> commitGenericRepository;
  private final ChangeRepositoryStarRepository changeRepositoryStarRepository;
  private final ChangeRepositoryWatchRepository changeRepositoryWatchRepository;
  private Repo currentRepo;

  public RepositoryPresenter(Scheduler mainScheduler, Scheduler ioScheduler, GenericRepository<RepoInfo, Repo> repoGenericRepository,
      GenericRepository<RepoInfo, List<Branch>> branchesGenericRepository, GenericRepository<CommitInfo, Commit> commitGenericRepository,
      ChangeRepositoryStarRepository changeRepositoryStarRepository, ChangeRepositoryWatchRepository changeRepositoryWatchRepository) {
    super(mainScheduler, ioScheduler, null);
    this.repoGenericRepository = repoGenericRepository;
    this.branchesGenericRepository = branchesGenericRepository;
    this.commitGenericRepository = commitGenericRepository;
    this.changeRepositoryStarRepository = changeRepositoryStarRepository;
    this.changeRepositoryWatchRepository = changeRepositoryWatchRepository;
  }

  @Override
  public void execute(final RepoInfo repoInfo) {
    if (isViewAttached() && repoInfo != null) {

      getView().showLoading();

      SdkItem<RepoInfo> repoInfoSdkItem = new SdkItem<>(repoInfo);

      Observable<Repo> repoObservable = repoGenericRepository.execute(repoInfoSdkItem).map(SdkItem::getK);
      Observable<Boolean> starredObservable = new CheckRepoStarredClient(repoInfo.owner, repoInfo.name).observable();
      Observable<Boolean> watchedObservable = new CheckRepoWatchedClient(repoInfo.owner, repoInfo.name).observable();

      Observable<Repo> observable = Observable.zip(repoObservable, starredObservable, watchedObservable, (repo, starred, watched) -> {
        repo.setStarred(starred);
        repo.setWatched(watched);
        return repo;
      });

      Observable<List<Branch>> branchObservable =
          branchesGenericRepository.execute(repoInfoSdkItem).map(SdkItem::getK).flatMap(Observable::from).flatMap((branch) -> {
            CommitInfo info = new CommitInfo();
            info.repoInfo = repoInfo;
            info.sha = branch.commit.sha;
            return commitGenericRepository.execute(new SdkItem<>(info)).map(SdkItem::getK);
          }, (branch, commit) -> {
            if (branch.commit.sha.equals(commit.sha)) {
              branch.commit = commit;
            }
            return branch;
          }).toList();

      observable = Observable.zip(observable, branchObservable, (repo, branches) -> {
        repo.setBranches(branches);
        if (branches != null && branches.size() > 0) {
          if (repo.defaultBranch == null) {
            repo.defaultBranch = branches.get(0).name;
            repo.defaultBranchObject = branches.get(0);
          } else {
            for (Branch branch : branches) {
              if (repo.defaultBranch.equals(branch.name)) {
                repo.defaultBranchObject = branch;
              }
            }
          }
        }

        return repo;
      });

      observable = observable.doOnNext(repo -> repoGenericRepository.save(repoInfo, repo));

      subscribe(observable.map(SdkItem::new), false);
    }
  }

  public void toggleStar() {
    if (currentRepo != null) {
      Boolean isStarred = currentRepo.isStarred();
      Observable<Boolean> observable;

      String owner = currentRepo.getOwner().getLogin();
      String name = currentRepo.getName();

      if (isStarred != null && isStarred) {
        observable = changeRepositoryStarRepository.unstar(owner, name);
      } else {
        observable = changeRepositoryStarRepository.star(owner, name);
      }

      RepoInfo repoInfo = new RepoInfo();
      repoInfo.owner = owner;
      repoInfo.name = name;

      Observable<Repo> repoObservable = repoGenericRepository.execute(new SdkItem<>(repoInfo), true).map(SdkItem::getK);
      Observable<Repo> resultObservable = observable.flatMap(aBoolean -> repoObservable, (starred, repo) -> {
        repo.setStarred(starred);
        return repo;
      });

      subscribe(resultObservable.map(SdkItem::new), false);
    }
  }

  public void toggleWatch() {
    if (currentRepo != null) {
      Boolean isWatched = currentRepo.isWatched();
      Observable<Boolean> observable;

      String owner = currentRepo.getOwner().getLogin();
      String name = currentRepo.getName();

      if (isWatched != null && isWatched) {
        observable = changeRepositoryWatchRepository.unwatch(owner, name);
      } else {
        observable = changeRepositoryWatchRepository.watch(owner, name);
      }

      RepoInfo repoInfo = new RepoInfo();
      repoInfo.owner = owner;
      repoInfo.name = name;

      Observable<Repo> repoObservable = repoGenericRepository.execute(new SdkItem<>(repoInfo), true).map(SdkItem::getK);
      Observable<Repo> resultObservable = observable.flatMap(aBoolean -> repoObservable, (watched, repo) -> {
        repo.setWatched(watched);
        return repo;
      });

      subscribe(resultObservable.map(SdkItem::new), false);
    }
  }

  @Override
  protected void subscribe(Observable<SdkItem<Repo>> observable, boolean isFromPaginated) {
    observable = observable.map(SdkItem::getK).doOnNext(repo -> this.currentRepo = repo).map(SdkItem::new);
    super.subscribe(observable, isFromPaginated);
  }
}
