package com.alorma.github.ui.activity.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;

public class RepoReadmeActivity extends RepositoryThemeActivity {

  private static final String REPO_INFO = "REPO_INFO";

  public static Intent createIntent(Context context, RepoInfo repoInfo) {
    Intent intent = new Intent(context, RepoReadmeActivity.class);
    intent.putExtra(REPO_INFO, repoInfo);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar_responsive);

    RepoInfo repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);
    if (repoInfo != null) {
      RepoReadmeFragment fragment = RepoReadmeFragment.newInstance(repoInfo);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.content, fragment);
      ft.commit();
    } else {
      finish();
    }
  }
}
