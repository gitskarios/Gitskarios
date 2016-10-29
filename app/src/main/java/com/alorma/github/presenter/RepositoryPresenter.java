package com.alorma.github.presenter;

import android.support.v4.util.Pair;
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

      observable.map(repo -> {
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

        return new Pair<>(repo, repoItems);
      }).subscribeOn(ioScheduler).observeOn(mainScheduler).subscribe(repoListPair -> {
        getView().onDataReceived(repoListPair.first, false);
        if (getView() instanceof RepositoryView) {
          ((RepositoryView) getView()).onRepositoryItemsReceived(repoListPair.second);
        }
      }, getView()::showError);
    }
  }

  public interface RepositoryView extends View<Repo> {
    void onRepositoryItemsReceived(List<RepoItem> items);
  }
}
