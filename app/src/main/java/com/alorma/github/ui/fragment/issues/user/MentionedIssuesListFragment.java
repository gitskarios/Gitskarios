package com.alorma.github.ui.fragment.issues.user;

import com.alorma.github.R;

public class MentionedIssuesListFragment extends UserIssuesListFragment {

  public static MentionedIssuesListFragment newInstance() {
    return new MentionedIssuesListFragment();
  }

  @Override
  public int getTitle() {
    return R.string.user_mentioned_issues;
  }

  @Override
  protected String getAction() {
    return "mentions";
  }
}
