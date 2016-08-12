package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.fragment.repos.ListForksFragment;
import com.alorma.github.utils.AttributesUtils;

public class ForksActivity extends RepositoryThemeActivity {

  public static final String REPO_INFO = "REPO_INFO";

  public static Intent launchIntent(Context context, RepoInfo info) {
    Intent intent = new Intent(context, ForksActivity.class);
    intent.putExtra(REPO_INFO, info);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar_responsive);

    if (getToolbar() != null) {
      getToolbar().setBackgroundColor(AttributesUtils.getPrimaryColor(this));
    }

    if (getIntent() != null && getIntent().getExtras() != null) {
      RepoInfo info = (RepoInfo) getIntent().getParcelableExtra(REPO_INFO);

      setTitle(getString(R.string.forks_screen_title, info.toString()));

      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.content, ListForksFragment.newInstance(info));
      ft.commit();
    }
  }
}
