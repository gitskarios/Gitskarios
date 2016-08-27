package com.alorma.github.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.fragment.base.BaseFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseFileFragment extends BaseFragment {

  public static final String FILE_INFO = "FILE_INFO";
  protected FileInfo fileInfo;

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onStart() {
    super.onStart();
    if (getArguments() != null) {
      fileInfo = getArguments().getParcelable(FILE_INFO);
      getContent(fileInfo);
    }
  }

  private void getContent(FileInfo fileInfo) {
    if (fileInfo.repoInfo != null) {
      getContentObservable(fileInfo).doOnNext(s -> {
        fileInfo.content = s;
        getActivity().invalidateOptionsMenu();
      }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onContentLoaded, this::onError);
    }
  }

  private void onError(Throwable throwable) {
    throwable.printStackTrace();
  }

  protected abstract Observable<String> getContentObservable(FileInfo fileInfo);

  protected abstract void onContentLoaded(String s);
}
