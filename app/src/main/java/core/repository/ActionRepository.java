package core.repository;

import com.alorma.github.sdk.bean.info.RepoInfo;
import core.datasource.CloudDataSource;
import core.datasource.SdkItem;
import rx.Observable;

public class ActionRepository {

  private CloudDataSource<RepoInfo, Boolean> positive;
  private CloudDataSource<RepoInfo, Boolean> negative;

  public ActionRepository(CloudDataSource<RepoInfo, Boolean> positive,
      CloudDataSource<RepoInfo, Boolean> negative) {
    this.positive = positive;
    this.negative = negative;
  }

  public Observable<Boolean> execute(RepoInfo repoInfo, Boolean value) {
    if (value != null && value) {
      return negative.execute(new SdkItem<>(repoInfo)).map(SdkItem::getK);
    } else {
      return positive.execute(new SdkItem<>(repoInfo)).map(SdkItem::getK).map(aBoolean -> !aBoolean);
    }
  }
}
