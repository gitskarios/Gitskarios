package core.repositories.readme;

import android.util.Base64;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.info.RepoInfo;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

public class ReadmeCloudDataSource extends CloudDataSource<RepoInfo, String> {
  private final ReadmeRetrofitWrapper restWrapper;

  public ReadmeCloudDataSource(ReadmeRetrofitWrapper restWrapper) {
    super(restWrapper);
    this.restWrapper = restWrapper;
  }

  @Override
  protected Observable<SdkItem<String>> execute(SdkItem<RepoInfo> request, RestWrapper service) {
    RepositoryReadmeService repositoryReadmeService = restWrapper.get();
    RepoInfo repoInfo = request.getK();
    return Observable.fromCallable(() -> {
      Call<Content> call;
      if (repoInfo.branch == null) {
        call = repositoryReadmeService.readme(repoInfo.owner, repoInfo.name);
      } else {
        call = repositoryReadmeService.readme(repoInfo.owner, repoInfo.name, repoInfo.branch);
      }

      Response<Content> contentResponse = call.execute();

      return contentResponse.body();
    }).map(Content::getContent).flatMap(content -> Observable.fromCallable(() -> {
      byte[] data = Base64.decode(content, Base64.DEFAULT);
      return new String(data, "UTF-8");
    })).map(SdkItem::new);
  }
}
