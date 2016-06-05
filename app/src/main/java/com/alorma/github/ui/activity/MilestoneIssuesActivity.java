package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.issues.IssuesListFragment;

public class MilestoneIssuesActivity extends BackActivity implements IssuesListFragment.SearchClientRequest {

  private static String REPO_INFO = "REPO_INFO";
  private static String MILESTONE = "MILESTONE";

  private RepoInfo repoInfo;
  private Milestone milestone;
  private IssuesListFragment issuesListFragment;

  public static Intent launchIntent(Context context, RepoInfo repoInfo, Milestone milestone) {
    Intent intent = new Intent(context, MilestoneIssuesActivity.class);

    intent.putExtra(REPO_INFO, repoInfo);
    intent.putExtra(MILESTONE, milestone);

    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_issues_search);

    setTitle("");

    if (getIntent().getExtras() != null) {
      repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);
      milestone = getIntent().getExtras().getParcelable(MILESTONE);

      if (repoInfo == null || milestone == null) {
        finish();
      }

      setTitle(getString(R.string.milestone_issues_activity_title, milestone.title));
      if (getSupportActionBar() != null) {
        getSupportActionBar().setSubtitle(repoInfo.toString());
      }

      issuesListFragment = IssuesListFragment.newInstance(repoInfo, true);
      issuesListFragment.setSearchClientRequest(this);

      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.content, issuesListFragment);
      ft.commit();
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    issuesListFragment.executeSearch();
  }

  @Override
  public String request() {
    return "repo:" + repoInfo.toString() + "+milestone:" + milestone.title;
  }
}
