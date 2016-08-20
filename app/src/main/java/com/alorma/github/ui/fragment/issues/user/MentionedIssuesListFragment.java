package com.alorma.github.ui.fragment.issues.user;

import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.presenter.issue.MentionedUserIssuesPresenter;
import com.mikepenz.iconics.typeface.IIcon;
import javax.inject.Inject;

public class MentionedIssuesListFragment extends UserIssuesListFragment {

  @Inject MentionedUserIssuesPresenter presenter;

  public static MentionedIssuesListFragment newInstance() {
    return new MentionedIssuesListFragment();
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
    return R.string.user_mentioned_issues;
  }

  @Override
  public IIcon getTitleIcon() {
    return null;
  }
}
