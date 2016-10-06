package com.alorma.github.injector.component.repository.tags;

import com.alorma.github.injector.module.repository.tags.RepositoryTagsModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.releases.RepositoryTagsFragment;

import dagger.Subcomponent;

@PerActivity @Subcomponent(modules = RepositoryTagsModule.class)
public interface RepositoryTagsComponent {
    void inject(RepositoryTagsFragment fragment);
}
