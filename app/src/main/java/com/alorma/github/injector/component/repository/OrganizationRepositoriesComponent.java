package com.alorma.github.injector.component.repository;

import com.alorma.github.injector.module.repository.OrganizationRepositoriesModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.orgs.OrgsReposFragment;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = OrganizationRepositoriesModule.class)
public interface OrganizationRepositoriesComponent {
    void inject(OrgsReposFragment fragment);
}
