package com.alorma.github.injector.module;

import com.alorma.github.injector.PerActivity;
import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.Github;
import com.alorma.github.sdk.core.GithubEnterprise;
import com.alorma.gitskarios.core.client.UrlProvider;
import com.alorma.gitskarios.core.client.UrlProviderInterface;
import dagger.Module;
import dagger.Provides;

@Module public class ApiModule {

  @Provides
  @PerActivity
  ApiClient providesGithubApi() {
    UrlProviderInterface instance = UrlProvider.getInstance();
    if (instance == null || instance.getUrl() == null) {
      return new Github();
    } else {
      return new GithubEnterprise(instance.getUrl());
    }
  }
}
