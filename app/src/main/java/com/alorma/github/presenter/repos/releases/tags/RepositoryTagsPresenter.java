package com.alorma.github.presenter.repos.releases.tags;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.bean.info.RepoInfo;
import core.ApiClient;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.repositories.releases.tags.Tag;
import core.repositories.releases.tags.TagsCloudDataSource;
import core.repositories.releases.tags.TagsRetrofitWrapper;
import core.repository.GenericRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Scheduler;

/**
 * Combines repository Tags with Commits and Releases. Callback receives list of tag with fetched
 * commits and releases. Release could be null in Tag object.
 */
public class RepositoryTagsPresenter extends Presenter<RepoInfo, List<Tag>> {
  private final Scheduler ioScheduler;
  private final Scheduler mainScheduler;
  private final TagsCacheDataSource tagsCacheDataSource;
  private final TagsCloudDataSource tagsCloudDataSource;
  private final TagsRetrofitWrapper tagsRetrofitWrapper;

  private Integer page;
  GenericRepository<RepoInfo, List<Tag>> genericRepository;

  public RepositoryTagsPresenter(@IOScheduler Scheduler ioScheduler, @MainScheduler Scheduler mainScheduler,
      TagsCacheDataSource tagsCacheDataSource, TagsCloudDataSource tagsCloudDataSource, TagsRetrofitWrapper tagsRetrofitWrapper) {
    this.ioScheduler = ioScheduler;
    this.mainScheduler = mainScheduler;
    this.tagsCacheDataSource = tagsCacheDataSource;
    this.tagsCloudDataSource = tagsCloudDataSource;
    this.tagsRetrofitWrapper = tagsRetrofitWrapper;
  }

  @Override
  public void load(RepoInfo repository, Callback<List<Tag>> callback) {
    execute(config().execute(new SdkItem<>(repository)), callback, true);
  }

  @Override
  public void loadMore(RepoInfo repository, Callback<List<Tag>> mapCallback) {
    if (page != null) {
      execute(config().execute(new SdkItem<>(page, repository)), mapCallback, false);
    }
  }

  private void execute(Observable<SdkItem<List<Tag>>> observable, Callback<List<Tag>> callback, boolean firstTime) {
    observable.timeout(20, TimeUnit.SECONDS)
        .retry(3)
        .subscribeOn(ioScheduler)
        .map(sdkItem -> {
          if (sdkItem.getPage() != null && sdkItem.getPage() > 0) {
            this.page = sdkItem.getPage();
          } else {
            this.page = null;
          }
          return sdkItem.getK();
        })
        .observeOn(mainScheduler)
        .doOnSubscribe(callback::showLoading)
        .doOnCompleted(callback::hideLoading)
        .subscribe(tags -> action(tags, callback, firstTime), throwable -> {
          callback.onResponseEmpty();
          callback.hideLoading();
          throwable.printStackTrace();
        });
  }

  @Override
  protected GenericRepository<RepoInfo, List<Tag>> configRepository(RestWrapper restWrapper) {
    if (genericRepository == null) {
      genericRepository = new GenericRepository<>(tagsCacheDataSource, tagsCloudDataSource);
    }
    return genericRepository;
  }

  @Override
  protected TagsRetrofitWrapper getRest(ApiClient apiClient, String token) {
    return tagsRetrofitWrapper;
  }

  @Override
  public void action(List<Tag> tags, Callback<List<Tag>> mapCallback, boolean firstTime) {
    if (tags != null && tags.size() > 0) {
      mapCallback.onResponse(tags, firstTime);
    } else {
      mapCallback.onResponseEmpty();
    }
  }

  public Integer getPage() {
    return page;
  }
}
