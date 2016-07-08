package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;

/**
 * Created by Bernat on 13/07/2014.
 */
public abstract class GithubRepoClient<K> extends GithubClient<K> {

  private RepoInfo repoInfo;

  public GithubRepoClient(RepoInfo repoInfo) {
    super();
    this.repoInfo = repoInfo;
  }

  public String getOwner() {
    return repoInfo.owner;
  }

  public String getRepo() {
    return repoInfo.name;
  }

  public String getBranch() {
    return repoInfo.branch;
  }
}
