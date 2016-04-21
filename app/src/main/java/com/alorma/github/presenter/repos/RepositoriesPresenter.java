package com.alorma.github.presenter.repos;

import android.util.Log;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.core.Github;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.repositories.CloudRepositoriesDataSource;
import com.alorma.github.sdk.core.repositories.Repo;
import com.alorma.github.sdk.core.repositories.RepositoriesRetrofitWrapper;
import com.alorma.github.sdk.core.repository.GenericRepository;
import com.alorma.github.sdk.core.usecase.GenericUseCase;
import com.alorma.gitskarios.core.client.TokenProvider;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RepositoriesPresenter extends Presenter<String, List<Repo>> {

  private String sortOrder;

  public RepositoriesPresenter(String sortOrder) {
    this.sortOrder = sortOrder;
  }

  @Override
  public void load(String username, Callback<List<Repo>> listCallback) {
    UserReposCache cache = new UserReposCache();

    String token = TokenProvider.getInstance().getToken();
    RestWrapper reposRetrofit = new RepositoriesRetrofitWrapper(new Github(), token);
    CloudRepositoriesDataSource cloudRepositoriesDataSource =
        new CloudRepositoriesDataSource(reposRetrofit, sortOrder);

    GenericRepository<String, List<Repo>> repository =
        new GenericRepository<>(cache, cloudRepositoriesDataSource);
    GenericUseCase<String, List<Repo>> useCase = new GenericUseCase<>(repository);

    Observable.fromCallable(() -> useCase.execute(username))
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(repos -> {
          dispatchRepos(repos, listCallback);
        }, Throwable::printStackTrace);
  }

  private void dispatchRepos(List<Repo> repos, Callback<List<Repo>> listCallback) {
    Log.i("ALORMA", "Repos:" + repos);
    listCallback.onResponse(repos);
  }
}
