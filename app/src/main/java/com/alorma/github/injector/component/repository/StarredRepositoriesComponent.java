package com.alorma.github.injector.component.repository;

import com.alorma.github.injector.module.repository.StarredRepositoriesModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.repos.StarredReposFragment;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = StarredRepositoriesModule.class)
public interface StarredRepositoriesComponent {
    void inject(StarredReposFragment starredReposFragment);
}
