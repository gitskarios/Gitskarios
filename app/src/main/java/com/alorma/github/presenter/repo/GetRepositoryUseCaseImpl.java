package com.alorma.github.presenter.repo;

import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.actions.CheckRepoStarredClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoWatchedClient;
import core.datasource.SdkItem;
import core.repositories.Branch;
import core.repositories.Commit;
import core.repositories.Repo;
import core.repository.GenericRepository;
import java.util.List;
import rx.Observable;

public class GetRepositoryUseCaseImpl implements GetRepositoryUseCase {
  private final GenericRepository<RepoInfo, Repo> repoGenericRepository;
  private final GenericRepository<RepoInfo, List<Branch>> branchesGenericRepository;
  private final GenericRepository<CommitInfo, Commit> commitGenericRepository;

  public GetRepositoryUseCaseImpl(GenericRepository<RepoInfo, Repo> repoGenericRepository,
      GenericRepository<RepoInfo, List<Branch>> branchesGenericRepository, GenericRepository<CommitInfo, Commit> commitGenericRepository) {

    this.repoGenericRepository = repoGenericRepository;
    this.branchesGenericRepository = branchesGenericRepository;
    this.commitGenericRepository = commitGenericRepository;
  }

  @Override
  public Observable<SdkItem<Repo>> getRepository(RepoInfo repoInfo) {
    return getRepository(repoInfo, false);
  }

  @Override
  public Observable<SdkItem<Repo>> getRepository(RepoInfo repoInfo, boolean refresh) {
    SdkItem<RepoInfo> repoInfoSdkItem = new SdkItem<>(repoInfo);
    Observable<Repo> repoObservable = repoGenericRepository.execute(repoInfoSdkItem, refresh).map(SdkItem::getK);

    // TODO move star & watch to Repository pattern

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
    return observable.map(SdkItem::new);
  }
}
