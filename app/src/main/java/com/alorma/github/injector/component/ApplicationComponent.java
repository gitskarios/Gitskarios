package com.alorma.github.injector.component;

import com.alorma.github.injector.SortOrder;
import com.alorma.github.injector.module.ApplicationModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = ApplicationModule.class) public interface ApplicationComponent {

  @SortOrder
  String getRepoSortOrder();
}