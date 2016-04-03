package com.alorma.github.ui.activity.issue;

import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.MilestoneState;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.GetMilestonesClient;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueMilestonePresenter extends Presenter<IssueInfo, List<Milestone>> {
  @Override
  public void load(IssueInfo issueInfo, Callback<List<Milestone>> listCallback) {
    GetMilestonesClient milestonesClient =
        new GetMilestonesClient(issueInfo.repoInfo, MilestoneState.open, true);
    milestonesClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(listCallback::showLoading)
        .doOnCompleted(listCallback::hideLoading)
        .subscribe(listCallback::onResponse, Throwable::printStackTrace);
  }
}
