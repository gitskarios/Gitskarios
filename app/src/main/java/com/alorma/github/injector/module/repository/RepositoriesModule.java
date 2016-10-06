package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.Token;
import com.alorma.github.injector.scope.PerActivity;

import core.ApiClient;
import core.repositories.RepositoriesRetrofitWrapper;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class RepositoriesModule {
    @Provides
    @PerActivity
    RepositoriesRetrofitWrapper provideRepositoriesRetrofitWrapper(
            ApiClient apiClient, @Token String token){
        return new RepositoriesRetrofitWrapper(apiClient, token);
    }
}
