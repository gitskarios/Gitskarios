package com.alorma.github.ui.fragment.content.source;

import android.os.Bundle;
import com.alorma.github.sdk.bean.info.FileInfo;
import rx.Observable;

public class ContentFileFragment extends TextBaseFileFragment {

  private static final String FILE_CONTENT = "FILE_CONTENT";

  public static ContentFileFragment getInstance(FileInfo info) {
    ContentFileFragment fragment = new ContentFileFragment();
    Bundle args = new Bundle();
    args.putParcelable(FILE_INFO, info);
    args.putString(FILE_CONTENT, info.content);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected Observable<String> getContentObservable(FileInfo fileInfo) {
    return Observable.defer(() -> {
      if (fileInfo.content == null && getArguments() != null) {
        String content = getArguments().getString(FILE_CONTENT);
        if (content != null) {
          return Observable.just(content);
        } else {
          return Observable.just(fileInfo.content);
        }
      } else {
        return Observable.just(fileInfo.content);
      }
    });
  }
}
