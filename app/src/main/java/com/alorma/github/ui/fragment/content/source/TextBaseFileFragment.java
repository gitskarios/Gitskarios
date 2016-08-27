package com.alorma.github.ui.fragment.content.source;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.fragment.content.BaseFileFragment;
import com.alorma.github.ui.utils.MarkdownUtils;
import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Language;
import com.pddstudio.highlightjs.models.Theme;

public abstract class TextBaseFileFragment extends BaseFileFragment {

  private HighlightJsView codeView;
  private FileCallback fileCallback;

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    if (fileInfo != null && fileInfo.content != null && MarkdownUtils.isMarkdown(fileInfo.name)) {
      menu.removeItem(R.id.action_show_markdown);
      getActivity().getMenuInflater().inflate(R.menu.file_from_markdown, menu);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    switch (item.getItemId()) {
      case R.id.action_show_markdown:
        if (fileCallback != null) {
          fileCallback.showAsMarkdown(fileInfo);
        }
        break;
    }

    return false;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.file_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    codeView = (HighlightJsView) view.findViewById(R.id.codeview);

    codeView.setZoomSupportEnabled(true);

    if (isDarkTheme()) {
      codeView.setTheme(Theme.ANDROID_STUDIO);
    } else {
      codeView.setTheme(Theme.ARDUINO_LIGHT);
    }

    codeView.setHighlightLanguage(Language.AUTO_DETECT);
  }

  @Override
  protected void onContentLoaded(String content) {
    codeView.setSource(content);
  }

  public void setFileCallback(FileCallback fileCallback) {
    this.fileCallback = fileCallback;
  }

  public interface FileCallback {
    void showAsMarkdown(FileInfo fileInfo);
  }
}
