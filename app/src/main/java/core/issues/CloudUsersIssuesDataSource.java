package core.issues;

import core.User;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.issue.IssuesSearchRequest;
import core.issue.IssuesSearchService;
import core.repositories.Repo;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

public class CloudUsersIssuesDataSource extends CloudDataSource<IssuesSearchRequest, List<Issue>> {

  public CloudUsersIssuesDataSource(RestWrapper restWrapper) {
    super(restWrapper);
  }

  @Override
  protected Observable<SdkItem<List<Issue>>> execute(final SdkItem<IssuesSearchRequest> data, final RestWrapper service) {
    return Observable.defer(() -> {
      IssuesSearchService issuesService = service.get();
      Call<IssueSearchResponse> call;
      if (data.getPage() != null) {
        call = issuesService.userIssues(data.getK().build(), data.getPage());
      } else {
        call = issuesService.userIssues(data.getK().build());
      }

      try {
        Response<IssueSearchResponse> response = call.execute();
        if (response.isSuccessful()) {
          Integer page = null;
          if (service.isPaginated(response)) {
            page = service.getPage(response);
          }

          return Observable.just(new SdkItem<>(page, response.body().getIssues()));
        } else {
          return Observable.error(new Exception(response.message()));
        }
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).map(listSdkItem -> {
      List<Issue> issues = listSdkItem.getK();

      if (issues != null) {
        for (Issue issue : issues) {
          if (issue.getRepository() == null && issue.getRepositoryUrl() != null) {
            String url = issue.getRepositoryUrl();
            url = url.replaceAll("https://", "");
            url = url.replaceAll("http://", "");
            url = url.replace("api.github.com/repos/", "");
            String[] parts = url.split("/");
            String owner = parts[0];
            String repo = parts[1];
            Repo repository = new Repo();
            User user = new User();
            user.setLogin(owner);
            repository.setOwner(user);
            repository.setName(repo);
            issue.setRepository(repository);
          }
        }
      }

      return listSdkItem;
    });
  }
}
