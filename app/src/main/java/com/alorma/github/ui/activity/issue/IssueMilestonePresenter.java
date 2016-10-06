package com.alorma.github.ui.activity.issue;

import com.alorma.github.presenter.BaseRxPresenter;
import com.alorma.github.presenter.View;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.MilestoneState;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.issues.GetMilestonesClient;
import core.ApiClient;
import core.datasource.RestWrapper;
import core.repository.GenericRepository;
import java.util.Collections;
import java.util.List;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueMilestonePresenter
        extends BaseRxPresenter<RepoInfo, List<Milestone>, View<List<Milestone>>> {
  private MilestoneState state;

  public IssueMilestonePresenter(Scheduler mainScheduler, Scheduler ioScheduler,
                                 MilestoneState state) {
    super(mainScheduler, ioScheduler, null);
    this.state = state;
  }

  @Override
  public void execute(RepoInfo repoInfo) {
    if(!isViewAttached()) return;

    GetMilestonesClient milestonesClient = new GetMilestonesClient(repoInfo, state, true);
    milestonesClient.observable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(() -> getView().showLoading())
            .doOnCompleted(() -> getView().hideLoading())
            .map(milestones -> {
              Collections.sort(milestones, (milestone, t1) -> t1.updatedAt.compareTo(milestone.updatedAt));
              return milestones;
            })
            .subscribe(milestones -> getView().onDataReceived(milestones, false),
                    throwable -> getView().showError(throwable));
  }
}
