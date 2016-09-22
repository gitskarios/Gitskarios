package com.alorma.github.di.tags;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.presenter.repos.releases.tags.RepositoryTagsPresenter;
import com.alorma.github.presenter.repos.releases.tags.TagsCacheDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.repositories.releases.tags.TagsCloudDataSource;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.schedulers.Schedulers;

@Module public class TagsTestModule {

    public TagsTestModule() {
    }

    @Provides
    @Singleton
    RestWrapper provideTagsRetrofitWrapper(){
        return Mockito.mock(RestWrapper.class);
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

    @Provides @Singleton @MainScheduler Scheduler provideMainScheduler(){
        return Schedulers.immediate();
    }

    @Provides @Singleton @IOScheduler Scheduler provideIOScheduler() {
        return Schedulers.immediate();
    }

    @Provides
    @Singleton
    RepositoryTagsPresenter provideRepositoryTagsPresenter() {
        return new RepositoryTagsPresenter();
    }
}
