package com.alorma.github.injector.component.repository;

import com.alorma.github.injector.module.repository.MembershipRepositoriesModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.repos.MembershipReposFragment;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = MembershipRepositoriesModule.class)
public interface MembershipRepositoriesComponent {
    void inject(MembershipReposFragment membershipReposFragment);
}
