package com.alorma.github.presenter.repo;

import com.alorma.github.sdk.bean.info.RepoInfo;
import core.datasource.SdkItem;
import core.repositories.Repo;
import rx.Observable;

public interface GetRepositoryUseCase {

  Observable<SdkItem<Repo>> getRepository(RepoInfo repoInfo);
  Observable<SdkItem<Repo>> getRepository(RepoInfo repoInfo, boolean refresh);

}
