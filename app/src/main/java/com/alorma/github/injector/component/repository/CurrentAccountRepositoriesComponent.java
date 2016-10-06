package com.alorma.github.injector.component.repository;

import com.alorma.github.injector.module.repository.CurrentAccountRepositoriesModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.repos.CurrentAccountReposFragment;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = CurrentAccountRepositoriesModule.class)
public interface CurrentAccountRepositoriesComponent {
    void inject(CurrentAccountReposFragment currentAccountReposFragment);
}
