package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseInfiniteCallback;
import com.alorma.github.sdk.services.client.GithubClient;
import java.util.List;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 20/07/2014.
 */
public class GetRepoCollaboratorsClient extends GithubClient<List<User>> {

  private final RepoInfo repoInfo;
  private int page = 0;

  public GetRepoCollaboratorsClient(RepoInfo repoInfo) {
    this(repoInfo, 0);
  }

  public GetRepoCollaboratorsClient(RepoInfo repoInfo, int page) {
    super();
    this.repoInfo = repoInfo;
    this.page = page;
  }

  @Override
  protected Observable<List<User>> getApiObservable(final RestAdapter restAdapter) {
    return Observable.create(new BaseInfiniteCallback<List<User>>() {
      @Override
      public void execute() {
        RepoService repoService = restAdapter.create(RepoService.class);
        repoService.collaborators(repoInfo.owner, repoInfo.name, this);
      }

      @Override
      protected void executePaginated(int nextPage) {
        RepoService repoService = restAdapter.create(RepoService.class);
        repoService.collaborators(repoInfo.owner, repoInfo.name, page, this);
      }
    });
  }
}
