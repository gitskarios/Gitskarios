package com.alorma.github.sdk.services.git;

import com.alorma.github.sdk.bean.dto.response.GitReference;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

public class GetReferenceClient extends GithubClient<GitReference> {

  private final RepoInfo info;

  public GetReferenceClient(RepoInfo repoInfo) {
    super();
    this.info = repoInfo;
  }

  @Override
  protected Observable<GitReference> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(GitDataService.class)
        .repoReference(info.owner, info.name, info.branch);
  }
}
