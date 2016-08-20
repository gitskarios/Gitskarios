package com.alorma.github.ui.fragment.issues.user;

import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.presenter.issue.AssignedUserIssuesPresenter;
import com.mikepenz.iconics.typeface.IIcon;
import javax.inject.Inject;

public class AssignedIssuesListFragment extends UserIssuesListFragment {

  @Inject AssignedUserIssuesPresenter presenter;

  public static AssignedIssuesListFragment newInstance() {
    return new AssignedIssuesListFragment();
  }

  @Override
  protected void initInjectors(ApiComponent apiComponent) {
    apiComponent.inject(this);
  }

  @Override
  protected void onRefresh() {
    presenter.load(null, this);
  }

  @Override
  public void loadMoreItems() {
    presenter.loadMore(null, this);
  }

  @Override
  public int getTitle() {
    return R.string.user_assigned_issues;
  }

  @Override
  public IIcon getTitleIcon() {
    return null;
  }
}
