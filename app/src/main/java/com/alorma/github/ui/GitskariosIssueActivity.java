package com.alorma.github.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.MainActivity;
import com.alorma.github.ui.activity.NewIssueActivity;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;

public class GitskariosIssueActivity extends RepositoryThemeActivity {

  public static final String REPO_INFO = "REPO_INFO";

  public static Intent createLauncherIntent(Context context, RepoInfo info) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(REPO_INFO, info);
    Intent intent = new Intent(context, GitskariosIssueActivity.class);
    intent.putExtras(bundle);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar_responsive);

    NewIssueFormFragment newIssueFormFragment = new NewIssueFormFragment();
    newIssueFormFragment.setArguments(getIntent().getExtras());

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.content, newIssueFormFragment);
    ft.commit();
  }

}

