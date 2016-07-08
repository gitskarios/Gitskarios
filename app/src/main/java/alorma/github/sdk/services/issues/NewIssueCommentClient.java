package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 06/09/2014.
 */
public class NewIssueCommentClient extends GithubClient<GithubComment> {
  private String body;
  private String owner;
  private String repo;
  private int num;

  public NewIssueCommentClient(IssueInfo issueInfo, String body) {
    super();
    this.body = body;
    owner = issueInfo.repoInfo.owner;
    repo = issueInfo.repoInfo.name;
    num = issueInfo.num;
  }

  @Override
  protected Observable<GithubComment> getApiObservable(RestAdapter restAdapter) {
    GithubComment comment = new GithubComment();
    comment.body = body;

    IssuesService service = restAdapter.create(IssuesService.class);
    return service.addComment(owner, repo, num, comment);
  }
}
