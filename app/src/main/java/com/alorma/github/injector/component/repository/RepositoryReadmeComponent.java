package com.alorma.github.injector.component.repository;

import com.alorma.github.injector.module.repository.RepositoryReadmeModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.activity.repo.RepoReadmeFragment;
import dagger.Subcomponent;

@PerActivity @Subcomponent(modules = RepositoryReadmeModule.class) public interface RepositoryReadmeComponent {
  void inject(RepoReadmeFragment repoReadmeFragment);
}
