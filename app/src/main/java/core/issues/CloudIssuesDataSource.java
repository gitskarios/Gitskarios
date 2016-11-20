package core.issues;

import com.alorma.github.sdk.bean.info.RepoInfo;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.issue.IssuesRequest;
import core.issue.IssuesService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

public class CloudIssuesDataSource extends CloudDataSource<IssuesRequest, List<Issue>> {

  public CloudIssuesDataSource(RestWrapper restWrapper) {
    super(restWrapper);
  }

  @Override
  protected Observable<SdkItem<List<Issue>>> execute(SdkItem<IssuesRequest> request, RestWrapper service) {

    IssuesService issuesService = service.get();

    Observable<List<Issue>> observable = Observable.defer(() -> {

      IssuesRequest k = request.getK();
      RepoInfo repoInfo = k.getRepoInfo();
      Map<String, String> filters = k.getFilters();

      Call<List<Issue>> call;
      if (request.getPage() != null && request.getPage() > 0) {
        call = issuesService.issues(repoInfo.owner, repoInfo.name, filters, request.getPage());
      } else {
        call = issuesService.issues(repoInfo.owner, repoInfo.name, filters);
      }

      try {
        Response<List<Issue>> response = call.execute();
        if (response.isSuccessful()) {
          return Observable.just(response.body());
        } else {
          return Observable.error(new Exception(response.errorBody().string()));
        }
      } catch (IOException e) {
        return Observable.error(e);
      }
    });

    return observable.map(SdkItem::new);
  }
}
