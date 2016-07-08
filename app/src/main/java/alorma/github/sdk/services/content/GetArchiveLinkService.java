package com.alorma.github.sdk.services.content;

import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Bernat on 17/12/2014.
 */
public class GetArchiveLinkService extends GithubClient {

  private RepoInfo repoInfo;
  private String fileType;
  private Downloader downloader;

  public GetArchiveLinkService(RepoInfo repoInfo, String fileType, Downloader downloader) {
    super();
    this.repoInfo = repoInfo;
    this.fileType = fileType;
    this.downloader = downloader;
  }

  @Override
  protected Observable getApiObservable(RestAdapter restAdapter) {
    Observable<Object> observable = restAdapter.create(ContentService.class)
        .archiveLink(repoInfo.owner, repoInfo.name, fileType, repoInfo.branch);

    observable.doOnError(new Action1<Throwable>() {
      @Override
      public void call(Throwable error) {
        if (error != null && error instanceof RetrofitError) {
          String url = ((RetrofitError) error).getResponse().getUrl();
          downloader.download(url);
        }
      }
    });

    return observable;
  }
}
