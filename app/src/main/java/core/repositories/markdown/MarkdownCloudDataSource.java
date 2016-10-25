package core.repositories.markdown;

import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.repositories.readme.RepositoryReadmeService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Observable;

public class MarkdownCloudDataSource extends CloudDataSource<String, String> {
  private final MarkdownRetrofitWrapper restWrapper;

  public MarkdownCloudDataSource(MarkdownRetrofitWrapper restWrapper) {
    super(restWrapper);
    this.restWrapper = restWrapper;
  }

  @Override
  protected Observable<SdkItem<String>> execute(SdkItem<String> request, RestWrapper service) {
    RepositoryReadmeService repositoryReadmeService = restWrapper.get();
    String readme = request.getK();
    return Observable.fromCallable(() -> {
      Call<ResponseBody> call = repositoryReadmeService.markdown(readme);
      return call.execute().body().string();
    }).map(SdkItem::new);
  }
}
