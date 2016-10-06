package com.alorma.github.injector.component.repository;

import com.alorma.github.injector.module.repository.WatchedRepositoriesModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.repos.WatchedReposFragment;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = WatchedRepositoriesModule.class)
public interface WatchedRepositoriesComponent {
    void inject(WatchedReposFragment watchedReposFragment);
}
