package com.alorma.github.presenter;

import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoStarredClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoWatchedClient;
import core.datasource.SdkItem;
import core.repositories.Branch;
import core.repositories.Repo;
import core.repository.GenericRepository;
import java.util.List;
import rx.Observable;
import rx.Scheduler;

public class RepositoryPresenter extends BaseRxPresenter<RepoInfo, Repo, View<Repo>> {

  private final GenericRepository<RepoInfo, Repo> repoGenericRepository;

  public RepositoryPresenter(Scheduler mainScheduler, Scheduler ioScheduler, GenericRepository<RepoInfo, Repo> repoGenericRepository) {
    super(mainScheduler, ioScheduler, null);
    this.repoGenericRepository = repoGenericRepository;
  }

  @Override
  public void execute(final RepoInfo repoInfo) {
    if (isViewAttached() && repoInfo != null) {

      Observable<Repo> repoObservable = repoGenericRepository.execute(new SdkItem<>(repoInfo)).map(SdkItem::getK);
      Observable<List<Branch>> branchesClient = new GetRepoBranchesClient(repoInfo).observable();
      Observable<Boolean> starredObservable = new CheckRepoStarredClient(repoInfo.owner, repoInfo.name).observable();
      Observable<Boolean> watchedObservable = new CheckRepoWatchedClient(repoInfo.owner, repoInfo.name).observable();

      Observable<Repo> observable =
          Observable.zip(repoObservable, branchesClient, starredObservable, watchedObservable, (repo, branches, starred, watched) -> {

            repo.branches = branches;
            if (repo.defaultBranch == null && branches != null && branches.size() > 0) {
              repo.defaultBranch = branches.get(0).name;
            }

            repo.setStarred(starred);
            repo.setWatched(starred);

            return repo;
          });

      observable = observable.doOnNext(repo -> repoGenericRepository.save(repoInfo, repo));
      Observable<SdkItem<Repo>> rObservable = observable.map(SdkItem::new);

      subscribe(rObservable, false);
    }
  }
}
