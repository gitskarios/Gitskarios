package core.orgs;

import core.User;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func0;

public class OrganizationsDataSource extends CloudDataSource<String, List<User>> {

  public OrganizationsDataSource(RestWrapper restWrapper) {
    super(restWrapper);
  }

  @Override
  protected Observable<SdkItem<List<User>>> execute(final SdkItem<String> data, final RestWrapper service) {
    return Observable.defer(new Func0<Observable<SdkItem<List<User>>>>() {
      @Override
      public Observable<SdkItem<List<User>>> call() {
        OrganizationsService organizationsService = service.get();
        Call<List<User>> call;
        if (data.getK() == null) {
          if (data.getPage() != null) {
            call = organizationsService.listOrgs(data.getPage());
          } else {
            call = organizationsService.listOrgs();
          }
        } else {
          if (data.getPage() != null) {
            call = organizationsService.listOrgs(data.getK(), data.getPage());
          } else {
            call = organizationsService.listOrgs(data.getK());
          }
        }

        try {
          Response<List<User>> listResponse = call.execute();
          if (listResponse.isSuccessful()) {
            Integer page = null;
            if (service.isPaginated(listResponse)) {
              page = service.getPage(listResponse);
            }

            return Observable.just(new SdkItem<>(page, listResponse.body()));
          } else {
            return Observable.error(new Exception(listResponse.message()));
          }
        } catch (IOException e) {
          return Observable.error(e);
        }
      }
    });
  }
}
