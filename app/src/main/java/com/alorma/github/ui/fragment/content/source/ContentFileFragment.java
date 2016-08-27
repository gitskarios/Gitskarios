package com.alorma.github.ui.fragment.content.source;

import android.os.Bundle;
import com.alorma.github.sdk.bean.info.FileInfo;
import rx.Observable;

public class ContentFileFragment extends TextBaseFileFragment {

  public static ContentFileFragment getInstance(FileInfo info) {
    ContentFileFragment fragment = new ContentFileFragment();
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
