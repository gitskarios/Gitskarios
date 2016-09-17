package com.alorma.github.ui.fragment.issues.user;

import com.alorma.github.R;

public class CreatedIssuesListFragment extends UserIssuesListFragment {

  public static CreatedIssuesListFragment newInstance() {
    return new CreatedIssuesListFragment();
  }

  @Override
  public int getTitle() {
    return R.string.user_created_issues;
  }

  @Override
  protected String getAction() {
    return "author";
  }
}
