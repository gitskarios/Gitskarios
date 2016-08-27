package com.alorma.github.ui.fragment.content.source;

import android.os.Bundle;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.services.content.GetFileContentClient;
import rx.Observable;

public class FileFragment extends TextBaseFileFragment {

  public static FileFragment getInstance(FileInfo info) {
    FileFragment fragment = new FileFragment();
    Bundle args = new Bundle();
    args.putParcelable(FILE_INFO, info);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected Observable<String> getContentObservable(FileInfo fileInfo) {
    return new GetFileContentClient(fileInfo).observable().map(content -> content.content).map(this::decodeContent);
  }
}
