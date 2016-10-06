package com.alorma.github.di;

import com.alorma.github.account.AccountNameProvider;
import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.SortOrder;

import core.ApiClient;
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
    ApiClient providesGithubApi() {
        return Mockito.mock(ApiClient.class);
    }

    @Provides @Singleton @MainScheduler
    Scheduler provideMainScheduler(){
        return Schedulers.immediate();
    }

    @Provides @Singleton @IOScheduler
    Scheduler provideIOScheduler() {
        return Schedulers.immediate();
    }
}
