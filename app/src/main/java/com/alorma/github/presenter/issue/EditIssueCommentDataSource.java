package com.alorma.github.presenter.issue;

import com.alorma.github.sdk.core.GithubComment;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.sdk.core.issue.EditIssueCommentBodyRequest;
import com.alorma.github.sdk.core.issue.IssueCommentsRetrofit;
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
