package com.alorma.github.injector.module;

import android.content.Context;

import com.alorma.github.BuildConfig;
import com.alorma.github.account.AccountNameProvider;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.log.LogWrapper;
import com.alorma.github.track.Tracker;
import com.alorma.github.utils.RepoUtils;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.fabric.sdk.android.Fabric;

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

  @Provides
  @Singleton
  Answers getAnswers() {
    return Answers.getInstance();
  }

  @Provides
  @Singleton
  Crashlytics getCrashlytics() {
    return Crashlytics.getInstance();
  }

  @Provides
  @Singleton
  Tracker getTracker(Answers answers, Crashlytics crashlytics) {
    if (BuildConfig.DEBUG) {
      return new DebugTracker();
    } else if (Fabric.isInitialized()){
      return new AnswersTracker(answers, crashlytics);
    } else {
      return new DebugTracker();
    }
  }

  @Provides
  @Singleton
  LogWrapper getLogWrapper() {
    return new AndroidLogWrapper();
  }
}
