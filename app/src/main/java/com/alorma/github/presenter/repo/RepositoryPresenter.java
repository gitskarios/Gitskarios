package com.alorma.github.presenter.repo;

import com.alorma.github.gcm.GcmTopicsHelper;
import com.alorma.github.presenter.BaseRxPresenter;
import com.alorma.github.presenter.View;
import com.alorma.github.presenter.repo.tags.GetTagsCountUseCase;
import com.alorma.github.sdk.bean.dto.request.WebHookConfigRequest;
import com.alorma.github.sdk.bean.dto.request.WebHookRequest;
import com.alorma.github.sdk.bean.dto.request.WebHookResponse;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.webhooks.AddWebHookClient;
import core.datasource.SdkItem;
import core.repositories.Repo;
import core.repository.ActionRepository;
import rx.Observable;
import rx.Scheduler;

public class RepositoryPresenter extends BaseRxPresenter<RepoInfo, Repo, View<Repo>> {

  private final GetRepositoryUseCase getRepositoryUseCase;
  private final ActionRepository changeRepositoryStarUseCase;
  private final ActionRepository changeRepositoryWatchUseCase;
  private final GetTagsCountUseCase getTagsCountUseCase;
  private Repo currentRepo;

  public RepositoryPresenter(Scheduler mainScheduler, Scheduler ioScheduler, GetRepositoryUseCase getRepositoryUseCase,
      ActionRepository changeRepositoryStarUseCase, ActionRepository changeRepositoryWatchUseCase,
      GetTagsCountUseCase getTagsCountUseCase) {
    super(mainScheduler, ioScheduler, null);
    this.getRepositoryUseCase = getRepositoryUseCase;
    this.changeRepositoryStarUseCase = changeRepositoryStarUseCase;
    this.changeRepositoryWatchUseCase = changeRepositoryWatchUseCase;
    this.getTagsCountUseCase = getTagsCountUseCase;
  }

  @Override
  public void execute(final RepoInfo repoInfo) {
    if (isViewAttached() && repoInfo != null) {

      getView().showLoading();

      subscribe(getRepositoryUseCase.getRepository(repoInfo), false);

      getTagsCountUseCase.getTagsCount(repoInfo)
          .map(SdkItem::getK)
          .subscribeOn(ioScheduler)
          .observeOn(mainScheduler)
          .subscribe(this::showReleases, throwable -> showReleases(0));
    }
  }

  private void showReleases(Integer integer) {
    if (isViewAttached() && getView() != null && getView() instanceof RepoView) {
      ((RepoView) getView()).showTagsCount(integer != null ? integer : 0);
    }
  }

  public void toggleStar() {
    Observable<SdkItem<Repo>> resultObservable = Observable.defer(() -> {
      if (currentRepo != null) {
        Boolean isStarred = currentRepo.isStarred();

        String owner = currentRepo.getOwner().getLogin();
        String name = currentRepo.getName();

        RepoInfo info = new RepoInfo();
        info.owner = owner;
        info.name = name;

        return changeRepositoryStarUseCase.execute(info, isStarred);
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

        RepoInfo info = new RepoInfo();
        info.owner = owner;
        info.name = name;

        return changeRepositoryWatchUseCase.execute(info, isWatched);
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

  public void subscribeRepoPushes(RepoInfo requestRepoInfo) {
    WebHookRequest webhook = new WebHookRequest();
    webhook.name = "web";
    webhook.active = true;
    webhook.events = new String[] {
        "issues"
    };
    webhook.config = new WebHookConfigRequest();
    webhook.config.content_type = "json";
    webhook.config.url = "https://cryptic-ravine-97684.herokuapp.com/message";

    Observable<WebHookResponse> observable = new AddWebHookClient(requestRepoInfo.owner, requestRepoInfo.name, webhook).observable();

    observable.subscribeOn(ioScheduler).observeOn(mainScheduler).subscribe(webHookResponse -> {
      GcmTopicsHelper.registerInTopic(requestRepoInfo);
    }, throwable -> {

    });
  }

  public interface RepoView extends View<Repo> {
    void showTagsCount(int tagsCount);
  }
}
