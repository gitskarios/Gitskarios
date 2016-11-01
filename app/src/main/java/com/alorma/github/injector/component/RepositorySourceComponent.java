package com.alorma.github.injector.component;

import com.alorma.github.injector.module.RepositorySourceModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.activity.repo.RepositorySourceActivity;
import dagger.Subcomponent;

@PerActivity @Subcomponent(modules = RepositorySourceModule.class) public interface RepositorySourceComponent {
  void inject(RepositorySourceActivity repositorySourceActivity);
}
