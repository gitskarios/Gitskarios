package com.alorma.github.injector.component;

import com.alorma.github.injector.module.CommitDetailModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.activity.CommitDetailActivity;
import com.alorma.github.ui.fragment.releases.RepositoryTagsFragment;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = CommitDetailModule.class)
public interface CommitDetailComponent {
    void inject(CommitDetailActivity commitDetailActivity);
}
