package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.request.RepoRequestDTO;
import com.alorma.github.sdk.services.client.GithubClient;
import core.repositories.Repo;
import retrofit.RestAdapter;
import rx.Observable;

public class CreateRepositoryClient extends GithubClient<Repo> {

  private RepoRequestDTO repoRequestDTO;

  public CreateRepositoryClient(RepoRequestDTO repoRequestDTO) {
    super();
    this.repoRequestDTO = repoRequestDTO;
  }

  @Override
  protected Observable<Repo> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(RepoService.class).create(repoRequestDTO);
  }
}
