package com.alorma.github.injector.module;

import android.content.Context;
import com.alorma.github.injector.SortOrder;
import com.alorma.github.utils.RepoUtils;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
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
  FirebaseJobDispatcher getJobDispatcher(Context context) {
    Driver myDriver = new GooglePlayDriver(context);
    return new FirebaseJobDispatcher(myDriver);
  }
}
