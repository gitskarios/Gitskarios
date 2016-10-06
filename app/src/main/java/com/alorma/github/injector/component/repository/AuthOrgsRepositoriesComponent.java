package com.alorma.github.injector.component.repository;

import com.alorma.github.injector.module.repository.AuthOrgsRepositoriesModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.repos.ReposFragmentFromOrgs;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = AuthOrgsRepositoriesModule.class)
public interface AuthOrgsRepositoriesComponent {
    void inject(ReposFragmentFromOrgs reposFragmentFromOrgs);
}
