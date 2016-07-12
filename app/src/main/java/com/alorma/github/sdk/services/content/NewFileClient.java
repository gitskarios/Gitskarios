package com.alorma.github.sdk.services.content;

import com.alorma.github.sdk.bean.dto.request.NewContentRequest;
import com.alorma.github.sdk.bean.dto.response.NewContentResponse;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

public class NewFileClient extends GithubClient<NewContentResponse> {

  private NewContentRequest newContentRequest;
  private final RepoInfo repoInfo;
  private final String path;

  public NewFileClient(NewContentRequest newContentRequest, RepoInfo repoInfo, String path) {
    this.newContentRequest = newContentRequest;
    this.repoInfo = repoInfo;
    this.path = path;
  }

  @Override
  protected Observable<NewContentResponse> getApiObservable(RestAdapter restAdapter) {
    ContentService contentService = restAdapter.create(ContentService.class);
    return contentService.createFile(repoInfo.owner, repoInfo.name, path, newContentRequest);
  }
}
