package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.fragment.FileFragment;

public class FileActivity extends RepositoryThemeActivity {

  private boolean fromUrl;
  private FileInfo info;

  public static Intent createLauncherIntent(Context context, FileInfo fileInfo, boolean fromUrl) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(FileFragment.FILE_INFO, fileInfo);
    bundle.putBoolean(FileFragment.FROM_URL, fromUrl);

    Intent intent = new Intent(context, FileActivity.class);
    intent.putExtras(bundle);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar);

    info = (FileInfo) getIntent().getExtras().getParcelable(FileFragment.FILE_INFO);
    fromUrl = getIntent().getExtras().getBoolean(FileFragment.FROM_URL);

    setTitle(info.name);

    FileFragment fileFragment = FileFragment.getInstance(info, fromUrl);
    fileFragment.setArguments(getIntent().getExtras());
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.content, fileFragment);
    ft.commit();
  }

  @Override
  protected void close(boolean navigateUp) {
    if (navigateUp && fromUrl) {
      Intent upIntent = RepoDetailActivity.createLauncherIntent(this, info.repoInfo);
      upIntent.putExtra(RepoDetailActivity.FROM_URL, fromUrl);
      TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
      finish();
    } else {
      super.close(navigateUp);
    }
  }
}
