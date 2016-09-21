package com.alorma.github.di;

import com.alorma.github.account.AccountNameProvider;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.sdk.core.ApiClient;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Dagger 2 test module
 */
@Module public class TestModule {

    @Provides
    @Singleton
    @SortOrder
    String provideReposSortOrder() {
        return "asc";
    }

    @Provides
    @Singleton
    AccountNameProvider provideAccountName(){
        AccountNameProvider accountNameProvider = new AccountNameProvider();
        accountNameProvider.setName("TEST_ACCOUNT");
        return accountNameProvider;
    }

    @Provides
    @Singleton
    Scheduler provideScheduler() {
        return Schedulers.immediate();
    }

    @Provides
    @Singleton
    ApiClient providesGithubApi() {
        return Mockito.mock(ApiClient.class);
    }
}
