package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.request.RepoRequestDTO;
import com.alorma.github.sdk.bean.info.RepoInfo;
import core.repositories.Repo;
import retrofit.RestAdapter;
import rx.Observable;

public class EditRepoClient extends GithubRepoClient<Repo> {

  private RepoRequestDTO repoRequestDTO;

  public EditRepoClient(RepoInfo repoInfo, RepoRequestDTO repoRequestDTO) {
    super(repoInfo);
    this.repoRequestDTO = repoRequestDTO;
  }

  @Override
  protected Observable<Repo> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(RepoService.class).edit(getOwner(), getRepo(), repoRequestDTO);
  }
}
