package com.alorma.github.injector.component.tags;

import com.alorma.github.injector.module.tags.RepositoryTagsModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.releases.RepositoryTagsFragment;

import dagger.Subcomponent;

@PerActivity @Subcomponent(modules = RepositoryTagsModule.class)
public interface RepositoryTagsComponent {
    void inject(RepositoryTagsFragment fragment);
}
