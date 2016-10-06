package com.alorma.github.injector.component.repository;

import com.alorma.github.injector.module.repository.RepoDetailModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.activity.repo.RepoDetailActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = RepoDetailModule.class)
public interface RepoDetailComponent {
    void inject(RepoDetailActivity activity);
}
