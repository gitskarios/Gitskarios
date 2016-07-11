package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.fragment.ContentFileFragment;
import com.alorma.github.ui.fragment.FileFragment;
import com.alorma.github.ui.fragment.ImageFileFragment;
import com.alorma.github.utils.ImageUtils;

public class FileActivity extends RepositoryThemeActivity {

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

    FileInfo info = getIntent().getExtras().getParcelable(FileFragment.FILE_INFO);
    boolean fromUrl = getIntent().getExtras().getBoolean(FileFragment.FROM_URL);

    if (info != null) {
      setTitle(info.name);

      if (ImageUtils.isImage(info.name)) {
        ImageFileFragment imageFileFragment = ImageFileFragment.getInstance(info, fromUrl);
        imageFileFragment.setArguments(getIntent().getExtras());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, imageFileFragment);
        ft.commit();
      } else if (info.content != null) {
        ContentFileFragment fileFragment = ContentFileFragment.getInstance(info);
        fileFragment.setArguments(getIntent().getExtras());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fileFragment);
        ft.commit();
      } else {
        FileFragment fileFragment = FileFragment.getInstance(info, fromUrl);
        fileFragment.setArguments(getIntent().getExtras());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fileFragment);
        ft.commit();
      }
    }
  }

  @Override
  protected void close(boolean navigateUp) {
    finish();
  }
}
