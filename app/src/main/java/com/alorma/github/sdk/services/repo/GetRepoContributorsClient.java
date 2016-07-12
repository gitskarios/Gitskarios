package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.response.Contributor;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseInfiniteCallback;
import com.alorma.github.sdk.services.client.GithubClient;
import java.util.List;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 20/07/2014.
 */
public class GetRepoContributorsClient extends GithubClient<List<Contributor>> {

  private final RepoInfo repoInfo;
  private int page;

  public GetRepoContributorsClient(RepoInfo repoInfo) {
    this(repoInfo, 0);
  }

  public GetRepoContributorsClient(RepoInfo repoInfo, int page) {
    super();
    this.repoInfo = repoInfo;
    this.page = page;
  }

  @Override
  protected Observable<List<Contributor>> getApiObservable(RestAdapter restAdapter) {
    return Observable.create(new BaseInfiniteCallback<List<Contributor>>() {
      @Override
      public void execute() {
        RepoService repoService = getRestAdapter().create(RepoService.class);
        repoService.contributors(getOwner(), getRepo(), this);
      }

      @Override
      protected void executePaginated(int nextPage) {
        RepoService repoService = getRestAdapter().create(RepoService.class);
        repoService.contributors(getOwner(), getRepo(), page, this);
      }
    });
  }

  private String getOwner() {
    return repoInfo.owner;
  }

  private String getRepo() {
    return repoInfo.name;
  }
}
