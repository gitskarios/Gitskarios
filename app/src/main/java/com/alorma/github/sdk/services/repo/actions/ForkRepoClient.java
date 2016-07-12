package com.alorma.github.sdk.services.repo.actions;

import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

public class ForkRepoClient extends GithubClient<Repo> {

  private RepoInfo repoInfo;
  private String org;

  public ForkRepoClient(RepoInfo repoInfo) {
    super();
    this.repoInfo = repoInfo;
  }

  public ForkRepoClient(RepoInfo repoInfo, String org) {
    this(repoInfo);
    this.org = org;
  }

  @Override
  protected Observable<Repo> getApiObservable(RestAdapter restAdapter) {
    if (org == null) {
      return restAdapter.create(RepoActionsService.class)
          .forkRepo(repoInfo.owner, repoInfo.name, new Object());
    } else {
      return restAdapter.create(RepoActionsService.class)
          .forkRepo(repoInfo.owner, repoInfo.name, org, new Object());
    }
  }
}
