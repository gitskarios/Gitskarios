package com.alorma.github.presenter.issue;

import core.GithubComment;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.issue.EditIssueCommentBodyRequest;
import core.issue.IssueCommentsRetrofit;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

public class EditIssueCommentDataSource extends CloudDataSource<EditIssueCommentBodyRequest, GithubComment> {
  public EditIssueCommentDataSource(RestWrapper restWrapper) {
    super(restWrapper);
  }

  @Override
  protected Observable<SdkItem<GithubComment>> execute(SdkItem<EditIssueCommentBodyRequest> request, RestWrapper service) {
    return Observable.defer(() -> {
      IssueCommentsRetrofit retrofit = service.get();
      EditIssueCommentBodyRequest k = request.getK();
      Call<GithubComment> call = retrofit.editComment(k.getRepoInfo().name, k.getRepoInfo().name, k.getCommentId(), k.getBody());

      try {
        Response<GithubComment> response = call.execute();

        if (response.isSuccessful()) {
          return Observable.just(response.body());
        } else {
          return Observable.error(new Exception(response.errorBody().string()));
        }
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).map(SdkItem::new);
  }
}
