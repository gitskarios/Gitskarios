package com.alorma.github.ui.fragment.content.markdown;

import android.os.Bundle;
import com.alorma.github.sdk.bean.info.FileInfo;
import rx.Observable;

public class MarkdownContentFileFragment extends BaseMarkdownFileFragment {

  public static BaseMarkdownFileFragment getInstance(FileInfo info) {
    BaseMarkdownFileFragment fragment = new MarkdownContentFileFragment();
    Bundle args = new Bundle();
    args.putParcelable(FILE_INFO, info);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected Observable<String> getContentObservable(FileInfo fileInfo) {
    return Observable.just(fileInfo.content);
  }
}
