package com.alorma.github.ui.fragment.pullrequest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.alorma.github.sdk.bean.info.IssueInfo;

public class PullRequestInfoFragment extends Fragment {

  private static final String ISSUE_INFO = "ISSUE_INFO";

  public static PullRequestInfoFragment newInstance(IssueInfo issueInfo) {
    Bundle bundle = new Bundle();

    bundle.putParcelable(ISSUE_INFO, issueInfo);

    PullRequestInfoFragment fragment = new PullRequestInfoFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

}
