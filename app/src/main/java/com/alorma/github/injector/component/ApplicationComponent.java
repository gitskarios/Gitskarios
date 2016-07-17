package com.alorma.github.injector.component;

import android.content.Context;
import com.alorma.github.account.AccountNameProvider;
import com.alorma.github.injector.SortOrder;
import com.alorma.github.injector.module.ApplicationModule;
import com.alorma.github.ui.activity.base.BaseActivity;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = ApplicationModule.class) public interface ApplicationComponent {

  @SortOrder
  String getRepoSortOrder();

  AccountNameProvider getAccountName();

  Context getContext();

  void inject(BaseActivity baseActivity);
}