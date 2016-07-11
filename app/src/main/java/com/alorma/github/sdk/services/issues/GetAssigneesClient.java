package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseInfiniteCallback;
import com.alorma.github.sdk.services.client.GithubClient;
import java.util.List;
import retrofit.RestAdapter;
import rx.Observable;

public class GetAssigneesClient extends GithubClient<List<User>> {

  private RepoInfo repoInfo;

  public GetAssigneesClient(RepoInfo repoInfo) {
    super();
    this.repoInfo = repoInfo;
  }

  @Override
  protected Observable<List<User>> getApiObservable(final RestAdapter restAdapter) {
    return Observable.create(new BaseInfiniteCallback<List<User>>() {
      @Override
      public void execute() {
        restAdapter.create(IssuesService.class).assignees(repoInfo.owner, repoInfo.name, this);
      }

      @Override
      protected void executePaginated(int nextPage) {
        restAdapter.create(IssuesService.class)
            .assignees(repoInfo.owner, repoInfo.name, nextPage, this);
      }
    });
  }
}
