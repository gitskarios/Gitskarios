package com.alorma.github.sdk.services.commit;

import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

public class GetSingleCommitClient extends GithubClient<Commit> {

  private CommitInfo info;

  public GetSingleCommitClient(CommitInfo info) {
    super();
    this.info = info;
  }

  @Override
  protected Observable<Commit> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(CommitsService.class)
        .singleCommit(info.repoInfo.owner, info.repoInfo.name, info.sha);
  }

  @Override
  public String getAcceptHeader() {
    return "application/vnd.github.cryptographer-preview+sha.json";
  }
}
