package com.alorma.github.injector.module;

import android.content.Context;
import com.alorma.github.injector.PerActivity;
import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.Github;
import com.alorma.github.sdk.core.GithubEnterprise;
import com.alorma.gitskarios.core.client.UrlProvider;
import com.alorma.gitskarios.core.client.UrlProviderInterface;
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
}
