package com.alorma.github.ui.fragment.issues.user;

import com.alorma.github.R;

public class AssignedIssuesListFragment extends UserIssuesListFragment {

  public static AssignedIssuesListFragment newInstance() {
    return new AssignedIssuesListFragment();
  }

  @Override
  protected String getAction() {
    return "assignee";
  }

  @Override
  public int getTitle() {
    return R.string.user_assigned_issues;
  }
}
