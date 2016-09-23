package com.alorma.github.injector.module.tags;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.injector.named.Token;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.repos.releases.tags.RepositoryTagsPresenter;
import com.alorma.github.presenter.repos.releases.tags.TagsCacheDataSource;

import core.ApiClient;
import core.repositories.releases.tags.TagsCloudDataSource;
import core.repositories.releases.tags.TagsRetrofitWrapper;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class RepositoryTagsModule {

    @Provides
    @PerActivity
    TagsCacheDataSource provideTagsCacheDataSource(){
        return new TagsCacheDataSource();
    }

    @Provides
    @PerActivity
    TagsRetrofitWrapper provideTagsRetrofitWrapper(ApiClient apiClient, @Token String token) {
        return new TagsRetrofitWrapper(apiClient, token);
    }

    @Provides
    @PerActivity
    TagsCloudDataSource provideTagsCloudDataSource(TagsRetrofitWrapper tagsRetrofitWrapper, @SortOrder String sortOrder){
        return new TagsCloudDataSource(tagsRetrofitWrapper, sortOrder);
    }

    @Provides
    @PerActivity
    RepositoryTagsPresenter provideRepositoryTagsPresenter(@IOScheduler Scheduler ioScheduler,
                                                           @MainScheduler Scheduler mainScheduler,
                                                           TagsCacheDataSource tagsCacheDataSource,
                                                           TagsCloudDataSource tagsCloudDataSource,
                                                           TagsRetrofitWrapper tagsRetrofitWrapper){
        return new RepositoryTagsPresenter(ioScheduler, mainScheduler, tagsCacheDataSource, tagsCloudDataSource, tagsRetrofitWrapper);
    }
}
