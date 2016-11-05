package com.alorma.github.presenter.repo.tags;

import com.alorma.github.sdk.bean.info.RepoInfo;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.repositories.releases.tags.RepositoryTagsService;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

public class TagsCountDatasource extends CloudDataSource<RepoInfo, Integer> {
  public TagsCountDatasource(RestWrapper restWrapper) {
    super(restWrapper);
  }

  @Override
  protected Observable<SdkItem<Integer>> execute(SdkItem<RepoInfo> request, RestWrapper service) {
    return Observable.defer(() -> {
      RepositoryTagsService tagsService = service.get();

      Call<Void> call = tagsService.tagsCount(request.getK().owner, request.getK().name);

      try {
        Response<Void> response = call.execute();
        if (service.isPaginated(response)) {
          return Observable.just(service.getLastPage(response));
        }
        return Observable.just(0);
      } catch (IOException e) {
        return Observable.error(e);
      }
    }).map(SdkItem::new);
  }
}
