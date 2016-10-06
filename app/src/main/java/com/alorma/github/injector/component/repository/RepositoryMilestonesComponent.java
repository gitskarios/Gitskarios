package com.alorma.github.injector.component.repository;

import com.alorma.github.injector.module.repository.RepositoryMilestonesModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.activity.issue.RepositoryMilestonesActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = RepositoryMilestonesModule.class)
public interface RepositoryMilestonesComponent {
    void inject(RepositoryMilestonesActivity activity);
}
