package com.alorma.github.presenter.issue;

import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.sdk.core.issues.CloudUsersIssuesDataSource;
import com.alorma.github.sdk.core.issues.Issue;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;

@PerActivity public class MentionedUserIssuesPresenter extends UserIssuesPresenter {

  @Inject
  public MentionedUserIssuesPresenter() {
    super();
  }

  @Override
  protected CacheDataSource<Void, List<Issue>> getUserIssuesCacheDataSource() {
    return new CacheDataSource<Void, List<Issue>>() {
      @Override
      public void saveData(SdkItem<Void> request, SdkItem<List<Issue>> data) {

      }

      @Override
      public Observable<SdkItem<List<Issue>>> getData(SdkItem<Void> request) {
        return Observable.empty();
      }
    };
  }

  @Override
  protected CloudDataSource<Void, List<Issue>> getCloudIssuesDataSource(RestWrapper issuesRetrofit) {
    return new CloudUsersIssuesDataSource(issuesRetrofit, "mentioned");
  }
}
