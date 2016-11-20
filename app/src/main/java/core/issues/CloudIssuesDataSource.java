package core.issues;

import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.issue.IssuesSearchRequest;
import core.issue.IssuesSearchService;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

public class CloudIssuesDataSource extends CloudDataSource<IssuesSearchRequest, List<Issue>> {

  public CloudIssuesDataSource(RestWrapper restWrapper) {
    super(restWrapper);
  }

  @Override
  protected Observable<SdkItem<List<Issue>>> execute(SdkItem<IssuesSearchRequest> request, RestWrapper service) {

    IssuesSearchService issuesService = service.get();

    Observable<List<Issue>> observable = Observable.defer(() -> {

      IssuesSearchRequest k = request.getK();

      Call<IssueSearchResponse> call;
      if (request.getPage() != null && request.getPage() > 0) {
        call = issuesService.issues(k.build(), request.getPage());
      } else {
        call = issuesService.issues(k.build());
      }

      try {
        Response<IssueSearchResponse> response = call.execute();
        if (response.isSuccessful()) {
          return Observable.just(response.body());
        } else {
          return Observable.error(new Exception(response.errorBody().string()));
        }
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).map(IssueSearchResponse::getIssues);

    return observable.map(SdkItem::new);
  }
}
