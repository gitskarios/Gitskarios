package com.alorma.github.injector.module.tags;

import com.alorma.github.injector.module.BaseModule;
import com.alorma.github.injector.named.SortOrder;
import com.alorma.github.injector.named.Token;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.repos.releases.tags.RepositoryTagsPresenter;
import com.alorma.github.presenter.repos.releases.tags.TagsCacheDataSource;
import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.repositories.releases.tags.TagsCloudDataSource;
import com.alorma.github.sdk.core.repositories.releases.tags.TagsRetrofitWrapper;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryTagsModule extends BaseModule {

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
    @Inject
    RepositoryTagsPresenter provideRepositoryTagsPresenter(){
        return new RepositoryTagsPresenter();
    }
}
