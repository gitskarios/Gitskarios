package com.alorma.github.ui.fragment.gists;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.fragment.content.source.TextBaseFileFragment;
import rx.Observable;

public class GistFileFragment extends TextBaseFileFragment {

  public static final String FILE_NAME = "FILE_NAME";
  public static final String CONTENT = "CONTENT";

  private String content;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_content, null, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (getArguments() != null) {
      content = getArguments().getString(CONTENT);
    }
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Gists;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Gists;
  }

  @Override
  protected Observable<String> getContentObservable(FileInfo fileInfo) {
    return Observable.just(content);
  }

}
