package com.alorma.github.di;

import com.alorma.github.account.AccountNameProvider;
import com.alorma.github.di.tags.TagsTestComponent;
import com.alorma.github.di.tags.TagsTestModule;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.sdk.core.ApiClient;

import javax.inject.Singleton;

import dagger.Component;
import rx.Scheduler;

/**
 * Dagger 2 test component
 */
@Singleton @Component(modules = TestModule.class) public interface TestComponent {

    @SortOrder
    String provideReposSortOrder();

    AccountNameProvider provideAccountName();

    Scheduler provideScheduler();

    ApiClient providesGithubApi();

    TagsTestComponent plus(TagsTestModule module);

}
