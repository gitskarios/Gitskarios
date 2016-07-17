package com.alorma.github.injector.module;

import android.content.Context;
import com.alorma.github.account.AccountNameProvider;
import com.alorma.github.injector.SortOrder;
import com.alorma.github.utils.RepoUtils;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module public class ApplicationModule {

  private Context application;

  public ApplicationModule(Context application) {
    this.application = application;
  }

  @Provides
  @Singleton
  Context providesApplicationContext() {
    return this.application;
  }

  @Provides
  @Singleton
  @SortOrder
  String getReposSortOrder(Context context) {
    return RepoUtils.sortOrder(context);
  }

  @Provides
  @Singleton
  AccountNameProvider getAccountName() {
    return new AccountNameProvider();
  }
}
