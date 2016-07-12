package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.request.CreateMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 15/04/2015.
 */
public class CreateMilestoneClient extends GithubClient<Milestone> {
  private RepoInfo repoInfo;
  private CreateMilestoneRequestDTO createMilestoneRequestDTO;

  public CreateMilestoneClient(RepoInfo repoInfo,
      CreateMilestoneRequestDTO createMilestoneRequestDTO) {
    super();
    this.repoInfo = repoInfo;
    this.createMilestoneRequestDTO = createMilestoneRequestDTO;
  }

  @Override
  protected Observable<Milestone> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(IssuesService.class)
        .createMilestone(repoInfo.owner, repoInfo.name, createMilestoneRequestDTO);
  }
}
