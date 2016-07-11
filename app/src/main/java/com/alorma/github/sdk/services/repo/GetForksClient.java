package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

/**
 * Created by a557114 on 05/09/2015.
 */
public class GetForksClient extends GithubListClient<List<Repo>> {

  public static final SortType NEWEST = SortType.NEWEST;
  public static final SortType OLDEST = SortType.OLDEST;
  public static final SortType STARGAZERS = SortType.STARGAZERS;
  private final RepoInfo repoInfo;
  private final int page;
  // newest, oldest, stargazers
  private SortType sort = null;

  public GetForksClient(RepoInfo repoInfo) {
    this(repoInfo, 0);
  }

  public GetForksClient(RepoInfo repoInfo, int page) {
    super();
    this.repoInfo = repoInfo;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        RepoService repoService = restAdapter.create(RepoService.class);
        if (page == 0) {
          repoService.listForks(repoInfo.owner, repoInfo.name, sort.getType(), this);
        } else {
          repoService.listForks(repoInfo.owner, repoInfo.name, sort.getType(), page, this);
        }
      }
    };
  }

  public void setSort(SortType sort) {
    this.sort = sort;
  }

  public enum SortType {
    NEWEST("newest"),
    OLDEST("oldest"),
    STARGAZERS("stargazers");

    private String type;

    SortType(String type) {

      this.type = type;
    }

    public String getType() {
      return type;
    }
  }
}
