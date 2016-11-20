package core.repositories;

import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import java.util.List;
import java.util.concurrent.Callable;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

public class CloudMembershipRepositoriesDataSource extends CloudDataSource<String, List<Repo>> {

  private String sortOrder;

  public CloudMembershipRepositoriesDataSource(RestWrapper restWrapper, String sortOrder) {
    super(restWrapper);
    this.sortOrder = sortOrder;
  }

  @Override
  protected Observable<SdkItem<List<Repo>>> execute(final SdkItem<String> data,
      final RestWrapper service) {
    return Observable.fromCallable(() -> {
      ReposService reposService = service.get();
      Call<List<Repo>> call;
      if (data.getPage() != null) {
        call = reposService.userMemberRepos(data.getPage(), sortOrder);
      } else {
        call = reposService.userMemberRepos(sortOrder);
      }

      Response<List<Repo>> listResponse = call.execute();
      Integer page = null;
      if (service.isPaginated(listResponse)) {
        page = service.getPage(listResponse);
      }

      return new SdkItem<>(page, listResponse.body());
    });
  }
}
