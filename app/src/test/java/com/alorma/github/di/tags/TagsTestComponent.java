package com.alorma.github.di.tags;

import com.alorma.github.ui.tags.RepositoryTagsPresenterTest;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Singleton
@Subcomponent(modules = {TagsTestModule.class})
public interface TagsTestComponent {

    void inject(RepositoryTagsPresenterTest test);

}
