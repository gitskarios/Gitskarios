package com.alorma.github.sdk.services.git;

import com.alorma.github.sdk.bean.dto.response.GitReference;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

public class GetReferencesClient extends GithubListClient<List<GitReference>> {

  private final RepoInfo info;
  private final int page;

  public GetReferencesClient(RepoInfo repoInfo) {
    this(repoInfo, 0);
  }

  public GetReferencesClient(RepoInfo repoInfo, int page) {
    super();
    this.info = repoInfo;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        GitDataService gitDataService = restAdapter.create(GitDataService.class);
        if (page == 0) {
          gitDataService.repoReferences(info.owner, info.name, this);
        } else {
          gitDataService.repoReferences(info.owner, info.name, page, this);
        }
      }
    };
  }
}
