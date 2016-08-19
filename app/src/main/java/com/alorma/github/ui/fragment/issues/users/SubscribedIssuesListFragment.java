package com.alorma.github.ui.fragment.issues.users;

import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.presenter.issue.MentionedUserIssuesPresenter;
import com.mikepenz.iconics.typeface.IIcon;
import javax.inject.Inject;

public class SubscribedIssuesListFragment extends UserIssuesListFragment {

  @Inject MentionedUserIssuesPresenter presenter;

  public static SubscribedIssuesListFragment newInstance() {
    return new SubscribedIssuesListFragment();
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
    return R.string.user_subscribed_issues;
  }

  @Override
  public IIcon getTitleIcon() {
    return null;
  }
}
