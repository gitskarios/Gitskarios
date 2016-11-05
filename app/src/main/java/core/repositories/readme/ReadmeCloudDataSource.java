package core.repositories.readme;

import android.util.Base64;
import com.alorma.github.sdk.bean.ReadmeInfo;
import com.alorma.github.sdk.bean.dto.response.Content;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

public class ReadmeCloudDataSource extends CloudDataSource<ReadmeInfo, String> {
  private final ReadmeRetrofitWrapper restWrapper;

  public ReadmeCloudDataSource(ReadmeRetrofitWrapper restWrapper) {
    super(restWrapper);
    this.restWrapper = restWrapper;
  }

  @Override
  protected Observable<SdkItem<String>> execute(SdkItem<ReadmeInfo> request, RestWrapper service) {
    RepositoryReadmeService repositoryReadmeService = restWrapper.get();
    ReadmeInfo readmeInfo = request.getK();
    return Observable.fromCallable(() -> {
      Call<Content> call;
      if (readmeInfo.getRepoInfo().branch == null) {
        call = repositoryReadmeService.readme(readmeInfo.getRepoInfo().owner, readmeInfo.getRepoInfo().name);
      } else {
        call =
            repositoryReadmeService.readme(readmeInfo.getRepoInfo().owner, readmeInfo.getRepoInfo().name, readmeInfo.getRepoInfo().branch);
      }

      Response<Content> contentResponse = call.execute();

      return contentResponse.body();
    }).map(Content::getContent).flatMap(content -> Observable.fromCallable(() -> {
      byte[] data = Base64.decode(content, Base64.DEFAULT);
      return new String(data, "UTF-8");
    })).map(s -> {
      if (readmeInfo.isTruncate()) {
        return trimString(s, 300, true);
      } else {
        return s;
      }
    }).map(SdkItem::new);
  }

  public static String trimString(String string, int length, boolean soft) {
    if (string == null || string.trim().isEmpty()) {
      return string;
    }

    StringBuilder sb = new StringBuilder(string);
    int actualLength = length - 3;
    if (sb.length() > actualLength) {
      // -3 because we add 3 dots at the end. Returned string length has to be length including the dots.
      if (!soft) {
        return sb.insert(actualLength, "...").substring(0, actualLength + 3);
      } else {
        int endIndex = sb.indexOf(" ", actualLength);
        return sb.insert(endIndex, "...").substring(0, endIndex + 3);
      }
    }
    return string;
  }
}
