package com.alorma.github.ui.fragment.issues.user;

import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.presenter.issue.CreatedUserIssuesPresenter;
import com.mikepenz.iconics.typeface.IIcon;
import javax.inject.Inject;

public class CreatedIssuesListFragment extends UserIssuesListFragment {

  @Inject CreatedUserIssuesPresenter presenter;

  public static CreatedIssuesListFragment newInstance() {
    return new CreatedIssuesListFragment();
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
    return R.string.user_created_issues;
  }

  @Override
  public IIcon getTitleIcon() {
    return null;
  }
}
