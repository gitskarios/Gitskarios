package com.alorma.github.ui.activity.issue;

import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.MilestoneState;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.issues.GetMilestonesClient;
import core.ApiClient;
import core.datasource.RestWrapper;
import core.repository.GenericRepository;
import java.util.Collections;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueMilestonePresenter extends Presenter<RepoInfo, List<Milestone>> {
  private MilestoneState state;

  public IssueMilestonePresenter(MilestoneState state) {
    this.state = state;
  }

  @Override
  public void load(RepoInfo repoInfo, Callback<List<Milestone>> listCallback) {
    GetMilestonesClient milestonesClient = new GetMilestonesClient(repoInfo, state, true);
    milestonesClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(listCallback::showLoading)
        .doOnCompleted(listCallback::hideLoading)
        .map(milestones -> {
          Collections.sort(milestones, (milestone, t1) -> t1.updatedAt.compareTo(milestone.updatedAt));
          return milestones;
        })
        .subscribe(milestones -> action(milestones, listCallback, true), Throwable::printStackTrace);
  }

  @Override
  public void loadMore(RepoInfo repoInfo, Callback<List<Milestone>> listCallback) {

  }

  @Override
  protected GenericRepository<RepoInfo, List<Milestone>> configRepository(RestWrapper restWrapper) {
    return null;
  }

  @Override
  protected RestWrapper getRest(ApiClient apiClient, String token) {
    return null;
  }

  @Override
  public void action(List<Milestone> milestones, Callback<List<Milestone>> listCallback, boolean firstTime) {
    listCallback.onResponse(milestones, firstTime);
  }
}
