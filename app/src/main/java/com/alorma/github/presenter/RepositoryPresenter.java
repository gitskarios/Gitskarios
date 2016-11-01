package com.alorma.github.presenter;

import android.support.annotation.NonNull;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoStarredClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoWatchedClient;
import com.alorma.github.ui.activity.repo.RepoItem;
import core.User;
import core.datasource.SdkItem;
import core.repositories.Branch;
import core.repositories.Repo;
import core.repository.GenericRepository;
import java.util.ArrayList;
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

      getView().showLoading();

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

      subscribe(observable.map(SdkItem::new), false);
    }
  }

  @NonNull
  private List<RepoItem> getRepoItems(Repo repo) {
    List<RepoItem> repoItems = new ArrayList<>();

    if (repo.description != null) {
      repoItems.add(new RepoItem().withId(R.id.repo_about_item_description)
          .withContent(repo.description)
          .withExpandable(true)
          .withIcon(R.drawable.ic_quote));
    }

    User owner = repo.getOwner();
    if (owner != null) {
      repoItems.add(new RepoItem().withId(R.id.repo_about_item_owner).withContent(owner.getLogin()).withAvatar(owner.getAvatar()));
    }

    if (repo.getDefaultBranch() != null) {
      repoItems.add(new RepoItem().withIcon(R.id.repo_about_item_default_branch)
          .withContent(repo.getDefaultBranch())
          .withExpandable(repo.getBranches() != null && repo.getBranches().size() > 1)
          .withIcon(R.drawable.ic_git_branch));
    }
    return repoItems;
  }
}
