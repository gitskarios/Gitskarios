package core.repository;

import com.alorma.github.injector.named.Starred;
import com.alorma.github.sdk.bean.info.RepoInfo;
import core.datasource.CloudDataSource;
import core.datasource.SdkItem;
import rx.Observable;

public class GetRepositoryStarRepository {

  private CloudDataSource<RepoInfo, Boolean> dataSource;

  public GetRepositoryStarRepository(@Starred CloudDataSource<RepoInfo, Boolean> dataSource) {
    this.dataSource = dataSource;
  }

  public Observable<Boolean> check(RepoInfo repoInfo) {
    return dataSource.execute(new SdkItem<>(repoInfo)).map(SdkItem::getK);
  }
}
