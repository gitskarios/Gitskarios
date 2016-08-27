package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.fragment.content.markdown.MarkdownContentFileFragment;
import com.alorma.github.ui.fragment.content.markdown.MarkdownFileFragment;
import com.alorma.github.ui.fragment.content.BaseFileFragment;
import com.alorma.github.ui.fragment.content.source.ContentFileFragment;
import com.alorma.github.ui.fragment.content.source.FileFragment;
import com.alorma.github.ui.fragment.content.source.ImageFileFragment;
import com.alorma.github.ui.utils.MarkdownUtils;
import com.alorma.github.utils.ImageUtils;

public class FileActivity extends RepositoryThemeActivity {

  public static Intent createLauncherIntent(Context context, FileInfo fileInfo) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(BaseFileFragment.FILE_INFO, fileInfo);

    Intent intent = new Intent(context, FileActivity.class);
    intent.putExtras(bundle);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar_responsive);

    FileInfo info = getIntent().getExtras().getParcelable(BaseFileFragment.FILE_INFO);

    if (info != null) {
      setTitle(info.name);

      if (ImageUtils.isImage(info.name)) {
        setFragment(ImageFileFragment.getInstance(info));
      } else if (MarkdownUtils.isMarkdown(info.name)) {
        if (info.content != null) {
          setFragment(MarkdownContentFileFragment.getInstance(info));
        } else {
          setFragment(MarkdownFileFragment.getInstance(info));
        }
      } else if (info.content != null) {
        setFragment(ContentFileFragment.getInstance(info));
      } else {
        setFragment(FileFragment.getInstance(info));
      }
    }
  }

  private void setFragment(Fragment fragment) {
    fragment.setArguments(getIntent().getExtras());
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.content, fragment);
    ft.commit();
  }

  @Override
  protected void close(boolean navigateUp) {
    finish();
  }
}
