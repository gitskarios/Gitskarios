package com.alorma.github.injector.component.repository;

import com.alorma.github.injector.module.repository.UsernameRepositoriesModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.repos.UsernameReposFragment;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = UsernameRepositoriesModule.class)
public interface UsernameRepositoriesComponent {
    void inject(UsernameReposFragment usernameReposFragment);
}
