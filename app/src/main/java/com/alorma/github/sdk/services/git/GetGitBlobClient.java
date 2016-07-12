package com.alorma.github.sdk.services.git;

import com.alorma.github.sdk.bean.dto.response.GitBlob;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

public class GetGitBlobClient extends GithubClient<GitBlob> {

  private CommitInfo info;

  public GetGitBlobClient(CommitInfo info) {
    super();
    this.info = info;
  }

  @Override
  protected Observable<GitBlob> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(GitDataService.class)
        .repoBlob(info.repoInfo.owner, info.repoInfo.name, info.sha);
  }
}
