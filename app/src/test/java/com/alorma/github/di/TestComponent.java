package com.alorma.github.di;

import com.alorma.github.account.AccountNameProvider;
import com.alorma.github.di.tags.TagsTestComponent;
import com.alorma.github.di.tags.TagsTestModule;
import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.SortOrder;
import core.ApiClient;
import dagger.Component;
import javax.inject.Singleton;
import rx.Scheduler;

/**
 * Dagger 2 test component
 */
@Singleton @Component(modules = TestModule.class) public interface TestComponent {

  @SortOrder
  String provideReposSortOrder();

  AccountNameProvider provideAccountName();

  @MainScheduler
  Scheduler provideMainScheduler();

  @IOScheduler
  Scheduler provideIOScheduler();

  ApiClient providesGithubApi();

  TagsTestComponent plus(TagsTestModule module);
}
