package com.alorma.github.sdk.services.reference;

import com.alorma.github.sdk.bean.dto.response.GitReference;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;

import retrofit.RestAdapter;
import rx.Observable;

public class GetReferenceClient extends GithubClient<GitReference> {
  private final RepoInfo info;
  private String ref;

  public GetReferenceClient(RepoInfo repoInfo, String ref) {
    super();
    this.info = repoInfo;
    this.ref = GitReferenceService.HEADS + ref;
  }

  @Override
  protected Observable<GitReference> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(GitReferenceService.class)
        .getReference(info.owner, info.name, ref);
  }
}
