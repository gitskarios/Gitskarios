package com.alorma.github.ui.activity.issue;

import com.alorma.github.presenter.BaseRxPresenter;
import com.alorma.github.presenter.View;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.MilestoneState;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.issues.GetMilestonesClient;
import core.datasource.SdkItem;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.Scheduler;

public class IssueMilestonePresenter extends BaseRxPresenter<RepoInfo, List<Milestone>, View<List<Milestone>>> {
  private MilestoneState state;

  public IssueMilestonePresenter(Scheduler mainScheduler, Scheduler ioScheduler, MilestoneState state) {
    super(mainScheduler, ioScheduler, null);
    this.state = state;
  }

  @Override
  public void execute(RepoInfo repoInfo) {
    if (!isViewAttached()) return;

    Observable<List<Milestone>> observable = new GetMilestonesClient(repoInfo, state, true).observable().map(milestones -> {
      Collections.sort(milestones, (milestone, t1) -> t1.updatedAt.compareTo(milestone.updatedAt));
      return milestones;
    });

    subscribe(observable.map(SdkItem::new), false);
  }
}
