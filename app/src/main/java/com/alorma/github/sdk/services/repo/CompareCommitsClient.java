package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.response.CompareCommit;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by a557114 on 31/07/2015.
 */
public class CompareCommitsClient extends GithubClient<CompareCommit> {
  private final RepoInfo repoInfo;
  private final String base;
  private final String head;

  public CompareCommitsClient(RepoInfo repoInfo, String base, String head) {
    super();
    this.repoInfo = repoInfo;
    this.base = base;
    this.head = head;
  }

  @Override
  protected Observable<CompareCommit> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(RepoService.class)
        .compareCommits(repoInfo.owner, repoInfo.name, base, head);
  }
}
