package com.alorma.github.injector.component;

import com.alorma.github.injector.module.ApplicationModule;
import com.alorma.github.sdk.core.ApiClient;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = ApplicationModule.class) public interface ApplicationComponent {

}