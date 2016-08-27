package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.fragment.content.BaseFileFragment;
import com.alorma.github.ui.fragment.content.markdown.BaseMarkdownFileFragment;
import com.alorma.github.ui.fragment.content.markdown.MarkdownContentFileFragment;
import com.alorma.github.ui.fragment.content.markdown.MarkdownFileFragment;
import com.alorma.github.ui.fragment.content.source.ContentFileFragment;
import com.alorma.github.ui.fragment.content.source.FileFragment;
import com.alorma.github.ui.fragment.content.source.ImageFileFragment;
import com.alorma.github.ui.fragment.content.source.TextBaseFileFragment;
import com.alorma.github.ui.utils.MarkdownUtils;
import com.alorma.github.utils.ImageUtils;

public class FileActivity extends RepositoryThemeActivity
    implements BaseMarkdownFileFragment.MarkdownFileCallback, TextBaseFileFragment.FileCallback {

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
        setFragment(ImageFileFragment.getInstance(info), false);
      } else if (MarkdownUtils.isMarkdown(info.name)) {
        BaseMarkdownFileFragment fragment;
        if (info.content != null) {
          fragment = MarkdownContentFileFragment.getInstance(info);
        } else {
          fragment = MarkdownFileFragment.getInstance(info);
        }
        fragment.setMarkdownFileCallback(this);
        setFragment(fragment, false);
      } else if (info.content != null) {
        TextBaseFileFragment fileFragment = ContentFileFragment.getInstance(info);
        fileFragment.setFileCallback(this);
        setFragment(fileFragment, false);
      } else {
        TextBaseFileFragment fileFragment = FileFragment.getInstance(info);
        fileFragment.setFileCallback(this);
        setFragment(fileFragment, false);
      }
    }
  }

  private void setFragment(Fragment fragment, boolean addToBackStack) {
    fragment.setArguments(getIntent().getExtras());
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.content, fragment);
    if (addToBackStack) {
      ft.addToBackStack(null);
    }
    ft.commit();
  }

  @Override
  public void showAsContent(FileInfo fileInfo) {
    FileFragment fileFragment = FileFragment.getInstance(fileInfo);
    fileFragment.setFileCallback(this);
    setFragment(fileFragment, true);
  }

  @Override
  public void showAsMarkdown(FileInfo fileInfo) {
    BaseMarkdownFileFragment markdownFileFragment = MarkdownFileFragment.getInstance(fileInfo);
    markdownFileFragment.setMarkdownFileCallback(this);
    setFragment(markdownFileFragment, true);
  }
}
