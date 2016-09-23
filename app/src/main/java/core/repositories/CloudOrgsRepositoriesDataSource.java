package core.repositories;

import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import java.util.List;
import java.util.concurrent.Callable;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

public class CloudOrgsRepositoriesDataSource extends CloudDataSource<String, List<Repo>> {

  private String sortOrder;

  public CloudOrgsRepositoriesDataSource(RestWrapper restWrapper, String sortOrder) {
    super(restWrapper);
    this.sortOrder = sortOrder;
  }

  @Override
  protected Observable<SdkItem<List<Repo>>> execute(final SdkItem<String> data,
      final RestWrapper service) {
    return Observable.fromCallable(new Callable<SdkItem<List<Repo>>>() {
      @Override
      public SdkItem<List<Repo>> call() throws Exception {
        ReposService reposService = service.get();
        Call<List<Repo>> call;
        if (data.getPage() != null) {
          call = reposService.userReposListFromOrgs(data.getPage(), sortOrder);
        } else {
          call = reposService.userReposListFromOrgs(sortOrder);
        }

        Response<List<Repo>> listResponse = call.execute();
        Integer page = null;
        if (service.isPaginated(listResponse)) {
          page = service.getPage(listResponse);
        }

        return new SdkItem<>(page, listResponse.body());
      }
    });
  }
}
