package com.alorma.github.di.tags;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.presenter.repos.releases.tags.RepositoryTagsPresenter;
import com.alorma.github.presenter.repos.releases.tags.TagsCacheDataSource;

import org.mockito.Mockito;

import javax.inject.Singleton;

import core.repositories.releases.tags.TagsCloudDataSource;
import core.repositories.releases.tags.TagsRetrofitWrapper;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module public class TagsTestModule {

    public TagsTestModule() {
    }

    @Provides
    @Singleton
    TagsRetrofitWrapper provideTagsRetrofitWrapper(){
        return Mockito.mock(TagsRetrofitWrapper.class);
    }

    @Provides
    @Singleton
    TagsCloudDataSource provideTagsCloudDataSource() {
        return Mockito.mock(TagsCloudDataSource.class);
    }

    @Provides
    @Singleton
    TagsCacheDataSource provideTagsCacheDataSource(){
        return Mockito.mock(TagsCacheDataSource.class);
    }

    @Provides
    @Singleton
    RepositoryTagsPresenter provideRepositoryTagsPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
            TagsCacheDataSource tagsCacheDataSource,
            TagsCloudDataSource tagsCloudDataSource) {

        return new RepositoryTagsPresenter(
                mainScheduler, ioScheduler,
                new GenericRepository<>(tagsCacheDataSource, tagsCloudDataSource));
    }
}
