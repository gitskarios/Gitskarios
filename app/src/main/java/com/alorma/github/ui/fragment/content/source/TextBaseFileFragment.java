package com.alorma.github.ui.fragment.content.source;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.R;
import com.alorma.github.ui.fragment.content.BaseFileFragment;
import io.github.kbiakov.codeview.CodeView;

public abstract class TextBaseFileFragment extends BaseFileFragment {

  private CodeView codeView;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.file_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    codeView = (CodeView) view.findViewById(R.id.codeview);
  }

  @Override
  protected void onContentLoaded(String content) {
    codeView.setCodeContent(content);
    codeView.highlightCode();
  }
}
