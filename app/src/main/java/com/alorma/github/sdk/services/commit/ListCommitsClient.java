package com.alorma.github.sdk.services.commit;

import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

public class ListCommitsClient extends GithubListClient<List<Commit>> {
  private CommitInfo info;
  private String path;
  private int page;

  public ListCommitsClient(CommitInfo info, int page) {
    super();
    this.info = info;
    this.page = page;
  }

  public ListCommitsClient(CommitInfo info, String path, int page) {
    super();
    this.info = info;
    this.path = path;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        CommitsService commitsService = restAdapter.create(CommitsService.class);
        if (path == null) {
          if (info.sha == null) {
            if (page == 0) {
              commitsService.commits(info.repoInfo.owner, info.repoInfo.name, this);
            } else {
              commitsService.commits(info.repoInfo.owner, info.repoInfo.name, page, this);
            }
          } else {
            if (page == 0) {
              commitsService.commits(info.repoInfo.owner, info.repoInfo.name, info.sha, this);
            } else {
              commitsService.commits(info.repoInfo.owner, info.repoInfo.name, page, info.sha, this);
            }
          }
        } else {
          if (info.sha == null) {
            if (page == 0) {
              commitsService.commitsByPath(info.repoInfo.owner, info.repoInfo.name, path, this);
            } else {
              commitsService.commitsByPath(info.repoInfo.owner, info.repoInfo.name, path, page, this);
            }
          } else {
            if (page == 0) {
              commitsService.commitsByPath(info.repoInfo.owner, info.repoInfo.name, path, info.sha, this);
            } else {
              commitsService.commitsByPath(info.repoInfo.owner, info.repoInfo.name, path, info.sha, page, this);
            }
          }
        }
      }
    };
  }

  @Override
  public String getAcceptHeader() {
    return "application/vnd.github.cryptographer-preview+sha.json";
  }
}
