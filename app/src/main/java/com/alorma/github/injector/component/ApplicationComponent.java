package com.alorma.github.injector.component;

import android.content.Context;
import com.alorma.github.account.AccountNameProvider;
import com.alorma.github.injector.module.ApplicationModule;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.log.LogWrapper;
import com.alorma.github.track.Tracker;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.fragment.base.BaseFragment;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = ApplicationModule.class) public interface ApplicationComponent {

  @SortOrder
  String getRepoSortOrder();

  AccountNameProvider getAccountName();

  Context getContext();

  Tracker getTracker();

  LogWrapper getLogWrapper();

  void inject(BaseActivity baseActivity);

  void inject(BaseFragment baseFragment);
}