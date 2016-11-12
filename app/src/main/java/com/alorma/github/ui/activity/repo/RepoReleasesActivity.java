package com.alorma.github.ui.activity.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.fragment.releases.ReleaseBottomSheetDialogFragment;
import com.alorma.github.ui.fragment.releases.RepositoryTagsFragment;
import com.alorma.github.ui.fragment.releases.TagBottomSheetDialogFragment;
import core.repositories.releases.Release;
import core.repositories.releases.tags.Tag;

public class RepoReleasesActivity extends RepositoryThemeActivity implements RepositoryTagsFragment.ReleasesCallback {

  private static final String REPO_INFO = "REPO_INFO";
  private RepoInfo repoInfo;

  public static Intent createIntent(Context context, RepoInfo repoInfo) {
    Intent intent = new Intent(context, RepoReleasesActivity.class);
    intent.putExtra(REPO_INFO, repoInfo);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar_responsive);

    repoInfo = getIntent().getParcelableExtra(REPO_INFO);
    if (repoInfo != null) {
      setTitle(repoInfo.toString());
      RepositoryTagsFragment fragment = RepositoryTagsFragment.newInstance(repoInfo);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.content, fragment);
      ft.commit();
    } else {
      finish();
    }
  }

  @Override
  public void showTagDialog(Tag tag) {
    TagBottomSheetDialogFragment fragment = TagBottomSheetDialogFragment.newInstance(repoInfo, tag);
    fragment.show(getSupportFragmentManager(), "");
  }

  @Override
  public void showReleaseDialog(Release release) {
    ReleaseBottomSheetDialogFragment fragment = ReleaseBottomSheetDialogFragment.newInstance(release);
    fragment.show(getSupportFragmentManager(), "");
  }
}
