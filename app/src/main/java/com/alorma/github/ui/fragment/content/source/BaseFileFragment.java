package com.alorma.github.ui.fragment.content.source;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.fragment.base.BaseFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseFileFragment extends BaseFragment {

  public static final String FILE_INFO = "FILE_INFO";

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  @Override
  public void onStart() {
    super.onStart();
    if (getArguments() != null) {
      FileInfo fileInfo = getArguments().getParcelable(FILE_INFO);
      getContent(fileInfo);
    }
  }

  private void getContent(FileInfo fileInfo) {
    if (fileInfo.repoInfo != null) {
      getContentObservable(fileInfo).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::onContentLoaded, Throwable::printStackTrace);
    }
  }

  protected abstract Observable<String> getContentObservable(FileInfo fileInfo);
  protected abstract void onContentLoaded(String s);

}
